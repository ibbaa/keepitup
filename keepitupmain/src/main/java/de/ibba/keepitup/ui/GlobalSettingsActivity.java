package de.ibba.keepitup.ui;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.google.android.material.switchmaterial.SwitchMaterial;

import java.io.File;
import java.util.Collections;
import java.util.List;

import de.ibba.keepitup.BuildConfig;
import de.ibba.keepitup.R;
import de.ibba.keepitup.logging.Dump;
import de.ibba.keepitup.logging.Log;
import de.ibba.keepitup.resources.PreferenceManager;
import de.ibba.keepitup.service.IFileManager;
import de.ibba.keepitup.service.IPowerManager;
import de.ibba.keepitup.ui.dialog.BatteryOptimizationDialog;
import de.ibba.keepitup.ui.dialog.FolderChooseDialog;
import de.ibba.keepitup.ui.dialog.SettingsInput;
import de.ibba.keepitup.ui.dialog.SettingsInputDialog;
import de.ibba.keepitup.ui.validation.ConnectCountFieldValidator;
import de.ibba.keepitup.ui.validation.PingCountFieldValidator;
import de.ibba.keepitup.util.BundleUtil;
import de.ibba.keepitup.util.DebugUtil;
import de.ibba.keepitup.util.NumberUtil;
import de.ibba.keepitup.util.StringUtil;

public class GlobalSettingsActivity extends SettingsInputActivity {

    private TextView pingCountText;
    private TextView connectCountText;
    private SwitchMaterial notificationInactiveNetworkSwitch;
    private TextView notificationInactiveNetworkOnOffText;
    private SwitchMaterial downloadExternalStorageSwitch;
    private RadioGroup externalStorageType;
    private TextView downloadExternalStorageOnOffText;
    private TextView downloadFolderText;
    private SwitchMaterial downloadKeepSwitch;
    private TextView batteryOptimizationText;
    private TextView downloadKeepOnOffText;
    private SwitchMaterial fileLoggerEnabledSwitch;
    private TextView fileLoggerEnabledOnOffText;
    private SwitchMaterial fileDumpEnabledSwitch;
    private TextView fileDumpEnabledOnOffText;
    private TextView logFolderText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setContentView(R.layout.activity_global_settings);
        preparePingCountField();
        prepareConnectCountField();
        prepareNotificationInactiveNetworkSwitch();
        prepareDownloadExternalStorageSwitch();
        prepareExternalStorageTypeRadioGroup();
        prepareDownloadFolderField();
        prepareDownloadKeepSwitch();
        prepareBatteryOptimizationField();
        prepareDebugSettingsLabel();
        prepareFileLoggerEnabledSwitch();
        prepareFileDumpEnabledSwitch();
        prepareLogFolderField();
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
            Log.d(GlobalSettingsActivity.class.getName(), "menu_action_reset triggered");
            PreferenceManager preferenceManager = new PreferenceManager(this);
            preferenceManager.removePreferencePingCount();
            preferenceManager.removePreferenceConnectCount();
            preferenceManager.removePreferenceNotificationInactiveNetwork();
            preferenceManager.removePreferenceDownloadExternalStorage();
            preferenceManager.removePreferenceExternalStorageType();
            preferenceManager.removePreferenceDownloadFolder();
            preferenceManager.removePreferenceDownloadKeep();
            preferenceManager.removePreferenceFileLoggerEnabled();
            preferenceManager.removePreferenceFileDumpEnabled();
            recreateActivity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void preparePingCountField() {
        Log.d(GlobalSettingsActivity.class.getName(), "preparePingCountField");
        PreferenceManager preferenceManager = new PreferenceManager(this);
        pingCountText = findViewById(R.id.textview_activity_global_settings_ping_count);
        setPingCount(String.valueOf(preferenceManager.getPreferencePingCount()));
        CardView pingCountCardView = findViewById(R.id.cardview_activity_global_settings_ping_count);
        pingCountCardView.setOnClickListener(this::showPingCountInputDialog);
    }

