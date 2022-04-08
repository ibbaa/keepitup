/*
 * Copyright (c) 2022. Alwin Ibba
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.ibbaa.keepitup.service.network;

import android.content.Context;
import android.content.res.Resources;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.db.NetworkTaskDAO;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.resources.ServiceFactoryContributor;
import net.ibbaa.keepitup.service.IFileManager;
import net.ibbaa.keepitup.service.ITimeService;
import net.ibbaa.keepitup.service.SystemFileManager;
import net.ibbaa.keepitup.util.HTTPUtil;
import net.ibbaa.keepitup.util.NumberUtil;
import net.ibbaa.keepitup.util.StreamUtil;
import net.ibbaa.keepitup.util.StringUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DownloadCommand implements Callable<DownloadCommandResult> {

    private final Context context;
    private final NetworkTask networkTask;
    private final URL url;
    private final File folder;
    private final boolean delete;
    private boolean valid;
    private boolean stopped;
    private final ITimeService timeService;

    public DownloadCommand(Context context, NetworkTask networkTask, URL url, File folder, boolean delete) {
        this.context = context;
        this.networkTask = networkTask;
        this.url = url;
        this.folder = folder;
        this.delete = delete;
        this.timeService = createTimeService();
    }

    @Override
    public DownloadCommandResult call() {
        Log.d(DownloadCommand.class.getName(), "call");
        URLConnection connection = null;
        InputStream inputStream = null;
        FileOutputStream outputStream = null;
        boolean connectSuccess = false;
        boolean downloadSuccess = false;
        boolean fileExists;
        boolean deleteSuccess = false;
        int httpCode = -1;
        String httpMessage = null;
        String fileName = null;
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        initializeValid();
        long start = timeService.getCurrentTimestamp();
        try {
            Log.d(DownloadCommand.class.getName(), "Establishing connection to " + url);
            connection = openConnection();
            if (connection == null) {
                Log.d(DownloadCommand.class.getName(), "Error establishing connection to " + url);
                long end = timeService.getCurrentTimestamp();
                return createDownloadCommandResult(false, false, false, false, httpCode, null, null, NumberUtil.ensurePositive(end - start), null);
            }
            connectSuccess = true;
            Log.d(DownloadCommand.class.getName(), "Connection established.");
            if (HTTPUtil.isHTTPConnection(connection)) {
                Log.d(DownloadCommand.class.getName(), "Download is an HTTP download.");
                HttpURLConnection httpConnection = (HttpURLConnection) connection;
                httpCode = httpConnection.getResponseCode();
                Log.d(DownloadCommand.class.getName(), "HTTP return code is " + httpCode);
                httpMessage = httpConnection.getResponseMessage();
                httpMessage += getLocationHeader(connection);
                Log.d(DownloadCommand.class.getName(), "HTTP message is " + httpMessage);
                if (!HTTPUtil.isHTTPReturnCodeOk(httpCode)) {
                    Log.d(DownloadCommand.class.getName(), "Connection successful but HTTP return code " + httpCode + " is not HTTP_OK");
                    long end = timeService.getCurrentTimestamp();
                    return createDownloadCommandResult(true, false, false, false, httpCode, httpMessage, null, NumberUtil.ensurePositive(end - start), null);
                }
            } else {
                Log.d(DownloadCommand.class.getName(), "Download is not an HTTP download.");
            }
            fileName = getFileName(connection);
            if (fileName == null) {
                Log.d(DownloadCommand.class.getName(), "Connection successful but download file name could not be determined");
                long end = timeService.getCurrentTimestamp();
                return createDownloadCommandResult(true, false, false, false, httpCode, httpMessage, null, NumberUtil.ensurePositive(end - start), null);
            }
            Log.d(DownloadCommand.class.getName(), "Using file name " + fileName);
            Log.d(DownloadCommand.class.getName(), "Opening streams...");
            inputStream = connection.getInputStream();
            outputStream = getOutputStream(fileName);
            int pollInterval = getResources().getInteger(R.integer.download_valid_poll_interval);
            Log.d(DownloadCommand.class.getName(), "Scheduling verify valid polling thread with an interval of " + pollInterval);
            executorService.scheduleWithFixedDelay(this::verifyValid, 0, pollInterval, TimeUnit.SECONDS);
            Log.d(DownloadCommand.class.getName(), "Starting download...");
            downloadSuccess = StreamUtil.inputStreamToOutputStream(inputStream, outputStream, this::isValid);
            Log.d(DownloadCommand.class.getName(), "Download successful: " + downloadSuccess);
            flushAndCloseOutputStream(outputStream);
            fileExists = downloadedFileExists(fileName);
            Log.d(DownloadCommand.class.getName(), "Partial download successful: " + fileExists);
            if (delete && fileExists) {
                Log.d(DownloadCommand.class.getName(), "Deleting downloaded file...");
                deleteSuccess = deleteDownloadedFile(fileName);
            }
            long end = timeService.getCurrentTimestamp();
            return createDownloadCommandResult(true, downloadSuccess, fileExists, deleteSuccess, httpCode, httpMessage, fileName, NumberUtil.ensurePositive(end - start), null);
        } catch (Exception exc) {
            Log.e(DownloadCommand.class.getName(), "Error executing download command", exc);
            Log.e(DownloadCommand.class.getName(), "Try closing stream.");
            flushAndCloseOutputStream(outputStream);
            fileExists = downloadedFileExists(fileName);
            Log.d(DownloadCommand.class.getName(), "Partial download successful: " + fileExists);
            if (delete && fileExists) {
                Log.d(DownloadCommand.class.getName(), "Deleting downloaded file...");
                deleteSuccess = deleteDownloadedFile(fileName);
            }
            long end = timeService.getCurrentTimestamp();
            return createDownloadCommandResult(connectSuccess, downloadSuccess, fileExists, deleteSuccess, httpCode, httpMessage, fileName, NumberUtil.ensurePositive(end - start), exc);
        } finally {
            closeResources(connection, inputStream, outputStream, executorService);
        }
    }

    protected URLConnection openConnection() throws IOException {
        Log.d(DownloadCommand.class.getName(), "openConnection");
        if (url == null) {
            Log.e(DownloadCommand.class.getName(), "URL is null");
            return null;
        }
        URLConnection connection = url.openConnection();
        if (connection == null) {
            Log.e(DownloadCommand.class.getName(), "Connection is null");
            return null;
        }
        connection.setConnectTimeout(getResources().getInteger(R.integer.download_connect_timeout) * 1000);
        connection.setReadTimeout(getResources().getInteger(R.integer.download_read_timeout) * 1000);
        connection.setDoInput(true);
        connection.setDoOutput(false);
        connection.connect();
        return connection;
    }

    private FileOutputStream getOutputStream(String fileName) throws FileNotFoundException {
        return new FileOutputStream(new File(folder, fileName));
    }

    private void flushAndCloseOutputStream(OutputStream outputStream) {
        Log.d(DownloadCommand.class.getName(), "flushAndCloseOutputStream");
        try {
            if (outputStream != null) {
                outputStream.flush();
                outputStream.close();
            }
        } catch (Exception exc) {
            Log.e(DownloadCommand.class.getName(), "Error closing output stream", exc);
        }
    }

    private String getLocationHeader(URLConnection connection) {
        Log.d(DownloadCommand.class.getName(), "getLocationHeader");
        String location = HTTPUtil.getLocation(getContext(), connection);
        if (!StringUtil.isEmpty(location)) {
            return " " + getResources().getString(R.string.http_header_content_location) + ": " + location;
        }
        return "";
    }

    private String getFileName(URLConnection connection) {
        Log.d(DownloadCommand.class.getName(), "getFileName");
        String contentDisposition = HTTPUtil.getContentDisposition(getContext(), connection);
        String contentType = connection.getContentType();
        Log.d(DownloadCommand.class.getName(), "Content-Disposition header is " + contentDisposition);
        Log.d(DownloadCommand.class.getName(), "Content-Type header is " + contentType);
        String contentDispositionFileName = HTTPUtil.getFileNameFromContentDisposition(contentDisposition);
        String mimeType = HTTPUtil.getMimeTypeFromContentType(contentType);
        Log.d(DownloadCommand.class.getName(), "Parsed file name from content disposition is " + contentDispositionFileName);
        Log.d(DownloadCommand.class.getName(), "Parsed mime type from content type is " + mimeType);
        IFileManager fileManager = getFileManager();
        String fileName = fileManager.getDownloadFileName(url, contentDispositionFileName, mimeType);
        Log.d(DownloadCommand.class.getName(), "Download file name is " + fileName);
        String validFileName = fileManager.getValidFileName(folder, fileName);
        Log.d(DownloadCommand.class.getName(), "Adjusted valid file name is " + validFileName);
        return validFileName;
    }

    private boolean deleteDownloadedFile(String fileName) {
        Log.d(DownloadCommand.class.getName(), "deleteDownloadedFile. fileName is " + fileName);
        if (StringUtil.isEmpty(fileName)) {
            return false;
        }
        try {
            File file = new File(folder, fileName);
            IFileManager fileManager = getFileManager();
            return fileManager.delete(file);
        } catch (Exception e) {
            Log.e(DownloadCommand.class.getName(), "Error deleting file " + fileName);
            return false;
        }
    }

    private boolean downloadedFileExists(String fileName) {
        Log.d(DownloadCommand.class.getName(), "downloadedFileExists. fileName is " + fileName);
        if (StringUtil.isEmpty(fileName)) {
            return false;
        }
        try {
            File file = new File(folder, fileName);
            return file.exists();
        } catch (Exception exc) {
            Log.e(DownloadCommand.class.getName(), "Error checking if file " + fileName + " exists.");
            return false;
        }
    }

    private synchronized DownloadCommandResult createDownloadCommandResult(boolean connectSuccess, boolean downloadSuccess, boolean fileExists, boolean deleteSuccess, int httpCode, String httpMessage, String fileName, long duration, Exception exc) {
        return new DownloadCommandResult(connectSuccess, downloadSuccess, fileExists, deleteSuccess, valid, stopped, httpCode, httpMessage, fileName, duration, exc);
    }

    public synchronized boolean isValid() {
        return valid && !Thread.currentThread().isInterrupted();
    }

    private synchronized void initializeValid() {
        valid = true;
        stopped = false;
    }

    private synchronized void verifyValid() {
        Log.d(DownloadCommand.class.getName(), "verifyValid");
        try {
            NetworkTaskDAO networkTaskDAO = new NetworkTaskDAO(getContext());
            NetworkTask databaseTask = networkTaskDAO.readNetworkTask(networkTask.getId());
            if (databaseTask == null || networkTask.getSchedulerId() != databaseTask.getSchedulerId()) {
                Log.d(DownloadCommand.class.getName(), "verifyValid, network task is not valid");
                valid = false;
                stopped = false;
                return;
            }
            if (databaseTask.isRunning()) {
                Log.d(DownloadCommand.class.getName(), "verifyValid, network task is valid and running");
                valid = true;
                stopped = false;
                return;
            }
            Log.d(DownloadCommand.class.getName(), "verifyValid, network task is not running");
            valid = false;
            stopped = true;
        } catch (Exception exc) {
            Log.e(DownloadCommand.class.getName(), "Exception while verifying valid state.");
            valid = true;
            stopped = false;
        }
    }

    private void closeResources(URLConnection connection, InputStream inputStream, FileOutputStream outputStream, ScheduledExecutorService executorService) {
        Log.d(DownloadCommand.class.getName(), "closeResources");
        flushAndCloseOutputStream(outputStream);
        try {
            if (inputStream != null) {
                Log.d(DownloadCommand.class.getName(), "Closing input stream");
                inputStream.close();
            }
        } catch (Exception exc) {
            Log.e(DownloadCommand.class.getName(), "Error closing input stream", exc);
        }
        try {
            if (HTTPUtil.isHTTPConnection(connection)) {
                Log.d(DownloadCommand.class.getName(), "Disconnecting http connection");
                ((HttpURLConnection) connection).disconnect();
            }
        } catch (Exception exc) {
            Log.e(DownloadCommand.class.getName(), "Error closing http connection", exc);
        }
        if (executorService != null) {
            Log.d(DownloadCommand.class.getName(), "Shutting down ScheduledExecutorService for polling thread");
            executorService.shutdownNow();
        }
    }

    protected IFileManager getFileManager() {
        return new SystemFileManager(getContext());
    }

    private ITimeService createTimeService() {
        ServiceFactoryContributor factoryContributor = new ServiceFactoryContributor(getContext());
        return factoryContributor.createServiceFactory().createTimeService();
    }

    public ITimeService getTimeService() {
        return timeService;
    }

    private Context getContext() {
        return context;
    }

    private Resources getResources() {
        return getContext().getResources();
    }
}
