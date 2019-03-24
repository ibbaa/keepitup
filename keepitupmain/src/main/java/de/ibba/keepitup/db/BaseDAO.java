package de.ibba.keepitup.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public abstract class BaseDAO {

    private final DBOpenHelper dbOpenHelper;
    private final Context context;

    protected BaseDAO(Context context) {
        this.context = context;
        dbOpenHelper = new DBOpenHelper(context);
    }

    protected <S, T> T executeDBOperationInTransaction(S modelObject, DBOperation<S, T> dbOperation) {
        Log.d(BaseDAO.class.getName(), "Executing db operation on " + modelObject);
        SQLiteDatabase db = null;
        T result;
        try {
            db = dbOpenHelper.getWritableDatabase();
            db.beginTransaction();
            result = dbOperation.execute(modelObject, db);
            db.setTransactionSuccessful();
        } catch (Throwable exc) {
            Log.e(BaseDAO.class.getName(), "Error executing database operation", exc);
            throw exc;
        } finally {
            if (db != null) {
                try {
                    db.endTransaction();
                } catch (Throwable exc) {
                    Log.e(BaseDAO.class.getName(), "Error committing changes to database", exc);
                }
                try {
                    db.close();
                } catch (Throwable exc) {
                    Log.e(BaseDAO.class.getName(), "Error closing database", exc);
                }
            }
        }
        return result;
    }

    protected DBOpenHelper getDBOpenHelper() {
        return dbOpenHelper;
    }

    protected Context getContext() {
        return context;
    }
}