    private void prepareConnectCountField() {
        Log.d(GlobalSettingsActivity.class.getName(), "prepareConnectCountField");
        PreferenceManager preferenceManager = new PreferenceManager(this);
        connectCountText = findViewById(R.id.textview_activity_global_settings_connect_count);
        setConnectCount(String.valueOf(preferenceManager.getPreferenceConnectCount()));
        CardView connectCountCardView = findViewById(R.id.cardview_activity_global_settings_connect_count);
        connectCountCardView.setOnClickListener(this::showConnectCountInputDialog);
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

    private void prepareDownloadExternalStorageOnOffText() {
        downloadExternalStorageOnOffText.setText(downloadExternalStorageSwitch.isChecked() ? getResources().getString(R.string.string_yes) : getResources().getString(R.string.string_no));
    }

    private void onDownloadExternalStorageCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.d(GlobalSettingsActivity.class.getName(), "onDownloadExternalStorageCheckedChanged, new value is " + isChecked);
        PreferenceManager preferenceManager = new PreferenceManager(this);
        preferenceManager.setPreferenceDownloadExternalStorage(isChecked);
        prepareDownloadExternalStorageOnOffText();
        prepareExternalStorageTypeRadioGroup();
        prepareDownloadFolderField();
        prepareDownloadKeepSwitch();
    }

    private void prepareExternalStorageTypeRadioGroup() {
        Log.d(GlobalSettingsActivity.class.getName(), "prepareExternalStorageTypeRadioGroup");
        externalStorageType = findViewById(R.id.radiogroup_activity_global_settings_external_storage_type);
        IFileManager fileManager = getFileManager();
        boolean sdCardSupported = fileManager.isSDCardSupported();
        Log.d(GlobalSettingsActivity.class.getName(), "SD card supported: " + sdCardSupported);
        if (downloadExternalStorageSwitch.isChecked() && sdCardSupported) {
            RadioButton primaryStorageTypeButton = findViewById(R.id.radiobutton_activity_global_settings_external_storage_type_primary);
            RadioButton sdCardStorageTypeButton = findViewById(R.id.radiobutton_activity_global_settings_external_storage_type_sdcard);
            sdCardStorageTypeButton.setVisibility(View.VISIBLE);
            PreferenceManager preferenceManager = new PreferenceManager(this);
            int externalStorage = preferenceManager.getPreferenceExternalStorageType();
            Log.d(GlobalSettingsActivity.class.getName(), "externalStorage is " + externalStorage);
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
            RadioButton primaryStorageTypeButton = findViewById(R.id.radiobutton_activity_global_settings_external_storage_type_primary);
            RadioButton sdCardStorageTypeButton = findViewById(R.id.radiobutton_activity_global_settings_external_storage_type_sdcard);
            primaryStorageTypeButton.setChecked(true);
            sdCardStorageTypeButton.setChecked(false);
            externalStorageType.setEnabled(false);
            primaryStorageTypeButton.setEnabled(false);
            sdCardStorageTypeButton.setEnabled(false);
            if (!fileManager.isSDCardSupported()) {
                sdCardStorageTypeButton.setVisibility(View.GONE);
            }
        }
    }

    private void onExternalStorageTypeChanged(RadioGroup group, int checkedId) {
        Log.d(GlobalSettingsActivity.class.getName(), "onExternalStorageTypeChanged");
        PreferenceManager preferenceManager = new PreferenceManager(this);
        int checkedExternalStorage = group.getCheckedRadioButtonId();
        if (R.id.radiobutton_activity_global_settings_external_storage_type_sdcard == checkedExternalStorage) {
            Log.d(GlobalSettingsActivity.class.getName(), "SD card selected as external storage type");
            preferenceManager.setPreferenceExternalStorageType(1);
        } else {
            Log.d(GlobalSettingsActivity.class.getName(), "Primary selected as external storage type");
            preferenceManager.setPreferenceExternalStorageType(0);
        }
        prepareDownloadFolderField();
    }

