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

package net.ibbaa.keepitup.service.network;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import net.ibbaa.keepitup.test.mock.TestConnectCommand;
import net.ibbaa.keepitup.test.mock.TestRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@MediumTest
@SuppressWarnings({"SameParameterValue"})
@RunWith(AndroidJUnit4.class)
public class ConnectCommandTest {

    private void prepareSuccessfulConnectionResultList(TestConnectCommand connectCommand, int number) {
        List<TestConnectCommand.MockConnectionResult> connectionResultList = new ArrayList<>(number);
        for (int ii = 0; ii < number; ii++) {
            connectionResultList.add(new TestConnectCommand.MockConnectionResult(true, 3, null));
        }
        connectCommand.setConnectionResults(connectionResultList);
    }

    private void prepareTimeoutConnectionResultList(TestConnectCommand connectCommand, int number) {
        List<TestConnectCommand.MockConnectionResult> connectionResultList = new ArrayList<>(number);
        for (int ii = 0; ii < number; ii++) {
            connectionResultList.add(new TestConnectCommand.MockConnectionResult(false, 3, null));
        }
        connectCommand.setConnectionResults(connectionResultList);
    }

    private void prepareExcpetionConnectionResultList(TestConnectCommand connectCommand, int number) {
        List<TestConnectCommand.MockConnectionResult> connectionResultList = new ArrayList<>(number);
        for (int ii = 0; ii < number; ii++) {
            connectionResultList.add(new TestConnectCommand.MockConnectionResult(false, 3, new IOException("Error " + (ii + 1))));
        }
        connectCommand.setConnectionResults(connectionResultList);
    }

    @Test
    public void testAllConnectionAttemptsSuccessful() {
        TestConnectCommand connectCommand = new TestConnectCommand(TestRegistry.getContext(), null, 80, 3, false);
        prepareSuccessfulConnectionResultList(connectCommand, 3);
        ConnectCommandResult result = connectCommand.call();
        assertTrue(result.success());
        assertEquals(3, result.attempts());
        assertEquals(3, result.successfulAttempts());
        assertEquals(0, result.timeoutAttempts());
        assertEquals(0, result.errorAttempts());
        assertEquals(3.0, result.averageTime(), 0.01);
        assertNull(result.exception());
    }

    @Test
    public void testAllConnectionAttemptsSuccessfulStopOnSuccess() {
        TestConnectCommand connectCommand = new TestConnectCommand(TestRegistry.getContext(), null, 80, 3, true);
        prepareSuccessfulConnectionResultList(connectCommand, 3);
        ConnectCommandResult result = connectCommand.call();
        assertTrue(result.success());
        assertEquals(1, result.attempts());
        assertEquals(1, result.successfulAttempts());
        assertEquals(0, result.timeoutAttempts());
        assertEquals(0, result.errorAttempts());
        assertEquals(3.0, result.averageTime(), 0.01);
        assertNull(result.exception());
    }

    @Test
    public void testAllConnectionAttemptsTimeout() {
        TestConnectCommand connectCommand = new TestConnectCommand(TestRegistry.getContext(), null, 80, 3, false);
        prepareTimeoutConnectionResultList(connectCommand, 3);
        ConnectCommandResult result = connectCommand.call();
        assertFalse(result.success());
        assertEquals(3, result.attempts());
        assertEquals(0, result.successfulAttempts());
        assertEquals(3, result.timeoutAttempts());
        assertEquals(0, result.errorAttempts());
        assertEquals(0, result.averageTime(), 0.01);
        assertNull(result.exception());
    }

    @Test
    public void testAllConnectionAttemptsTimeoutStopOnSuccess() {
        TestConnectCommand connectCommand = new TestConnectCommand(TestRegistry.getContext(), null, 80, 3, true);
        prepareTimeoutConnectionResultList(connectCommand, 3);
        ConnectCommandResult result = connectCommand.call();
        assertFalse(result.success());
        assertEquals(3, result.attempts());
        assertEquals(0, result.successfulAttempts());
        assertEquals(3, result.timeoutAttempts());
        assertEquals(0, result.errorAttempts());
        assertEquals(0, result.averageTime(), 0.01);
        assertNull(result.exception());
    }

