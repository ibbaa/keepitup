/*
 * Copyright (c) 2026 Alwin Ibba
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

import net.ibbaa.keepitup.logging.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class LegacyTLSSSLSocketFactory extends SSLSocketFactory {

    private final SSLSocketFactory delegate;

    public LegacyTLSSSLSocketFactory(SSLSocketFactory delegate) {
        this.delegate = delegate;
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
    public Socket createSocket() throws IOException {
        return enableAllSupported(delegate.createSocket());
    }

    @Override
    public Socket createSocket(Socket underlyingSocket, String host, int port, boolean autoClose) throws IOException {
        return enableAllSupported(delegate.createSocket(underlyingSocket, host, port, autoClose));
    }

    @Override
    public Socket createSocket(String host, int port) throws IOException {
        return enableAllSupported(delegate.createSocket(host, port));
    }

    @Override
    public Socket createSocket(String host, int port, InetAddress localAddr, int localPort) throws IOException {
        return enableAllSupported(delegate.createSocket(host, port, localAddr, localPort));
    }

    @Override
    public Socket createSocket(InetAddress address, int port) throws IOException {
        return enableAllSupported(delegate.createSocket(address, port));
    }

    @Override
    public Socket createSocket(InetAddress address, int port, InetAddress localAddr, int localPort) throws IOException {
        return enableAllSupported(delegate.createSocket(address, port, localAddr, localPort));
    }

    private Socket enableAllSupported(Socket socket) {
        if (socket instanceof SSLSocket sslSocket) {
            Log.d(LegacyTLSSSLSocketFactory.class.getName(), "enableAllSupported");
            sslSocket.setEnabledProtocols(sslSocket.getSupportedProtocols());
            sslSocket.setEnabledCipherSuites(sslSocket.getSupportedCipherSuites());
        }
        return socket;
    }
}
