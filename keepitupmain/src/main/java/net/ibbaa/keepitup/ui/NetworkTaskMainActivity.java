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

package net.ibbaa.keepitup.ui;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.db.NetworkTaskDAO;
import net.ibbaa.keepitup.db.ResolveDAO;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.model.AccessTypeData;
import net.ibbaa.keepitup.model.Equality;
import net.ibbaa.keepitup.model.Header;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.model.Resolve;
import net.ibbaa.keepitup.model.SNMPVersion;
import net.ibbaa.keepitup.notification.NotificationHandler;
import net.ibbaa.keepitup.resources.NoBackupPreferenceManager;
import net.ibbaa.keepitup.resources.PreferenceManager;
import net.ibbaa.keepitup.service.IAlarmManager;
import net.ibbaa.keepitup.service.NetworkTaskProcessServiceScheduler;
import net.ibbaa.keepitup.service.SystemAlarmManager;
import net.ibbaa.keepitup.service.alarm.AlarmService;
import net.ibbaa.keepitup.ui.adapter.NetworkTaskAdapter;
import net.ibbaa.keepitup.ui.adapter.NetworkTaskDragAndDropCallback;
import net.ibbaa.keepitup.ui.adapter.NetworkTaskUIWrapper;
import net.ibbaa.keepitup.ui.dialog.AlarmPermissionDialog;
import net.ibbaa.keepitup.ui.dialog.ConfirmDialog;
import net.ibbaa.keepitup.ui.dialog.CredentialInfoDialog;
import net.ibbaa.keepitup.ui.dialog.GeneralMessageDialog;
import net.ibbaa.keepitup.ui.dialog.InfoDialog;
import net.ibbaa.keepitup.ui.dialog.NetworkTaskEditDialog;
import net.ibbaa.keepitup.ui.dialog.SNMPItemDialog;
import net.ibbaa.keepitup.ui.dialog.SettingsInput;
import net.ibbaa.keepitup.ui.dialog.SettingsInputDialog;
import net.ibbaa.keepitup.ui.permission.IPermissionManager;
import net.ibbaa.keepitup.ui.permission.IStoragePermissionManager;
import net.ibbaa.keepitup.ui.permission.PermissionManager;
import net.ibbaa.keepitup.ui.permission.StoragePermissionManager;
import net.ibbaa.keepitup.ui.support.AlarmPermissionSupport;
import net.ibbaa.keepitup.ui.support.MessageSupport;
import net.ibbaa.keepitup.ui.support.SettingsInputSupport;
import net.ibbaa.keepitup.ui.sync.HeaderBulkDeleteTask;
import net.ibbaa.keepitup.ui.sync.HeaderSyncHandler;
import net.ibbaa.keepitup.ui.sync.NetworkTaskMainUIBroadcastReceiver;
import net.ibbaa.keepitup.ui.sync.NetworkTaskMainUIInitTask;
import net.ibbaa.keepitup.ui.validation.CredentialInfo;
import net.ibbaa.keepitup.ui.validation.NetworkTaskNameFieldValidator;
import net.ibbaa.keepitup.util.BundleUtil;
import net.ibbaa.keepitup.util.CollectionUtil;
import net.ibbaa.keepitup.util.HTTPUtil;
import net.ibbaa.keepitup.util.StringUtil;
import net.ibbaa.keepitup.util.SystemUtil;
import net.ibbaa.keepitup.util.ThreadUtil;
import net.ibbaa.keepitup.util.UIUtil;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class NetworkTaskMainActivity extends RecyclerViewBaseActivity implements SettingsInputSupport, AlarmPermissionSupport, MessageSupport {

    private enum Message {
        SAFNOTICE,
    }

    private static final Equality<Header> HEADER_TECHNICAL_EQUALITY = Header::isTechnicallyEqual;
    private static final Equality<Resolve> RESOLVE_TECHNICAL_EQUALITY = Resolve::isTechnicallyEqual;

    private NetworkTaskMainUIBroadcastReceiver broadcastReceiver;
    private IPermissionManager permissionManager;
    private ItemTouchHelper itemTouchHelper;
    private boolean credentialInfoDialogShown;
    private boolean folderPermissionNotificationSent;

    public static String getBypassSystemSAFKey() {
        return NetworkTaskMainActivity.class.getSimpleName() + ".BypassSystemSAF";
    }

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
    public int getRecyclerViewId() {
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
        initEdgeToEdgeInsets(R.id.layout_activity_main);
        restoreInstanceState(savedInstanceState);
        initRecyclerView();
        checkIndexConsistency();
        initDragAndDrop();
        prepareAddImageButton();
        startForegroundServiceDelayed();
        checkInvalidCredentials();
        checkPermissions();
        //handleSAFNotice();
    }

    private void initDragAndDrop() {
        Log.d(NetworkTaskMainActivity.class.getName(), "initDragAndDrop");
        RecyclerView recyclerView = findViewById(getRecyclerViewId());
        itemTouchHelper = new ItemTouchHelper(new NetworkTaskDragAndDropCallback(this));
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @SuppressWarnings("NotifyDataSetChanged")
    private void checkIndexConsistency() {
        Log.d(NetworkTaskMainActivity.class.getName(), "checkIndexConsistency");
        boolean isNetworkTaskIndexConsistent = ((NetworkTaskAdapter) getAdapter()).isNetworkTaskIndexConsistent();
        if (!isNetworkTaskIndexConsistent) {
            Log.e(NetworkTaskMainActivity.class.getName(), "UI index is inconsistent. Repairing...");
            NetworkTaskDAO networkTaskDAO = new NetworkTaskDAO(this);
            networkTaskDAO.normalizeUIIndex();
            ((NetworkTaskAdapter) getAdapter()).replaceItems(readNetworkTasksFromDatabase());
            getAdapter().notifyDataSetChanged();
        }
        boolean isResolveIndexConsistent = ((NetworkTaskAdapter) getAdapter()).isResolveIndexConsistent();
        if (!isResolveIndexConsistent) {
            Log.e(NetworkTaskMainActivity.class.getName(), "Resolve index is inconsistent. Repairing...");
            ResolveDAO resolveDAO = new ResolveDAO(this);
            resolveDAO.normalizeUIIndex();
        }
    }

    private void prepareAddImageButton() {
        Log.d(NetworkTaskMainActivity.class.getName(), "prepareAddImageButton");
        ImageView addImage = findViewById(R.id.imageview_activity_main_network_task_add);
        addImage.setOnClickListener(this::onMainAddClicked);
    }

    private void startForegroundServiceDelayed() {
        Log.d(NetworkTaskMainActivity.class.getName(), "startForegroundServiceDelayed");
        List<NetworkTaskUIWrapper> networkTasks = ((NetworkTaskAdapter) getAdapter()).getAllItems();
        for (NetworkTaskUIWrapper wrapper : networkTasks) {
            if (wrapper.getNetworkTask().isRunning()) {
                getNetworkTaskProcessServiceScheduler().restartForegroundService();
                return;
            }
        }
    }

    private void checkInvalidCredentials() {
        Log.d(NetworkTaskMainActivity.class.getName(), "checkInvalidCredentials");
        NetworkTaskAdapter adapter = (NetworkTaskAdapter) getAdapter();
        Map<Long, List<Header>> invalidHeaders = adapter.getInvalidHeaders();
        HeaderSyncHandler syncHandler = new HeaderSyncHandler(this);
        List<NetworkTask> invalidSNMPCommunities = adapter.getInvalidSNMPCommunities();
        List<Header> defaultHeaders = syncHandler.getGlobalHeaders();
        List<Header> invalidDefaultHeaders = syncHandler.getInvalidHeaders(defaultHeaders);
        if (invalidSNMPCommunities.isEmpty() && invalidHeaders.isEmpty() && invalidDefaultHeaders.isEmpty()) {
            return;
        }
        List<Header> toDelete = new ArrayList<>();
        List<CredentialInfo> toDisplay = collectCredentialsActionLists(adapter, invalidSNMPCommunities, invalidHeaders, invalidDefaultHeaders, toDelete);
        if (!credentialInfoDialogShown && !toDisplay.isEmpty()) {
            String tag = CredentialInfoDialog.class.getName();
            FragmentManager fragmentManager = getSupportFragmentManager();
            if (fragmentManager.findFragmentByTag(tag) == null) {
                credentialInfoDialogShown = true;
                showCredentialInfoDialog(toDisplay);
            }
        }
        if (!toDelete.isEmpty()) {
            HeaderBulkDeleteTask bulkDeleteTask = new HeaderBulkDeleteTask(this, toDelete);
            ThreadUtil.execute(bulkDeleteTask);
        }
    }

    private List<CredentialInfo> collectCredentialsActionLists(NetworkTaskAdapter adapter, List<NetworkTask> snmpCommunities, Map<Long, List<Header>> headers, List<Header> defaultHeaders, List<Header> toDelete) {
        Log.d(NetworkTaskMainActivity.class.getName(), "collectCredentialsActionLists");
        List<CredentialInfo> toDisplay = new ArrayList<>();
        if (snmpCommunities != null && !snmpCommunities.isEmpty()) {
            List<CredentialInfo> credentialInfos = UIUtil.snmpCommunitiesToCredentialInfoList(this, snmpCommunities);
            toDisplay.addAll(credentialInfos);
        }
        if (defaultHeaders != null && !defaultHeaders.isEmpty()) {
            List<CredentialInfo> credentialInfos = UIUtil.headersToCredentialInfoList(this, null, defaultHeaders);
            toDisplay.addAll(credentialInfos);
        }
        if (headers != null && !headers.isEmpty()) {
            List<NetworkTaskUIWrapper> items = adapter.getAllItems();
            for (NetworkTaskUIWrapper currentItem : items) {
                NetworkTask task = currentItem.getNetworkTask();
                long networkTaskId = task.getId();
                List<Header> taskInvalidHeaders = headers.get(networkTaskId);
                if (taskInvalidHeaders != null) {
                    boolean useDefaultHeaders = currentItem.getAccessTypeData() == null || currentItem.getAccessTypeData().isUseDefaultHeaders();
                    boolean isDownload = HTTPUtil.isDownloadTask(task);
                    if (useDefaultHeaders || !isDownload) {
                        if (toDelete != null) {
                            toDelete.addAll(taskInvalidHeaders);
                        }
                    } else {
                        List<CredentialInfo> credentialInfos = UIUtil.headersToCredentialInfoList(this, task, taskInvalidHeaders);
                        toDisplay.addAll(credentialInfos);
                    }
                }
            }
        }
        return toDisplay;
    }

    @Override
    protected void onResume() {
        Log.d(NetworkTaskMainActivity.class.getName(), "onResume");
        super.onResume();
        registerReceiver();
        NetworkTaskMainUIInitTask uiInitTask = getUIInitTask((NetworkTaskAdapter) getAdapter());
        ThreadUtil.execute(uiInitTask);
        checkActiveAlarm();
        scrollToProvidedEntry();
    }

    @Override
    protected void onSaveInstanceState(@NotNull Bundle state) {
        super.onSaveInstanceState(state);
        state.putBoolean(getCredentialInfoDialogShownKey(), credentialInfoDialogShown);
        state.putBoolean(getFolderPermissionNotificationSentKey(), folderPermissionNotificationSent);
    }

    private void restoreInstanceState(Bundle state) {
        if (state == null) {
            return;
        }
        if (state.containsKey(getCredentialInfoDialogShownKey())) {
            credentialInfoDialogShown = state.getBoolean(getCredentialInfoDialogShownKey());
        }
        if (state.containsKey(getFolderPermissionNotificationSentKey())) {
            folderPermissionNotificationSent = state.getBoolean(getFolderPermissionNotificationSentKey());
        }
    }

    private String getCredentialInfoDialogShownKey() {
        return NetworkTaskMainActivity.class.getSimpleName() + ".CredentialInfoDialogShown";
    }

    private String getFolderPermissionNotificationSentKey() {
        return NetworkTaskMainActivity.class.getSimpleName() + ".FolderPermissionNotificationSent";
    }

    private void scrollToProvidedEntry() {
        Log.d(NetworkTaskMainActivity.class.getName(), "scrollToProvidedEntry");
        Intent intent = getIntent();
        if (intent == null) {
            Log.d(NetworkTaskMainActivity.class.getName(), "scrollToProvidedEntry, no intent present");
            return;
        }
        Bundle taskBundle = intent.getBundleExtra(getNetworkTaskKey());
        if (taskBundle == null) {
            Log.d(NetworkTaskMainActivity.class.getName(), "scrollToProvidedEntry, no taskBundle present");
            return;
        }
        NetworkTask task = new NetworkTask(taskBundle);
        scrollToEntryByIndex(task.getIndex());
        Log.d(NetworkTaskMainActivity.class.getName(), "scrollToProvidedEntry, remove task bundle");
        getIntent().removeExtra(getNetworkTaskKey());
    }

    private void scrollToEntryByIndex(int index) {
        Log.d(NetworkTaskMainActivity.class.getName(), "scrollToEntryByIndex, index is " + index);
        if (!getResources().getBoolean(R.bool.ui_use_scrolling)) {
            Log.d(NetworkTaskMainActivity.class.getName(), "scrolling is disabled");
            return;
        }
        if (index >= 0 && index < getAdapter().getItemCount()) {
            RecyclerView recyclerView = findViewById(getRecyclerViewId());
            NestedScrollView scrollView = findViewById(R.id.scrollview_main_content);
            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            if (layoutManager == null) {
                Log.e(NetworkTaskMainActivity.class.getName(), "scrollToProvidedEntry, layoutManager is null");
                return;
            }
            layoutManager.scrollToPosition(index);
            Log.d(NetworkTaskMainActivity.class.getName(), "scrollToProvidedEntry, post scroll event");
            recyclerView.postDelayed(() -> {
                View targetView = layoutManager.findViewByPosition(index);
                if (targetView != null) {
                    int[] location = new int[2];
                    targetView.getLocationOnScreen(location);
                    int[] scrollLocation = new int[2];
                    scrollView.getLocationOnScreen(scrollLocation);
                    int scrollY = location[1] - scrollLocation[1];
                    scrollView.smoothScrollBy(0, scrollY);
                }
            }, 300);
        }
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(broadcastReceiver, new IntentFilter(NetworkTaskMainUIBroadcastReceiver.class.getName()), Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(broadcastReceiver, new IntentFilter(NetworkTaskMainUIBroadcastReceiver.class.getName()));
        }
    }

    private void unregisterReceiver() {
        Log.d(NetworkTaskMainActivity.class.getName(), "unregisterReceiver");
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
            broadcastReceiver = null;
        }
    }

    private void checkActiveAlarm() {
        Log.d(NetworkTaskMainActivity.class.getName(), "checkActiveAlarm");
        if (AlarmService.isRunning()) {
            Log.d(NetworkTaskMainActivity.class.getName(), "Alarm is active");
            FragmentManager fragmentManager = getSupportFragmentManager();
            String tag = ConfirmDialog.class.getName();
            if (fragmentManager.findFragmentByTag(tag) == null) {
                showConfirmDialog(getResources().getString(R.string.text_dialog_confirm_dismiss_active_alarm), getResources().getString(R.string.text_dialog_confirm_dismiss_active_alarm_description), ConfirmDialog.Type.DISMISSALARM);
            }
        }
    }

    private void checkPermissions() {
        Log.d(NetworkTaskMainActivity.class.getName(), "checkPermissions");
        NoBackupPreferenceManager noBackupPreferenceManager = new NoBackupPreferenceManager(this);
        IPermissionManager permissionManager = getPermissionManager();
        if (!permissionManager.hasPostNotificationsPermission(this) && !noBackupPreferenceManager.getPreferenceAskedNotificationPermission()) {
            Log.d(NetworkTaskMainActivity.class.getName(), "Permission to post notifications is missing");
            noBackupPreferenceManager.setPreferenceAskedNotificationPermission(true);
            permissionManager.requestPostNotificationsPermission(this);
        }
        if (!createAlarmManager().canScheduleAlarms()) {
            Log.d(NetworkTaskMainActivity.class.getName(), "Permission to schedule alarms is missing");
            showAlarmPermissionDialog();
        }
        checkSAFPermissions();
    }

    @SuppressWarnings("unused")
    private void handleSAFNotice() {
        Log.d(NetworkTaskMainActivity.class.getName(), "handleSAFNotice");
        boolean bypassSystemSAF = BundleUtil.booleanFromBundle(getBypassSystemSAFKey(), getIntent().getExtras());
        if (!SystemUtil.supportsSAFFeature() || bypassSystemSAF) {
            Log.d(NetworkTaskMainActivity.class.getName(), "SAF not supported. Not showing dialog.");
            return;
        }
        PreferenceManager preferenceManager = new PreferenceManager(this);
        if (preferenceManager.getPreferenceAllowArbitraryFileLocation()) {
            Log.d(NetworkTaskMainActivity.class.getName(), "SAF is enabled. Not showing dialog.");
            return;
        }
        //Set default to true in systemprefs.xml, remove initializeSAFFlag in StartupService, SystemActivity.onImportDone, SystemActivity.onPurgeDone
        if (!preferenceManager.getPreferenceSAFNoticeShown()) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            String tag = GeneralMessageDialog.class.getName();
            if (fragmentManager.findFragmentByTag(tag) == null) {
                showMessageDialog(getResources().getString(R.string.text_dialog_general_message_saf_notice_title), getResources().getString(R.string.text_dialog_general_message_saf_notice_message), Typeface.NORMAL, Message.SAFNOTICE.name(), true);
            }
        }
    }

    @SuppressWarnings("unused")
    private boolean canSAFBeEnabledInBackground() {
        Log.d(NetworkTaskMainActivity.class.getName(), "canSAFBeEnabledInBackground");
        PreferenceManager preferenceManager = new PreferenceManager(this);
        boolean allowArbitraryFileLocationKeyPresent = preferenceManager.isPreferenceValueConfigured(getResources().getString(R.string.allow_arbitrary_file_location_key));
        boolean downloadExternalStorageEnabled = preferenceManager.getPreferenceDownloadExternalStorage();
        boolean logFileEnabled = preferenceManager.getPreferenceLogFile();
        Log.d(NetworkTaskMainActivity.class.getName(), "allowArbitraryFileLocationKeyPresent is " + allowArbitraryFileLocationKeyPresent);
        Log.d(NetworkTaskMainActivity.class.getName(), "downloadExternalStorageEnabled is " + downloadExternalStorageEnabled);
        Log.d(NetworkTaskMainActivity.class.getName(), "logFileEnabled is " + logFileEnabled);
        return !allowArbitraryFileLocationKeyPresent && !downloadExternalStorageEnabled && !logFileEnabled;
    }

    private void checkSAFPermissions() {
        PreferenceManager preferenceManager = new PreferenceManager(this);
        IPermissionManager permissionManager = getPermissionManager();
        if (SystemUtil.supportsSAFFeature() && preferenceManager.getPreferenceAllowArbitraryFileLocation() && permissionManager.hasPostNotificationsPermission(this) && !folderPermissionNotificationSent) {
            if (preferenceManager.getPreferenceLogFile()) {
                if (!checkArbitraryLogFolderPermission(preferenceManager)) {
                    Log.d(NetworkTaskMainActivity.class.getName(), "Log folder permission is missing");
                    createNotificationHandler().sendMessageNotificationMissingLogFolderPermission();
                }
            }
            if (preferenceManager.getPreferenceDownloadExternalStorage()) {
                if (!checkArbitraryDownloadFolderPermission(preferenceManager)) {
                    Log.d(NetworkTaskMainActivity.class.getName(), "Download folder permission is missing");
                    createNotificationHandler().sendMessageNotificationMissingDownloadFolderPermission();
                }
            }
            folderPermissionNotificationSent = true;
        }
    }

    private boolean checkArbitraryLogFolderPermission(PreferenceManager preferenceManager) {
        Log.d(NetworkTaskMainActivity.class.getName(), "checkArbitraryLogFolderPermission");
        return createStoragePermissionManager().hasPersistentPermission(this, preferenceManager.getPreferenceArbitraryLogFolder());
    }

    private boolean checkArbitraryDownloadFolderPermission(PreferenceManager preferenceManager) {
        Log.d(NetworkTaskMainActivity.class.getName(), "checkArbitraryDownloadFolderPermission");
        return createStoragePermissionManager().hasPersistentPermission(this, preferenceManager.getPreferenceArbitraryDownloadFolder());
    }

    private void showAlarmPermissionDialog() {
        Log.d(NetworkTaskMainActivity.class.getName(), "showAlarmPermissionDialog");
        FragmentManager fragmentManager = getSupportFragmentManager();
        String tag = AlarmPermissionDialog.class.getName();
        if (fragmentManager.findFragmentByTag(tag) == null) {
            AlarmPermissionDialog alarmPermissionDialog = new AlarmPermissionDialog();
            alarmPermissionDialog.show(getSupportFragmentManager(), AlarmPermissionDialog.class.getName());
        }
    }

    private List<NetworkTaskUIWrapper> readNetworkTasksFromDatabase() {
        Log.d(NetworkTaskMainActivity.class.getName(), "readNetworkTasksFromDatabase");
        try {
            NetworkTaskMainUIInitTask uiInitTask = getUIInitTask(null);
            Future<List<NetworkTaskUIWrapper>> wrapperListFuture = ThreadUtil.execute(uiInitTask);
            List<NetworkTaskUIWrapper> wrapperList = wrapperListFuture.get(getResources().getInteger(R.integer.database_access_timeout), TimeUnit.SECONDS);
            if (wrapperList == null) {
                Log.e(NetworkTaskMainActivity.class.getName(), "Reading all network tasks from database returned null");
                showMessageDialog(getResources().getString(R.string.text_dialog_general_message_read_network_tasks));
                return new ArrayList<>();
            }
            return wrapperList;
        } catch (Exception exc) {
            Log.e(NetworkTaskMainActivity.class.getName(), "Error reading all network tasks from database", exc);
            showMessageDialog(getResources().getString(R.string.text_dialog_general_message_read_network_tasks));
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
            Bundle bundle = prepareSystemActivityCredentialInfoBundle();
            if (bundle != null) {
                intent.putExtra(SystemActivity.getCredentialsKey(), bundle);
            }
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

    private Bundle prepareSystemActivityCredentialInfoBundle() {
        Log.d(NetworkTaskMainActivity.class.getName(), "prepareSystemActivityCredentialInfoBundle");
        NetworkTaskAdapter adapter = (NetworkTaskAdapter) getAdapter();
        List<NetworkTask> invalidSNMPCommunities = adapter.getInvalidSNMPCommunities();
        List<NetworkTask> validSNMPCommunities = adapter.getValidSNMPCommunities();
        Map<Long, List<Header>> invalidHeaders = adapter.getInvalidHeaders();
        Map<Long, List<Header>> secretHeaders = adapter.getSecretHeaders();
        HeaderSyncHandler syncHandler = new HeaderSyncHandler(this);
        List<Header> defaultHeaders = syncHandler.getGlobalHeaders();
        List<Header> invalidDefaultHeaders = syncHandler.getInvalidHeaders(defaultHeaders);
        List<Header> secretDefaultHeaders = syncHandler.getSecretHeaders(defaultHeaders);
        List<CredentialInfo> toDisplayInvalid = collectCredentialsActionLists(adapter, invalidSNMPCommunities, invalidHeaders, invalidDefaultHeaders, null);
        List<CredentialInfo> toDisplaySecret = collectCredentialsActionLists(adapter, validSNMPCommunities, secretHeaders, secretDefaultHeaders, null);
        if (!toDisplayInvalid.isEmpty() || !toDisplaySecret.isEmpty()) {
            Bundle bundle = BundleUtil.credentialInfoListToBundle(SystemActivity.getInvalidCredentialsBaseKey(), toDisplayInvalid);
            BundleUtil.credentialInfoListToBundle(SystemActivity.getCredentialsBaseKey(), toDisplaySecret, bundle);
            return bundle;
        }
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(NetworkTaskMainActivity.class.getName(), "onActivityResult");
    }

    public void onMainTitleClicked(int position) {
        Log.d(NetworkTaskMainActivity.class.getName(), "onMainTitleClicked, position is " + position);
        if (position < 0) {
            Log.e(NetworkTaskMainActivity.class.getName(), "position " + position + " is invalid");
            return;
        }
        NetworkTaskUIWrapper uiWrapper = ((NetworkTaskAdapter) getAdapter()).getItem(position);
        if (uiWrapper == null) {
            Log.e(NetworkTaskMainActivity.class.getName(), "No item on position " + position);
            return;
        }
        NetworkTask networkTask = uiWrapper.getNetworkTask();
        String name = StringUtil.isEmpty(networkTask.getName()) ? getResources().getString(R.string.task_title_normal) : networkTask.getName();
        String title = getResources().getString(R.string.label_dialog_settings_change_task_name);
        List<String> validators = Collections.singletonList(NetworkTaskNameFieldValidator.class.getName());
        SettingsInput input = new SettingsInput(SettingsInput.Type.TASKNAME, title, name, getResources().getString(R.string.task_name_field_name), position, validators);
        showInputDialog(input.toBundle());
    }

    public void onMainAddClicked(View view) {
        NetworkTask task = new NetworkTask(this);
        AccessTypeData data = new AccessTypeData(this);
        showSNMPItemDialogTest();
        //openNetworkTaskEditDialog(task, data, null, null, -1);
    }

    public void onMainStartStopClicked(int position) {
        Log.d(NetworkTaskMainActivity.class.getName(), "onMainStartStopClicked, position is " + position);
        if (position < 0) {
            Log.e(NetworkTaskMainActivity.class.getName(), "position " + position + " is invalid");
            return;
        }
        NetworkTaskUIWrapper uiWrapper = ((NetworkTaskAdapter) getAdapter()).getItem(position);
        if (uiWrapper == null) {
            Log.e(NetworkTaskMainActivity.class.getName(), "No item on position " + position);
            return;
        }
        NetworkTask networkTask = uiWrapper.getNetworkTask();
        AccessTypeData accessTypeData = uiWrapper.getAccessTypeData();
        Log.d(NetworkTaskMainActivity.class.getName(), "onMainStartStopClicked for network task " + networkTask + " and access type data " + accessTypeData);
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
        if (position < 0) {
            Log.e(NetworkTaskMainActivity.class.getName(), "position " + position + " is invalid");
            return;
        }
        showConfirmDialog(getResources().getString(R.string.text_dialog_confirm_delete_network_task), ConfirmDialog.Type.DELETETASK, position);
    }

    public void onMainDeleteSwiped(int position) {
        Log.d(NetworkTaskMainActivity.class.getName(), "onMainDeleteSwiped for position " + position + ", opening " + ConfirmDialog.class.getSimpleName() + " for type " + ConfirmDialog.Type.DELETETASKSWIPE);
        if (position < 0) {
            Log.e(NetworkTaskMainActivity.class.getName(), "position " + position + " is invalid");
            return;
        }
        showConfirmDialog(getResources().getString(R.string.text_dialog_confirm_delete_network_task), ConfirmDialog.Type.DELETETASKSWIPE, position);
    }

    public void onMainEditClicked(int position) {
        Log.d(NetworkTaskMainActivity.class.getName(), "onMainEditClicked, position is " + position);
        if (position < 0) {
            Log.e(NetworkTaskMainActivity.class.getName(), "position " + position + " is invalid");
            return;
        }
        NetworkTaskUIWrapper uiWrapper = ((NetworkTaskAdapter) getAdapter()).getItem(position);
        if (uiWrapper == null) {
            Log.e(NetworkTaskMainActivity.class.getName(), "No item on position " + position);
            return;
        }
        NetworkTask task = uiWrapper.getNetworkTask();
        AccessTypeData accessTypeData = uiWrapper.getAccessTypeData();
        List<Resolve> resolves = uiWrapper.getResolves();
        List<Header> headers = uiWrapper.getHeaders();
        openNetworkTaskEditDialog(task, accessTypeData, resolves, headers, position);
    }

    public void onMainCopyClicked(int position) {
        Log.d(NetworkTaskMainActivity.class.getName(), "onMainCopyClicked, position is " + position);
        if (position < 0) {
            Log.e(NetworkTaskMainActivity.class.getName(), "position " + position + " is invalid");
            return;
        }
        NetworkTaskUIWrapper uiWrapper = ((NetworkTaskAdapter) getAdapter()).getItem(position);
        if (uiWrapper == null) {
            Log.e(NetworkTaskMainActivity.class.getName(), "No item on position " + position);
            return;
        }
        NetworkTask task = new NetworkTask(uiWrapper.getNetworkTask());
        AccessTypeData accessTypeData = new AccessTypeData(uiWrapper.getAccessTypeData());
        List<Resolve> resolves = uiWrapper.getResolves() == null ? null : new ArrayList<>(uiWrapper.getResolves());
        List<Header> headers = uiWrapper.getHeaders() == null ? null : new ArrayList<>(uiWrapper.getHeaders());
        openNetworkTaskEditDialog(task, accessTypeData, resolves, headers, position);
    }

    private void openNetworkTaskEditDialog(NetworkTask task, AccessTypeData accessTypeData, List<Resolve> resolves, List<Header> headers, int position) {
        Log.d(NetworkTaskMainActivity.class.getName(), "openNetworkTaskEditDialog, task is " + task + ", accessTypeData is, position is " + position);
        NetworkTaskEditDialog editDialog = new NetworkTaskEditDialog();
        if (permissionManager != null) {
            editDialog.injectPermissionManager(permissionManager);
        }
        Bundle bundle = BundleUtil.integerToBundle(editDialog.getPositionKey(), position);
        bundle = BundleUtil.bundleToBundle(editDialog.getTaskKey(), task.toBundle(), bundle);
        bundle = BundleUtil.bundleToBundle(editDialog.getAccessTypeDataKey(), accessTypeData.toBundle(), bundle);
        if (resolves != null) {
            Bundle resolvesBundle = BundleUtil.resolveListToBundle(editDialog.getResolvesBaseKey(), resolves);
            bundle = BundleUtil.bundleToBundle(editDialog.getResolvesKey(), resolvesBundle, bundle);
        }
        if (headers != null) {
            Bundle headersBundle = BundleUtil.headerListToBundle(editDialog.getHeadersBaseKey(), headers);
            bundle = BundleUtil.bundleToBundle(editDialog.getHeadersKey(), headersBundle, bundle);
        }
        editDialog.setArguments(bundle);
        Log.d(NetworkTaskMainActivity.class.getName(), "Opening " + NetworkTaskEditDialog.class.getSimpleName());
        editDialog.show(getSupportFragmentManager(), NetworkTaskEditDialog.class.getName());
    }

    public void onMainLogClicked(int position) {
        Log.d(NetworkTaskMainActivity.class.getName(), "onMainLogClicked, position is " + position);
        if (position < 0) {
            Log.e(NetworkTaskMainActivity.class.getName(), "position " + position + " is invalid");
            return;
        }
        NetworkTaskUIWrapper uiWrapper = ((NetworkTaskAdapter) getAdapter()).getItem(position);
        if (uiWrapper == null) {
            Log.e(NetworkTaskMainActivity.class.getName(), "No item on position " + position);
            return;
        }
        NetworkTask networkTask = uiWrapper.getNetworkTask();
        Log.d(NetworkTaskMainActivity.class.getName(), "onMainLogClicked for network task " + networkTask);
        Intent intent = new Intent(this, NetworkTaskLogActivity.class);
        intent.putExtras(networkTask.toBundle());
        intent.setPackage(getPackageName());
        startActivity(intent);
    }

    public void onEditDialogOkClicked(NetworkTaskEditDialog editDialog) {
        NetworkTask task = editDialog.getNetworkTask();
        AccessTypeData accessTypeData = editDialog.getAccessTypeData();
        List<Resolve> resolves = editDialog.getResolves();
        setResolvesNetworkTaskId(resolves, task.getId());
        List<Header> headers = editDialog.getHeaders();
        setHeadersNetworkTaskId(headers, task.getId());
        Log.d(NetworkTaskMainActivity.class.getName(), "onEditDialogOkClicked, network task is " + task + ", access type data is " + accessTypeData + ", resolves are " + resolves + ", headers are " + headers);
        NetworkTaskHandler handler = new NetworkTaskHandler(this);
        if (task.getId() < 0) {
            Log.d(NetworkTaskMainActivity.class.getName(), "Network task is new, inserting " + task);
            handler.insertNetworkTask(task, accessTypeData, resolves, headers);
            getAdapter().notifyItemInserted(getAdapter().getItemCount() + 1);
            scrollToEntryByIndex(task.getIndex());
        } else {
            NetworkTask initialTask = editDialog.getInitialNetworkTask();
            AccessTypeData initialAccessTypeData = editDialog.getInitialAccessTypeData();
            initialAccessTypeData.setNetworkTaskId(initialTask.getId());
            List<Resolve> initialResolves = editDialog.getInitialResolves();
            setResolvesNetworkTaskId(initialResolves, task.getId());
            List<Header> initialHeaders = editDialog.getInitialHeaders();
            setHeadersNetworkTaskId(initialHeaders, task.getId());
            Log.d(NetworkTaskMainActivity.class.getName(), "Initial network task is " + initialTask);
            Log.d(NetworkTaskMainActivity.class.getName(), "Initial access type data is " + initialAccessTypeData);
            Log.d(NetworkTaskMainActivity.class.getName(), "Initial resolve objects are " + initialResolves);
            Log.d(NetworkTaskMainActivity.class.getName(), "Initial headers are " + initialHeaders);
            boolean taskChanged = !initialTask.isTechnicallyEqual(task);
            boolean accessTypeDataChanged = !initialAccessTypeData.isTechnicallyEqual(accessTypeData);
            boolean resolvesChanged = resolves != null && !CollectionUtil.areListsEqual(initialResolves, resolves, RESOLVE_TECHNICAL_EQUALITY);
            boolean headersChanged = headers != null && !CollectionUtil.areListsEqual(initialHeaders, headers, HEADER_TECHNICAL_EQUALITY);
            Log.d(NetworkTaskMainActivity.class.getName(), "Initial network task changed: " + taskChanged);
            Log.d(NetworkTaskMainActivity.class.getName(), "Initial access type data changed: " + accessTypeDataChanged);
            Log.d(NetworkTaskMainActivity.class.getName(), "Initial resolve objects changed: " + resolvesChanged);
            Log.d(NetworkTaskMainActivity.class.getName(), "Initial headers changed: " + headersChanged);
            if (!taskChanged && !accessTypeDataChanged && !resolvesChanged && !headersChanged) {
                Log.d(NetworkTaskMainActivity.class.getName(), "No changes were made. Skipping update.");
            } else {
                Log.d(NetworkTaskMainActivity.class.getName(), "Updating " + task);
                handler.updateNetworkTask(task, accessTypeDataChanged ? accessTypeData : null, resolvesChanged ? resolves : null, headersChanged ? headers : null);
                getAdapter().notifyItemChanged(editDialog.getPosition());
            }
        }
        editDialog.dismiss();
    }

    private void setHeadersNetworkTaskId(List<Header> headers, long taskId) {
        if (headers != null) {
            for (Header header : headers) {
                header.setNetworkTaskId(taskId);
            }
        }
    }

    private void setResolvesNetworkTaskId(List<Resolve> resolves, long taskId) {
        if (resolves != null) {
            for (Resolve resolve : resolves) {
                resolve.setNetworkTaskId(taskId);
            }
        }
    }

    public void onEditDialogCancelClicked(NetworkTaskEditDialog editDialog) {
        Log.d(NetworkTaskMainActivity.class.getName(), "onEditDialogCancelClicked");
        editDialog.dismiss();
    }

    public void onConfirmDialogOkClicked(ConfirmDialog confirmDialog, ConfirmDialog.Type type) {
        Log.d(NetworkTaskMainActivity.class.getName(), "onConfirmDialogOkClicked for type " + type);
        if (ConfirmDialog.Type.DELETETASK.equals(type) || ConfirmDialog.Type.DELETETASKSWIPE.equals(type)) {
            int position = confirmDialog.getPosition();
            if (position >= 0) {
                NetworkTaskHandler handler = new NetworkTaskHandler(this);
                NetworkTask task = ((NetworkTaskAdapter) getAdapter()).getItem(position).getNetworkTask();
                Log.d(NetworkTaskMainActivity.class.getName(), "Deleting " + task);
                handler.deleteNetworkTask(task);
                getAdapter().notifyItemRemoved(position);
                RecyclerView recyclerView = findViewById(getRecyclerViewId());
                recyclerView.post(() -> {
                    for (int ii = position; ii < getAdapter().getItemCount(); ii++) {
                        getAdapter().notifyItemChanged(ii);
                    }
                });
                if (ConfirmDialog.Type.DELETETASKSWIPE.equals(type)) {
                    reattachItemTouchHelper();
                }
            } else {
                Log.e(NetworkTaskMainActivity.class.getName(), ConfirmDialog.class.getSimpleName() + " arguments do not contain position key " + confirmDialog.getPositionKey());
            }
        } else if (ConfirmDialog.Type.DISMISSALARM.equals(type)) {
            Log.d(NetworkTaskMainActivity.class.getName(), "Stopping alarm service...");
            stopService(new Intent(this, AlarmService.class));
        } else {
            Log.e(NetworkTaskMainActivity.class.getName(), "Unknown type " + type);
        }
        confirmDialog.dismiss();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(NetworkTaskMainActivity.class.getName(), "onRequestPermissionsResult for code " + requestCode);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        IPermissionManager permissionManager = getPermissionManager();
        permissionManager.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
        if (permissionManager.hasPostNotificationsPermission(this)) {
            checkSAFPermissions();
        }
    }


    public void onConfirmDialogCancelClicked(ConfirmDialog confirmDialog, ConfirmDialog.Type type) {
        Log.d(NetworkTaskMainActivity.class.getName(), "onConfirmDialogCancelClicked for type " + type);
        if (ConfirmDialog.Type.DELETETASKSWIPE.equals(type)) {
            int position = confirmDialog.getPosition();
            getAdapter().notifyItemChanged(position);
            reattachItemTouchHelper();
        }
        confirmDialog.dismiss();
    }

    private void reattachItemTouchHelper() {
        Log.d(NetworkTaskMainActivity.class.getName(), "reattachItemTouchHelper");
        RecyclerView recyclerView = findViewById(getRecyclerViewId());
        if (itemTouchHelper != null) {
            itemTouchHelper.attachToRecyclerView(null);
        }
        recyclerView.post(() -> {
            itemTouchHelper = new ItemTouchHelper(new NetworkTaskDragAndDropCallback(this));
            itemTouchHelper.attachToRecyclerView(recyclerView);
        });
    }

    private void showInputDialog(Bundle bundle) {
        Log.d(NetworkTaskMainActivity.class.getName(), "showInputDialog, opening SettingsInputDialog");
        SettingsInputDialog inputDialog = new SettingsInputDialog();
        inputDialog.setArguments(bundle);
        inputDialog.show(getSupportFragmentManager(), NetworkTaskMainActivity.class.getName());
    }

    @Override
    public void onInputDialogOkClicked(SettingsInputDialog inputDialog, SettingsInput type) {
        Log.d(NetworkTaskMainActivity.class.getName(), "onInputDialogOkClicked, type is " + type + ", value is " + inputDialog.getValue());
        if (SettingsInput.Type.TASKNAME.equals(type.getType())) {
            NetworkTaskUIWrapper uiWrapper = ((NetworkTaskAdapter) getAdapter()).getItem(type.getPosition());
            if (uiWrapper == null) {
                Log.e(NetworkTaskMainActivity.class.getName(), "No item on position " + type.getPosition());
                return;
            }
            NetworkTask networkTask = uiWrapper.getNetworkTask();
            AccessTypeData accessTypeData = uiWrapper.getAccessTypeData();
            Log.d(NetworkTaskMainActivity.class.getName(), "onInputDialogOkClicked for network task " + networkTask + " and access type data " + accessTypeData);
            NetworkTaskHandler handler = new NetworkTaskHandler(this);
            String name = StringUtil.isEmpty(inputDialog.getValue()) ? getResources().getString(R.string.task_title_normal) : inputDialog.getValue();
            Log.d(NetworkTaskMainActivity.class.getName(), "new name is " + name);
            handler.updateNetworkTaskName(networkTask, name);
            getAdapter().notifyItemChanged(type.getPosition());
        } else {
            Log.e(NetworkTaskMainActivity.class.getName(), "type " + type + " unknown");
        }
        inputDialog.dismiss();
    }

    @Override
    public void onInputDialogCancelClicked(SettingsInputDialog inputDialog) {
        Log.d(NetworkTaskMainActivity.class.getName(), "onInputDialogCancelClicked");
        inputDialog.dismiss();
    }

    private void showCredentialInfoDialog(List<CredentialInfo> credentialInfo) {
        Log.d(NetworkTaskMainActivity.class.getName(), "showCredentialInfoDialog");
        CredentialInfoDialog infoDialog = new CredentialInfoDialog();
        Bundle bundle = BundleUtil.credentialInfoListToBundle(infoDialog.getCredentialInfoBaseKey(), credentialInfo);
        String title = getResources().getString(R.string.text_dialog_credential_info_title);
        BundleUtil.stringToBundle(infoDialog.getTitleKey(), title, bundle);
        String message = getResources().getString(R.string.text_dialog_credential_info_message);
        BundleUtil.stringToBundle(infoDialog.getMessageKey(), message, bundle);
        infoDialog.setArguments(bundle);
        infoDialog.show(getSupportFragmentManager(), CredentialInfoDialog.class.getName());
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

    @Override
    public void onMessageDialogOkClicked(GeneralMessageDialog messageDialog) {
        Log.d(NetworkTaskMainActivity.class.getName(), "onMessageDialogOkClicked");
        String extraData = messageDialog.getExtraData();
        Log.d(NetworkTaskMainActivity.class.getName(), "onMessageDialogOkClicked, extraData is " + extraData);
        messageDialog.dismiss();
        if (Message.SAFNOTICE.name().equals(extraData)) {
            PreferenceManager preferenceManager = new PreferenceManager(this);
            preferenceManager.setPreferenceSAFNoticeShown(messageDialog.isDoNotShowAgain());
        }
    }

    public static String getNetworkTaskKey() {
        return NetworkTaskMainActivity.class + ".NetworkTaskKey";
    }

    private NetworkTaskProcessServiceScheduler getNetworkTaskProcessServiceScheduler() {
        return new NetworkTaskProcessServiceScheduler(this);
    }

    private NetworkTaskMainUIInitTask getUIInitTask(NetworkTaskAdapter adapter) {
        return new NetworkTaskMainUIInitTask(this, adapter);
    }

    private IAlarmManager createAlarmManager() {
        return new SystemAlarmManager(this);
    }

    private NotificationHandler createNotificationHandler() {
        return new NotificationHandler(this, getPermissionManager());
    }

    private IStoragePermissionManager createStoragePermissionManager() {
        return new StoragePermissionManager();
    }

    @SuppressWarnings("unused")
    private void showSNMPItemDialogTest() {
        Log.d(NetworkTaskMainActivity.class.getName(), "showSNMPItemDialogTest");
        SNMPItemDialog dialog = new SNMPItemDialog();
        Bundle bundle = new Bundle();
        bundle.putString(dialog.getAddressKey(), "gaia.ibbaa.lan");
        bundle.putInt(dialog.getPortKey(), 161);
        bundle.putString(dialog.getSNMPVersionKey(), SNMPVersion.V2C.name());
        bundle.putString(dialog.getCommunityKey(), "gaia");
        dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager(), SNMPItemDialog.class.getName());
    }
}
