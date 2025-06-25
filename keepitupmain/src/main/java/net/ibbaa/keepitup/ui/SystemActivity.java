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

import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.switchmaterial.SwitchMaterial;

import net.ibbaa.keepitup.BuildConfig;
import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Dump;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.logging.NetworkTaskLog;
import net.ibbaa.keepitup.resources.PreferenceManager;
import net.ibbaa.keepitup.resources.PreferenceSetup;
import net.ibbaa.keepitup.resources.SystemSetupResult;
import net.ibbaa.keepitup.service.IFileManager;
import net.ibbaa.keepitup.service.IPowerManager;
import net.ibbaa.keepitup.service.IThemeManager;
import net.ibbaa.keepitup.service.NetworkTaskProcessServiceScheduler;
import net.ibbaa.keepitup.service.StartupService;
import net.ibbaa.keepitup.service.SystemThemeManager;
import net.ibbaa.keepitup.ui.dialog.BatteryOptimizationDialog;
import net.ibbaa.keepitup.ui.dialog.ConfirmDialog;
import net.ibbaa.keepitup.ui.dialog.FileChooseDialog;
import net.ibbaa.keepitup.ui.dialog.GeneralMessageDialog;
import net.ibbaa.keepitup.ui.permission.GenericPermissionLauncher;
import net.ibbaa.keepitup.ui.permission.IStoragePermissionManager;
import net.ibbaa.keepitup.ui.permission.NullPermissionLauncher;
import net.ibbaa.keepitup.ui.permission.PermissionLauncher;
import net.ibbaa.keepitup.ui.sync.DBPurgeTask;
import net.ibbaa.keepitup.ui.sync.ExportTask;
import net.ibbaa.keepitup.ui.sync.ImportTask;
import net.ibbaa.keepitup.util.BundleUtil;
import net.ibbaa.keepitup.util.DebugUtil;
import net.ibbaa.keepitup.util.FileUtil;
import net.ibbaa.keepitup.util.StringUtil;
import net.ibbaa.keepitup.util.SystemUtil;
import net.ibbaa.keepitup.util.ThreadUtil;

import java.io.File;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@SuppressWarnings({"unused", "FieldCanBeLocal"})
public class SystemActivity extends SettingsInputActivity implements ExportSupport, ImportSupport, DBPurgeSupport, MessageSupport {

    private enum Error {
        IMPORTERROR,
        PURGERROR
    }

    private TextView exportFolderText;
    private TextView importFolderText;
    private RadioGroup externalStorageType;
    private SwitchMaterial arbitraryFileLocationSwitch;
    private TextView arbitraryFileLocationOnOffText;
    private SwitchMaterial alarmOnHighPrioSwitch;
    private TextView alarmOnHighPrioOnOffText;
    private SwitchMaterial fileLoggerEnabledSwitch;
    private TextView fileLoggerEnabledOnOffText;
    private SwitchMaterial fileDumpEnabledSwitch;
    private TextView fileDumpEnabledOnOffText;
    private TextView logFolderText;
    private TextView batteryOptimizationText;
    private RadioGroup theme;
    private PermissionLauncher exportFileLauncher;
    private PermissionLauncher importFileLauncher;
    private PermissionLauncher arbitraryFolderLauncher;

    private DBPurgeTask purgeTask;
    private ExportTask exportTask;
    private ImportTask importTask;
    private NetworkTaskProcessServiceScheduler networkTaskProcessServiceScheduler;
    private IThemeManager themeManager;

    public void injectExportTask(ExportTask exportTask) {
        this.exportTask = exportTask;
    }

    public void injectImportTask(ImportTask importTask) {
        this.importTask = importTask;
    }

    public void injectPurgeTask(DBPurgeTask purgeTask) {
        this.purgeTask = purgeTask;
    }

    public void injectNetworkTaskProcessServiceScheduler(NetworkTaskProcessServiceScheduler scheduler) {
        this.networkTaskProcessServiceScheduler = scheduler;
    }

    public void injectThemeManager(IThemeManager themeManager) {
        this.themeManager = themeManager;
    }

    public void injectArbitraryFolderLauncher(PermissionLauncher arbitraryFolderLauncher) {
        this.arbitraryFolderLauncher = arbitraryFolderLauncher;
    }

    public void injectExportFileLauncher(PermissionLauncher exportFileLauncher) {
        this.exportFileLauncher = exportFileLauncher;
    }

