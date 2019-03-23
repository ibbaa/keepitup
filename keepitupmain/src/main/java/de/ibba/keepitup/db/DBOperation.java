package de.ibba.keepitup.db;

import android.database.sqlite.SQLiteDatabase;

@FunctionalInterface
interface DBOperation<S, T> {
    T execute(S modelObject, SQLiteDatabase db);
}
