/*
 * Copyright (c) 2023. Alwin Ibba
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

class IntervalDBConstants {

    private final String tableName;
    private final String idColumnName;
    private final String activeColumnName;
    private final String hourstartColumnName;
    private final String minutestartColumnName;
    private final String hourendColumnName;
    private final String minuteendColumnName;

    public IntervalDBConstants(Context context) {
        tableName = context.getResources().getString(R.string.interval_table_name);
        idColumnName = context.getResources().getString(R.string.interval_id_column_name);
        activeColumnName = context.getResources().getString(R.string.interval_active_column_name);
        hourstartColumnName = context.getResources().getString(R.string.interval_hourstart_column_name);
        minutestartColumnName = context.getResources().getString(R.string.interval_minutestart_column_name);
        hourendColumnName = context.getResources().getString(R.string.interval_hourend_column_name);
        minuteendColumnName = context.getResources().getString(R.string.interval_minuteend_column_name);
    }

    public String getTableName() {
        return tableName;
    }

    public String getIdColumnName() {
        return idColumnName;
    }

    public String getActiveColumnName() {
        return activeColumnName;
    }

    public String getHourstartColumnName() {
        return hourstartColumnName;
    }

    public String getMinutestartColumnName() {
        return minutestartColumnName;
    }

    public String getHourendColumnName() {
        return hourendColumnName;
    }

    public String getMinuteendColumnName() {
        return minuteendColumnName;
    }

    public String getCreateTableStatement() {
        return ("CREATE TABLE IF NOT EXISTS  " + getTableName() + "(") +
                getIdColumnName() + " INTEGER PRIMARY KEY ASC, " +
                getActiveColumnName() + " INTEGER, " +
                getHourstartColumnName() + " INTEGER, " +
                getMinutestartColumnName() + " INTEGER, " +
                getHourendColumnName() + " INTEGER, " +
                getMinuteendColumnName() + " INTEGER);";
    }

    public String getDropTableStatement() {
        return "DROP TABLE IF EXISTS " + getTableName();
    }

    public String getReadIntervalStatement() {
        return "SELECT " +
                getIdColumnName() + ", " +
                getActiveColumnName() + ", " +
                getHourstartColumnName() + ", " +
                getMinutestartColumnName() + ", " +
                getHourendColumnName() + ", " +
                getMinuteendColumnName() +
                " FROM " + getTableName() +
                "  WHERE " + getIdColumnName() + " = ?;";
    }

    public String getReadAllIntervalsStatement() {
        return "SELECT " +
                getIdColumnName() + ", " +
                getActiveColumnName() + ", " +
                getHourstartColumnName() + ", " +
                getMinutestartColumnName() + ", " +
                getHourendColumnName() + ", " +
                getMinuteendColumnName() +
                " FROM " + getTableName() + ";";
    }
}
