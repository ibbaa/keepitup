/*
 * Copyright (c) 2025 Alwin Ibba
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.ibbaa.keepitup.test.mock;

import net.ibbaa.keepitup.service.INetworkManager;

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
