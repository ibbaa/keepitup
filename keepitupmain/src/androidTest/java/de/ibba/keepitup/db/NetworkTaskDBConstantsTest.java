package de.ibba.keepitup.db;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import org.junit.Test;
import org.junit.runner.RunWith;

import de.ibba.keepitup.R;
import de.ibba.keepitup.test.mock.TestRegistry;

import static org.junit.Assert.assertEquals;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class NetworkTaskDBConstantsTest {

    @Test
    public void testGetColumnNames() {
        NetworkTaskDBConstants dbConstants = new NetworkTaskDBConstants(TestRegistry.getContext());
        assertEquals(getResourceString(R.string.task_table_name), dbConstants.getTableName());
        assertEquals(getResourceString(R.string.task_id_column_name), dbConstants.getIdColumnName());
        assertEquals(getResourceString(R.string.task_index_column_name), dbConstants.getIndexColumnName());
        assertEquals(getResourceString(R.string.task_schedulerid_column_name), dbConstants.getSchedulerIdColumnName());
        assertEquals(getResourceString(R.string.task_instances_column_name), dbConstants.getInstancesColumnName());
        assertEquals(getResourceString(R.string.task_address_column_name), dbConstants.getAddressColumnName());
        assertEquals(getResourceString(R.string.task_port_column_name), dbConstants.getPortColumnName());
        assertEquals(getResourceString(R.string.task_accesstype_column_name), dbConstants.getAccessTypeColumnName());
        assertEquals(getResourceString(R.string.task_interval_column_name), dbConstants.getIntervalColumnName());
        assertEquals(getResourceString(R.string.task_onlywifi_column_name), dbConstants.getOnlyWifiColumnName());
        assertEquals(getResourceString(R.string.task_notification_column_name), dbConstants.getNotificationColumnName());
        assertEquals(getResourceString(R.string.task_running_column_name), dbConstants.getRunningColumnName());
        assertEquals(getResourceString(R.string.task_lastscheduled_column_name), dbConstants.getLastScheduledColumnName());
    }

    private String getResourceString(int id) {
        return TestRegistry.getContext().getResources().getString(id);
    }
}
