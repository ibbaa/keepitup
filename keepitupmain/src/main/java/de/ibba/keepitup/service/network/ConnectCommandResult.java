package de.ibba.keepitup.service.network;

import androidx.annotation.NonNull;

public class ConnectCommandResult {

    private final boolean success;
    private final Throwable exception;

    public ConnectCommandResult(boolean success, Throwable exception) {
        this.success = success;
        this.exception = exception;
    }

    public boolean isSuccess() {
        return success;
    }

    public Throwable getException() {
        return exception;
    }

    @NonNull
    @Override
    public String toString() {
        return "ConnectCommandResult{" +
                "success=" + success +
                ", exception=" + exception +
                '}';
    }
}
