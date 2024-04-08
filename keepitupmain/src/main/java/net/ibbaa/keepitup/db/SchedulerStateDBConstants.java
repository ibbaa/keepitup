/*
 * Copyright (c) 2024. Alwin Ibba
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

public class SchedulerStateDBConstants {

    private final String tableName;
    private final String idColumnName;
    private final String suspendedColumnName;
    private final String timestampColumnName;

    public SchedulerStateDBConstants(Context context) {
        tableName = context.getResources().getString(R.string.scheduler_state_table_name);
        idColumnName = context.getResources().getString(R.string.scheduler_state_id_column_name);
        suspendedColumnName = context.getResources().getString(R.string.scheduler_state_suspended_column_name);
        timestampColumnName = context.getResources().getString(R.string.scheduler_state_timestamp_column_name);
    }

    public String getTableName() {
        return tableName;
    }

    public String getIdColumnName() {
        return idColumnName;
    }

    public String getSuspendedColumnName() {
        return suspendedColumnName;
    }

    public String getTimestampColumnName() {
        return timestampColumnName;
    }

    public String getCreateTableStatement() {
        return ("CREATE TABLE IF NOT EXISTS  " + getTableName() + "(") +
                getIdColumnName() + " INTEGER PRIMARY KEY ASC, " +
                getSuspendedColumnName() + " INTEGER NOT NULL, " +
                getTimestampColumnName() + " INTEGER NOT NULL);";

    }

    public String getDropTableStatement() {
        return "DROP TABLE IF EXISTS " + getTableName();
    }

    public String getReadSchedulerStateStatement() {
        return "SELECT " +
                getIdColumnName() + ", " +
                getSuspendedColumnName() + ", " +
                getTimestampColumnName() +
                " FROM " + getTableName();
    }

    public String getInitializeSchedulerStateStatement() {
        return "INSERT INTO " + getTableName() + "(" +
                getSuspendedColumnName() + ", " +
                getTimestampColumnName() +
                ") VALUES(0, 0);";
    }
}
