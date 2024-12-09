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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import net.ibbaa.keepitup.test.mock.TestPingCommand;
import net.ibbaa.keepitup.test.mock.TestRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Collections;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class PingCommandTest {

    @Test
    public void testCallNotStopOnSuccess() {
        TestPingCommand pingCommand = new TestPingCommand(TestRegistry.getContext(), "127.0.0.1", 3, true, 56, false, false);
        PingCommandResult result = new PingCommandResult(0, 1, "testoutput", null);
        pingCommand.setPingResults(Collections.singletonList(result));
        PingCommandResult callResult = pingCommand.call();
        assertEquals(0, callResult.processReturnCode());
        assertEquals(1, result.pingCalls());
        assertEquals("testoutput", result.output());
        assertNull(result.exception());
    }

    @Test
    public void testCallSuccessOneAttemptStopOnSuccess() {
        TestPingCommand pingCommand = new TestPingCommand(TestRegistry.getContext(), "127.0.0.1", 2, true, 56, true, false);
        PingCommandResult result = new PingCommandResult(0, 1, "testoutput", null);
        pingCommand.setPingResults(Collections.singletonList(result));
        PingCommandResult callResult = pingCommand.call();
        assertEquals(0, callResult.processReturnCode());
        assertEquals(1, result.pingCalls());
        assertEquals("testoutput", result.output());
        assertNull(result.exception());
    }

    @Test
    public void testCallFailureOneAttemptStopOnSuccess() {
        TestPingCommand pingCommand = new TestPingCommand(TestRegistry.getContext(), "127.0.0.1", 1, true, 56, true, false);
        PingCommandResult result = new PingCommandResult(1, 1, "testoutput", null);
        pingCommand.setPingResults(Collections.singletonList(result));
        PingCommandResult callResult = pingCommand.call();
        assertEquals(1, callResult.processReturnCode());
        assertEquals(1, result.pingCalls());
        assertEquals("testoutput", result.output());
        assertNull(result.exception());
    }

    @Test
    public void testCallSuccessTwoAttemptsStopOnSuccess() {
        TestPingCommand pingCommand = new TestPingCommand(TestRegistry.getContext(), "127.0.0.1", 3, true, 56, true, false);
        PingCommandResult result1 = new PingCommandResult(1, 1, "testoutput", null);
        PingCommandResult result2 = new PingCommandResult(0, 1, "testoutput", null);
        PingCommandResult result3 = new PingCommandResult(1, 1, "testoutput", null);
        pingCommand.setPingResults(Arrays.asList(result1, result2, result3));
        PingCommandResult callResult = pingCommand.call();
        assertEquals(0, callResult.processReturnCode());
        assertEquals(2, callResult.pingCalls());
        assertEquals("testoutput", callResult.output());
        assertNull(callResult.exception());
    }

    @Test
    public void testCallFailureTwoAttemptsStopOnSuccess() {
        TestPingCommand pingCommand = new TestPingCommand(TestRegistry.getContext(), "127.0.0.1", 2, true, 56, true, false);
        PingCommandResult result1 = new PingCommandResult(1, 1, "testoutput", null);
        PingCommandResult result2 = new PingCommandResult(2, 1, "testoutput", null);
        pingCommand.setPingResults(Arrays.asList(result1, result2));
        PingCommandResult callResult = pingCommand.call();
        assertEquals(2, callResult.processReturnCode());
        assertEquals(2, callResult.pingCalls());
        assertEquals("testoutput", callResult.output());
        assertNull(callResult.exception());
    }

    @Test
    public void testCallSuccessThreeAttemptsStopOnSuccess() {
        TestPingCommand pingCommand = new TestPingCommand(TestRegistry.getContext(), "127.0.0.1", 3, true, 56, true, false);
        PingCommandResult result1 = new PingCommandResult(1, 1, "testoutput", null);
        PingCommandResult result2 = new PingCommandResult(1, 1, "testoutput", null);
        PingCommandResult result3 = new PingCommandResult(0, 1, "testoutput", null);
        pingCommand.setPingResults(Arrays.asList(result1, result2, result3));
        PingCommandResult callResult = pingCommand.call();
        assertEquals(0, callResult.processReturnCode());
        assertEquals(3, callResult.pingCalls());
        assertEquals("testoutput", callResult.output());
        assertNull(callResult.exception());
    }

    @Test
    public void testCallExceptionStopOnSuccess() {
        TestPingCommand pingCommand = new TestPingCommand(TestRegistry.getContext(), "127.0.0.1", 2, true, 56, true, false);
        PingCommandResult result1 = new PingCommandResult(1, 1, "testoutput", new Exception("Test"));
        PingCommandResult result2 = new PingCommandResult(0, 1, "testoutput", null);
        pingCommand.setPingResults(Arrays.asList(result1, result2));
        PingCommandResult callResult = pingCommand.call();
        assertEquals(1, callResult.processReturnCode());
        assertEquals(1, callResult.pingCalls());
        assertEquals("testoutput", callResult.output());
        assertEquals("Test", callResult.exception().getMessage());
    }

    @Test
    public void testCallExceptionTwoAttemptsStopOnSuccess() {
        TestPingCommand pingCommand = new TestPingCommand(TestRegistry.getContext(), "127.0.0.1", 2, true, 56, true, false);
        PingCommandResult result1 = new PingCommandResult(1, 1, "testoutput", null);
        PingCommandResult result2 = new PingCommandResult(1, 1, "testoutput", new Exception("Test"));
        pingCommand.setPingResults(Arrays.asList(result1, result2));
        PingCommandResult callResult = pingCommand.call();
        assertEquals(1, callResult.processReturnCode());
        assertEquals(2, callResult.pingCalls());
        assertEquals("testoutput", callResult.output());
        assertEquals("Test", callResult.exception().getMessage());
    }

    @Test
    public void testCallExceptionSuccessStopOnSuccess() {
        TestPingCommand pingCommand = new TestPingCommand(TestRegistry.getContext(), "127.0.0.1", 2, true, 56, true, false);
        PingCommandResult result1 = new PingCommandResult(0, 1, "testoutput", null);
        PingCommandResult result2 = new PingCommandResult(1, 1, "testoutput", new Exception("Test"));
        pingCommand.setPingResults(Arrays.asList(result1, result2));
        PingCommandResult callResult = pingCommand.call();
        assertEquals(0, callResult.processReturnCode());
        assertEquals(1, callResult.pingCalls());
        assertEquals("testoutput", callResult.output());
        assertNull(callResult.exception());
    }
}
