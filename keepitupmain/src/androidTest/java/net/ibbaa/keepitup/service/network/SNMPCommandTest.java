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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.model.SNMPVersion;
import net.ibbaa.keepitup.test.mock.TestRegistry;
import net.ibbaa.keepitup.test.mock.TestSNMPCommand;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.InetAddress;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

@MediumTest
@RunWith(AndroidJUnit4.class)
@SuppressWarnings({"SequencedCollectionMethodCanBeUsed"})
public class SNMPCommandTest {

    private TestSNMPCommand createCommand(long lastSysUpTime) {
        return new TestSNMPCommand(TestRegistry.getContext(), InetAddress.getLoopbackAddress(), 161, SNMPVersion.V2C, "public", lastSysUpTime, false);
    }

    @Test
    public void testCallSuccess() {
        TestSNMPCommand command = createCommand(-1);
        String sysUpTimeOid = TestRegistry.getContext().getString(R.string.sys_uptime_oid);
        String sysDescrOid = TestRegistry.getContext().getString(R.string.sys_descr_oid);
        TreeMap<String, String> resultMap = new TreeMap<>();
        resultMap.put(sysUpTimeOid, "12345");
        resultMap.put(sysDescrOid, "Test system");
        command.getMockSNMPAccess().setWalkResult(new SNMPAccess.WalkResult(true, resultMap, null, Collections.emptyList()));
        SNMPCommandResult result = command.call();
        assertTrue(result.success());
        assertFalse(result.reboot());
        assertEquals(2, result.result().size());
        assertEquals("12345", result.result().get(sysUpTimeOid));
        assertEquals("Test system", result.result().get(sysDescrOid));
        assertNull(result.exception());
        assertTrue(result.errorMessages().isEmpty());
        assertTrue(result.duration() >= 0);
    }

    @Test
    public void testCallFailure() {
        TestSNMPCommand command = createCommand(-1);
        command.getMockSNMPAccess().setWalkResult(new SNMPAccess.WalkResult(false, Collections.emptyMap(), null, List.of("No response from host.")));
        SNMPCommandResult result = command.call();
        assertFalse(result.success());
        assertFalse(result.reboot());
        assertTrue(result.result().isEmpty());
        assertNull(result.exception());
        assertEquals(1, result.errorMessages().size());
        assertEquals("No response from host.", result.errorMessages().get(0));
        assertTrue(result.duration() >= 0);
    }

    @Test
    public void testCallFailureWithErrors() {
        TestSNMPCommand command = createCommand(-1);
        command.getMockSNMPAccess().setWalkResult(new SNMPAccess.WalkResult(false, Collections.emptyMap(), null, List.of("Error 1", "Error 2")));
        SNMPCommandResult result = command.call();
        assertFalse(result.success());
        assertFalse(result.reboot());
        assertEquals(2, result.errorMessages().size());
        assertEquals("Error 1", result.errorMessages().get(0));
        assertEquals("Error 2", result.errorMessages().get(1));
    }

    @Test
    public void testCallWithException() {
        TestSNMPCommand command = createCommand(-1);
        RuntimeException exception = new RuntimeException("SNMP error");
        command.getMockSNMPAccess().setWalkResult(new SNMPAccess.WalkResult(false, Collections.emptyMap(), exception, List.of("SNMP error")));
        SNMPCommandResult result = command.call();
        assertFalse(result.success());
        assertFalse(result.reboot());
        assertNotNull(result.exception());
        assertEquals("SNMP error", result.exception().getMessage());
        assertEquals(1, result.errorMessages().size());
        assertEquals("SNMP error", result.errorMessages().get(0));
    }

    @Test
    public void testCallNullResultMap() {
        TestSNMPCommand command = createCommand(-1);
        command.getMockSNMPAccess().setWalkResult(new SNMPAccess.WalkResult(false, null, null, Collections.emptyList()));
        SNMPCommandResult result = command.call();
        assertFalse(result.success());
        assertFalse(result.reboot());
        assertTrue(result.result().isEmpty());
    }

