package de.ibba.keepitup.ui.adapter;

import android.support.annotation.NonNull;

import de.ibba.keepitup.model.LogEntry;
import de.ibba.keepitup.model.NetworkTask;

public class NetworkTaskUIWrapper {

    private final NetworkTask networkTask;
    private final LogEntry logEntry;

    public NetworkTaskUIWrapper(NetworkTask networkTask, LogEntry logEntry) {
        this.networkTask = networkTask;
        this.logEntry = logEntry;
    }

    public long getId() {
        return networkTask.getId();
    }

    public NetworkTask getNetworkTask() {
        return networkTask;
    }

    public LogEntry getLogEntry() {
        return logEntry;
    }

    @NonNull
    @Override
    public String toString() {
        return "NetworkTaskUIWrapper{" +
                "networkTask=" + networkTask +
                ", logEntry=" + logEntry +
                '}';
    }
}
