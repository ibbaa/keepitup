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

package net.ibbaa.keepitup.ui.adapter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import net.ibbaa.keepitup.model.AccessType;
import net.ibbaa.keepitup.model.AccessTypeData;
import net.ibbaa.keepitup.model.LogEntry;
import net.ibbaa.keepitup.model.NetworkTask;

import org.junit.Test;
import org.junit.runner.RunWith;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class NetworkTaskUIWrapperTest {

    @Test
    public void testIsEqual() {
        NetworkTaskUIWrapper wrapper1 = new NetworkTaskUIWrapper(getNetworkTask(), getAccessTypeData(), getLogEntry());
        NetworkTaskUIWrapper wrapper2 = new NetworkTaskUIWrapper(null, null, null);
        assertFalse(wrapper1.isEqual(wrapper2));
        wrapper2 = new NetworkTaskUIWrapper(null, null, getLogEntry());
        assertFalse(wrapper1.isEqual(wrapper2));
        assertFalse(wrapper2.isEqual(wrapper1));
        wrapper2 = new NetworkTaskUIWrapper(null, getAccessTypeData(), getLogEntry());
        assertFalse(wrapper1.isEqual(wrapper2));
        assertFalse(wrapper2.isEqual(wrapper1));
        wrapper2 = new NetworkTaskUIWrapper(getNetworkTask(), getAccessTypeData(), getLogEntry());
        assertTrue(wrapper1.isEqual(wrapper2));
        assertTrue(wrapper2.isEqual(wrapper1));
        NetworkTask task = getNetworkTask();
        task.setInstances(3);
        wrapper2 = new NetworkTaskUIWrapper(task, getAccessTypeData(), getLogEntry());
        assertFalse(wrapper1.isEqual(wrapper2));
        assertFalse(wrapper2.isEqual(wrapper1));
        LogEntry entry = getLogEntry();
        entry.setNetworkTaskId(2);
        wrapper2 = new NetworkTaskUIWrapper(getNetworkTask(), getAccessTypeData(), entry);
        assertFalse(wrapper1.isEqual(wrapper2));
        assertFalse(wrapper2.isEqual(wrapper1));
        AccessTypeData data = getAccessTypeData();
        data.setConnectCount(25);
        wrapper2 = new NetworkTaskUIWrapper(getNetworkTask(), data, getLogEntry());
        assertFalse(wrapper1.isEqual(wrapper2));
        assertFalse(wrapper2.isEqual(wrapper1));
        wrapper1 = new NetworkTaskUIWrapper(null, null, null);
        wrapper2 = new NetworkTaskUIWrapper(null, null, null);
        assertTrue(wrapper1.isEqual(wrapper2));
    }

    private NetworkTask getNetworkTask() {
        NetworkTask task = new NetworkTask();
        task.setId(0);
        task.setIndex(1);
        task.setSchedulerId(0);
        task.setName("name");
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

    private LogEntry getLogEntry() {
        LogEntry insertedLogEntry1 = new LogEntry();
        insertedLogEntry1.setId(0);
        insertedLogEntry1.setNetworkTaskId(1);
        insertedLogEntry1.setSuccess(true);
        insertedLogEntry1.setTimestamp(123);
        insertedLogEntry1.setMessage("TestMessage1");
        return insertedLogEntry1;
    }

    private AccessTypeData getAccessTypeData() {
        AccessTypeData data = new AccessTypeData();
        data.setId(0);
        data.setNetworkTaskId(0);
        data.setPingCount(10);
        data.setPingPackageSize(1234);
        data.setConnectCount(3);
        data.setStopOnSuccess(true);
        data.setIgnoreSSLError(true);
        return data;
    }
}
