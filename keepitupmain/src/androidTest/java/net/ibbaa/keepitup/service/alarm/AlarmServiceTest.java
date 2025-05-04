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

package net.ibbaa.keepitup.service.alarm;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.Intent;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.model.AccessType;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.test.mock.TestRegistry;
import net.ibbaa.keepitup.test.mock.TestUtil;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class AlarmServiceTest {

    @Before
    public void beforeEachTestMethod() {
        stopAlarmService();
    }

    @Test
    public void testNoNetworkTask() {
        Intent startIntent = new Intent(TestRegistry.getContext(), AlarmService.class);
        startIntent.putExtra(TestRegistry.getContext().getResources().getString(R.string.task_alarm_duration_key), 300);
        startIntent.setPackage(TestRegistry.getContext().getPackageName());
        TestRegistry.getContext().startService(startIntent);
        TestUtil.waitUntil(() -> !AlarmService.isRunning(), 300);
        assertFalse(AlarmService.isRunning());
    }

    @Test
    public void testRemoveNetworkTask() throws Exception {
        Intent startIntent = new Intent(TestRegistry.getContext(), AlarmService.class);
        startIntent.putExtra(TestRegistry.getContext().getResources().getString(R.string.task_alarm_duration_key), 10);
        startIntent.putExtra(AlarmService.getNetworkTaskBundleKey(), getNetworkTask(123).toBundle());
        startIntent.setPackage(TestRegistry.getContext().getPackageName());
        assertFalse(AlarmService.isRunning());
        TestRegistry.getContext().startService(startIntent);
        TestUtil.waitUntil(AlarmService::isRunning, 300);
        assertTrue(AlarmService.isRunning());
        startIntent = new Intent(TestRegistry.getContext(), AlarmService.class);
        startIntent.putExtra(TestRegistry.getContext().getResources().getString(R.string.task_alarm_duration_key), 10);
        startIntent.putExtra(AlarmService.getNetworkTaskBundleKey(), getNetworkTask(456).toBundle());
        startIntent.setPackage(TestRegistry.getContext().getPackageName());
        TestRegistry.getContext().startService(startIntent);
        Thread.sleep(1000);
        AlarmService.removeNetworkTask(TestRegistry.getContext(), getNetworkTask(123));
        assertTrue(AlarmService.isRunning());
        AlarmService.removeNetworkTask(TestRegistry.getContext(), getNetworkTask(456));
        TestUtil.waitUntil(() -> !AlarmService.isRunning(), 300);
        assertFalse(AlarmService.isRunning());
    }

    @Test
    public void testPlaybackTimeout() throws Exception {
        Intent startIntent = new Intent(TestRegistry.getContext(), AlarmService.class);
        startIntent.putExtra(TestRegistry.getContext().getResources().getString(R.string.task_alarm_duration_key), 10);
        startIntent.putExtra(AlarmService.getNetworkTaskBundleKey(), getNetworkTask(123).toBundle());
        startIntent.setPackage(TestRegistry.getContext().getPackageName());
        assertFalse(AlarmService.isRunning());
        TestRegistry.getContext().startService(startIntent);
        TestUtil.waitUntil(AlarmService::isRunning, 300);
        assertTrue(AlarmService.isRunning());
        Thread.sleep(12000);
        assertFalse(AlarmService.isRunning());
    }

    public void stopAlarmService() {
        if (!AlarmService.isRunning()) {
            return;
        }
        TestRegistry.getContext().stopService(new Intent(TestRegistry.getContext(), AlarmService.class));
        TestUtil.waitUntil(() -> !AlarmService.isRunning(), 100);
    }

    private NetworkTask getNetworkTask(int schedulerId) {
        NetworkTask task = new NetworkTask();
        task.setId(0);
        task.setIndex(1);
        task.setSchedulerId(schedulerId);
        task.setInstances(1);
        task.setAddress("127.0.0.1");
        task.setPort(80);
        task.setAccessType(AccessType.PING);
        task.setInterval(15);
        task.setOnlyWifi(false);
        task.setNotification(true);
        task.setRunning(true);
        task.setLastScheduled(0);
        task.setFailureCount(2);
        task.setHighPrio(true);
        return task;
    }
}
