package de.ibba.keepitup.logging;

import androidx.annotation.NonNull;

public class LogFileEntry {

    private long timestamp;
    private LogLevel level;
    private String tag;
    private String message;
    private Throwable throwable;

    public LogFileEntry(long timestamp, LogLevel level, String tag, String message, Throwable throwable) {
        this.timestamp = timestamp;
        this.level = level;
        this.tag = tag;
        this.message = message;
        this.throwable = throwable;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public LogLevel getLevel() {
        return level;
    }

    public String getTag() {
        return tag;
    }

    public String getMessage() {
        return message;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    @NonNull
    @Override
    public String toString() {
        return "LogFileEntry{" +
                "timestamp=" + timestamp +
                ", level=" + level +
                ", tag='" + tag + '\'' +
                ", message='" + message + '\'' +
                ", throwable=" + throwable +
                '}';
    }
}
