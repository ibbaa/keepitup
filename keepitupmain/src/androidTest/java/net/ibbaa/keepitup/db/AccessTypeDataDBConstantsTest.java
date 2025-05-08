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

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.test.mock.TestRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class AccessTypeDataDBConstantsTest {

    @Test
    public void testGetColumnNames() {
        AccessTypeDataDBConstants dbConstants = new AccessTypeDataDBConstants(TestRegistry.getContext());
        assertEquals(getResourceString(R.string.accesstypedata_table_name), dbConstants.getTableName());
        assertEquals(getResourceString(R.string.accesstypedata_id_column_name), dbConstants.getIdColumnName());
        assertEquals(getResourceString(R.string.accesstypedata_taskid_column_name), dbConstants.getNetworkTaskIdColumnName());
        assertEquals(getResourceString(R.string.accesstypedata_pingcount_column_name), dbConstants.getPingCountColumnName());
        assertEquals(getResourceString(R.string.accesstypedata_pingpackagesize_column_name), dbConstants.getPingPackageSizeColumnName());
        assertEquals(getResourceString(R.string.accesstypedata_connectcount_column_name), dbConstants.getConnectCountColumnName());
        assertEquals(getResourceString(R.string.accesstypedata_stoponsuccess_column_name), dbConstants.getStopOnSuccessColumnName());
        assertEquals(getResourceString(R.string.accesstypedata_ignoresslerror_column_name), dbConstants.getIgnoreSSLErrorColumnName());
    }

    private String getResourceString(int id) {
        return TestRegistry.getContext().getResources().getString(id);
    }
}