    public void injectImportFileLauncher(PermissionLauncher importFileLauncher) {
        this.importFileLauncher = importFileLauncher;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setContentView(R.layout.activity_system);
        initEdgeToEdgeInsets(R.id.layout_activity_system);
        prepareConfigurationResetField();
        prepareConfigurationExportField();
        prepareConfigurationExportFileLauncher();
        prepareConfigurationImportField();
        prepareConfigurationImportFileLauncher();
        prepareExternalStorageTypeRadioGroup();
        prepareBatteryOptimizationField();
        prepareThemeRadioGroup();
        prepareArbitraryFolderLauncher();
        prepareAllowArbitraryFileLocationSwitch();
        prepareArbitraryFolderPermissions();
        prepareAlarmOnHighPrioSwitch();
        prepareDebugSettingsLabel();
        prepareFileLoggerEnabledSwitch();
        prepareFileDumpEnabledSwitch();
        prepareLogFolderField();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_system, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_action_activity_system_reset) {
            Log.d(SystemActivity.class.getName(), "menu_action_activity_system_reset triggered");
            PreferenceSetup preferenceSetup = new PreferenceSetup(this);
            preferenceSetup.removeSystemSettings();
            resetTheme();
            recreateActivity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void prepareConfigurationResetField() {
        Log.d(SystemActivity.class.getName(), "prepareConfigurationResetField");
        CardView configurationResetView = findViewById(R.id.cardview_activity_system_config_reset);
        configurationResetView.setOnClickListener(this::showResetConfirmDialog);
    }

    private void prepareConfigurationExportField() {
        Log.d(SystemActivity.class.getName(), "prepareConfigurationExportField");
        CardView configurationExportView = findViewById(R.id.cardview_activity_system_config_export);
        exportFolderText = findViewById(R.id.textview_activity_system_config_export_folder);
        PreferenceManager preferenceManager = new PreferenceManager(this);
        if (preferenceManager.getPreferenceAllowArbitraryFileLocation()) {
            setExportFolder(getResources().getString(R.string.text_activity_system_import_export_choose_file));
            configurationExportView.setOnClickListener(this::requestConfigurationExportFilePermission);
        } else {
            String exportFolder = getExternalImportExportFolder(preferenceManager.getPreferenceExportFolder());
            setExportFolder(exportFolder);
            configurationExportView.setOnClickListener(this::showExportFolderChooseDialog);
        }
    }

    private void prepareConfigurationImportField() {
        Log.d(SystemActivity.class.getName(), "prepareConfigurationImportField");
        CardView configurationImportView = findViewById(R.id.cardview_activity_system_config_import);
        importFolderText = findViewById(R.id.textview_activity_system_config_import_folder);
        PreferenceManager preferenceManager = new PreferenceManager(this);
        if (preferenceManager.getPreferenceAllowArbitraryFileLocation()) {
            setImportFolder(getResources().getString(R.string.text_activity_system_import_export_choose_file));
            configurationImportView.setOnClickListener(this::requestConfigurationImportFilePermission);
        } else {
            String importFolder = getExternalImportExportFolder(preferenceManager.getPreferenceImportFolder());
            setImportFolder(importFolder);
            configurationImportView.setOnClickListener(this::showImportFolderChooseDialog);
        }
    }

    private void prepareExternalStorageTypeRadioGroup() {
        Log.d(SystemActivity.class.getName(), "prepareExternalStorageTypeRadioGroup");
        externalStorageType = findViewById(R.id.radiogroup_activity_system_external_storage_type);
        PreferenceManager preferenceManager = new PreferenceManager(this);
        IFileManager fileManager = getFileManager();
        boolean sdCardSupported = fileManager.isSDCardSupported();
        Log.d(SystemActivity.class.getName(), "SD card supported: " + sdCardSupported);
        if (sdCardSupported) {
            RadioButton primaryStorageTypeButton = findViewById(R.id.radiobutton_activity_system_external_storage_type_primary);
            RadioButton sdCardStorageTypeButton = findViewById(R.id.radiobutton_activity_system_external_storage_type_sdcard);
            primaryStorageTypeButton.setButtonTintList(ColorStateList.valueOf(ResourcesCompat.getColor(getResources(), R.color.textColor, null)));
            sdCardStorageTypeButton.setButtonTintList(ColorStateList.valueOf(ResourcesCompat.getColor(getResources(), R.color.textColor, null)));
            sdCardStorageTypeButton.setVisibility(View.VISIBLE);
            int externalStorage = preferenceManager.getPreferenceExternalStorageType();
            Log.d(SystemActivity.class.getName(), "externalStorage is " + externalStorage);
            externalStorageType.setEnabled(true);
            primaryStorageTypeButton.setEnabled(true);
            sdCardStorageTypeButton.setEnabled(true);
            if (externalStorage <= 0) {
                primaryStorageTypeButton.setChecked(true);
                sdCardStorageTypeButton.setChecked(false);
            } else {
                primaryStorageTypeButton.setChecked(false);
                sdCardStorageTypeButton.setChecked(true);
            }
            externalStorageType.setOnCheckedChangeListener(this::onExternalStorageTypeChanged);
        } else {
            externalStorageType.setOnCheckedChangeListener(null);
            RadioButton primaryStorageTypeButton = findViewById(R.id.radiobutton_activity_system_external_storage_type_primary);
            RadioButton sdCardStorageTypeButton = findViewById(R.id.radiobutton_activity_system_external_storage_type_sdcard);
            primaryStorageTypeButton.setChecked(true);
            sdCardStorageTypeButton.setChecked(false);
            externalStorageType.setEnabled(false);
            primaryStorageTypeButton.setEnabled(false);
            sdCardStorageTypeButton.setEnabled(false);
            sdCardStorageTypeButton.setVisibility(View.GONE);
        }
        if (preferenceManager.getPreferenceAllowArbitraryFileLocation()) {
            disableExternalStorageTypeRadioGroup();
        }
    }

    private void disableExternalStorageTypeRadioGroup() {
        Log.d(SystemActivity.class.getName(), "disableExternalStorageTypeRadioGroup");
        RadioButton primaryStorageTypeButton = findViewById(R.id.radiobutton_activity_system_external_storage_type_primary);
        RadioButton sdCardStorageTypeButton = findViewById(R.id.radiobutton_activity_system_external_storage_type_sdcard);
        externalStorageType.setEnabled(false);
        primaryStorageTypeButton.setEnabled(false);
        sdCardStorageTypeButton.setEnabled(false);
    }

    private void onExternalStorageTypeChanged(RadioGroup group, int checkedId) {
        Log.d(SystemActivity.class.getName(), "onExternalStorageTypeChanged");
        PreferenceManager preferenceManager = new PreferenceManager(this);
        int checkedExternalStorage = group.getCheckedRadioButtonId();
        if (R.id.radiobutton_activity_system_external_storage_type_sdcard == checkedExternalStorage) {
            Log.d(SystemActivity.class.getName(), "SD card selected as external storage type");
            preferenceManager.setPreferenceExternalStorageType(1);
        } else {
            Log.d(SystemActivity.class.getName(), "Primary selected as external storage type");
            preferenceManager.setPreferenceExternalStorageType(0);
        }
        prepareConfigurationExportField();
        prepareConfigurationImportField();
    }

    private void prepareBatteryOptimizationField() {
        Log.d(SystemActivity.class.getName(), "prepareBatteryOptimizationField");
        CardView batteryOptimizationCardView = findViewById(R.id.cardview_activity_system_battery_optimization);
        batteryOptimizationText = findViewById(R.id.textview_activity_system_battery_optimization);
        IPowerManager powerManager = getPowerManager();
        if (powerManager.isBatteryOptimized()) {
            Log.d(SystemActivity.class.getName(), "Battery optimization is active");
            batteryOptimizationText.setText(R.string.string_active);
        } else {
            Log.d(SystemActivity.class.getName(), "Battery optimization is inactive");
            batteryOptimizationText.setText(R.string.string_inactive);
        }
        if (powerManager.supportsBatteryOptimization()) {
            Log.d(SystemActivity.class.getName(), "Battery optimization is supported");
            batteryOptimizationCardView.setOnClickListener(this::showBatteryOptimizationDialog);
            batteryOptimizationCardView.setEnabled(true);
        } else {
            Log.d(SystemActivity.class.getName(), "Battery optimization is not supported");
            batteryOptimizationCardView.setOnClickListener(null);
            batteryOptimizationCardView.setEnabled(false);
        }
    }

    private void showBatteryOptimizationDialog(View view) {
        Log.d(SystemActivity.class.getName(), "showBatteryOptimizationDialog");
        BatteryOptimizationDialog batteryOptimizationDialog = new BatteryOptimizationDialog();
        batteryOptimizationDialog.show(getSupportFragmentManager(), BatteryOptimizationDialog.class.getName());
    }

    @Override
    public void onBatteryOptimizationDialogOkClicked(BatteryOptimizationDialog batteryOptimizationDialog) {
        Log.d(SystemActivity.class.getName(), "onBatteryOptimizationDialogOkClicked");
        prepareBatteryOptimizationField();
        batteryOptimizationDialog.dismiss();
    }

    private void prepareThemeRadioGroup() {
        Log.d(SystemActivity.class.getName(), "prepareThemeRadioGroup");
        theme = findViewById(R.id.radiogroup_activity_system_theme);
        PreferenceManager preferenceManager = new PreferenceManager(this);
        int themeCode = preferenceManager.getPreferenceTheme();
        IThemeManager themeManager = getThemeManager();
        Log.d(SystemActivity.class.getName(), "theme is " + themeManager.getThemeName(themeCode));
        RadioButton systemThemeButton = findViewById(R.id.radiobutton_activity_system_theme_system);
        RadioButton lightThemeButton = findViewById(R.id.radiobutton_activity_system_theme_light);
        RadioButton darkThemeButton = findViewById(R.id.radiobutton_activity_system_theme_dark);
        systemThemeButton.setButtonTintList(ColorStateList.valueOf(ResourcesCompat.getColor(getResources(), R.color.textColor, null)));
        lightThemeButton.setButtonTintList(ColorStateList.valueOf(ResourcesCompat.getColor(getResources(), R.color.textColor, null)));
        darkThemeButton.setButtonTintList(ColorStateList.valueOf(ResourcesCompat.getColor(getResources(), R.color.textColor, null)));
        theme.setOnCheckedChangeListener(null);
        if (themeCode == AppCompatDelegate.MODE_NIGHT_NO) {
            lightThemeButton.setChecked(true);
            systemThemeButton.setChecked(false);
            darkThemeButton.setChecked(false);
        } else if (themeCode == AppCompatDelegate.MODE_NIGHT_YES) {
            darkThemeButton.setChecked(true);
            systemThemeButton.setChecked(false);
            lightThemeButton.setChecked(false);
        } else {
            systemThemeButton.setChecked(true);
            lightThemeButton.setChecked(false);
            darkThemeButton.setChecked(false);
        }
        theme.setOnCheckedChangeListener(this::onThemeChanged);
    }

    private void onThemeChanged(RadioGroup group, int checkedId) {
        Log.d(SystemActivity.class.getName(), "onThemeChanged");
        PreferenceManager preferenceManager = new PreferenceManager(this);
        int checkedTheme = group.getCheckedRadioButtonId();
        IThemeManager themeManager = getThemeManager();
        if (R.id.radiobutton_activity_system_theme_light == checkedTheme) {
            Log.d(SystemActivity.class.getName(), "Light theme selected");
            preferenceManager.setPreferenceTheme(AppCompatDelegate.MODE_NIGHT_NO);
            themeManager.setThemeByCode(AppCompatDelegate.MODE_NIGHT_NO);
        } else if (R.id.radiobutton_activity_system_theme_dark == checkedTheme) {
            Log.d(SystemActivity.class.getName(), "Dark theme selected");
            preferenceManager.setPreferenceTheme(AppCompatDelegate.MODE_NIGHT_YES);
            themeManager.setThemeByCode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            Log.d(SystemActivity.class.getName(), "System theme selected");
            preferenceManager.setPreferenceTheme(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            themeManager.setThemeByCode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
    }

    private void prepareArbitraryFolderLauncher() {
        Log.d(SystemActivity.class.getName(), "prepareArbitraryFolderLauncher");
        boolean bypassSystemSAF = BundleUtil.booleanFromBundle(getBypassSystemSAFKey(), getIntent().getExtras());
        if (SystemUtil.supportsSAFFeature() && !bypassSystemSAF) {
            arbitraryFolderLauncher = new GenericPermissionLauncher(this, this::grantArbitraryFolderPermissions);
        } else {
            arbitraryFolderLauncher = new NullPermissionLauncher();
        }
    }

    public static String getBypassSystemSAFKey() {
        return SystemActivity.class.getSimpleName() + "BypassSystemSAF";
    }

    private void prepareAllowArbitraryFileLocationSwitch() {
        Log.d(SystemActivity.class.getName(), "prepareAllowArbitraryFileLocationSwitch");
        CardView arbitraryFileLocationCardView = findViewById(R.id.cardview_activity_system_allow_arbitrary_file_location);
        arbitraryFileLocationSwitch = findViewById(R.id.switch_activity_system_allow_arbitrary_file_location);
        arbitraryFileLocationOnOffText = findViewById(R.id.textview_activity_system_allow_arbitrary_file_location_on_off);
        PreferenceManager preferenceManager = new PreferenceManager(this);
        if (SystemUtil.supportsSAFFeature()) {
            Log.d(SystemActivity.class.getName(), "API version is " + Build.VERSION.SDK_INT + ". Enable SAF feature.");
            arbitraryFileLocationCardView.setVisibility(View.VISIBLE);
            arbitraryFileLocationSwitch.setOnCheckedChangeListener(null);
            arbitraryFileLocationSwitch.setChecked(preferenceManager.getPreferenceAllowArbitraryFileLocation());
            arbitraryFileLocationSwitch.setOnCheckedChangeListener(this::onAllowArbitraryFileLocationCheckedChanged);
            prepareAllowArbitraryFileLocationOnOffText();
        } else {
            Log.d(SystemActivity.class.getName(), "API version is " + Build.VERSION.SDK_INT + ". Disable SAF feature.");
            arbitraryFileLocationCardView.setVisibility(View.GONE);
            preferenceManager.setPreferenceAllowArbitraryFileLocation(false);
            arbitraryFileLocationSwitch.setChecked(false);
        }
    }

    private void prepareAllowArbitraryFileLocationOnOffText() {
        arbitraryFileLocationOnOffText.setText(arbitraryFileLocationSwitch.isChecked() ? getResources().getString(R.string.string_yes) : getResources().getString(R.string.string_no));
    }

    private void onAllowArbitraryFileLocationCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.d(SystemActivity.class.getName(), "onAllowArbitraryFileLocationCheckedChanged, new value is " + isChecked);
        PreferenceManager preferenceManager = new PreferenceManager(this);
        preferenceManager.setPreferenceAllowArbitraryFileLocation(isChecked);
        prepareAllowArbitraryFileLocationOnOffText();
        if (arbitraryFileLocationSwitch.isChecked()) {
            prepareArbitraryFolderPermissions();
            prepareConfigurationExportField();
            prepareConfigurationImportField();
            disableExternalStorageTypeRadioGroup();
        } else {
            prepareConfigurationExportField();
            prepareConfigurationImportField();
            prepareExternalStorageTypeRadioGroup();
        }
        NetworkTaskLog.clear();
    }

    private void prepareArbitraryFolderPermissions() {
        Log.d(SystemActivity.class.getName(), "prepareArbitraryFolderPermissions");
        IStoragePermissionManager storagePermissionManager = getStoragePermissionManager();
        if (arbitraryFileLocationSwitch.isChecked()) {
            if (!checkFolderPermissions()) {
                storagePermissionManager.requestPersistentFolderPermission(arbitraryFolderLauncher, null);
            } else {
                PreferenceManager preferenceManager = new PreferenceManager(this);
                storagePermissionManager.revokeOrphanPersistentPermissions(this, preferenceManager.getArbitraryFolders());
            }
        }
    }

    public void grantArbitraryFolderPermissions(Uri uri) {
        Log.d(SystemActivity.class.getName(), "grantArbitraryFolderPermissions for uri " + uri);
        if (uri == null) {
            Log.e(SystemActivity.class.getName(), "uri is null");
            return;
        }
        String arbitraryFolder = uri.toString();
        Log.d(GlobalSettingsActivity.class.getName(), "Chosen arbitrary folder is " + arbitraryFolder);
        PreferenceManager preferenceManager = new PreferenceManager(this);
        preferenceManager.setPreferenceArbitraryLogFolder(arbitraryFolder);
        preferenceManager.setPreferenceArbitraryDownloadFolder(arbitraryFolder);
        IStoragePermissionManager storagePermissionManager = getStoragePermissionManager();
        storagePermissionManager.revokeOrphanPersistentPermissions(this, preferenceManager.getArbitraryFolders());
        NetworkTaskLog.clear();
    }

    private void prepareAlarmOnHighPrioSwitch() {
        Log.d(SystemActivity.class.getName(), "prepareAlarmOnHighPrioSwitch");
        PreferenceManager preferenceManager = new PreferenceManager(this);
        alarmOnHighPrioSwitch = findViewById(R.id.switch_activity_system_alarm_on_high_prio);
        alarmOnHighPrioOnOffText = findViewById(R.id.textview_activity_system_alarm_on_high_prio_on_off);
        alarmOnHighPrioSwitch.setOnCheckedChangeListener(null);
        alarmOnHighPrioSwitch.setChecked(preferenceManager.getPreferenceAlarmOnHighPrio());
        alarmOnHighPrioSwitch.setOnCheckedChangeListener(this::onAlarmOnHighPrioCheckedChanged);
        prepareAlarmOnHighPrioOnOffText();

    }

    private void prepareAlarmOnHighPrioOnOffText() {
        alarmOnHighPrioOnOffText.setText(alarmOnHighPrioSwitch.isChecked() ? getResources().getString(R.string.string_yes) : getResources().getString(R.string.string_no));
    }

    private void onAlarmOnHighPrioCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.d(SystemActivity.class.getName(), "onAlarmOnHighPrioCheckedChanged, new value is " + isChecked);
        PreferenceManager preferenceManager = new PreferenceManager(this);
        if (isChecked && !preferenceManager.getPreferenceAlarmInfoShown()) {
            int alarmDuration = getResources().getInteger(R.integer.task_alarm_duration);
            showMessageDialog(getResources().getString(R.string.text_dialog_general_message_alarm_on_high_prio_notice, alarmDuration), Typeface.NORMAL);
            preferenceManager.setPreferenceAlarmInfoShown(true);
        }
        preferenceManager.setPreferenceAlarmOnHighPrio(isChecked);
        prepareAlarmOnHighPrioOnOffText();
    }

    private void prepareDebugSettingsLabel() {
        Log.d(SystemActivity.class.getName(), "prepareDebugSettingsLabel");
        CardView debugSettingsCardView = findViewById(R.id.cardview_activity_system_debug);
        if (BuildConfig.DEBUG) {
            Log.d(SystemActivity.class.getName(), "Debug version. Enabling debug settings.");
            debugSettingsCardView.setVisibility(View.VISIBLE);
        } else {
            Log.d(SystemActivity.class.getName(), "Release version. Enabling debug settings.");
            debugSettingsCardView.setVisibility(View.GONE);
        }
    }

    private void prepareFileLoggerEnabledSwitch() {
        Log.d(SystemActivity.class.getName(), "prepareFileLoggerEnabled");
        CardView fileLoggerEnabledCardView = findViewById(R.id.cardview_activity_system_file_logger_enabled);
        fileLoggerEnabledSwitch = findViewById(R.id.switch_activity_system_file_logger_enabled);
        fileLoggerEnabledOnOffText = findViewById(R.id.textview_activity_system_file_logger_enabled_on_off);
        if (BuildConfig.DEBUG) {
            Log.d(SystemActivity.class.getName(), "Debug version. Enabling debug settings.");
            fileLoggerEnabledCardView.setVisibility(View.VISIBLE);
            PreferenceManager preferenceManager = new PreferenceManager(this);
            fileLoggerEnabledSwitch.setOnCheckedChangeListener(null);
            fileLoggerEnabledSwitch.setChecked(preferenceManager.getPreferenceFileLoggerEnabled());
            fileLoggerEnabledSwitch.setOnCheckedChangeListener(this::onFileLoggerEnabledCheckedChanged);
            prepareFileLoggerEnabledOnOffText();
        } else {
            Log.d(SystemActivity.class.getName(), "Release version. Enabling debug settings.");
            fileLoggerEnabledCardView.setVisibility(View.GONE);
        }
    }

    private void prepareFileLoggerEnabledOnOffText() {
        fileLoggerEnabledOnOffText.setText(fileLoggerEnabledSwitch.isChecked() ? getResources().getString(R.string.string_yes) : getResources().getString(R.string.string_no));
    }

    private void onFileLoggerEnabledCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.d(SystemActivity.class.getName(), "onFileLoggerEnabledCheckedChanged, new value is " + isChecked);
        PreferenceManager preferenceManager = new PreferenceManager(this);
        preferenceManager.setPreferenceFileLoggerEnabled(isChecked);
        prepareFileLoggerEnabledOnOffText();
        if (isChecked && BuildConfig.DEBUG) {
            Log.initialize(DebugUtil.getFileLogger(this, getFileManager()));
        } else {
            Log.initialize(null);
        }
    }

