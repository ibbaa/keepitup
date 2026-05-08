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

import android.content.Context;
import android.content.res.Resources;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.model.SNMPVersion;
import net.ibbaa.keepitup.util.StringUtil;
import net.ibbaa.keepitup.util.URLUtil;

import org.snmp4j.CommunityTarget;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.Variable;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.TreeEvent;
import org.snmp4j.util.TreeUtils;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class SNMPAccess {

    private final Context context;
    private final InetAddress address;
    private final int port;
    private final SNMPVersion snmpVersion;
    private final String community;
    private final boolean ip6;

    public SNMPAccess(Context context, InetAddress address, int port, SNMPVersion snmpVersion, String community, boolean ip6) {
        this.context = context;
        this.address = address;
        this.port = port;
        this.snmpVersion = snmpVersion;
        this.community = community;
        this.ip6 = ip6;
    }

    public WalkResult walk(String oid) {
        Log.d(SNMPAccess.class.getName(), "walk, oid is " + oid);
        TransportMapping<UdpAddress> transport;
        Snmp snmp = null;
        try {
            transport = new DefaultUdpTransportMapping();
            snmp = new Snmp(transport);
            transport.listen();
            CommunityTarget<?> target = configureCommunityTarget();
            Map<String, Variable> results = new HashMap<>();
            List<String> errors = new ArrayList<>();
            if (!fetchAndProcessSubtree(snmp, target, oid, results, errors)) {
                return new WalkResult(false, Collections.emptyMap(), null, List.of(getResources().getString(R.string.text_snmp_no_response)));
            }
            Map<String, String> filteredResult = filterResult(results);
            if (errors.isEmpty()) {
                SNMPMapping snmpMapping = new SNMPMapping(getContext());
                long currentSysUpTime = snmpMapping.getSysUpTime(filteredResult);
                if (currentSysUpTime < 0) {
                    String sysUpTimeOIDValue = getResources().getString(R.string.sys_uptime_label_short) + " (" + snmpMapping.getSysUpTimeOID() + ")";
                    String sysUpTimeError = getResources().getString(R.string.text_snmp_mandatory_oid_missing, sysUpTimeOIDValue);
                    errors.add(sysUpTimeError);
                }
            }
            return new WalkResult(errors.isEmpty(), filteredResult, null, errors);
        } catch (Exception exc) {
            Log.e(SNMPAccess.class.getName(), "Error on SNMP request", exc);
            return new WalkResult(false, Collections.emptyMap(), exc, List.of(exc.getMessage() != null ? exc.getMessage() : ""));
        } finally {
            if (snmp != null) {
                try {
                    snmp.close();
                } catch (Exception exc) {
                    Log.e(SNMPAccess.class.getName(), "Error closing snmp object", exc);
                }
            }
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    protected boolean fetchAndProcessSubtree(Snmp snmp, CommunityTarget<?> target, String oid, Map<String, Variable> results, List<String> errors) {
        TreeUtils treeUtils = new TreeUtils(snmp, new DefaultPDUFactory());
        List<TreeEvent> events = treeUtils.getSubtree(target, new OID(oid));
        if (events == null || events.isEmpty()) {
            return false;
        }
        prepareResult(events, results, errors);
        return true;
    }

    private Map<String, String> filterResult(Map<String, Variable> results) {
        SNMPMapping snmpMapping = new SNMPMapping(getContext());
        Map<String, String> filteredResults = new TreeMap<>();
        for (Map.Entry<String, Variable> entry : results.entrySet()) {
            String oid = entry.getKey();
            Variable variable = entry.getValue();
            if (snmpMapping.supportsOID(oid)) {
                String value = snmpMapping.getValueForOID(oid, variable);
                if (value != null) {
                    filteredResults.put(oid, value);
                }
            }
        }
        return filteredResults;
    }

    private void prepareResult(List<TreeEvent> events, Map<String, Variable> results, List<String> errors) {
        Log.d(SNMPAccess.class.getName(), "prepareResult");
        for (TreeEvent event : events) {
            if (event.isError()) {
                errors.add(StringUtil.notNull(event.getErrorMessage()).trim());
            } else {
                VariableBinding[] bindings = event.getVariableBindings();
                if (bindings != null) {
                    for (VariableBinding binding : bindings) {
                        results.put(binding.getOid().toString(), binding.getVariable());
                    }
                }
            }
        }
    }

    private CommunityTarget<?> configureCommunityTarget() {
        Log.d(SNMPAccess.class.getName(), "configureCommunityTarget");
        CommunityTarget<UdpAddress> target = new CommunityTarget<>();
        target.setCommunity(new OctetString(StringUtil.notNull(community)));
        target.setAddress((UdpAddress) GenericAddress.parse(formatAddress()));
        target.setVersion(version());
        target.setTimeout(getResources().getInteger(R.integer.snmp_request_timeout) * 1000L);
        target.setRetries(getResources().getInteger(R.integer.snmp_request_retries));
        return target;
    }

    private int version() {
        if (snmpVersion != null) {
            return snmpVersion.isV2C() ? SnmpConstants.version2c : SnmpConstants.version1;
        }
        return SnmpConstants.version2c;
    }

    private String formatAddress() {
        String hostAddress = URLUtil.getHostAddress(address);
        String address = ip6 ? "udp:[" + hostAddress + "]" : "udp:" + hostAddress;
        if (port >= 0) {
            address += "/" + port;
        }
        return address;
    }

    private Context getContext() {
        return context;
    }

    private Resources getResources() {
        return getContext().getResources();
    }

    public record WalkResult(boolean success, Map<String, String> result, Throwable exception, List<String> errorMessages) {
    }
}
