/*
 * Copyright (c) 2024. Alwin Ibba
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

import net.ibbaa.keepitup.service.network.DNSLookup;
import net.ibbaa.keepitup.service.network.DNSLookupResult;

public class MockDNSLookup extends DNSLookup {

    private final DNSLookupResult dnsLookupResult;

    public MockDNSLookup(String host, DNSLookupResult dnsLookupResult) {
        super(host);
        this.dnsLookupResult = dnsLookupResult;
    }

    @Override
    public DNSLookupResult call() {
        return dnsLookupResult;
    }
}
