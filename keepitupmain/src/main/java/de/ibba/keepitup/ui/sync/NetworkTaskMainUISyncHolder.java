package de.ibba.keepitup.ui.sync;

import de.ibba.keepitup.db.LogDAO;
import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.ui.adapter.NetworkTaskAdapter;

public class NetworkTaskMainUISyncHolder {

    private final NetworkTask networkTask;
    private final NetworkTaskAdapter adapter;
    private final LogDAO logDAO;

    public NetworkTaskMainUISyncHolder(NetworkTask networkTask, NetworkTaskAdapter adapter, LogDAO logDAO) {
        this.networkTask = networkTask;
        this.adapter = adapter;
        this.logDAO = logDAO;
    }

    public NetworkTask getNetworkTask() {
        return networkTask;
    }

    public NetworkTaskAdapter getAdapter() {
        return adapter;
    }

    public LogDAO getLogDAO() {
        return logDAO;
    }
}
