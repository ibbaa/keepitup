/*
 * Copyright (c) 2023. Alwin Ibba
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
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MockURLConnection extends URLConnection {

    private boolean connected;
    private Map<String, List<String>> headers;
    private InputStream inputStream;
    private String contentType;
    private IOException exceptionOnInputStream;

    public MockURLConnection(URL url) {
        super(url);
        reset();
    }

    private void reset() {
        headers = new HashMap<>();
        connected = false;
        inputStream = null;
        contentType = null;
        exceptionOnInputStream = null;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public String getHeaderField(String name) {
        List<String> values = headers.get(name);
        if (values == null || values.isEmpty()) {
            return null;
        }
        return values.get(values.size() - 1);
    }

    @Override
    public Map<String, List<String>> getHeaderFields() {
        return Collections.unmodifiableMap(headers);
    }

    public void addHeader(String name, String value) {
        List<String> values = headers.get(name);
        if (values == null) {
            values = new ArrayList<>();
            headers.put(name, values);
        }
        values.add(value);
    }

    @Override
    public InputStream getInputStream() throws IOException {
        if (exceptionOnInputStream != null) {
            throw exceptionOnInputStream;
        }
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public void setExceptionOnInputStream(IOException exceptionOnInputStream) {
        this.exceptionOnInputStream = exceptionOnInputStream;
    }

    @Override
    public void connect() {
        connected = true;
    }

    public boolean isConnected() {
        return connected;
    }
}
