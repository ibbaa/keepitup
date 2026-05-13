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
import net.ibbaa.keepitup.model.SNMPInterfaceInfo;
import net.ibbaa.keepitup.model.SNMPItem;
import net.ibbaa.keepitup.model.SNMPVersion;
import net.ibbaa.keepitup.service.network.DNSLookup;
import net.ibbaa.keepitup.service.network.DNSLookupResult;
import net.ibbaa.keepitup.service.network.SNMPAccess;
import net.ibbaa.keepitup.service.network.SNMPMapping;
import net.ibbaa.keepitup.ui.validation.AccessTypeDataValidator;
import net.ibbaa.keepitup.ui.validation.NetworkTaskValidator;
import net.ibbaa.keepitup.ui.validation.StandardAccessTypeDataValidator;
import net.ibbaa.keepitup.ui.validation.StandardHostPortValidator;
import net.ibbaa.keepitup.ui.validation.ValidationResult;
import net.ibbaa.keepitup.util.StringUtil;
import net.ibbaa.keepitup.util.ThreadUtil;
import net.ibbaa.keepitup.util.URLUtil;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SNMPScanTask extends UIDispatchBackgroundTask<SNMPScanResult> {

    private final Context context;
    private final long networktaskId;
    private final String address;
    private final int port;
    private final SNMPVersion snmpVersion;
    private final String community;
    private InetAddress inetAddress;

    public SNMPScanTask(UITaskResultDispatcher<SNMPScanResult> dispatcher, Context context, long networktaskId, String address, int port, SNMPVersion snmpVersion, String community) {
        super(dispatcher);
        this.context = context;
        this.networktaskId = networktaskId;
        this.address = address;
        this.port = port;
        this.snmpVersion = snmpVersion == null ? SNMPVersion.V2C : snmpVersion;
        this.community = StringUtil.notNull(community);
    }

    @Override
    protected SNMPScanResult runInBackground() {
        Log.d(SNMPScanTask.class.getName(), "runInBackground");
        List<String> errors = validate();
        if (!errors.isEmpty()) {
            return new SNMPScanResult(false, Collections.emptyList(), Collections.emptyMap(), errors, null);
        }
        Callable<DNSLookupResult> dnsLookup = getDNSLookup(address);
        Future<DNSLookupResult> futureDNS = ThreadUtil.execute(dnsLookup);
        try {
            Log.d(SNMPScanTask.class.getName(), "Performing DNS lookup");
            int timeout = getResources().getInteger(R.integer.dns_timeout);
            DNSLookupResult dnsResult = futureDNS.get(timeout, TimeUnit.MILLISECONDS);
            List<InetAddress> addresses = dnsResult.getAddresses();
            if (addresses == null || addresses.isEmpty()) {
                Log.d(SNMPScanTask.class.getName(), "DNS lookup returned no addresses");
                String errorMessage = getResources().getString(R.string.text_dns_lookup_error, address) + " " + getResources().getString(R.string.text_dns_lookup_no_address);
                return new SNMPScanResult(false, Collections.emptyList(), Collections.emptyMap(), List.of(errorMessage), null);
            } else {
                Log.d(SNMPScanTask.class.getName(), "DNS lookup returned the following addresses " + addresses);
                inetAddress = URLUtil.findAddress(addresses, getResources().getBoolean(R.bool.network_prefer_ipv4));
            }
        } catch (Exception exc) {
            futureDNS.cancel(true);
            String errorMessage = getResources().getString(R.string.text_dns_lookup_error, address);
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

    private SNMPScanResult walk() {
        Log.d(SNMPScanTask.class.getName(), "walk");
        SNMPMapping snmpMapping = new SNMPMapping(getContext());
        SNMPAccess snmpAccess = getSNMPAccess();
        SNMPAccess.WalkResult ifDescrResult = snmpAccess.walkInterfacesDescr();
        if (!ifDescrResult.success()) {
            return new SNMPScanResult(false, Collections.emptyList(), Collections.emptyMap(), ifDescrResult.errorMessages(), ifDescrResult.exception());
        }
        List<SNMPItem> snmpItems = snmpMapping.toSNMPInterfaceItems(ifDescrResult.result(), networktaskId);
        Collections.sort(snmpItems, new SNMPItemNameComparator());
        SNMPAccess.WalkResult ifTypeResult = snmpAccess.walkInterfacesType();
        if (!ifTypeResult.success()) {
            return new SNMPScanResult(true, snmpItems, Collections.emptyMap(), ifTypeResult.errorMessages(), ifTypeResult.exception());
        }
        SNMPAccess.WalkResult ifOperStatusResult = snmpAccess.walkInterfacesOperStatus();
        if (!ifOperStatusResult.success()) {
            return new SNMPScanResult(true, snmpItems, Collections.emptyMap(), ifOperStatusResult.errorMessages(), ifOperStatusResult.exception());
        }
        Map<String, String> combinedInfo = new HashMap<>(ifTypeResult.result().size() + ifOperStatusResult.result().size());
        combinedInfo.putAll(ifTypeResult.result());
        combinedInfo.putAll(ifOperStatusResult.result());
        Map<String, SNMPInterfaceInfo> interfaceInfos = snmpMapping.toInterfaceInfo(snmpItems, combinedInfo);
        return new SNMPScanResult(true, snmpItems, interfaceInfos, Collections.emptyList(), null);
    }

    protected Callable<DNSLookupResult> getDNSLookup(String host) {
        return new DNSLookup(host);
    }

    protected SNMPAccess getSNMPAccess() {
        int timeout = getResources().getInteger(R.integer.snmp_request_timeout_ui);
        int retries = getResources().getInteger(R.integer.snmp_request_retries_ui);
        return new SNMPAccess(getContext(), inetAddress, port, snmpVersion, community, inetAddress instanceof Inet6Address, timeout, retries);
    }

    protected Resources getResources() {
        return getContext().getResources();
    }

    protected Context getContext() {
        return context;
    }

    private static class SNMPItemNameComparator implements Comparator<SNMPItem> {
        @Override
        public int compare(SNMPItem item1, SNMPItem item2) {
            String name1 = item1.getName() != null ? item1.getName() : "";
            String name2 = item2.getName() != null ? item2.getName() : "";
            return name1.compareTo(name2);
        }
    }
}
