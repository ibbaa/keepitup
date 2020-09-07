package de.ibba.keepitup.ui;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;

import org.junit.Test;

import java.util.List;

import de.ibba.keepitup.model.AccessType;
import de.ibba.keepitup.model.LogEntry;
import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.test.mock.TestRegistry;
import de.ibba.keepitup.ui.adapter.LogEntryAdapter;

import static org.junit.Assert.assertEquals;

public class LogHandlerTest extends BaseUITest {

    @Test
    public void testDeleteNetworkTask() {
        NetworkTask task1 = getNetworkTask();
        task1 = getNetworkTaskDAO().insertNetworkTask(task1);
        ActivityScenario<?> activityScenario = launchRecyclerViewBaseActivity(getNetworkTaskIntent(task1));
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
        networkTask.setInstances(0);
        networkTask.setAddress("127.0.0.1");
        networkTask.setPort(80);
        networkTask.setAccessType(AccessType.PING);
        networkTask.setInterval(15);
        networkTask.setOnlyWifi(false);
        networkTask.setNotification(true);
        networkTask.setRunning(false);
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

    private Intent getNetworkTaskIntent(NetworkTask task) {
        Intent intent = new Intent(TestRegistry.getContext(), NetworkTaskLogActivity.class);
        intent.putExtras(task.toBundle());
        return intent;
    }
}
