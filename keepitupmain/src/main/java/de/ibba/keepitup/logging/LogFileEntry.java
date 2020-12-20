package de.ibba.keepitup.logging;

import androidx.annotation.NonNull;

public class LogFileEntry {

    private final long timestamp;
    private final String thread;
    private final LogLevel level;
    private final String tag;
    private final String message;
    private final Throwable throwable;

    public LogFileEntry(long timestamp, String thread, LogLevel level, String tag, String message, Throwable throwable) {
        this.timestamp = timestamp;
        this.thread = thread;
        this.level = level;
        this.tag = tag;
        this.message = message;
        this.throwable = throwable;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getThread() {
        return thread;
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
                ", thread='" + thread + '\'' +
                ", level=" + level +
                ", tag='" + tag + '\'' +
                ", message='" + message + '\'' +
                ", throwable=" + throwable +
                '}';
    }
}
