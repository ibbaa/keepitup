package de.ibba.keepitup.ui;

import android.support.test.InstrumentationRegistry;
import android.support.test.annotation.UiThreadTest;
import android.support.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import de.ibba.keepitup.db.NetworkTaskDAO;
import de.ibba.keepitup.model.AccessType;
import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.service.NetworkKeepAliveServiceScheduler;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class NetworkTaskHandlerTest {

    @Rule
    public final ActivityTestRule<NetworkTaskMainActivity> rule = new ActivityTestRule<>(NetworkTaskMainActivity.class);

    private NetworkTaskMainActivity activity;
    private NetworkTaskDAO dao;
    private NetworkKeepAliveServiceScheduler scheduler;
    private NetworkTaskHandler handler;

    @Before
    @UiThreadTest
    public void beforeEachTestMethod() {
        activity = rule.getActivity();
        dao = new NetworkTaskDAO(InstrumentationRegistry.getTargetContext());
        dao.deleteAllNetworkTasks();
        scheduler = new NetworkKeepAliveServiceScheduler(InstrumentationRegistry.getTargetContext());
        scheduler.stopAll();
        handler = new NetworkTaskHandler(activity);
    }

    @After
    public void afterEachTestMethod() {
        dao.deleteAllNetworkTasks();
        scheduler.stopAll();
    }

    @Test
    public void testStartStopNetworkTask() {
        NetworkTask task = getNetworkTask();
        dao.insertNetworkTask(task);
        handler.startNetworkTask(task);
        assertTrue(task.getSchedulerid() >= 0);
        List<NetworkTask> tasks = dao.readAllNetworkTasks();
        task = tasks.get(0);
        assertTrue(task.getSchedulerid() >= 0);
        assertTrue(scheduler.isRunning(task));
        handler.stopNetworkTask(task);
        assertTrue(task.getSchedulerid() < 0);
        tasks = dao.readAllNetworkTasks();
        task = tasks.get(0);
        assertTrue(task.getSchedulerid() < 0);
        assertFalse(scheduler.isRunning(task));
    }

    private NetworkTask getNetworkTask() {
        NetworkTask networkTask = new NetworkTask();
        networkTask.setId(-1);
        networkTask.setIndex(0);
        networkTask.setSchedulerid(-1);
        networkTask.setAddress("127.0.0.1");
        networkTask.setPort(80);
        networkTask.setAccessType(AccessType.PING);
        networkTask.setInterval(15);
        networkTask.setSuccess(true);
        networkTask.setTimestamp(789);
        networkTask.setMessage("TestMessage1");
        networkTask.setOnlyWifi(false);
        networkTask.setNotification(true);
        return networkTask;
    }
}
