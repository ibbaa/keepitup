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

import java.net.IDN;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.Callable;

public class DNSLookup implements Callable<DNSLookupResult> {

    private final String host;

    public DNSLookup(String host) {
        this.host = host;
    }

    @Override
    public DNSLookupResult call() {
        Log.d(DNSLookup.class.getName(), "call");
        try {
            String asciiHost;
            try {
                asciiHost = IDN.toASCII(host);
            } catch (Exception exc) {
                Log.e(DNSLookup.class.getName(), "Exception using toASCII on " + host, exc);
                asciiHost = host;
            }
            InetAddress[] addresses = InetAddress.getAllByName(IDN.toASCII(asciiHost));
            return new DNSLookupResult(Arrays.asList(addresses), null);
        } catch (Exception exc) {
            Log.e(DNSLookup.class.getName(), "Error executing DNS lookup", exc);
            return new DNSLookupResult(Collections.emptyList(), exc);
        }
    }
}
