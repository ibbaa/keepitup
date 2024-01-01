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

package net.ibbaa.keepitup.ui.sync;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.test.mock.TestLogEntryUIBroadcastReceiver;
import net.ibbaa.keepitup.test.mock.TestRegistry;
import net.ibbaa.keepitup.ui.BaseUITest;
import net.ibbaa.keepitup.ui.NetworkTaskMainActivity;
import net.ibbaa.keepitup.ui.adapter.LogEntryAdapter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collections;

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
