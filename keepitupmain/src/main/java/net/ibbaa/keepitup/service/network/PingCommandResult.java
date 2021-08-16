package net.ibbaa.keepitup.service.network;

import androidx.annotation.NonNull;

public class PingCommandResult {

    private final int processReturnCode;
    private final String output;
    private final Throwable exception;

    public PingCommandResult(int processReturnCode, String output, Throwable exception) {
        this.processReturnCode = processReturnCode;
        this.output = output;
        this.exception = exception;
    }

    public int getProcessReturnCode() {
        return processReturnCode;
    }

    public String getOutput() {
        return output;
    }

    public Throwable getException() {
        return exception;
    }

    @NonNull
    @Override
    public String toString() {
        return "PingCommandResult{" +
                "processReturnCode=" + processReturnCode +
                ", output='" + output + '\'' +
                ", exception=" + exception +
                '}';
    }
}