    private void prepareFileDumpEnabledSwitch() {
        Log.d(SystemActivity.class.getName(), "prepareFileDumpEnabledSwitch");
        CardView fileDumpEnabledCardView = findViewById(R.id.cardview_activity_system_file_dump_enabled);
        fileDumpEnabledSwitch = findViewById(R.id.switch_activity_system_file_dump_enabled);
        fileDumpEnabledOnOffText = findViewById(R.id.textview_activity_system_file_dump_enabled_on_off);
        if (BuildConfig.DEBUG) {
            Log.d(SystemActivity.class.getName(), "Debug version. Enabling debug settings.");
            fileDumpEnabledCardView.setVisibility(View.VISIBLE);
            PreferenceManager preferenceManager = new PreferenceManager(this);
            fileDumpEnabledSwitch.setOnCheckedChangeListener(null);
            fileDumpEnabledSwitch.setChecked(preferenceManager.getPreferenceFileDumpEnabled());
            fileDumpEnabledSwitch.setOnCheckedChangeListener(this::onFileDumpEnabledCheckedChanged);
            prepareFileDumpEnabledOnOffText();
        } else {
            Log.d(SystemActivity.class.getName(), "Release version. Disabling debug settings.");
            fileDumpEnabledCardView.setVisibility(View.GONE);
        }
    }