    @Test
    public void testAllConnectionAttemptsException() {
        TestConnectCommand connectCommand = new TestConnectCommand(TestRegistry.getContext(), null, 80, 3, false);
        prepareExcpetionConnectionResultList(connectCommand, 3);
        ConnectCommandResult result = connectCommand.call();
        assertFalse(result.success());
        assertEquals(3, result.attempts());
        assertEquals(0, result.successfulAttempts());
        assertEquals(0, result.timeoutAttempts());
        assertEquals(3, result.errorAttempts());
        assertEquals(0, result.averageTime(), 0.01);
        assertEquals("Error 3", result.exception().getMessage());
    }

    @Test
    public void testAllConnectionAttemptsExceptionStopOnSuccess() {
        TestConnectCommand connectCommand = new TestConnectCommand(TestRegistry.getContext(), null, 80, 3, true);
        prepareExcpetionConnectionResultList(connectCommand, 3);
        ConnectCommandResult result = connectCommand.call();
        assertFalse(result.success());
        assertEquals(3, result.attempts());
        assertEquals(0, result.successfulAttempts());
        assertEquals(0, result.timeoutAttempts());
        assertEquals(3, result.errorAttempts());
        assertEquals(0, result.averageTime(), 0.01);
        assertEquals("Error 3", result.exception().getMessage());
    }

    @Test
    public void testSuccessOneTimeout() {
        TestConnectCommand connectCommand = new TestConnectCommand(TestRegistry.getContext(), null, 80, 3, false);
        List<TestConnectCommand.MockConnectionResult> connectionResultList = new ArrayList<>(3);
        connectionResultList.add(new TestConnectCommand.MockConnectionResult(true, 3, null));
        connectionResultList.add(new TestConnectCommand.MockConnectionResult(false, 3, null));
        connectionResultList.add(new TestConnectCommand.MockConnectionResult(true, 3, null));
        connectCommand.setConnectionResults(connectionResultList);
        ConnectCommandResult result = connectCommand.call();
        assertTrue(result.success());
        assertEquals(3, result.attempts());
        assertEquals(2, result.successfulAttempts());
        assertEquals(1, result.timeoutAttempts());
        assertEquals(0, result.errorAttempts());
        assertEquals(3.0, result.averageTime(), 0.01);
        assertNull(result.exception());
    }

    @Test
    public void testSuccessOneTimeoutStopOnSuccess() {
        TestConnectCommand connectCommand = new TestConnectCommand(TestRegistry.getContext(), null, 80, 3, true);
        List<TestConnectCommand.MockConnectionResult> connectionResultList = new ArrayList<>(3);
        connectionResultList.add(new TestConnectCommand.MockConnectionResult(false, 3, null));
        connectionResultList.add(new TestConnectCommand.MockConnectionResult(true, 3, null));
        connectionResultList.add(new TestConnectCommand.MockConnectionResult(true, 3, null));
        connectCommand.setConnectionResults(connectionResultList);
        ConnectCommandResult result = connectCommand.call();
        assertTrue(result.success());
        assertEquals(2, result.attempts());
        assertEquals(1, result.successfulAttempts());
        assertEquals(1, result.timeoutAttempts());
        assertEquals(0, result.errorAttempts());
        assertEquals(3.0, result.averageTime(), 0.01);
        assertNull(result.exception());
    }

    @Test
    public void testSuccessOneException() {
        TestConnectCommand connectCommand = new TestConnectCommand(TestRegistry.getContext(), null, 80, 3, false);
        List<TestConnectCommand.MockConnectionResult> connectionResultList = new ArrayList<>(3);
        connectionResultList.add(new TestConnectCommand.MockConnectionResult(true, 3, null));
        connectionResultList.add(new TestConnectCommand.MockConnectionResult(false, 3, new IOException("Error")));
        connectionResultList.add(new TestConnectCommand.MockConnectionResult(true, 3, null));
        connectCommand.setConnectionResults(connectionResultList);
        ConnectCommandResult result = connectCommand.call();
        assertTrue(result.success());
        assertEquals(3, result.attempts());
        assertEquals(2, result.successfulAttempts());
        assertEquals(0, result.timeoutAttempts());
        assertEquals(1, result.errorAttempts());
        assertEquals(3.0, result.averageTime(), 0.01);
        assertEquals("Error", result.exception().getMessage());
    }