    @Test
    public void testCallRebootDetected() {
        TestSNMPCommand command = createCommand(5000);
        String sysUpTimeOid = TestRegistry.getContext().getString(R.string.sys_uptime_oid);
        TreeMap<String, String> resultMap = new TreeMap<>();
        resultMap.put(sysUpTimeOid, "100");
        command.getMockSNMPAccess().setWalkResult(new SNMPAccess.WalkResult(true, resultMap, null, Collections.emptyList()));
        SNMPCommandResult result = command.call();
        assertTrue(result.success());
        assertTrue(result.reboot());
    }

    @Test
    public void testCallNoRebootSysUpTimeIncreased() {
        TestSNMPCommand command = createCommand(1000);
        String sysUpTimeOid = TestRegistry.getContext().getString(R.string.sys_uptime_oid);
        TreeMap<String, String> resultMap = new TreeMap<>();
        resultMap.put(sysUpTimeOid, "5000");
        command.getMockSNMPAccess().setWalkResult(new SNMPAccess.WalkResult(true, resultMap, null, Collections.emptyList()));
        SNMPCommandResult result = command.call();
        assertTrue(result.success());
        assertFalse(result.reboot());
    }

    @Test
    public void testCallNoRebootLastSysUpTimeNegative() {
        TestSNMPCommand command = createCommand(-1);
        String sysUpTimeOid = TestRegistry.getContext().getString(R.string.sys_uptime_oid);
        TreeMap<String, String> resultMap = new TreeMap<>();
        resultMap.put(sysUpTimeOid, "100");
        command.getMockSNMPAccess().setWalkResult(new SNMPAccess.WalkResult(true, resultMap, null, Collections.emptyList()));
        SNMPCommandResult result = command.call();
        assertTrue(result.success());
        assertFalse(result.reboot());
    }

    @Test
    public void testCallNoRebootCurrentSysUpTimeMissing() {
        TestSNMPCommand command = createCommand(5000);
        String sysDescrOid = TestRegistry.getContext().getString(R.string.sys_descr_oid);
        TreeMap<String, String> resultMap = new TreeMap<>();
        resultMap.put(sysDescrOid, "Test system");
        command.getMockSNMPAccess().setWalkResult(new SNMPAccess.WalkResult(true, resultMap, null, Collections.emptyList()));
        SNMPCommandResult result = command.call();
        assertTrue(result.success());
        assertFalse(result.reboot());
    }

    @Test
    public void testCallNoRebootCounterOverflow() {
        long aboveThreshold = 4200000000L;
        TestSNMPCommand command = createCommand(aboveThreshold);
        String sysUpTimeOid = TestRegistry.getContext().getString(R.string.sys_uptime_oid);
        TreeMap<String, String> resultMap = new TreeMap<>();
        resultMap.put(sysUpTimeOid, "1000");
        command.getMockSNMPAccess().setWalkResult(new SNMPAccess.WalkResult(true, resultMap, null, Collections.emptyList()));
        SNMPCommandResult result = command.call();
        assertTrue(result.success());
        assertFalse(result.reboot());
    }

    @Test
    public void testCallRebootDetectedBelowOverflowThreshold() {
        long belowThreshold = 4000000000L;
        TestSNMPCommand command = createCommand(belowThreshold);
        String sysUpTimeOid = TestRegistry.getContext().getString(R.string.sys_uptime_oid);
        TreeMap<String, String> resultMap = new TreeMap<>();
        resultMap.put(sysUpTimeOid, "1000");
        command.getMockSNMPAccess().setWalkResult(new SNMPAccess.WalkResult(true, resultMap, null, Collections.emptyList()));
        SNMPCommandResult result = command.call();
        assertTrue(result.success());
        assertTrue(result.reboot());
    }
}
