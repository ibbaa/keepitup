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

package net.ibbaa.keepitup.service.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class ConnectToRedirectSocket extends Socket {

    private final String overrideHost;
    private final int overridePort;

    public ConnectToRedirectSocket(String overrideHost, int overridePort) {
        super();
        this.overrideHost = overrideHost;
        this.overridePort = overridePort;
    }

    private InetSocketAddress overrideSocketAddress() {
        return new InetSocketAddress(overrideHost, overridePort);
    }

    @Override
    public void connect(SocketAddress endpoint, int timeout) throws IOException {
        super.connect(overrideSocketAddress(), timeout);
    }

    @Override
    public void connect(SocketAddress endpoint) throws IOException {
        super.connect(overrideSocketAddress());
    }
}
