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
public class SchedulerIdHistoryDBConstantsTest {

    @Test
    public void testGetColumnNames() {
        SchedulerIdHistoryDBConstants dbConstants = new SchedulerIdHistoryDBConstants(TestRegistry.getContext());
        assertEquals(getResourceString(R.string.schedulerid_table_name), dbConstants.getTableName());
        assertEquals(getResourceString(R.string.schedulerid_history_id_column_name), dbConstants.getIdColumnName());
        assertEquals(getResourceString(R.string.schedulerid_history_schedulerid_column_name), dbConstants.getSchedulerIdColumnName());
        assertEquals(getResourceString(R.string.schedulerid_history_timestamp_column_name), dbConstants.getTimestampColumnName());
    }

    private String getResourceString(int id) {
        return TestRegistry.getContext().getResources().getString(id);
    }
}
