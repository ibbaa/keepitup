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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.app.Service;
import android.content.Intent;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import net.ibbaa.keepitup.model.AccessType;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.test.mock.MockAlarmMediaPlayer;
import net.ibbaa.keepitup.test.mock.TestAlarmService;
import net.ibbaa.keepitup.test.mock.TestNetworkTaskProcessServiceScheduler;
import net.ibbaa.keepitup.test.mock.TestRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class AlarmServiceMockTest {

    private TestAlarmService service;
    private TestNetworkTaskProcessServiceScheduler scheduler;

    @Before
    public void beforeEachTestMethod() {
        service = new TestAlarmService();
        service.onCreate();
        scheduler = (TestNetworkTaskProcessServiceScheduler) service.getNetworkTaskProcessServiceScheduler();
        service.reset();
    }

    @After
    public void afterEachTestMethod() {
        service.reset();
        service.onDestroy();
    }

    @Test
    public void testStartStop() {
        Intent intent = new Intent(TestRegistry.getContext(), TestAlarmService.class);
        intent.putExtra(AlarmService.getNetworkTaskBundleKey(), getNetworkTask().toBundle());
        assertFalse(TestAlarmService.isRunning());
        int startFlag = service.onStartCommand(intent, 1, 1);
        assertEquals(Service.START_NOT_STICKY, startFlag);
        assertFalse(service.wasStopCalled());
        assertTrue(TestAlarmService.isRunning());
        assertTrue(scheduler.wasRestartForegroundServiceCalled());
        MockAlarmMediaPlayer mediaPlayer = (MockAlarmMediaPlayer) service.getMediaPlayer();
        assertTrue(mediaPlayer.isPlaying());
        assertTrue(mediaPlayer.wasPlayAlarmCalled());
        assertTrue(service.wasStartPlayTimerCalled());
        scheduler.reset();
        service.onDestroy();
        assertFalse(TestAlarmService.isRunning());
        assertFalse(mediaPlayer.isPlaying());
        assertTrue(mediaPlayer.wasStopAlarmCalled());
        assertTrue(service.wasStopPlayTimerCalled());
        assertTrue(scheduler.wasRestartForegroundServiceCalled());
    }

    private NetworkTask getNetworkTask() {
        NetworkTask task = new NetworkTask();
        task.setId(0);
        task.setIndex(1);
        task.setSchedulerId(123);
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
