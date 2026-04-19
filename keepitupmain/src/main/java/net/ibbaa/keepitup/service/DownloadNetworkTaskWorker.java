/*
 * Copyright (c) 2026 Alwin Ibba
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

package net.ibbaa.keepitup.service;

import android.content.Context;
import android.net.Uri;
import android.os.PowerManager;
import android.text.TextUtils;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.db.HeaderDAO;
import net.ibbaa.keepitup.db.ResolveDAO;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.model.AccessTypeData;
import net.ibbaa.keepitup.model.Header;
import net.ibbaa.keepitup.model.LogEntry;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.model.Resolve;
import net.ibbaa.keepitup.resources.PreferenceManager;
import net.ibbaa.keepitup.service.network.DownloadCommand;
import net.ibbaa.keepitup.service.network.DownloadCommandResult;
import net.ibbaa.keepitup.service.network.DownloadConnectResult;
import net.ibbaa.keepitup.util.FileUtil;
import net.ibbaa.keepitup.util.HTTPUtil;
import net.ibbaa.keepitup.util.StringUtil;
import net.ibbaa.keepitup.util.URLUtil;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@SuppressWarnings({"SequencedCollectionMethodCanBeUsed"})
public class DownloadNetworkTaskWorker extends NetworkTaskWorker {

    public DownloadNetworkTaskWorker(Context context, NetworkTask networkTask, PowerManager.WakeLock wakeLock) {
        super(context, networkTask, wakeLock);
    }

    @Override
    public int getMaxInstances() {
        return getResources().getInteger(R.integer.download_worker_max_instances);
    }

    @Override
    public String getMaxInstancesErrorMessage(int activeInstances) {
        return getResources().getQuantityString(R.plurals.text_download_worker_max_instances_error, activeInstances, activeInstances);
    }

    @Override
    public ExecutionResult execute(NetworkTask networkTask, AccessTypeData data) {
        Log.d(DownloadNetworkTaskWorker.class.getName(), "execute, network task is " + networkTask + " and access type data is " + data);
        List<Header> headers = getHeaders(networkTask, data);
        List<Resolve> resolves = getResolves(networkTask);
        URL url = determineURL(networkTask.getAddress());
        if (url == null) {
            Log.d(DownloadNetworkTaskWorker.class.getName(), "Determined url is null");
            LogEntry logEntry = new LogEntry();
            logEntry.setSuccess(false);
            logEntry.setMessage(getResources().getString(R.string.text_download_url_error, networkTask.getAddress()));
            completeLogEntry(networkTask, logEntry);
            return new ExecutionResult(false, logEntry);
        }
        Log.d(DownloadNetworkTaskWorker.class.getName(), "URL is " + url.toExternalForm());
        if (resolves == null || resolves.isEmpty()) {
            Log.d(DownloadNetworkTaskWorker.class.getName(), "No valid resolve object present");
            ExecutionResult downloadExecutionResult = executeDownloadCommand(networkTask, data, url, Collections.emptyList(), headers);
            LogEntry logEntry = downloadExecutionResult.getLogEntry();
            completeLogEntry(networkTask, logEntry);
            Log.d(DownloadNetworkTaskWorker.class.getName(), "Returning " + downloadExecutionResult);
            return downloadExecutionResult;
        }
        List<DownloadCommand.ConnectToAddress> connectToAddresses = prepareConnectToAddresses(resolves, url);
        ExecutionResult downloadExecutionResult = executeDownloadCommand(networkTask, data, url, connectToAddresses, headers);
        LogEntry logEntry = downloadExecutionResult.getLogEntry();
        completeLogEntry(networkTask, logEntry);
        Log.d(DownloadNetworkTaskWorker.class.getName(), "Returning " + downloadExecutionResult);
        return downloadExecutionResult;
    }

    private void completeLogEntry(NetworkTask networkTask, LogEntry logEntry) {
        logEntry.setNetworkTaskId(networkTask.getId());
        logEntry.setTimestamp(getTimeService().getCurrentTimestamp());
    }

    private List<DownloadCommand.ConnectToAddress> prepareConnectToAddresses(List<Resolve> resolves, URL url) {
        Log.d(DownloadNetworkTaskWorker.class.getName(), "prepareConnectToAddresses, resolve objects are " + resolves);
        Map<String, List<Integer>> hostToIndices = new LinkedHashMap<>();
        for (int ii = 0; ii < resolves.size(); ii++) {
            Resolve resolve = resolves.get(ii);
            completeResolve(resolve, url);
            String normalizedHost = URLUtil.normalizeHost(resolve.getTargetAddress());
            List<Integer> indices = hostToIndices.get(normalizedHost);
            if (indices == null) {
                indices = new ArrayList<>();
                hostToIndices.put(normalizedHost, indices);
            }
            indices.add(ii);
        }
        List<String> uniqueHosts = new ArrayList<>(hostToIndices.keySet());
        Log.d(DownloadNetworkTaskWorker.class.getName(), "prepareConnectToAddresses, " + resolves.size() + " resolves, " + uniqueHosts.size() + " unique hosts");
        List<DNSExecutionResult> dnsExecutionResults = executeDNSLookup(uniqueHosts, getResources().getBoolean(R.bool.network_prefer_ipv4));
        if (dnsExecutionResults == null || dnsExecutionResults.isEmpty() || dnsExecutionResults.size() < uniqueHosts.size()) {
            Log.e(DownloadNetworkTaskWorker.class.getName(), "prepareConnectToAddresses, DNS lookup returned inconsistent result");
            return Collections.emptyList();
        }
        Map<String, DNSExecutionResult> hostToResult = new LinkedHashMap<>();
        int resultIndex = 0;
        for (String host : hostToIndices.keySet()) {
            hostToResult.put(host, dnsExecutionResults.get(resultIndex++));
        }
        DNSExecutionResult[] resultByIndex = new DNSExecutionResult[resolves.size()];
        for (Map.Entry<String, List<Integer>> entry : hostToIndices.entrySet()) {
            DNSExecutionResult result = hostToResult.get(entry.getKey());
            for (int index : entry.getValue()) {
                resultByIndex[index] = result;
            }
        }
        return prepareConnectToAddresses(resolves, Arrays.asList(resultByIndex));
    }

    private List<DownloadCommand.ConnectToAddress> prepareConnectToAddresses(List<Resolve> resolves, List<DNSExecutionResult> dnsExecutionResults) {
        List<DownloadCommand.ConnectToAddress> connectToAddresses = new ArrayList<>();
        for (int ii = 0; ii < dnsExecutionResults.size(); ii++) {
            DNSExecutionResult dnsExecutionResult = dnsExecutionResults.get(ii);
            Resolve resolve = resolves.get(ii);
            if (dnsExecutionResult.getLogEntry().isSuccess()) {
                connectToAddresses.add(new DownloadCommand.ConnectToAddress(resolve, dnsExecutionResult.getAddress(), ""));
            } else {
                connectToAddresses.add(new DownloadCommand.ConnectToAddress(resolve, null, dnsExecutionResult.getLogEntry().getMessage()));
            }
        }
        return connectToAddresses;
    }

    private void completeResolve(Resolve resolve, URL url) {
        String sourceAddress = URLUtil.getSourceAddress(resolve, url);
        String targetAddress = URLUtil.getTargetAddress(resolve, url);
        int sourcePort = URLUtil.getSourcePort(resolve, url);
        int targetPort = URLUtil.getTargetPort(resolve, url);
        resolve.setSourceAddress(sourceAddress);
        resolve.setTargetAddress(targetAddress);
        resolve.setSourcePort(sourcePort);
        resolve.setTargetPort(targetPort);
    }

    private List<Resolve> getResolves(NetworkTask networkTask) {
        Log.d(DownloadNetworkTaskWorker.class.getName(), "getResolves for network task " + networkTask);
        ResolveDAO resolveDAO = new ResolveDAO(getContext());
        List<Resolve> resolves = resolveDAO.readAllResolvesForNetworkTask(networkTask.getId());
        Log.d(DownloadNetworkTaskWorker.class.getName(), "Resolve objects from database: " + resolves);
        return resolves;
    }

    private List<Header> getHeaders(NetworkTask networkTask, AccessTypeData accessTypeData) {
        Log.d(DownloadNetworkTaskWorker.class.getName(), "getHeaders for network task " + networkTask + " and accessTypeData " + accessTypeData);
        if (accessTypeData.isUseDefaultHeaders()) {
            return null;
        }
        HeaderDAO headerDAO = new HeaderDAO(getContext());
        return headerDAO.readHeadersForNetworkTask(networkTask.getId());
    }

    @SuppressWarnings("resource")
    private ExecutionResult executeDownloadCommand(NetworkTask networkTask, AccessTypeData data, URL url, List<DownloadCommand.ConnectToAddress> connectToAddresses, List<Header> headers) {
        Log.d(DownloadNetworkTaskWorker.class.getName(), "executeDownloadCommand, networkTask is " + networkTask);
        PreferenceManager preferenceManager = new PreferenceManager(getContext());
        IFileManager fileManager = getFileManager();
        LogEntry logEntry = new LogEntry();
        boolean interrupted = false;
        String folder = determineDownloadFolder();
        if (folder == null) {
            Log.d(DownloadNetworkTaskWorker.class.getName(), "Determined folder is null");
            logEntry.setSuccess(false);
            String message;
            if (preferenceManager.getPreferenceAllowArbitraryFileLocation()) {
                getNotificationHandler().sendMessageNotificationMissingDownloadFolderPermission();
                message = getResources().getString(R.string.text_download_folder_error_arbitrary_permission, Uri.decode(preferenceManager.getPreferenceArbitraryDownloadFolder()));
            } else {
                String folderMessage = preferenceManager.getPreferenceDownloadExternalStorage() ? preferenceManager.getPreferenceDownloadFolder() : fileManager.getDefaultDownloadDirectoryName();
                message = getResources().getString(R.string.text_download_folder_error, folderMessage);
            }
            logEntry.setMessage(message);
            return new ExecutionResult(false, logEntry);
        } else {
            Log.d(DownloadNetworkTaskWorker.class.getName(), "Download folder is " + folder);
        }
        boolean delete = determineDeleteDownloadedFile();
        Log.d(DownloadNetworkTaskWorker.class.getName(), "Delete downloaded file: " + delete);
        Callable<DownloadCommandResult> downloadCommand = getDownloadCommand(networkTask, data, url, folder, delete, connectToAddresses, headers);
        int timeout = getResources().getInteger(R.integer.download_timeout);
        Log.d(DownloadNetworkTaskWorker.class.getName(), "Creating ExecutorService");
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<DownloadCommandResult> downloadResultFuture = null;
        try {
            downloadResultFuture = executorService.submit(downloadCommand);
            DownloadCommandResult downloadResult = downloadResultFuture.get(timeout, TimeUnit.SECONDS);
            Log.d(DownloadNetworkTaskWorker.class.getName(), downloadCommand.getClass().getSimpleName() + " returned " + downloadResult);
            if (returnedConnectFailure(downloadResult)) {
                Log.d(DownloadNetworkTaskWorker.class.getName(), "Connection failed. Preparing error message.");
                prepareConnectError(downloadResult, timeout, folder, delete, logEntry);
                return new ExecutionResult(false, logEntry);
            }
            if (returnedHttpFailure(downloadResult)) {
                Log.d(DownloadNetworkTaskWorker.class.getName(), "HTTP download and HTTP return code is not HTTP_OK. Preparing error message.");
                prepareHTTPReturnCodeError(downloadResult, timeout, folder, delete, logEntry);
                return new ExecutionResult(false, logEntry);
            }
            if (downloadResult.downloadSuccess()) {
                if (!downloadResult.fileExists()) {
                    Log.d(DownloadNetworkTaskWorker.class.getName(), "The download was successful but the downloaded file does not exist. Preparing error message.");
                    prepareUnknownError(downloadResult, timeout, folder, delete, logEntry);
                    return new ExecutionResult(false, logEntry);
                }
                Log.d(DownloadNetworkTaskWorker.class.getName(), "The download was successful. Preparing message.");
                prepareSuccess(downloadResult, timeout, folder, delete, logEntry);
                return new ExecutionResult(false, logEntry);
            }
            if (downloadResult.stopped()) {
                Log.d(DownloadNetworkTaskWorker.class.getName(), "The download was stopped. Preparing error message.");
                prepareStoppedError(downloadResult, timeout, folder, delete, logEntry);
                return new ExecutionResult(false, logEntry);
            }
            if (!downloadResult.valid()) {
                Log.d(DownloadNetworkTaskWorker.class.getName(), "The network task is invalid. Preparing error message.");
                prepareInvalidError(downloadResult, timeout, folder, delete, logEntry);
                return new ExecutionResult(false, logEntry);
            }
            Log.d(DownloadNetworkTaskWorker.class.getName(), "The download failed for an unknown reason.");
            prepareUnknownError(downloadResult, timeout, folder, delete, logEntry);
        } catch (Throwable exc) {
            Log.d(DownloadNetworkTaskWorker.class.getName(), "Error executing " + downloadCommand.getClass().getName(), exc);
            logEntry.setSuccess(false);
            logEntry.setMessage(getMessageFromException(getResources().getString(R.string.text_download_error, url.toExternalForm()), exc, timeout));
            if (downloadResultFuture != null && isInterrupted(exc)) {
                Log.d(DownloadNetworkTaskWorker.class.getName(), "Cancelling " + downloadCommand.getClass().getSimpleName());
                downloadResultFuture.cancel(true);
                interrupted = true;
            }
        } finally {
            Log.d(DownloadNetworkTaskWorker.class.getName(), "Shutting down ExecutorService");
            executorService.shutdownNow();
        }
        return new ExecutionResult(interrupted, logEntry);
    }

    private void prepareConnectError(DownloadCommandResult downloadResult, int timeout, String folder, boolean delete, LogEntry logEntry) {
        Log.d(DownloadNetworkTaskWorker.class.getName(), "prepareConnectError");
        String downloadError = getResources().getString(R.string.text_download_error, downloadResult.url().toExternalForm());
        prepareError(downloadResult, timeout, folder, delete, logEntry, downloadError);
    }

    private void prepareHTTPReturnCodeError(DownloadCommandResult downloadResult, int timeout, String folder, boolean delete, LogEntry logEntry) {
        Log.d(DownloadNetworkTaskWorker.class.getName(), "prepareHTTPReturnCodeError");
        String downloadError = getResources().getString(R.string.text_download_error, downloadResult.url().toExternalForm());
        String httpMessage;
        String actualReturnMessage = getActualReturnMessage(downloadResult);
        if (StringUtil.isEmpty(actualReturnMessage)) {
            httpMessage = getResources().getString(R.string.text_download_http_error_without_message, getActualReturnCode(downloadResult));
        } else {
            httpMessage = getResources().getString(R.string.text_download_http_error_with_message, getActualReturnCode(downloadResult), actualReturnMessage);
        }
        String message = downloadError + " " + httpMessage;
        prepareError(downloadResult, timeout, folder, delete, logEntry, message);
    }

    private void prepareStoppedError(DownloadCommandResult downloadResult, int timeout, String folder, boolean delete, LogEntry logEntry) {
        Log.d(DownloadNetworkTaskWorker.class.getName(), "prepareStoppedError");
        String stoppedMessage = getResources().getString(R.string.text_download_stopped_error);
        prepareError(downloadResult, timeout, folder, delete, logEntry, stoppedMessage);
    }

    private void prepareInvalidError(DownloadCommandResult downloadResult, int timeout, String folder, boolean delete, LogEntry logEntry) {
        Log.d(DownloadNetworkTaskWorker.class.getName(), "prepareInvalidError");
        String invalidMessage = getResources().getString(R.string.text_download_invalid_error);
        prepareError(downloadResult, timeout, folder, delete, logEntry, invalidMessage);
    }

    private void prepareUnknownError(DownloadCommandResult downloadResult, int timeout, String folder, boolean delete, LogEntry logEntry) {
        Log.d(DownloadNetworkTaskWorker.class.getName(), "prepareUnknownError");
        logEntry.setSuccess(false);
        Throwable exc = downloadResult.exception();
        String message;
        if (exc == null) {
            message = getResources().getString(R.string.text_download_unknown_error, downloadResult.url().toExternalForm());
        } else {
            message = getResources().getString(R.string.text_download_error, downloadResult.url().toExternalForm());
        }
        prepareError(downloadResult, timeout, folder, delete, logEntry, message);
    }

    private void prepareError(DownloadCommandResult downloadResult, int timeout, String folder, boolean delete, LogEntry logEntry, String message) {
        Log.d(DownloadNetworkTaskWorker.class.getName(), "prepareError");
        logEntry.setSuccess(false);
        String redirectMessage = getRedirectMessage(downloadResult);
        if (downloadResult.fileExists()) {
            Log.d(DownloadNetworkTaskWorker.class.getName(), "The file was partially downloaded.");
            PreferenceManager preferenceManager = new PreferenceManager(getContext());
            if (preferenceManager.getPreferenceAllowArbitraryFileLocation()) {
                folder = Uri.decode(folder);
            }
            message += " " + getResources().getString(R.string.text_download_partial);
            if (!delete) {
                message += " " + getResources().getString(R.string.text_download_file, new File(folder, downloadResult.fileName()).toString());
            } else {
                if (downloadResult.deleteSuccess()) {
                    Log.d(DownloadNetworkTaskWorker.class.getName(), "The partially downloaded file was deleted.");
                    message += " " + getResources().getString(R.string.text_download_partial_delete);
                } else {
                    Log.d(DownloadNetworkTaskWorker.class.getName(), "The deletion of the partially downloaded file failed.");
                    message += " " + getResources().getString(R.string.text_download_partial_delete_error);
                }
            }
            String durationMessage = getResources().getString(R.string.text_download_time, StringUtil.formatTimeRange(downloadResult.duration(), getContext()));
            message += " " + durationMessage;
        }
        String connectMessage = getConnectionMessage(getActualConnectResult(downloadResult));
        if (!StringUtil.isEmpty(connectMessage) && !StringUtil.isEmpty(message)) {
            connectMessage += " ";
        }
        String invalidHeaderMessage = getInvalidHeaderMessage(getActualConnectResult(downloadResult));
        if (!StringUtil.isEmpty(invalidHeaderMessage)) {
            connectMessage += invalidHeaderMessage + " ";
        }
        Throwable exc = downloadResult.exception();
        String mismatchMessage = getProtocolMismatchMessage(downloadResult);
        if (exc == null) {
            logEntry.setMessage(redirectMessage + connectMessage + message + mismatchMessage);
        } else {
            logEntry.setMessage(redirectMessage + connectMessage + getMessageFromException(message, exc, timeout) + mismatchMessage);
        }
    }

    private void prepareSuccess(DownloadCommandResult downloadResult, int timeout, String folder, boolean delete, LogEntry logEntry) {
        Log.d(DownloadNetworkTaskWorker.class.getName(), "prepareSuccess");
        logEntry.setSuccess(true);
        String successMessage = getRedirectMessage(downloadResult);
        String connectMessage = getConnectionMessage(getActualConnectResult(downloadResult));
        if (!StringUtil.isEmpty(connectMessage)) {
            successMessage += connectMessage + " ";
        }
        String invalidHeaderMessage = getInvalidHeaderMessage(getActualConnectResult(downloadResult));
        if (!StringUtil.isEmpty(invalidHeaderMessage)) {
            successMessage += invalidHeaderMessage + " ";
        }
        successMessage += getResources().getString(R.string.text_download_success, downloadResult.url().toExternalForm());
        String durationMessage = getResources().getString(R.string.text_download_time, StringUtil.formatTimeRange(downloadResult.duration(), getContext()));
        if (!delete) {
            PreferenceManager preferenceManager = new PreferenceManager(getContext());
            if (preferenceManager.getPreferenceAllowArbitraryFileLocation()) {
                folder = Uri.decode(folder);
            }
            String fileMessage = getResources().getString(R.string.text_download_file, new File(folder, downloadResult.fileName()).toString());
            logEntry.setMessage(successMessage + " " + fileMessage + " " + durationMessage);
            return;
        }
        if (downloadResult.deleteSuccess()) {
            String deleteMessage = getResources().getString(R.string.text_download_delete);
            logEntry.setMessage(successMessage + " " + deleteMessage + " " + durationMessage);
        } else {
            Log.d(DownloadNetworkTaskWorker.class.getName(), "The download was successful but the file could not be deleted.");
            String deleteErrorMessage = getResources().getString(R.string.text_download_delete_error);
            String message = successMessage + " " + deleteErrorMessage + " " + durationMessage;
            Throwable exc = downloadResult.exception();
            if (exc == null) {
                logEntry.setMessage(message);
            } else {
                logEntry.setMessage(getMessageFromException(message, exc, timeout));
            }
        }
    }

    private URL determineURL(String baseURL) {
        Log.d(DownloadNetworkTaskWorker.class.getName(), "determineURL, baseURL is " + baseURL);
        return URLUtil.getURL(baseURL);
    }

    private boolean returnedConnectFailure(DownloadCommandResult downloadResult) {
        List<DownloadConnectResult> connectResults = downloadResult.connectResults();
        if (connectResults == null || connectResults.isEmpty()) {
            return false;
        }
        DownloadConnectResult connectResult = getActualConnectResult(downloadResult);
        if (connectResult == null) {
            return false;
        }
        return !connectResult.success();
    }

    private DownloadConnectResult getActualConnectResult(DownloadCommandResult downloadResult) {
        List<DownloadConnectResult> connectResults = downloadResult.connectResults();
        if (connectResults == null || connectResults.isEmpty()) {
            return null;
        }
        return connectResults.get(connectResults.size() - 1);
    }

    private boolean returnedHttpFailure(DownloadCommandResult downloadResult) {
        List<Integer> httpResponseCodes = downloadResult.httpResponseCodes();
        if (httpResponseCodes == null || httpResponseCodes.isEmpty()) {
            return false;
        }
        int code = getActualReturnCode(downloadResult);
        if (code < 0) {
            return false;
        }
        return !HTTPUtil.isHTTPReturnCodeOk(code);
    }

    private int getActualReturnCode(DownloadCommandResult downloadResult) {
        List<Integer> httpResponseCodes = downloadResult.httpResponseCodes();
        if (httpResponseCodes == null || httpResponseCodes.isEmpty()) {
            return -1;
        }
        return httpResponseCodes.get(httpResponseCodes.size() - 1);
    }

    private String getActualReturnMessage(DownloadCommandResult downloadResult) {
        List<String> httpResponseMessages = downloadResult.httpResponseMessages();
        if (httpResponseMessages == null || httpResponseMessages.isEmpty()) {
            return "";
        }
        return httpResponseMessages.get(httpResponseMessages.size() - 1);
    }

    private String getRedirectMessage(DownloadCommandResult downloadResult) {
        PreferenceManager preferenceManager = new PreferenceManager(getContext());
        StringBuilder message = new StringBuilder();
        if (!preferenceManager.getPreferenceDownloadFollowsRedirects()) {
            return message.toString();
        }
        List<Integer> httpResponseCodes = downloadResult.httpResponseCodes();
        List<String> httpResponseMessages = downloadResult.httpResponseMessages();
        for (int ii = 0; ii < httpResponseCodes.size(); ii++) {
            if (HTTPUtil.isHTTPReturnCodeRedirect(httpResponseCodes.get(ii))) {
                String connectMessage = getConnectionMessage(downloadResult, ii);
                if (!StringUtil.isEmpty(connectMessage)) {
                    message.append(connectMessage);
                    message.append(" ");
                }
                String httpMessage = ii < httpResponseMessages.size() ? httpResponseMessages.get(ii) : "";
                if (StringUtil.isEmpty(httpMessage)) {
                    message.append(getResources().getString(R.string.text_download_http_redirect_without_message, httpResponseCodes.get(ii)));
                    message.append(" ");
                } else {
                    message.append(getResources().getString(R.string.text_download_http_redirect_with_message, httpResponseCodes.get(ii), httpMessage));
                    message.append(" ");
                }
            }
        }
        return message.toString();
    }

    private String getConnectionMessage(DownloadCommandResult downloadResult, int index) {
        List<DownloadConnectResult> connectResults = downloadResult.connectResults();
        if (connectResults == null || connectResults.size() <= index) {
            return "";
        }
        DownloadConnectResult connectResult = connectResults.get(index);
        return getConnectionMessage(connectResult);
    }

    private String getConnectionMessage(DownloadConnectResult connectResult) {
        if (connectResult == null) {
            return "";
        }
        if (connectResult.success()) {
            return getResources().getString(R.string.text_download_connect_success, getHostAndPortMessage(connectResult));
        }
        String connectMessage = "";
        if (!StringUtil.isEmpty(connectResult.connectMessage())) {
            connectMessage = " " + connectResult.connectMessage();
            if (!connectMessage.endsWith(".")) {
                connectMessage += ".";
            }
        }
        return getResources().getString(R.string.text_download_connect_error, getHostAndPortMessage(connectResult)) + connectMessage;
    }

    private String getInvalidHeaderMessage(DownloadConnectResult connectResult) {
        if (connectResult == null) {
            return "";
        }
        List<Header> invalidHeaders = connectResult.invalidHeader();
        if (invalidHeaders == null || invalidHeaders.isEmpty()) {
            return "";
        }
        List<String> names = new ArrayList<>();
        for (Header header : invalidHeaders) {
            if (HTTPUtil.isBasicAuthHeader(header)) {
                names.add(header.getName() + " (" + getResources().getString(R.string.label_dialog_basic_auth_title) + ")");
            } else {
                names.add(header.getName());
            }
        }
        return getResources().getString(R.string.text_download_invalid_header, TextUtils.join(", ", names));
    }

    private String getHostAndPortMessage(DownloadConnectResult connectResult) {
        String host;
        int port;
        if (connectResult.connectAddress() == null || connectResult.connectPort() < 0) {
            host = URLUtil.isValidIP6Address(connectResult.host()) ? "[" + connectResult.host() + "]" : connectResult.host();
            port = connectResult.port();
        } else {
            String hostAddress = connectResult.connectAddress().getHostAddress();
            host = URLUtil.isValidIP6Address(hostAddress) ? "[" + hostAddress + "]" : hostAddress;
            port = connectResult.connectPort();
        }
        return host + ":" + port;
    }

    private String getProtocolMismatchMessage(DownloadCommandResult downloadResult) {
        DownloadConnectResult connectResult = getActualConnectResult(downloadResult);
        if (connectResult == null) {
            return "";
        }
        if (URLUtil.isHTTP(downloadResult.url())) {
            if (connectResult.connectPort() == 443 || (connectResult.connectPort() < 0 && connectResult.port() == 443)) {
                return " " + getResources().getString(R.string.text_download_protocol_mismatch_http_to_443);
            }
        }
        if (URLUtil.isHTTPS(downloadResult.url())) {
            if (connectResult.connectPort() == 80 || (connectResult.connectPort() < 0 && connectResult.port() == 80)) {
                return " " + getResources().getString(R.string.text_download_protocol_mismatch_https_to_80);
            }
        }
        return "";
    }

    private String determineDownloadFolder() {
        Log.d(DownloadNetworkTaskWorker.class.getName(), "determineDownloadFolder");
        PreferenceManager preferenceManager = new PreferenceManager(getContext());
        IFileManager fileManager = getFileManager();
        if (preferenceManager.getPreferenceDownloadExternalStorage()) {
            if (preferenceManager.getPreferenceAllowArbitraryFileLocation()) {
                Log.d(DownloadNetworkTaskWorker.class.getName(), "Determining arbitrary external download folder");
                String arbitraryDownloadFolder = preferenceManager.getPreferenceArbitraryDownloadFolder();
                Log.d(DownloadNetworkTaskWorker.class.getName(), "Specified arbitrary external download folder is " + arbitraryDownloadFolder);
                if (getDocumentManager().getArbitraryDirectory(arbitraryDownloadFolder) == null) {
                    Log.e(DownloadNetworkTaskWorker.class.getName(), "Error accessing folder " + arbitraryDownloadFolder);
                    return null;
                }
                if (!getStoragePermissionManager().hasPersistentPermission(getContext(), arbitraryDownloadFolder)) {
                    return null;
                }
                return arbitraryDownloadFolder;
            } else {
                Log.d(DownloadNetworkTaskWorker.class.getName(), "Determining external download folder");
                String preferenceDownloadFolder = preferenceManager.getPreferenceDownloadFolder();
                Log.d(DownloadNetworkTaskWorker.class.getName(), "Specified external download folder is " + preferenceDownloadFolder);
                File folder = FileUtil.getExternalDirectory(fileManager, preferenceManager, preferenceDownloadFolder);
                if (folder == null) {
                    return null;
                }
                return folder.getAbsolutePath();
            }
        }
        Log.d(DownloadNetworkTaskWorker.class.getName(), "Determining internal download folder");
        File folder = fileManager.getInternalDownloadDirectory();
        if (folder == null) {
            return null;
        }
        return folder.getAbsolutePath();
    }

    private boolean determineDeleteDownloadedFile() {
        Log.d(DownloadNetworkTaskWorker.class.getName(), "determineDeleteDownloadedFile");
        PreferenceManager preferenceManager = new PreferenceManager(getContext());
        if (preferenceManager.getPreferenceDownloadExternalStorage()) {
            Log.d(DownloadNetworkTaskWorker.class.getName(), "Downloading to external folder. Using keep downloaded files switch.");
            return !preferenceManager.getPreferenceDownloadKeep();
        }
        Log.d(DownloadNetworkTaskWorker.class.getName(), "Downloading to internal folder. Always delete.");
        return true;
    }

    protected IFileManager getFileManager() {
        return new SystemFileManager(getContext());
    }

    protected Callable<DownloadCommandResult> getDownloadCommand(NetworkTask networkTask, AccessTypeData data, URL url, String folder, boolean delete, List<DownloadCommand.ConnectToAddress> connectToAddresses, List<Header> headers) {
        return new DownloadCommand(getContext(), networkTask, data, url, folder, delete, connectToAddresses, headers);
    }
}