    private void prepareFileDumpEnabledOnOffText() {
        fileDumpEnabledOnOffText.setText(fileDumpEnabledSwitch.isChecked() ? getResources().getString(R.string.string_yes) : getResources().getString(R.string.string_no));
    }

    private void onFileDumpEnabledCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.d(SystemActivity.class.getName(), "onFileDumpEnabledCheckedChanged, new value is " + isChecked);
        PreferenceManager preferenceManager = new PreferenceManager(this);
        preferenceManager.setPreferenceFileDumpEnabled(isChecked);
        prepareFileDumpEnabledOnOffText();
        if (isChecked && BuildConfig.DEBUG) {
            Dump.initialize(DebugUtil.getFileDump(this, getFileManager()));
        } else {
            Dump.initialize(null);
        }
    }

    private void prepareLogFolderField() {
        Log.d(SystemActivity.class.getName(), "prepareLogFolderField");
        CardView logFolderCardView = findViewById(R.id.cardview_activity_system_log_folder);
        logFolderText = findViewById(R.id.textview_activity_system_log_folder);
        logFolderText.setEnabled(false);
        if (BuildConfig.DEBUG) {
            Log.d(SystemActivity.class.getName(), "Debug version. Enabling debug settings.");
            logFolderCardView.setVisibility(View.VISIBLE);
            String logFolder = getExternalLogFolder();
            Log.d(SystemActivity.class.getName(), "External log folder is " + logFolder);
            if (logFolder == null) {
                Log.e(SystemActivity.class.getName(), "Error accessing log folder.");
                setLogFolder("");
                Log.d(SystemActivity.class.getName(), "Showing error dialog.");
                showMessageDialog(getResources().getString(R.string.text_dialog_general_message_external_root_access));
            } else {
                setLogFolder(logFolder);
            }
        } else {
            Log.d(SystemActivity.class.getName(), "Release version. Disabling debug settings.");
            logFolderCardView.setVisibility(View.GONE);
            setLogFolder("");
        }
    }

    private void prepareConfigurationExportFileLauncher() {
        Log.d(SystemActivity.class.getName(), "prepareConfigurationExportFileLauncher");
        boolean bypassSystemSAF = BundleUtil.booleanFromBundle(getBypassSystemSAFKey(), getIntent().getExtras());
        if (SystemUtil.supportsSAFFeature() && !bypassSystemSAF) {
            exportFileLauncher = new GenericPermissionLauncher(this, this::grantConfigurationExportFilePermission);
        } else {
            exportFileLauncher = new NullPermissionLauncher();
        }
    }

    private void prepareConfigurationImportFileLauncher() {
        Log.d(SystemActivity.class.getName(), "prepareConfigurationImportFileLauncher");
        boolean bypassSystemSAF = BundleUtil.booleanFromBundle(getBypassSystemSAFKey(), getIntent().getExtras());
        if (SystemUtil.supportsSAFFeature() && !bypassSystemSAF) {
            importFileLauncher = new GenericPermissionLauncher(this, this::grantConfigurationImportFilePermission);
        } else {
            importFileLauncher = new NullPermissionLauncher();
        }
    }

    private void setLogFolder(String logFolder) {
        logFolderText.setText(StringUtil.notNull(logFolder));
    }

    private void setExportFolder(String exportFolder) {
        exportFolderText.setText(StringUtil.notNull(exportFolder));
    }

    private void setImportFolder(String importFolder) {
        importFolderText.setText(StringUtil.notNull(importFolder));
    }

    private void showResetConfirmDialog(View view) {
        Log.d(SystemActivity.class.getName(), "showResetConfirmDialog");
        String message = getResources().getString(R.string.text_dialog_confirm_config_reset);
        String description = getResources().getString(R.string.text_dialog_confirm_config_reset_description);
        showConfirmDialog(message, description, ConfirmDialog.Type.RESETCONFIG);
    }

    private void showImportConfirmDialog(String file) {
        Log.d(SystemActivity.class.getName(), "showImportConfirmDialog");
        String message = getResources().getString(R.string.text_dialog_confirm_config_import);
        String description = getResources().getString(R.string.text_dialog_confirm_config_import_description);
        Bundle extraData = getFileExtraDataBundle(file);
        showConfirmDialog(message, description, ConfirmDialog.Type.IMPORTCONFIG, extraData);
    }

    private void showExportConfirmDialog(String file) {
        Log.d(SystemActivity.class.getName(), "showExportConfirmDialog");
        String message = getResources().getString(R.string.text_dialog_confirm_config_export_existing_file);
        String description = getResources().getString(R.string.text_dialog_confirm_config_export_existing_file_description);
        Bundle extraData = getFileExtraDataBundle(file);
        showConfirmDialog(message, description, ConfirmDialog.Type.EXPORTCONFIGEXISTINGFILE, extraData);
    }

    private Bundle getFileExtraDataBundle(String file) {
        return BundleUtil.stringToBundle(getFileExtraDataKey(), file);
    }

    private String getFileExtraDataKey() {
        return SystemActivity.class.getSimpleName() + "ImportExportFile";
    }

    private String getFileExtraData(ConfirmDialog confirmDialog) {
        Bundle bundle = confirmDialog.getExtraData();
        if (bundle == null) {
            return null;
        }
        return BundleUtil.stringFromBundle(getFileExtraDataKey(), bundle);
    }

    private void requestConfigurationExportFilePermission(View view) {
        Log.d(SystemActivity.class.getName(), "requestConfigurationExportFilePermission");
        String fileName = getResources().getString(R.string.export_file_prefix);
        getStoragePermissionManager().requestCreateFilePermission(exportFileLauncher, fileName);
    }

    private void requestConfigurationImportFilePermission(View view) {
        Log.d(SystemActivity.class.getName(), "requestConfigurationImportFilePermission");
        PreferenceManager preferenceManager = new PreferenceManager(this);
        getStoragePermissionManager().requestOpenFilePermission(importFileLauncher, preferenceManager.getPreferenceLastArbitraryExportFile());
    }

    public void grantConfigurationExportFilePermission(Uri uri) {
        Log.d(SystemActivity.class.getName(), "grantConfigurationExportFilePermission for uri " + uri);
        if (uri == null) {
            Log.e(SystemActivity.class.getName(), "uri is null");
            return;
        }
        PreferenceManager preferenceManager = new PreferenceManager(this);
        preferenceManager.setPreferenceLastArbitraryExportFile(uri.toString());
        doConfigurationExport(null, uri.toString());
    }

    public void grantConfigurationImportFilePermission(Uri uri) {
        Log.d(SystemActivity.class.getName(), "grantConfigurationImportFilePermission for uri " + uri);
        if (uri == null) {
            Log.e(SystemActivity.class.getName(), "uri is null");
            return;
        }
        showImportConfirmDialog(uri.toString());
    }

    private void showExportFolderChooseDialog(View view) {
        Log.d(SystemActivity.class.getName(), "showExportFolderChooseDialog");
        String root = getExternalRootFolder();
        Log.d(SystemActivity.class.getName(), "External root folder is " + root);
        String exportFolder = getPreferenceExportFolder();
        String file = "";
        if (exportFolder != null) {
            IFileManager fileManager = getFileManager();
            PreferenceManager preferenceManager = new PreferenceManager(this);
            File folder = FileUtil.getExternalDirectory(fileManager, preferenceManager, exportFolder);
            if (folder != null) {
                String fileName = getResources().getString(R.string.export_file_prefix) + getResources().getString(R.string.file_extension_json);
                file = StringUtil.notNull(fileManager.getValidFileName(folder, fileName));
            }
        }
        Log.d(SystemActivity.class.getName(), "Preference export folder is " + exportFolder);
        showImportExportFolderChooseDialog(root, exportFolder, file, FileChooseDialog.Type.EXPORTFOLDER);
    }

    private void showImportFolderChooseDialog(View view) {
        Log.d(SystemActivity.class.getName(), "showImportFolderChooseDialog");
        String root = getExternalRootFolder();
        Log.d(SystemActivity.class.getName(), "External root folder is " + root);
        String importFolder = getPreferenceImportFolder();
        Log.d(SystemActivity.class.getName(), "Preference import folder is " + importFolder);
        showImportExportFolderChooseDialog(root, importFolder, "", FileChooseDialog.Type.IMPORTFOLDER);
    }

    private void showImportExportFolderChooseDialog(String root, String folder, String file, FileChooseDialog.Type type) {
        Log.d(SystemActivity.class.getName(), "showImportExportFolderChooseDialog, root is " + root + ", folder is " + folder);
        FileChooseDialog fileChooseDialog = new FileChooseDialog();
        if (root == null || folder == null) {
            Log.e(SystemActivity.class.getName(), "Error accessing folder.");
            Log.d(SystemActivity.class.getName(), "Showing error dialog.");
            showMessageDialog(getResources().getString(R.string.text_dialog_general_message_external_root_access));
            return;
        }
        Bundle bundle = BundleUtil.stringsToBundle(new String[]{fileChooseDialog.getFolderRootKey(), fileChooseDialog.getFolderKey(), fileChooseDialog.getFileKey(), fileChooseDialog.getFileModeKey(), fileChooseDialog.getTypeKey()}, new String[]{root, folder, file, FileChooseDialog.Mode.FILE.name(), type.name()});
        fileChooseDialog.setArguments(bundle);
        fileChooseDialog.show(getSupportFragmentManager(), SystemActivity.class.getName());
    }

    @Override
    public void onFileChooseDialogOkClicked(FileChooseDialog fileChooseDialog, FileChooseDialog.Type type) {
        Log.d(SystemActivity.class.getName(), "onFileChooseDialogOkClicked, type is " + type);
        IFileManager fileManager = getFileManager();
        PreferenceManager preferenceManager = new PreferenceManager(this);
        if (FileChooseDialog.Type.IMPORTFOLDER.equals(type) || FileChooseDialog.Type.EXPORTFOLDER.equals(type)) {
            String folder = fileChooseDialog.getFolder();
            File importExportFolder = FileUtil.getExternalDirectory(fileManager, preferenceManager, folder);
            Log.d(SystemActivity.class.getName(), "External folder is " + importExportFolder);
            if (importExportFolder == null) {
                Log.e(SystemActivity.class.getName(), "Error accessing folder.");
                fileChooseDialog.dismiss();
                Log.d(SystemActivity.class.getName(), "Showing error dialog.");
                showMessageDialog(getResources().getString(R.string.text_dialog_general_message_external_import_export_create));
                return;
            }
            String file = fileChooseDialog.getFile();
            if (FileChooseDialog.Type.IMPORTFOLDER.equals(type)) {
                preferenceManager.setPreferenceImportFolder(folder);
                setImportFolder(importExportFolder.getAbsolutePath());
                showImportConfirmDialog(file);
            } else {
                preferenceManager.setPreferenceExportFolder(folder);
                setExportFolder(importExportFolder.getAbsolutePath());
                if (fileManager.doesFileExist(importExportFolder, file)) {
                    showExportConfirmDialog(file);
                } else {
                    fileChooseDialog.dismiss();
                    doConfigurationExport(importExportFolder, file);
                }
            }
        } else {
            Log.e(SystemActivity.class.getName(), "Unknown type " + type);
            fileChooseDialog.dismiss();
        }
    }

    private void doConfigurationExport(File exportFolder, String file) {
        Log.d(SystemActivity.class.getName(), "doConfigurationExport");
        Log.d(SystemActivity.class.getName(), "Export folder is " + exportFolder);
        Log.d(SystemActivity.class.getName(), "File name is " + file);
        if (StringUtil.isEmpty(file)) {
            Log.e(SystemActivity.class.getName(), "Folder or file is empty.");
            showMessageDialog(getResources().getString(R.string.text_dialog_general_message_config_export));
            return;
        }
        showProgressDialog();
        ExportTask exportTask = getExportTask(exportFolder, file);
        Future<Boolean> exportFuture = ThreadUtil.execute(exportTask);
        boolean synchronousExecution = getResources().getBoolean(R.bool.uisync_synchronous_execution);
        if (synchronousExecution) {
            try {
                int timeout = getResources().getInteger(R.integer.export_timeout);
                exportFuture.get(timeout, TimeUnit.MILLISECONDS);
            } catch (Exception exc) {
                Log.e(SystemActivity.class.getName(), "Error waiting for export execution", exc);
                closeProgressDialog();
            }
        }
    }

    private void doConfigurationImport(File importFolder, String file) {
        Log.d(SystemActivity.class.getName(), "doConfigurationImport");
        Log.d(SystemActivity.class.getName(), "Import folder is " + importFolder);
        Log.d(SystemActivity.class.getName(), "File name is " + file);
        if (StringUtil.isEmpty(file)) {
            Log.e(SystemActivity.class.getName(), "Folder or file is empty.");
            showMessageDialog(getResources().getString(R.string.text_dialog_general_message_config_import));
            return;
        }
        showProgressDialog();
        ImportTask importTask = getImportTask(importFolder, file);
        Future<SystemSetupResult> importFuture = ThreadUtil.execute(importTask);
        boolean synchronousExecution = getResources().getBoolean(R.bool.uisync_synchronous_execution);
        if (synchronousExecution) {
            try {
                int dropTableRetry = getResources().getInteger(R.integer.drop_table_retry_count);
                int dropTableTimeout = getResources().getInteger(R.integer.drop_table_timeout);
                int deleteTableRetry = getResources().getInteger(R.integer.delete_table_retry_count);
                int deleteTableTimeout = getResources().getInteger(R.integer.delete_table_timeout);
                int importTimeout = getResources().getInteger(R.integer.import_timeout);
                int timeout = importTimeout + (dropTableRetry * dropTableTimeout + deleteTableRetry * deleteTableTimeout) * 2;
                importFuture.get(timeout, TimeUnit.MILLISECONDS);
            } catch (Exception exc) {
                Log.e(SystemActivity.class.getName(), "Error waiting for import execution", exc);
                closeProgressDialog();
            }
        }
    }

    @Override
    public void onConfirmDialogOkClicked(ConfirmDialog confirmDialog, ConfirmDialog.Type type) {
        Log.d(SystemActivity.class.getName(), "onConfirmDialogOkClicked for type " + type);
        if (ConfirmDialog.Type.RESETCONFIG.equals(type)) {
            stopSchedulers();
            dismissConfirmDialog(confirmDialog);
            showProgressDialog();
            purgeDatabase();
        } else if (ConfirmDialog.Type.EXPORTCONFIGEXISTINGFILE.equals(type)) {
            IFileManager fileManager = getFileManager();
            PreferenceManager preferenceManager = new PreferenceManager(this);
            File exportFolder = FileUtil.getExternalDirectory(fileManager, preferenceManager, preferenceManager.getPreferenceExportFolder());
            String file = getFileExtraData(confirmDialog);
            dismissConfirmDialog(confirmDialog);
            doConfigurationExport(exportFolder, file);
        } else if (ConfirmDialog.Type.IMPORTCONFIG.equals(type)) {
            IFileManager fileManager = getFileManager();
            PreferenceManager preferenceManager = new PreferenceManager(this);
            File importFolder = null;
            String file = getFileExtraData(confirmDialog);
            if (!preferenceManager.getPreferenceAllowArbitraryFileLocation()) {
                importFolder = FileUtil.getExternalDirectory(fileManager, preferenceManager, preferenceManager.getPreferenceImportFolder());
            }
            stopSchedulers();
            dismissConfirmDialog(confirmDialog);
            doConfigurationImport(importFolder, file);
        } else {
            Log.e(SystemActivity.class.getName(), "Unknown type " + type);
        }
    }

    private void dismissConfirmDialog(ConfirmDialog confirmDialog) {
        Log.d(SystemActivity.class.getName(), "dismissConfirmDialog");
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for (Fragment fragment : fragments) {
            if (fragment instanceof FileChooseDialog) {
                try {
                    ((FileChooseDialog) fragment).dismiss();
                } catch (Exception exc) {
                    Log.d(SystemActivity.class.getName(), "Error closing FileChooseDialog", exc);
                }
            }
        }
        confirmDialog.dismiss();
    }

    private void stopSchedulers() {
        Log.d(SystemActivity.class.getName(), "stopSchedulers");
        getNetworkTaskProcessServiceScheduler().cancelAll();
        getTimeBasedSuspensionScheduler().stop();
        getTimeBasedSuspensionScheduler().reset();
    }

    @Override
    public void onMessageDialogOkClicked(GeneralMessageDialog errorDialog) {
        Log.d(SystemActivity.class.getName(), "onErrorDialogOkClicked");
        String extraData = errorDialog.getExtraData();
        Log.d(SystemActivity.class.getName(), "onErrorDialogOkClicked, extraData is " + extraData);
        errorDialog.dismiss();
        if (Error.IMPORTERROR.name().equals(extraData) || Error.PURGERROR.name().equals(extraData)) {
            resetActivity();
        }
    }

    @Override
    public void onExportDone(boolean success) {
        Log.d(SystemActivity.class.getName(), "onExportDone, success is " + success);
        closeProgressDialog();
        if (!success) {
            showMessageDialog(getResources().getString(R.string.text_dialog_general_message_config_export));
        }
    }

    @Override
    public void onImportDone(boolean success, String message) {
        Log.d(SystemActivity.class.getName(), "onImportDone, success is " + success);
        closeProgressDialog();
        getTimeBasedSuspensionScheduler().restart();
        if (success) {
            NetworkTaskLog.clear();
            resetActivity();
        } else {
            showMessageDialog(message != null ? message : getResources().getString(R.string.text_dialog_general_message_config_import), Typeface.BOLD, Error.IMPORTERROR.name());
        }
    }

    @Override
    public void onPurgeDone(boolean success) {
        Log.d(SystemActivity.class.getName(), "onPurgeDone, success is " + success);
        closeProgressDialog();
        getTimeBasedSuspensionScheduler().restart();
        if (success) {
            resetPreferences();
            resetFolderPermissions();
            NetworkTaskLog.clear();
            resetActivity();
        } else {
            showMessageDialog(getResources().getString(R.string.text_dialog_general_message_db_purge), Typeface.BOLD, Error.PURGERROR.name());
        }
    }

    private void resetPreferences() {
        Log.d(SystemActivity.class.getName(), "resetPreferences");
        PreferenceSetup preferenceSetup = new PreferenceSetup(this);
        preferenceSetup.removeAllSettings();
    }

    @SuppressWarnings("RedundantIfStatement")
    private boolean checkFolderPermissions() {
        Log.d(SystemActivity.class.getName(), "checkFolderPermissions");
        PreferenceManager preferenceManager = new PreferenceManager(this);
        IStoragePermissionManager storagePermissionManager = getStoragePermissionManager();
        if (!storagePermissionManager.hasPersistentPermission(this, preferenceManager.getPreferenceArbitraryLogFolder())) {
            return false;
        }
        if (!storagePermissionManager.hasPersistentPermission(this, preferenceManager.getPreferenceArbitraryDownloadFolder())) {
            return false;
        }
        return true;
    }

    private void resetFolderPermissions() {
        Log.d(SystemActivity.class.getName(), "resetFolderPermissions");
        IStoragePermissionManager storagePermissionManager = getStoragePermissionManager();
        storagePermissionManager.revokeAllPersistentPermissions(this);
    }

    private void resetActivity() {
        Log.d(SystemActivity.class.getName(), "resetActivity");
        resetTheme();
        recreateActivity();
    }

    private void resetTheme() {
        Log.d(SystemActivity.class.getName(), "resetTheme");
        IThemeManager themeManager = getThemeManager();
        PreferenceManager preferenceManager = new PreferenceManager(this);
        int themeCode = preferenceManager.getPreferenceTheme();
        Log.d(StartupService.class.getName(), "theme is " + themeManager.getThemeName(themeCode));
        themeManager.setThemeByCode(themeCode);
    }

    protected void purgeDatabase() {
        Log.d(SystemActivity.class.getName(), "purgeDatabase");
        DBPurgeTask purgeTask = getPurgeTask();
        Future<Boolean> purgeFuture = ThreadUtil.execute(purgeTask);
        boolean synchronousExecution = getResources().getBoolean(R.bool.uisync_synchronous_execution);
        if (synchronousExecution) {
            try {
                int dropTableRetry = getResources().getInteger(R.integer.drop_table_retry_count);
                int dropTableTimeout = getResources().getInteger(R.integer.drop_table_timeout);
                int deleteTableRetry = getResources().getInteger(R.integer.delete_table_retry_count);
                int deleteTableTimeout = getResources().getInteger(R.integer.delete_table_timeout);
                int timeout = (dropTableRetry * dropTableTimeout + deleteTableRetry * deleteTableTimeout) * 2;
                purgeFuture.get(timeout, TimeUnit.MILLISECONDS);
            } catch (Exception exc) {
                Log.e(SystemActivity.class.getName(), "Error waiting for purge execution", exc);
                closeProgressDialog();
            }
        }
    }

    private String getExternalLogFolder() {
        Log.d(SystemActivity.class.getName(), "getExternalLogFolder");
        String folder = getResources().getString(R.string.debug_file_logger_log_directory_default);
        IFileManager fileManager = getFileManager();
        File logFolder = fileManager.getExternalDirectory(folder, 0);
        Log.d(SystemActivity.class.getName(), "External log folder is " + logFolder);
        if (logFolder == null) {
            return null;
        }
        return logFolder.getAbsolutePath();
    }

    private String getExternalRootFolder() {
        Log.d(SystemActivity.class.getName(), "getExternalRootFolder");
        PreferenceManager preferenceManager = new PreferenceManager(this);
        IFileManager fileManager = getFileManager();
        File root = FileUtil.getExternalRootDirectory(fileManager, preferenceManager);
        Log.d(SystemActivity.class.getName(), "External root folder is " + root);
        if (root == null) {
            return null;
        }
        return root.getAbsolutePath();
    }

    private String getPreferenceExportFolder() {
        Log.d(SystemActivity.class.getName(), "getPreferenceExportFolder");
        PreferenceManager preferenceManager = new PreferenceManager(this);
        String exportFolder = preferenceManager.getPreferenceExportFolder();
        String exportFolderAbsolute = getExternalImportExportFolder(exportFolder);
        if (exportFolderAbsolute == null) {
            return null;
        }
        return exportFolder;
    }

    private String getPreferenceImportFolder() {
        Log.d(SystemActivity.class.getName(), "getPreferenceImportFolder");
        PreferenceManager preferenceManager = new PreferenceManager(this);
        String importFolder = preferenceManager.getPreferenceImportFolder();
        String importFolderAbsolute = getExternalImportExportFolder(importFolder);
        if (importFolderAbsolute == null) {
            return null;
        }
        return importFolder;
    }

    private String getExternalImportExportFolder(String preferenceFolder) {
        Log.d(SystemActivity.class.getName(), "getExternalImportExportFolder, preferenceFolder is " + preferenceFolder);
        PreferenceManager preferenceManager = new PreferenceManager(this);
        IFileManager fileManager = getFileManager();
        File importExportFolder = FileUtil.getExternalDirectory(fileManager, preferenceManager, preferenceFolder);
        Log.d(SystemActivity.class.getName(), "External import/export folder is " + importExportFolder);
        if (importExportFolder == null) {
            return null;
        }
        return importExportFolder.getAbsolutePath();
    }

    private ExportTask getExportTask(File folder, String file) {
        if (exportTask != null) {
            return exportTask;
        }
        PreferenceManager preferenceManager = new PreferenceManager(this);
        return new ExportTask(this, folder, file, preferenceManager.getPreferenceAllowArbitraryFileLocation());
    }

    private ImportTask getImportTask(File folder, String file) {
        if (importTask != null) {
            return importTask;
        }
        PreferenceManager preferenceManager = new PreferenceManager(this);
        return new ImportTask(this, folder, file, preferenceManager.getPreferenceAllowArbitraryFileLocation());
    }

    private DBPurgeTask getPurgeTask() {
        if (purgeTask != null) {
            return purgeTask;
        }
        return new DBPurgeTask(this);
    }

    @SuppressWarnings({"ReplaceNullCheck"})
    private NetworkTaskProcessServiceScheduler getNetworkTaskProcessServiceScheduler() {
        if (networkTaskProcessServiceScheduler != null) {
            return networkTaskProcessServiceScheduler;
        }
        return new NetworkTaskProcessServiceScheduler(this);
    }

    private IThemeManager getThemeManager() {
        if (themeManager != null) {
            return themeManager;
        }
        return new SystemThemeManager();
    }
}
