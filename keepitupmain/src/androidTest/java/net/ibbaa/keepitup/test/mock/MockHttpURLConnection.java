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

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"unused"})
public class MockHttpURLConnection extends HttpURLConnection {

    private MockURLConnection mockURLConnection;
    private boolean disconnected;
    private int respondeCode;
    private String responseMessage;

    public MockHttpURLConnection(URL url) {
        super(url);
        reset();
    }

    private void reset() {
        mockURLConnection = new MockURLConnection(getURL());
        disconnected = false;
        respondeCode = -1;
        responseMessage = null;
    }

    @Override
    public String getContentType() {
        return mockURLConnection.getContentType();
    }

    public void setContentType(String contentType) {
        mockURLConnection.setContentType(contentType);
    }

    @Override
    public String getHeaderField(String name) {
        return mockURLConnection.getHeaderField(name);
    }

    @Override
    public Map<String, List<String>> getHeaderFields() {
        return mockURLConnection.getHeaderFields();
    }

    public void addHeader(String name, String value) {
        mockURLConnection.addHeader(name, value);
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return mockURLConnection.getInputStream();
    }

    public void setInputStream(InputStream inputStream) {
        mockURLConnection.setInputStream(inputStream);
    }

    public void setExceptionOnInputStream(IOException exceptionOnInputStream) {
        mockURLConnection.setExceptionOnInputStream(exceptionOnInputStream);
    }

    @Override
    public void connect() throws IOException {
        mockURLConnection.connect();
    }

    public boolean isConnected() {
        return mockURLConnection.isConnected();
    }

    @Override
    public int getResponseCode() {
        return respondeCode;
    }

    public void setRespondeCode(int respondeCode) {
        this.respondeCode = respondeCode;
    }

    @Override
    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    @Override
    public void disconnect() {
        disconnected = true;
    }

    public boolean isDisconnected() {
        return disconnected;
    }

    @Override
    public boolean usingProxy() {
        return false;
    }
}
