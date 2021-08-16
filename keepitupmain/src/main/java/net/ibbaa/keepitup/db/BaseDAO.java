package net.ibbaa.keepitup.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import net.ibbaa.keepitup.logging.Log;

public abstract class BaseDAO {

    private final Context context;

    protected BaseDAO(Context context) {
        this.context = context;
    }

    protected <S, T> T executeDBOperationInTransaction(S modelObject, DBOperation<S, T> dbOperation) {
        Log.d(BaseDAO.class.getName(), "Executing db operation on " + modelObject);
        SQLiteDatabase db = null;
        T result;
        try {
            db = DBOpenHelper.getInstance(getContext()).getWritableDatabase();
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
            }
        }
        return result;
    }

    protected Context getContext() {
        return context;
    }
}
