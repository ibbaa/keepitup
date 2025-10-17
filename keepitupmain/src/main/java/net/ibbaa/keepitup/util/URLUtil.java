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
import net.ibbaa.keepitup.model.Resolve;

import java.io.UnsupportedEncodingException;
import java.net.IDN;
import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Set;
import java.util.regex.Pattern;

public class URLUtil {

    private static final Pattern UNENCODED_CHARS = Pattern.compile("[^\\p{ASCII}]|[ \"<>#{}|\\\\^`\\[\\]\\t\\n\\r+]");
    private static final Pattern VALID_PERCENT = Pattern.compile("%[0-9a-fA-F]{2}");

    private static final Set<Character> PATH_RESERVED = Set.of('=', '?', '&', '#', '/', '+', ';', ',');
    private static final Set<Character> QUERY_KEY_RESERVED = Set.of('=', '&', '?', '#', '+', '/', ';', ',');
    private static final Set<Character> QUERY_VALUE_RESERVED = Set.of('&', '?', '#', '+', ';', ',');
    private static final Set<Character> FRAGMENT_RESERVED = Set.of(' ', '"', '#', '<', '>', '`', '\\', '^', '[', ']', '{', '}', '|', '+');
    private static final Set<Character> USER_INFO_RESERVED = Set.of(' ', '/', '@', '%', '"', '\\', '^', '`', '[', ']', '{', '}', '|', '#', '?');

    private static final String USER_INFO_ALLOWED = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-._~!$&'()*+,;=:";

    public enum URLComponent {
        PATH,
        QUERY_KEY,
        QUERY_VALUE,
        FRAGMENT,
        USER_INFO
    }

    public static boolean isValidIP6Address(String ipAddress) {
        if (StringUtil.isEmpty(ipAddress) || !ipAddress.contains(":")) {
            return false;
        }
        return InetAddresses.isInetAddress(ipAddress.trim());
    }

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

    public static boolean isSameHostAndPort(URL url1, URL url2) {
        if (url1 == null || url2 == null) {
            return false;
        }
        String host1 = normalizeHost(url1.getHost());
        String host2 = normalizeHost(url2.getHost());
        boolean sameHost;
        if (isValidIPAddress(host1) && isValidIPAddress(host2)) {
            try {
                InetAddress address1 = InetAddress.getByName(host1);
                InetAddress address2 = InetAddress.getByName(host2);
                sameHost = address1.equals(address2);
            } catch (Exception exc) {
                sameHost = false;
            }
        } else {
            sameHost = host1.equalsIgnoreCase(host2);
        }
        int port1 = getPort(url1);
        int port2 = getPort(url2);
        return sameHost && port1 == port2;
    }

    public static int getPort(URL url) {
        return url.getPort() != -1 ? url.getPort() : url.getDefaultPort();
    }

    public static String normalizeHost(String host) {
        if (host == null || host.length() < 2) {
            return host;
        }
        if (host.startsWith("[") && host.endsWith("]")) {
            return host.substring(1, host.length() - 1);
        }
        return host;
    }

    public static String getTargetAddress(Resolve resolve, URL url) {
        if (StringUtil.isEmpty(resolve.getTargetAddress())) {
            return url.getHost();
        }
        return resolve.getTargetAddress();
    }

