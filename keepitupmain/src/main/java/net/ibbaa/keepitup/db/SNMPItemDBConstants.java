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

public class SNMPItemDBConstants {

    private final NetworkTaskDBConstants networkTaskDBConstants;
    private final String tableName;
    private final String idColumnName;
    private final String networkTaskIdColumnName;
    private final String snmpItemTypeColumnName;
    private final String nameColumnName;
    private final String oidColumnName;
    private final String monitoredColumnName;

    public SNMPItemDBConstants(Context context) {
        networkTaskDBConstants = new NetworkTaskDBConstants(context);
        tableName = context.getResources().getString(R.string.snmpitem_table_name);
        idColumnName = context.getResources().getString(R.string.snmpitem_id_column_name);
        networkTaskIdColumnName = context.getResources().getString(R.string.snmpitem_taskid_column_name);
        snmpItemTypeColumnName = context.getResources().getString(R.string.snmpitem_type_column_name);
        nameColumnName = context.getResources().getString(R.string.snmpitem_name_column_name);
        oidColumnName = context.getResources().getString(R.string.snmpitem_oid_column_name);
        monitoredColumnName = context.getResources().getString(R.string.snmpitem_monitored_column_name);
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

    public String getSnmpItemTypeColumnName() {
        return snmpItemTypeColumnName;
    }

    public String getNameColumnName() {
        return nameColumnName;
    }

    public String getOidColumnName() {
        return oidColumnName;
    }

    public String getMonitoredColumnName() {
        return monitoredColumnName;
    }

    public String getCreateTableStatement() {
        return ("CREATE TABLE IF NOT EXISTS  " + getTableName() + "(") +
                getIdColumnName() + " INTEGER PRIMARY KEY ASC, " +
                getNetworkTaskIdColumnName() + " INTEGER NOT NULL, " +
                getSnmpItemTypeColumnName() + " INTEGER, " +
                getNameColumnName() + " TEXT, " +
                getOidColumnName() + " TEXT, " +
                getMonitoredColumnName() + " INTEGER);";
    }

    public String getDropTableStatement() {
        return "DROP TABLE IF EXISTS " + getTableName();
    }

    public String getReadSNMPItemForNetworkTaskStatement() {
        return "SELECT " +
                getIdColumnName() + ", " +
                getNetworkTaskIdColumnName() + ", " +
                getSnmpItemTypeColumnName() + ", " +
                getNameColumnName() + ", " +
                getOidColumnName() + ", " +
                getMonitoredColumnName() +
                " FROM " + getTableName() +
                " WHERE " + getNetworkTaskIdColumnName() + " = ?" +
                " ORDER BY " + getNameColumnName() + " ASC";
    }

    public String getReadAllSNMPItemStatement() {
        return "SELECT " +
                getIdColumnName() + ", " +
                getNetworkTaskIdColumnName() + ", " +
                getSnmpItemTypeColumnName() + ", " +
                getNameColumnName() + ", " +
                getOidColumnName() + ", " +
                getMonitoredColumnName() +
                " FROM " + getTableName() +
                " ORDER BY " + getNameColumnName() + " ASC";
    }

    public String getDeleteOrphanSNMPItemStatement() {
        return "DELETE FROM " + getTableName() + " WHERE " + getNetworkTaskIdColumnName() + " NOT IN (SELECT " + networkTaskDBConstants.getIdColumnName() + " FROM " + networkTaskDBConstants.getTableName() + ");";
    }
}
