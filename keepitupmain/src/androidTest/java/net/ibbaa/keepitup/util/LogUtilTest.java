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

package net.ibbaa.keepitup.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import androidx.documentfile.provider.DocumentFile;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import net.ibbaa.keepitup.model.LogEntry;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.resources.PreferenceManager;
import net.ibbaa.keepitup.service.SystemFileManager;
import net.ibbaa.keepitup.test.mock.MockDocumentManager;
import net.ibbaa.keepitup.test.mock.MockFileManager;
import net.ibbaa.keepitup.test.mock.TestRegistry;
import net.ibbaa.phonelog.FileLogger;
import net.ibbaa.phonelog.ILogger;
import net.ibbaa.phonelog.android.DocumentFileLogger;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class LogUtilTest {

    private MockFileManager fileManager;
    private MockDocumentManager documentManager;

    @Before
    public void beforeEachTestMethod() {
        fileManager = new MockFileManager();
        documentManager = new MockDocumentManager();
    }

    @Test
    public void testGetFileLogger() {
        NetworkTask networkTask = new NetworkTask();
        networkTask.setIndex(1);
        fileManager.setExternalDirectory(null, 0);
        assertNull(LogUtil.getFileLogger(TestRegistry.getContext(), fileManager, documentManager, networkTask));
        networkTask.setIndex(-1);
        fileManager.setExternalDirectory(new File("Test"), 0);
        assertNull(LogUtil.getFileLogger(TestRegistry.getContext(), fileManager, documentManager, networkTask));
        networkTask.setIndex(1);
        fileManager.setExternalDirectory(new File("Test"), 0);
        ILogger logger = LogUtil.getFileLogger(TestRegistry.getContext(), fileManager, documentManager, networkTask);
        assertNotNull(logger);
        assertTrue(logger instanceof FileLogger);
    }

    @Test
    public void testGetDocumentFileLogger() {
        PreferenceManager preferenceManager = new PreferenceManager(TestRegistry.getContext());
        preferenceManager.setPreferenceAllowArbitraryFileLocation(true);
        NetworkTask networkTask = new NetworkTask();
        networkTask.setIndex(1);
        assertNull(LogUtil.getFileLogger(TestRegistry.getContext(), fileManager, documentManager, networkTask));
        networkTask.setIndex(-1);
        documentManager.setArbitraryDirectory(DocumentFile.fromFile(new File("test")));
        assertNull(LogUtil.getFileLogger(TestRegistry.getContext(), fileManager, documentManager, networkTask));
        networkTask.setIndex(1);
        documentManager.setArbitraryDirectory(DocumentFile.fromFile(new File("test")));
        ILogger logger = LogUtil.getFileLogger(TestRegistry.getContext(), fileManager, documentManager, networkTask);
        assertNotNull(logger);
        assertTrue(logger instanceof DocumentFileLogger);
    }

    @Test
    public void testGetLogFileName() {
        NetworkTask networkTask = new NetworkTask();
        networkTask.setIndex(1);
        networkTask.setAddress("127.0.0.1");
        networkTask.setSchedulerId(123);
        assertEquals("networktask_2_127_0_0_1_id_0000007B.log", LogUtil.getLogFileName(TestRegistry.getContext(), new SystemFileManager(TestRegistry.getContext()), networkTask));
        networkTask.setName("Network task");
        assertEquals("networktask_2_127_0_0_1_id_0000007B.log", LogUtil.getLogFileName(TestRegistry.getContext(), new SystemFileManager(TestRegistry.getContext()), networkTask));
        networkTask.setName("nam e");
        assertEquals("networktask_2_nam_e_127_0_0_1_id_0000007B.log", LogUtil.getLogFileName(TestRegistry.getContext(), new SystemFileManager(TestRegistry.getContext()), networkTask));
    }

    @Test
    public void testGetLogFileKey() {
        NetworkTask networkTask = new NetworkTask();
        networkTask.setIndex(1);
        networkTask.setAddress("127.0.0.1");
        networkTask.setSchedulerId(123);
        assertEquals("networktask_1_123_127.0.0.1", LogUtil.getLogFileKey(TestRegistry.getContext(), networkTask));
    }

    @Test
    public void testFormatLogEntryLog() {
        NetworkTask networkTask = new NetworkTask();
        networkTask.setIndex(1);
        LogEntry entry = getLogEntry();
        assertEquals("Log entry for network task 2, Execution successful, Timestamp: Jan 1, 1970 1:00:00 AM, Message: TestMessage1", LogUtil.formatLogEntryLog(TestRegistry.getContext(), networkTask, entry));
        networkTask.setName("name");
        assertEquals("Log entry for name (network task 2), Execution successful, Timestamp: Jan 1, 1970 1:00:00 AM, Message: TestMessage1", LogUtil.formatLogEntryLog(TestRegistry.getContext(), networkTask, entry));
    }

    @Test
    public void testGetLogTitleText() {
        NetworkTask networkTask = new NetworkTask();
        networkTask.setIndex(1);
        assertEquals("Log entry for network task 2", LogUtil.getLogTitleText(TestRegistry.getContext(), networkTask));
        networkTask.setName("name");
        assertEquals("Log entry for name (network task 2)", LogUtil.getLogTitleText(TestRegistry.getContext(), networkTask));
    }

    private LogEntry getLogEntry() {
        LogEntry insertedLogEntry1 = new LogEntry();
        insertedLogEntry1.setId(0);
        insertedLogEntry1.setNetworkTaskId(1);
        insertedLogEntry1.setSuccess(true);
        insertedLogEntry1.setTimestamp(123);
        insertedLogEntry1.setMessage("TestMessage1");
        return insertedLogEntry1;
    }
}
