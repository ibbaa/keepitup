package de.ibba.keepitup.ui.sync;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collections;

import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.test.mock.TestLogEntryUIBroadcastReceiver;
import de.ibba.keepitup.test.mock.TestRegistry;
import de.ibba.keepitup.ui.BaseUITest;
import de.ibba.keepitup.ui.NetworkTaskMainActivity;
import de.ibba.keepitup.ui.adapter.LogEntryAdapter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class LogEntryUIBroadcastReceiverTest extends BaseUITest {

    private ActivityScenario<?> activityScenario;

    @Before
    public void beforeEachTestMethod() {
        super.beforeEachTestMethod();
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class);
    }

    @Test
    public void testOnReceiveMatchingTask() {
        TestLogEntryUIBroadcastReceiver logEntryUIBroadcastReceiver = createTestReceiver(23);
        Intent intent = new Intent(TestLogEntryUIBroadcastReceiver.class.getName());
        NetworkTask testTask = new NetworkTask();
        testTask.setId(23);
        intent.putExtras(testTask.toBundle());
        logEntryUIBroadcastReceiver.onReceive(TestRegistry.getContext(), intent);
        assertTrue(logEntryUIBroadcastReceiver.wasDoSyncCalled());
    }

    @Test
    public void testOnReceiveMismatchingTask() {
        TestLogEntryUIBroadcastReceiver logEntryUIBroadcastReceiver = createTestReceiver(23);
        Intent intent = new Intent(TestLogEntryUIBroadcastReceiver.class.getName());
        NetworkTask testTask = new NetworkTask();
        testTask.setId(24);
        intent.putExtras(testTask.toBundle());
        logEntryUIBroadcastReceiver.onReceive(TestRegistry.getContext(), intent);
        assertFalse(logEntryUIBroadcastReceiver.wasDoSyncCalled());
    }

    private TestLogEntryUIBroadcastReceiver createTestReceiver(long adapterTaskId) {
        NetworkTask adapterTask = new NetworkTask();
        adapterTask.setId(adapterTaskId);
        LogEntryAdapter adapter = new LogEntryAdapter(adapterTask, Collections.emptyList(), TestRegistry.getContext());
        return new TestLogEntryUIBroadcastReceiver(getActivity(activityScenario), adapter);
    }
}
