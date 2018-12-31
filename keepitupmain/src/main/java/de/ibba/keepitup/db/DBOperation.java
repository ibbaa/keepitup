package de.ibba.keepitup.db;

import android.database.sqlite.SQLiteDatabase;

import de.ibba.keepitup.model.NetworkJob;

@FunctionalInterface
interface DBOperation<T> {
    T execute(NetworkJob networkJob, SQLiteDatabase db);
}
