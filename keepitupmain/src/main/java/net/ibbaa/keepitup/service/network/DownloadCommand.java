/*
 * Copyright (c) 2025 Alwin Ibba
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
import android.os.ParcelFileDescriptor;

import androidx.documentfile.provider.DocumentFile;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.db.NetworkTaskDAO;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.resources.PreferenceManager;
import net.ibbaa.keepitup.resources.ServiceFactoryContributor;
import net.ibbaa.keepitup.service.IDocumentManager;
import net.ibbaa.keepitup.service.IFileManager;
import net.ibbaa.keepitup.service.ITimeService;
import net.ibbaa.keepitup.service.SystemDocumentManager;
import net.ibbaa.keepitup.service.SystemFileManager;
import net.ibbaa.keepitup.util.HTTPUtil;
import net.ibbaa.keepitup.util.NumberUtil;
import net.ibbaa.keepitup.util.StreamUtil;
import net.ibbaa.keepitup.util.StringUtil;
import net.ibbaa.keepitup.util.URLUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DownloadCommand implements Callable<DownloadCommandResult> {

    private final static String UNKNOWN_MIME_TYPE = "unknown/unknown";

    private final Context context;
    private final NetworkTask networkTask;
    private final URL url;
    private final String folder;
    private final boolean delete;
    private boolean valid;
    private boolean stopped;
    private final ITimeService timeService;

    public DownloadCommand(Context context, NetworkTask networkTask, URL url, String folder, boolean delete) {
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
        URL downloadUrl = url;
        URLConnection connection = null;
        boolean redirect = false;
        InputStream inputStream = null;
        FileOutputStream outputStream = null;
        ParcelFileDescriptor fileDescriptor = null;
        boolean connectSuccess = false;
        boolean downloadSuccess = false;
        boolean fileExists;
        boolean deleteSuccess = false;
        List<Integer> httpCodes = new ArrayList<>();
        List<String> httpMessages = new ArrayList<>();
        String fileName = null;
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        initializeValid();
        long start = -1;
        PreferenceManager preferenceManager = new PreferenceManager(context);
        try {
            do {
                Log.d(DownloadCommand.class.getName(), "Establishing connection to " + downloadUrl);
                redirect = false;
                connectSuccess = false;
                start = timeService.getCurrentTimestamp();
                connection = openConnection(downloadUrl);
                if (connection == null) {
                    Log.d(DownloadCommand.class.getName(), "Error establishing connection to " + downloadUrl);
                    long end = timeService.getCurrentTimestamp();
                    return createDownloadCommandResult(downloadUrl, false, false, false, false, httpCodes, null, null, NumberUtil.ensurePositive(end - start), null);
                }
                connectSuccess = true;
                Log.d(DownloadCommand.class.getName(), "Connection established.");
                if (HTTPUtil.isHTTPConnection(connection)) {
                    Log.d(DownloadCommand.class.getName(), "Download is an HTTP download.");
                    HttpURLConnection httpConnection = (HttpURLConnection) connection;
                    int httpCode = httpConnection.getResponseCode();
                    Log.d(DownloadCommand.class.getName(), "HTTP return code is " + httpCode);
                    String httpMessage = httpConnection.getResponseMessage();
                    String location = HTTPUtil.getLocation(getContext(), connection);
                    httpMessage += getLocationHeaderMessage(location);
                    Log.d(DownloadCommand.class.getName(), "HTTP message is " + httpMessage);
                    if (HTTPUtil.isHTTPReturnCodeRedirect(httpCode) && preferenceManager.getPreferenceDownloadFollowsRedirects()) {
                        Log.d(DownloadCommand.class.getName(), "HTTP return code " + httpCode + " is a redirect, location is " + location);
                        if (StringUtil.isEmpty(location)) {
                            Log.e(DownloadCommand.class.getName(), "Location header is empty");
                            httpMessage += getLocationInvalidMessage();
                        } else if (!URLUtil.isValidURL(location)) {
                            Log.e(DownloadCommand.class.getName(), "Location URL is invalid");
                            httpMessage += getLocationInvalidMessage();
                        } else {
                            URL locationUrl = URLUtil.getURL(location, null);
                            if (locationUrl != null) {
                                redirect = true;
                                downloadUrl = locationUrl;
                            } else {
                                Log.e(DownloadCommand.class.getName(), "Error parsing location URL");
                                httpMessage += getLocationInvalidMessage();
                            }
                        }
                    }
                    httpCodes.add(httpCode);
                    httpMessages.add(httpMessage);
                    if (!HTTPUtil.isHTTPReturnCodeOk(httpCode) && !redirect) {
                        Log.d(DownloadCommand.class.getName(), "Connection successful but HTTP return code " + httpCode + " is not HTTP_OK");
                        long end = timeService.getCurrentTimestamp();
                        return createDownloadCommandResult(downloadUrl, true, false, false, false, httpCodes, httpMessages, null, NumberUtil.ensurePositive(end - start), null);
                    }
                } else {
                    Log.d(DownloadCommand.class.getName(), "Download is not an HTTP download.");
                }
            } while (redirect);
            fileName = getFileName(connection, downloadUrl);
            if (fileName == null) {
                Log.d(DownloadCommand.class.getName(), "Connection successful but download file name could not be determined");
                long end = timeService.getCurrentTimestamp();
                return createDownloadCommandResult(downloadUrl, true, false, false, false, httpCodes, httpMessages, null, NumberUtil.ensurePositive(end - start), null);
            }
            Log.d(DownloadCommand.class.getName(), "Using file name " + fileName);
            Log.d(DownloadCommand.class.getName(), "Opening streams...");
            inputStream = connection.getInputStream();
            if (useDocumentFileAPI()) {
                DocumentFile downloadDocumentFile = getDownloadDocumentFile(fileName);
                if (downloadDocumentFile == null) {
                    Log.e(DownloadCommand.class.getName(), "Error access download file");
                    long end = timeService.getCurrentTimestamp();
                    return createDownloadCommandResult(downloadUrl, true, false, false, false, httpCodes, httpMessages, null, NumberUtil.ensurePositive(end - start), null);
                }
                fileDescriptor = getDownloadFileDescriptor(downloadDocumentFile);
                outputStream = getOutputStream(fileDescriptor);
            } else {
                outputStream = getOutputStream(fileName);
            }
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
            return createDownloadCommandResult(downloadUrl, true, downloadSuccess, fileExists, deleteSuccess, httpCodes, httpMessages, fileName, NumberUtil.ensurePositive(end - start), null);
        } catch (Exception exc) {
            Log.e(DownloadCommand.class.getName(), "Error executing download command", exc);
            Log.e(DownloadCommand.class.getName(), "Try closing stream.");
            flushAndCloseOutputStream(outputStream);
            fileExists = downloadedFileExists(fileName);
            Log.d(DownloadCommand.class.getName(), "Download file exists: " + fileExists);
            if (delete && fileExists) {
                Log.d(DownloadCommand.class.getName(), "Deleting downloaded file...");
                deleteSuccess = deleteDownloadedFile(fileName);
            }
            long end = timeService.getCurrentTimestamp();
            return createDownloadCommandResult(downloadUrl, connectSuccess, downloadSuccess, fileExists, deleteSuccess, httpCodes, httpMessages, fileName, NumberUtil.ensurePositive(end - start), exc);
        } finally {
            closeResources(connection, inputStream, outputStream, fileDescriptor, executorService);
        }
    }

    protected URLConnection openConnection(URL url) throws IOException {
        Log.d(DownloadCommand.class.getName(), "openConnection to " + url);
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

    private DocumentFile getDownloadDocumentFile(String fileName) {
        Log.d(DownloadCommand.class.getName(), "getDownloadDocumentFile, fileName is " + fileName);
        DocumentFile documentDownloadDirectory = getDocumentManager().getFolder(folder);
        if (documentDownloadDirectory == null) {
            Log.e(DownloadCommand.class.getName(), "Error accessing download folder " + folder);
            return null;
        }
        DocumentFile documentDownloadFile = getDocumentManager().getFile(documentDownloadDirectory, fileName);
        if (documentDownloadFile == null) {
            documentDownloadFile = documentDownloadDirectory.createFile(UNKNOWN_MIME_TYPE, fileName);
        }
        return documentDownloadFile;
    }

    protected ParcelFileDescriptor getDownloadFileDescriptor(DocumentFile documentDownloadFile) throws IOException {
        return getContext().getContentResolver().openFileDescriptor(documentDownloadFile.getUri(), "wa");
    }

    protected FileOutputStream getOutputStream(ParcelFileDescriptor documentFileDescriptor) {
        return new FileOutputStream(documentFileDescriptor.getFileDescriptor());
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

    private String getLocationHeaderMessage(String location) {
        Log.d(DownloadCommand.class.getName(), "getLocationHeaderString, location is " + location);
        if (!StringUtil.isEmpty(location)) {
            return " " + getResources().getString(R.string.http_header_content_location) + ": " + location;
        }
        return "";
    }

    private String getFileName(URLConnection connection, URL url) {
        Log.d(DownloadCommand.class.getName(), "getFileName");
        if (folder == null) {
            Log.d(DownloadCommand.class.getName(), "Download folder is null");
            return null;
        }
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
        String validFileName = getValidFileName(fileName);
        Log.d(DownloadCommand.class.getName(), "Adjusted valid file name is " + validFileName);
        return validFileName;
    }

    private String getLocationInvalidMessage() {
        return " " + getResources().getString(R.string.text_download_http_redirect_location_invalid);
    }

    private String getValidFileName(String fileName) {
        Log.d(DownloadCommand.class.getName(), "getValidFileName, fileName is " + fileName);
        if (useDocumentFileAPI()) {
            IDocumentManager documentManager = getDocumentManager();
            DocumentFile downloadDirectory = documentManager.getFolder(folder);
            if (downloadDirectory != null) {
                return documentManager.getValidFileName(downloadDirectory, fileName);
            }
            Log.e(DownloadCommand.class.getName(), "Error accessing download folder");
            return null;
        }
        IFileManager fileManager = getFileManager();
        return fileManager.getValidFileName(new File(folder), fileName);
    }

    private boolean deleteDownloadedFile(String fileName) {
        Log.d(DownloadCommand.class.getName(), "deleteDownloadedFile. fileName is " + fileName);
        if (StringUtil.isEmpty(fileName)) {
            return false;
        }
        try {
            if (useDocumentFileAPI()) {
                DocumentFile downloadDocumentFile = getDownloadDocumentFile(fileName);
                if (downloadDocumentFile == null) {
                    return false;
                }
                return getDocumentManager().delete(downloadDocumentFile);

            }
            File file = new File(folder, fileName);
            IFileManager fileManager = getFileManager();
            return fileManager.delete(file);
        } catch (Exception exc) {
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
            if (useDocumentFileAPI()) {
                DocumentFile documentDownloadDirectory = getDocumentManager().getFolder(folder);
                if (documentDownloadDirectory == null) {
                    Log.e(DownloadCommand.class.getName(), "Error accessing download folder " + folder);
                    return false;
                }
                return getDocumentManager().fileExists(documentDownloadDirectory, fileName);
            }
            File file = new File(folder, fileName);
            return file.exists();
        } catch (Exception exc) {
            Log.e(DownloadCommand.class.getName(), "Error checking if file " + fileName + " exists.");
            return false;
        }
    }

    private synchronized DownloadCommandResult createDownloadCommandResult(URL url, boolean connectSuccess, boolean downloadSuccess, boolean fileExists, boolean deleteSuccess, List<Integer> httpCodes, List<String>  httpMessages, String fileName, long duration, Exception exc) {
        return new DownloadCommandResult(url, connectSuccess, downloadSuccess, fileExists, deleteSuccess, valid, stopped, httpCodes, httpMessages, fileName, duration, exc);
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

    private void closeResources(URLConnection connection, InputStream inputStream, FileOutputStream outputStream, ParcelFileDescriptor fileDescriptor, ScheduledExecutorService executorService) {
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
        try {
            if (fileDescriptor != null) {
                fileDescriptor.close();
            }
        } catch (Exception exc) {
            Log.e(DownloadCommand.class.getName(), "Error closing http connection", exc);
        }
        if (executorService != null) {
            Log.d(DownloadCommand.class.getName(), "Shutting down ScheduledExecutorService for polling thread");
            executorService.shutdownNow();
        }
    }

    private boolean useDocumentFileAPI() {
        PreferenceManager preferenceManager = new PreferenceManager(getContext());
        return preferenceManager.getPreferenceAllowArbitraryFileLocation() && preferenceManager.getPreferenceDownloadExternalStorage();
    }

    protected IFileManager getFileManager() {
        return new SystemFileManager(getContext(), getTimeService());
    }

    protected IDocumentManager getDocumentManager() {
        return new SystemDocumentManager(getContext(), getTimeService());
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