    private void prepareDownloadFolderField() {
        Log.d(GlobalSettingsActivity.class.getName(), "prepareDownloadFolderField");
        PreferenceManager preferenceManager = new PreferenceManager(this);
        CardView downloadFolderCardView = findViewById(R.id.cardview_activity_global_settings_download_folder);
        downloadFolderText = findViewById(R.id.textview_activity_global_settings_download_folder);
        if (downloadExternalStorageSwitch.isChecked()) {
            String downloadFolder = getExternalDownloadFolder();
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
        } else {
            setDownloadFolder(getResources().getString(R.string.text_activity_global_settings_download_folder_internal));
            downloadFolderCardView.setEnabled(false);
            downloadFolderCardView.setOnClickListener(null);
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

    private void prepareBatteryOptimizationField() {
        Log.d(GlobalSettingsActivity.class.getName(), "prepareBatteryOptimizationField");
        CardView batteryOptimizationCardView = findViewById(R.id.cardview_activity_global_settings_battery_optimization);
        batteryOptimizationText = findViewById(R.id.textview_activity_global_settings_battery_optimization);
        IPowerManager powerManager = getPowerManager();
        if (powerManager.isBatteryOptimized()) {
            Log.d(GlobalSettingsActivity.class.getName(), "Battery optimization is active");
            batteryOptimizationText.setText(R.string.string_active);
        } else {
            Log.d(GlobalSettingsActivity.class.getName(), "Battery optimization is inactive");
            batteryOptimizationText.setText(R.string.string_inactive);
        }
        if (powerManager.supportsBatteryOptimization()) {
            Log.d(GlobalSettingsActivity.class.getName(), "Battery optimization is supported");
            batteryOptimizationCardView.setOnClickListener(this::showBatteryOptimizationDialog);
            batteryOptimizationCardView.setEnabled(true);
        } else {
            Log.d(GlobalSettingsActivity.class.getName(), "Battery optimization is not supported");
            batteryOptimizationCardView.setOnClickListener(null);
            batteryOptimizationCardView.setEnabled(false);
        }
    }

    private void prepareDebugSettingsLabel() {
        Log.d(GlobalSettingsActivity.class.getName(), "prepareDebugSettingsLabel");
        CardView debugSettingsCardView = findViewById(R.id.cardview_activity_global_settings_debug);
        if (BuildConfig.DEBUG) {
            Log.d(GlobalSettingsActivity.class.getName(), "Debug version. Enabling debug settings.");
            debugSettingsCardView.setVisibility(View.VISIBLE);
        } else {
            Log.d(GlobalSettingsActivity.class.getName(), "Release version. Enabling debug settings.");
            debugSettingsCardView.setVisibility(View.GONE);
        }
    }

    private void prepareFileLoggerEnabledSwitch() {
        Log.d(GlobalSettingsActivity.class.getName(), "prepareFileLoggerEnabled");
        CardView fileLoggerEnabledCardView = findViewById(R.id.cardview_activity_global_settings_file_logger_enabled);
        fileLoggerEnabledSwitch = findViewById(R.id.switch_activity_global_settings_file_logger_enabled);
        fileLoggerEnabledOnOffText = findViewById(R.id.textview_activity_global_settings_file_logger_enabled_on_off);
        if (BuildConfig.DEBUG) {
            Log.d(GlobalSettingsActivity.class.getName(), "Debug version. Enabling debug settings.");
            fileLoggerEnabledCardView.setVisibility(View.VISIBLE);
            PreferenceManager preferenceManager = new PreferenceManager(this);
            fileLoggerEnabledSwitch.setOnCheckedChangeListener(null);
            fileLoggerEnabledSwitch.setChecked(preferenceManager.getPreferenceFileLoggerEnabled());
            fileLoggerEnabledSwitch.setOnCheckedChangeListener(this::onFileLoggerEnabledCheckedChanged);
            prepareFileLoggerEnabledOnOffText();
        } else {
            Log.d(GlobalSettingsActivity.class.getName(), "Release version. Enabling debug settings.");
            fileLoggerEnabledCardView.setVisibility(View.GONE);
        }
    }

    private void prepareFileLoggerEnabledOnOffText() {
        fileLoggerEnabledOnOffText.setText(fileLoggerEnabledSwitch.isChecked() ? getResources().getString(R.string.string_yes) : getResources().getString(R.string.string_no));
    }

    private void onFileLoggerEnabledCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.d(GlobalSettingsActivity.class.getName(), "onFileLoggerEnabledCheckedChanged, new value is " + isChecked);
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
        Log.d(GlobalSettingsActivity.class.getName(), "prepareFileDumpEnabledSwitch");
        CardView fileDumpEnabledCardView = findViewById(R.id.cardview_activity_global_settings_file_logger_enabled);
        fileDumpEnabledSwitch = findViewById(R.id.switch_activity_global_settings_file_dump_enabled);
        fileDumpEnabledOnOffText = findViewById(R.id.textview_activity_global_settings_file_dump_enabled_on_off);
        if (BuildConfig.DEBUG) {
            Log.d(GlobalSettingsActivity.class.getName(), "Debug version. Enabling debug settings.");
            fileDumpEnabledCardView.setVisibility(View.VISIBLE);
            PreferenceManager preferenceManager = new PreferenceManager(this);
            fileDumpEnabledSwitch.setOnCheckedChangeListener(null);
            fileDumpEnabledSwitch.setChecked(preferenceManager.getPreferenceFileDumpEnabled());
            fileDumpEnabledSwitch.setOnCheckedChangeListener(this::onFileDumpEnabledCheckedChanged);
            prepareFileDumpEnabledOnOffText();
        } else {
            Log.d(GlobalSettingsActivity.class.getName(), "Release version. Disabling debug settings.");
            fileDumpEnabledCardView.setVisibility(View.GONE);
        }
    }

    private void prepareFileDumpEnabledOnOffText() {
        fileDumpEnabledOnOffText.setText(fileDumpEnabledSwitch.isChecked() ? getResources().getString(R.string.string_yes) : getResources().getString(R.string.string_no));
    }

    private void onFileDumpEnabledCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.d(GlobalSettingsActivity.class.getName(), "onFileDumpEnabledCheckedChanged, new value is " + isChecked);
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
        Log.d(GlobalSettingsActivity.class.getName(), "prepareLogFolderField");
        CardView logFolderCardView = findViewById(R.id.cardview_activity_global_settings_log_folder);
        logFolderText = findViewById(R.id.textview_activity_global_settings_log_folder);
        logFolderText.setEnabled(false);
        if (BuildConfig.DEBUG) {
            Log.d(GlobalSettingsActivity.class.getName(), "Debug version. Enabling debug settings.");
            logFolderCardView.setVisibility(View.VISIBLE);
            String logFolder = getExternalLogFolder();
            Log.d(GlobalSettingsActivity.class.getName(), "External log folder is " + logFolder);
            if (logFolder == null) {
                Log.e(GlobalSettingsActivity.class.getName(), "Error accessing log folder.");
                setLogFolder("");
                Log.d(GlobalSettingsActivity.class.getName(), "Showing error dialog.");
                showErrorDialog(getResources().getString(R.string.text_dialog_general_error_external_root_access));
            } else {
                setLogFolder(logFolder);
            }
        } else {
            Log.d(GlobalSettingsActivity.class.getName(), "Release version. Diabling debug settings.");
            logFolderCardView.setVisibility(View.GONE);
            setLogFolder("");
        }
    }

