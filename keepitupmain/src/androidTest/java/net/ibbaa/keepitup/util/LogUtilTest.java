/*
 * Copyright (c) 2022. Alwin Ibba
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

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import net.ibbaa.keepitup.model.LogEntry;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.test.mock.MockFileManager;
import net.ibbaa.keepitup.test.mock.TestRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class LogUtilTest {

    private MockFileManager fileManager;

    @Before
    public void beforeEachTestMethod() {
        fileManager = new MockFileManager();
    }

    @Test
    public void testGetFileLogger() {
        NetworkTask networkTask = new NetworkTask();
        networkTask.setIndex(1);
        fileManager.setExternalDirectory(null, 0);
        assertNull(LogUtil.getFileLogger(TestRegistry.getContext(), fileManager, networkTask));
        networkTask.setIndex(-1);
        fileManager.setExternalDirectory(new File("Test"), 0);
        assertNull(LogUtil.getFileLogger(TestRegistry.getContext(), fileManager, networkTask));
        networkTask.setIndex(1);
        fileManager.setExternalDirectory(new File("Test"), 0);
        assertNotNull(LogUtil.getFileLogger(TestRegistry.getContext(), fileManager, networkTask));
    }

    @Test
    public void formatLogEntryLog() {
        LogEntry entry = getLogEntry();
        assertEquals("Log entry for network task 2 Execution successful Jan 1, 1970 1:00:00 AM Message: TestMessage1", LogUtil.formatLogEntryLog(TestRegistry.getContext(), 1, entry));
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
