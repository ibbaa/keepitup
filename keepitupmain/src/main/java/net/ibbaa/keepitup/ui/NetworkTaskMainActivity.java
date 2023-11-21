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

package net.ibbaa.keepitup.ui;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.db.IntervalDAO;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.model.Interval;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.model.Time;
import net.ibbaa.keepitup.service.IAlarmManager;
import net.ibbaa.keepitup.service.SystemAlarmManager;
import net.ibbaa.keepitup.service.TimeBasedSuspensionScheduler;
import net.ibbaa.keepitup.ui.adapter.NetworkTaskAdapter;
import net.ibbaa.keepitup.ui.adapter.NetworkTaskUIWrapper;
import net.ibbaa.keepitup.ui.dialog.AlarmPermissionDialog;
import net.ibbaa.keepitup.ui.dialog.ConfirmDialog;
import net.ibbaa.keepitup.ui.dialog.InfoDialog;
import net.ibbaa.keepitup.ui.dialog.NetworkTaskEditDialog;
import net.ibbaa.keepitup.ui.permission.IPermissionManager;
import net.ibbaa.keepitup.ui.permission.PermissionManager;
import net.ibbaa.keepitup.ui.sync.NetworkTaskMainUIBroadcastReceiver;
import net.ibbaa.keepitup.ui.sync.NetworkTaskMainUIInitTask;
import net.ibbaa.keepitup.util.ThreadUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class NetworkTaskMainActivity extends RecyclerViewBaseActivity implements AlarmPermissionSupport {

    private NetworkTaskMainUIBroadcastReceiver broadcastReceiver;
    private IPermissionManager permissionManager;

    public void injectPermissionManager(IPermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }

    public IPermissionManager getPermissionManager() {
        if (permissionManager != null) {
            return permissionManager;
        }
        return new PermissionManager();
    }

    @Override
    protected int getRecyclerViewId() {
        return R.id.listview_activity_main_network_tasks;
    }

    @Override
    protected RecyclerView.Adapter<?> createAdapter() {
        return new NetworkTaskAdapter(readNetworkTasksFromDatabase(), this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(NetworkTaskMainActivity.class.getName(), "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_network_task);
        initRecyclerView();
        prepareAddImageButton();
        IPermissionManager permissionManager = getPermissionManager();
        if (!permissionManager.hasPostNotificationsPermission(this)) {
            permissionManager.requestPostNotificationsPermission(this);
        }
        if (!createAlarmManager().canScheduleAlarms()) {
            showAlarmPermissionDialog();
        }
        new IntervalDAO(this).deleteAllIntervals();
        Interval interval1 = new Interval();
        Interval interval2 = new Interval();
        Interval interval3 = new Interval();
        Time timeStart1 = new Time();
        timeStart1.setHour(10);
        timeStart1.setMinute(11);
        Time timeEnd1 = new Time();
        timeEnd1.setHour(13);
        timeEnd1.setMinute(14);
        Time timeStart2 = new Time();
        timeStart2.setHour(15);
        timeStart2.setMinute(16);
        Time timeEnd2 = new Time();
        timeEnd2.setHour(17);
        timeEnd2.setMinute(18);
        Time timeStart3 = new Time();
        timeStart3.setHour(15);
        timeStart3.setMinute(16);
        Time timeEnd3 = new Time();
        timeEnd3.setHour(17);
        timeEnd3.setMinute(18);
        interval1.setStart(timeStart1);
        interval1.setEnd(timeEnd1);
        interval2.setStart(timeStart2);
        interval2.setEnd(timeEnd2);
        interval3.setStart(timeStart3);
        interval3.setEnd(timeEnd3);
        new IntervalDAO(this).insertInterval(interval1);
        new IntervalDAO(this).insertInterval(interval2);
        new IntervalDAO(this).insertInterval(interval3);
        new TimeBasedSuspensionScheduler(this).restart();
    }

    private void prepareAddImageButton() {
        Log.d(NetworkTaskMainActivity.class.getName(), "prepareAddImageButton");
        ImageView addImage = findViewById(R.id.imageview_activity_main_network_task_add);
        addImage.setOnClickListener(this::onMainAddClicked);
    }

    private void showAlarmPermissionDialog() {
        Log.d(NetworkTaskMainActivity.class.getName(), "showAlarmPermissionDialog");
        AlarmPermissionDialog alarmPermissionDialog = new AlarmPermissionDialog();
        alarmPermissionDialog.show(getSupportFragmentManager(), AlarmPermissionDialog.class.getName());
    }

    @Override
    protected void onResume() {
        Log.d(NetworkTaskMainActivity.class.getName(), "onResume");
        super.onResume();
        registerReceiver();
        NetworkTaskMainUIInitTask uiInitTask = getUIInitTask((NetworkTaskAdapter) getAdapter());
        ThreadUtil.exexute(uiInitTask);
    }

    @Override
    protected void onPause() {
        Log.d(NetworkTaskMainActivity.class.getName(), "onPause");
        super.onPause();
        unregisterReceiver();
    }

    private void registerReceiver() {
        Log.d(NetworkTaskMainActivity.class.getName(), "registerReceiver");
        unregisterReceiver();
        broadcastReceiver = new NetworkTaskMainUIBroadcastReceiver(this, (NetworkTaskAdapter) getAdapter());
        registerReceiver(broadcastReceiver, new IntentFilter(NetworkTaskMainUIBroadcastReceiver.class.getName()));
    }

    private void unregisterReceiver() {
        Log.d(NetworkTaskMainActivity.class.getName(), "unregisterReceiver");
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
            broadcastReceiver = null;
        }
    }

    private List<NetworkTaskUIWrapper> readNetworkTasksFromDatabase() {
        Log.d(NetworkTaskMainActivity.class.getName(), "readNetworkTasksFromDatabase");
        try {
            NetworkTaskMainUIInitTask uiInitTask = getUIInitTask(null);
            Future<List<NetworkTaskUIWrapper>> wrapperListFuture = ThreadUtil.exexute(uiInitTask);
            List<NetworkTaskUIWrapper> wrapperList = wrapperListFuture.get(getResources().getInteger(R.integer.database_access_timeout), TimeUnit.SECONDS);
            if (wrapperList == null) {
                Log.e(NetworkTaskMainActivity.class.getName(), "Reading all network tasks from database returned null");
                showErrorDialog(getResources().getString(R.string.text_dialog_general_error_read_network_tasks));
                return new ArrayList<>();
            }
            return wrapperList;
        } catch (Exception exc) {
            Log.e(NetworkTaskMainActivity.class.getName(), "Error reading all network tasks from database", exc);
            showErrorDialog(getResources().getString(R.string.text_dialog_general_error_read_network_tasks));
            return new ArrayList<>();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_action_activity_main_defaults) {
            Log.d(NetworkTaskMainActivity.class.getName(), "menu_action_activity_main_defaults triggered");
            Intent intent = new Intent(this, DefaultsActivity.class);
            intent.setPackage(getPackageName());
            startActivity(intent);
            return true;
        } else if (id == R.id.menu_action_activity_main_global_settings) {
            Log.d(NetworkTaskMainActivity.class.getName(), "menu_action_activity_main_global_settings triggered");
            Intent intent = new Intent(this, GlobalSettingsActivity.class);
            intent.setPackage(getPackageName());
            startActivity(intent);
            return true;
        } else if (id == R.id.menu_action_activity_main_system) {
            Log.d(NetworkTaskMainActivity.class.getName(), "menu_action_activity_main_system triggered");
            Intent intent = new Intent(this, SystemActivity.class);
            intent.setPackage(getPackageName());
            startActivity(intent);
            return true;
        } else if (id == R.id.menu_action_activity_main_info) {
            Log.d(NetworkTaskMainActivity.class.getName(), "menu_action_activity_main_info triggered");
            InfoDialog infoDialog = new InfoDialog();
            infoDialog.show(getSupportFragmentManager(), InfoDialog.class.getName());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(NetworkTaskMainActivity.class.getName(), "onActivityResult");
    }

    public void onMainAddClicked(View view) {
        Log.d(NetworkTaskMainActivity.class.getName(), "onMainAddClicked");
        NetworkTaskEditDialog editDialog = new NetworkTaskEditDialog();
        if (permissionManager != null) {
            editDialog.injectPermissionManager(permissionManager);
        }
        NetworkTask task = new NetworkTask(this);
        editDialog.setArguments(task.toBundle());
        Log.d(NetworkTaskMainActivity.class.getName(), "Opening " + NetworkTaskEditDialog.class.getSimpleName());
        editDialog.show(getSupportFragmentManager(), NetworkTaskEditDialog.class.getName());
    }

    public void onMainStartStopClicked(int position) {
        NetworkTask networkTask = ((NetworkTaskAdapter) getAdapter()).getItem(position).getNetworkTask();
        Log.d(NetworkTaskMainActivity.class.getName(), "onMainStartStopClicked for network task " + networkTask);
        NetworkTaskHandler handler = new NetworkTaskHandler(this);
        if (networkTask.isRunning()) {
            Log.d(NetworkTaskMainActivity.class.getName(), "Network task is running, stopping " + networkTask);
            handler.stopNetworkTask(networkTask);
        } else {
            Log.d(NetworkTaskMainActivity.class.getName(), "Network task is not running, starting " + networkTask);
            handler.startNetworkTask(networkTask);
        }
        getAdapter().notifyItemChanged(position);
    }

    public void onMainDeleteClicked(int position) {
        Log.d(NetworkTaskMainActivity.class.getName(), "onMainDeleteClicked for position " + position + ", opening " + ConfirmDialog.class.getSimpleName() + " for type " + ConfirmDialog.Type.DELETETASK);
        showConfirmDialog(getResources().getString(R.string.text_dialog_confirm_delete_network_task), ConfirmDialog.Type.DELETETASK, position);
    }

    public void onMainEditClicked(int position) {
        NetworkTask task = ((NetworkTaskAdapter) getAdapter()).getItem(position).getNetworkTask();
        Log.d(NetworkTaskMainActivity.class.getName(), "onMainEditClicked for network task " + task);
        NetworkTaskEditDialog editDialog = new NetworkTaskEditDialog();
        if (permissionManager != null) {
            editDialog.injectPermissionManager(permissionManager);
        }
        editDialog.setArguments(task.toBundle());
        Log.d(NetworkTaskMainActivity.class.getName(), "Opening " + NetworkTaskEditDialog.class.getSimpleName());
        editDialog.show(getSupportFragmentManager(), NetworkTaskEditDialog.class.getName());
    }

    public void onMainLogClicked(int position) {
        NetworkTask networkTask = ((NetworkTaskAdapter) getAdapter()).getItem(position).getNetworkTask();
        Log.d(NetworkTaskMainActivity.class.getName(), "onMainLogClicked for network task " + networkTask);
        Intent intent = new Intent(this, NetworkTaskLogActivity.class);
        intent.putExtras(networkTask.toBundle());
        intent.setPackage(getPackageName());
        startActivity(intent);
    }

    public void onEditDialogOkClicked(NetworkTaskEditDialog editDialog) {
        NetworkTask task = editDialog.getNetworkTask();
        Log.d(NetworkTaskMainActivity.class.getName(), "onEditDialogOkClicked, network task is " + task);
        NetworkTaskHandler handler = new NetworkTaskHandler(this);
        if (task.getId() < 0) {
            Log.d(NetworkTaskMainActivity.class.getName(), "Network task is new, inserting " + task);
            handler.insertNetworkTask(task);
            getAdapter().notifyDataSetChanged();
        } else {
            NetworkTask initialTask = editDialog.getInitialNetworkTask();
            Log.d(NetworkTaskMainActivity.class.getName(), "Initial network task is " + initialTask);
            if (initialTask.isTechnicallyEqual(task)) {
                Log.d(NetworkTaskMainActivity.class.getName(), "Initial network task and network task are technically equal.");
                Log.d(NetworkTaskMainActivity.class.getName(), "No changes were made. Skipping update.");
            } else {
                Log.d(NetworkTaskMainActivity.class.getName(), "Updating " + task);
                handler.updateNetworkTask(task);
                getAdapter().notifyDataSetChanged();
            }
        }
        editDialog.dismiss();
    }

    public void onEditDialogCancelClicked(NetworkTaskEditDialog editDialog) {
        Log.d(NetworkTaskMainActivity.class.getName(), "onEditDialogCancelClicked");
        editDialog.dismiss();
    }

    public void onConfirmDialogOkClicked(ConfirmDialog confirmDialog, ConfirmDialog.Type type) {
        Log.d(NetworkTaskMainActivity.class.getName(), "onConfirmDialogOkClicked for type " + type);
        if (ConfirmDialog.Type.DELETETASK.equals(type)) {
            int position = confirmDialog.getPosition();
            if (position >= 0) {
                NetworkTaskHandler handler = new NetworkTaskHandler(this);
                NetworkTask task = ((NetworkTaskAdapter) getAdapter()).getItem(position).getNetworkTask();
                Log.d(NetworkTaskMainActivity.class.getName(), "Deleting " + task);
                handler.deleteNetworkTask(task);
                getAdapter().notifyDataSetChanged();
            } else {
                Log.e(NetworkTaskMainActivity.class.getName(), ConfirmDialog.class.getSimpleName() + " arguments do not contain position key " + confirmDialog.getPositionKey());
            }
        } else {
            Log.e(NetworkTaskMainActivity.class.getName(), "Unknown type " + type);
        }
        confirmDialog.dismiss();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(NetworkTaskMainActivity.class.getName(), "onRequestPermissionsResult for code " + requestCode);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        IPermissionManager permissionManager = getPermissionManager();
        permissionManager.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    public void onConfirmDialogCancelClicked(ConfirmDialog confirmDialog) {
        Log.d(NetworkTaskMainActivity.class.getName(), "onConfirmDialogCancelClicked");
        confirmDialog.dismiss();
    }

    @Override
    public void onAlarmPermissionDialogOkClicked(AlarmPermissionDialog alarmPermissionDialog) {
        Log.d(NetworkTaskMainActivity.class.getName(), "onAlarmPermissionDialogOkClicked");
        if (createAlarmManager().canScheduleAlarms()) {
            alarmPermissionDialog.dismiss();
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Log.d(NetworkTaskMainActivity.class.getName(), "Redirecting to alarm settings");
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
            startActivity(intent);
        }
    }

    private NetworkTaskMainUIInitTask getUIInitTask(NetworkTaskAdapter adapter) {
        return new NetworkTaskMainUIInitTask(this, adapter);
    }

    private IAlarmManager createAlarmManager() {
        return new SystemAlarmManager(this);
    }
}
