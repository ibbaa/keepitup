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
public class LogDBConstantsTest {

    @Test
    public void testGetColumnNames() {
        LogDBConstants dbConstants = new LogDBConstants(TestRegistry.getContext());
        assertEquals(getResourceString(R.string.log_table_name), dbConstants.getTableName());
        assertEquals(getResourceString(R.string.log_id_column_name), dbConstants.getIdColumnName());
        assertEquals(getResourceString(R.string.log_taskid_column_name), dbConstants.getNetworkTaskIdColumnName());
        assertEquals(getResourceString(R.string.log_timestamp_column_name), dbConstants.getTimestampColumnName());
        assertEquals(getResourceString(R.string.log_success_column_name), dbConstants.getSuccessColumnName());
        assertEquals(getResourceString(R.string.log_message_column_name), dbConstants.getMessageColumnName());
    }

    private String getResourceString(int id) {
        return TestRegistry.getContext().getResources().getString(id);
    }
}
