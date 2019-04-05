package de.ibba.keepitup.ui.sync;

import java.util.List;

import de.ibba.keepitup.db.LogDAO;
import de.ibba.keepitup.ui.adapter.NetworkTaskAdapter;
import de.ibba.keepitup.ui.adapter.NetworkTaskUIWrapper;

public class UISyncHolder {

    private final List<NetworkTaskUIWrapper> networkTaskWrapperList;
    private final NetworkTaskAdapter adapter;
    private final LogDAO logDAO;

    public UISyncHolder(List<NetworkTaskUIWrapper> networkTaskWrapperList, NetworkTaskAdapter adapter, LogDAO logDAO) {
        this.networkTaskWrapperList = networkTaskWrapperList;
        this.adapter = adapter;
        this.logDAO = logDAO;
    }

    public List<NetworkTaskUIWrapper> getNetworkTaskWrapperList() {
        return networkTaskWrapperList;
    }

    public NetworkTaskAdapter getAdapter() {
        return adapter;
    }

    public LogDAO getLogDAO() {
        return logDAO;
    }
}