    @Test
    public void testSuccessOneExceptionStopOnSuccess() {
        TestConnectCommand connectCommand = new TestConnectCommand(TestRegistry.getContext(), null, 80, 3, true);
        List<TestConnectCommand.MockConnectionResult> connectionResultList = new ArrayList<>(3);
        connectionResultList.add(new TestConnectCommand.MockConnectionResult(false, 3, new IOException("Error")));
        connectionResultList.add(new TestConnectCommand.MockConnectionResult(true, 3, null));
        connectionResultList.add(new TestConnectCommand.MockConnectionResult(true, 3, null));
        connectCommand.setConnectionResults(connectionResultList);
        ConnectCommandResult result = connectCommand.call();
        assertTrue(result.success());
        assertEquals(2, result.attempts());
        assertEquals(1, result.successfulAttempts());
        assertEquals(0, result.timeoutAttempts());
        assertEquals(1, result.errorAttempts());
        assertEquals(3.0, result.averageTime(), 0.01);
        assertEquals("Error", result.exception().getMessage());
    }

    @Test
    public void testSuccessMixedResult() {
        TestConnectCommand connectCommand = new TestConnectCommand(TestRegistry.getContext(), null, 80, 6, false);
        List<TestConnectCommand.MockConnectionResult> connectionResultList = new ArrayList<>(3);
        connectionResultList.add(new TestConnectCommand.MockConnectionResult(false, 3, new IOException("Error 1")));
        connectionResultList.add(new TestConnectCommand.MockConnectionResult(false, 3, new IOException("Error 2")));
        connectionResultList.add(new TestConnectCommand.MockConnectionResult(true, 3, null));
        connectionResultList.add(new TestConnectCommand.MockConnectionResult(false, 3, null));
        connectionResultList.add(new TestConnectCommand.MockConnectionResult(false, 3, new IOException("Error 3")));
        connectionResultList.add(new TestConnectCommand.MockConnectionResult(true, 3, null));
        connectCommand.setConnectionResults(connectionResultList);
        ConnectCommandResult result = connectCommand.call();
        assertTrue(result.success());
        assertEquals(6, result.attempts());
        assertEquals(2, result.successfulAttempts());
        assertEquals(1, result.timeoutAttempts());
        assertEquals(3, result.errorAttempts());
        assertEquals(3.0, result.averageTime(), 0.01);
        assertEquals("Error 3", result.exception().getMessage());
    }

    @Test
    public void testSuccessMixedResultStopOnSuccess() {
        TestConnectCommand connectCommand = new TestConnectCommand(TestRegistry.getContext(), null, 80, 6, true);
        List<TestConnectCommand.MockConnectionResult> connectionResultList = new ArrayList<>(3);
        connectionResultList.add(new TestConnectCommand.MockConnectionResult(false, 3, new IOException("Error 1")));
        connectionResultList.add(new TestConnectCommand.MockConnectionResult(false, 3, new IOException("Error 2")));
        connectionResultList.add(new TestConnectCommand.MockConnectionResult(true, 3, null));
        connectionResultList.add(new TestConnectCommand.MockConnectionResult(false, 3, null));
        connectionResultList.add(new TestConnectCommand.MockConnectionResult(false, 3, new IOException("Error 3")));
        connectionResultList.add(new TestConnectCommand.MockConnectionResult(true, 3, null));
        connectCommand.setConnectionResults(connectionResultList);
        ConnectCommandResult result = connectCommand.call();
        assertTrue(result.success());
        assertEquals(3, result.attempts());
        assertEquals(1, result.successfulAttempts());
        assertEquals(0, result.timeoutAttempts());
        assertEquals(2, result.errorAttempts());
        assertEquals(3.0, result.averageTime(), 0.01);
        assertEquals("Error 2", result.exception().getMessage());
    }

    @Test
    public void testFailureMixedResult() {
        TestConnectCommand connectCommand = new TestConnectCommand(TestRegistry.getContext(), null, 80, 6, false);
        List<TestConnectCommand.MockConnectionResult> connectionResultList = new ArrayList<>(3);
        connectionResultList.add(new TestConnectCommand.MockConnectionResult(false, 3, new IOException("Error 1")));
        connectionResultList.add(new TestConnectCommand.MockConnectionResult(false, 3, new IOException("Error 2")));
        connectionResultList.add(new TestConnectCommand.MockConnectionResult(false, 3, null));
        connectionResultList.add(new TestConnectCommand.MockConnectionResult(false, 3, null));
        connectionResultList.add(new TestConnectCommand.MockConnectionResult(false, 3, new IOException("Error 3")));
        connectionResultList.add(new TestConnectCommand.MockConnectionResult(false, 3, null));
        connectCommand.setConnectionResults(connectionResultList);
        ConnectCommandResult result = connectCommand.call();
        assertFalse(result.success());
        assertEquals(6, result.attempts());
        assertEquals(0, result.successfulAttempts());
        assertEquals(3, result.timeoutAttempts());
        assertEquals(3, result.errorAttempts());
        assertEquals(0, result.averageTime(), 0.01);
        assertEquals("Error 3", result.exception().getMessage());
    }

