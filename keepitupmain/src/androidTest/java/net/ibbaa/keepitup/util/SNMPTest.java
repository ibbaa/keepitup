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

package net.ibbaa.keepitup.util;

import net.ibbaa.keepitup.model.SNMPVersion;
import net.ibbaa.keepitup.service.network.SNMPAccess;
import net.ibbaa.keepitup.test.mock.TestRegistry;

import org.junit.Test;
import org.snmp4j.smi.Variable;

import java.net.InetAddress;
import java.util.Map;
import java.util.TreeMap;

public class SNMPTest {

    @Test
    public void testAccess() throws Exception {
        SNMPAccess access = new SNMPAccess(TestRegistry.getContext(), InetAddress.getByName("gaia.ibbaa.lan"), 161, SNMPVersion.V2C, "", false);
        SNMPAccess.WalkResult walkResultSystem = access.walkSystem();
        SNMPAccess.WalkResult walkResultInterfacesDescr = access.walkInterfacesDescr();
        SNMPAccess.WalkResult walkResultInterfacesType = access.walkInterfacesType();
        SNMPAccess.WalkResult walkResultInterfacesAlias = access.walkInterfacesAlias();
        SNMPAccess.WalkResult walkResultInterfacesOperStatus = access.walkInterfacesOperStatus();
        System.out.println(walkResultSystem.result());
        System.out.println(walkResultInterfacesDescr.result());
        System.out.println(walkResultInterfacesType.result());
        System.out.println(walkResultInterfacesAlias.result());
        System.out.println(walkResultInterfacesOperStatus.result());

        /*SNMPAccess.WalkResult walkResultIF = access.walk("1.3.6.1.2.1.2.2.1.2", this::filter);
        SNMPAccess.WalkResult walkResultIFType = access.walk("1.3.6.1.2.1.2.2.1.3", this::filter);
        SNMPAccess.WalkResult walkResultConnector = access.walk("1.3.6.1.2.1.31.1.1.1.17", this::filter);
        SNMPAccess.WalkResult walkResultAlias = access.walk("1.3.6.1.2.1.31.1.1.1.18", this::filter);
        SNMPAccess.WalkResult walkResultUp = access.walk("1.3.6.1.2.1.2.2.1.8", this::filter);

        SNMPAccess.WalkResult walkResultIFGesamt = access.walk("1.3.6.1.2.1.2.2.1", this::filter);
        SNMPAccess.WalkResult walkResultIFX = access.walk("1.3.6.1.2.1.31.1.1.1", this::filter);

        Map<String, String> resultIF = walkResultIF.result();
        Map<String, String> resultIFType = walkResultIFType.result();
        Map<String, String> resultConnector = walkResultConnector.result();
        Map<String, String> resultAlias = walkResultAlias.result();
        Map<String, String> resultUp = walkResultUp.result();
        Map<String, String> resultIFGesamt = walkResultIFGesamt.result();
        Map<String, String> resultIFX = walkResultIFX.result();


        System.out.println("1.3.6.1.2.1.2.2.1.2 -> " + resultIF);
        System.out.println("1.3.6.1.2.1.2.2.1.3 -> " + resultIFType);
        System.out.println("1.3.6.1.2.1.31.1.1.1.17 -> " + resultConnector);
        System.out.println("1.3.6.1.2.1.31.1.1.1.18 -> " + resultAlias);
        System.out.println("1.3.6.1.2.1.2.2.1.8 -> " + resultUp);

        System.out.println("1.3.6.1.2.1.2.2.1 -> " + resultIFGesamt);
        System.out.println("1.3.6.1.2.1.31.1.1.1 -> " + resultIFX);*/
    }

    private Map<String, String> filter(Map<String, Variable> results) {
        Map<String, String> filtered = new TreeMap<>();
        for (Map.Entry<String, Variable> entry : results.entrySet()) {
            filtered.put(entry.getKey(), entry.getValue().toString());
        }
        return filtered;
    }
}