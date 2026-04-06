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

import android.content.Context;

import net.ibbaa.keepitup.R;

public class ResolveDBConstants {

    private final NetworkTaskDBConstants networkTaskDBConstants;
    private final String tableName;
    private final String idColumnName;
    private final String indexColumnName;
    private final String networkTaskIdColumnName;
    private final String sourceAddressColumnName;
    private final String sourcePortColumnName;
    private final String targetAddressColumnName;
    private final String targetPortColumnName;

    public ResolveDBConstants(Context context) {
        networkTaskDBConstants = new NetworkTaskDBConstants(context);
        tableName = context.getResources().getString(R.string.resolve_table_name);
        idColumnName = context.getResources().getString(R.string.resolve_id_column_name);
        indexColumnName = context.getResources().getString(R.string.resolve_index_column_name);
        networkTaskIdColumnName = context.getResources().getString(R.string.resolve_taskid_column_name);
        sourceAddressColumnName = context.getResources().getString(R.string.resolve_source_address_column_name);
        sourcePortColumnName = context.getResources().getString(R.string.resolve_source_port_column_name);
        targetAddressColumnName = context.getResources().getString(R.string.resolve_target_address_column_name);
        targetPortColumnName = context.getResources().getString(R.string.resolve_target_port_column_name);
    }

    public String getTableName() {
        return tableName;
    }

    public String getIdColumnName() {
        return idColumnName;
    }

    public String getIndexColumnName() {
        return indexColumnName;
    }

    public String getNetworkTaskIdColumnName() {
        return networkTaskIdColumnName;
    }

    public String getSourcePortColumnName() {
        return sourcePortColumnName;
    }

    public String getSourceAddressColumnName() {
        return sourceAddressColumnName;
    }

    public String getTargetAddressColumnName() {
        return targetAddressColumnName;
    }

    public String getTargetPortColumnName() {
        return targetPortColumnName;
    }

    public String getAddIndexColumnStatement() {
        return "ALTER TABLE " + getTableName() + " ADD COLUMN " + getIndexColumnName() + " INTEGER NOT NULL DEFAULT 0;";
    }

    public String getDropIndexColumnStatement() {
        return "ALTER TABLE " + getTableName() + " DROP COLUMN " + getIndexColumnName() + ";";
    }

    public String getCreateTableStatement() {
        return ("CREATE TABLE IF NOT EXISTS  " + getTableName() + "(") +
                getIdColumnName() + " INTEGER PRIMARY KEY ASC, " +
                getNetworkTaskIdColumnName() + " INTEGER NOT NULL, " +
                getIndexColumnName() + " INTEGER NOT NULL DEFAULT 0, " +
                getSourceAddressColumnName() + " TEXT, " +
                getSourcePortColumnName() + " INTEGER, " +
                getTargetAddressColumnName() + " TEXT, " +
                getTargetPortColumnName() + " INTEGER);";
    }

    public String getCreateTableStatementWithoutIndex() {
        return ("CREATE TABLE IF NOT EXISTS  " + getTableName() + "(") +
                getIdColumnName() + " INTEGER PRIMARY KEY ASC, " +
                getNetworkTaskIdColumnName() + " INTEGER NOT NULL, " +
                getSourceAddressColumnName() + " TEXT, " +
                getSourcePortColumnName() + " INTEGER, " +
                getTargetAddressColumnName() + " TEXT, " +
                getTargetPortColumnName() + " INTEGER);";
    }

    public String getDropTableStatement() {
        return "DROP TABLE IF EXISTS " + getTableName();
    }

    public String getReadResolveForNetworkTaskStatement() {
        return "SELECT " +
                getIdColumnName() + ", " +
                getIndexColumnName() + ", " +
                getNetworkTaskIdColumnName() + ", " +
                getSourceAddressColumnName() + ", " +
                getSourcePortColumnName() + ", " +
                getTargetAddressColumnName() + ", " +
                getTargetPortColumnName() +
                " FROM " + getTableName() +
                " WHERE " + getNetworkTaskIdColumnName() + " = ?" +
                " ORDER BY " + getIndexColumnName() + " ASC";
    }

    public String getReadAllResolveStatement() {
        return "SELECT " +
                getIdColumnName() + ", " +
                getIndexColumnName() + ", " +
                getNetworkTaskIdColumnName() + ", " +
                getSourceAddressColumnName() + ", " +
                getSourcePortColumnName() + ", " +
                getTargetAddressColumnName() + ", " +
                getTargetPortColumnName() +
                " FROM " + getTableName() +
                " ORDER BY " + getIndexColumnName() + " ASC";
    }

    public String getUpdateIndexResolveStatement() {
        return "UPDATE " + getTableName() + " SET " + getIndexColumnName() + " = " + getIndexColumnName() + " - 1 WHERE " + getIndexColumnName() + " > ? AND " + getNetworkTaskIdColumnName() + " = ?;";
    }

    public String getDeleteOrphanResolveStatement() {
        return "DELETE FROM " + getTableName() + " WHERE " + getNetworkTaskIdColumnName() + " NOT IN (SELECT " + networkTaskDBConstants.getIdColumnName() + " FROM " + networkTaskDBConstants.getTableName() + ");";
    }

    public String getReadResolveIndexStatement() {
        return "SELECT " +
                getIdColumnName() + ", " +
                getNetworkTaskIdColumnName() + ", " +
                getIndexColumnName() +
                " FROM " + getTableName() +
                " ORDER BY " + getNetworkTaskIdColumnName() + " ASC, " + getIndexColumnName() + " ASC";
    }

    public record IndexResolve(long id, long networkTaskId, int uiIndex) {
    }
}
