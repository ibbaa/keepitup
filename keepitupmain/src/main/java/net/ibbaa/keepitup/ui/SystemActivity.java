package net.ibbaa.keepitup.ui;

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
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import net.ibbaa.keepitup.BuildConfig;
import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Dump;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.resources.PreferenceManager;
import net.ibbaa.keepitup.resources.PreferenceSetup;
import net.ibbaa.keepitup.service.IFileManager;
import net.ibbaa.keepitup.service.NetworkTaskProcessServiceScheduler;
import net.ibbaa.keepitup.ui.dialog.ConfirmDialog;
import net.ibbaa.keepitup.ui.dialog.FileChooseDialog;
import net.ibbaa.keepitup.ui.sync.DBPurgeTask;
import net.ibbaa.keepitup.ui.sync.ExportTask;
import net.ibbaa.keepitup.ui.sync.ImportTask;
import net.ibbaa.keepitup.util.BundleUtil;
import net.ibbaa.keepitup.util.DebugUtil;
import net.ibbaa.keepitup.util.FileUtil;
import net.ibbaa.keepitup.util.StringUtil;
import net.ibbaa.keepitup.util.ThreadUtil;

public class SystemActivity extends SettingsInputActivity implements ExportSupport, ImportSupport, DBPurgeSupport {

    private TextView exportFolderText;
    private TextView importFolderText;
    private RadioGroup externalStorageType;
    private SwitchMaterial fileLoggerEnabledSwitch;
    private TextView fileLoggerEnabledOnOffText;
    private SwitchMaterial fileDumpEnabledSwitch;
    private TextView fileDumpEnabledOnOffText;
    private TextView logFolderText;

    private DBPurgeTask purgeTask;
    private ExportTask exportTask;
    private ImportTask importTask;
    private NetworkTaskProcessServiceScheduler scheduler;

    public void injectExportTask(ExportTask exportTask) {
        this.exportTask = exportTask;
    }

    public void injectImportTask(ImportTask importTask) {
        this.importTask = importTask;
    }

    public void injectPurgeTask(DBPurgeTask purgeTask) {
        this.purgeTask = purgeTask;
    }

    public void injectScheduler(NetworkTaskProcessServiceScheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setContentView(R.layout.activity_system);
        prepareConfigurationResetField();
        prepareConfigurationExportField();
        prepareConfigurationImportField();
        prepareExternalStorageTypeRadioGroup();
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
        CardView configurationResetView = findViewById(R.id.cardview_activity_system_config_export);
        exportFolderText = findViewById(R.id.textview_activity_system_config_export_folder);
        PreferenceManager preferenceManager = new PreferenceManager(this);
        String exportFolder = getExternalImportExportFolder(preferenceManager.getPreferenceExportFolder());
        setExportFolder(exportFolder);
        configurationResetView.setOnClickListener(this::showExportFolderChooseDialog);
    }

    private void prepareConfigurationImportField() {
        Log.d(SystemActivity.class.getName(), "prepareConfigurationImportField");
        CardView configurationResetView = findViewById(R.id.cardview_activity_system_config_import);
        importFolderText = findViewById(R.id.textview_activity_system_config_import_folder);
        PreferenceManager preferenceManager = new PreferenceManager(this);
        String importFolder = getExternalImportExportFolder(preferenceManager.getPreferenceImportFolder());
        setImportFolder(importFolder);
        configurationResetView.setOnClickListener(this::showImportFolderChooseDialog);
    }

