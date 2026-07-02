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
import net.ibbaa.keepitup.model.SNMPAuthAlgorithm;
import net.ibbaa.keepitup.model.SNMPAuthInfo;
import net.ibbaa.keepitup.model.SNMPPrivAlgorithm;
import net.ibbaa.keepitup.model.SNMPTransport;
import net.ibbaa.keepitup.model.SNMPVersion;
import net.ibbaa.keepitup.util.StringUtil;
import net.ibbaa.keepitup.util.URLUtil;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.ScopedPDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.TransportMapping;
import org.snmp4j.UserTarget;
import org.snmp4j.mp.MessageProcessingModel;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.AuthMD5;
import org.snmp4j.security.AuthSHA;
import org.snmp4j.security.SecurityLevel;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.security.UsmUser;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.Variable;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultTcpTransportMapping;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.PDUFactory;
import org.snmp4j.util.TreeEvent;
import org.snmp4j.util.TreeUtils;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class SNMPAccess implements AutoCloseable {

    static {
        SecurityProtocols.getInstance().addAuthenticationProtocol(new AuthMD5());
        SecurityProtocols.getInstance().addAuthenticationProtocol(new AuthSHA());
    }

    private final Context context;
    private final InetAddress address;
    private final int port;
    private final SNMPVersion snmpVersion;
    private final SNMPTransport snmpTransport;
    private final SNMPAuthInfo authInfo;
    private final boolean ip6;
    private final int timeoutSec;
    private final int retries;

    private TransportMapping<?> transport;
    private Snmp snmp;
    private Target<Address> target;

    public SNMPAccess(Context context, InetAddress address, int port, SNMPVersion snmpVersion, SNMPTransport snmpTransport, SNMPAuthInfo authInfo, boolean ip6) {
        this(context, address, port, snmpVersion, snmpTransport, authInfo, ip6, context.getResources().getInteger(R.integer.snmp_request_timeout), context.getResources().getInteger(R.integer.snmp_request_retries));
    }

    public SNMPAccess(Context context, InetAddress address, int port, SNMPVersion snmpVersion, SNMPTransport snmpTransport, SNMPAuthInfo authInfo, boolean ip6, int timeoutSec, int retries) {
        this.context = context;
        this.address = address;
        this.port = port;
        this.snmpVersion = snmpVersion;
        this.snmpTransport = snmpTransport;
        this.authInfo = authInfo;
        this.ip6 = ip6;
        this.timeoutSec = timeoutSec;
        this.retries = retries;
    }

    public WalkResult walk(String oid, WalkFilter filter, boolean emptyIsValid) {
        Log.d(SNMPAccess.class.getName(), "walk, oid is " + oid);
        try {
            ensureSession();
            Map<String, Variable> results = new HashMap<>();
            List<String> errors = new ArrayList<>();
            if (!fetchAndProcessSubtree(snmp, target, oid, results, errors, emptyIsValid)) {
                return new WalkResult(false, Collections.emptyMap(), null, List.of(getResources().getString(R.string.text_snmp_no_response)));
            }
            Map<String, String> filteredResult = filter.filter(results);
            return new WalkResult(errors.isEmpty(), filteredResult, null, errors);
        } catch (Exception exc) {
            Log.e(SNMPAccess.class.getName(), "Error on SNMP request", exc);
            return new WalkResult(false, Collections.emptyMap(), exc, Collections.emptyList());
        }
    }

    private void ensureSession() throws IOException {
        Log.d(SNMPAccess.class.getName(), "ensureSession");
        if (snmp != null) {
            return;
        }
        transport = getTransportMapping();
        snmp = new Snmp(transport);
        transport.listen();
        if (version() == SnmpConstants.version3) {
            OctetString localEngineID = new OctetString(snmp.getLocalEngineID());
            USM usm = new USM(SecurityProtocols.getInstance(), localEngineID, 0);
            SecurityModels.getInstance().addSecurityModel(usm);
            UsmUser usmUser = getUsmUser();
            if (usmUser != null) {
                snmp.getUSM().addUser(usmUser);
            }
            target = configureUserTarget();
        } else {
            target = configureCommunityTarget();
        }
    }

    @Override
    public void close() {
        Log.d(SNMPAccess.class.getName(), "close");
        if (snmp != null) {
            try {
                snmp.close();
            } catch (Exception exc) {
                Log.e(SNMPAccess.class.getName(), "Error closing snmp object", exc);
            } finally {
                snmp = null;
                transport = null;
                target = null;
            }
        }
    }

    public WalkResult walkInterfacesDescr() {
        Log.d(SNMPAccess.class.getName(), "walkInterfacesDescr");
        String oid = new SNMPMapping(getContext()).getInterfaceDescrOID();
        return walk(oid, results -> prefixFilter(oid, results), true);
    }

    public WalkResult walkInterfacesType() {
        Log.d(SNMPAccess.class.getName(), "walkInterfacesType");
        String oid = new SNMPMapping(getContext()).getInterfaceTypeOID();
        return walk(oid, results -> prefixFilter(oid, results), true);
    }

    public WalkResult walkInterfacesAlias() {
        Log.d(SNMPAccess.class.getName(), "walkInterfacesAlias");
        String oid = new SNMPMapping(getContext()).getInterfaceAliasOID();
        return walk(oid, results -> prefixFilter(oid, results), true);
    }

    public WalkResult walkInterfacesOperStatus() {
        Log.d(SNMPAccess.class.getName(), "walkInterfacesOperStatus");
        String oid = new SNMPMapping(getContext()).getInterfaceOperStatusOID();
        return walk(oid, results -> prefixFilter(oid, results), true);
    }

    public WalkResult walkSystem() {
        Log.d(SNMPAccess.class.getName(), "walkSystem");
        SNMPMapping snmpMapping = new SNMPMapping(getContext());
        WalkResult walkResult = walk(snmpMapping.getSystemOID(), this::systemFilter, false);
        if (!walkResult.success()) {
            return walkResult;
        }
        long currentSysUpTime = snmpMapping.getSysUpTime(walkResult.result());
        if (currentSysUpTime >= 0) {
            return walkResult;
        }
        String sysUpTimeOIDValue = getResources().getString(R.string.sys_uptime_label_short) + " (" + snmpMapping.getSysUpTimeOID() + ")";
        String sysUpTimeError = getResources().getString(R.string.text_snmp_mandatory_oid_missing, sysUpTimeOIDValue);
        return new WalkResult(false, walkResult.result(), walkResult.exception(), List.of(sysUpTimeError));
    }

    private Map<String, String> prefixFilter(String baseOID, Map<String, Variable> results) {
        SNMPMapping snmpMapping = new SNMPMapping(getContext());
        OID prefix = new OID(baseOID);
        Map<String, String> filteredResults = new TreeMap<>();
        for (Map.Entry<String, Variable> entry : results.entrySet()) {
            String oid = entry.getKey();
            Variable variable = entry.getValue();
            OID entryOID = getOID(oid);
            if (entryOID != null && entryOID.startsWith(prefix)) {
                String value = snmpMapping.getValueForOID(oid, variable);
                if (value != null) {
                    filteredResults.put(oid, value);
                }
            }
        }
        return filteredResults;
    }

    private OID getOID(String oid) {
        Log.d(SNMPAccess.class.getName(), "getOID for oid " + oid);
        try {
            return new OID(oid);
        } catch (Exception exc) {
            Log.e(SNMPAccess.class.getName(), "Unparseable OID: " + oid);
        }
        return null;
    }

    private Map<String, String> systemFilter(Map<String, Variable> results) {
        SNMPMapping snmpMapping = new SNMPMapping(getContext());
        Map<String, String> filteredResults = new TreeMap<>();
        for (Map.Entry<String, Variable> entry : results.entrySet()) {
            String oid = entry.getKey();
            Variable variable = entry.getValue();
            if (snmpMapping.supportsSystemOID(oid)) {
                String value = snmpMapping.getValueForOID(oid, variable);
                if (value != null) {
                    filteredResults.put(oid, value);
                }
            }
        }
        return filteredResults;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    protected boolean fetchAndProcessSubtree(Snmp snmp, Target<Address> target, String oid, Map<String, Variable> results, List<String> errors, boolean emptyIsValid) {
        TreeUtils treeUtils = new TreeUtils(snmp, getPDUFactory());
        List<TreeEvent> events = treeUtils.getSubtree(target, new OID(oid));
        if (events == null || events.isEmpty()) {
            return emptyIsValid;
        }
        prepareResult(events, results, errors);
        return true;
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

    private CommunityTarget<Address> configureCommunityTarget() {
        Log.d(SNMPAccess.class.getName(), "configureCommunityTarget");
        CommunityTarget<Address> target = new CommunityTarget<>();
        target.setCommunity(new OctetString(StringUtil.notNull(authInfo != null ? authInfo.getCommunity() : null)));
        target.setAddress(GenericAddress.parse(formatAddress()));
        target.setVersion(version());
        target.setTimeout(timeoutSec * 1000L);
        target.setRetries(retries);
        return target;
    }

    private UserTarget<Address> configureUserTarget() {
        Log.d(SNMPAccess.class.getName(), "configureUserTarget");
        UserTarget<Address> target = new UserTarget<>();
        target.setAddress(GenericAddress.parse(formatAddress()));
        target.setVersion(version());
        target.setTimeout(timeoutSec * 1000L);
        target.setRetries(retries);
        target.setSecurityLevel(getSecurityLevel());
        target.setSecurityName(new OctetString(StringUtil.notNull(authInfo != null ? authInfo.getUserName() : null)));
        return target;
    }

    private int version() {
        if (snmpVersion != null) {
            if (snmpVersion.isV3()) {
                return SnmpConstants.version3;
            }
            return snmpVersion.isV2C() ? SnmpConstants.version2c : SnmpConstants.version1;
        }
        return SnmpConstants.version2c;
    }

    private TransportMapping<?> getTransportMapping() throws IOException {
        if (snmpTransport != null && snmpTransport.isTCP()) {
            return new DefaultTcpTransportMapping();
        }
        return new DefaultUdpTransportMapping();
    }

    private int getSecurityLevel() {
        if (authInfo == null) {
            return SecurityLevel.AUTH_PRIV;
        }
        SNMPAuthAlgorithm authAlgorithm = authInfo.getAuthAlgorithm();
        SNMPPrivAlgorithm privAlgorithm = authInfo.getPrivAlgorithm();
        if (authAlgorithm == null || authAlgorithm.isNone()) {
            return SecurityLevel.NOAUTH_NOPRIV;
        } else if (privAlgorithm == null || privAlgorithm.isNone()) {
            return SecurityLevel.AUTH_NOPRIV;
        }
        return SecurityLevel.AUTH_PRIV;
    }

    private PDUFactory getPDUFactory() {
        if (version() == SnmpConstants.version3) {
            return new ScopedPDUFactory();
        }
        return new DefaultPDUFactory();
    }

    private String formatAddress() {
        String hostAddress = URLUtil.getHostAddress(address);
        String prefix = (snmpTransport != null && snmpTransport.isTCP()) ? "tcp:" : "udp:";
        String address = ip6 ? prefix + "[" + hostAddress + "]" : prefix + hostAddress;
        if (port >= 0) {
            address += "/" + port;
        }
        return address;
    }

    private UsmUser getUsmUser() {
        if (authInfo == null) {
            return null;
        }
        SNMPMapping snmpMapping = new SNMPMapping(getContext());
        SNMPAuthAlgorithm authAlgorithm = authInfo.getAuthAlgorithm();
        SNMPPrivAlgorithm privAlgorithm = authInfo.getPrivAlgorithm();
        OID authOID = snmpMapping.getAuthAlgorithmOID(authAlgorithm);
        OID privOID = snmpMapping.getPrivAlgorithmOID(privAlgorithm);
        return new UsmUser(new OctetString(StringUtil.notNull(authInfo.getUserName())), authOID, authOID != null ? new OctetString(authInfo.getAuthPassphrase()) : null, privOID, privOID != null ? new OctetString(authInfo.getPrivPassphrase()) : null);
    }

    private Context getContext() {
        return context;
    }

    private Resources getResources() {
        return getContext().getResources();
    }

    private static class ScopedPDUFactory implements PDUFactory {
        @Override
        public PDU createPDU(Target<?> target) {
            return new ScopedPDU();
        }

        @Override
        public PDU createPDU(MessageProcessingModel messageProcessingModel) {
            return new ScopedPDU();
        }
    }

    public interface WalkFilter {
        Map<String, String> filter(Map<String, Variable> results);
    }

    public record WalkResult(boolean success, Map<String, String> result, Throwable exception, List<String> errorMessages) {
    }
}
