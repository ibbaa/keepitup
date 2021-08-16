package net.ibbaa.keepitup.ui.adapter;

import androidx.annotation.NonNull;

import net.ibbaa.keepitup.model.LogEntry;
import net.ibbaa.keepitup.model.NetworkTask;

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