    @Test
    public void testFailureMixedResultStopOnSuccess() {
        TestConnectCommand connectCommand = new TestConnectCommand(TestRegistry.getContext(), null, 80, 6, true);
        List<TestConnectCommand.MockConnectionResult> connectionResultList = new ArrayList<>(3);
        connectionResultList.add(new TestConnectCommand.MockConnectionResult(false, 3, new IOException("Error 1")));
        connectionResultList.add(new TestConnectCommand.MockConnectionResult(false, 3, new IOException("Error 2")));
        connectionResultList.add(new TestConnectCommand.MockConnectionResult(false, 3, null));
        connectionResultList.add(new TestConnectCommand.MockConnectionResult(false, 3, null));
        connectionResultList.add(new TestConnectCommand.MockConnectionResult(false, 3, new IOException("Error 3")));
        connectionResultList.add(new TestConnectCommand.MockConnectionResult(false, 3, null));
        connectCommand.setConnectionResults(connectionResultList);
        ConnectCommandResult result = connectCommand.call();
        assertFalse(result.success());
        assertEquals(6, result.attempts());
        assertEquals(0, result.successfulAttempts());
        assertEquals(3, result.timeoutAttempts());
        assertEquals(3, result.errorAttempts());
        assertEquals(0, result.averageTime(), 0.01);
        assertEquals("Error 3", result.exception().getMessage());
    }

    @Test
    public void testAverageTime() {
        TestConnectCommand connectCommand = new TestConnectCommand(TestRegistry.getContext(), null, 80, 6, false);
        List<TestConnectCommand.MockConnectionResult> connectionResultList = new ArrayList<>(3);
        connectionResultList.add(new TestConnectCommand.MockConnectionResult(true, 1, null));
        connectionResultList.add(new TestConnectCommand.MockConnectionResult(true, 2, null));
        connectionResultList.add(new TestConnectCommand.MockConnectionResult(true, 3, null));
        connectionResultList.add(new TestConnectCommand.MockConnectionResult(true, 4, null));
        connectionResultList.add(new TestConnectCommand.MockConnectionResult(true, 5, null));
        connectionResultList.add(new TestConnectCommand.MockConnectionResult(true, 6, null));
        connectCommand.setConnectionResults(connectionResultList);
        ConnectCommandResult result = connectCommand.call();
        assertTrue(result.success());
        assertEquals(6, result.attempts());
        assertEquals(6, result.successfulAttempts());
        assertEquals(0, result.timeoutAttempts());
        assertEquals(0, result.errorAttempts());
        assertEquals(3.5, result.averageTime(), 0.01);
        assertNull(result.exception());
    }

    @Test
    public void testAverageTimeStopOnSuccess() {
        TestConnectCommand connectCommand = new TestConnectCommand(TestRegistry.getContext(), null, 80, 6, true);
        List<TestConnectCommand.MockConnectionResult> connectionResultList = new ArrayList<>(3);
        connectionResultList.add(new TestConnectCommand.MockConnectionResult(true, 1, null));
        connectionResultList.add(new TestConnectCommand.MockConnectionResult(true, 2, null));
        connectionResultList.add(new TestConnectCommand.MockConnectionResult(true, 3, null));
        connectionResultList.add(new TestConnectCommand.MockConnectionResult(true, 4, null));
        connectionResultList.add(new TestConnectCommand.MockConnectionResult(true, 5, null));
        connectionResultList.add(new TestConnectCommand.MockConnectionResult(true, 6, null));
        connectCommand.setConnectionResults(connectionResultList);
        ConnectCommandResult result = connectCommand.call();
        assertTrue(result.success());
        assertEquals(1, result.attempts());
        assertEquals(1, result.successfulAttempts());
        assertEquals(0, result.timeoutAttempts());
        assertEquals(0, result.errorAttempts());
        assertEquals(1, result.averageTime(), 0.01);
        assertNull(result.exception());
    }
}
