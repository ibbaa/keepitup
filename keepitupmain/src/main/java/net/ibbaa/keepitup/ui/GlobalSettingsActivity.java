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

package net.ibbaa.keepitup.ui;

import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.GridLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.google.android.material.switchmaterial.SwitchMaterial;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.logging.NetworkTaskLog;
import net.ibbaa.keepitup.model.Interval;
import net.ibbaa.keepitup.model.NotificationType;
import net.ibbaa.keepitup.resources.PreferenceManager;
import net.ibbaa.keepitup.resources.PreferenceSetup;
import net.ibbaa.keepitup.service.IFileManager;
import net.ibbaa.keepitup.ui.dialog.FileChooseDialog;
import net.ibbaa.keepitup.ui.dialog.SettingsInput;
import net.ibbaa.keepitup.ui.dialog.SettingsInputDialog;
import net.ibbaa.keepitup.ui.dialog.SuspensionIntervalsDialog;
import net.ibbaa.keepitup.ui.permission.GenericPermissionLauncher;
import net.ibbaa.keepitup.ui.permission.IStoragePermissionManager;
import net.ibbaa.keepitup.ui.permission.NullPermissionLauncher;
import net.ibbaa.keepitup.ui.permission.PermissionLauncher;
import net.ibbaa.keepitup.ui.validation.NotificationAfterFailuresFieldValidator;
import net.ibbaa.keepitup.util.BundleUtil;
import net.ibbaa.keepitup.util.FileUtil;
import net.ibbaa.keepitup.util.NumberUtil;
import net.ibbaa.keepitup.util.StringUtil;
import net.ibbaa.keepitup.util.SystemUtil;
import net.ibbaa.keepitup.util.TimeUtil;

import java.io.File;
import java.util.Collections;
import java.util.List;

@SuppressWarnings({"unused", "FieldCanBeLocal"})
public class GlobalSettingsActivity extends SettingsInputActivity implements SuspensionIntervalsSupport {

