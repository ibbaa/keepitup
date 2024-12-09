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

package net.ibbaa.keepitup.db;

import static org.junit.Assert.assertEquals;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.test.mock.TestRegistry;

import org.junit.Test;

public class SchedulerStateDBConstantsTest {

    @Test
    public void testGetColumnNames() {
        SchedulerStateDBConstants dbConstants = new SchedulerStateDBConstants(TestRegistry.getContext());
        assertEquals(getResourceString(R.string.scheduler_state_table_name), dbConstants.getTableName());
        assertEquals(getResourceString(R.string.scheduler_state_id_column_name), dbConstants.getIdColumnName());
        assertEquals(getResourceString(R.string.scheduler_state_suspended_column_name), dbConstants.getSuspendedColumnName());
        assertEquals(getResourceString(R.string.scheduler_state_timestamp_column_name), dbConstants.getTimestampColumnName());
    }

    private String getResourceString(int id) {
        return TestRegistry.getContext().getResources().getString(id);
    }
}