    private String getPingCount() {
        return StringUtil.notNull(pingCountText.getText());
    }

    private void setPingCount(String pingCount) {
        pingCountText.setText(StringUtil.notNull(pingCount));
    }

    private String getConnectCount() {
        return StringUtil.notNull(connectCountText.getText());
    }

    private void setConnectCount(String connectCount) {
        connectCountText.setText(StringUtil.notNull(connectCount));
    }

    private String getDownloadFolder() {
        return StringUtil.notNull(downloadFolderText.getText());
    }

    private void setDownloadFolder(String downloadFolder) {
        downloadFolderText.setText(StringUtil.notNull(downloadFolder));
    }

    private void setLogFolder(String logFolder) {
        logFolderText.setText(StringUtil.notNull(logFolder));
    }

    private void showPingCountInputDialog(View view) {
        Log.d(GlobalSettingsActivity.class.getName(), "showPingCountInputDialog");
        List<String> validators = Collections.singletonList(PingCountFieldValidator.class.getName());
        SettingsInput input = new SettingsInput(SettingsInput.Type.PINGCOUNT, getPingCount(), getResources().getString(R.string.label_activity_global_settings_ping_count), validators);
        showInputDialog(input.toBundle());
    }

    private void showConnectCountInputDialog(View view) {
        Log.d(GlobalSettingsActivity.class.getName(), "showConnectCountInputDialog");
        List<String> validators = Collections.singletonList(ConnectCountFieldValidator.class.getName());
        SettingsInput input = new SettingsInput(SettingsInput.Type.CONNECTCOUNT, getConnectCount(), getResources().getString(R.string.label_activity_global_settings_connect_count), validators);
        showInputDialog(input.toBundle());
    }

