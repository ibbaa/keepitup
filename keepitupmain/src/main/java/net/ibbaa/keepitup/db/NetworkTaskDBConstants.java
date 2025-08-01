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

class NetworkTaskDBConstants {

    private final String tableName;
    private final String idColumnName;
    private final String indexColumnName;
    private final String schedulerIdColumnName;
    private final String nameColumnName;
    private final String instancesColumnName;
    private final String addressColumnName;
    private final String portColumnName;
    private final String accessTypeColumnName;
    private final String intervalColumnName;
    private final String onlyWifiColumnName;
    private final String notificationColumnName;
    private final String runningColumnName;
    private final String lastScheduledColumnName;
    private final String failureCountColumnName;
    private final String highPrioColumnName;

    public NetworkTaskDBConstants(Context context) {
        tableName = context.getResources().getString(R.string.task_table_name);
        idColumnName = context.getResources().getString(R.string.task_id_column_name);
        indexColumnName = context.getResources().getString(R.string.task_index_column_name);
        schedulerIdColumnName = context.getResources().getString(R.string.task_schedulerid_column_name);
        nameColumnName = context.getResources().getString(R.string.task_name_column_name);
        instancesColumnName = context.getResources().getString(R.string.task_instances_column_name);
        addressColumnName = context.getResources().getString(R.string.task_address_column_name);
        portColumnName = context.getResources().getString(R.string.task_port_column_name);
        accessTypeColumnName = context.getResources().getString(R.string.task_accesstype_column_name);
        intervalColumnName = context.getResources().getString(R.string.task_interval_column_name);
        onlyWifiColumnName = context.getResources().getString(R.string.task_onlywifi_column_name);
        notificationColumnName = context.getResources().getString(R.string.task_notification_column_name);
        runningColumnName = context.getResources().getString(R.string.task_running_column_name);
        lastScheduledColumnName = context.getResources().getString(R.string.task_lastscheduled_column_name);
        failureCountColumnName = context.getResources().getString(R.string.task_failurecount_column_name);
        highPrioColumnName = context.getResources().getString(R.string.task_highprio_column_name);
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

    public String getSchedulerIdColumnName() {
        return schedulerIdColumnName;
    }

    public String getNameColumnName() {
        return nameColumnName;
    }

    public String getInstancesColumnName() {
        return instancesColumnName;
    }

    public String getAddressColumnName() {
        return addressColumnName;
    }

    public String getPortColumnName() {
        return portColumnName;
    }

    public String getAccessTypeColumnName() {
        return accessTypeColumnName;
    }

    public String getIntervalColumnName() {
        return intervalColumnName;
    }

    public String getOnlyWifiColumnName() {
        return onlyWifiColumnName;
    }

    public String getNotificationColumnName() {
        return notificationColumnName;
    }

    public String getRunningColumnName() {
        return runningColumnName;
    }

    public String getLastScheduledColumnName() {
        return lastScheduledColumnName;
    }

    public String getFailureCountColumnName() {
        return failureCountColumnName;
    }

    public String getHighPrioColumnName() {
        return highPrioColumnName;
    }

    public String getCreateTableStatement() {
        return ("CREATE TABLE IF NOT EXISTS " + getTableName() + "(") +
                getIdColumnName() + " INTEGER PRIMARY KEY ASC, " +
                getIndexColumnName() + " INTEGER NOT NULL, " +
                getSchedulerIdColumnName() + " INTEGER, " +
                getNameColumnName() + " TEXT, " +
                getInstancesColumnName() + " INTEGER, " +
                getAddressColumnName() + " TEXT, " +
                getPortColumnName() + " INTEGER, " +
                getAccessTypeColumnName() + " INTEGER, " +
                getIntervalColumnName() + " INTEGER, " +
                getOnlyWifiColumnName() + " TEXT, " +
                getNotificationColumnName() + " INTEGER, " +
                getRunningColumnName() + " INTEGER, " +
                getLastScheduledColumnName() + " INTEGER, " +
                getFailureCountColumnName() + " INTEGER, " +
                getHighPrioColumnName() + " INTEGER);";
    }

    public String getCreateTableStatementWithoutFailureCount() {
        return ("CREATE TABLE IF NOT EXISTS " + getTableName() + "(") +
                getIdColumnName() + " INTEGER PRIMARY KEY ASC, " +
                getIndexColumnName() + " INTEGER NOT NULL, " +
                getSchedulerIdColumnName() + " INTEGER, " +
                getNameColumnName() + " TEXT, " +
                getInstancesColumnName() + " INTEGER, " +
                getAddressColumnName() + " TEXT, " +
                getPortColumnName() + " INTEGER, " +
                getAccessTypeColumnName() + " INTEGER, " +
                getIntervalColumnName() + " INTEGER, " +
                getOnlyWifiColumnName() + " TEXT, " +
                getNotificationColumnName() + " INTEGER, " +
                getRunningColumnName() + " INTEGER, " +
                getLastScheduledColumnName() + " INTEGER, " +
                getHighPrioColumnName() + " INTEGER);";
    }

    public String getCreateTableStatementWithoutHighPrioAndName() {
        return ("CREATE TABLE IF NOT EXISTS " + getTableName() + "(") +
                getIdColumnName() + " INTEGER PRIMARY KEY ASC, " +
                getIndexColumnName() + " INTEGER NOT NULL, " +
                getSchedulerIdColumnName() + " INTEGER, " +
                getInstancesColumnName() + " INTEGER, " +
                getAddressColumnName() + " TEXT, " +
                getPortColumnName() + " INTEGER, " +
                getAccessTypeColumnName() + " INTEGER, " +
                getIntervalColumnName() + " INTEGER, " +
                getOnlyWifiColumnName() + " TEXT, " +
                getNotificationColumnName() + " INTEGER, " +
                getRunningColumnName() + " INTEGER, " +
                getLastScheduledColumnName() + " INTEGER, " +
                getFailureCountColumnName() + " INTEGER);";
    }

    public String getCreateTableStatementWithoutAddedColumns() {
        return ("CREATE TABLE IF NOT EXISTS " + getTableName() + "(") +
                getIdColumnName() + " INTEGER PRIMARY KEY ASC, " +
                getIndexColumnName() + " INTEGER NOT NULL, " +
                getSchedulerIdColumnName() + " INTEGER, " +
                getInstancesColumnName() + " INTEGER, " +
                getAddressColumnName() + " TEXT, " +
                getPortColumnName() + " INTEGER, " +
                getAccessTypeColumnName() + " INTEGER, " +
                getIntervalColumnName() + " INTEGER, " +
                getOnlyWifiColumnName() + " TEXT, " +
                getNotificationColumnName() + " INTEGER, " +
                getRunningColumnName() + " INTEGER, " +
                getLastScheduledColumnName() + " INTEGER);";
    }

    public String getDropTableStatement() {
        return "DROP TABLE IF EXISTS " + getTableName();
    }

    public String getAddFailureCountColumnStatement() {
        return "ALTER TABLE " + getTableName() + " ADD COLUMN " + getFailureCountColumnName() + " INTEGER;";
    }

    public String getDropFailureCountColumnStatement() {
        return "ALTER TABLE " + getTableName() + " DROP COLUMN " + getFailureCountColumnName() + ";";
    }

    public String getAddHighPrioColumnStatement() {
        return "ALTER TABLE " + getTableName() + " ADD COLUMN " + getHighPrioColumnName() + " INTEGER;";
    }

    public String getDropHighPrioColumnStatement() {
        return "ALTER TABLE " + getTableName() + " DROP COLUMN " + getHighPrioColumnName() + ";";
    }

    public String getAddNameColumnStatement() {
        return "ALTER TABLE " + getTableName() + " ADD COLUMN " + getNameColumnName() + " TEXT;";
    }

    public String getDropNameColumnStatement() {
        return "ALTER TABLE " + getTableName() + " DROP COLUMN " + getNameColumnName() + ";";
    }

    public String getReadNetworkTaskStatement() {
        return "SELECT " +
                getIdColumnName() + ", " +
                getIndexColumnName() + ", " +
                getSchedulerIdColumnName() + ", " +
                getNameColumnName() + ", " +
                getInstancesColumnName() + ", " +
                getAddressColumnName() + ", " +
                getPortColumnName() + ", " +
                getAccessTypeColumnName() + ", " +
                getIntervalColumnName() + ", " +
                getOnlyWifiColumnName() + ", " +
                getNotificationColumnName() + ", " +
                getRunningColumnName() + ", " +
                getLastScheduledColumnName() + ", " +
                getFailureCountColumnName() + ", " +
                getHighPrioColumnName() +
                " FROM " + getTableName() +
                "  WHERE " + getIdColumnName() + " = ?;";
    }


    public String getReadAllNetworkTasksStatement() {
        return "SELECT " +
                getIdColumnName() + ", " +
                getIndexColumnName() + ", " +
                getSchedulerIdColumnName() + ", " +
                getNameColumnName() + ", " +
                getInstancesColumnName() + ", " +
                getAddressColumnName() + ", " +
                getPortColumnName() + ", " +
                getAccessTypeColumnName() + ", " +
                getIntervalColumnName() + ", " +
                getOnlyWifiColumnName() + ", " +
                getNotificationColumnName() + ", " +
                getRunningColumnName() + ", " +
                getLastScheduledColumnName() + ", " +
                getFailureCountColumnName() + ", " +
                getHighPrioColumnName() +
                " FROM " + getTableName() +
                " ORDER BY " + getIndexColumnName() + " ASC";
    }

    public String getReadNetworkTasksIndexStatement() {
        return "SELECT " +
                getIdColumnName() + ", " +
                getIndexColumnName() +
                " FROM " + getTableName() +
                " ORDER BY " + getIndexColumnName() + " ASC";
    }

    public String getSchedulerIdCountStatement() {
        return "SELECT COUNT(*) FROM " + getTableName() + " WHERE " + getSchedulerIdColumnName() + " = ?";
    }

    public String getRunningCountStatement() {
        return "SELECT COUNT(*) FROM " + getTableName() + " WHERE " + getRunningColumnName() + " = ?";
    }

    public String getReadInstancesStatement() {
        return "SELECT " + getInstancesColumnName() + " FROM " + getTableName() + " WHERE " + getIdColumnName() + " = ?";
    }

    public String getReadFailureCountStatement() {
        return "SELECT " + getFailureCountColumnName() + " FROM " + getTableName() + " WHERE " + getIdColumnName() + " = ?";
    }

    public String getUpdateIndexNetworkTasksStatement() {
        return "UPDATE " + getTableName() + " SET " + getIndexColumnName() + " = " + getIndexColumnName() + " - 1 WHERE " + getIndexColumnName() + " > ?;";
    }

    public record IndexTask(long id, int uiIndex) {

    }
}
