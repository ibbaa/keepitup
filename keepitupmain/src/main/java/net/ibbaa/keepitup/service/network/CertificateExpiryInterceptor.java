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

import net.ibbaa.keepitup.service.ITimeService;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.Handshake;
import okhttp3.Interceptor;
import okhttp3.Response;

public class CertificateExpiryInterceptor implements Interceptor {

    private final int warningThresholdDays;
    private final ITimeService timeService;
    private List<CertificateExpiryInfo> expiryInfo;

    public CertificateExpiryInterceptor(int warningThresholdDays, ITimeService timeService) {
        this.warningThresholdDays = warningThresholdDays;
        this.timeService = timeService;
        this.expiryInfo = Collections.emptyList();
    }

    @Override
    public @NotNull Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());
        Handshake handshake = response.handshake();
        List<CertificateExpiryInfo> foundExpiryInfo = new ArrayList<>();
        if (handshake != null) {
            long nowMillis = timeService.getCurrentTimestamp();
            for (Certificate cert : handshake.peerCertificates()) {
                if (cert instanceof X509Certificate x509) {
                    long notAfterMillis = x509.getNotAfter().getTime();
                    long daysUntilExpiry = (notAfterMillis - nowMillis) / (24L * 60 * 60 * 1000);
                    if (daysUntilExpiry <= warningThresholdDays) {
                        foundExpiryInfo.add(new CertificateExpiryInfo(x509.getSubjectX500Principal().getName(), x509.getNotAfter().getTime()));
                    }
                }
            }
        }
        this.expiryInfo = foundExpiryInfo;
        return response;
    }

    public List<CertificateExpiryInfo> getExpiryInfo() {
        return expiryInfo;
    }
}
