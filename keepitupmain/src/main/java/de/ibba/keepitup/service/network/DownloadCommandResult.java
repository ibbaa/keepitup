package de.ibba.keepitup.service.network;

import androidx.annotation.NonNull;

public class DownloadCommandResult {

    private final boolean connectSuccess;
    private final boolean downloadSuccess;
    private final boolean fileExists;
    private final boolean deleteSuccess;
    private final boolean valid;
    private final boolean stopped;
    private final int httpResponseCode;
    private final String httpResponseMessage;
    private final String fileName;
    private final Throwable exception;

    public DownloadCommandResult(boolean connectSuccess, boolean downloadSuccess, boolean fileExists, boolean deleteSuccess, boolean valid, boolean stopped, int httpResponseCode, String httpResponseMessage, String fileName, Throwable exception) {
        this.connectSuccess = connectSuccess;
        this.downloadSuccess = downloadSuccess;
        this.fileExists = fileExists;
        this.deleteSuccess = deleteSuccess;
        this.valid = valid;
        this.stopped = stopped;
        this.httpResponseCode = httpResponseCode;
        this.httpResponseMessage = httpResponseMessage;
        this.fileName = fileName;
        this.exception = exception;
    }

    public boolean isConnectSuccess() {
        return connectSuccess;
    }

    public boolean isDownloadSuccess() {
        return downloadSuccess;
    }

    public boolean fileExists() {
        return fileExists;
    }

    public boolean isDeleteSuccess() {
        return deleteSuccess;
    }

    public boolean isValid() {
        return valid;
    }

    public boolean isStopped() {
        return stopped;
    }

    public int getHttpResponseCode() {
        return httpResponseCode;
    }

    public String getHttpResponseMessage() {
        return httpResponseMessage;
    }

    public String getFileName() {
        return fileName;
    }

    public Throwable getException() {
        return exception;
    }

    @NonNull
    @Override
    public String toString() {
        return "DownloadCommandResult{" +
                "connectSuccess=" + connectSuccess +
                ", downloadSuccess=" + downloadSuccess +
                ", fileExists=" + fileExists +
                ", deleteSuccess=" + deleteSuccess +
                ", valid=" + valid +
                ", stopped=" + stopped +
                ", httpResponseCode=" + httpResponseCode +
                ", httpResponseMessage='" + httpResponseMessage + '\'' +
                ", fileName='" + fileName + '\'' +
                ", exception=" + exception +
                '}';
    }
}
