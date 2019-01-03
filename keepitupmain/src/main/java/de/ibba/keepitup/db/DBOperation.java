package de.ibba.keepitup.db;

import android.database.sqlite.SQLiteDatabase;

import de.ibba.keepitup.model.NetworkTask;

@FunctionalInterface
interface DBOperation<T> {
    T execute(NetworkTask networkTask, SQLiteDatabase db);
}