    private SwitchMaterial notificationInactiveNetworkSwitch;
    private TextView notificationInactiveNetworkOnOffText;
    private RadioGroup notificationType;
    private TextView notificationAfterFailuresText;
    private SwitchMaterial suspensionEnabledSwitch;
    private TextView suspensionEnabledOnOffText;
    private SwitchMaterial enforcePingPackageSizeEnabledSwitch;
    private TextView enforcePingPackageSizeEnabledOnOffText;
    private SwitchMaterial downloadExternalStorageSwitch;
    private TextView downloadExternalStorageOnOffText;
    private TextView downloadFolderText;
    private SwitchMaterial downloadKeepSwitch;
    private TextView downloadKeepOnOffText;
    private SwitchMaterial logFileSwitch;
    private TextView logFileOnOffText;
    private TextView logFolderText;
    private PermissionLauncher logFolderLauncher;
    private PermissionLauncher downloadFolderLauncher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setContentView(R.layout.activity_global_settings);
        prepareNotificationInactiveNetworkSwitch();
        prepareNotificationTypeRadioGroup();
        prepareNotificationAfterFailuresField();
        prepareSuspensionEnabledSwitch();
        prepareSuspensionIntervalsField();
        prepareEnforcePingPackageSizeEnabledSwitch();
        prepareDownloadFolderLauncher();
        prepareDownloadExternalStorageSwitch();
        prepareDownloadFolderField();
        prepareDownloadKeepSwitch();
        prepareLogFolderLauncher();
        prepareLogFileSwitch();
        prepareLogFolderField();
    }

    public void injectLogFolderLauncher(PermissionLauncher logFolderLauncher) {
        this.logFolderLauncher = logFolderLauncher;
    }

    public void injectDownloadFolderLauncher(PermissionLauncher downloadFolderLauncher) {
        this.downloadFolderLauncher = downloadFolderLauncher;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_global_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_action_activity_global_settings_reset) {
            Log.d(GlobalSettingsActivity.class.getName(), "menu_action_activity_global_settings_reset triggered");
            PreferenceSetup preferenceSetup = new PreferenceSetup(this);
            preferenceSetup.removeGlobalSettings();
            recreateActivity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void prepareNotificationInactiveNetworkSwitch() {
        Log.d(GlobalSettingsActivity.class.getName(), "prepareNotificationInactiveNetworkSwitch");
        PreferenceManager preferenceManager = new PreferenceManager(this);
        notificationInactiveNetworkSwitch = findViewById(R.id.switch_activity_global_settings_notification_inactive_network);
        notificationInactiveNetworkOnOffText = findViewById(R.id.textview_activity_global_settings_notification_inactive_network_on_off);
        notificationInactiveNetworkSwitch.setOnCheckedChangeListener(null);
        notificationInactiveNetworkSwitch.setChecked(preferenceManager.getPreferenceNotificationInactiveNetwork());
        notificationInactiveNetworkSwitch.setOnCheckedChangeListener(this::onNotificationInactiveNetworkCheckedChanged);
        prepareNotificationInactiveNetworkOnOffText();
    }

    private void prepareNotificationInactiveNetworkOnOffText() {
        notificationInactiveNetworkOnOffText.setText(notificationInactiveNetworkSwitch.isChecked() ? getResources().getString(R.string.string_yes) : getResources().getString(R.string.string_no));
    }

    private void onNotificationInactiveNetworkCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.d(GlobalSettingsActivity.class.getName(), "onNotificationInactiveNetworkCheckedChanged, new value is " + isChecked);
        PreferenceManager preferenceManager = new PreferenceManager(this);
        preferenceManager.setPreferenceNotificationInactiveNetwork(isChecked);
        prepareNotificationInactiveNetworkOnOffText();
    }

    private void prepareNotificationTypeRadioGroup() {
        Log.d(GlobalSettingsActivity.class.getName(), "prepareNotificationTypeRadioGroup");
        notificationType = findViewById(R.id.radiogroup_activity_global_settings_notification_type);
        PreferenceManager preferenceManager = new PreferenceManager(this);
        NotificationType type = preferenceManager.getPreferenceNotificationType();
        Log.d(GlobalSettingsActivity.class.getName(), "notification type is " + type);
        RadioButton typeFailureButton = findViewById(R.id.radiobutton_activity_global_settings_notification_type_failure);
        RadioButton typeChangeButton = findViewById(R.id.radiobutton_activity_global_settings_notification_type_change);
        notificationType.setOnCheckedChangeListener(null);
        if (type == NotificationType.FAILURE) {
            typeFailureButton.setChecked(true);
            typeChangeButton.setChecked(false);
        } else if (type == NotificationType.CHANGE) {
            typeFailureButton.setChecked(false);
            typeChangeButton.setChecked(true);
        } else {
            typeFailureButton.setChecked(true);
            typeChangeButton.setChecked(false);
        }
        notificationType.setOnCheckedChangeListener(this::onNotificationTypeChanged);
    }

    private void onNotificationTypeChanged(RadioGroup group, int checkedId) {
        Log.d(GlobalSettingsActivity.class.getName(), "onNotificationTypeChanged");
        PreferenceManager preferenceManager = new PreferenceManager(this);
        int checkedType = group.getCheckedRadioButtonId();
        if (R.id.radiobutton_activity_global_settings_notification_type_failure == checkedType) {
            Log.d(GlobalSettingsActivity.class.getName(), "Notification type FAILURE selected");
            preferenceManager.setPreferenceNotificationType(NotificationType.FAILURE);
        } else if (R.id.radiobutton_activity_global_settings_notification_type_change == checkedType) {
            Log.d(GlobalSettingsActivity.class.getName(), "Notification type CHANGE selected");
            preferenceManager.setPreferenceNotificationType(NotificationType.CHANGE);
        } else {
            Log.d(GlobalSettingsActivity.class.getName(), "Unknown notification type selected");
            preferenceManager.setPreferenceNotificationType(NotificationType.FAILURE);
        }
    }

    private void prepareNotificationAfterFailuresField() {
        Log.d(GlobalSettingsActivity.class.getName(), "prepareNotificationAfterFailuresField");
        PreferenceManager preferenceManager = new PreferenceManager(this);
        notificationAfterFailuresText = findViewById(R.id.textview_activity_global_settings_notification_after_failures);
        int notificationAfterFailures = preferenceManager.getPreferenceNotificationAfterFailures();
        setNotificationAfterFailures(String.valueOf(preferenceManager.getPreferenceNotificationAfterFailures()));
        CardView notificationsAfterFailuresCardView = findViewById(R.id.cardview_activity_global_settings_notification_after_failures);
        notificationsAfterFailuresCardView.setOnClickListener(this::showNotificationAfterFailuresInputDialog);
    }

    private void prepareSuspensionEnabledSwitch() {
        Log.d(GlobalSettingsActivity.class.getName(), "prepareSuspensionEnabledSwitch");
        PreferenceManager preferenceManager = new PreferenceManager(this);
        suspensionEnabledSwitch = findViewById(R.id.switch_activity_global_settings_suspension_enabled);
        suspensionEnabledOnOffText = findViewById(R.id.textview_activity_global_settings_suspension_enabled_on_off);
        suspensionEnabledSwitch.setOnCheckedChangeListener(null);
        suspensionEnabledSwitch.setChecked(preferenceManager.getPreferenceSuspensionEnabled());
        suspensionEnabledSwitch.setOnCheckedChangeListener(this::onSuspensionEnabledCheckedChanged);
        prepareSuspensionEnabledOnOffText();
    }

    private void prepareSuspensionEnabledOnOffText() {
        suspensionEnabledOnOffText.setText(suspensionEnabledSwitch.isChecked() ? getResources().getString(R.string.string_yes) : getResources().getString(R.string.string_no));
    }

    private void onSuspensionEnabledCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.d(GlobalSettingsActivity.class.getName(), "onSuspensionEnabledCheckedChanged, new value is " + isChecked);
        PreferenceManager preferenceManager = new PreferenceManager(this);
        preferenceManager.setPreferenceSuspensionEnabled(isChecked);
        prepareSuspensionEnabledOnOffText();
        prepareSuspensionIntervalsField();
        if (!getTimeBasedSuspensionScheduler().getIntervals().isEmpty()) {
            getTimeBasedSuspensionScheduler().restart();
        }
    }

    private void prepareSuspensionIntervalsField() {
        Log.d(GlobalSettingsActivity.class.getName(), "prepareSuspensionIntervalsField");
        CardView suspensionIntervalsCardView = findViewById(R.id.cardview_activity_global_settings_suspension_intervals);
        if (suspensionEnabledSwitch.isChecked()) {
            suspensionIntervalsCardView.setEnabled(true);
            suspensionIntervalsCardView.setOnClickListener(this::showSuspensionIntervalsDialog);
            prepareSuspensionIntervalsTextLayoutFields(false);
        } else {
            prepareSuspensionIntervalsTextLayoutFields(true);
            suspensionIntervalsCardView.setEnabled(false);
            suspensionIntervalsCardView.setOnClickListener(null);
        }
    }

    private void prepareSuspensionIntervalsTextLayoutFields(boolean disabled) {
        Log.d(GlobalSettingsActivity.class.getName(), "prepareSuspensionIntervalsTextFields, disabled is " + disabled);
        GridLayout gridLayout = findViewById(R.id.gridlayout_activity_global_settings_suspension_intervals_value);
        gridLayout.removeAllViews();
        List<Interval> intervals = getTimeBasedSuspensionScheduler().getIntervals();
        if (disabled) {
            Log.d(GlobalSettingsActivity.class.getName(), "Suspension intervals are disabled");
            prepareSuspensionIntervalsTextFieldsSingleLayoutColumn(getResources().getString(R.string.text_activity_global_settings_suspension_intervals_disabled));
            return;
        }
        if (intervals.isEmpty()) {
            Log.d(GlobalSettingsActivity.class.getName(), "Suspension intervals are enabled but none are present");
            prepareSuspensionIntervalsTextFieldsSingleLayoutColumn(getResources().getString(R.string.text_activity_global_settings_suspension_intervals_none));
            return;
        }
        if (intervals.size() > 3) {
            Log.d(GlobalSettingsActivity.class.getName(), "More than 3 suspension intervals are present");
            gridLayout.setColumnCount(2);
            int index = (intervals.size() + 1) / 2;
            prepareSuspensionIntervalsTextFieldsLayoutColumn(0, intervals.size(), intervals.subList(0, index));
            prepareSuspensionIntervalsTextFieldsLayoutColumn(1, intervals.size(), intervals.subList(index, intervals.size()));
        } else {
            Log.d(GlobalSettingsActivity.class.getName(), "Less than 3 suspension intervals are present");
            gridLayout.setColumnCount(1);
            prepareSuspensionIntervalsTextFieldsLayoutColumn(0, intervals.size(), intervals);
        }

    }

    private void prepareSuspensionIntervalsTextFieldsSingleLayoutColumn(String text) {
        Log.d(GlobalSettingsActivity.class.getName(), "prepareSuspensionIntervalsTextFieldsSingleLayoutColumn with text " + text);
        GridLayout gridLayout = findViewById(R.id.gridlayout_activity_global_settings_suspension_intervals_value);
        gridLayout.setColumnCount(1);
        TextView intervalText = getSuspensionIntervalTextView(text, getSuspensionIntervalsTextSize(1));
        GridLayout.LayoutParams intervalTextParams = getSuspensionIntervalTextViewLayoutParams(0, 0);
        gridLayout.addView(intervalText, intervalTextParams);
    }

    private void prepareSuspensionIntervalsTextFieldsLayoutColumn(int column, int overallSize, List<Interval> intervals) {
        Log.d(GlobalSettingsActivity.class.getName(), "prepareSuspensionIntervalsTextFieldsLayoutColumn for column " + column);
        GridLayout gridLayout = findViewById(R.id.gridlayout_activity_global_settings_suspension_intervals_value);
        for (int ii = 0; ii < intervals.size(); ii++) {
            Interval interval = intervals.get(ii);
            TextView intervalText = getSuspensionIntervalTextView(TimeUtil.formatSuspensionIntervalText(interval, this), getSuspensionIntervalsTextSize(overallSize));
            GridLayout.LayoutParams intervalTextParams = getSuspensionIntervalTextViewLayoutParams(ii, column);
            gridLayout.addView(intervalText, intervalTextParams);
        }
    }

    private TextView getSuspensionIntervalTextView(String text, int textSize) {
        TextView intervalText = new TextView(this);
        intervalText.setId(View.generateViewId());
        intervalText.setText(text);
        intervalText.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        return intervalText;
    }

    private GridLayout.LayoutParams getSuspensionIntervalTextViewLayoutParams(int row, int column) {
        GridLayout.LayoutParams intervalTextParams = new GridLayout.LayoutParams();
        intervalTextParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
        intervalTextParams.width = GridLayout.LayoutParams.WRAP_CONTENT;
        intervalTextParams.setGravity(Gravity.CENTER);
        intervalTextParams.rightMargin = getResources().getDimensionPixelSize(R.dimen.textview_activity_global_settings_intervals_value_margin_right);
        intervalTextParams.topMargin = getResources().getDimensionPixelSize(R.dimen.textview_activity_global_settings_intervals_value_margin_top);
        intervalTextParams.columnSpec = GridLayout.spec(column, 1, GridLayout.LEFT);
        intervalTextParams.rowSpec = GridLayout.spec(row + 1, 1, GridLayout.LEFT);
        return intervalTextParams;
    }

    private int getSuspensionIntervalsTextSize(int intervalCount) {
        if (intervalCount <= 1) {
            return getResources().getInteger(R.integer.suspension_intervals_text_size_normal);
        } else if (intervalCount == 2) {
            return getResources().getInteger(R.integer.suspension_intervals_text_size_smaller);
        } else {
            return getResources().getInteger(R.integer.suspension_intervals_text_size_small);
        }
    }

    private void showSuspensionIntervalsDialog(View view) {
        Log.d(GlobalSettingsActivity.class.getName(), "showSuspensionIntervalsDialog");
        SuspensionIntervalsDialog intervalsDialog = new SuspensionIntervalsDialog();
        intervalsDialog.show(getSupportFragmentManager(), SuspensionIntervalsDialog.class.getName());
    }

    private void prepareEnforcePingPackageSizeEnabledSwitch() {
        Log.d(GlobalSettingsActivity.class.getName(), "prepareEnforcePingPackageSizeEnabledSwitch");
        PreferenceManager preferenceManager = new PreferenceManager(this);
        enforcePingPackageSizeEnabledSwitch = findViewById(R.id.switch_activity_global_settings_enforce_ping_package_size_enabled);
        enforcePingPackageSizeEnabledOnOffText = findViewById(R.id.textview_activity_global_settings_enforce_ping_package_size_enabled_on_off);
        enforcePingPackageSizeEnabledSwitch.setOnCheckedChangeListener(null);
        enforcePingPackageSizeEnabledSwitch.setChecked(preferenceManager.getPreferenceEnforceDefaultPingPackageSize());
        enforcePingPackageSizeEnabledSwitch.setOnCheckedChangeListener(this::onEnforcePingPackageSizeEnabledCheckedChanged);
        prepareEnforcePingPackageSizeEnabledOnOffText();
    }

    private void prepareEnforcePingPackageSizeEnabledOnOffText() {
        enforcePingPackageSizeEnabledOnOffText.setText(enforcePingPackageSizeEnabledSwitch.isChecked() ? getResources().getString(R.string.string_yes) : getResources().getString(R.string.string_no));
    }

    private void onEnforcePingPackageSizeEnabledCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.d(GlobalSettingsActivity.class.getName(), "onEnforcePingPackageSizeEnabledCheckedChanged, new value is " + isChecked);
        PreferenceManager preferenceManager = new PreferenceManager(this);
        preferenceManager.setPreferenceEnforceDefaultPingPackageSize(isChecked);
        prepareEnforcePingPackageSizeEnabledOnOffText();
    }

    private void prepareDownloadExternalStorageOnOffText() {
        downloadExternalStorageOnOffText.setText(downloadExternalStorageSwitch.isChecked() ? getResources().getString(R.string.string_yes) : getResources().getString(R.string.string_no));
    }

    private void prepareDownloadExternalStorageSwitch() {
        Log.d(GlobalSettingsActivity.class.getName(), "prepareDownloadExternalStorageSwitch");
        PreferenceManager preferenceManager = new PreferenceManager(this);
        downloadExternalStorageSwitch = findViewById(R.id.switch_activity_global_settings_download_external_storage);
        downloadExternalStorageOnOffText = findViewById(R.id.textview_activity_global_settings_download_external_storage_on_off);
        downloadExternalStorageSwitch.setOnCheckedChangeListener(null);
        downloadExternalStorageSwitch.setChecked(preferenceManager.getPreferenceDownloadExternalStorage());
        downloadExternalStorageSwitch.setOnCheckedChangeListener(this::onDownloadExternalStorageCheckedChanged);
        prepareDownloadExternalStorageOnOffText();
    }

    private void onDownloadExternalStorageCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.d(GlobalSettingsActivity.class.getName(), "onDownloadExternalStorageCheckedChanged, new value is " + isChecked);
        PreferenceManager preferenceManager = new PreferenceManager(this);
        preferenceManager.setPreferenceDownloadExternalStorage(isChecked);
        prepareDownloadExternalStorageOnOffText();
        prepareDownloadFolderField();
        prepareDownloadKeepSwitch();
    }

    private void prepareDownloadFolderLauncher() {
        Log.d(GlobalSettingsActivity.class.getName(), "prepareDownloadFolderLauncher");
        boolean bypassSystemSAF = BundleUtil.booleanFromBundle(getBypassSystemSAFKey(), getIntent().getExtras());
        if (SystemUtil.supportsSAFFeature() && !bypassSystemSAF) {
            downloadFolderLauncher = new GenericPermissionLauncher(this, this::grantArbitraryDownloadFolderPermission);
        } else {
            downloadFolderLauncher = new NullPermissionLauncher();
        }
    }

    private void prepareDownloadFolderField() {
        Log.d(GlobalSettingsActivity.class.getName(), "prepareDownloadFolderField");
        PreferenceManager preferenceManager = new PreferenceManager(this);
        CardView downloadFolderCardView = findViewById(R.id.cardview_activity_global_settings_download_folder);
        downloadFolderText = findViewById(R.id.textview_activity_global_settings_download_folder);
        if (downloadExternalStorageSwitch.isChecked()) {
            if (preferenceManager.getPreferenceAllowArbitraryFileLocation()) {
                prepareArbitraryDownloadFolder(downloadFolderCardView);
            } else {
                prepareExternalAppDownloadFolder(downloadFolderCardView, preferenceManager);
            }
        } else {
            setDownloadFolder(getResources().getString(R.string.text_activity_global_settings_download_folder_internal));
            downloadFolderCardView.setEnabled(false);
            downloadFolderCardView.setOnClickListener(null);
        }
    }

    private void prepareArbitraryDownloadFolder(CardView downloadFolderCardView) {
        Log.d(GlobalSettingsActivity.class.getName(), "prepareArbitraryDownloadFolder");
        downloadFolderCardView.setEnabled(true);
        downloadFolderCardView.setOnClickListener(this::requestArbitraryDownloadFolderPermission);
        IStoragePermissionManager storagePermissionManager = getStoragePermissionManager();
        String arbitraryDownloadFolder = getPreferenceArbitraryDownloadFolder();
        if (storagePermissionManager.hasPersistentPermission(this, arbitraryDownloadFolder)) {
            Log.d(GlobalSettingsActivity.class.getName(), "Permission for " + arbitraryDownloadFolder + " is already present");
            setDownloadFolder(arbitraryDownloadFolder);
        } else {
            Log.d(GlobalSettingsActivity.class.getName(), "Requesting permission for " + arbitraryDownloadFolder);
            storagePermissionManager.requestPersistentFolderPermission(downloadFolderLauncher, arbitraryDownloadFolder);
        }
    }

    private void prepareExternalAppDownloadFolder(CardView downloadFolderCardView, PreferenceManager preferenceManager) {
        Log.d(GlobalSettingsActivity.class.getName(), "prepareExternalAppDownloadFolder");
        String downloadFolder = getExternalAppStorageDownloadFolder();
        Log.d(GlobalSettingsActivity.class.getName(), "External download folder is " + downloadFolder);
        if (downloadFolder != null) {
            setDownloadFolder(downloadFolder);
            downloadFolderCardView.setEnabled(true);
            downloadFolderCardView.setOnClickListener(this::showDownloadFolderChooseDialog);
        } else {
            Log.e(GlobalSettingsActivity.class.getName(), "Error accessing download folder.");
            Log.d(GlobalSettingsActivity.class.getName(), "Reset to internal folder.");
            setDownloadFolder(getResources().getString(R.string.text_activity_global_settings_download_folder_internal));
            downloadFolderCardView.setEnabled(false);
            downloadFolderCardView.setOnClickListener(null);
            preferenceManager.setPreferenceDownloadExternalStorage(false);
            downloadExternalStorageSwitch.setChecked(false);
            prepareDownloadExternalStorageOnOffText();
            prepareDownloadKeepSwitch();
            Log.d(GlobalSettingsActivity.class.getName(), "Showing error dialog.");
            showErrorDialog(getResources().getString(R.string.text_dialog_general_error_external_root_access));
        }
    }

    private void prepareDownloadKeepSwitch() {
        Log.d(GlobalSettingsActivity.class.getName(), "prepareDownloadKeepSwitch");
        PreferenceManager preferenceManager = new PreferenceManager(this);
        downloadKeepSwitch = findViewById(R.id.switch_activity_global_settings_download_keep);
        downloadKeepOnOffText = findViewById(R.id.textview_activity_global_settings_download_keep_on_off);
        if (downloadExternalStorageSwitch.isChecked()) {
            downloadKeepSwitch.setOnCheckedChangeListener(null);
            downloadKeepSwitch.setChecked(preferenceManager.getPreferenceDownloadKeep());
            downloadKeepSwitch.setOnCheckedChangeListener(this::onDownloadKeepCheckedChanged);
            downloadKeepSwitch.setEnabled(true);
        } else {
            downloadKeepSwitch.setOnCheckedChangeListener(null);
            downloadKeepSwitch.setChecked(false);
            downloadKeepSwitch.setEnabled(false);
        }
        prepareDownloadKeepOnOffText();
    }

    private void prepareDownloadKeepOnOffText() {
        downloadKeepOnOffText.setText(downloadKeepSwitch.isChecked() ? getResources().getString(R.string.string_yes) : getResources().getString(R.string.string_no));
    }

    private void onDownloadKeepCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.d(GlobalSettingsActivity.class.getName(), "onDownloadKeepCheckedChanged, new value is " + isChecked);
        PreferenceManager preferenceManager = new PreferenceManager(this);
        preferenceManager.setPreferenceDownloadKeep(isChecked);
        prepareDownloadKeepOnOffText();
    }

    private void prepareLogFileSwitch() {
        Log.d(GlobalSettingsActivity.class.getName(), "prepareLogFileSwitch");
        PreferenceManager preferenceManager = new PreferenceManager(this);
        logFileSwitch = findViewById(R.id.switch_activity_global_settings_log_file);
        logFileOnOffText = findViewById(R.id.textview_activity_global_settings_log_file_on_off);
        logFileSwitch.setOnCheckedChangeListener(null);
        logFileSwitch.setChecked(preferenceManager.getPreferenceLogFile());
        logFileSwitch.setOnCheckedChangeListener(this::onLogFileCheckedChanged);
        prepareLogFileOnOffText();
    }

    private void prepareLogFileOnOffText() {
        logFileOnOffText.setText(logFileSwitch.isChecked() ? getResources().getString(R.string.string_yes) : getResources().getString(R.string.string_no));
    }

    private void onLogFileCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.d(GlobalSettingsActivity.class.getName(), "onLogFileCheckedChanged, new value is " + isChecked);
        PreferenceManager preferenceManager = new PreferenceManager(this);
        preferenceManager.setPreferenceLogFile(isChecked);
        prepareLogFileOnOffText();
        prepareLogFolderField();
    }

    private void prepareLogFolderLauncher() {
        Log.d(GlobalSettingsActivity.class.getName(), "prepareLogFolderLauncher");
        boolean bypassSystemSAF = BundleUtil.booleanFromBundle(getBypassSystemSAFKey(), getIntent().getExtras());
        if (SystemUtil.supportsSAFFeature() && !bypassSystemSAF) {
            logFolderLauncher = new GenericPermissionLauncher(this, this::grantArbitraryLogFolderPermission);
        } else {
            logFolderLauncher = new NullPermissionLauncher();
        }
    }

    private void prepareLogFolderField() {
        Log.d(GlobalSettingsActivity.class.getName(), "prepareLogFolderField");
        PreferenceManager preferenceManager = new PreferenceManager(this);
        CardView logFolderCardView = findViewById(R.id.cardview_activity_global_settings_log_folder);
        logFolderText = findViewById(R.id.textview_activity_global_settings_log_folder);
        if (logFileSwitch.isChecked()) {
            if (preferenceManager.getPreferenceAllowArbitraryFileLocation()) {
                prepareArbitraryLogFolder(logFolderCardView);
            } else {
                prepareExternalAppLogFolder(logFolderCardView, preferenceManager);
            }
        } else {
            setLogFolder(getResources().getString(R.string.text_activity_global_settings_log_folder_none));
            logFolderCardView.setEnabled(false);
            logFolderCardView.setOnClickListener(null);
        }
    }

    private void prepareArbitraryLogFolder(CardView logFolderCardView) {
        Log.d(GlobalSettingsActivity.class.getName(), "prepareArbitraryLogFolder");
        logFolderCardView.setEnabled(true);
        logFolderCardView.setOnClickListener(this::requestArbitraryLogFolderPermission);
        IStoragePermissionManager storagePermissionManager = getStoragePermissionManager();
        String arbitraryLogFolder = getPreferenceArbitraryLogFolder();
        if (storagePermissionManager.hasPersistentPermission(this, arbitraryLogFolder)) {
            Log.d(GlobalSettingsActivity.class.getName(), "Permission for " + arbitraryLogFolder + " is already present");
            setLogFolder(arbitraryLogFolder);
        } else {
            Log.d(GlobalSettingsActivity.class.getName(), "Requesting permission for " + arbitraryLogFolder);
            storagePermissionManager.requestPersistentFolderPermission(logFolderLauncher, arbitraryLogFolder);
        }
    }

    private void prepareExternalAppLogFolder(CardView logFolderCardView, PreferenceManager preferenceManager) {
        Log.d(GlobalSettingsActivity.class.getName(), "prepareExternalAppLogFolder");
        String logFolder = getExternalAppStorageLogFolder();
        Log.d(GlobalSettingsActivity.class.getName(), "Log folder is " + logFolder);
        if (logFolder != null) {
            setLogFolder(logFolder);
            logFolderCardView.setEnabled(true);
            logFolderCardView.setOnClickListener(this::showLogFolderChooseDialog);
        } else {
            Log.e(GlobalSettingsActivity.class.getName(), "Error accessing log folder.");
            Log.d(GlobalSettingsActivity.class.getName(), "Reset to none.");
            setLogFolder(getResources().getString(R.string.text_activity_global_settings_log_folder_none));
            logFolderCardView.setEnabled(false);
            logFolderCardView.setOnClickListener(null);
            preferenceManager.setPreferenceLogFile(false);
            logFileSwitch.setChecked(false);
            prepareLogFileOnOffText();
            Log.d(GlobalSettingsActivity.class.getName(), "Showing error dialog.");
            showErrorDialog(getResources().getString(R.string.text_dialog_general_error_external_root_access));
        }
    }

    private String getNotificationAfterFailures() {
        return StringUtil.notNull(notificationAfterFailuresText.getText());
    }

    private void setNotificationAfterFailures(String notificationAfterFailures) {
        notificationAfterFailuresText.setText(StringUtil.notNull(notificationAfterFailures));
    }

    private void setDownloadFolder(String downloadFolder) {
        downloadFolderText.setText(StringUtil.notNull(downloadFolder));
    }

    private void setLogFolder(String logFolder) {
        logFolderText.setText(StringUtil.notNull(logFolder));
    }

    private void showNotificationAfterFailuresInputDialog(View view) {
        Log.d(GlobalSettingsActivity.class.getName(), "showNotificationAfterFailuresInputDialog");
        List<String> validators = Collections.singletonList(NotificationAfterFailuresFieldValidator.class.getName());
        SettingsInput input = new SettingsInput(SettingsInput.Type.NOTIFICATIONAFTER, getNotificationAfterFailures(), getResources().getString(R.string.label_activity_global_settings_notification_after_failures), validators);
        showInputDialog(input.toBundle());
    }

    private void showDownloadFolderChooseDialog(View view) {
        Log.d(GlobalSettingsActivity.class.getName(), "showDownloadFolderChooseDialog");
        FileChooseDialog fileChooseDialog = new FileChooseDialog();
        String root = getExternalRootFolder();
        Log.d(GlobalSettingsActivity.class.getName(), "External root folder is " + root);
        if (root == null) {
            Log.e(GlobalSettingsActivity.class.getName(), "Error accessing root folder.");
            Log.d(GlobalSettingsActivity.class.getName(), "Showing error dialog.");
            showErrorDialog(getResources().getString(R.string.text_dialog_general_error_external_root_access));
            return;
        }
        String folder = getPreferenceDownloadFolder();
        Log.d(GlobalSettingsActivity.class.getName(), "Preference download folder is " + folder);
        if (folder == null) {
            Log.e(GlobalSettingsActivity.class.getName(), "Error accessing download folder.");
            Log.d(GlobalSettingsActivity.class.getName(), "Showing error dialog.");
            showErrorDialog(getResources().getString(R.string.text_dialog_general_error_external_root_access));
            return;
        }
        Bundle bundle = BundleUtil.stringsToBundle(new String[]{fileChooseDialog.getFolderRootKey(), fileChooseDialog.getFolderKey(), fileChooseDialog.getFileModeKey(), fileChooseDialog.getTypeKey()}, new String[]{root, folder, FileChooseDialog.Mode.FOLDER.name(), FileChooseDialog.Type.DOWNLOADFOLDER.name()});
        fileChooseDialog.setArguments(bundle);
        fileChooseDialog.show(getSupportFragmentManager(), FileChooseDialog.class.getName());
    }

    private void requestArbitraryDownloadFolderPermission(View view) {
        Log.d(GlobalSettingsActivity.class.getName(), "requestArbitraryDownloadFolderPermission");
        IStoragePermissionManager storagePermissionManager = getStoragePermissionManager();
        String arbitraryDownloadFolder = getPreferenceArbitraryDownloadFolder();
        storagePermissionManager.requestPersistentFolderPermission(downloadFolderLauncher, arbitraryDownloadFolder);
    }

    public void grantArbitraryDownloadFolderPermission(Uri uri) {
        Log.d(GlobalSettingsActivity.class.getName(), "grantArbitraryDownloadFolderPermission for uri " + uri);
        if (uri == null) {
            Log.e(GlobalSettingsActivity.class.getName(), "uri is null");
            return;
        }
        String downloadFolder = uri.toString();
        Log.d(GlobalSettingsActivity.class.getName(), "New arbitrary download folder is " + downloadFolder);
        PreferenceManager preferenceManager = new PreferenceManager(this);
        String currentDownloadFolder = preferenceManager.getPreferenceArbitraryDownloadFolder();
        Log.d(GlobalSettingsActivity.class.getName(), "Old arbitrary download folder is " + currentDownloadFolder);
        if (!currentDownloadFolder.equals(downloadFolder)) {
            Log.d(GlobalSettingsActivity.class.getName(), "Arbitrary download folder changed. Revoking old permission.");
            preferenceManager.setPreferenceArbitraryDownloadFolder(downloadFolder);
            IStoragePermissionManager storagePermissionManager = getStoragePermissionManager();
            storagePermissionManager.revokeOrphanPersistentPermissions(this, preferenceManager.getArbitraryFolders());
            NetworkTaskLog.clear();
        }
        setDownloadFolder(downloadFolder);
    }

    private void showLogFolderChooseDialog(View view) {
        Log.d(GlobalSettingsActivity.class.getName(), "showLogFolderChooseDialog");
        FileChooseDialog fileChooseDialog = new FileChooseDialog();
        String root = getExternalRootFolder();
        Log.d(GlobalSettingsActivity.class.getName(), "External root folder is " + root);
        if (root == null) {
            Log.e(GlobalSettingsActivity.class.getName(), "Error accessing root folder.");
            Log.d(GlobalSettingsActivity.class.getName(), "Showing error dialog.");
            showErrorDialog(getResources().getString(R.string.text_dialog_general_error_external_root_access));
            return;
        }
        String folder = getPreferenceLogFolder();
        Log.d(GlobalSettingsActivity.class.getName(), "Preference log folder is " + folder);
        if (folder == null) {
            Log.e(GlobalSettingsActivity.class.getName(), "Error accessing log folder.");
            Log.d(GlobalSettingsActivity.class.getName(), "Showing error dialog.");
            showErrorDialog(getResources().getString(R.string.text_dialog_general_error_external_root_access));
            return;
        }
        Bundle bundle = BundleUtil.stringsToBundle(new String[]{fileChooseDialog.getFolderRootKey(), fileChooseDialog.getFolderKey(), fileChooseDialog.getFileModeKey(), fileChooseDialog.getTypeKey()}, new String[]{root, folder, FileChooseDialog.Mode.FOLDER.name(), FileChooseDialog.Type.LOGFOLDER.name()});
        fileChooseDialog.setArguments(bundle);
        fileChooseDialog.show(getSupportFragmentManager(), GlobalSettingsActivity.class.getName());
    }

    private void requestArbitraryLogFolderPermission(View view) {
        Log.d(GlobalSettingsActivity.class.getName(), "requestArbitraryLogFolderPermission");
        IStoragePermissionManager storagePermissionManager = getStoragePermissionManager();
        String arbitraryLogFolder = getPreferenceArbitraryLogFolder();
        storagePermissionManager.requestPersistentFolderPermission(logFolderLauncher, arbitraryLogFolder);
    }

    public void grantArbitraryLogFolderPermission(Uri uri) {
        Log.d(GlobalSettingsActivity.class.getName(), "grantArbitraryLogFolderPermission for uri " + uri);
        if (uri == null) {
            Log.e(GlobalSettingsActivity.class.getName(), "uri is null");
            return;
        }
        String logFolder = uri.toString();
        Log.d(GlobalSettingsActivity.class.getName(), "New arbitrary log folder is " + logFolder);
        PreferenceManager preferenceManager = new PreferenceManager(this);
        String currentLogFolder = preferenceManager.getPreferenceArbitraryLogFolder();
        Log.d(GlobalSettingsActivity.class.getName(), "Old arbitrary log folder is " + currentLogFolder);
        if (!currentLogFolder.equals(logFolder)) {
            Log.d(GlobalSettingsActivity.class.getName(), "Arbitrary log folder changed. Revoking old permission.");
            preferenceManager.setPreferenceArbitraryLogFolder(logFolder);
            IStoragePermissionManager storagePermissionManager = getStoragePermissionManager();
            storagePermissionManager.revokeOrphanPersistentPermissions(this, preferenceManager.getArbitraryFolders());
        }
        setLogFolder(logFolder);
    }

    private void showInputDialog(Bundle bundle) {
        Log.d(GlobalSettingsActivity.class.getName(), "showInputDialog, opening SettingsInputDialog");
        SettingsInputDialog inputDialog = new SettingsInputDialog();
        inputDialog.setArguments(bundle);
        inputDialog.show(getSupportFragmentManager(), GlobalSettingsActivity.class.getName());
    }

    private String getExternalRootFolder() {
        Log.d(GlobalSettingsActivity.class.getName(), "getExternalRootFolder");
        PreferenceManager preferenceManager = new PreferenceManager(this);
        IFileManager fileManager = getFileManager();
        File root = FileUtil.getExternalRootDirectory(fileManager, preferenceManager);
        Log.d(GlobalSettingsActivity.class.getName(), "External root folder is " + root);
        if (root == null) {
            return null;
        }
        return root.getAbsolutePath();
    }

    private String getPreferenceDownloadFolder() {
        Log.d(GlobalSettingsActivity.class.getName(), "getPreferenceDownloadFolder");
        String downloadFolder = getExternalAppStorageDownloadFolder();
        if (downloadFolder == null) {
            return null;
        }
        PreferenceManager preferenceManager = new PreferenceManager(this);
        return preferenceManager.getPreferenceDownloadFolder();
    }

    private String getPreferenceArbitraryDownloadFolder() {
        Log.d(GlobalSettingsActivity.class.getName(), "getPreferenceArbitraryDownloadFolder");
        PreferenceManager preferenceManager = new PreferenceManager(this);
        return preferenceManager.getPreferenceArbitraryDownloadFolder();
    }

    private String getPreferenceLogFolder() {
        Log.d(GlobalSettingsActivity.class.getName(), "getPreferenceLogFolder");
        String logFolder = getExternalAppStorageLogFolder();
        if (logFolder == null) {
            return null;
        }
        PreferenceManager preferenceManager = new PreferenceManager(this);
        return preferenceManager.getPreferenceLogFolder();
    }

    private String getPreferenceArbitraryLogFolder() {
        Log.d(GlobalSettingsActivity.class.getName(), "getPreferenceArbitraryLogFolder");
        PreferenceManager preferenceManager = new PreferenceManager(this);
        return preferenceManager.getPreferenceArbitraryLogFolder();
    }

    private String getExternalAppStorageDownloadFolder() {
        Log.d(GlobalSettingsActivity.class.getName(), "getExternalAppStorageDownloadFolder");
        PreferenceManager preferenceManager = new PreferenceManager(this);
        String folder = preferenceManager.getPreferenceDownloadFolder();
        IFileManager fileManager = getFileManager();
        File downloadFolder = FileUtil.getExternalDirectory(fileManager, preferenceManager, folder);
        Log.d(GlobalSettingsActivity.class.getName(), "External download folder is " + downloadFolder);
        if (downloadFolder == null) {
            return null;
        }
        return downloadFolder.getAbsolutePath();
    }

    private String getExternalAppStorageLogFolder() {
        Log.d(GlobalSettingsActivity.class.getName(), "getExternalAppStorageLogFolder");
        PreferenceManager preferenceManager = new PreferenceManager(this);
        String folder = preferenceManager.getPreferenceLogFolder();
        IFileManager fileManager = getFileManager();
        File logFolder = FileUtil.getExternalDirectory(fileManager, preferenceManager, folder);
        Log.d(GlobalSettingsActivity.class.getName(), "Log folder is " + logFolder);
        if (logFolder == null) {
            return null;
        }
        return logFolder.getAbsolutePath();
    }

    @Override
    public void onInputDialogOkClicked(SettingsInputDialog inputDialog, SettingsInput.Type type) {
        Log.d(GlobalSettingsActivity.class.getName(), "onInputDialogOkClicked, type is " + type + ", value is " + inputDialog.getValue());
        PreferenceManager preferenceManager = new PreferenceManager(this);
        if (SettingsInput.Type.NOTIFICATIONAFTER.equals(type)) {
            setNotificationAfterFailures(inputDialog.getValue());
            preferenceManager.setPreferenceNotificationAfterFailures(NumberUtil.getIntValue(getNotificationAfterFailures(), getResources().getInteger(R.integer.notification_after_failures_default)));
        } else {
            Log.e(GlobalSettingsActivity.class.getName(), "type " + type + " unknown");
        }
        inputDialog.dismiss();
    }

    @Override
    public void onFileChooseDialogOkClicked(FileChooseDialog folderChooseDialog, FileChooseDialog.Type type) {
        Log.d(GlobalSettingsActivity.class.getName(), "onFileChooseDialogOkClicked, type is " + type);
        IFileManager fileManager = getFileManager();
        PreferenceManager preferenceManager = new PreferenceManager(this);
        if (FileChooseDialog.Type.DOWNLOADFOLDER.equals(type)) {
            String folder = folderChooseDialog.getFolder();
            File downloadFolder = FileUtil.getExternalDirectory(fileManager, preferenceManager, folder);
            Log.d(GlobalSettingsActivity.class.getName(), "External download folder is " + downloadFolder);
            if (downloadFolder == null) {
                Log.e(GlobalSettingsActivity.class.getName(), "Error accessing download folder.");
                folderChooseDialog.dismiss();
                Log.d(GlobalSettingsActivity.class.getName(), "Showing error dialog.");
                showErrorDialog(getResources().getString(R.string.text_dialog_general_error_external_download_create));
                return;
            }
            preferenceManager.setPreferenceDownloadFolder(folder);
            setDownloadFolder(downloadFolder.getAbsolutePath());
        }
        if (FileChooseDialog.Type.LOGFOLDER.equals(type)) {
            String folder = folderChooseDialog.getFolder();
            File logFolder = FileUtil.getExternalDirectory(fileManager, preferenceManager, folder);
            Log.d(GlobalSettingsActivity.class.getName(), "Log folder is " + logFolder);
            if (logFolder == null) {
                Log.e(GlobalSettingsActivity.class.getName(), "Error accessing log folder.");
                folderChooseDialog.dismiss();
                Log.d(GlobalSettingsActivity.class.getName(), "Showing error dialog.");
                showErrorDialog(getResources().getString(R.string.text_dialog_general_error_external_log_create));
                return;
            }
            preferenceManager.setPreferenceLogFolder(folder);
            setLogFolder(logFolder.getAbsolutePath());
            NetworkTaskLog.clear();
        } else {
            Log.e(GlobalSettingsActivity.class.getName(), "Unknown type " + type);
        }
        folderChooseDialog.dismiss();
    }

    @Override
    public void onSuspensionIntervalsDialogOkClicked(SuspensionIntervalsDialog intervalsDialog) {
        Log.d(GlobalSettingsActivity.class.getName(), "onSuspensionIntervalsDialogOkClicked");
        IntervalHandler handler = new IntervalHandler(this, intervalsDialog);
        if (handler.synchronizeIntervals()) {
            getTimeBasedSuspensionScheduler().restart();
            prepareSuspensionIntervalsField();
        }
        intervalsDialog.dismiss();
    }

    @Override
    public void onSuspensionIntervalsDialogCancelClicked(SuspensionIntervalsDialog intervalsDialog) {
        Log.d(GlobalSettingsActivity.class.getName(), "onSuspensionIntervalsDialogCancelClicked");
        intervalsDialog.dismiss();
    }

    public static String getBypassSystemSAFKey() {
        return GlobalSettingsActivity.class.getSimpleName() + "BypassSystemSAF";
    }
}
