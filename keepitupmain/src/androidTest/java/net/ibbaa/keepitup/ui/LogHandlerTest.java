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

package net.ibbaa.keepitup.ui;

import static org.junit.Assert.assertEquals;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import net.ibbaa.keepitup.model.AccessType;
import net.ibbaa.keepitup.model.LogEntry;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.test.mock.TestRegistry;
import net.ibbaa.keepitup.ui.adapter.LogEntryAdapter;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class LogHandlerTest extends BaseUITest {

    @Test
    public void testDeleteNetworkTask() {
        NetworkTask task1 = getNetworkTask();
        task1 = getNetworkTaskDAO().insertNetworkTask(task1);
        ActivityScenario<?> activityScenario = launchRecyclerViewBaseActivity(getNetworkTaskLogIntent(task1));
        LogHandler handler = new LogHandler((NetworkTaskLogActivity) getActivity(activityScenario));
        LogEntryAdapter adapter = (LogEntryAdapter) ((NetworkTaskLogActivity) getActivity(activityScenario)).getAdapter();
        NetworkTask task2 = getNetworkTask();
        task2 = getNetworkTaskDAO().insertNetworkTask(task2);
        LogEntry logEntry1 = getLogEntryWithNetworkTaskId(task1.getId());
        getLogDAO().insertAndDeleteLog(logEntry1);
        getLogDAO().insertAndDeleteLog(logEntry1);
        getLogDAO().insertAndDeleteLog(logEntry1);
        getLogDAO().insertAndDeleteLog(logEntry1);
        List<LogEntry> logEntries1 = getLogDAO().readAllLogsForNetworkTask(task1.getId());
        for (LogEntry logEntry : logEntries1) {
            adapter.addItem(logEntry);
        }
        LogEntry logEntry2 = getLogEntryWithNetworkTaskId(task2.getId());
        getLogDAO().insertAndDeleteLog(logEntry2);
        getLogDAO().insertAndDeleteLog(logEntry2);
        logEntries1 = getLogDAO().readAllLogsForNetworkTask(task1.getId());
        assertEquals(4, logEntries1.size());
        List<LogEntry> logEntries2 = getLogDAO().readAllLogsForNetworkTask(task2.getId());
        assertEquals(2, logEntries2.size());
        assertEquals(4, adapter.getItemCount());
        handler.deleteLogsForNetworkTask(task1);
        logEntries1 = getLogDAO().readAllLogsForNetworkTask(task1.getId());
        assertEquals(0, logEntries1.size());
        logEntries2 = getLogDAO().readAllLogsForNetworkTask(task2.getId());
        assertEquals(2, logEntries2.size());
        assertEquals(1, adapter.getItemCount());
        activityScenario.close();
    }

    private NetworkTask getNetworkTask() {
        NetworkTask networkTask = new NetworkTask();
        networkTask.setId(-1);
        networkTask.setIndex(-1);
        networkTask.setSchedulerId(-1);
        networkTask.setName("name");
        networkTask.setInstances(0);
        networkTask.setAddress("127.0.0.1");
        networkTask.setPort(80);
        networkTask.setAccessType(AccessType.PING);
        networkTask.setInterval(15);
        networkTask.setOnlyWifi(false);
        networkTask.setNotification(true);
        networkTask.setRunning(false);
        networkTask.setLastScheduled(1);
        networkTask.setFailureCount(1);
        networkTask.setHighPrio(true);
        return networkTask;
    }

    private LogEntry getLogEntryWithNetworkTaskId(long networkTaskId) {
        LogEntry logEntry = new LogEntry();
        logEntry.setId(0);
        logEntry.setNetworkTaskId(networkTaskId);
        logEntry.setSuccess(false);
        logEntry.setTimestamp(1);
        logEntry.setMessage("TestMessage");
        return logEntry;
    }

    private Intent getNetworkTaskLogIntent(NetworkTask task) {
        Intent intent = new Intent(TestRegistry.getContext(), NetworkTaskLogActivity.class);
        intent.putExtras(task.toBundle());
        return intent;
    }
}
