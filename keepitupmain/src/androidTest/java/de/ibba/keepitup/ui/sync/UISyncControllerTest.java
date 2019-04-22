package de.ibba.keepitup.ui.sync;

import android.support.test.InstrumentationRegistry;
import android.support.test.filters.MediumTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import de.ibba.keepitup.model.AccessType;
import de.ibba.keepitup.model.LogEntry;
import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.test.mock.MockHandler;
import de.ibba.keepitup.ui.BaseUITest;
import de.ibba.keepitup.ui.NetworkTaskMainActivity;
import de.ibba.keepitup.ui.adapter.NetworkTaskAdapter;
import de.ibba.keepitup.ui.adapter.NetworkTaskUIWrapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class UISyncControllerTest extends BaseUITest {

    @Rule
    public final ActivityTestRule<NetworkTaskMainActivity> rule = new ActivityTestRule<>(NetworkTaskMainActivity.class, false, false);

    public void startUISyncController(NetworkTaskMainActivity activity) {
        activity.runOnUiThread(() -> UISyncController.start(getAdapter()));
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
    }

    public void stopUISyncController(NetworkTaskMainActivity activity) {
        activity.runOnUiThread(() -> UISyncController.stop());
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
    }

    @Test
    public void testStartStop() {
        NetworkTaskMainActivity activity = (NetworkTaskMainActivity) launchRecyclerViewBaseActivity(rule);
        assertTrue(UISyncController.isRunning());
        MockHandler handler = (MockHandler) UISyncController.getHandler();
        assertNotNull(handler);
        assertTrue(handler.wasStartCalled());
        assertTrue(handler.wasStartDelayedCalled());
        assertFalse(handler.wasStopCalled());
        MockHandler.StartDelayedCall startDelayedCall = handler.getStartDelayedCalls().get(0);
        assertEquals(5000, startDelayedCall.getDelay());
        MockHandler.StartCall startCall = handler.getStartCalls().get(0);
        Runnable startedRunnable = startCall.getRunnable();
        stopUISyncController(activity);
        assertFalse(UISyncController.isRunning());
        assertTrue(handler.wasStopCalled());
        MockHandler.StopCall stopCall = handler.getStopCalls().get(0);
        Runnable stoppedRunnable = stopCall.getRunnable();
        assertSame(startedRunnable, stoppedRunnable);
    }

    @Test
    public void testUpdateAdapter() {
        NetworkTask task1 = getNetworkTask1();
        NetworkTask task2 = getNetworkTask2();
        NetworkTask task3 = getNetworkTask3();
        task1 = getNetworkTaskDAO().insertNetworkTask(task1);
        task2 = getNetworkTaskDAO().insertNetworkTask(task2);
        task3 = getNetworkTaskDAO().insertNetworkTask(task3);
        LogEntry entry11 = getLogEntry(task1, new GregorianCalendar(2016, Calendar.JULY, 1), true, "Message11");
        LogEntry entry21 = getLogEntry(task2, new GregorianCalendar(2016, Calendar.JULY, 1), true, "Message21");
        LogEntry entry31 = getLogEntry(task3, new GregorianCalendar(2016, Calendar.JULY, 1), true, "Message31");
        getLogDAO().insertAndDeleteLog(entry11);
        getLogDAO().insertAndDeleteLog(entry21);
        getLogDAO().insertAndDeleteLog(entry31);
        NetworkTaskMainActivity activity = (NetworkTaskMainActivity) launchRecyclerViewBaseActivity(rule);
        stopUISyncController(activity);
        LogEntry entry12 = getLogEntry(task1, new GregorianCalendar(2016, Calendar.JULY, 2), true, "Message12");
        LogEntry entry22 = getLogEntry(task2, new GregorianCalendar(2016, Calendar.JULY, 2), true, "Message22");
        LogEntry entry32 = getLogEntry(task3, new GregorianCalendar(2016, Calendar.JULY, 2), true, "Message32");
        getLogDAO().insertAndDeleteLog(entry12);
        getLogDAO().insertAndDeleteLog(entry22);
        getLogDAO().insertAndDeleteLog(entry32);
        startUISyncController(activity);
        List<NetworkTaskUIWrapper> uiWWrapperList = getAdapter().getAllItems();
        LogEntry adapterEntry1 = uiWWrapperList.get(0).getLogEntry();
        LogEntry adapterEntry2 = uiWWrapperList.get(1).getLogEntry();
        LogEntry adapterEntry3 = uiWWrapperList.get(2).getLogEntry();
        assertEquals("Message11", adapterEntry1.getMessage());
        assertEquals("Message22", adapterEntry2.getMessage());
        assertEquals("Message31", adapterEntry3.getMessage());
        assertEquals(entry11.getTimestamp(), adapterEntry1.getTimestamp());
        assertEquals(entry22.getTimestamp(), adapterEntry2.getTimestamp());
        assertEquals(entry31.getTimestamp(), adapterEntry3.getTimestamp());
    }

    private NetworkTask getNetworkTask1() {
        NetworkTask networkTask = new NetworkTask();
        networkTask.setId(-1);
        networkTask.setIndex(0);
        networkTask.setSchedulerId(-1);
        networkTask.setAddress("127.0.0.1");
        networkTask.setPort(80);
        networkTask.setAccessType(AccessType.PING);
        networkTask.setInterval(15);
        networkTask.setOnlyWifi(false);
        networkTask.setNotification(true);
        networkTask.setRunning(false);
        return networkTask;
    }

    private NetworkTask getNetworkTask2() {
        NetworkTask networkTask = new NetworkTask();
        networkTask.setId(-1);
        networkTask.setIndex(1);
        networkTask.setSchedulerId(-1);
        networkTask.setAddress("127.0.0.1");
        networkTask.setPort(80);
        networkTask.setAccessType(AccessType.PING);
        networkTask.setInterval(15);
        networkTask.setOnlyWifi(false);
        networkTask.setNotification(true);
        networkTask.setRunning(true);
        return networkTask;
    }

    private NetworkTask getNetworkTask3() {
        NetworkTask networkTask = new NetworkTask();
        networkTask.setId(-1);
        networkTask.setIndex(2);
        networkTask.setSchedulerId(-1);
        networkTask.setAddress("127.0.0.1");
        networkTask.setPort(80);
        networkTask.setAccessType(AccessType.PING);
        networkTask.setInterval(15);
        networkTask.setOnlyWifi(false);
        networkTask.setNotification(true);
        networkTask.setRunning(false);
        return networkTask;
    }

    private LogEntry getLogEntry(NetworkTask task, Calendar calendar, boolean success, String message) {
        LogEntry logEntry = new LogEntry();
        logEntry.setId(0);
        logEntry.setNetworkTaskId(task.getId());
        logEntry.setSuccess(success);
        logEntry.setTimestamp(calendar.getTime().getTime());
        logEntry.setMessage(message);
        return logEntry;
    }

    private NetworkTaskAdapter getAdapter() {
        NetworkTaskMainActivity activity = rule.getActivity();
        return (NetworkTaskAdapter) activity.getAdapter();
    }
}
