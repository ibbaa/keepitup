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

import java.net.IDN;
import java.net.URI;
import java.net.URL;

public class URLUtil {

    public static boolean isValidIPAddress(String ipAddress) {
        return InetAddresses.isInetAddress(ipAddress);
    }

    public static boolean isValidHostName(String hostName) {
        return InternetDomainName.isValid(hostName);
    }

    public static boolean isValidURL(String inputUrl) {
        Log.d(URLUtil.class.getName(), "isValidURL, inputUrl is " + inputUrl);
        try {
            URL url = new URL(inputUrl);
            new URI(url.getProtocol(), url.getUserInfo(), IDN.toASCII(url.getHost()), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
            return !StringUtil.isEmpty(IDN.toASCII(url.getHost()));
        } catch (Exception exc) {
            Log.d(URLUtil.class.getName(), "Exception parsing url " + inputUrl, exc);
        }
        return false;
    }

    public static String prefixHTTPProtocol(String inputUrl) {
        Log.d(URLUtil.class.getName(), "prefixHTTPProtocol, inputUrl is " + inputUrl);
        if (inputUrl.toLowerCase().startsWith("http://") || inputUrl.toLowerCase().startsWith("https://")) {
            return inputUrl;
        }
        return "http://" + inputUrl;
    }

    public static String encodeURL(String inputUrl) {
        Log.d(URLUtil.class.getName(), "encodeURL, inputUrl is " + inputUrl);
        try {
            URL url = new URL(inputUrl);
            URI uri = new URI(url.getProtocol(), url.getUserInfo(), IDN.toASCII(url.getHost()), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
            return uri.toASCIIString();
        } catch (Exception exc) {
            Log.d(URLUtil.class.getName(), "Exception parsing url " + inputUrl, exc);
        }
        return inputUrl;
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
            URI uri = new URI(url.getProtocol(), url.getUserInfo(), IDN.toASCII(url.getHost()), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
            return uri.toURL();
        } catch (Exception exc) {
            Log.d(URLUtil.class.getName(), "Exception parsing url " + inputUrl, exc);
        }
        return null;
    }
}
