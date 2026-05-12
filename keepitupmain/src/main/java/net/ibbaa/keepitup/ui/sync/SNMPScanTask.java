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

package net.ibbaa.keepitup.ui.sync;

import android.content.Context;
import android.content.res.Resources;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.model.SNMPVersion;
import net.ibbaa.keepitup.service.network.DNSLookupResult;
import net.ibbaa.keepitup.ui.validation.AccessTypeDataValidator;
import net.ibbaa.keepitup.ui.validation.NetworkTaskValidator;
import net.ibbaa.keepitup.ui.validation.StandardAccessTypeDataValidator;
import net.ibbaa.keepitup.ui.validation.StandardHostPortValidator;
import net.ibbaa.keepitup.ui.validation.ValidationResult;
import net.ibbaa.keepitup.util.ThreadUtil;
import net.ibbaa.keepitup.util.URLUtil;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SNMPScanTask extends UIDispatchBackgroundTask<SNMPScanResult> {

    private final Context context;
    private final String address;
    private final int port;
    private final SNMPVersion snmpVersion;
    private final String community;
    private InetAddress inetAddress;

    public SNMPScanTask(UITaskResultDispatcher<SNMPScanResult> dispatcher, Context context, String address, int port, SNMPVersion snmpVersion, String community) {
        super(dispatcher);
        this.context = context;
        this.address = address;
        this.port = port;
        this.snmpVersion = snmpVersion == null ? SNMPVersion.V2C : snmpVersion;
        this.community = community;
    }

    @Override
    protected SNMPScanResult runInBackground() {
        Log.d(SNMPScanTask.class.getName(), "runInBackground");
        List<String> errors = validate();
        if (!errors.isEmpty()) {
            return new SNMPScanResult(false, Collections.emptyList(), Collections.emptyMap(), errors, null);
        }
        Future<DNSLookupResult> futureDNS = ThreadUtil.execute(this::dnsLookup);
        String errorMessage = getResources().getString(R.string.text_dns_lookup_error, address) + " " + getResources().getString(R.string.text_dns_lookup_no_address);
        try {
            Log.d(SNMPScanTask.class.getName(), "Performing DNS lookup");
            int timeout = getResources().getInteger(R.integer.dns_timeout);
            DNSLookupResult dnsResult = futureDNS.get(timeout, TimeUnit.MILLISECONDS);
            List<InetAddress> addresses = dnsResult.getAddresses();
            if (addresses == null || addresses.isEmpty()) {
                Log.d(SNMPScanTask.class.getName(), "DNS lookup returned no addresses");
                return new SNMPScanResult(false, Collections.emptyList(), Collections.emptyMap(), List.of(errorMessage), null);
            } else {
                Log.d(SNMPScanTask.class.getName(), "DNS lookup returned the following addresses " + addresses);
                inetAddress = URLUtil.findAddress(addresses, getResources().getBoolean(R.bool.network_prefer_ipv4));
            }
        } catch (Exception exc) {
            futureDNS.cancel(true);
            return new SNMPScanResult(false, Collections.emptyList(), Collections.emptyMap(), List.of(errorMessage), exc);
        }
        Future<SNMPScanResult> futureSNMP = ThreadUtil.execute(this::walk);
        try {
            Log.d(SNMPScanTask.class.getName(), "Performing SNMP walk");
            int timeout = getResources().getInteger(R.integer.snmp_scan_timeout);
            return futureSNMP.get(timeout, TimeUnit.MILLISECONDS);
        } catch (TimeoutException exc) {
            futureSNMP.cancel(true);
            return new SNMPScanResult(false, Collections.emptyList(), Collections.emptyMap(), List.of(getContext().getString(R.string.text_snmp_no_response)), exc);
        } catch (Exception exc) {
            futureSNMP.cancel(true);
            return new SNMPScanResult(false, Collections.emptyList(), Collections.emptyMap(), Collections.emptyList(), exc);
        }
    }

    private List<String> validate() {
        Log.d(SNMPScanTask.class.getName(), "validate");
        NetworkTaskValidator networkTaskValidator = new StandardHostPortValidator(getContext());
        AccessTypeDataValidator accessTypeDataValidator = new StandardAccessTypeDataValidator(getContext());
        List<String> errors = new ArrayList<>();
        ValidationResult addressResult = networkTaskValidator.validateAddress(address);
        ValidationResult portResult = networkTaskValidator.validatePort(String.valueOf(port));
        ValidationResult communityResult = accessTypeDataValidator.validateSNMPCommunity(community);
        if (!addressResult.isValidationSuccessful()) {
            errors.add(addressResult.getFieldName() + " " + getResources().getString(R.string.string_invalid));
        }
        if (!portResult.isValidationSuccessful()) {
            errors.add(portResult.getFieldName() + " " + getResources().getString(R.string.string_invalid));
        }
        if (!communityResult.isValidationSuccessful()) {
            errors.add(communityResult.getFieldName() + " " + getResources().getString(R.string.string_invalid));
        }
        return errors;
    }

    private DNSLookupResult dnsLookup() {
        Log.d(SNMPScanTask.class.getName(), "dnsLookup");
        return new DNSLookupResult(Collections.emptyList(), "", null);
    }

    private SNMPScanResult walk() {
        Log.d(SNMPScanTask.class.getName(), "walk");

        return new SNMPScanResult(true, Collections.emptyList(), Collections.emptyMap(), Collections.emptyList(), null);
    }

    protected Resources getResources() {
        return getContext().getResources();
    }

    protected Context getContext() {
        return context;
    }
}
