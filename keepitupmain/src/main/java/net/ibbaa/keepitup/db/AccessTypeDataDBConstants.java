/*
 * Copyright (c) 2025 Alwin Ibba
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

import android.content.Context;

import net.ibbaa.keepitup.R;

public class AccessTypeDataDBConstants {

    private final NetworkTaskDBConstants networkTaskDBConstants;
    private final String tableName;
    private final String idColumnName;
    private final String networkTaskIdColumnName;
    private final String pingCountColumnName;
    private final String pingPackageSizeColumnName;
    private final String connectCountColumnName;
    private final String stopOnSuccessColumnName;
    private final String ignoreSSLErrorColumnName;

    public AccessTypeDataDBConstants(Context context) {
        networkTaskDBConstants = new NetworkTaskDBConstants(context);
        tableName = context.getResources().getString(R.string.accesstypedata_table_name);
        idColumnName = context.getResources().getString(R.string.accesstypedata_id_column_name);
        networkTaskIdColumnName = context.getResources().getString(R.string.accesstypedata_taskid_column_name);
        pingCountColumnName = context.getResources().getString(R.string.accesstypedata_pingcount_column_name);
        pingPackageSizeColumnName = context.getResources().getString(R.string.accesstypedata_pingpackagesize_column_name);
        connectCountColumnName = context.getResources().getString(R.string.accesstypedata_connectcount_column_name);
        stopOnSuccessColumnName = context.getResources().getString(R.string.accesstypedata_stoponsuccess_column_name);
        ignoreSSLErrorColumnName = context.getResources().getString(R.string.accesstypedata_ignoresslerror_column_name);
    }

    public String getTableName() {
        return tableName;
    }

    public String getIdColumnName() {
        return idColumnName;
    }

    public String getNetworkTaskIdColumnName() {
        return networkTaskIdColumnName;
    }

    public String getPingCountColumnName() {
        return pingCountColumnName;
    }

    public String getPingPackageSizeColumnName() {
        return pingPackageSizeColumnName;
    }

    public String getConnectCountColumnName() {
        return connectCountColumnName;
    }

    public String getStopOnSuccessColumnName() {
        return stopOnSuccessColumnName;
    }

    public String getIgnoreSSLErrorColumnName() {
        return ignoreSSLErrorColumnName;
    }

    public String getCreateTableStatement() {
        return ("CREATE TABLE IF NOT EXISTS  " + getTableName() + "(") +
                getIdColumnName() + " INTEGER PRIMARY KEY ASC, " +
                getNetworkTaskIdColumnName() + " INTEGER NOT NULL, " +
                getPingCountColumnName() + " INTEGER, " +
                getPingPackageSizeColumnName() + " INTEGER, " +
                getConnectCountColumnName() + " INTEGER, " +
                getStopOnSuccessColumnName() + " INTEGER, " +
                getIgnoreSSLErrorColumnName() + " INTEGER);";
    }

    public String getCreateTableStatementWithoutStopOnSuccess() {
        return ("CREATE TABLE IF NOT EXISTS  " + getTableName() + "(") +
                getIdColumnName() + " INTEGER PRIMARY KEY ASC, " +
                getNetworkTaskIdColumnName() + " INTEGER NOT NULL, " +
                getPingCountColumnName() + " INTEGER, " +
                getPingPackageSizeColumnName() + " INTEGER, " +
                getConnectCountColumnName() + " INTEGER, " +
                getIgnoreSSLErrorColumnName() + " INTEGER);";
    }

    public String getCreateTableStatementWithoutIgnoreSSLError() {
        return ("CREATE TABLE IF NOT EXISTS  " + getTableName() + "(") +
                getIdColumnName() + " INTEGER PRIMARY KEY ASC, " +
                getNetworkTaskIdColumnName() + " INTEGER NOT NULL, " +
                getPingCountColumnName() + " INTEGER, " +
                getPingPackageSizeColumnName() + " INTEGER, " +
                getConnectCountColumnName() + " INTEGER, " +
                getStopOnSuccessColumnName() + " INTEGER);";
    }

    public String getDropTableStatement() {
        return "DROP TABLE IF EXISTS " + getTableName();
    }

    public String getReadAccessTypeDataForNetworkTaskStatement() {
        return "SELECT " +
                getIdColumnName() + ", " +
                getNetworkTaskIdColumnName() + ", " +
                getPingCountColumnName() + ", " +
                getPingPackageSizeColumnName() + ", " +
                getConnectCountColumnName() + ", " +
                getStopOnSuccessColumnName() + ", " +
                getIgnoreSSLErrorColumnName() +
                " FROM " + getTableName() +
                " WHERE " + getNetworkTaskIdColumnName() + " = ?";
    }

    public String getReadAllAccessTypeDataStatement() {
        return "SELECT " +
                getIdColumnName() + ", " +
                getNetworkTaskIdColumnName() + ", " +
                getPingCountColumnName() + ", " +
                getPingPackageSizeColumnName() + ", " +
                getConnectCountColumnName() + ", " +
                getStopOnSuccessColumnName() + ", " +
                getIgnoreSSLErrorColumnName() +
                " FROM " + getTableName();
    }

    public String getDeleteOrphanAccessTypeDataStatement() {
        return "DELETE FROM " + getTableName() + " WHERE " + getNetworkTaskIdColumnName() + " NOT IN (SELECT " + networkTaskDBConstants.getIdColumnName() + " FROM " + networkTaskDBConstants.getTableName() + ");";
    }

    public String getMigrateNetworkTasksAccessTypeDataStatement() {
        return "INSERT INTO " + getTableName() + "(" +
                getNetworkTaskIdColumnName() + ") SELECT " +
                networkTaskDBConstants.getIdColumnName() +
                " FROM " + networkTaskDBConstants.getTableName();
    }

    public String getAddStopOnSuccessColumnStatement() {
        return "ALTER TABLE " + getTableName() + " ADD COLUMN " + getStopOnSuccessColumnName() + " INTEGER;";
    }

    public String getAddIgnoreSSLErrorColumnStatement() {
        return "ALTER TABLE " + getTableName() + " ADD COLUMN " + getIgnoreSSLErrorColumnName() + " INTEGER;";
    }

    public String getDropStopOnSuccessColumnStatement() {
        return "ALTER TABLE " + getTableName() + " DROP COLUMN " + getStopOnSuccessColumnName() + ";";
    }

    public String getDropIgnoreSSLErrorColumnStatement() {
        return "ALTER TABLE " + getTableName() + " DROP COLUMN " + getIgnoreSSLErrorColumnName() + ";";
    }
}