    private void prepareExternalStorageTypeRadioGroup() {
        Log.d(SystemActivity.class.getName(), "prepareExternalStorageTypeRadioGroup");
        externalStorageType = findViewById(R.id.radiogroup_activity_system_external_storage_type);
        IFileManager fileManager = getFileManager();
        boolean sdCardSupported = fileManager.isSDCardSupported();
        Log.d(SystemActivity.class.getName(), "SD card supported: " + sdCardSupported);
        if (sdCardSupported) {
            RadioButton primaryStorageTypeButton = findViewById(R.id.radiobutton_activity_system_external_storage_type_primary);
            RadioButton sdCardStorageTypeButton = findViewById(R.id.radiobutton_activity_system_external_storage_type_sdcard);
            sdCardStorageTypeButton.setVisibility(View.VISIBLE);
            PreferenceManager preferenceManager = new PreferenceManager(this);
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
                showErrorDialog(getResources().getString(R.string.text_dialog_general_error_external_root_access));
            } else {
                setLogFolder(logFolder);
            }
        } else {
            Log.d(SystemActivity.class.getName(), "Release version. Disabling debug settings.");
            logFolderCardView.setVisibility(View.GONE);
            setLogFolder("");
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
            showErrorDialog(getResources().getString(R.string.text_dialog_general_error_external_root_access));
            return;
        }
        Bundle bundle = BundleUtil.stringsToBundle(new String[]{fileChooseDialog.getFolderRootKey(), fileChooseDialog.getFolderKey(), fileChooseDialog.getFileKey(), fileChooseDialog.getFileModeKey(), fileChooseDialog.getTypeKey()}, new String[]{root, folder, file, FileChooseDialog.Mode.FILE.name(), type.name()});
        fileChooseDialog.setArguments(bundle);
        fileChooseDialog.show(getSupportFragmentManager(), GlobalSettingsActivity.class.getName());
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
                showErrorDialog(getResources().getString(R.string.text_dialog_general_error_external_import_export_create));
                return;
            }
            String file = fileChooseDialog.getFile();
            fileChooseDialog.dismiss();
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
                    showProgressDialog();
                    doConfigurationExport(importExportFolder, file);
                }
            }
        } else {
            Log.e(GlobalSettingsActivity.class.getName(), "Unknown type " + type);
            fileChooseDialog.dismiss();
        }
    }

    private void doConfigurationExport(File exportFolder, String file) {
        Log.d(SystemActivity.class.getName(), "doConfigurationExport");
        Log.d(SystemActivity.class.getName(), "Export folder is " + exportFolder);
        Log.d(SystemActivity.class.getName(), "File name is " + file);
        if (exportFolder == null || StringUtil.isEmpty(file)) {
            Log.e(SystemActivity.class.getName(), "Folder or file is empty.");
            showErrorDialog(getResources().getString(R.string.text_dialog_general_error_config_export));
            return;
        }
        ExportTask exportTask = getExportTask(exportFolder, file);
        Future<Boolean> exportFuture = ThreadUtil.exexute(exportTask);
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
        if (importFolder == null || StringUtil.isEmpty(file)) {
            Log.e(SystemActivity.class.getName(), "Folder or file is empty.");
            showErrorDialog(getResources().getString(R.string.text_dialog_general_error_config_import));
            return;
        }
        ImportTask importTask = getImportTask(importFolder, file);
        Future<Boolean> importFuture = ThreadUtil.exexute(importTask);
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
            terminateAllNetworkTasks();
            confirmDialog.dismiss();
            showProgressDialog();
            purgeDatabase();
        } else if (ConfirmDialog.Type.EXPORTCONFIGEXISTINGFILE.equals(type)) {
            IFileManager fileManager = getFileManager();
            PreferenceManager preferenceManager = new PreferenceManager(this);
            File exportFolder = FileUtil.getExternalDirectory(fileManager, preferenceManager, preferenceManager.getPreferenceExportFolder());
            String file = getFileExtraData(confirmDialog);
            confirmDialog.dismiss();
            showProgressDialog();
            doConfigurationExport(exportFolder, file);
        } else if (ConfirmDialog.Type.IMPORTCONFIG.equals(type)) {
            IFileManager fileManager = getFileManager();
            PreferenceManager preferenceManager = new PreferenceManager(this);
            File importFolder = FileUtil.getExternalDirectory(fileManager, preferenceManager, preferenceManager.getPreferenceImportFolder());
            String file = getFileExtraData(confirmDialog);
            terminateAllNetworkTasks();
            confirmDialog.dismiss();
            showProgressDialog();
            doConfigurationImport(importFolder, file);
        } else {
            Log.e(SystemActivity.class.getName(), "Unknown type " + type);
        }
    }

    @Override
    public void onExportDone(boolean success) {
        Log.d(SystemActivity.class.getName(), "onExportDone, success is " + success);
        closeProgressDialog();
        if (!success) {
            showErrorDialog(getResources().getString(R.string.text_dialog_general_error_config_export));
        }
    }

    @Override
    public void onImportDone(boolean success) {
        Log.d(SystemActivity.class.getName(), "onImportDone, success is " + success);
        closeProgressDialog();
        if (!success) {
            showErrorDialog(getResources().getString(R.string.text_dialog_general_error_config_import));
        }
    }

    @Override
    public void onPurgeDone(boolean success) {
        Log.d(SystemActivity.class.getName(), "onPurgeDone, success is " + success);
        closeProgressDialog();
        if (success) {
            resetPreferences();
        } else {
            showErrorDialog(getResources().getString(R.string.text_dialog_general_error_db_purge));
        }
    }

    private void resetPreferences() {
        Log.d(SystemActivity.class.getName(), "resetPreferences");
        PreferenceSetup preferenceSetup = new PreferenceSetup(this);
        preferenceSetup.removeAllSettings();
        recreateActivity();
    }

    private void terminateAllNetworkTasks() {
        Log.d(SystemActivity.class.getName(), "terminateAllNetworkTasks");
        NetworkTaskProcessServiceScheduler scheduler = getScheduler();
        scheduler.terminateAll();
    }

    protected void purgeDatabase() {
        Log.d(SystemActivity.class.getName(), "purgeDatabase");
        DBPurgeTask purgeTask = getPurgeTask();
        Future<Boolean> purgeFuture = ThreadUtil.exexute(purgeTask);
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
        String folder = getResources().getString(R.string.file_logger_log_directory_default);
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
        return new ExportTask(this, folder, file);
    }

    private ImportTask getImportTask(File folder, String file) {
        if (importTask != null) {
            return importTask;
        }
        return new ImportTask(this, folder, file);
    }

    private DBPurgeTask getPurgeTask() {
        if (purgeTask != null) {
            return purgeTask;
        }
        return new DBPurgeTask(this);
    }

    private NetworkTaskProcessServiceScheduler getScheduler() {
        if (scheduler != null) {
            return scheduler;
        }
        return new NetworkTaskProcessServiceScheduler(this);
    }
}