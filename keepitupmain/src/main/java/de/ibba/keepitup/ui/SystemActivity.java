package de.ibba.keepitup.ui;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.google.android.material.switchmaterial.SwitchMaterial;

import java.io.File;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import de.ibba.keepitup.BuildConfig;
import de.ibba.keepitup.R;
import de.ibba.keepitup.logging.Dump;
import de.ibba.keepitup.logging.Log;
import de.ibba.keepitup.resources.PreferenceManager;
import de.ibba.keepitup.resources.PreferenceSetup;
import de.ibba.keepitup.service.IFileManager;
import de.ibba.keepitup.ui.dialog.ConfirmDialog;
import de.ibba.keepitup.ui.sync.DBPurgeTask;
import de.ibba.keepitup.util.DebugUtil;
import de.ibba.keepitup.util.StringUtil;
import de.ibba.keepitup.util.ThreadUtil;

public class SystemActivity extends SettingsInputActivity implements DBPurgeSupport {

    private SwitchMaterial fileLoggerEnabledSwitch;
    private TextView fileLoggerEnabledOnOffText;
    private SwitchMaterial fileDumpEnabledSwitch;
    private TextView fileDumpEnabledOnOffText;
    private TextView logFolderText;

    private DBPurgeTask purgeTask;

    public void injectPurgeTask(DBPurgeTask purgeTask) {
        this.purgeTask = purgeTask;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setContentView(R.layout.activity_system);
        prepareConfigurationResetField();
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
        configurationResetView.setOnClickListener(this::showConfirmDialog);
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

    private void showConfirmDialog(View view) {
        Log.d(SystemActivity.class.getName(), "showConfirmDialog");
        String message = getResources().getString(R.string.text_dialog_confirm_config_reset);
        String desciption = getResources().getString(R.string.text_dialog_confirm_config_reset_description);
        showConfirmDialog(message, desciption, ConfirmDialog.Type.RESETCONFIG);
    }

    @Override
    public void onConfirmDialogOkClicked(ConfirmDialog confirmDialog, ConfirmDialog.Type type) {
        Log.d(SystemActivity.class.getName(), "onConfirmDialogOkClicked for type " + type);
        if (ConfirmDialog.Type.RESETCONFIG.equals(type)) {
            confirmDialog.dismiss();
            showProgressDialog();
            purgeDatabase();
        } else {
            Log.e(SystemActivity.class.getName(), "Unknown type " + type);
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
                Log.d(SystemActivity.class.getName(), "Error waiting for purge execution", exc);
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

    private DBPurgeTask getPurgeTask() {
        if (purgeTask != null) {
            return purgeTask;
        }
        return new DBPurgeTask(this);
    }
}
