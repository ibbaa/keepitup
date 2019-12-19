package de.ibba.keepitup.test.mock;

import java.util.ArrayList;
import java.util.List;

import de.ibba.keepitup.logging.ILogger;
import de.ibba.keepitup.logging.LogFileEntry;
import de.ibba.keepitup.logging.LogLevel;

public class MockLogger implements ILogger {

    private final List<LogFileEntry> logEntries;

    public MockLogger() {
        logEntries = new ArrayList<>();
    }

    @Override
    public void log(String tag, String message, Throwable throwable, LogLevel level) {
        LogFileEntry logEntry = new LogFileEntry(1, "thread", level, tag, message, throwable);
        logEntries.add(logEntry);
    }

    public void reset() {
        logEntries.clear();
    }

    public boolean wasLogCalled() {
        return numberLogEntries() > 0;
    }

    public int numberLogEntries() {
        return logEntries.size();
    }

    public LogFileEntry getEntry(int index) {
        return logEntries.get(index);
    }
}
