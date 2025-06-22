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

import net.ibbaa.keepitup.logging.Log;

import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

@SuppressWarnings("CustomX509TrustManager")
public class TrustAllX509TrustManager implements X509TrustManager {

    public X509Certificate[] getAcceptedIssuers() {
        Log.d(TrustAllX509TrustManager.class.getName(), "getAcceptedIssuers");
        return new X509Certificate[0];
    }

    public void checkClientTrusted(X509Certificate[] certs, String authType) {
        Log.d(TrustAllX509TrustManager.class.getName(), "checkClientTrusted");
    }

    public void checkServerTrusted(X509Certificate[] certs, String authType) {
        Log.d(TrustAllX509TrustManager.class.getName(), "checkServerTrusted");
    }
}
