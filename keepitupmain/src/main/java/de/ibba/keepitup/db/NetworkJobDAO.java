package de.ibba.keepitup.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import de.ibba.keepitup.model.AccessType;
import de.ibba.keepitup.model.NetworkJob;

public class NetworkJobDAO {

    private final DBOpenHelper dbOpenHelper;
    private final Context context;

    public NetworkJobDAO(Context context) {
        dbOpenHelper = new DBOpenHelper(context);
        this.context = context;
    }

    public long insertNetworkJob(NetworkJob networkJob) {
        Log.d(NetworkJobDAO.class.getName(), "Inserting job " + networkJob);
        return executeDBOperationInTransaction(networkJob, this::insertNetworkJob);
    }

    public void deleteNetworkJob(long jobId) {
        Log.d(NetworkJobDAO.class.getName(), "Deleting job with id " + jobId);
        NetworkJob networkJob = new NetworkJob();
        networkJob.setId(jobId);
        executeDBOperationInTransaction(networkJob, this::deleteNetworkJob);
    }

    public void deleteAllNetworkJobs() {
        Log.d(NetworkJobDAO.class.getName(), "Deleting all jobs");
        executeDBOperationInTransaction(null, this::deleteAllNetworkJobs);
    }

    public void updateNetworkJobNotification(long jobId, boolean notification) {
        Log.d(NetworkJobDAO.class.getName(), "Updating notification status to " + notification + " of job with id " + jobId);
        NetworkJob networkJob = new NetworkJob();
        networkJob.setId(jobId);
        networkJob.setNotification(notification);
        executeDBOperationInTransaction(networkJob, this::updateNetworkJobNotification);
    }

    public void updateNetworkJobSuccess(long jobId, boolean success, String message) {
        Log.d(NetworkJobDAO.class.getName(), "Updating success status to " + success + " with a message " + message + " of job with id " + jobId);
        NetworkJob networkJob = new NetworkJob();
        networkJob.setId(jobId);
        networkJob.setSuccess(success);
        networkJob.setMessage(message);
        executeDBOperationInTransaction(networkJob, this::updateNetworkJobSuccess);
    }

    public void updateNetworkJobRunning(long jobId, boolean running) {
        Log.d(NetworkJobDAO.class.getName(), "Updating running status to " + running + " of job with id " + jobId);
        NetworkJob networkJob = new NetworkJob();
        networkJob.setId(jobId);
        networkJob.setRunning(running);
        executeDBOperationInTransaction(networkJob, this::updateNetworkJobRunning);
    }

    public List<NetworkJob> readAllNetworkJobs() {
        Log.d(NetworkJobDAO.class.getName(), "Read all jobs");
        return executeDBOperationInTransaction(null, this::readAllNetworkJobs);
    }

    public long readMaximumIndex() {
        Log.d(NetworkJobDAO.class.getName(), "Read maximum index");
        return executeDBOperationInTransaction(null, this::readMaximumIndex);
    }

    private long insertNetworkJob(NetworkJob networkJob, SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        JobDBConstants dbConstants = new JobDBConstants(context);
        values.put(dbConstants.getJobIndexColumnName(), networkJob.getIndex());
        values.put(dbConstants.getJobAddressColumnName(), networkJob.getAddress());
        values.put(dbConstants.getJobAccessTypeColumnName(), networkJob.getAccessType() == null ? null : networkJob.getAccessType().getCode());
        values.put(dbConstants.getJobIntervalColumnName(), networkJob.getInterval());
        values.put(dbConstants.getJobSuccessColumnName(), networkJob.isSuccess() ? 1 : 0);
        values.put(dbConstants.getJobMessageColumnName(), networkJob.getMessage());
        values.put(dbConstants.getJobNotificationColumnName(), networkJob.isNotification() ? 1 : 0);
        values.put(dbConstants.getJobRunningColumnName(), networkJob.isRunning() ? 1 : 0);
        long rowid = db.insert(dbConstants.getJobTableName(), null, values);
        if (rowid < 0) {
            Log.e(NetworkJobDAO.class.getName(), "Error inserting job into database. Insert returned -1.");
        }
        return rowid;
    }

    private int deleteNetworkJob(NetworkJob networkJob, SQLiteDatabase db) {
        JobDBConstants dbConstants = new JobDBConstants(context);
        String selection = dbConstants.getJobIdColumnName() + " = ?";
        String[] selectionArgs = {String.valueOf(networkJob.getId())};
        return db.delete(dbConstants.getJobTableName(), selection, selectionArgs);
    }

    @SuppressWarnings("unused")
    private int deleteAllNetworkJobs(NetworkJob networkJob, SQLiteDatabase db) {
        JobDBConstants dbConstants = new JobDBConstants(context);
        return db.delete(dbConstants.getJobTableName(), null, null);
    }

    private int updateNetworkJobRunning(NetworkJob networkJob, SQLiteDatabase db) {
        JobDBConstants dbConstants = new JobDBConstants(context);
        String selection = dbConstants.getJobIdColumnName() + " = ?";
        String[] selectionArgs = {String.valueOf(networkJob.getId())};
        ContentValues values = new ContentValues();
        values.put(dbConstants.getJobRunningColumnName(), networkJob.isRunning() ? 1 : 0);
        return db.update(dbConstants.getJobTableName(), values, selection, selectionArgs);
    }

