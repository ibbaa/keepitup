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
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import net.ibbaa.keepitup.BuildConfig;
import net.ibbaa.keepitup.logging.Dump;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.model.Header;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HeaderDAO extends BaseDAO {

    public HeaderDAO(Context context) {
        super(context);
    }

    public Header insertHeader(Header header) {
        Log.d(HeaderDAO.class.getName(), "Inserting header " + header);
        Header returnedHeader = executeDBOperationInTransaction(header, this::insertHeader);
        Log.d(HeaderDAO.class.getName(), "Inserted header is " + returnedHeader);
        dumpDatabase("Dump after insertHeader call");
        return returnedHeader;
    }

    public int insertHeaders(List<Header> headers) {
        Log.d(HeaderDAO.class.getName(), "Inserting headers " + headers);
        int count = executeDBOperationInTransactionWithRollback(headers, this::insertHeaders);
        dumpDatabase("Dump after insertHeader call");
        return count;
    }

    public void updateHeader(Header header) {
        Log.d(HeaderDAO.class.getName(), "Updating header with id " + header.getId());
        Header returnedHeader = executeDBOperationInTransaction(header, this::updateHeader);
        Log.d(HeaderDAO.class.getName(), "Updated header is " + returnedHeader);
        dumpDatabase("Dump after updateHeader call");
    }

    public List<Header> readGlobalHeaders() {
        Log.d(HeaderDAO.class.getName(), "Reading global headers");
        List<Header> headerList = executeDBOperationInTransaction((Header) null, this::readGlobalHeaders);
        Log.d(HeaderDAO.class.getName(), "Number of headers read: " + headerList.size());
        return headerList;
    }

    public Map<Long, List<Header>> readAllHeadersForNetworkTasks() {
        Log.d(HeaderDAO.class.getName(), "Reading all headers for all network tasks");
        return executeDBOperationInTransaction((Header) null, this::readAllHeadersForNetworkTasks);
    }

    public List<Header> readHeadersForNetworkTask(long networkTaskId) {
        Log.d(HeaderDAO.class.getName(), "Reading headers for network task with id " + networkTaskId);
        Header header = new Header();
        header.setNetworkTaskId(networkTaskId);
        List<Header> headerList = executeDBOperationInTransaction(header, this::readHeadersForNetworkTask);
        Log.d(HeaderDAO.class.getName(), "Number of headers read: " + headerList.size());
        return headerList;
    }

    public List<Header> readAllHeaders() {
        Log.d(HeaderDAO.class.getName(), "Reading all headers");
        List<Header> headerList = executeDBOperationInTransaction((Header) null, this::readAllHeaders);
        Log.d(HeaderDAO.class.getName(), "Number of headers read: " + headerList.size());
        return headerList;
    }

    public void deleteGlobalHeaders() {
        Log.d(HeaderDAO.class.getName(), "Deleting all global headers");
        executeDBOperationInTransaction((Header) null, this::deleteGlobalHeaders);
        dumpDatabase("Dump after deleteGlobalHeaders call");
    }

    public void deleteHeader(Header header) {
        Log.d(HeaderDAO.class.getName(), "Deleting header with id " + header.getId());
        executeDBOperationInTransaction(header, this::deleteHeader);
        dumpDatabase("Dump after deleteInterval call");
    }

    public void deleteHeadersForNetworkTask(long networkTaskId) {
        Log.d(HeaderDAO.class.getName(), "Deleting all headers for network task with id " + networkTaskId);
        Header header = new Header();
        header.setNetworkTaskId(networkTaskId);
        executeDBOperationInTransaction(header, this::deleteHeadersForNetworkTask);
        dumpDatabase("Dump after deleteHeadersForNetworkTask call");
    }

    public void deleteAllOrphanHeaders() {
        Log.d(HeaderDAO.class.getName(), "Deleting all orphan headers");
        executeDBOperationInTransaction((Header) null, this::deleteAllOrphanHeaders);
        dumpDatabase("Dump after deleteAllOrphanHeaders call");
    }

    public void deleteAllHeaders() {
        Log.d(HeaderDAO.class.getName(), "Deleting all headers");
        executeDBOperationInTransaction((Header) null, this::deleteAllHeaders);
        dumpDatabase("Dump after deleteAllHeaders call");
    }

    private void dumpDatabase(String message) {
        if (BuildConfig.DEBUG) {
            Dump.dump(HeaderDAO.class.getName(), message, Header.class.getSimpleName().toLowerCase(), this::readAllHeaders);
        }
    }

    private Header insertHeader(Header header, SQLiteDatabase db) {
        Log.d(HeaderDAO.class.getName(), "insertHeader, header is " + header);
        ContentValues values = new ContentValues();
        HeaderDBConstants dbConstants = new HeaderDBConstants(getContext());
        values.put(dbConstants.getNetworkTaskIdColumnName(), header.getNetworkTaskId() < 0 ? null : header.getNetworkTaskId());
        values.put(dbConstants.getNameColumnName(), header.getName());
        values.put(dbConstants.getValueColumnName(), header.getValue());
        long rowid = db.insert(dbConstants.getTableName(), null, values);
        if (rowid < 0) {
            Log.e(HeaderDAO.class.getName(), "Error inserting header into database. Insert returned -1.");
        }
        header.setId(rowid);
        return header;
    }

    private DBResult<Integer> insertHeaders(List<Header> headers, SQLiteDatabase db) {
        Log.d(HeaderDAO.class.getName(), "insertHeaders with headers " + headers);
        int count = 0;
        for (Header header : headers) {
            ContentValues values = new ContentValues();
            HeaderDBConstants dbConstants = new HeaderDBConstants(getContext());
            values.put(dbConstants.getNetworkTaskIdColumnName(), header.getNetworkTaskId() < 0 ? null : header.getNetworkTaskId());
            values.put(dbConstants.getNameColumnName(), header.getName());
            values.put(dbConstants.getValueColumnName(), header.getValue());
            long rowid = db.insert(dbConstants.getTableName(), null, values);
            if (rowid < 0) {
                Log.e(HeaderDAO.class.getName(), "Error inserting header into database.");
                return new DBResult<>(false, -1);
            } else {
                count++;
            }
            header.setId(rowid);
        }
        return new DBResult<>(true, count);
    }

    private Header updateHeader(Header header, SQLiteDatabase db) {
        Log.d(HeaderDAO.class.getName(), "updateHeader, header is " + header);
        HeaderDBConstants dbConstants = new HeaderDBConstants(getContext());
        String selection = dbConstants.getIdColumnName() + " = ?";
        String[] selectionArgs = {String.valueOf(header.getId())};
        ContentValues values = new ContentValues();
        values.put(dbConstants.getNetworkTaskIdColumnName(), header.getNetworkTaskId() < 0 ? null : header.getNetworkTaskId());
        values.put(dbConstants.getNameColumnName(), header.getName());
        values.put(dbConstants.getValueColumnName(), header.getValue());
        db.update(dbConstants.getTableName(), values, selection, selectionArgs);
        return header;
    }

    private List<Header> readGlobalHeaders(Header header, SQLiteDatabase db) {
        Log.d(HeaderDAO.class.getName(), "readGlobalHeaders, header is " + header);
        HeaderDBConstants dbConstants = new HeaderDBConstants(getContext());
        List<Header> result = new ArrayList<>();
        readHeadersInternal(db, dbConstants.getReadGlobalHeadersStatement(), null, result::add);
        Log.d(HeaderDAO.class.getName(), "readGlobalHeaders, returning " + result);
        return result;
    }

    private Map<Long, List<Header>> readAllHeadersForNetworkTasks(Header header, SQLiteDatabase db) {
        Log.d(HeaderDAO.class.getName(), "readAllHeadersForNetworkTasks, header is " + header);
        HeaderDBConstants dbConstants = new HeaderDBConstants(getContext());
        Map<Long, List<Header>> result = new HashMap<>();
        readHeadersInternal(db, dbConstants.getReadNonGlobalHeadersStatement(), null, mappedHeader -> collectHeaderForNetworkTask(result, mappedHeader));
        Log.d(HeaderDAO.class.getName(), "readAllHeadersForNetworkTasks, returning " + result);
        return result;
    }

    private void collectHeaderForNetworkTask(Map<Long, List<Header>> result, Header mappedHeader) {
        if (mappedHeader.getNetworkTaskId() >= 0) {
            List<Header> currentList = result.get(mappedHeader.getNetworkTaskId());
            if (currentList == null) {
                currentList = new ArrayList<>();
                result.put(mappedHeader.getNetworkTaskId(), currentList);
            }
            currentList.add(mappedHeader);
        }
    }

    private List<Header> readHeadersForNetworkTask(Header header, SQLiteDatabase db) {
        Log.d(HeaderDAO.class.getName(), "readHeadersForNetworkTask, header is " + header);
        HeaderDBConstants dbConstants = new HeaderDBConstants(getContext());
        List<Header> result = new ArrayList<>();
        readHeadersInternal(db, dbConstants.getReadHeadersForNetworkTaskStatement(), new String[]{String.valueOf(header.getNetworkTaskId())}, result::add);
        Log.d(HeaderDAO.class.getName(), "readHeadersForNetworkTask, returning " + result);
        return result;
    }

    private List<Header> readAllHeaders(Header header, SQLiteDatabase db) {
        Log.d(HeaderDAO.class.getName(), "readAllHeaders, header is " + header);
        HeaderDBConstants dbConstants = new HeaderDBConstants(getContext());
        List<Header> result = new ArrayList<>();
        readHeadersInternal(db, dbConstants.getReadAllHeadersStatement(), null, result::add);
        Log.d(HeaderDAO.class.getName(), "readAllHeaders, returning " + result);
        return result;
    }

    private void readHeadersInternal(SQLiteDatabase db, String sql, String[] sqlArgs, HeaderCollector collector) {
        Log.d(HeaderDAO.class.getName(), "readHeadersInternal");
        Cursor cursor = null;
        HeaderDBConstants dbConstants = new HeaderDBConstants(getContext());
        try {
            Log.d(HeaderDAO.class.getName(), "Executing SQL " + sql + (sqlArgs != null ? " with parameters " + Arrays.toString(sqlArgs) : ""));
            cursor = db.rawQuery(sql, sqlArgs);
            while (cursor.moveToNext()) {
                int indexIdColumn = cursor.getColumnIndex(dbConstants.getIdColumnName());
                if (!cursor.isNull(indexIdColumn)) {
                    collector.collect(mapCursorToHeader(cursor));
                }
            }
        } finally {
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Throwable exc) {
                    Log.e(HeaderDAO.class.getName(), "Error closing result cursor", exc);
                }
            }
        }
    }

    private int deleteHeader(Header header, SQLiteDatabase db) {
        Log.d(HeaderDAO.class.getName(), "deleteHeader, header is " + header);
        HeaderDBConstants dbConstants = new HeaderDBConstants(getContext());
        String selection = dbConstants.getIdColumnName() + " = ?";
        String[] selectionArgs = {String.valueOf(header.getId())};
        return db.delete(dbConstants.getTableName(), selection, selectionArgs);
    }

    private int deleteGlobalHeaders(Header header, SQLiteDatabase db) {
        Log.d(HeaderDAO.class.getName(), "deleteGlobalHeaders, header is " + header);
        HeaderDBConstants dbConstants = new HeaderDBConstants(getContext());
        Log.d(HeaderDAO.class.getName(), "Executing SQL " + dbConstants.getDeleteGlobalHeadersStatement());
        db.execSQL(dbConstants.getDeleteGlobalHeadersStatement());
        return -1;
    }

    private int deleteHeadersForNetworkTask(Header header, SQLiteDatabase db) {
        Log.d(HeaderDAO.class.getName(), "deleteHeadersForNetworkTask, header is " + header);
        HeaderDBConstants dbConstants = new HeaderDBConstants(getContext());
        String selection = dbConstants.getNetworkTaskIdColumnName() + " = ?";
        String[] selectionArgs = {String.valueOf(header.getNetworkTaskId())};
        return db.delete(dbConstants.getTableName(), selection, selectionArgs);
    }

    private int deleteAllHeaders(Header header, SQLiteDatabase db) {
        Log.d(HeaderDAO.class.getName(), "deleteAllHeaders, header is " + header);
        HeaderDBConstants dbConstants = new HeaderDBConstants(getContext());
        return db.delete(dbConstants.getTableName(), null, null);
    }

    private int deleteAllOrphanHeaders(Header header, SQLiteDatabase db) {
        Log.d(HeaderDAO.class.getName(), "deleteAllOrphanHeaders, header is " + header);
        HeaderDBConstants dbConstants = new HeaderDBConstants(getContext());
        Log.d(HeaderDAO.class.getName(), "Executing SQL " + dbConstants.getDeleteOrphanHeadersStatement());
        db.execSQL(dbConstants.getDeleteOrphanHeadersStatement());
        return -1;
    }

    private Header mapCursorToHeader(Cursor cursor) {
        Header header = new Header();
        HeaderDBConstants dbConstants = new HeaderDBConstants(getContext());
        int indexIdColumn = cursor.getColumnIndex(dbConstants.getIdColumnName());
        int indexNetworkTaskIdColumn = cursor.getColumnIndex(dbConstants.getNetworkTaskIdColumnName());
        int indexNameColumn = cursor.getColumnIndex(dbConstants.getNameColumnName());
        int indexValueColumn = cursor.getColumnIndex(dbConstants.getValueColumnName());
        header.setId(cursor.getLong(indexIdColumn));
        header.setNetworkTaskId(cursor.isNull(indexNetworkTaskIdColumn) ? -1 : cursor.getLong(indexNetworkTaskIdColumn));
        header.setName(cursor.getString(indexNameColumn));
        header.setValue(cursor.getString(indexValueColumn));
        return header;
    }

    @FunctionalInterface
    private interface HeaderCollector {
        void collect(Header header);
    }
}