    private void showDownloadFolderChooseDialog(View view) {
        Log.d(DefaultsActivity.class.getName(), "showDownloadFolderChooseDialog");
        FolderChooseDialog folderChooseDialog = new FolderChooseDialog();
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
        Bundle bundle = BundleUtil.stringsToBundle(new String[]{folderChooseDialog.getFolderRootKey(), folderChooseDialog.getFolderKey()}, new String[]{root, folder});
        bundle = BundleUtil.booleanToBundle(folderChooseDialog.getSupportsFileSelectionKey(), false, bundle);
        folderChooseDialog.setArguments(bundle);
        folderChooseDialog.show(getSupportFragmentManager(), GlobalSettingsActivity.class.getName());

    }

    private void showInputDialog(Bundle bundle) {
        Log.d(GlobalSettingsActivity.class.getName(), "showInputDialog, opening SettingsInputDialog");
        SettingsInputDialog inputDialog = new SettingsInputDialog();
        inputDialog.setArguments(bundle);
        inputDialog.show(getSupportFragmentManager(), GlobalSettingsActivity.class.getName());
    }

    private void showBatteryOptimizationDialog(View view) {
        Log.d(GlobalSettingsActivity.class.getName(), "showBatteryOptimizationDialog");
        BatteryOptimizationDialog batteryOptimizationDialog = new BatteryOptimizationDialog();
        batteryOptimizationDialog.show(getSupportFragmentManager(), BatteryOptimizationDialog.class.getName());
    }

    private String getExternalRootFolder() {
        Log.d(GlobalSettingsActivity.class.getName(), "getExternalRootFolder");
        PreferenceManager preferenceManager = new PreferenceManager(this);
        IFileManager fileManager = getFileManager();
        File root;
        if (fileManager.isSDCardSupported()) {
            Log.d(GlobalSettingsActivity.class.getName(), "SD card is supported");
            root = fileManager.getExternalRootDirectory(preferenceManager.getPreferenceExternalStorageType());
        } else {
            Log.d(GlobalSettingsActivity.class.getName(), "SD card is not supported");
            root = fileManager.getExternalRootDirectory(0);
        }
        Log.d(GlobalSettingsActivity.class.getName(), "External root folder is " + root);
        if (root == null) {
            return null;
        }
        return root.getAbsolutePath();
    }

