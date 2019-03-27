package de.ibba.keepitup.ui;

import de.ibba.keepitup.model.LogEntry;
import de.ibba.keepitup.model.NetworkTask;

public class NetworkTaskUIWrapper {

    private final NetworkTask networkTask;
    private final LogEntry logEntry;

    public NetworkTaskUIWrapper(NetworkTask networkTask, LogEntry logEntry) {
        this.networkTask = networkTask;
        this.logEntry = logEntry;
    }

    public NetworkTask getNetworkTask() {
        return networkTask;
    }

    public LogEntry getLogEntry() {
        return logEntry;
    }
}
