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
import android.database.sqlite.SQLiteDatabase;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.model.AccessTypeData;
import net.ibbaa.keepitup.model.Header;
import net.ibbaa.keepitup.model.HeaderType;
import net.ibbaa.keepitup.model.Interval;
import net.ibbaa.keepitup.model.LogEntry;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.model.Resolve;
import net.ibbaa.keepitup.model.validation.AccessTypeDataValidator;
import net.ibbaa.keepitup.model.validation.HeaderValidator;
import net.ibbaa.keepitup.model.validation.IntervalValidator;
import net.ibbaa.keepitup.model.validation.NetworkTaskValidator;
import net.ibbaa.keepitup.model.validation.ResolveValidator;
import net.ibbaa.keepitup.resources.ConstantPreferenceManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DBSetup {

    private final Context context;
    private final NetworkTaskDBConstants networkTaskDBConstants;
    private final LogDBConstants logDBConstants;
    private final SchedulerIdHistoryDBConstants schedulerIdDBConstants;
    private final IntervalDBConstants intervalDBConstants;
    private final SchedulerStateDBConstants schedulerStateDBConstants;
    private final AccessTypeDataDBConstants accessTypeDataDBConstants;
    private final ResolveDBConstants resolveDBConstants;
    private final HeaderDBConstants headerDBConstants;
    private final SNMPItemDBConstants snmpItemDBConstants;

    public DBSetup(Context context) {
        this.context = context;
        this.networkTaskDBConstants = new NetworkTaskDBConstants(context);
        this.logDBConstants = new LogDBConstants(context);
        this.schedulerIdDBConstants = new SchedulerIdHistoryDBConstants(context);
        this.intervalDBConstants = new IntervalDBConstants(context);
        this.schedulerStateDBConstants = new SchedulerStateDBConstants(context);
        this.accessTypeDataDBConstants = new AccessTypeDataDBConstants(context);
        this.resolveDBConstants = new ResolveDBConstants(context);
        this.headerDBConstants = new HeaderDBConstants(context);
        this.snmpItemDBConstants = new SNMPItemDBConstants(context);
    }

    public void createTables(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "createTables");
        createNetworkTaskTable(db);
        createLogTable(db);
        createSchedulerIdHistoryTable(db);
        createIntervalTable(db);
        createSchedulerStateTable(db);
        createAccessTypeDataTable(db);
        createResolveTable(db);
        createHeaderTable(db);
        createSNMPItemTable(db);
    }

    public void tryDropTables(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "tryDropTables");
        try {
            dropSchedulerIdHistoryTable(db);
        } catch (Exception exc) {
            Log.d(DBSetup.class.getName(), "dropSchedulerIdHistoryTable failed ", exc);
        }
        try {
            dropLogTable(db);
        } catch (Exception exc) {
            Log.d(DBSetup.class.getName(), "dropLogTable failed ", exc);
        }
        try {
            dropNetworkTaskTable(db);
        } catch (Exception exc) {
            Log.d(DBSetup.class.getName(), "dropNetworkTaskTable failed ", exc);
        }
        try {
            dropIntervalTable(db);
        } catch (Exception exc) {
            Log.d(DBSetup.class.getName(), "dropIntervalTable failed ", exc);
        }
        try {
            dropSchedulerStateTable(db);
        } catch (Exception exc) {
            Log.d(DBSetup.class.getName(), "dropSchedulerStateTable failed ", exc);
        }
        try {
            dropAccessTypeDataTable(db);
        } catch (Exception exc) {
            Log.d(DBSetup.class.getName(), "dropAccessTypeDataTable failed ", exc);
        }
        try {
            dropResolveTable(db);
        } catch (Exception exc) {
            Log.d(DBSetup.class.getName(), "dropResolveTable failed ", exc);
        }
        try {
            dropHeaderTable(db);
        } catch (Exception exc) {
            Log.d(DBSetup.class.getName(), "dropHeaderTable failed ", exc);
        }
        try {
            dropSNMPItemTable(db);
        } catch (Exception exc) {
            Log.d(DBSetup.class.getName(), "dropSNMPItemTable failed ", exc);
        }
    }

    public void createNetworkTaskTable(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "createNetworkTaskTable, table is " + networkTaskDBConstants.getTableName());
        db.execSQL(networkTaskDBConstants.getCreateTableStatement());
    }

    public void addFailureCountColumnToNetworkTaskTable(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "addFailureCountColumnToNetworkTaskTable, adding column " + networkTaskDBConstants.getFailureCountColumnName() + " to table " + networkTaskDBConstants.getTableName());
        db.execSQL(networkTaskDBConstants.getAddFailureCountColumnStatement());
    }

    public void addHighPrioColumnToNetworkTaskTable(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "addHighPrioColumnToNetworkTaskTable, adding column " + networkTaskDBConstants.getHighPrioColumnName() + " to table " + networkTaskDBConstants.getTableName());
        db.execSQL(networkTaskDBConstants.getAddHighPrioColumnStatement());
    }

    public void addNameColumnToNetworkTaskTable(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "addNameColumnToNetworkTaskTable, adding column " + networkTaskDBConstants.getNameColumnName() + " to table " + networkTaskDBConstants.getTableName());
        db.execSQL(networkTaskDBConstants.getAddNameColumnStatement());
    }

    public void addLastSysUpTimeColumnToNetworkTaskTable(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "addLastSysUpTimeColumnToNetworkTaskTable, adding column " + networkTaskDBConstants.getLastSysUpTimeColumnName() + " to table " + networkTaskDBConstants.getTableName());
        db.execSQL(networkTaskDBConstants.getAddLastSysUpTimeColumnStatement());
    }

    public void initializeFailureCountColumn(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "initializeFailureCountColumn");
        Log.d(DBSetup.class.getName(), "Setting " + networkTaskDBConstants.getFailureCountColumnName() + " to 0 in " + networkTaskDBConstants.getTableName());
        NetworkTaskDBConstants dbConstants = new NetworkTaskDBConstants(getContext());
        ContentValues values = new ContentValues();
        values.put(dbConstants.getFailureCountColumnName(), 0);
        executeDBOperationInTransaction(db, database -> database.update(dbConstants.getTableName(), values, null, null));
    }

    public void createLogTable(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "createLogTable, table is " + logDBConstants.getTableName());
        db.execSQL(logDBConstants.getCreateTableStatement());
    }

    public void createSchedulerIdHistoryTable(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "createSchedulerIdHistoryTable, table is " + schedulerIdDBConstants.getTableName());
        db.execSQL(schedulerIdDBConstants.getCreateTableStatement());
    }

    public void createIntervalTable(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "createIntervalTable, table is " + intervalDBConstants.getTableName());
        db.execSQL(intervalDBConstants.getCreateTableStatement());
    }

    public void initializeSchedulerStateTable(SQLiteDatabase db) {
        db.execSQL(schedulerStateDBConstants.getInitializeSchedulerStateStatement());
    }

    public void createSchedulerStateTable(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "createSchedulerStateTable, table is " + schedulerStateDBConstants.getTableName());
        db.execSQL(schedulerStateDBConstants.getCreateTableStatement());
        initializeSchedulerStateTable(db);
    }

    public void createAccessTypeDataTable(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "createAccessTypeDataTable, table is " + accessTypeDataDBConstants.getTableName());
        db.execSQL(accessTypeDataDBConstants.getCreateTableStatement());
    }

    public void initializeAccessTypeDataTable(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "initializeAccessTypeDataTable");
        executeDBOperationInTransaction(db, database -> database.execSQL(accessTypeDataDBConstants.getMigrateNetworkTasksAccessTypeDataStatement()));
        AccessTypeData accessTypeData = new AccessTypeData(getContext());
        AccessTypeDataDBConstants dbConstants = new AccessTypeDataDBConstants(getContext());
        ContentValues values = new ContentValues();
        values.put(dbConstants.getPingCountColumnName(), accessTypeData.getPingCount());
        values.put(dbConstants.getPingPackageSizeColumnName(), accessTypeData.getPingPackageSize());
        values.put(dbConstants.getConnectCountColumnName(), accessTypeData.getConnectCount());
        values.put(dbConstants.getStopOnSuccessColumnName(), accessTypeData.isStopOnSuccess() ? 1 : 0);
        values.put(dbConstants.getIgnoreSSLErrorColumnName(), accessTypeData.isIgnoreSSLError() ? 1 : 0);
        values.put(dbConstants.getUseDefaultHeadersColumnName(), accessTypeData.isUseDefaultHeaders() ? 1 : 0);
        values.put(dbConstants.getSnmpVersionColumnName(), accessTypeData.getSnmpVersion() == null ? null : accessTypeData.getSnmpVersion().getCode());
        executeDBOperationInTransaction(db, database -> database.update(dbConstants.getTableName(), values, null, null));
    }

    public void addStopOnSuccessColumnToAccessTypeDataTable(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "addStopOnSuccessColumnToAccessTypeDataTable, adding column " + accessTypeDataDBConstants.getStopOnSuccessColumnName() + " to table " + accessTypeDataDBConstants.getTableName());
        db.execSQL(accessTypeDataDBConstants.getAddStopOnSuccessColumnStatement());
    }

    public void addIgnoreSSLErrorColumnToAccessTypeDataTable(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "addIgnoreSSLErrorColumnToAccessTypeDataTable, adding column " + accessTypeDataDBConstants.getIgnoreSSLErrorColumnName() + " to table " + accessTypeDataDBConstants.getTableName());
        db.execSQL(accessTypeDataDBConstants.getAddIgnoreSSLErrorColumnStatement());
    }

    public void addUseDefaultHeadersColumnToAccessTypeDataTable(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "addUseDefaultHeadersColumnToAccessTypeDataTable, adding column " + accessTypeDataDBConstants.getUseDefaultHeadersColumnName() + " to table " + accessTypeDataDBConstants.getTableName());
        db.execSQL(accessTypeDataDBConstants.getAddUseDefaultHeadersColumnStatement());
    }

    public void addSnmpVersionColumnToAccessTypeDataTable(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "addSnmpVersionColumnToAccessTypeDataTable, adding column " + accessTypeDataDBConstants.getSnmpVersionColumnName() + " to table " + accessTypeDataDBConstants.getTableName());
        db.execSQL(accessTypeDataDBConstants.getAddSnmpVersionColumnStatement());
    }

    public void addSnmpCommunityColumnToAccessTypeDataTable(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "addSnmpCommunityColumnToAccessTypeDataTable, adding column " + accessTypeDataDBConstants.getSnmpCommunityColumnName() + " to table " + accessTypeDataDBConstants.getTableName());
        db.execSQL(accessTypeDataDBConstants.getAddSnmpCommunityColumnStatement());
    }

    public void addSnmpCommunityIVColumnToAccessTypeDataTable(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "addSnmpCommunityIVColumnToAccessTypeDataTable, adding column " + accessTypeDataDBConstants.getSnmpCommunityIVColumnName() + " to table " + accessTypeDataDBConstants.getTableName());
        db.execSQL(accessTypeDataDBConstants.getAddSnmpCommunityIVColumnStatement());
    }

    public void createResolveTable(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "createResolveTable, table is " + resolveDBConstants.getTableName());
        db.execSQL(resolveDBConstants.getCreateTableStatement());
    }

    public void addIndexColumnToResolveTable(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "addIndexColumnToResolveTable, adding column " + resolveDBConstants.getIndexColumnName() + " to table " + resolveDBConstants.getTableName());
        db.execSQL(resolveDBConstants.getAddIndexColumnStatement());
    }

    public void createHeaderTable(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "createHeaderTable, table is " + headerDBConstants.getTableName());
        db.execSQL(headerDBConstants.getCreateTableStatement());
        initializeHeaderTable(db);
    }

    public void createSNMPItemTable(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "createSNMPItemTable, table is " + snmpItemDBConstants.getTableName());
        db.execSQL(snmpItemDBConstants.getCreateTableStatement());
    }

    public void initializeHeaderTable(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "initializeHeaderTable");
        ContentValues values = new ContentValues();
        HeaderDBConstants dbConstants = new HeaderDBConstants(getContext());
        ConstantPreferenceManager preferenceManager = new ConstantPreferenceManager(getContext());
        values.put(dbConstants.getNetworkTaskIdColumnName(), (Long) null);
        values.put(dbConstants.getNameColumnName(), getContext().getResources().getString(R.string.http_header_user_agent));
        values.put(dbConstants.getValueColumnName(), preferenceManager.getPreferenceHTTPUserAgent());
        values.put(dbConstants.getHeaderTypeColumnName(), HeaderType.GENERIC.getCode());
        executeDBOperationInTransaction(db, database -> database.insert(dbConstants.getTableName(), null, values));
        preferenceManager.removePreferenceHTTPUserAgent();
    }

    public void addHeaderTypeColumnToHeaderTable(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "addHeaderTypeColumnToHeaderTable, adding column " + headerDBConstants.getHeaderTypeColumnName() + " to table " + headerDBConstants.getTableName());
        db.execSQL(headerDBConstants.getAddHeaderTypeColumnStatement());
    }

    public void addValueIVColumnToHeaderTable(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "addValueIVColumnToHeaderTable, adding column " + headerDBConstants.getValueIVColumnName() + " to table " + headerDBConstants.getTableName());
        db.execSQL(headerDBConstants.getAddValueIVColumnStatement());
    }

    private void executeDBOperationInTransaction(SQLiteDatabase db, DBSetupOperation operation) {
        Log.d(DBSetup.class.getName(), "executeDBOperationInTransaction");
        try {
            db.beginTransaction();
            operation.execute(db);
            db.setTransactionSuccessful();
        } catch (Throwable exc) {
            Log.e(DBSetup.class.getName(), "Error executing database operation", exc);
            throw exc;
        } finally {
            if (db != null) {
                try {
                    db.endTransaction();
                } catch (Throwable exc) {
                    Log.e(DBSetup.class.getName(), "Error committing changes to database", exc);
                }
            }
        }
    }

    public void dropTables(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "dropTables");
        dropSchedulerIdHistoryTable(db);
        dropLogTable(db);
        dropNetworkTaskTable(db);
        dropIntervalTable(db);
        dropSchedulerStateTable(db);
        dropAccessTypeDataTable(db);
        dropResolveTable(db);
        dropHeaderTable(db);
        dropSNMPItemTable(db);
    }

    public void dropNetworkTaskTable(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "dropNetworkTaskTable, table is " + networkTaskDBConstants.getTableName());
        db.execSQL(networkTaskDBConstants.getDropTableStatement());
    }

    public void tryDropNetworkTaskTable(SQLiteDatabase db) {
        try {
            dropNetworkTaskTable(db);
        } catch (Exception exc) {
            Log.d(DBSetup.class.getName(), "dropNetworkTaskTable failed ", exc);
        }
    }

    public void dropFailureCountColumnFromNetworkTaskTable(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "dropFailureCountColumnFromNetworkTaskTable, dropping column " + networkTaskDBConstants.getFailureCountColumnName() + " from table " + networkTaskDBConstants.getTableName());
        db.execSQL(networkTaskDBConstants.getDropFailureCountColumnStatement());
    }

    public void dropHighPrioColumnFromNetworkTaskTable(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "dropHighPrioColumnFromNetworkTaskTable, dropping column " + networkTaskDBConstants.getHighPrioColumnName() + " from table " + networkTaskDBConstants.getTableName());
        db.execSQL(networkTaskDBConstants.getDropHighPrioColumnStatement());
    }

    public void dropNameColumnFromNetworkTaskTable(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "dropNameColumnFromNetworkTaskTable, dropping column " + networkTaskDBConstants.getNameColumnName() + " from table " + networkTaskDBConstants.getTableName());
        db.execSQL(networkTaskDBConstants.getDropNameColumnStatement());
    }

    public void dropLastSysUpTimeColumnFromNetworkTaskTable(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "dropLastSysUpTimeColumnFromNetworkTaskTable, dropping column " + networkTaskDBConstants.getLastSysUpTimeColumnName() + " from table " + networkTaskDBConstants.getTableName());
        db.execSQL(networkTaskDBConstants.getDropLastSysUpTimeColumnStatement());
    }

    public void dropLogTable(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "dropLogTable, table is " + logDBConstants.getTableName());
        db.execSQL(logDBConstants.getDropTableStatement());
    }

    public void tryDropLogTable(SQLiteDatabase db) {
        try {
            dropLogTable(db);
        } catch (Exception exc) {
            Log.d(DBSetup.class.getName(), "dropLogTable failed ", exc);
        }
    }

    public void dropSchedulerIdHistoryTable(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "dropSchedulerIdHistoryTable, table is " + schedulerIdDBConstants.getTableName());
        db.execSQL(schedulerIdDBConstants.getDropTableStatement());
    }

    public void tryDropSchedulerIdHistoryTable(SQLiteDatabase db) {
        try {
            dropSchedulerIdHistoryTable(db);
        } catch (Exception exc) {
            Log.d(DBSetup.class.getName(), "dropSchedulerIdHistoryTable failed ", exc);
        }
    }

    public void dropIntervalTable(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "dropIntervalTable, table is " + intervalDBConstants.getTableName());
        db.execSQL(intervalDBConstants.getDropTableStatement());
    }

    public void tryDropIntervalTable(SQLiteDatabase db) {
        try {
            dropIntervalTable(db);
        } catch (Exception exc) {
            Log.d(DBSetup.class.getName(), "dropIntervalTable failed ", exc);
        }
    }

    public void dropSchedulerStateTable(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "dropSchedulerStateTable, table is " + schedulerStateDBConstants.getTableName());
        db.execSQL(schedulerStateDBConstants.getDropTableStatement());
    }

    public void tryDropSchedulerStateTable(SQLiteDatabase db) {
        try {
            dropSchedulerStateTable(db);
        } catch (Exception exc) {
            Log.d(DBSetup.class.getName(), "dropSchedulerStateTable failed ", exc);
        }
    }

    public void dropAccessTypeDataTable(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "dropAccessTypeDataTable, table is " + accessTypeDataDBConstants.getTableName());
        db.execSQL(accessTypeDataDBConstants.getDropTableStatement());
    }

    public void tryDropAccessTypeDataTable(SQLiteDatabase db) {
        try {
            dropAccessTypeDataTable(db);
        } catch (Exception exc) {
            Log.d(DBSetup.class.getName(), "dropAccessTypeDataTable failed ", exc);
        }
    }

    public void dropStopOnSuccessColumnFromAccessTypeDataTable(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "dropStopOnSuccessColumnFromAccessTypeDataTable, dropping column " + accessTypeDataDBConstants.getStopOnSuccessColumnName() + " from table " + accessTypeDataDBConstants.getTableName());
        db.execSQL(accessTypeDataDBConstants.getDropStopOnSuccessColumnStatement());
    }

    public void dropIgnoreSSLErrorColumnFromAccessTypeDataTable(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "dropIgnoreSSLErrorColumnFromAccessTypeDataTable, dropping column " + accessTypeDataDBConstants.getIgnoreSSLErrorColumnName() + " from table " + accessTypeDataDBConstants.getTableName());
        db.execSQL(accessTypeDataDBConstants.getDropIgnoreSSLErrorColumnStatement());
    }

    public void dropUseDefaultHeadersColumnFromAccessTypeDataTable(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "dropUseDefaultHeadersColumnFromAccessTypeDataTable, dropping column " + accessTypeDataDBConstants.getUseDefaultHeadersColumnName() + " from table " + accessTypeDataDBConstants.getTableName());
        db.execSQL(accessTypeDataDBConstants.getDropUseDefaultHeadersColumnStatement());
    }

    public void dropSnmpVersionColumnFromAccessTypeDataTable(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "dropSnmpVersionColumnFromAccessTypeDataTable, dropping column " + accessTypeDataDBConstants.getSnmpVersionColumnName() + " from table " + accessTypeDataDBConstants.getTableName());
        db.execSQL(accessTypeDataDBConstants.getDropSnmpVersionColumnStatement());
    }

    public void dropSnmpCommunityColumnFromAccessTypeDataTable(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "dropSnmpCommunityColumnFromAccessTypeDataTable, dropping column " + accessTypeDataDBConstants.getSnmpCommunityColumnName() + " from table " + accessTypeDataDBConstants.getTableName());
        db.execSQL(accessTypeDataDBConstants.getDropSnmpCommunityColumnStatement());
    }

    public void dropSnmpCommunityIVColumnFromAccessTypeDataTable(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "dropSnmpCommunityIVColumnFromAccessTypeDataTable, dropping column " + accessTypeDataDBConstants.getSnmpCommunityIVColumnName() + " from table " + accessTypeDataDBConstants.getTableName());
        db.execSQL(accessTypeDataDBConstants.getDropSnmpCommunityIVColumnStatement());
    }

    public void dropResolveTable(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "dropResolveTable, table is " + resolveDBConstants.getTableName());
        db.execSQL(resolveDBConstants.getDropTableStatement());
    }

    public void tryDropResolveTable(SQLiteDatabase db) {
        try {
            dropResolveTable(db);
        } catch (Exception exc) {
            Log.d(DBSetup.class.getName(), "dropResolveTable failed ", exc);
        }
    }

    public void dropIndexColumnFromResolveTable(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "dropIndexColumnFromResolveTable, dropping column " + resolveDBConstants.getIndexColumnName() + " from table " + resolveDBConstants.getTableName());
        db.execSQL(resolveDBConstants.getDropIndexColumnStatement());
    }

    public void dropHeaderTable(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "dropHeaderTable, table is " + headerDBConstants.getTableName());
        db.execSQL(headerDBConstants.getDropTableStatement());
    }

    public void tryDropHeaderTable(SQLiteDatabase db) {
        try {
            dropHeaderTable(db);
        } catch (Exception exc) {
            Log.d(DBSetup.class.getName(), "dropHeaderTable failed ", exc);
        }
    }

    public void dropSNMPItemTable(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "dropSNMPItemTable, table is " + snmpItemDBConstants.getTableName());
        db.execSQL(snmpItemDBConstants.getDropTableStatement());
    }

    public void tryDropSNMPItemTable(SQLiteDatabase db) {
        try {
            dropSNMPItemTable(db);
        } catch (Exception exc) {
            Log.d(DBSetup.class.getName(), "dropSNMPItemTable failed ", exc);
        }
    }

    public void dropHeaderTypeColumnFromHeaderTable(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "dropHeaderTypeColumnFromHeaderTable, dropping column " + headerDBConstants.getHeaderTypeColumnName() + " from table " + headerDBConstants.getTableName());
        db.execSQL(headerDBConstants.getDropHeaderTypeColumnStatement());
    }

    public void dropValueIVColumnFromHeaderTable(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "dropValueIVColumnFromHeaderTable, dropping column " + headerDBConstants.getValueIVColumnName() + " from table " + headerDBConstants.getTableName());
        db.execSQL(headerDBConstants.getDropValueIVColumnStatement());
    }

    public void recreateNetworkTaskTable(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "recreateNetworkTaskTable");
        dropNetworkTaskTable(db);
        createNetworkTaskTable(db);
    }

    public void recreateLogTable(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "recreateLogTable");
        dropLogTable(db);
        createLogTable(db);
    }

    public void recreateSchedulerIdHistoryTable(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "recreateSchedulerIdHistoryTable");
        dropSchedulerIdHistoryTable(db);
        createSchedulerIdHistoryTable(db);
    }

    public void recreateIntervalTable(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "recreateIntervalTable");
        dropIntervalTable(db);
        createIntervalTable(db);
    }

    public void recreateAccessTypeDataTable(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "recreateAccessTypeDataTable");
        dropAccessTypeDataTable(db);
        createAccessTypeDataTable(db);
    }

    public void recreateResolveTable(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "recreateResolveTable");
        dropResolveTable(db);
        createResolveTable(db);
    }

    public void recreateHeaderTable(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "recreateHeaderTable");
        dropHeaderTable(db);
        createHeaderTable(db);
    }

    public void recreateSchedulerStateTable(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "recreateSchedulerStateTable");
        dropSchedulerStateTable(db);
        createSchedulerStateTable(db);
    }

    public void recreateTables(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "recreateTables");
        dropTables(db);
        createTables(db);
    }

    public void createTables() {
        createTables(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    public void tryDropTables() {
        tryDropTables(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    public void createNetworkTaskTable() {
        createNetworkTaskTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    public void addFailureCountColumnToNetworkTaskTable() {
        addFailureCountColumnToNetworkTaskTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    public void addHighPrioColumnToNetworkTaskTable() {
        addHighPrioColumnToNetworkTaskTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    public void addNameColumnToNetworkTaskTable() {
        addNameColumnToNetworkTaskTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    public void addLastSysUpTimeColumnToNetworkTaskTable() {
        addLastSysUpTimeColumnToNetworkTaskTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    public void addStopOnSuccessColumnToAccessTypeDataTable() {
        addStopOnSuccessColumnToAccessTypeDataTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    public void addIgnoreSSLErrorColumnToAccessTypeDataTable() {
        addIgnoreSSLErrorColumnToAccessTypeDataTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    public void addUseDefaultHeadersColumnToAccessTypeDataTable() {
        addUseDefaultHeadersColumnToAccessTypeDataTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    public void addSnmpVersionColumnToAccessTypeDataTable() {
        addSnmpVersionColumnToAccessTypeDataTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    public void addSnmpCommunityColumnToAccessTypeDataTable() {
        addSnmpCommunityColumnToAccessTypeDataTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    public void addSnmpCommunityIVColumnToAccessTypeDataTable() {
        addSnmpCommunityIVColumnToAccessTypeDataTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    public void initializeFailureCountColumn() {
        initializeFailureCountColumn(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    public void createLogTable() {
        createLogTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    public void createSchedulerIdHistoryTable() {
        createSchedulerIdHistoryTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    public void createIntervalTable() {
        createIntervalTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    public void createSchedulerStateTable() {
        createSchedulerStateTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    public void createAccessTypeDataTable() {
        createAccessTypeDataTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    public void initializeAccessTypeDataTable() {
        initializeAccessTypeDataTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    public void createResolveTable() {
        createResolveTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    public void addIndexColumnToResolveTable() {
        addIndexColumnToResolveTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    public void createHeaderTable() {
        createHeaderTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    public void createSNMPItemTable() {
        createSNMPItemTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    public void initializeHeaderTable() {
        initializeHeaderTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    public void addHeaderTypeColumnToHeaderTable() {
        addHeaderTypeColumnToHeaderTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    public void addValueIVColumnToHeaderTable() {
        addValueIVColumnToHeaderTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    public void dropTables() {
        dropTables(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    public void dropNetworkTaskTable() {
        dropNetworkTaskTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    @SuppressWarnings({"unused"})
    public void tryDropNetworkTaskTable() {
        tryDropNetworkTaskTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    @SuppressWarnings({"unused"})
    public void dropFailureCountColumnFromNetworkTaskTable() {
        dropFailureCountColumnFromNetworkTaskTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    @SuppressWarnings({"unused"})
    public void dropHighPrioColumnFromNetworkTaskTable() {
        dropHighPrioColumnFromNetworkTaskTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    @SuppressWarnings({"unused"})
    public void dropStopOnSuccessColumnFromAccessTypeDataTable() {
        dropStopOnSuccessColumnFromAccessTypeDataTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    @SuppressWarnings({"unused"})
    public void dropIgnoreSSLErrorColumnFromAccessTypeDataTable() {
        dropIgnoreSSLErrorColumnFromAccessTypeDataTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    @SuppressWarnings({"unused"})
    public void dropUseDefaultHeadersColumnFromAccessTypeDataTable() {
        dropUseDefaultHeadersColumnFromAccessTypeDataTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    @SuppressWarnings({"unused"})
    public void dropSnmpVersionColumnFromAccessTypeDataTable() {
        dropSnmpVersionColumnFromAccessTypeDataTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    @SuppressWarnings({"unused"})
    public void dropSnmpCommunityColumnFromAccessTypeDataTable() {
        dropSnmpCommunityColumnFromAccessTypeDataTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    @SuppressWarnings({"unused"})
    public void dropSnmpCommunityIVColumnFromAccessTypeDataTable() {
        dropSnmpCommunityIVColumnFromAccessTypeDataTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    @SuppressWarnings({"unused"})
    public void dropNameColumnFromNetworkTaskTable() {
        dropNameColumnFromNetworkTaskTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    @SuppressWarnings({"unused"})
    public void dropLastSysUpTimeColumnFromNetworkTaskTable() {
        dropLastSysUpTimeColumnFromNetworkTaskTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    public void dropLogTable() {
        dropLogTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    @SuppressWarnings({"unused"})
    public void tryDropLogTable() {
        tryDropLogTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    public void dropSchedulerIdHistoryTable() {
        dropSchedulerIdHistoryTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    @SuppressWarnings({"unused"})
    public void tryDropSchedulerIdHistoryTable() {
        tryDropSchedulerIdHistoryTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    public void dropIntervalTable() {
        dropIntervalTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    @SuppressWarnings({"unused"})
    public void tryDropIntervalTable() {
        tryDropIntervalTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    public void dropSchedulerStateTable() {
        dropSchedulerStateTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    @SuppressWarnings({"unused"})
    public void tryDropSchedulerStateTable() {
        tryDropSchedulerStateTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    public void dropAccessTypeDataTable() {
        dropAccessTypeDataTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    @SuppressWarnings({"unused"})
    public void tryDropAccessTypeDataTable() {
        tryDropAccessTypeDataTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    public void dropResolveTable() {
        dropResolveTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    @SuppressWarnings({"unused"})
    public void tryDropResolveTable() {
        tryDropResolveTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    @SuppressWarnings({"unused"})
    public void dropIndexColumnFromResolveTable() {
        dropIndexColumnFromResolveTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    public void dropHeaderTable() {
        dropHeaderTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    @SuppressWarnings({"unused"})
    public void tryDropHeaderTable() {
        tryDropHeaderTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    public void dropSNMPItemTable() {
        dropSNMPItemTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    @SuppressWarnings({"unused"})
    public void tryDropSNMPItemTable() {
        tryDropSNMPItemTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    @SuppressWarnings({"unused"})
    public void dropHeaderTypeColumnFromHeaderTable() {
        dropHeaderTypeColumnFromHeaderTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    @SuppressWarnings({"unused"})
    public void dropValueIVColumnFromHeaderTable() {
        dropValueIVColumnFromHeaderTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    public void recreateNetworkTaskTable() {
        recreateNetworkTaskTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    public void recreateLogTable() {
        recreateLogTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    public void recreateSchedulerIdHistoryTable() {
        recreateSchedulerIdHistoryTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    public void recreateIntervalTable() {
        recreateIntervalTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    public void recreateSchedulerStateTable() {
        recreateSchedulerStateTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    public void recreateAccessTypeDataTable() {
        recreateAccessTypeDataTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    public void recreateResolveTable() {
        recreateResolveTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    public void recreateHeaderTable() {
        recreateHeaderTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    public void recreateTables() {
        recreateTables(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    public void deleteAllNetworkTasks() {
        Log.d(DBSetup.class.getName(), "deleteAllNetworkTasks");
        NetworkTaskDAO dao = new NetworkTaskDAO(getContext());
        dao.deleteAllNetworkTasks();
    }

    public void deleteAllLogs() {
        Log.d(DBSetup.class.getName(), "deleteAllLogs");
        LogDAO dao = new LogDAO(getContext());
        dao.deleteAllLogs();
    }

    public void deleteAllSchedulerIds() {
        Log.d(DBSetup.class.getName(), "deleteAllSchedulerIds");
        SchedulerIdHistoryDAO dao = new SchedulerIdHistoryDAO(getContext());
        dao.deleteAllSchedulerIds();
    }

    public void deleteAllIntervals() {
        Log.d(DBSetup.class.getName(), "deleteAllIntervals");
        IntervalDAO dao = new IntervalDAO(getContext());
        dao.deleteAllIntervals();
    }

    public void deleteSchedulerState() {
        Log.d(DBSetup.class.getName(), "deleteSchedulerState");
        SchedulerStateDAO dao = new SchedulerStateDAO(getContext());
        dao.deleteSchedulerState();
    }

    public void deleteAllAccessTypeData() {
        Log.d(DBSetup.class.getName(), "deleteAllAccessTypeData");
        AccessTypeDataDAO dao = new AccessTypeDataDAO(getContext());
        dao.deleteAllAccessTypeData();
    }

    public void deleteAllResolve() {
        Log.d(DBSetup.class.getName(), "deleteAllResolve");
        ResolveDAO dao = new ResolveDAO(getContext());
        dao.deleteAllResolves();
    }

    public void deleteAllHeaders() {
        Log.d(DBSetup.class.getName(), "deleteAllHeaders");
        HeaderDAO dao = new HeaderDAO(getContext());
        dao.deleteAllHeaders();
    }

    public void normalizeUIIndex() {
        Log.d(DBSetup.class.getName(), "normalizeUIIndex");
        NetworkTaskDAO networkTaskDAO = new NetworkTaskDAO(getContext());
        networkTaskDAO.normalizeUIIndex();
        ResolveDAO resolveDAO = new ResolveDAO(getContext());
        resolveDAO.normalizeUIIndex();
    }

    public List<Map<String, ?>> exportNetworkTasks() {
        Log.d(DBSetup.class.getName(), "exportNetworkTasks");
        NetworkTaskDAO dao = new NetworkTaskDAO(getContext());
        List<NetworkTask> taskList = dao.readAllNetworkTasks();
        List<Map<String, ?>> exportedList = new ArrayList<>();
        for (NetworkTask task : taskList) {
            exportedList.add(task.toMap());
        }
        return exportedList;
    }

    public List<Map<String, ?>> exportLogsForNetworkTask(long networkTaskId) {
        Log.d(DBSetup.class.getName(), "exportLogsForNetworkTask, networkTaskId is " + networkTaskId);
        LogDAO dao = new LogDAO(getContext());
        List<LogEntry> logList = dao.readAllLogsForNetworkTask(networkTaskId);
        List<Map<String, ?>> exportedList = new ArrayList<>();
        for (LogEntry entry : logList) {
            exportedList.add(entry.toMap());
        }
        return exportedList;
    }

    public Map<String, ?> exportAccessTypeDataForNetworkTask(long networkTaskId, boolean encrypted) {
        Log.d(DBSetup.class.getName(), "exportAccessTypeDataForNetworkTask, networkTaskId is " + networkTaskId + ", encrypted is " + encrypted);
        AccessTypeDataDAO dao = new AccessTypeDataDAO(getContext());
        AccessTypeData accessTypeData = dao.readAccessTypeDataForNetworkTask(networkTaskId);
        if (accessTypeData != null) {
            if (!shouldExportSNMPCommunity(accessTypeData, encrypted)) {
                accessTypeData.setSnmpCommunity(null);
            }
            accessTypeData.setSnmpCommunityValid(true);
            return accessTypeData.toMap();
        }
        return null;
    }

    private boolean shouldExportSNMPCommunity(AccessTypeData accessTypeData, boolean encrypted) {
        Log.d(DBSetup.class.getName(), "shouldExportSNMPCommunity, accessTypeData is " + accessTypeData + ", encrypted is " + encrypted);
        if (!accessTypeData.isSnmpCommunityValid()) {
            return false;
        }
        return encrypted;
    }

    public List<Map<String, ?>> exportResolvesForNetworkTask(long networkTaskId) {
        Log.d(DBSetup.class.getName(), "exportResolvesForNetworkTask, networkTaskId is " + networkTaskId);
        ResolveDAO dao = new ResolveDAO(getContext());
        List<Resolve> resolves = dao.readAllResolvesForNetworkTask(networkTaskId);
        List<Map<String, ?>> exportedList = new ArrayList<>();
        for (Resolve resolve : resolves) {
            exportedList.add(resolve.toMap());
        }
        return exportedList;
    }

    public List<Map<String, ?>> exportHeadersForNetworkTask(long networkTaskId, boolean encrypted) {
        Log.d(DBSetup.class.getName(), "exportHeadersForNetworkTask, networkTaskId is " + networkTaskId + ", encrypted is " + encrypted);
        HeaderDAO dao = new HeaderDAO(getContext());
        List<Header> headerList = dao.readHeadersForNetworkTask(networkTaskId);
        List<Map<String, ?>> exportedList = new ArrayList<>();
        for (Header header : headerList) {
            if (shouldExportHeader(header, encrypted)) {
                exportedList.add(header.toMap());
            }
        }
        return exportedList;
    }

    public List<Map<String, ?>> exportIntervals() {
        Log.d(DBSetup.class.getName(), "exportIntervals");
        IntervalDAO dao = new IntervalDAO(getContext());
        List<Interval> intervalList = dao.readAllIntervals();
        List<Map<String, ?>> exportedList = new ArrayList<>();
        for (Interval interval : intervalList) {
            exportedList.add(interval.toMap());
        }
        return exportedList;
    }

    public List<Map<String, ?>> exportGlobalHeaders(boolean encrypted) {
        Log.d(DBSetup.class.getName(), "exportGlobalHeaders, encrypted is " + encrypted);
        HeaderDAO dao = new HeaderDAO(getContext());
        List<Header> headerList = dao.readGlobalHeaders();
        List<Map<String, ?>> exportedList = new ArrayList<>();
        for (Header header : headerList) {
            if (shouldExportHeader(header, encrypted)) {
                exportedList.add(header.toMap());
            }
        }
        return exportedList;
    }

    private boolean shouldExportHeader(Header header, boolean encrypted) {
        Log.d(DBSetup.class.getName(), "shouldExportHeader, header is " + header + ", encrypted is " + encrypted);
        if (!header.isValueValid()) {
            return false;
        }
        if (!header.isValueSecret()) {
            return true;
        }
        return encrypted;
    }

    public void importNetworkTaskWithAssociatedObjects(Map<String, ?> taskMap, List<Map<String, ?>> logList, Map<String, ?> accessTypeDataMap, List<Map<String, ?>> resolveList, List<Map<String, ?>> headerList) {
        importNetworkTaskWithAssociatedObjects(taskMap, logList, accessTypeDataMap, resolveList, headerList, true);
    }

    public void importNetworkTaskWithAssociatedObjects(Map<String, ?> taskMap, List<Map<String, ?>> logList, Map<String, ?> accessTypeDataMap, List<Map<String, ?>> resolveList, List<Map<String, ?>> headerList, boolean resetRunnning) {
        Log.d(DBSetup.class.getName(), "importNetworkTaskWithAssociatedObjects, resetRunning is " + resetRunnning);
        NetworkTaskDAO networkTaskDAO = new NetworkTaskDAO(getContext());
        NetworkTaskValidator networkTaskValidator = new NetworkTaskValidator(getContext());
        NetworkTask task = new NetworkTask(taskMap);
        if (task.getAddress() != null) {
            task.setAddress(task.getAddress().trim());
        }
        Log.d(DBSetup.class.getName(), "NetworkTask is " + task);
        if (!networkTaskValidator.validate(task)) {
            Log.e(DBSetup.class.getName(), "NetworkTask is invalid and will not be imported: " + task);
            return;
        }
        if (resetRunnning) {
            task.setRunning(false);
        }
        Log.d(DBSetup.class.getName(), "Importing net work task.");
        task = networkTaskDAO.insertNetworkTask(task);
        if (task.getId() > 0) {
            importLogs(logList, task);
            importAccessTypeData(accessTypeDataMap, task);
            importResolves(resolveList, task);
            importHeaders(headerList, task);
        }
    }

    public void importHeaders(List<Map<String, ?>> headerList, NetworkTask task) {
        Log.d(DBSetup.class.getName(), "importHeaders");
        importHeaders(headerList, header -> {
            header.setNetworkTaskId(task.getId());
            return true;
        });
    }

    private void importResolves(List<Map<String, ?>> resolveList, NetworkTask task) {
        Log.d(DBSetup.class.getName(), "importResolves");
        if (resolveList == null) {
            Log.d(DBSetup.class.getName(), "Resolve list is null. Nothing to import.");
        } else {
            ResolveDAO resolveDAO = new ResolveDAO(getContext());
            ResolveValidator resolveValidator = new ResolveValidator(getContext());
            for (Map<String, ?> resolveMap : resolveList) {
                Resolve resolve = new Resolve(resolveMap);
                resolve.setNetworkTaskId(task.getId());
                if (resolve.getTargetAddress() != null) {
                    resolve.setTargetAddress(resolve.getTargetAddress().trim());
                }
                if (resolve.getSourceAddress() != null) {
                    resolve.setSourceAddress(resolve.getSourceAddress().trim());
                }
                Log.d(DBSetup.class.getName(), "Resolve object is " + resolve);
                if (resolveValidator.validate(resolve)) {
                    Log.d(DBSetup.class.getName(), "Importing resolve object.");
                    resolveDAO.insertResolve(resolve);
                } else {
                    Log.e(DBSetup.class.getName(), "Resolve object is invalid and will not be imported: " + resolve);
                }
            }
        }
    }

    private void importAccessTypeData(Map<String, ?> accessTypeDataMap, NetworkTask task) {
        Log.d(DBSetup.class.getName(), "importAccessTypeData");
        AccessTypeDataValidator accessTypeDataValidator = new AccessTypeDataValidator(getContext());
        AccessTypeDataDAO accessTypeDataDAO = new AccessTypeDataDAO(getContext());
        AccessTypeData accessTypeData = accessTypeDataMap == null ? new AccessTypeData(getContext()) : new AccessTypeData(accessTypeDataMap);
        accessTypeData.setNetworkTaskId(task.getId());
        Log.d(DBSetup.class.getName(), "AccessTypeData is " + accessTypeData);
        if (accessTypeDataValidator.validate(accessTypeData) && accessTypeData.isSnmpCommunityValid()) {
            Log.d(DBSetup.class.getName(), "Importing accessTypeData.");
            accessTypeDataDAO.insertAccessTypeData(accessTypeData);
        } else {
            Log.e(DBSetup.class.getName(), "AccessTypeData is invalid and will not be imported: " + accessTypeData);
            Log.e(DBSetup.class.getName(), "Importing default AccessTypeData.");
            AccessTypeData defaultAccessTypeData = new AccessTypeData(getContext());
            defaultAccessTypeData.setNetworkTaskId(task.getId());
            accessTypeDataDAO.insertAccessTypeData(defaultAccessTypeData);
        }
    }

    private void importLogs(List<Map<String, ?>> logList, NetworkTask task) {
        Log.d(DBSetup.class.getName(), "importLogs");
        if (logList != null) {
            LogDAO logDAO = new LogDAO(getContext());
            for (Map<String, ?> logMap : logList) {
                LogEntry entry = new LogEntry(logMap);
                entry.setNetworkTaskId(task.getId());
                Log.d(DBSetup.class.getName(), "LogEntry is " + entry);
                Log.d(DBSetup.class.getName(), "Importing log entry.");
                logDAO.insertAndDeleteLog(entry);
            }
        }
    }

    public void importIntervals(List<Map<String, ?>> intervalList) {
        Log.d(DBSetup.class.getName(), "importIntervals");
        IntervalDAO dao = new IntervalDAO(getContext());
        List<Interval> insertedList = new ArrayList<>();
        IntervalValidator validator = new IntervalValidator(getContext());
        for (Map<String, ?> intervalMap : intervalList) {
            Interval interval = new Interval(intervalMap);
            Log.d(DBSetup.class.getName(), "Interval is " + interval);
            if (validator.validate(interval, insertedList)) {
                Log.d(DBSetup.class.getName(), "Importing interval.");
                dao.insertInterval(interval);
                insertedList.add(interval);
            } else {
                Log.e(DBSetup.class.getName(), "Interval is invalid and will not be imported: " + interval);
            }
        }
    }

    public void importGlobalHeaders(List<Map<String, ?>> headerList) {
        Log.d(DBSetup.class.getName(), "importGlobalHeaders");
        importHeaders(headerList, header -> {
            if (header.getNetworkTaskId() >= 0) {
                Log.e(DBSetup.class.getName(), "Header is not a global header and will not be imported: " + header);
                return false;
            }
            return true;
        });
    }

    private void importHeaders(List<Map<String, ?>> headerList, HeaderPreparer prepare) {
        Log.d(DBSetup.class.getName(), "importHeaders");
        if (headerList == null) {
            return;
        }
        HeaderDAO dao = new HeaderDAO(getContext());
        HeaderValidator validator = new HeaderValidator(getContext());
        List<Header> headersToInsert = new ArrayList<>();
        for (Map<String, ?> headerMap : headerList) {
            Header header = new Header(headerMap);
            Log.d(DBSetup.class.getName(), "Header is " + header);
            if (validator.validate(header)) {
                if (prepare.prepare(header)) {
                    header.setName(header.getName().trim());
                    if (validator.validateNameExists(headersToInsert, header)) {
                        Log.d(DBSetup.class.getName(), "Adding header to import list.");
                        headersToInsert.add(header);
                    } else {
                        Log.e(DBSetup.class.getName(), "Header name exists. Not adding header to import list.");
                    }
                }
            } else {
                Log.e(DBSetup.class.getName(), "Header is invalid and will not be imported: " + header);
            }
        }
        Log.d(DBSetup.class.getName(), "Inserting all headers...");
        dao.insertHeaders(headersToInsert);
    }

    private Context getContext() {
        return context;
    }

    @FunctionalInterface
    private interface HeaderPreparer {
        boolean prepare(Header header);
    }
}
