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

package net.ibbaa.keepitup.ui.sync;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import net.ibbaa.keepitup.model.AccessType;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.test.mock.TestNetworkTaskMainUIBroadcastReceiver;
import net.ibbaa.keepitup.test.mock.TestRegistry;
import net.ibbaa.keepitup.ui.BaseUITest;
import net.ibbaa.keepitup.ui.NetworkTaskMainActivity;
import net.ibbaa.keepitup.ui.adapter.NetworkTaskAdapter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collections;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class NetworkTaskUIBroadcastReceiverTest extends BaseUITest {

    private ActivityScenario<?> activityScenario;

    @Before
    public void beforeEachTestMethod() {
        super.beforeEachTestMethod();
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class);
    }

    @Test
    public void testTaskLoadedFromDatabase() {
        NetworkTask task = getNetworkTaskDAO().insertNetworkTask(getNetworkTask());
        getNetworkTaskDAO().increaseNetworkTaskFailureCount(task.getId());
        TestNetworkTaskMainUIBroadcastReceiver networkTaskMainUIBroadcastReceiver = createTestReceiver();
        Intent intent = new Intent(TestNetworkTaskMainUIBroadcastReceiver.class.getName());
        intent.putExtras(task.toBundle());
        networkTaskMainUIBroadcastReceiver.onReceive(TestRegistry.getContext(), intent);
        NetworkTask doSyncTask = networkTaskMainUIBroadcastReceiver.getDoSyncTask();
        assertEquals(1, doSyncTask.getFailureCount());
    }

    @Test
    public void testSyncSkippedInvalidTask() {
        NetworkTask task = getNetworkTaskDAO().insertNetworkTask(getNetworkTask());
        getNetworkTaskDAO().increaseNetworkTaskFailureCount(task.getId());
        TestNetworkTaskMainUIBroadcastReceiver networkTaskMainUIBroadcastReceiver = createTestReceiver();
        Intent intent = new Intent(TestNetworkTaskMainUIBroadcastReceiver.class.getName());
        task.setSchedulerId(task.getSchedulerId() + 1);
        intent.putExtras(task.toBundle());
        networkTaskMainUIBroadcastReceiver.onReceive(TestRegistry.getContext(), intent);
        NetworkTask doSyncTask = networkTaskMainUIBroadcastReceiver.getDoSyncTask();
        assertNull(doSyncTask);
    }

    private TestNetworkTaskMainUIBroadcastReceiver createTestReceiver() {
        NetworkTaskAdapter adapter = new NetworkTaskAdapter(Collections.emptyList(), (NetworkTaskMainActivity) getActivity(activityScenario));
        return new TestNetworkTaskMainUIBroadcastReceiver((NetworkTaskMainActivity) getActivity(activityScenario), adapter);
    }

    private NetworkTask getNetworkTask() {
        NetworkTask task = new NetworkTask();
        task.setId(0);
        task.setIndex(1);
        task.setSchedulerId(0);
        task.setInstances(0);
        task.setAddress("127.0.0.1");
        task.setPort(80);
        task.setAccessType(AccessType.PING);
        task.setInterval(15);
        task.setOnlyWifi(false);
        task.setNotification(true);
        task.setRunning(true);
        task.setLastScheduled(1);
        task.setFailureCount(1);
        task.setHighPrio(true);
        return task;
    }
}
