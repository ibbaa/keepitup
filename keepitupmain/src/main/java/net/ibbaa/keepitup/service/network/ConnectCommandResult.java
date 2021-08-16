package net.ibbaa.keepitup.service.network;

import androidx.annotation.NonNull;

public class ConnectCommandResult {

    private final boolean success;
    private final int attempts;
    private final int successfulAttempts;
    private final int timeoutAttempts;
    private final int errorAttempts;
    private final double averageTime;
    private final Throwable exception;

    public ConnectCommandResult(boolean success, int attempts, int successfulAttempts, int timeoutAttempts, int errorAttempts, double averageTime, Throwable exception) {
        this.success = success;
        this.attempts = attempts;
        this.successfulAttempts = successfulAttempts;
        this.timeoutAttempts = timeoutAttempts;
        this.errorAttempts = errorAttempts;
        this.averageTime = averageTime;
        this.exception = exception;
    }

    public boolean isSuccess() {
        return success;
    }

    public int getAttempts() {
        return attempts;
    }

    public int getSuccessfulAttempts() {
        return successfulAttempts;
    }

    public int getTimeoutAttempts() {
        return timeoutAttempts;
    }

    public int getErrorAttempts() {
        return errorAttempts;
    }

    public double getAverageTime() {
        return averageTime;
    }

    public Throwable getException() {
        return exception;
    }

    @NonNull
    @Override
    public String toString() {
        return "ConnectCommandResult{" +
                "success=" + success +
                ", attempts=" + attempts +
                ", successfulAttempts=" + successfulAttempts +
                ", timeoutAttempts=" + timeoutAttempts +
                ", errorAttempts=" + errorAttempts +
                ", averageTime=" + averageTime +
                ", exception=" + exception +
                '}';
    }
}
