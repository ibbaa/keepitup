package de.ibba.keepitup.test.mock;

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

    protected MockURLConnection(URL url) {
        super(url);
        reset();
    }

    private void reset() {
        headers = new HashMap<>();
        connected = false;
        inputStream = null;
        contentType = null;
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
        }
        values.add(value);
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public void connect() throws IOException {
        connected = true;
    }

    public boolean isConnected() {
        return connected;
    }
}