    private String getPreferenceDownloadFolder() {
        Log.d(GlobalSettingsActivity.class.getName(), "getPreferenceDownloadFolder");
        String downloadFolder = getExternalDownloadFolder();
        if (downloadFolder == null) {
            return null;
        }
        PreferenceManager preferenceManager = new PreferenceManager(this);
        return preferenceManager.getPreferenceDownloadFolder();
    }

    private String getExternalDownloadFolder() {
        Log.d(GlobalSettingsActivity.class.getName(), "getExternalDownloadFolder");
        PreferenceManager preferenceManager = new PreferenceManager(this);
        String folder = preferenceManager.getPreferenceDownloadFolder();
        IFileManager fileManager = getFileManager();
        File downloadFolder;
        if (fileManager.isSDCardSupported()) {
            Log.d(GlobalSettingsActivity.class.getName(), "SD card is supported");
            downloadFolder = fileManager.getExternalDirectory(folder, preferenceManager.getPreferenceExternalStorageType());
        } else {
            Log.d(GlobalSettingsActivity.class.getName(), "SD card is not supported");
            downloadFolder = fileManager.getExternalDirectory(folder, 0);
        }
        Log.d(GlobalSettingsActivity.class.getName(), "External download folder is " + downloadFolder);
        if (downloadFolder == null) {
            return null;
        }
        return downloadFolder.getAbsolutePath();
    }

    private String getExternalLogFolder() {
        Log.d(GlobalSettingsActivity.class.getName(), "getExternalLogFolder");
        PreferenceManager preferenceManager = new PreferenceManager(this);
        String folder = getResources().getString(R.string.file_logger_log_directory_default);
        IFileManager fileManager = getFileManager();
        File logFolder = fileManager.getExternalDirectory(folder, 0);
        Log.d(GlobalSettingsActivity.class.getName(), "External log folder is " + logFolder);
        if (logFolder == null) {
            return null;
        }
        return logFolder.getAbsolutePath();
    }

    @Override
    public void onInputDialogOkClicked(SettingsInputDialog inputDialog, SettingsInput.Type type) {
        Log.d(GlobalSettingsActivity.class.getName(), "onInputDialogOkClicked, type is " + type + ", value is " + inputDialog.getValue());
        PreferenceManager preferenceManager = new PreferenceManager(this);
        if (SettingsInput.Type.PINGCOUNT.equals(type)) {
            setPingCount(inputDialog.getValue());
            preferenceManager.setPreferencePingCount(NumberUtil.getIntValue(getPingCount(), getResources().getInteger(R.integer.ping_count_default)));
        } else if (SettingsInput.Type.CONNECTCOUNT.equals(type)) {
            setConnectCount(inputDialog.getValue());
            preferenceManager.setPreferenceConnectCount(NumberUtil.getIntValue(getConnectCount(), getResources().getInteger(R.integer.connect_count_default)));
        } else {
            Log.e(GlobalSettingsActivity.class.getName(), "type " + type + " unknown");
        }
        inputDialog.dismiss();
    }

    @Override
    public void onFolderChooseDialogOkClicked(FolderChooseDialog folderChooseDialog) {
        Log.d(GlobalSettingsActivity.class.getName(), "onFolderChooseEditDialogOkClicked");
        IFileManager fileManager = getFileManager();
        PreferenceManager preferenceManager = new PreferenceManager(this);
        String folder = folderChooseDialog.getFolder();
        File downloadFolder;
        if (fileManager.isSDCardSupported()) {
            downloadFolder = fileManager.getExternalDirectory(folder, preferenceManager.getPreferenceExternalStorageType());
        } else {
            downloadFolder = fileManager.getExternalDirectory(folder, 0);
        }
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
        folderChooseDialog.dismiss();
    }

    @Override
    public void onBatteryOptimizationDialogOkClicked(BatteryOptimizationDialog batteryOptimizationDialog) {
        Log.d(SettingsInputActivity.class.getName(), "onBatteryOptimizationDialogOkClicked");
        prepareBatteryOptimizationField();
        batteryOptimizationDialog.dismiss();
    }
}
