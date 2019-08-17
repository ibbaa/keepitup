package de.ibba.keepitup.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.model.SchedulerId;

public class SchedulerIdHistoryDAO extends BaseDAO {

    public SchedulerIdHistoryDAO(Context context) {
        super(context);
    }

    public void deleteAllSchedulerIds() {
        Log.d(SchedulerIdHistoryDAO.class.getName(), "Deleting all scheduler ids");
        executeDBOperationInTransaction((NetworkTask) null, this::deleteAllSchedulerIds);
    }

    public List<SchedulerId> readAllSchedulerIds() {
        Log.d(SchedulerIdHistoryDAO.class.getName(), "Reading all scheduler ids");
        return executeDBOperationInTransaction((SchedulerId) null, this::readAllSchedulerIds);
    }

    private int deleteAllSchedulerIds(NetworkTask networkTask, SQLiteDatabase db) {
        SchedulerIdHistoryDBConstants dbConstants = new SchedulerIdHistoryDBConstants(getContext());
        return db.delete(dbConstants.getTableName(), null, null);
    }

    private List<SchedulerId> readAllSchedulerIds(SchedulerId scheduldeId, SQLiteDatabase db) {
        Cursor cursor = null;
        List<SchedulerId> result = new ArrayList<>();
        SchedulerIdHistoryDBConstants dbConstants = new SchedulerIdHistoryDBConstants(getContext());
        try {
            cursor = db.rawQuery(dbConstants.getReadAllSchedulerIdHistoryEntriesStatement(), null);
            while (cursor.moveToNext()) {
                int indexIdColumn = cursor.getColumnIndex(dbConstants.getIdColumnName());
                if (!cursor.isNull(indexIdColumn)) {
                    SchedulerId mappedSchedulerId = mapCursorToSchedulerId(cursor);
                    result.add(mappedSchedulerId);
                }
            }
        } finally {
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Throwable exc) {
                    Log.e(SchedulerIdHistoryDAO.class.getName(), "Error closing result cursor", exc);
                }
            }
        }
        return result;
    }

    private SchedulerId mapCursorToSchedulerId(Cursor cursor) {
        SchedulerId schedulerId = new SchedulerId();
        SchedulerIdHistoryDBConstants dbConstants = new SchedulerIdHistoryDBConstants(getContext());
        int indexIdColumn = cursor.getColumnIndex(dbConstants.getIdColumnName());
        int indexSchedulerIdColumn = cursor.getColumnIndex(dbConstants.getSchedulerIdColumnName());
        int indexTimestampColumn = cursor.getColumnIndex(dbConstants.getTimestampColumnName());
        schedulerId.setId(cursor.getInt(indexIdColumn));
        schedulerId.setSchedulerId(cursor.getInt(indexSchedulerIdColumn));
        schedulerId.setTimestamp(cursor.getLong(indexTimestampColumn));
        return schedulerId;
    }
}