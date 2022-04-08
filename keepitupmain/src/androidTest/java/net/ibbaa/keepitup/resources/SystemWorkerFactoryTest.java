/*
 * Copyright (c) 2022. Alwin Ibba
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

package net.ibbaa.keepitup.resources;

import static org.junit.Assert.assertEquals;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import net.ibbaa.keepitup.model.AccessType;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.service.ConnectNetworkTaskWorker;
import net.ibbaa.keepitup.service.DownloadNetworkTaskWorker;
import net.ibbaa.keepitup.service.NullNetworkTaskWorker;
import net.ibbaa.keepitup.service.PingNetworkTaskWorker;
import net.ibbaa.keepitup.test.mock.TestRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class SystemWorkerFactoryTest {

    private SystemWorkerFactory systemWorkerFactory;

    @Before
    public void beforeEachTestMethod() {
        systemWorkerFactory = new SystemWorkerFactory();
    }

    @Test
    public void testCreateWorker() {
        assertEquals(NullNetworkTaskWorker.class, systemWorkerFactory.createWorker(TestRegistry.getContext(), getNetworkTask(null), null).getClass());
        assertEquals(PingNetworkTaskWorker.class, systemWorkerFactory.createWorker(TestRegistry.getContext(), getNetworkTask(AccessType.PING), null).getClass());
        assertEquals(ConnectNetworkTaskWorker.class, systemWorkerFactory.createWorker(TestRegistry.getContext(), getNetworkTask(AccessType.CONNECT), null).getClass());
        assertEquals(DownloadNetworkTaskWorker.class, systemWorkerFactory.createWorker(TestRegistry.getContext(), getNetworkTask(AccessType.DOWNLOAD), null).getClass());
    }

    private NetworkTask getNetworkTask(AccessType type) {
        NetworkTask task = new NetworkTask();
        task.setAccessType(type);
        return task;
    }
}
