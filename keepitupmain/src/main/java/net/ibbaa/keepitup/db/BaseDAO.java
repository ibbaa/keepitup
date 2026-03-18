/*
 * Copyright (c) 2026 Alwin Ibba
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

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.resources.encryption.AlgorithmData;
import net.ibbaa.keepitup.resources.encryption.CipherManager;
import net.ibbaa.keepitup.resources.encryption.MainKeyAccess;

import java.util.Map;

public abstract class BaseDAO {

    private final Context context;

    protected BaseDAO(Context context) {
        this.context = context;
    }

    protected <S, T> T executeDBOperationInTransaction(S modelObject, DBOperation<S, T> dbOperation) {
        return executeInternal(modelObject, (obj, db) -> new DBResult<>(true, dbOperation.execute(obj, db)));
    }

    protected <S, T> T executeDBOperationInTransactionWithRollback(S modelObject, DBOperation<S, DBResult<T>> dbOperation) {
        return executeInternal(modelObject, dbOperation);
    }

    private <S, T> T executeInternal(S modelObject, DBOperation<S, DBResult<T>> dbOperation) {
        Log.d(BaseDAO.class.getName(), "executeInternal");
        Log.d(BaseDAO.class.getName(), "Executing db operation on " + modelObject);
        SQLiteDatabase db = null;
        DBResult<T> result;
        try {
            db = DBOpenHelper.getInstance(getContext()).getWritableDatabase();
            db.beginTransaction();
            result = dbOperation.execute(modelObject, db);
            if (result.success()) {
                db.setTransactionSuccessful();
            }
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
        return result.value();
    }

    protected EncryptResult encrypt(ContentValues values, String valueColumn, String ivColumn, String value, String aad) {
        MainKeyAccess mainKeyAccess = new MainKeyAccess(getContext());
        MainKeyAccess.MainKey mainKey = mainKeyAccess.getMainKey();
        if (!mainKey.success()) {
            return new EncryptResult(false, mainKey.keyDecryptError(), mainKey.message());
        }
        byte[] mainKeyBytes = mainKey.key();
        AlgorithmData algorithmData = new AlgorithmData(getContext());
        Map<String, String> algorithmParam = algorithmData.getAlgorithmDefaultParam(AlgorithmData.Algorithm.AES256GCM);
        CipherManager cipherManager = new CipherManager(getContext());
        CipherManager.EncryptionResult encryptionResult = cipherManager.encrypt(algorithmParam, mainKeyBytes, aad, value, false);
        if (!encryptionResult.success()) {
            return new EncryptResult(false, false, encryptionResult.message());
        }
        values.put(valueColumn, encryptionResult.ciphertext());
        String iv = algorithmParam.get(getResources().getString(R.string.iv_json_key));
        values.put(ivColumn, iv);
        return new EncryptResult(true, false, encryptionResult.message());
    }

    protected DecryptResult decrypt(Cursor cursor, String valueColumn, String ivColumn, String aad) {
        MainKeyAccess mainKeyAccess = new MainKeyAccess(getContext());
        MainKeyAccess.MainKey mainKey = mainKeyAccess.getMainKey();
        if (!mainKey.success()) {
            return new DecryptResult(false, mainKey.keyDecryptError(), mainKey.message(), null);
        }
        byte[] mainKeyBytes = mainKey.key();
        int indexValueColumn = cursor.getColumnIndex(valueColumn);
        int indexIVColumn = cursor.getColumnIndex(ivColumn);
        String value = cursor.getString(indexValueColumn);
        String iv = cursor.getString(indexIVColumn);
        AlgorithmData algorithmData = new AlgorithmData(getContext());
        Map<String, String> algorithmParam = algorithmData.getAlgorithmDefaultParam(AlgorithmData.Algorithm.AES256GCM);
        algorithmParam.put(getResources().getString(R.string.iv_json_key), iv);
        CipherManager cipherManager = new CipherManager(getContext());
        CipherManager.DecryptionResult decryptionResult = cipherManager.decrypt(algorithmParam, mainKeyBytes, aad, value, false);
        if (!decryptionResult.success()) {
            return new DecryptResult(false, false, decryptionResult.message(), null);
        }
        return new DecryptResult(true, false, decryptionResult.message(), decryptionResult.plaintext());
    }

    protected Context getContext() {
        return context;
    }

    protected Resources getResources() {
        return getContext().getResources();
    }

    protected record EncryptResult(boolean success, boolean keyDecryptError, String message) {
    }

    protected record DecryptResult(boolean success, boolean keyDecryptError, String message, String plainText) {
    }

    protected record DBResult<T>(boolean success, T value) {
    }
}
