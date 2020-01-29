package de.ibba.keepitup.service.network;

import androidx.annotation.NonNull;

public class ConnectCommandResult {

    private final boolean success;
    private final long duration;
    private final Throwable exception;

    public ConnectCommandResult(boolean success, long duration, Throwable exception) {
        this.success = success;
        this.duration = duration;
        this.exception = exception;
    }

    public boolean isSuccess() {
        return success;
    }

    public long getDuration() {
        return duration;
    }

    public Throwable getException() {
        return exception;
    }

    @NonNull
    @Override
    public String toString() {
        return "ConnectCommandResult{" +
                "success=" + success +
                ", duration=" + duration +
                ", exception=" + exception +
                '}';
    }
}
