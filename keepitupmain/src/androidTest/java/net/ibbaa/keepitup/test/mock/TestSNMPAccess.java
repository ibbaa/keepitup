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

package net.ibbaa.keepitup.test.mock;

import android.content.Context;

import net.ibbaa.keepitup.model.SNMPAuthInfo;
import net.ibbaa.keepitup.model.SNMPTransport;
import net.ibbaa.keepitup.model.SNMPVersion;
import net.ibbaa.keepitup.service.network.SNMPAccess;

import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.Variable;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestSNMPAccess extends SNMPAccess {

    private Map<String, Variable> subtreeResults;
    private List<String> subtreeErrors;
    private boolean subtreeEmpty;
    private RuntimeException subtreeException;
    private byte[] discoveredEngineID;
    private int discoverEngineIDCallCount;

    public TestSNMPAccess(Context context, InetAddress address, int port, SNMPVersion snmpVersion, SNMPTransport snmpTransport, SNMPAuthInfo authInfo, boolean ip6) {
        super(context, address, port, snmpVersion, snmpTransport, authInfo, ip6);
        reset();
    }

    public void reset() {
        subtreeResults = new HashMap<>();
        subtreeErrors = new ArrayList<>();
        subtreeEmpty = false;
        subtreeException = null;
        discoveredEngineID = new byte[]{1, 2, 3, 4, 5};
        discoverEngineIDCallCount = 0;
    }

    public void setDiscoveredEngineID(byte[] discoveredEngineID) {
        this.discoveredEngineID = discoveredEngineID;
    }

    public int getDiscoverEngineIDCallCount() {
        return discoverEngineIDCallCount;
    }

    @Override
    protected byte[] discoverEngineID(Address targetAddress) {
        discoverEngineIDCallCount++;
        return discoveredEngineID;
    }

    public void setSubtreeResults(Map<String, Variable> subtreeResults) {
        this.subtreeResults = subtreeResults;
    }

    public void setSubtreeErrors(List<String> subtreeErrors) {
        this.subtreeErrors = subtreeErrors;
    }

    public void setSubtreeEmpty(boolean subtreeEmpty) {
        this.subtreeEmpty = subtreeEmpty;
    }

    public void setSubtreeException(RuntimeException subtreeException) {
        this.subtreeException = subtreeException;
    }

    @Override
    protected boolean fetchAndProcessSubtree(Snmp snmp, Target<Address> target, String oid, Map<String, Variable> results, List<String> errors, boolean emptyIsValid) {
        if (subtreeException != null) {
            throw subtreeException;
        }
        if (subtreeEmpty) {
            return emptyIsValid;
        }
        results.putAll(subtreeResults);
        errors.addAll(subtreeErrors);
        return true;
    }
}
