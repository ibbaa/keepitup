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

import javax.net.ssl.SSLSocketFactory;

@SuppressWarnings({"resource"})
public class ConnectToSSLSocketFactory extends SSLSocketFactory {

    private final SSLSocketFactory delegate;
    private final String overrideHost;
    private final int overridePort;

    public ConnectToSSLSocketFactory(SSLSocketFactory delegate, String overrideHost, int overridePort) {
        this.delegate = delegate;
        this.overrideHost = overrideHost;
        this.overridePort = overridePort;
    }

    @Override
    public String[] getDefaultCipherSuites() {
        return delegate.getDefaultCipherSuites();
    }

    @Override
    public String[] getSupportedCipherSuites() {
        return delegate.getSupportedCipherSuites();
    }

    @Override
    public Socket createSocket() {
        return new ConnectToRedirectSocket(overrideHost, overridePort);
    }

    @Override
    public Socket createSocket(Socket underlyingSocket, String host, int port, boolean autoClose) throws IOException {
        if (underlyingSocket == null) {
            underlyingSocket = new ConnectToRedirectSocket(overrideHost, overridePort);
            underlyingSocket.connect(new InetSocketAddress(overrideHost, overridePort));
        } else {
            if (!underlyingSocket.isConnected()) {
                underlyingSocket.connect(new InetSocketAddress(overrideHost, overridePort));
            }
        }
        return delegate.createSocket(underlyingSocket, host, port, autoClose);
    }

    @Override
    public Socket createSocket(String host, int port) throws IOException {
        Socket redirectSocket = new ConnectToRedirectSocket(overrideHost, overridePort);
        redirectSocket.connect(new InetSocketAddress(overrideHost, overridePort));
        return delegate.createSocket(redirectSocket, host, port, true);
    }

    @Override
    public Socket createSocket(String host, int port, java.net.InetAddress localAddr, int localPort) throws IOException {
        Socket redirectSocket = new ConnectToRedirectSocket(overrideHost, overridePort);
        redirectSocket.bind(new InetSocketAddress(localAddr, localPort));
        redirectSocket.connect(new InetSocketAddress(overrideHost, overridePort));
        return delegate.createSocket(redirectSocket, host, port, true);
    }

    @Override
    public Socket createSocket(InetAddress address, int port) throws IOException {
        return createSocket(address.getHostName(), port);
    }

    @Override
    public Socket createSocket(java.net.InetAddress address, int port, java.net.InetAddress localAddr, int localPort) throws IOException {
        return createSocket(address.getHostName(), port, localAddr, localPort);
    }
}
