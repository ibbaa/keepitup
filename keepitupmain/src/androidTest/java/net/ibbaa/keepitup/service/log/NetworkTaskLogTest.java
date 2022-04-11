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

package net.ibbaa.keepitup.service.log;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import net.ibbaa.keepitup.logging.ILogger;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.test.mock.TestRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class NetworkTaskLogTest {

    @Test
    public void testInitializeNegativeIndex() {
        NetworkTask task = getNetworkTask();
        task.setIndex(-1);
        NetworkTaskLog.initialize(TestRegistry.getContext(), task);
        assertNull(NetworkTaskLog.getLogger(TestRegistry.getContext(), task));
    }

    @Test
    public void testGetLogger() {
        ILogger logger = NetworkTaskLog.getLogger(TestRegistry.getContext(), getNetworkTask());
        assertNotNull(logger);
        ILogger logger2 = NetworkTaskLog.getLogger(TestRegistry.getContext(), getNetworkTask());
        assertNotNull(logger2);
        assertSame(logger, logger2);
        ILogger logger3 = NetworkTaskLog.getLogger(TestRegistry.getContext(), getNetworkTask2());
        assertNotNull(logger3);
        assertNotSame(logger, logger3);
    }

    @Test
    public void testClear() {
        ILogger logger = NetworkTaskLog.getLogger(TestRegistry.getContext(), getNetworkTask());
        assertNotNull(logger);
        NetworkTaskLog.clear();
        ILogger logger2 = NetworkTaskLog.getLogger(TestRegistry.getContext(), getNetworkTask());
        assertNotNull(logger2);
        assertNotSame(logger, logger2);
    }

    private NetworkTask getNetworkTask() {
        NetworkTask task = new NetworkTask();
        task.setIndex(1);
        task.setSchedulerId(567);
        task.setAddress("host.ibbaa.net");
        return task;
    }

    private NetworkTask getNetworkTask2() {
        NetworkTask task = new NetworkTask();
        task.setIndex(1);
        task.setSchedulerId(345);
        task.setAddress("host.ibbaa.net");
        return task;
    }
}