    private int updateNetworkJobSuccess(NetworkJob networkJob, SQLiteDatabase db) {
        JobDBConstants dbConstants = new JobDBConstants(context);
        String selection = dbConstants.getJobIdColumnName() + " = ?";
        String[] selectionArgs = {String.valueOf(networkJob.getId())};
        ContentValues values = new ContentValues();
        values.put(dbConstants.getJobSuccessColumnName(), networkJob.isSuccess() ? 1 : 0);
        values.put(dbConstants.getJobMessageColumnName(), networkJob.getMessage());
        return db.update(dbConstants.getJobTableName(), values, selection, selectionArgs);
    }

    private int updateNetworkJobNotification(NetworkJob networkJob, SQLiteDatabase db) {
        JobDBConstants dbConstants = new JobDBConstants(context);
        String selection = dbConstants.getJobIdColumnName() + " = ?";
        String[] selectionArgs = {String.valueOf(networkJob.getId())};
        ContentValues values = new ContentValues();
        values.put(dbConstants.getJobNotificationColumnName(), networkJob.isNotification() ? 1 : 0);
        return db.update(dbConstants.getJobTableName(), values, selection, selectionArgs);
    }

    @SuppressWarnings("unused")
    private List<NetworkJob> readAllNetworkJobs(NetworkJob networkJob, SQLiteDatabase db) {
        Cursor cursor = null;
        List<NetworkJob> result = new ArrayList<>();
        JobDBConstants dbConstants = new JobDBConstants(context);
        try {
            cursor = db.rawQuery(dbConstants.getAllJobsStatement(), null);
            while (cursor.moveToNext()) {
                int indexIdColumn = cursor.getColumnIndex(dbConstants.getJobIdColumnName());
                if (!cursor.isNull(indexIdColumn)) {
                    NetworkJob mappedNetworkJob = mapCursorToNetworkJob(cursor);
                    result.add(mappedNetworkJob);
                }
            }
        } finally {
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Throwable exc) {
                    Log.e(NetworkJobDAO.class.getName(), "Error closing result cursor", exc);
                }
            }
        }
        return result;
    }

    @SuppressWarnings("unused")
    private long readMaximumIndex(NetworkJob networkJob, SQLiteDatabase db) {
        Cursor cursor = null;
        long index = 0;
        JobDBConstants dbConstants = new JobDBConstants(context);
        try {
            cursor = db.rawQuery(dbConstants.getMaximumIndexStatement(), null);
            while (cursor.moveToNext()) {
                if (!cursor.isNull(0)) {
                    index = cursor.getLong(0);
                }
            }
        } finally {
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Throwable exc) {
                    Log.e(NetworkJobDAO.class.getName(), "Error closing result cursor", exc);
                }
            }
        }
        return index;
    }

    private NetworkJob mapCursorToNetworkJob(Cursor cursor) {
        NetworkJob networkJob = new NetworkJob();
        JobDBConstants dbConstants = new JobDBConstants(context);
        int indexIdColumn = cursor.getColumnIndex(dbConstants.getJobIdColumnName());
        int indexIndexColumn = cursor.getColumnIndex(dbConstants.getJobIndexColumnName());
        int indexAddressColumn = cursor.getColumnIndex(dbConstants.getJobAddressColumnName());
        int indexAccessTypeColumn = cursor.getColumnIndex(dbConstants.getJobAccessTypeColumnName());
        int indexIntervalColumn = cursor.getColumnIndex(dbConstants.getJobIntervalColumnName());
        int indexSuccessColumn = cursor.getColumnIndex(dbConstants.getJobSuccessColumnName());
        int indexMessageColumn = cursor.getColumnIndex(dbConstants.getJobMessageColumnName());
        int indexNotificationColumn = cursor.getColumnIndex(dbConstants.getJobNotificationColumnName());
        int indexRunningColumn = cursor.getColumnIndex(dbConstants.getJobRunningColumnName());
        networkJob.setId(cursor.getLong(indexIdColumn));
        networkJob.setIndex(cursor.getLong(indexIndexColumn));
        networkJob.setAddress(cursor.getString(indexAddressColumn));
        if (cursor.isNull(indexAccessTypeColumn)) {
            networkJob.setAccessType(null);
        } else {
            networkJob.setAccessType(AccessType.forCode(cursor.getInt(indexAccessTypeColumn)));
        }
        networkJob.setInterval(cursor.getLong(indexIntervalColumn));
        networkJob.setSuccess(cursor.getInt(indexSuccessColumn) >= 1);
        networkJob.setMessage(cursor.getString(indexMessageColumn));
        networkJob.setNotification(cursor.getInt(indexNotificationColumn) >= 1);
        networkJob.setRunning(cursor.getInt(indexRunningColumn) >= 1);
        return networkJob;
    }

    private <T> T executeDBOperationInTransaction(NetworkJob networkJob, DBOperation<T> dbOperation) {
        SQLiteDatabase db = null;
        T result;
        try {
            db = dbOpenHelper.getWritableDatabase();
            db.beginTransaction();
            result = dbOperation.execute(networkJob, db);
            db.setTransactionSuccessful();
        } catch (Throwable exc) {
            Log.e(NetworkJobDAO.class.getName(), "Error executing database operation", exc);
            throw exc;
        } finally {
            if (db != null) {
                try {
                    db.endTransaction();
                } catch (Throwable exc) {
                    Log.e(NetworkJobDAO.class.getName(), "Error committing changes to database", exc);
                }
                try {
                    db.close();
                } catch (Throwable exc) {
                    Log.e(NetworkJobDAO.class.getName(), "Error closing database", exc);
                }
            }
        }
        return result;
    }
}
