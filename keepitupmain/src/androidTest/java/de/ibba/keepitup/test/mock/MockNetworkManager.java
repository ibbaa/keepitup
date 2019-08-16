package de.ibba.keepitup.test.mock;

import de.ibba.keepitup.service.INetworkManager;

public class MockNetworkManager implements INetworkManager {

    private boolean connected;
    private boolean connectedWithWiFi;

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public void setConnectedWithWiFi(boolean connectedWithWiFi) {
        this.connectedWithWiFi = connectedWithWiFi;
    }

    @Override
    public boolean isConnected() {
        return connected;
    }

    @Override
    public boolean isConnectedWithWiFi() {
        return connectedWithWiFi;
    }
}