    public static int getTargetPort(Resolve resolve, URL url) {
        if (resolve.getTargetPort() < 0) {
            return getPort(url);
        }
        return resolve.getTargetPort();
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

    public static boolean isEncoded(String input, URLComponent urlComponent) {
        Log.d(URLUtil.class.getName(), "isEncoded, input is " + input + ", urlComponent is " + urlComponent);
        try {
            if (StringUtil.isEmpty(input)) {
                return true;
            }
            if (UNENCODED_CHARS.matcher(input).find()) {
                return false;
            }
            Set<Character> forbiddenChars = switch (urlComponent) {
                case PATH -> PATH_RESERVED;
                case QUERY_KEY -> QUERY_KEY_RESERVED;
                case QUERY_VALUE -> QUERY_VALUE_RESERVED;
                case FRAGMENT -> FRAGMENT_RESERVED;
                case USER_INFO -> USER_INFO_RESERVED;
            };
            for (int ii = 0; ii < input.length(); ii++) {
                char cc = input.charAt(ii);
                if (cc == '%') {
                    if (ii + 2 >= input.length() || !VALID_PERCENT.matcher(input.substring(ii, ii + 3)).matches()) {
                        return false;
                    }
                    ii += 2;
                } else {
                    if (forbiddenChars.contains(cc)) {
                        return false;
                    }
                }
            }
            for (int ii = 0; ii < input.length(); ii++) {
                if (input.charAt(ii) == '%') {
                    if (ii + 2 >= input.length() || !VALID_PERCENT.matcher(input.substring(ii, ii + 3)).matches()) {
                        return false;
                    }
                }
            }
            return true;
        } catch (Exception exc) {
            Log.d(URLUtil.class.getName(), "Exception processing url component " + input, exc);
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

    public static boolean isHTTP(URL url) {
        if (url == null) {
            return false;
        }
        return "http".equalsIgnoreCase(url.getProtocol());
    }

    public static boolean isHTTPS(URL url) {
        if (url == null) {
            return false;
        }
        return "https".equalsIgnoreCase(url.getProtocol());
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
            if (!isHTTP(url) && !isHTTPS(url)) {
                return null;
            }
            if (StringUtil.isEmpty(host)) {
                return null;
            }
            if (userInfo != null) {
                userInfo = getEncodedUserInfo(userInfo);
            }
            try {
                asciiHost = IDN.toASCII(host);
            } catch (Exception exc) {
                Log.e(URLUtil.class.getName(), "Exception using toASCII on " + host, exc);
                asciiHost = host;
            }
            if (path != null) {
                path = getEncodedPath(path);
            }
            if (query != null) {
                query = getEncodedQuery(query);
            }
            if (ref != null) {
                ref = getEncodedRef(ref);
            }
            String finalURL = assembleURL(protocol, userInfo, asciiHost, port, path, query, ref);
            URI uri = new URI(finalURL);
            return uri.toURL();
        } catch (Exception exc) {
            Log.d(URLUtil.class.getName(), "Exception parsing url " + inputUrl, exc);
        }
        return null;
    }

    public static String getEncodedQuery(String query) {
        Log.d(URLUtil.class.getName(), "getEncodedQuery, query is " + query);
        if (StringUtil.isEmpty(query)) {
            return "";
        }
        StringBuilder encodedQuery = new StringBuilder();
        String[] pairs = query.split("&");
        for (int ii = 0; ii < pairs.length; ii++) {
            if (ii > 0) {
                encodedQuery.append("&");
            }
            String[] keyValue = pairs[ii].split("=", 2);
            String key = keyValue.length > 0 ? keyValue[0] : "";
            String value = keyValue.length > 1 ? keyValue[1] : "";
            encodedQuery.append(isEncoded(key, URLComponent.QUERY_KEY) ? key : encodeURIComponent(key));
            encodedQuery.append("=");
            encodedQuery.append(isEncoded(value, URLComponent.QUERY_VALUE) ? value : encodeURIComponent(value));
        }
        return encodedQuery.toString();
    }

    public static String getEncodedPath(String path) {
        Log.d(URLUtil.class.getName(), "getEncodedPath, path is " + path);
        if (StringUtil.isEmpty(path)) {
            return "";
        }
        StringBuilder encodedPath = new StringBuilder();
        String[] segments = path.split("/", -1);
        for (int ii = 0; ii < segments.length; ii++) {
            if (ii > 0) {
                encodedPath.append("/");
            }
            String segment = segments[ii];
            encodedPath.append(isEncoded(segment, URLComponent.PATH) ? segment : encodeURIComponent(segment));
        }
        return encodedPath.toString();
    }

    public static String getEncodedUserInfo(String userInfo) {
        Log.d(URLUtil.class.getName(), "getEncodedUserInfo, userInfo is " + userInfo);
        if (!isEncoded(userInfo, URLComponent.USER_INFO)) {
            return encodeUserInfo(userInfo);
        }
        return userInfo;
    }

    public static String getEncodedRef(String ref) {
        Log.d(URLUtil.class.getName(), "getEncodedRef, ref is " + ref);
        if (!isEncoded(ref, URLComponent.FRAGMENT)) {
            return encodeURIComponent(ref);
        }
        return ref;
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

    @SuppressWarnings({"CharsetObjectCanBeUsed"})
    private static String encodeUserInfo(String input) {
        Log.d(URLUtil.class.getName(), "encodeUserInfo, input is " + input);
        StringBuilder userInfoBuilder = new StringBuilder();
        for (char cc : input.toCharArray()) {
            if (USER_INFO_ALLOWED.indexOf(cc) != -1) {
                userInfoBuilder.append(cc);
            } else {
                try {
                    userInfoBuilder.append(URLEncoder.encode(String.valueOf(cc), "UTF-8"));
                } catch (UnsupportedEncodingException exc) {
                    Log.e(URLUtil.class.getName(), "Exception encoding " + input, exc);
                    userInfoBuilder.append(cc);
                }
            }
        }
        return userInfoBuilder.toString().replace("+", "%20").replace("%21", "!").replace("%27", "'").replace("%28", "(").replace("%29", ")").replace("%7E", "~");
    }
}
