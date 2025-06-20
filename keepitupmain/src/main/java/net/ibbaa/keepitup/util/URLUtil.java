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

package net.ibbaa.keepitup.util;

import com.google.common.net.InetAddresses;
import com.google.common.net.InternetDomainName;

import net.ibbaa.keepitup.logging.Log;

import java.io.UnsupportedEncodingException;
import java.net.IDN;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.regex.Pattern;

public class URLUtil {

    private static final Pattern UNENCODED_CHARS = Pattern.compile("[^\\p{ASCII}]|[ \"<>#{}|\\\\^`\\[\\]]");
    private static final Pattern VALID_PERCENT = Pattern.compile("%[0-9a-fA-F]{2}");

    public static boolean isValidIPAddress(String ipAddress) {
        if (StringUtil.isEmpty(ipAddress)) {
            return false;
        }
        return InetAddresses.isInetAddress(ipAddress.trim());
    }

    public static boolean isValidHostName(String hostName) {
        if (StringUtil.isEmpty(hostName)) {
            return false;
        }
        return InternetDomainName.isValid(hostName.trim());
    }

    public static String prefixHTTPProtocol(String inputUrl) {
        Log.d(URLUtil.class.getName(), "prefixHTTPProtocol, inputUrl is " + inputUrl);
        if (inputUrl == null || inputUrl.trim().isEmpty()) {
            return inputUrl;
        }
        String url = inputUrl.trim();
        int colonIndex = url.indexOf(':');
        if (colonIndex > 0) {
            String scheme = url.substring(0, colonIndex).toLowerCase();
            if (scheme.equals("http") || scheme.equals("https")) {
                return url;
            } else {
                String rest = url.substring(colonIndex + 1);
                if (rest.startsWith("//")) {
                    return "https:" + rest;
                } else {
                    return "https://" + rest;
                }
            }
        } else {
            return "https://" + url;
        }
    }

    public static boolean isValidURL(String inputUrl) {
        Log.d(URLUtil.class.getName(), "isValidURL, inputUrl is " + inputUrl);
        return getURL(inputUrl) != null;
    }

    public static boolean isEncoded(String string) {
        Log.d(URLUtil.class.getName(), "isEncoded, string is " + string);
        try {
            if (string == null) {
                return true;
            }
            if (UNENCODED_CHARS.matcher(string).find()) {
                return false;
            }
            for (int i = 0; i < string.length(); i++) {
                if (string.charAt(i) == '%') {
                    if (i + 2 >= string.length() || !VALID_PERCENT.matcher(string.substring(i, i + 3)).matches()) {
                        return false;
                    }
                }
            }
            return true;
        } catch (Exception exc) {
            Log.d(URLUtil.class.getName(), "Exception parsing url " + string, exc);
        }
        return false;
    }

    public static String getHostAndPort(URL url) {
        int port = url.getPort();
        if (port < 0) {
            return url.getHost();
        }
        return url.getHost() + ":" + port;
    }

    public static URL getURL(String inputUrl) {
        return getURL(null, inputUrl);
    }

    public static URL getURL(URL baseURL, String inputUrl) {
        Log.d(URLUtil.class.getName(), "getURL, baseURL is " + baseURL + ", inputUrl is " + inputUrl);
        try {
            URL url = baseURL == null ? new URL(inputUrl) : new URL(baseURL, inputUrl);
            String protocol = url.getProtocol();
            String userInfo = url.getUserInfo();
            String host = url.getHost();
            int port = url.getPort();
            String path = url.getPath();
            String query = url.getQuery();
            String ref = url.getRef();
            String asciiHost;
            if (!"http".equalsIgnoreCase(protocol) && !"https".equalsIgnoreCase(protocol)) {
                return null;
            }
            if (StringUtil.isEmpty(host)) {
                return null;
            }
            try {
                asciiHost = IDN.toASCII(host);
            } catch (Exception exc) {
                Log.e(URLUtil.class.getName(), "Exception using  toASCII on " + host, exc);
                asciiHost = host;
            }
            if (path != null && !isEncoded(path)) {
                path = encodePath(path);
            }
            if (query != null && !isEncoded(query)) {
                query = encodeQuery(query);
            }
            String finalURL = assembleURL(protocol, userInfo, asciiHost, port, path, query, ref);
            URI uri = new URI(finalURL);
            return uri.toURL();
        } catch (Exception exc) {
            Log.d(URLUtil.class.getName(), "Exception parsing url " + inputUrl, exc);
        }
        return null;
    }

    public static String assembleURL(String protocol, String userInfo, String asciiHost, int port, String path, String query, String ref) {
        Log.d(URLUtil.class.getName(), "assembleURL, protocol= " + protocol + ", userInfo= " + userInfo + ", asciiHost= " + asciiHost + ",port= " + port + ", query= " + query + ", ref= " + ref);
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(protocol).append("://");
        if (userInfo != null) {
            urlBuilder.append(userInfo).append("@");
        }
        urlBuilder.append(asciiHost);
        if (port != -1) {
            urlBuilder.append(":").append(port);
        }
        if (path != null) {
            urlBuilder.append(path);
        }
        if (query != null) {
            urlBuilder.append("?").append(query);
        }
        if (ref != null) {
            urlBuilder.append("#").append(ref);
        }
        return urlBuilder.toString();
    }

    public static String encodeQuery(String query) {
        Log.d(URLUtil.class.getName(), "encodeQuery, query is " + query);
        StringBuilder encoded = new StringBuilder();
        String[] pairs = query.split("&");
        boolean first = true;
        for (String pair : pairs) {
            if (!first) {
                encoded.append('&');
            }
            first = false;
            int index = pair.indexOf('=');
            if (index >= 0) {
                String key = pair.substring(0, index);
                String value = pair.substring(index + 1);
                encoded.append(encodeURIComponent(key));
                encoded.append('=');
                encoded.append(encodeURIComponent(value));
            } else {
                encoded.append(encodeURIComponent(pair));
            }
        }
        return encoded.toString();
    }

    public static String encodePath(String path) {
        Log.d(URLUtil.class.getName(), "encodePath, path is " + path);
        StringBuilder encoded = new StringBuilder();
        for (String segment : path.split("/")) {
            if (!segment.isEmpty()) {
                encoded.append('/');
                encoded.append(encodeURIComponent(segment));
            }
        }
        if (path.endsWith("/")) {
            encoded.append('/');
        }
        return encoded.length() == 0 ? "/" : encoded.toString();
    }

    @SuppressWarnings({"CharsetObjectCanBeUsed"})
    private static String encodeURIComponent(String component) {
        Log.d(URLUtil.class.getName(), "encodeURIComponent, component is " + component);
        try {
            return URLEncoder.encode(component, "UTF-8").replace("+", "%20").replace("%21", "!").replace("%27", "'").replace("%28", "(").replace("%29", ")").replace("%7E", "~");
        } catch (UnsupportedEncodingException exc) {
            Log.e(URLUtil.class.getName(), "Exception encoding " + component, exc);
            return component;
        }
    }
}
