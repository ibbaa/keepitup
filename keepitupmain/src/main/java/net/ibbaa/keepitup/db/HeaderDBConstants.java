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

public class HeaderDBConstants {

    private final NetworkTaskDBConstants networkTaskDBConstants;
    private final String tableName;
    private final String idColumnName;
    private final String networkTaskIdColumnName;
    private final String nameColumnName;
    private final String valueColumnName;

    public HeaderDBConstants(Context context) {
        networkTaskDBConstants = new NetworkTaskDBConstants(context);
        tableName = context.getResources().getString(R.string.header_table_name);
        idColumnName = context.getResources().getString(R.string.header_id_column_name);
        networkTaskIdColumnName = context.getResources().getString(R.string.header_taskid_column_name);
        nameColumnName = context.getResources().getString(R.string.header_name_column_name);
        valueColumnName = context.getResources().getString(R.string.header_value_column_name);
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

    public String getNameColumnName() {
        return nameColumnName;
    }

    public String getValueColumnName() {
        return valueColumnName;
    }

    public String getCreateTableStatement() {
        return ("CREATE TABLE IF NOT EXISTS  " + getTableName() + "(") +
                getIdColumnName() + " INTEGER PRIMARY KEY ASC, " +
                getNetworkTaskIdColumnName() + " INTEGER," +
                getNameColumnName() + " TEXT, " +
                getValueColumnName() + " TEXT);";
    }

    public String getDropTableStatement() {
        return "DROP TABLE IF EXISTS " + getTableName();
    }

    public String getReadGlobalHeadersStatement() {
        return "SELECT " +
                getIdColumnName() + ", " +
                getNetworkTaskIdColumnName() + ", " +
                getNameColumnName() + ", " +
                getValueColumnName() +
                " FROM " + getTableName() +
                " WHERE " + getNetworkTaskIdColumnName() + " IS NULL" +
                " ORDER BY " + getNameColumnName() + " ASC";
    }

    public String getReadHeadersForNetworkTaskStatement() {
        return "SELECT " +
                getIdColumnName() + ", " +
                getNetworkTaskIdColumnName() + ", " +
                getNameColumnName() + ", " +
                getValueColumnName() +
                " FROM " + getTableName() +
                " WHERE " + getNetworkTaskIdColumnName() + " = ?" +
                " ORDER BY " + getNameColumnName() + " ASC";
    }

    public String getReadAllHeadersStatement() {
        return "SELECT " +
                getIdColumnName() + ", " +
                getNetworkTaskIdColumnName() + ", " +
                getNameColumnName() + ", " +
                getValueColumnName() +
                " FROM " + getTableName() +
                " ORDER BY " + getNameColumnName() + " ASC";
    }

    public String getDeleteGlobalHeadersStatement() {
        return "DELETE FROM " + getTableName() + " WHERE " + getNetworkTaskIdColumnName() + " IS NULL;";
    }

    public String getDeleteOrphanHeadersStatement() {
        return "DELETE FROM " + getTableName() + " WHERE " + getNetworkTaskIdColumnName() + " NOT IN (SELECT " + networkTaskDBConstants.getIdColumnName() + " FROM " + networkTaskDBConstants.getTableName() + ");";
    }
}
