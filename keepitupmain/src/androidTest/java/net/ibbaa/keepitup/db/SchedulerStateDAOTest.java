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

package net.ibbaa.keepitup.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import net.ibbaa.keepitup.logging.Dump;
import net.ibbaa.keepitup.model.SchedulerState;
import net.ibbaa.keepitup.test.mock.TestRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SchedulerStateDAOTest {

    private SchedulerStateDAO schedulerStateDAO;

    @Before
    public void beforeEachTestMethod() {
        Dump.initialize(null);
        schedulerStateDAO = new SchedulerStateDAO(TestRegistry.getContext());
        schedulerStateDAO.deleteSchedulerState();
    }

    @After
    public void afterEachTestMethod() {
        schedulerStateDAO.deleteSchedulerState();
    }

    @Test
    public void testInsertReadDelete() {
        assertNull(schedulerStateDAO.readSchedulerState());
        schedulerStateDAO.insertSchedulerState(new SchedulerState(0, true, 12345));
        SchedulerState schedulerState = schedulerStateDAO.readSchedulerState();
        assertTrue(schedulerState.isSuspended());
        assertEquals(12345, schedulerState.getTimestamp());
        schedulerStateDAO.insertSchedulerState(new SchedulerState(0, false, 54321));
        schedulerState = schedulerStateDAO.readSchedulerState();
        assertFalse(schedulerState.isSuspended());
        assertEquals(54321, schedulerState.getTimestamp());
        schedulerStateDAO.deleteSchedulerState();
        assertNull(schedulerStateDAO.readSchedulerState());
    }

    @Test
    public void testUpdate() {
        schedulerStateDAO.insertSchedulerState(new SchedulerState(0, true, 12345));
        schedulerStateDAO.updateSchedulerState(new SchedulerState(0, false, 54321));
        SchedulerState schedulerState = schedulerStateDAO.readSchedulerState();
        assertFalse(schedulerState.isSuspended());
        assertEquals(54321, schedulerState.getTimestamp());
    }
}
