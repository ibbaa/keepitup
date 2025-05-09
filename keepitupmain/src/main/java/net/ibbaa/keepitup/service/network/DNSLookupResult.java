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

import androidx.annotation.NonNull;

import java.net.InetAddress;
import java.util.Collections;
import java.util.List;

public class DNSLookupResult {

    private final List<InetAddress> addresses;
    private final Throwable exception;

    public DNSLookupResult(InetAddress address, Throwable exception) {
        this.addresses = Collections.singletonList(address);
        this.exception = exception;
    }

    public DNSLookupResult(List<InetAddress> addresses, Throwable exception) {
        this.addresses = addresses;
        this.exception = exception;
    }

    public List<InetAddress> getAddresses() {
        return Collections.unmodifiableList(addresses);
    }

    public Throwable getException() {
        return exception;
    }

    @NonNull
    @Override
    public String toString() {
        return "DNSLookupResult{" +
                "addresses=" + addresses +
                ", exception=" + exception +
                '}';
    }
}
