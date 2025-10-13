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
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import javax.net.SocketFactory;

public class ConnectToSocketFactory extends SocketFactory {

    private final String overrideHost;
    private final int overridePort;

    public ConnectToSocketFactory(String overrideHost, int overridePort) {
        this.overrideHost = overrideHost;
        this.overridePort = overridePort;
    }

    @Override
    public Socket createSocket() {
        return new ConnectToRedirectSocket(overrideHost, overridePort);
    }

    @Override
    public Socket createSocket(String host, int port) throws IOException {
        return connectRedirectSocket();
    }

    @Override
    public Socket createSocket(InetAddress address, int port) throws IOException {
        return connectRedirectSocket();
    }

    @Override
    public Socket createSocket(String host, int port, InetAddress localAddress, int localPort) throws IOException {
        return bindAndConnectRedirectSocket(localAddress, localPort);
    }

    @Override
    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
        return bindAndConnectRedirectSocket(localAddress, localPort);
    }

    private ConnectToRedirectSocket connectRedirectSocket() throws IOException {
        ConnectToRedirectSocket redirectSocket = new ConnectToRedirectSocket(overrideHost, overridePort);
        redirectSocket.connect(new InetSocketAddress(overrideHost, overridePort));
        return redirectSocket;
    }

    private ConnectToRedirectSocket bindAndConnectRedirectSocket(InetAddress localAddress, int localPort) throws IOException {
        ConnectToRedirectSocket redirectSocket = new ConnectToRedirectSocket(overrideHost, overridePort);
        redirectSocket.bind(new InetSocketAddress(localAddress, localPort));
        redirectSocket.connect(new InetSocketAddress(overrideHost, overridePort));
        return redirectSocket;
    }
}

