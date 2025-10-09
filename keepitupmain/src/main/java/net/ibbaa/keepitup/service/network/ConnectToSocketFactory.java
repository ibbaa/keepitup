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

import net.ibbaa.keepitup.util.StringUtil;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import javax.net.SocketFactory;

public class ConnectToSocketFactory extends SocketFactory {

    private final String overrideHost;
    private final int overridePort;
    private final SocketFactory delegate;

    public ConnectToSocketFactory(String overrideHost, int overridePort) {
        this.overrideHost = overrideHost;
        this.overridePort = overridePort;
        this.delegate = SocketFactory.getDefault();
    }

    @Override
    public Socket createSocket() throws IOException {
        return delegate.createSocket();
    }

    @Override
    public Socket createSocket(String host, int port) throws IOException {
        return delegate.createSocket(getHost(host), getPort(port));
    }

    @Override
    public Socket createSocket(InetAddress address, int port) throws IOException {
        return delegate.createSocket(getInetAddress(address), getPort(port));
    }

    @Override
    public Socket createSocket(String host, int port, InetAddress localAddress, int localPort) throws IOException {
        return delegate.createSocket(getHost(host), getPort(port), localAddress, localPort);
    }

    @Override
    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
        return delegate.createSocket(getInetAddress(address), getPort(port), localAddress, localPort);
    }

    private String getHost(String host) {
        return StringUtil.isEmpty(overrideHost) ? host : overrideHost;
    }

    private InetAddress getInetAddress(InetAddress address) throws IOException {
        return StringUtil.isEmpty(overrideHost) ? address : InetAddress.getByName(overrideHost);
    }

    private int getPort(int port) {
        return overridePort < 0 ? port : overridePort;
    }
}

