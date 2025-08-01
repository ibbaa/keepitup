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

package net.ibbaa.keepitup.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import androidx.documentfile.provider.DocumentFile;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import net.ibbaa.keepitup.db.AccessTypeDataDAO;
import net.ibbaa.keepitup.db.LogDAO;
import net.ibbaa.keepitup.db.NetworkTaskDAO;
import net.ibbaa.keepitup.model.AccessType;
import net.ibbaa.keepitup.model.AccessTypeData;
import net.ibbaa.keepitup.model.LogEntry;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.model.NotificationType;
import net.ibbaa.keepitup.notification.NotificationHandler;
import net.ibbaa.keepitup.resources.PreferenceManager;
import net.ibbaa.keepitup.service.network.DNSLookupResult;
import net.ibbaa.keepitup.test.mock.MockDNSLookup;
import net.ibbaa.keepitup.test.mock.MockDocumentManager;
import net.ibbaa.keepitup.test.mock.MockNetworkManager;
import net.ibbaa.keepitup.test.mock.MockNotificationManager;
import net.ibbaa.keepitup.test.mock.MockStoragePermissionManager;
import net.ibbaa.keepitup.test.mock.MockTimeService;
import net.ibbaa.keepitup.test.mock.TestNetworkTaskWorker;
import net.ibbaa.keepitup.test.mock.TestRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class NetworkTaskWorkerTest {

    private NetworkTaskDAO networkTaskDAO;
    private AccessTypeDataDAO accessTypeDataDAO;
    private LogDAO logDAO;
    private PreferenceManager preferenceManager;

    @Before
    public void beforeEachTestMethod() {
        networkTaskDAO = new NetworkTaskDAO(TestRegistry.getContext());
        networkTaskDAO.deleteAllNetworkTasks();
        accessTypeDataDAO = new AccessTypeDataDAO(TestRegistry.getContext());
        accessTypeDataDAO.deleteAllAccessTypeData();
        logDAO = new LogDAO(TestRegistry.getContext());
        logDAO.deleteAllLogs();
        preferenceManager = new PreferenceManager(TestRegistry.getContext());
        preferenceManager.removeAllPreferences();
    }

    @After
    public void afterEachTestMethod() {
        networkTaskDAO.deleteAllNetworkTasks();
        accessTypeDataDAO.deleteAllAccessTypeData();
        logDAO.deleteAllLogs();
        preferenceManager.removeAllPreferences();
    }

    @Test
    public void testSuccessfulExecution() {
        NetworkTask task = getNetworkTask();
        task = networkTaskDAO.insertNetworkTask(task);
        AccessTypeData data = getAccessTypeDataWithNetworkTaskId(task.getId());
        data = accessTypeDataDAO.insertAccessTypeData(data);
        TestNetworkTaskWorker testNetworkTaskWorker = new TestNetworkTaskWorker(TestRegistry.getContext(), task, null, true);
        setCurrentTime(testNetworkTaskWorker);
        MockNetworkManager networkManager = (MockNetworkManager) testNetworkTaskWorker.getNetworkManager();
        networkManager.setConnected(true);
        networkManager.setConnectedWithWiFi(true);
        testNetworkTaskWorker.run();
        List<LogEntry> entries = logDAO.readAllLogsForNetworkTask(task.getId());
        assertEquals(1, entries.size());
        LogEntry entry = entries.get(0);
        assertEquals(task.getId(), entry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), entry.getTimestamp());
        assertLastScheduledInDatabase(task, getTestTimestamp());
        assertTrue(entry.isSuccess());
        assertEquals("successful", entry.getMessage());
        assertTrue(task.isTechnicallyEqual(testNetworkTaskWorker.getExecuteTask()));
        assertTrue(data.isTechnicallyEqual(testNetworkTaskWorker.getExecuteData()));
        NetworkTask readTask = networkTaskDAO.readNetworkTask(task.getId());
        assertEquals(0, readTask.getFailureCount());
        NotificationHandler notificationHandler = testNetworkTaskWorker.getNotificationHandler();
        MockNotificationManager notificationManager = (MockNotificationManager) notificationHandler.getNotificationManager();
        assertFalse(notificationManager.wasNotifyCalled());
    }

    @Test
    public void testSuccessfulExecutionNoLogPermission() {
        preferenceManager.setPreferenceAllowArbitraryFileLocation(true);
        preferenceManager.setPreferenceLogFile(true);
        preferenceManager.setPreferenceArbitraryLogFolder("Test");
        NetworkTask task = getNetworkTask();
        task = networkTaskDAO.insertNetworkTask(task);
        AccessTypeData data = getAccessTypeDataWithNetworkTaskId(task.getId());
        accessTypeDataDAO.insertAccessTypeData(data);
        TestNetworkTaskWorker testNetworkTaskWorker = new TestNetworkTaskWorker(TestRegistry.getContext(), task, null, true);
        setCurrentTime(testNetworkTaskWorker);
        MockDocumentManager documentManager = new MockDocumentManager();
        testNetworkTaskWorker.setDocumentManager(documentManager);
        documentManager.setArbitraryDirectory(DocumentFile.fromFile(new File("Test")));
        MockStoragePermissionManager storagePermissionManager = new MockStoragePermissionManager();
        testNetworkTaskWorker.setStoragePermissionManager(storagePermissionManager);
        storagePermissionManager.requestPersistentFolderPermission(null, "Movies");
        MockNetworkManager networkManager = (MockNetworkManager) testNetworkTaskWorker.getNetworkManager();
        networkManager.setConnected(true);
        networkManager.setConnectedWithWiFi(true);
        testNetworkTaskWorker.run();
        NotificationHandler notificationHandler = testNetworkTaskWorker.getNotificationHandler();
        MockNotificationManager notificationManager = (MockNotificationManager) notificationHandler.getNotificationManager();
        assertTrue(notificationManager.wasNotifyCalled());
    }

    @Test
    public void testSuccessfulExecutionNoLogPermissionInvalidFolder() {
        preferenceManager.setPreferenceAllowArbitraryFileLocation(true);
        preferenceManager.setPreferenceLogFile(true);
        preferenceManager.setPreferenceArbitraryLogFolder("Test");
        NetworkTask task = getNetworkTask();
        task = networkTaskDAO.insertNetworkTask(task);
        AccessTypeData data = getAccessTypeDataWithNetworkTaskId(task.getId());
        accessTypeDataDAO.insertAccessTypeData(data);
        TestNetworkTaskWorker testNetworkTaskWorker = new TestNetworkTaskWorker(TestRegistry.getContext(), task, null, true);
        setCurrentTime(testNetworkTaskWorker);
        MockDocumentManager documentManager = new MockDocumentManager();
        testNetworkTaskWorker.setDocumentManager(documentManager);
        documentManager.setArbitraryDirectory(null);
        MockStoragePermissionManager storagePermissionManager = new MockStoragePermissionManager();
        testNetworkTaskWorker.setStoragePermissionManager(storagePermissionManager);
        storagePermissionManager.requestPersistentFolderPermission(null, "Movies");
        MockNetworkManager networkManager = (MockNetworkManager) testNetworkTaskWorker.getNetworkManager();
        networkManager.setConnected(true);
        networkManager.setConnectedWithWiFi(true);
        testNetworkTaskWorker.run();
        NotificationHandler notificationHandler = testNetworkTaskWorker.getNotificationHandler();
        MockNotificationManager notificationManager = (MockNotificationManager) notificationHandler.getNotificationManager();
        assertTrue(notificationManager.wasNotifyCalled());
    }

    @Test
    public void testSuccessfulExecutionWithLogPermission() {
        preferenceManager.setPreferenceAllowArbitraryFileLocation(true);
        preferenceManager.setPreferenceLogFile(true);
        preferenceManager.setPreferenceArbitraryLogFolder("Movies");
        NetworkTask task = getNetworkTask();
        task = networkTaskDAO.insertNetworkTask(task);
        AccessTypeData data = getAccessTypeDataWithNetworkTaskId(task.getId());
        accessTypeDataDAO.insertAccessTypeData(data);
        TestNetworkTaskWorker testNetworkTaskWorker = new TestNetworkTaskWorker(TestRegistry.getContext(), task, null, true);
        setCurrentTime(testNetworkTaskWorker);
        MockDocumentManager documentManager = new MockDocumentManager();
        testNetworkTaskWorker.setDocumentManager(documentManager);
        documentManager.setArbitraryDirectory(DocumentFile.fromFile(new File("Test")));
        MockStoragePermissionManager storagePermissionManager = new MockStoragePermissionManager();
        testNetworkTaskWorker.setStoragePermissionManager(storagePermissionManager);
        storagePermissionManager.requestPersistentFolderPermission(null, "Movies");
        MockNetworkManager networkManager = (MockNetworkManager) testNetworkTaskWorker.getNetworkManager();
        networkManager.setConnected(true);
        networkManager.setConnectedWithWiFi(true);
        testNetworkTaskWorker.run();
        NotificationHandler notificationHandler = testNetworkTaskWorker.getNotificationHandler();
        MockNotificationManager notificationManager = (MockNotificationManager) notificationHandler.getNotificationManager();
        assertFalse(notificationManager.wasNotifyCalled());
    }

    @Test
    public void testSuccessfulExecutionAccessTypeDataNotFound() {
        NetworkTask task = getNetworkTask();
        task = networkTaskDAO.insertNetworkTask(task);
        TestNetworkTaskWorker testNetworkTaskWorker = new TestNetworkTaskWorker(TestRegistry.getContext(), task, null, true);
        setCurrentTime(testNetworkTaskWorker);
        MockNetworkManager networkManager = (MockNetworkManager) testNetworkTaskWorker.getNetworkManager();
        networkManager.setConnected(true);
        networkManager.setConnectedWithWiFi(true);
        testNetworkTaskWorker.run();
        assertTrue(task.isTechnicallyEqual(testNetworkTaskWorker.getExecuteTask()));
        assertTrue(new AccessTypeData(TestRegistry.getContext()).isTechnicallyEqual(testNetworkTaskWorker.getExecuteData()));
    }

    @Test
    public void testSuccessfulExecutionLastSuccessOnFailure() {
        preferenceManager.setPreferenceNotificationType(NotificationType.FAILURE);
        NetworkTask task = getNetworkTask();
        task = networkTaskDAO.insertNetworkTask(task);
        accessTypeDataDAO.insertAccessTypeData(getAccessTypeDataWithNetworkTaskId(task.getId()));
        logDAO.insertAndDeleteLog(getLogEntry(task.getId(), true));
        TestNetworkTaskWorker testNetworkTaskWorker = new TestNetworkTaskWorker(TestRegistry.getContext(), task, null, true);
        setCurrentTime(testNetworkTaskWorker);
        MockNetworkManager networkManager = (MockNetworkManager) testNetworkTaskWorker.getNetworkManager();
        networkManager.setConnected(true);
        networkManager.setConnectedWithWiFi(true);
        testNetworkTaskWorker.run();
        NetworkTask readTask = networkTaskDAO.readNetworkTask(task.getId());
        assertEquals(0, readTask.getFailureCount());
        NotificationHandler notificationHandler = testNetworkTaskWorker.getNotificationHandler();
        MockNotificationManager notificationManager = (MockNotificationManager) notificationHandler.getNotificationManager();
        assertFalse(notificationManager.wasNotifyCalled());
    }

    @Test
    public void testSuccessfulExecutionLastSuccessOnChange() {
        preferenceManager.setPreferenceNotificationType(NotificationType.CHANGE);
        NetworkTask task = getNetworkTask();
        task = networkTaskDAO.insertNetworkTask(task);
        accessTypeDataDAO.insertAccessTypeData(getAccessTypeDataWithNetworkTaskId(task.getId()));
        logDAO.insertAndDeleteLog(getLogEntry(task.getId(), true));
        TestNetworkTaskWorker testNetworkTaskWorker = new TestNetworkTaskWorker(TestRegistry.getContext(), task, null, true);
        setCurrentTime(testNetworkTaskWorker);
        MockNetworkManager networkManager = (MockNetworkManager) testNetworkTaskWorker.getNetworkManager();
        networkManager.setConnected(true);
        networkManager.setConnectedWithWiFi(true);
        testNetworkTaskWorker.run();
        NetworkTask readTask = networkTaskDAO.readNetworkTask(task.getId());
        assertEquals(0, readTask.getFailureCount());
        NotificationHandler notificationHandler = testNetworkTaskWorker.getNotificationHandler();
        MockNotificationManager notificationManager = (MockNotificationManager) notificationHandler.getNotificationManager();
        assertFalse(notificationManager.wasNotifyCalled());
    }

    @Test
    public void testSuccessfulExecutionLastFailureOnChange() {
        preferenceManager.setPreferenceNotificationType(NotificationType.CHANGE);
        NetworkTask task = getNetworkTask();
        task = networkTaskDAO.insertNetworkTask(task);
        accessTypeDataDAO.insertAccessTypeData(getAccessTypeDataWithNetworkTaskId(task.getId()));
        logDAO.insertAndDeleteLog(getLogEntry(task.getId(), false));
        networkTaskDAO.increaseNetworkTaskFailureCount(task.getId());
        TestNetworkTaskWorker testNetworkTaskWorker = new TestNetworkTaskWorker(TestRegistry.getContext(), task, null, true);
        setCurrentTime(testNetworkTaskWorker);
        MockNetworkManager networkManager = (MockNetworkManager) testNetworkTaskWorker.getNetworkManager();
        networkManager.setConnected(true);
        networkManager.setConnectedWithWiFi(true);
        testNetworkTaskWorker.run();
        NetworkTask readTask = networkTaskDAO.readNetworkTask(task.getId());
        assertEquals(0, readTask.getFailureCount());
        NotificationHandler notificationHandler = testNetworkTaskWorker.getNotificationHandler();
        MockNotificationManager notificationManager = (MockNotificationManager) notificationHandler.getNotificationManager();
        assertTrue(notificationManager.wasNotifyCalled());
    }

    @Test
    public void testSuccessfulExecutionLastFailureOnFailure() {
        preferenceManager.setPreferenceNotificationType(NotificationType.FAILURE);
        NetworkTask task = getNetworkTask();
        task = networkTaskDAO.insertNetworkTask(task);
        accessTypeDataDAO.insertAccessTypeData(getAccessTypeDataWithNetworkTaskId(task.getId()));
        logDAO.insertAndDeleteLog(getLogEntry(task.getId(), false));
        networkTaskDAO.increaseNetworkTaskFailureCount(task.getId());
        TestNetworkTaskWorker testNetworkTaskWorker = new TestNetworkTaskWorker(TestRegistry.getContext(), task, null, true);
        setCurrentTime(testNetworkTaskWorker);
        MockNetworkManager networkManager = (MockNetworkManager) testNetworkTaskWorker.getNetworkManager();
        networkManager.setConnected(true);
        networkManager.setConnectedWithWiFi(true);
        testNetworkTaskWorker.run();
        NetworkTask readTask = networkTaskDAO.readNetworkTask(task.getId());
        assertEquals(0, readTask.getFailureCount());
        NotificationHandler notificationHandler = testNetworkTaskWorker.getNotificationHandler();
        MockNotificationManager notificationManager = (MockNotificationManager) notificationHandler.getNotificationManager();
        assertFalse(notificationManager.wasNotifyCalled());
    }

    @Test
    public void testSuccessfulExecutionNumberInstances() {
        NetworkTask task = getNetworkTask();
        task = networkTaskDAO.insertNetworkTask(task);
        accessTypeDataDAO.insertAccessTypeData(getAccessTypeDataWithNetworkTaskId(task.getId()));
        TestNetworkTaskWorker testNetworkTaskWorker = new TestNetworkTaskWorker(TestRegistry.getContext(), task, null, true);
        setCurrentTime(testNetworkTaskWorker);
        MockNetworkManager networkManager = (MockNetworkManager) testNetworkTaskWorker.getNetworkManager();
        networkManager.setConnected(true);
        networkManager.setConnectedWithWiFi(true);
        int activeInstances = networkTaskDAO.readNetworkTaskInstances(task.getId());
        assertEquals(0, activeInstances);
        testNetworkTaskWorker.run();
        assertEquals(1, testNetworkTaskWorker.getInstancesOnExecute());
        activeInstances = networkTaskDAO.readNetworkTaskInstances(task.getId());
        assertEquals(0, activeInstances);
        assertLastScheduledInDatabase(task, getTestTimestamp());
    }

    @Test
    public void testTooManyInstances() {
        NetworkTask task = getNetworkTask();
        task = networkTaskDAO.insertNetworkTask(task);
        accessTypeDataDAO.insertAccessTypeData(getAccessTypeDataWithNetworkTaskId(task.getId()));
        networkTaskDAO.increaseNetworkTaskInstances(task.getId());
        TestNetworkTaskWorker testNetworkTaskWorker = new TestNetworkTaskWorker(TestRegistry.getContext(), task, null, true, 1);
        setCurrentTime(testNetworkTaskWorker);
        MockNetworkManager networkManager = (MockNetworkManager) testNetworkTaskWorker.getNetworkManager();
        networkManager.setConnected(true);
        networkManager.setConnectedWithWiFi(true);
        testNetworkTaskWorker.run();
        List<LogEntry> entries = logDAO.readAllLogsForNetworkTask(task.getId());
        assertEquals(1, entries.size());
        LogEntry entry = entries.get(0);
        assertEquals(task.getId(), entry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), entry.getTimestamp());
        assertLastScheduledInDatabase(task, getTestTimestamp());
        assertFalse(entry.isSuccess());
        assertEquals("TestMaxInstancesError 1", entry.getMessage());
        NotificationHandler notificationHandler = testNetworkTaskWorker.getNotificationHandler();
        MockNotificationManager notificationManager = (MockNotificationManager) notificationHandler.getNotificationManager();
        assertFalse(notificationManager.wasNotifyCalled());
    }

    @Test
    public void testInterrupted() {
        NetworkTask task = getNetworkTask();
        task = networkTaskDAO.insertNetworkTask(task);
        accessTypeDataDAO.insertAccessTypeData(getAccessTypeDataWithNetworkTaskId(task.getId()));
        TestNetworkTaskWorker testNetworkTaskWorker = new TestNetworkTaskWorker(TestRegistry.getContext(), task, null, false, 10, true);
        setCurrentTime(testNetworkTaskWorker);
        MockNetworkManager networkManager = (MockNetworkManager) testNetworkTaskWorker.getNetworkManager();
        networkManager.setConnected(true);
        networkManager.setConnectedWithWiFi(true);
        testNetworkTaskWorker.run();
        List<LogEntry> entries = logDAO.readAllLogsForNetworkTask(task.getId());
        assertEquals(1, entries.size());
        LogEntry entry = entries.get(0);
        assertEquals(task.getId(), entry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), entry.getTimestamp());
        assertLastScheduledInDatabase(task, getTestTimestamp());
        assertFalse(entry.isSuccess());
        assertEquals("failed", entry.getMessage());
        NetworkTask readTask = networkTaskDAO.readNetworkTask(task.getId());
        assertEquals(0, readTask.getFailureCount());
        NotificationHandler notificationHandler = testNetworkTaskWorker.getNotificationHandler();
        MockNotificationManager notificationManager = (MockNotificationManager) notificationHandler.getNotificationManager();
        assertFalse(notificationManager.wasNotifyCalled());
    }

    @Test
    public void testInterruptedLastFailureOnChange() {
        preferenceManager.setPreferenceNotificationType(NotificationType.CHANGE);
        NetworkTask task = getNetworkTask();
        task = networkTaskDAO.insertNetworkTask(task);
        accessTypeDataDAO.insertAccessTypeData(getAccessTypeDataWithNetworkTaskId(task.getId()));
        logDAO.insertAndDeleteLog(getLogEntry(task.getId(), false));
        networkTaskDAO.increaseNetworkTaskFailureCount(task.getId());
        TestNetworkTaskWorker testNetworkTaskWorker = new TestNetworkTaskWorker(TestRegistry.getContext(), task, null, false, 10, true);
        setCurrentTime(testNetworkTaskWorker);
        MockNetworkManager networkManager = (MockNetworkManager) testNetworkTaskWorker.getNetworkManager();
        networkManager.setConnected(true);
        networkManager.setConnectedWithWiFi(true);
        testNetworkTaskWorker.run();
        NetworkTask readTask = networkTaskDAO.readNetworkTask(task.getId());
        assertEquals(1, readTask.getFailureCount());
        NotificationHandler notificationHandler = testNetworkTaskWorker.getNotificationHandler();
        MockNotificationManager notificationManager = (MockNotificationManager) notificationHandler.getNotificationManager();
        assertFalse(notificationManager.wasNotifyCalled());
    }

    @Test
    public void testInterruptedLastFailureOnFailure() {
        preferenceManager.setPreferenceNotificationType(NotificationType.FAILURE);
        NetworkTask task = getNetworkTask();
        task = networkTaskDAO.insertNetworkTask(task);
        accessTypeDataDAO.insertAccessTypeData(getAccessTypeDataWithNetworkTaskId(task.getId()));
        logDAO.insertAndDeleteLog(getLogEntry(task.getId(), false));
        networkTaskDAO.increaseNetworkTaskFailureCount(task.getId());
        TestNetworkTaskWorker testNetworkTaskWorker = new TestNetworkTaskWorker(TestRegistry.getContext(), task, null, false, 10, true);
        setCurrentTime(testNetworkTaskWorker);
        MockNetworkManager networkManager = (MockNetworkManager) testNetworkTaskWorker.getNetworkManager();
        networkManager.setConnected(true);
        networkManager.setConnectedWithWiFi(true);
        testNetworkTaskWorker.run();
        NetworkTask readTask = networkTaskDAO.readNetworkTask(task.getId());
        assertEquals(1, readTask.getFailureCount());
        NotificationHandler notificationHandler = testNetworkTaskWorker.getNotificationHandler();
        MockNotificationManager notificationManager = (MockNotificationManager) notificationHandler.getNotificationManager();
        assertFalse(notificationManager.wasNotifyCalled());
    }

    @Test
    public void testFailureWithNotification() {
        NetworkTask task = getNetworkTask();
        networkTaskDAO.insertNetworkTask(task);
        accessTypeDataDAO.insertAccessTypeData(getAccessTypeDataWithNetworkTaskId(task.getId()));
        TestNetworkTaskWorker testNetworkTaskWorker = new TestNetworkTaskWorker(TestRegistry.getContext(), task, null, false);
        setCurrentTime(testNetworkTaskWorker);
        MockNetworkManager networkManager = (MockNetworkManager) testNetworkTaskWorker.getNetworkManager();
        networkManager.setConnected(true);
        networkManager.setConnectedWithWiFi(true);
        testNetworkTaskWorker.run();
        List<LogEntry> entries = logDAO.readAllLogsForNetworkTask(task.getId());
        assertEquals(1, entries.size());
        LogEntry entry = entries.get(0);
        assertEquals(task.getId(), entry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), entry.getTimestamp());
        assertLastScheduledInDatabase(task, getTestTimestamp());
        assertFalse(entry.isSuccess());
        assertEquals("failed", entry.getMessage());
        NetworkTask readTask = networkTaskDAO.readNetworkTask(task.getId());
        assertEquals(1, readTask.getFailureCount());
        NotificationHandler notificationHandler = testNetworkTaskWorker.getNotificationHandler();
        MockNotificationManager notificationManager = (MockNotificationManager) notificationHandler.getNotificationManager();
        assertTrue(notificationManager.wasNotifyCalled());
    }

    @Test
    public void testFailureWithNotificationAfter5Failures() {
        preferenceManager.setPreferenceNotificationAfterFailures(5);
        NetworkTask task = getNetworkTask();
        networkTaskDAO.insertNetworkTask(task);
        accessTypeDataDAO.insertAccessTypeData(getAccessTypeDataWithNetworkTaskId(task.getId()));
        TestNetworkTaskWorker testNetworkTaskWorker = new TestNetworkTaskWorker(TestRegistry.getContext(), task, null, false);
        setCurrentTime(testNetworkTaskWorker);
        MockNetworkManager networkManager = (MockNetworkManager) testNetworkTaskWorker.getNetworkManager();
        networkManager.setConnected(true);
        networkManager.setConnectedWithWiFi(true);
        testNetworkTaskWorker.run();
        testNetworkTaskWorker.run();
        testNetworkTaskWorker.run();
        testNetworkTaskWorker.run();
        assertEquals(4, logDAO.readAllLogsForNetworkTask(task.getId()).size());
        NetworkTask readTask = networkTaskDAO.readNetworkTask(task.getId());
        assertEquals(4, readTask.getFailureCount());
        NotificationHandler notificationHandler = testNetworkTaskWorker.getNotificationHandler();
        MockNotificationManager notificationManager = (MockNotificationManager) notificationHandler.getNotificationManager();
        assertFalse(notificationManager.wasNotifyCalled());
        testNetworkTaskWorker.run();
        assertEquals(5, logDAO.readAllLogsForNetworkTask(task.getId()).size());
        readTask = networkTaskDAO.readNetworkTask(task.getId());
        assertEquals(5, readTask.getFailureCount());
        assertTrue(notificationManager.wasNotifyCalled());
    }

    @Test
    public void testFailureWithNotificationAfter2Failures() {
        preferenceManager.setPreferenceNotificationAfterFailures(2);
        NetworkTask task = getNetworkTask();
        networkTaskDAO.insertNetworkTask(task);
        accessTypeDataDAO.insertAccessTypeData(getAccessTypeDataWithNetworkTaskId(task.getId()));
        TestNetworkTaskWorker testNetworkTaskWorker = new TestNetworkTaskWorker(TestRegistry.getContext(), task, null, false);
        setCurrentTime(testNetworkTaskWorker);
        MockNetworkManager networkManager = (MockNetworkManager) testNetworkTaskWorker.getNetworkManager();
        networkManager.setConnected(true);
        networkManager.setConnectedWithWiFi(true);
        testNetworkTaskWorker.run();
        NotificationHandler notificationHandler = testNetworkTaskWorker.getNotificationHandler();
        MockNotificationManager notificationManager = (MockNotificationManager) notificationHandler.getNotificationManager();
        assertFalse(notificationManager.wasNotifyCalled());
        notificationManager.reset();
        testNetworkTaskWorker.run();
        assertTrue(notificationManager.wasNotifyCalled());
        notificationManager.reset();
        testNetworkTaskWorker.run();
        notificationManager = (MockNotificationManager) notificationHandler.getNotificationManager();
        assertFalse(notificationManager.wasNotifyCalled());
        notificationManager.reset();
        testNetworkTaskWorker.run();
        assertTrue(notificationManager.wasNotifyCalled());
        notificationManager.reset();
        testNetworkTaskWorker.run();
        notificationManager = (MockNotificationManager) notificationHandler.getNotificationManager();
        assertFalse(notificationManager.wasNotifyCalled());
        notificationManager.reset();
        testNetworkTaskWorker.run();
        assertTrue(notificationManager.wasNotifyCalled());
    }

    @Test
    public void testFailureWithNotificationLastSuccessOnFailure() {
        preferenceManager.setPreferenceNotificationType(NotificationType.FAILURE);
        NetworkTask task = getNetworkTask();
        task = networkTaskDAO.insertNetworkTask(task);
        accessTypeDataDAO.insertAccessTypeData(getAccessTypeDataWithNetworkTaskId(task.getId()));
        logDAO.insertAndDeleteLog(getLogEntry(task.getId(), true));
        TestNetworkTaskWorker testNetworkTaskWorker = new TestNetworkTaskWorker(TestRegistry.getContext(), task, null, false);
        setCurrentTime(testNetworkTaskWorker);
        MockNetworkManager networkManager = (MockNetworkManager) testNetworkTaskWorker.getNetworkManager();
        networkManager.setConnected(true);
        networkManager.setConnectedWithWiFi(true);
        testNetworkTaskWorker.run();
        NetworkTask readTask = networkTaskDAO.readNetworkTask(task.getId());
        assertEquals(1, readTask.getFailureCount());
        NotificationHandler notificationHandler = testNetworkTaskWorker.getNotificationHandler();
        MockNotificationManager notificationManager = (MockNotificationManager) notificationHandler.getNotificationManager();
        assertTrue(notificationManager.wasNotifyCalled());
    }

    @Test
    public void testFailureWithNotificationLastSuccessOnFailureAfter20Failures() {
        preferenceManager.setPreferenceNotificationAfterFailures(20);
        preferenceManager.setPreferenceNotificationType(NotificationType.FAILURE);
        NetworkTask task = getNetworkTask();
        task = networkTaskDAO.insertNetworkTask(task);
        accessTypeDataDAO.insertAccessTypeData(getAccessTypeDataWithNetworkTaskId(task.getId()));
        logDAO.insertAndDeleteLog(getLogEntry(task.getId(), true));
        TestNetworkTaskWorker testNetworkTaskWorker = new TestNetworkTaskWorker(TestRegistry.getContext(), task, null, false);
        setCurrentTime(testNetworkTaskWorker);
        MockNetworkManager networkManager = (MockNetworkManager) testNetworkTaskWorker.getNetworkManager();
        networkManager.setConnected(true);
        networkManager.setConnectedWithWiFi(true);
        testNetworkTaskWorker.run();
        NetworkTask readTask = networkTaskDAO.readNetworkTask(task.getId());
        assertEquals(1, readTask.getFailureCount());
        NotificationHandler notificationHandler = testNetworkTaskWorker.getNotificationHandler();
        MockNotificationManager notificationManager = (MockNotificationManager) notificationHandler.getNotificationManager();
        assertFalse(notificationManager.wasNotifyCalled());
        for (int ii = 0; ii < 18; ii++) {
            testNetworkTaskWorker.run();
            assertFalse(notificationManager.wasNotifyCalled());
        }
        testNetworkTaskWorker.run();
        readTask = networkTaskDAO.readNetworkTask(task.getId());
        assertEquals(20, readTask.getFailureCount());
        assertTrue(notificationManager.wasNotifyCalled());
    }

    @Test
    public void testFailureWithNotificationLastSuccessNotificationOnChange() {
        preferenceManager.setPreferenceNotificationType(NotificationType.CHANGE);
        NetworkTask task = getNetworkTask();
        task = networkTaskDAO.insertNetworkTask(task);
        accessTypeDataDAO.insertAccessTypeData(getAccessTypeDataWithNetworkTaskId(task.getId()));
        logDAO.insertAndDeleteLog(getLogEntry(task.getId(), true));
        TestNetworkTaskWorker testNetworkTaskWorker = new TestNetworkTaskWorker(TestRegistry.getContext(), task, null, false);
        setCurrentTime(testNetworkTaskWorker);
        MockNetworkManager networkManager = (MockNetworkManager) testNetworkTaskWorker.getNetworkManager();
        networkManager.setConnected(true);
        networkManager.setConnectedWithWiFi(true);
        testNetworkTaskWorker.run();
        NetworkTask readTask = networkTaskDAO.readNetworkTask(task.getId());
        assertEquals(1, readTask.getFailureCount());
        NotificationHandler notificationHandler = testNetworkTaskWorker.getNotificationHandler();
        MockNotificationManager notificationManager = (MockNotificationManager) notificationHandler.getNotificationManager();
        assertTrue(notificationManager.wasNotifyCalled());
    }

    @Test
    public void testFailureWithNotificationLastFailureOnChange() {
        preferenceManager.setPreferenceNotificationType(NotificationType.CHANGE);
        NetworkTask task = getNetworkTask();
        task = networkTaskDAO.insertNetworkTask(task);
        accessTypeDataDAO.insertAccessTypeData(getAccessTypeDataWithNetworkTaskId(task.getId()));
        logDAO.insertAndDeleteLog(getLogEntry(task.getId(), false));
        networkTaskDAO.increaseNetworkTaskFailureCount(task.getId());
        TestNetworkTaskWorker testNetworkTaskWorker = new TestNetworkTaskWorker(TestRegistry.getContext(), task, null, false);
        setCurrentTime(testNetworkTaskWorker);
        MockNetworkManager networkManager = (MockNetworkManager) testNetworkTaskWorker.getNetworkManager();
        networkManager.setConnected(true);
        networkManager.setConnectedWithWiFi(true);
        testNetworkTaskWorker.run();
        NetworkTask readTask = networkTaskDAO.readNetworkTask(task.getId());
        assertEquals(2, readTask.getFailureCount());
        NotificationHandler notificationHandler = testNetworkTaskWorker.getNotificationHandler();
        MockNotificationManager notificationManager = (MockNotificationManager) notificationHandler.getNotificationManager();
        assertFalse(notificationManager.wasNotifyCalled());
    }

    @Test
    public void testFailureWithNotificationLastFailureOnFailure() {
        preferenceManager.setPreferenceNotificationType(NotificationType.FAILURE);
        NetworkTask task = getNetworkTask();
        task = networkTaskDAO.insertNetworkTask(task);
        accessTypeDataDAO.insertAccessTypeData(getAccessTypeDataWithNetworkTaskId(task.getId()));
        logDAO.insertAndDeleteLog(getLogEntry(task.getId(), false));
        networkTaskDAO.increaseNetworkTaskFailureCount(task.getId());
        TestNetworkTaskWorker testNetworkTaskWorker = new TestNetworkTaskWorker(TestRegistry.getContext(), task, null, false);
        setCurrentTime(testNetworkTaskWorker);
        MockNetworkManager networkManager = (MockNetworkManager) testNetworkTaskWorker.getNetworkManager();
        networkManager.setConnected(true);
        networkManager.setConnectedWithWiFi(true);
        testNetworkTaskWorker.run();
        NetworkTask readTask = networkTaskDAO.readNetworkTask(task.getId());
        assertEquals(2, readTask.getFailureCount());
        NotificationHandler notificationHandler = testNetworkTaskWorker.getNotificationHandler();
        MockNotificationManager notificationManager = (MockNotificationManager) notificationHandler.getNotificationManager();
        assertTrue(notificationManager.wasNotifyCalled());
    }

    @Test
    public void testFailureWithNotificationLastFailureOnFailureAfter3Failures() {
        preferenceManager.setPreferenceNotificationAfterFailures(3);
        preferenceManager.setPreferenceNotificationType(NotificationType.FAILURE);
        NetworkTask task = getNetworkTask();
        task = networkTaskDAO.insertNetworkTask(task);
        accessTypeDataDAO.insertAccessTypeData(getAccessTypeDataWithNetworkTaskId(task.getId()));
        logDAO.insertAndDeleteLog(getLogEntry(task.getId(), false));
        networkTaskDAO.increaseNetworkTaskFailureCount(task.getId());
        TestNetworkTaskWorker testNetworkTaskWorker = new TestNetworkTaskWorker(TestRegistry.getContext(), task, null, false);
        setCurrentTime(testNetworkTaskWorker);
        MockNetworkManager networkManager = (MockNetworkManager) testNetworkTaskWorker.getNetworkManager();
        networkManager.setConnected(true);
        networkManager.setConnectedWithWiFi(true);
        testNetworkTaskWorker.run();
        NetworkTask readTask = networkTaskDAO.readNetworkTask(task.getId());
        assertEquals(2, readTask.getFailureCount());
        NotificationHandler notificationHandler = testNetworkTaskWorker.getNotificationHandler();
        MockNotificationManager notificationManager = (MockNotificationManager) notificationHandler.getNotificationManager();
        assertFalse(notificationManager.wasNotifyCalled());
        testNetworkTaskWorker.run();
        assertTrue(notificationManager.wasNotifyCalled());
    }

    @Test
    public void testFailureWithoutNotification() {
        NetworkTask task = getNetworkTask();
        task.setNotification(false);
        networkTaskDAO.insertNetworkTask(task);
        accessTypeDataDAO.insertAccessTypeData(getAccessTypeDataWithNetworkTaskId(task.getId()));
        TestNetworkTaskWorker testNetworkTaskWorker = new TestNetworkTaskWorker(TestRegistry.getContext(), task, null, false);
        setCurrentTime(testNetworkTaskWorker);
        MockNetworkManager networkManager = (MockNetworkManager) testNetworkTaskWorker.getNetworkManager();
        networkManager.setConnected(true);
        networkManager.setConnectedWithWiFi(true);
        testNetworkTaskWorker.run();
        List<LogEntry> entries = logDAO.readAllLogsForNetworkTask(task.getId());
        assertEquals(1, entries.size());
        LogEntry entry = entries.get(0);
        assertEquals(task.getId(), entry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), entry.getTimestamp());
        assertLastScheduledInDatabase(task, getTestTimestamp());
        assertFalse(entry.isSuccess());
        assertEquals("failed", entry.getMessage());
        NetworkTask readTask = networkTaskDAO.readNetworkTask(task.getId());
        assertEquals(1, readTask.getFailureCount());
        NotificationHandler notificationHandler = testNetworkTaskWorker.getNotificationHandler();
        MockNotificationManager notificationManager = (MockNotificationManager) notificationHandler.getNotificationManager();
        assertFalse(notificationManager.wasNotifyCalled());
    }

    @Test
    public void testFailureWithoutNotificationLastSuccessOnChange() {
        preferenceManager.setPreferenceNotificationType(NotificationType.CHANGE);
        NetworkTask task = getNetworkTask();
        task.setNotification(false);
        task = networkTaskDAO.insertNetworkTask(task);
        accessTypeDataDAO.insertAccessTypeData(getAccessTypeDataWithNetworkTaskId(task.getId()));
        logDAO.insertAndDeleteLog(getLogEntry(task.getId(), true));
        TestNetworkTaskWorker testNetworkTaskWorker = new TestNetworkTaskWorker(TestRegistry.getContext(), task, null, false);
        setCurrentTime(testNetworkTaskWorker);
        MockNetworkManager networkManager = (MockNetworkManager) testNetworkTaskWorker.getNetworkManager();
        networkManager.setConnected(true);
        networkManager.setConnectedWithWiFi(true);
        testNetworkTaskWorker.run();
        NetworkTask readTask = networkTaskDAO.readNetworkTask(task.getId());
        assertEquals(1, readTask.getFailureCount());
        NotificationHandler notificationHandler = testNetworkTaskWorker.getNotificationHandler();
        MockNotificationManager notificationManager = (MockNotificationManager) notificationHandler.getNotificationManager();
        assertFalse(notificationManager.wasNotifyCalled());
    }

    @Test
    public void testFailureNumberInstances() {
        NetworkTask task = getNetworkTask();
        task = networkTaskDAO.insertNetworkTask(task);
        accessTypeDataDAO.insertAccessTypeData(getAccessTypeDataWithNetworkTaskId(task.getId()));
        TestNetworkTaskWorker testNetworkTaskWorker = new TestNetworkTaskWorker(TestRegistry.getContext(), task, null, false);
        setCurrentTime(testNetworkTaskWorker);
        MockNetworkManager networkManager = (MockNetworkManager) testNetworkTaskWorker.getNetworkManager();
        networkManager.setConnected(true);
        networkManager.setConnectedWithWiFi(true);
        int activeInstances = networkTaskDAO.readNetworkTaskInstances(task.getId());
        assertEquals(0, activeInstances);
        testNetworkTaskWorker.run();
        assertEquals(1, testNetworkTaskWorker.getInstancesOnExecute());
        activeInstances = networkTaskDAO.readNetworkTaskInstances(task.getId());
        assertEquals(0, activeInstances);
        assertLastScheduledInDatabase(task, getTestTimestamp());
    }

    @Test
    public void testNetworkTaskDoesNotExist() {
        NetworkTask task = getNetworkTask();
        TestNetworkTaskWorker testNetworkTaskWorker = new TestNetworkTaskWorker(TestRegistry.getContext(), task, null, false);
        setCurrentTime(testNetworkTaskWorker);
        MockNetworkManager networkManager = (MockNetworkManager) testNetworkTaskWorker.getNetworkManager();
        networkManager.setConnected(true);
        networkManager.setConnectedWithWiFi(true);
        testNetworkTaskWorker.run();
        List<LogEntry> entries = logDAO.readAllLogsForNetworkTask(task.getId());
        assertEquals(0, entries.size());
        NotificationHandler notificationHandler = testNetworkTaskWorker.getNotificationHandler();
        MockNotificationManager notificationManager = (MockNotificationManager) notificationHandler.getNotificationManager();
        assertFalse(notificationManager.wasNotifyCalled());
    }

    @Test
    public void testNetworkTaskIsNotValid() {
        NetworkTask task = getNetworkTask();
        task = networkTaskDAO.insertNetworkTask(task);
        accessTypeDataDAO.insertAccessTypeData(getAccessTypeDataWithNetworkTaskId(task.getId()));
        task.setSchedulerId(task.getSchedulerId() + 1);
        TestNetworkTaskWorker testNetworkTaskWorker = new TestNetworkTaskWorker(TestRegistry.getContext(), task, null, false);
        setCurrentTime(testNetworkTaskWorker);
        MockNetworkManager networkManager = (MockNetworkManager) testNetworkTaskWorker.getNetworkManager();
        networkManager.setConnected(true);
        networkManager.setConnectedWithWiFi(true);
        testNetworkTaskWorker.run();
        List<LogEntry> entries = logDAO.readAllLogsForNetworkTask(task.getId());
        assertEquals(0, entries.size());
        NotificationHandler notificationHandler = testNetworkTaskWorker.getNotificationHandler();
        MockNotificationManager notificationManager = (MockNotificationManager) notificationHandler.getNotificationManager();
        assertFalse(notificationManager.wasNotifyCalled());
        assertLastScheduledInDatabase(task, -1);
    }

    @Test
    public void testNoNetworkConnectionWithoutNotification() {
        NetworkTask task = getNetworkTask();
        networkTaskDAO.insertNetworkTask(task);
        accessTypeDataDAO.insertAccessTypeData(getAccessTypeDataWithNetworkTaskId(task.getId()));
        TestNetworkTaskWorker testNetworkTaskWorker = new TestNetworkTaskWorker(TestRegistry.getContext(), task, null, true);
        setCurrentTime(testNetworkTaskWorker);
        MockNetworkManager networkManager = (MockNetworkManager) testNetworkTaskWorker.getNetworkManager();
        networkManager.setConnected(false);
        networkManager.setConnectedWithWiFi(false);
        preferenceManager.setPreferenceNotificationInactiveNetwork(false);
        testNetworkTaskWorker.run();
        List<LogEntry> entries = logDAO.readAllLogsForNetworkTask(task.getId());
        assertEquals(1, entries.size());
        LogEntry entry = entries.get(0);
        assertEquals(task.getId(), entry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), entry.getTimestamp());
        assertLastScheduledInDatabase(task, getTestTimestamp());
        assertFalse(entry.isSuccess());
        assertEquals("No active network connection.", entry.getMessage());
        NetworkTask readTask = networkTaskDAO.readNetworkTask(task.getId());
        assertEquals(0, readTask.getFailureCount());
        NotificationHandler notificationHandler = testNetworkTaskWorker.getNotificationHandler();
        MockNotificationManager notificationManager = (MockNotificationManager) notificationHandler.getNotificationManager();
        assertFalse(notificationManager.wasNotifyCalled());
    }

    @Test
    public void testNoNetworkConnectionWithNotification() {
        NetworkTask task = getNetworkTask();
        networkTaskDAO.insertNetworkTask(task);
        accessTypeDataDAO.insertAccessTypeData(getAccessTypeDataWithNetworkTaskId(task.getId()));
        TestNetworkTaskWorker testNetworkTaskWorker = new TestNetworkTaskWorker(TestRegistry.getContext(), task, null, true);
        setCurrentTime(testNetworkTaskWorker);
        MockNetworkManager networkManager = (MockNetworkManager) testNetworkTaskWorker.getNetworkManager();
        networkManager.setConnected(false);
        networkManager.setConnectedWithWiFi(false);
        preferenceManager.setPreferenceNotificationInactiveNetwork(true);
        testNetworkTaskWorker.run();
        List<LogEntry> entries = logDAO.readAllLogsForNetworkTask(task.getId());
        assertEquals(1, entries.size());
        LogEntry entry = entries.get(0);
        assertEquals(task.getId(), entry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), entry.getTimestamp());
        assertLastScheduledInDatabase(task, getTestTimestamp());
        assertFalse(entry.isSuccess());
        assertEquals("No active network connection.", entry.getMessage());
        NetworkTask readTask = networkTaskDAO.readNetworkTask(task.getId());
        assertEquals(1, readTask.getFailureCount());
        NotificationHandler notificationHandler = testNetworkTaskWorker.getNotificationHandler();
        MockNotificationManager notificationManager = (MockNotificationManager) notificationHandler.getNotificationManager();
        assertTrue(notificationManager.wasNotifyCalled());
    }

    @Test
    public void testNoNetworkConnectionWithNotificationAfter2Failures() {
        preferenceManager.setPreferenceNotificationAfterFailures(2);
        NetworkTask task = getNetworkTask();
        networkTaskDAO.insertNetworkTask(task);
        accessTypeDataDAO.insertAccessTypeData(getAccessTypeDataWithNetworkTaskId(task.getId()));
        TestNetworkTaskWorker testNetworkTaskWorker = new TestNetworkTaskWorker(TestRegistry.getContext(), task, null, true);
        setCurrentTime(testNetworkTaskWorker);
        MockNetworkManager networkManager = (MockNetworkManager) testNetworkTaskWorker.getNetworkManager();
        networkManager.setConnected(false);
        networkManager.setConnectedWithWiFi(false);
        preferenceManager.setPreferenceNotificationInactiveNetwork(true);
        testNetworkTaskWorker.run();
        List<LogEntry> entries = logDAO.readAllLogsForNetworkTask(task.getId());
        assertEquals(1, entries.size());
        LogEntry entry = entries.get(0);
        assertEquals(task.getId(), entry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), entry.getTimestamp());
        assertLastScheduledInDatabase(task, getTestTimestamp());
        assertFalse(entry.isSuccess());
        assertEquals("No active network connection.", entry.getMessage());
        NetworkTask readTask = networkTaskDAO.readNetworkTask(task.getId());
        assertEquals(1, readTask.getFailureCount());
        NotificationHandler notificationHandler = testNetworkTaskWorker.getNotificationHandler();
        MockNotificationManager notificationManager = (MockNotificationManager) notificationHandler.getNotificationManager();
        assertFalse(notificationManager.wasNotifyCalled());
        testNetworkTaskWorker.run();
        assertTrue(notificationManager.wasNotifyCalled());
    }

    @Test
    public void testNoNetworkConnectionWithNotificationLastSuccessOnChange() {
        preferenceManager.setPreferenceNotificationType(NotificationType.CHANGE);
        NetworkTask task = getNetworkTask();
        task = networkTaskDAO.insertNetworkTask(task);
        accessTypeDataDAO.insertAccessTypeData(getAccessTypeDataWithNetworkTaskId(task.getId()));
        logDAO.insertAndDeleteLog(getLogEntry(task.getId(), true));
        TestNetworkTaskWorker testNetworkTaskWorker = new TestNetworkTaskWorker(TestRegistry.getContext(), task, null, true);
        setCurrentTime(testNetworkTaskWorker);
        MockNetworkManager networkManager = (MockNetworkManager) testNetworkTaskWorker.getNetworkManager();
        networkManager.setConnected(false);
        networkManager.setConnectedWithWiFi(false);
        preferenceManager.setPreferenceNotificationInactiveNetwork(true);
        testNetworkTaskWorker.run();
        NetworkTask readTask = networkTaskDAO.readNetworkTask(task.getId());
        assertEquals(1, readTask.getFailureCount());
        NotificationHandler notificationHandler = testNetworkTaskWorker.getNotificationHandler();
        MockNotificationManager notificationManager = (MockNotificationManager) notificationHandler.getNotificationManager();
        assertTrue(notificationManager.wasNotifyCalled());
    }

    @Test
    public void testNoNetworkConnectionWithNotificationLastFailureOnChange() {
        preferenceManager.setPreferenceNotificationType(NotificationType.CHANGE);
        NetworkTask task = getNetworkTask();
        task = networkTaskDAO.insertNetworkTask(task);
        accessTypeDataDAO.insertAccessTypeData(getAccessTypeDataWithNetworkTaskId(task.getId()));
        logDAO.insertAndDeleteLog(getLogEntry(task.getId(), false));
        networkTaskDAO.increaseNetworkTaskFailureCount(task.getId());
        TestNetworkTaskWorker testNetworkTaskWorker = new TestNetworkTaskWorker(TestRegistry.getContext(), task, null, true);
        setCurrentTime(testNetworkTaskWorker);
        MockNetworkManager networkManager = (MockNetworkManager) testNetworkTaskWorker.getNetworkManager();
        networkManager.setConnected(false);
        networkManager.setConnectedWithWiFi(false);
        preferenceManager.setPreferenceNotificationInactiveNetwork(true);
        testNetworkTaskWorker.run();
        NetworkTask readTask = networkTaskDAO.readNetworkTask(task.getId());
        assertEquals(2, readTask.getFailureCount());
        NotificationHandler notificationHandler = testNetworkTaskWorker.getNotificationHandler();
        MockNotificationManager notificationManager = (MockNotificationManager) notificationHandler.getNotificationManager();
        assertFalse(notificationManager.wasNotifyCalled());
    }

    @Test
    public void testNoNetworkConnectionNumberInstancesAfterExecution() {
        NetworkTask task = getNetworkTask();
        task = networkTaskDAO.insertNetworkTask(task);
        TestNetworkTaskWorker testNetworkTaskWorker = new TestNetworkTaskWorker(TestRegistry.getContext(), task, null, true);
        setCurrentTime(testNetworkTaskWorker);
        MockNetworkManager networkManager = (MockNetworkManager) testNetworkTaskWorker.getNetworkManager();
        networkManager.setConnected(false);
        networkManager.setConnectedWithWiFi(true);
        int activeInstances = networkTaskDAO.readNetworkTaskInstances(task.getId());
        assertEquals(0, activeInstances);
        testNetworkTaskWorker.run();
        activeInstances = networkTaskDAO.readNetworkTaskInstances(task.getId());
        assertEquals(0, activeInstances);
        assertLastScheduledInDatabase(task, getTestTimestamp());
    }

    @Test
    public void testNoWifiConnectionWithoutNotification() {
        NetworkTask task = getNetworkTask();
        networkTaskDAO.insertNetworkTask(task);
        accessTypeDataDAO.insertAccessTypeData(getAccessTypeDataWithNetworkTaskId(task.getId()));
        task.setOnlyWifi(true);
        TestNetworkTaskWorker testNetworkTaskWorker = new TestNetworkTaskWorker(TestRegistry.getContext(), task, null, true);
        setCurrentTime(testNetworkTaskWorker);
        MockNetworkManager networkManager = (MockNetworkManager) testNetworkTaskWorker.getNetworkManager();
        networkManager.setConnected(true);
        networkManager.setConnectedWithWiFi(false);
        testNetworkTaskWorker.run();
        List<LogEntry> entries = logDAO.readAllLogsForNetworkTask(task.getId());
        assertEquals(1, entries.size());
        LogEntry entry = entries.get(0);
        assertEquals(task.getId(), entry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), entry.getTimestamp());
        assertLastScheduledInDatabase(task, getTestTimestamp());
        assertFalse(entry.isSuccess());
        assertEquals("Skipped. No active wifi connection.", entry.getMessage());
        NetworkTask readTask = networkTaskDAO.readNetworkTask(task.getId());
        assertEquals(0, readTask.getFailureCount());
        NotificationHandler notificationHandler = testNetworkTaskWorker.getNotificationHandler();
        MockNotificationManager notificationManager = (MockNotificationManager) notificationHandler.getNotificationManager();
        assertFalse(notificationManager.wasNotifyCalled());
    }

    @Test
    public void testNoWifiConnectionButAllowsNoWifi() {
        NetworkTask task = getNetworkTask();
        networkTaskDAO.insertNetworkTask(task);
        accessTypeDataDAO.insertAccessTypeData(getAccessTypeDataWithNetworkTaskId(task.getId()));
        task.setOnlyWifi(false);
        TestNetworkTaskWorker testNetworkTaskWorker = new TestNetworkTaskWorker(TestRegistry.getContext(), task, null, true);
        setCurrentTime(testNetworkTaskWorker);
        MockNetworkManager networkManager = (MockNetworkManager) testNetworkTaskWorker.getNetworkManager();
        networkManager.setConnected(true);
        networkManager.setConnectedWithWiFi(false);
        testNetworkTaskWorker.run();
        List<LogEntry> entries = logDAO.readAllLogsForNetworkTask(task.getId());
        assertEquals(1, entries.size());
        LogEntry entry = entries.get(0);
        assertEquals(task.getId(), entry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), entry.getTimestamp());
        assertLastScheduledInDatabase(task, getTestTimestamp());
        assertTrue(entry.isSuccess());
        assertEquals("successful", entry.getMessage());
        NetworkTask readTask = networkTaskDAO.readNetworkTask(task.getId());
        assertEquals(0, readTask.getFailureCount());
        NotificationHandler notificationHandler = testNetworkTaskWorker.getNotificationHandler();
        MockNotificationManager notificationManager = (MockNotificationManager) notificationHandler.getNotificationManager();
        assertFalse(notificationManager.wasNotifyCalled());
    }

    @Test
    public void testNoWifiConnectionWithoutNotificationLastSuccessOnChange() {
        preferenceManager.setPreferenceNotificationType(NotificationType.CHANGE);
        NetworkTask task = getNetworkTask();
        task.setOnlyWifi(true);
        task = networkTaskDAO.insertNetworkTask(task);
        accessTypeDataDAO.insertAccessTypeData(getAccessTypeDataWithNetworkTaskId(task.getId()));
        logDAO.insertAndDeleteLog(getLogEntry(task.getId(), true));
        TestNetworkTaskWorker testNetworkTaskWorker = new TestNetworkTaskWorker(TestRegistry.getContext(), task, null, true);
        setCurrentTime(testNetworkTaskWorker);
        MockNetworkManager networkManager = (MockNetworkManager) testNetworkTaskWorker.getNetworkManager();
        networkManager.setConnected(true);
        networkManager.setConnectedWithWiFi(false);
        testNetworkTaskWorker.run();
        NetworkTask readTask = networkTaskDAO.readNetworkTask(task.getId());
        assertEquals(0, readTask.getFailureCount());
        NotificationHandler notificationHandler = testNetworkTaskWorker.getNotificationHandler();
        MockNotificationManager notificationManager = (MockNotificationManager) notificationHandler.getNotificationManager();
        assertFalse(notificationManager.wasNotifyCalled());
    }

    @Test
    public void testNoWifiConnectionWithoutNotificationLastFailureOnChange() {
        preferenceManager.setPreferenceNotificationType(NotificationType.CHANGE);
        NetworkTask task = getNetworkTask();
        task.setOnlyWifi(true);
        task = networkTaskDAO.insertNetworkTask(task);
        accessTypeDataDAO.insertAccessTypeData(getAccessTypeDataWithNetworkTaskId(task.getId()));
        networkTaskDAO.increaseNetworkTaskFailureCount(task.getId());
        logDAO.insertAndDeleteLog(getLogEntry(task.getId(), false));
        TestNetworkTaskWorker testNetworkTaskWorker = new TestNetworkTaskWorker(TestRegistry.getContext(), task, null, true);
        setCurrentTime(testNetworkTaskWorker);
        MockNetworkManager networkManager = (MockNetworkManager) testNetworkTaskWorker.getNetworkManager();
        networkManager.setConnected(true);
        networkManager.setConnectedWithWiFi(false);
        testNetworkTaskWorker.run();
        NetworkTask readTask = networkTaskDAO.readNetworkTask(task.getId());
        assertEquals(1, readTask.getFailureCount());
        NotificationHandler notificationHandler = testNetworkTaskWorker.getNotificationHandler();
        MockNotificationManager notificationManager = (MockNotificationManager) notificationHandler.getNotificationManager();
        assertFalse(notificationManager.wasNotifyCalled());
    }

    @Test
    public void testNoWifiConnectionWithoutNotificationLastFailureOnFailure() {
        preferenceManager.setPreferenceNotificationType(NotificationType.FAILURE);
        NetworkTask task = getNetworkTask();
        task.setOnlyWifi(true);
        task = networkTaskDAO.insertNetworkTask(task);
        accessTypeDataDAO.insertAccessTypeData(getAccessTypeDataWithNetworkTaskId(task.getId()));
        networkTaskDAO.increaseNetworkTaskFailureCount(task.getId());
        logDAO.insertAndDeleteLog(getLogEntry(task.getId(), false));
        TestNetworkTaskWorker testNetworkTaskWorker = new TestNetworkTaskWorker(TestRegistry.getContext(), task, null, true);
        setCurrentTime(testNetworkTaskWorker);
        MockNetworkManager networkManager = (MockNetworkManager) testNetworkTaskWorker.getNetworkManager();
        networkManager.setConnected(true);
        networkManager.setConnectedWithWiFi(false);
        testNetworkTaskWorker.run();
        NetworkTask readTask = networkTaskDAO.readNetworkTask(task.getId());
        assertEquals(1, readTask.getFailureCount());
        NotificationHandler notificationHandler = testNetworkTaskWorker.getNotificationHandler();
        MockNotificationManager notificationManager = (MockNotificationManager) notificationHandler.getNotificationManager();
        assertFalse(notificationManager.wasNotifyCalled());
    }

    @Test
    public void testNoWifiConnectionNumberInstancesAfterExecution() {
        NetworkTask task = getNetworkTask();
        task = networkTaskDAO.insertNetworkTask(task);
        accessTypeDataDAO.insertAccessTypeData(getAccessTypeDataWithNetworkTaskId(task.getId()));
        task.setOnlyWifi(true);
        TestNetworkTaskWorker testNetworkTaskWorker = new TestNetworkTaskWorker(TestRegistry.getContext(), task, null, true);
        setCurrentTime(testNetworkTaskWorker);
        MockNetworkManager networkManager = (MockNetworkManager) testNetworkTaskWorker.getNetworkManager();
        networkManager.setConnected(true);
        networkManager.setConnectedWithWiFi(false);
        int activeInstances = networkTaskDAO.readNetworkTaskInstances(task.getId());
        assertEquals(0, activeInstances);
        testNetworkTaskWorker.run();
        activeInstances = networkTaskDAO.readNetworkTaskInstances(task.getId());
        assertEquals(0, activeInstances);
        assertLastScheduledInDatabase(task, getTestTimestamp());
    }

    @Test
    public void testExecuteDNSLookupPreferIP4() throws Exception {
        TestNetworkTaskWorker testNetworkTaskWorker = new TestNetworkTaskWorker(TestRegistry.getContext(), getNetworkTask(), null, true);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("::1"), InetAddress.getByName("127.0.0.1"), InetAddress.getByName("2a00:1450:4016:801::200e")), null);
        testNetworkTaskWorker.setMockDNSLookup(new MockDNSLookup("127.0.0.1", dnsLookupResult));
        NetworkTaskWorker.DNSExecutionResult dnsExecutionResult = testNetworkTaskWorker.executeDNSLookup("host.com", true);
        InetAddress address = dnsExecutionResult.getAddress();
        LogEntry logEntry = dnsExecutionResult.getLogEntry();
        assertEquals(InetAddress.getByName("127.0.0.1"), address);
        assertTrue(logEntry.isSuccess());
        assertEquals("DNS lookup for host.com successful. Resolved address is 127.0.0.1.", logEntry.getMessage());
    }

    @Test
    public void testExecuteDNSLookupPreferIP6() throws Exception {
        TestNetworkTaskWorker testNetworkTaskWorker = new TestNetworkTaskWorker(TestRegistry.getContext(), getNetworkTask(), null, true);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("::1"), InetAddress.getByName("127.0.0.1"), InetAddress.getByName("2a00:1450:4016:801::200e")), null);
        testNetworkTaskWorker.setMockDNSLookup(new MockDNSLookup("127.0.0.1", dnsLookupResult));
        NetworkTaskWorker.DNSExecutionResult dnsExecutionResult = testNetworkTaskWorker.executeDNSLookup("host.com", false);
        InetAddress address = dnsExecutionResult.getAddress();
        LogEntry logEntry = dnsExecutionResult.getLogEntry();
        assertEquals(InetAddress.getByName("::1"), address);
        assertTrue(logEntry.isSuccess());
        assertEquals("DNS lookup for host.com successful. Resolved address is ::1.", logEntry.getMessage());
    }

    @Test
    public void testExecuteDNSLookupPreferIP6NoIP6AddressAvailable() throws Exception {
        TestNetworkTaskWorker testNetworkTaskWorker = new TestNetworkTaskWorker(TestRegistry.getContext(), getNetworkTask(), null, true);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("192.168.178.1")), null);
        testNetworkTaskWorker.setMockDNSLookup(new MockDNSLookup("127.0.0.1", dnsLookupResult));
        NetworkTaskWorker.DNSExecutionResult dnsExecutionResult = testNetworkTaskWorker.executeDNSLookup("host.com", false);
        InetAddress address = dnsExecutionResult.getAddress();
        LogEntry logEntry = dnsExecutionResult.getLogEntry();
        assertEquals(InetAddress.getByName("127.0.0.1"), address);
        assertTrue(logEntry.isSuccess());
        assertEquals("DNS lookup for host.com successful. Resolved address is 127.0.0.1.", logEntry.getMessage());
    }

    @Test
    public void testExecuteDNSLookupNoAddresses() {
        TestNetworkTaskWorker testNetworkTaskWorker = new TestNetworkTaskWorker(TestRegistry.getContext(), getNetworkTask(), null, true);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Collections.emptyList(), null);
        testNetworkTaskWorker.setMockDNSLookup(new MockDNSLookup("127.0.0.1", dnsLookupResult));
        NetworkTaskWorker.DNSExecutionResult dnsExecutionResult = testNetworkTaskWorker.executeDNSLookup("host.com", true);
        InetAddress address = dnsExecutionResult.getAddress();
        LogEntry logEntry = dnsExecutionResult.getLogEntry();
        assertNull(address);
        assertFalse(logEntry.isSuccess());
        assertEquals("DNS lookup for host.com failed. No address for host.", logEntry.getMessage());
    }

    @Test
    public void testExecuteDNSLookupExceptionThrown() {
        TestNetworkTaskWorker testNetworkTaskWorker = new TestNetworkTaskWorker(TestRegistry.getContext(), getNetworkTask(), null, true);
        IllegalArgumentException exception = new IllegalArgumentException("TestException");
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Collections.emptyList(), exception);
        testNetworkTaskWorker.setMockDNSLookup(new MockDNSLookup("127.0.0.1", dnsLookupResult));
        NetworkTaskWorker.DNSExecutionResult dnsExecutionResult = testNetworkTaskWorker.executeDNSLookup("host.com", true);
        InetAddress address = dnsExecutionResult.getAddress();
        LogEntry logEntry = dnsExecutionResult.getLogEntry();
        assertNull(address);
        assertFalse(logEntry.isSuccess());
        assertEquals("DNS lookup for host.com failed. IllegalArgumentException: TestException", logEntry.getMessage());
    }

    private void setCurrentTime(TestNetworkTaskWorker testNetworkTaskWorker) {
        MockTimeService timeService = (MockTimeService) testNetworkTaskWorker.getTimeService();
        timeService.setTimestamp(getTestTimestamp());
        timeService.setTimestamp2(getTestTimestamp());
    }

    private long getTestTimestamp() {
        Calendar calendar = new GregorianCalendar(1985, Calendar.DECEMBER, 24, 1, 1, 1);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTimeInMillis();
    }

    private void assertLastScheduledInDatabase(NetworkTask task, long value) {
        task = networkTaskDAO.readNetworkTask(task.getId());
        assertEquals(value, task.getLastScheduled());
    }

    private NetworkTask getNetworkTask() {
        NetworkTask task = new NetworkTask();
        task.setId(45);
        task.setIndex(1);
        task.setSchedulerId(123);
        task.setName("name");
        task.setInstances(0);
        task.setAddress("127.0.0.1");
        task.setPort(80);
        task.setAccessType(AccessType.PING);
        task.setInterval(15);
        task.setOnlyWifi(false);
        task.setNotification(true);
        task.setRunning(true);
        task.setLastScheduled(1);
        task.setFailureCount(0);
        task.setHighPrio(true);
        return task;
    }

    private AccessTypeData getAccessTypeDataWithNetworkTaskId(long networkTaskId) {
        AccessTypeData data = new AccessTypeData();
        data.setId(0);
        data.setNetworkTaskId(networkTaskId);
        data.setPingCount(10);
        data.setPingPackageSize(1234);
        data.setConnectCount(3);
        data.setStopOnSuccess(true);
        data.setIgnoreSSLError(true);
        return data;
    }

    private LogEntry getLogEntry(long networkTaskId, boolean success) {
        LogEntry logEntry = new LogEntry();
        logEntry.setNetworkTaskId(networkTaskId);
        logEntry.setSuccess(success);
        return logEntry;
    }
}
