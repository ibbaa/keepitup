/*
 * Copyright (c) 2021. Alwin Ibba
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

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.ibbaa.keepitup.test.mock.TestConnectCommand;
import net.ibbaa.keepitup.test.mock.TestRegistry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class ConnectCommandTest {

    private void prepareSuccessfulConnectionResultList(TestConnectCommand connectCommand, int number) {
        List<TestConnectCommand.MockConnectionResult> connectionResultList = new ArrayList<>(number);
        for (int ii = 0; ii < number; ii++) {
            connectionResultList.add(connectCommand.new MockConnectionResult(true, 3, null));
        }
        connectCommand.setConnectionResults(connectionResultList);
    }

    private void prepareTimeoutConnectionResultList(TestConnectCommand connectCommand, int number) {
        List<TestConnectCommand.MockConnectionResult> connectionResultList = new ArrayList<>(number);
        for (int ii = 0; ii < number; ii++) {
            connectionResultList.add(connectCommand.new MockConnectionResult(false, 3, null));
        }
        connectCommand.setConnectionResults(connectionResultList);
    }

    private void prepareExcpetionConnectionResultList(TestConnectCommand connectCommand, int number) {
        List<TestConnectCommand.MockConnectionResult> connectionResultList = new ArrayList<>(number);
        for (int ii = 0; ii < number; ii++) {
            connectionResultList.add(connectCommand.new MockConnectionResult(false, 3, new IOException("Error " + (ii + 1))));
        }
        connectCommand.setConnectionResults(connectionResultList);
    }

    @Test
    public void testAllConnectionAttemptsSuccessful() {
        TestConnectCommand connectCommand = new TestConnectCommand(TestRegistry.getContext(), null, 80, 3);
        prepareSuccessfulConnectionResultList(connectCommand, 3);
        ConnectCommandResult result = connectCommand.call();
        assertTrue(result.isSuccess());
        assertEquals(3, result.getAttempts());
        assertEquals(3, result.getSuccessfulAttempts());
        assertEquals(0, result.getTimeoutAttempts());
        assertEquals(3.0, result.getAverageTime(), 0.01);
        assertNull(result.getException());
    }

    @Test
    public void testAllConnectionAttemptsTimeout() {
        TestConnectCommand connectCommand = new TestConnectCommand(TestRegistry.getContext(), null, 80, 3);
        prepareTimeoutConnectionResultList(connectCommand, 3);
        ConnectCommandResult result = connectCommand.call();
        assertFalse(result.isSuccess());
        assertEquals(3, result.getAttempts());
        assertEquals(0, result.getSuccessfulAttempts());
        assertEquals(3, result.getTimeoutAttempts());
        assertEquals(0, result.getAverageTime(), 0.01);
        assertNull(result.getException());
    }

    @Test
    public void testAllConnectionAttemptsException() {
        TestConnectCommand connectCommand = new TestConnectCommand(TestRegistry.getContext(), null, 80, 3);
        prepareExcpetionConnectionResultList(connectCommand, 3);
        ConnectCommandResult result = connectCommand.call();
        assertFalse(result.isSuccess());
        assertEquals(3, result.getAttempts());
        assertEquals(0, result.getSuccessfulAttempts());
        assertEquals(0, result.getTimeoutAttempts());
        assertEquals(0, result.getAverageTime(), 0.01);
        assertEquals("Error 3", result.getException().getMessage());
    }

    @Test
    public void testSuccessOneTimeout() {
        TestConnectCommand connectCommand = new TestConnectCommand(TestRegistry.getContext(), null, 80, 3);
        List<TestConnectCommand.MockConnectionResult> connectionResultList = new ArrayList<>(3);
        connectionResultList.add(connectCommand.new MockConnectionResult(true, 3, null));
        connectionResultList.add(connectCommand.new MockConnectionResult(false, 3, null));
        connectionResultList.add(connectCommand.new MockConnectionResult(true, 3, null));
        connectCommand.setConnectionResults(connectionResultList);
        ConnectCommandResult result = connectCommand.call();
        assertTrue(result.isSuccess());
        assertEquals(3, result.getAttempts());
        assertEquals(2, result.getSuccessfulAttempts());
        assertEquals(1, result.getTimeoutAttempts());
        assertEquals(3.0, result.getAverageTime(), 0.01);
        assertNull(result.getException());
    }

    @Test
    public void testSuccessOneException() {
        TestConnectCommand connectCommand = new TestConnectCommand(TestRegistry.getContext(), null, 80, 3);
        List<TestConnectCommand.MockConnectionResult> connectionResultList = new ArrayList<>(3);
        connectionResultList.add(connectCommand.new MockConnectionResult(true, 3, null));
        connectionResultList.add(connectCommand.new MockConnectionResult(false, 3, new IOException("Error")));
        connectionResultList.add(connectCommand.new MockConnectionResult(true, 3, null));
        connectCommand.setConnectionResults(connectionResultList);
        ConnectCommandResult result = connectCommand.call();
        assertTrue(result.isSuccess());
        assertEquals(3, result.getAttempts());
        assertEquals(2, result.getSuccessfulAttempts());
        assertEquals(0, result.getTimeoutAttempts());
        assertEquals(3.0, result.getAverageTime(), 0.01);
        assertEquals("Error", result.getException().getMessage());
    }

    @Test
    public void testSuccessMixedResult() {
        TestConnectCommand connectCommand = new TestConnectCommand(TestRegistry.getContext(), null, 80, 6);
        List<TestConnectCommand.MockConnectionResult> connectionResultList = new ArrayList<>(3);
        connectionResultList.add(connectCommand.new MockConnectionResult(false, 3, new IOException("Error 1")));
        connectionResultList.add(connectCommand.new MockConnectionResult(false, 3, new IOException("Error 2")));
        connectionResultList.add(connectCommand.new MockConnectionResult(true, 3, null));
        connectionResultList.add(connectCommand.new MockConnectionResult(false, 3, null));
        connectionResultList.add(connectCommand.new MockConnectionResult(false, 3, new IOException("Error 3")));
        connectionResultList.add(connectCommand.new MockConnectionResult(true, 3, null));
        connectCommand.setConnectionResults(connectionResultList);
        ConnectCommandResult result = connectCommand.call();
        assertTrue(result.isSuccess());
        assertEquals(6, result.getAttempts());
        assertEquals(2, result.getSuccessfulAttempts());
        assertEquals(1, result.getTimeoutAttempts());
        assertEquals(3.0, result.getAverageTime(), 0.01);
        assertEquals("Error 3", result.getException().getMessage());
    }

    @Test
    public void testFailureMixedResult() {
        TestConnectCommand connectCommand = new TestConnectCommand(TestRegistry.getContext(), null, 80, 6);
        List<TestConnectCommand.MockConnectionResult> connectionResultList = new ArrayList<>(3);
        connectionResultList.add(connectCommand.new MockConnectionResult(false, 3, new IOException("Error 1")));
        connectionResultList.add(connectCommand.new MockConnectionResult(false, 3, new IOException("Error 2")));
        connectionResultList.add(connectCommand.new MockConnectionResult(false, 3, null));
        connectionResultList.add(connectCommand.new MockConnectionResult(false, 3, null));
        connectionResultList.add(connectCommand.new MockConnectionResult(false, 3, new IOException("Error 3")));
        connectionResultList.add(connectCommand.new MockConnectionResult(false, 3, null));
        connectCommand.setConnectionResults(connectionResultList);
        ConnectCommandResult result = connectCommand.call();
        assertFalse(result.isSuccess());
        assertEquals(6, result.getAttempts());
        assertEquals(0, result.getSuccessfulAttempts());
        assertEquals(3, result.getTimeoutAttempts());
        assertEquals(0, result.getAverageTime(), 0.01);
        assertEquals("Error 3", result.getException().getMessage());
    }

    @Test
    public void testAverageTime() {
        TestConnectCommand connectCommand = new TestConnectCommand(TestRegistry.getContext(), null, 80, 6);
        List<TestConnectCommand.MockConnectionResult> connectionResultList = new ArrayList<>(3);
        connectionResultList.add(connectCommand.new MockConnectionResult(true, 1, null));
        connectionResultList.add(connectCommand.new MockConnectionResult(true, 2, null));
        connectionResultList.add(connectCommand.new MockConnectionResult(true, 3, null));
        connectionResultList.add(connectCommand.new MockConnectionResult(true, 4, null));
        connectionResultList.add(connectCommand.new MockConnectionResult(true, 5, null));
        connectionResultList.add(connectCommand.new MockConnectionResult(true, 6, null));
        connectCommand.setConnectionResults(connectionResultList);
        ConnectCommandResult result = connectCommand.call();
        assertTrue(result.isSuccess());
        assertEquals(6, result.getAttempts());
        assertEquals(6, result.getSuccessfulAttempts());
        assertEquals(0, result.getTimeoutAttempts());
        assertEquals(3.5, result.getAverageTime(), 0.01);
        assertNull(result.getException());
    }
}
