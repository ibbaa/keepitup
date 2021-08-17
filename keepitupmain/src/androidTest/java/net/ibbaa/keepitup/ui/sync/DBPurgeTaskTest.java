/*
 * Copyright (c) 2021. Alwin Ibba
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

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.ibbaa.keepitup.model.LogEntry;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.ui.BaseUITest;
import net.ibbaa.keepitup.ui.NetworkTaskMainActivity;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class DBPurgeTaskTest extends BaseUITest {

    private ActivityScenario<?> activityScenario;

    @Before
    public void beforeEachTestMethod() {
        super.beforeEachTestMethod();
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class);
    }

    @After
    public void afterEachTestMethod() {
        super.afterEachTestMethod();
        activityScenario.close();
    }

    @Test
    public void testPurge() {
        getNetworkTaskDAO().insertNetworkTask(new NetworkTask());
        getLogDAO().insertAndDeleteLog(new LogEntry());
        assertFalse(getNetworkTaskDAO().readAllNetworkTasks().isEmpty());
        assertFalse(getSchedulerIdHistoryDAO().readAllSchedulerIds().isEmpty());
        assertFalse(getLogDAO().readAllLogs().isEmpty());
        DBPurgeTask task = new DBPurgeTask(getActivity(activityScenario));
        assertTrue(task.runInBackground());
        assertTrue(getNetworkTaskDAO().readAllNetworkTasks().isEmpty());
        assertTrue(getSchedulerIdHistoryDAO().readAllSchedulerIds().isEmpty());
        assertTrue(getLogDAO().readAllLogs().isEmpty());
    }
}
