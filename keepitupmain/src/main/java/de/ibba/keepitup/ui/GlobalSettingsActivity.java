package de.ibba.keepitup.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import java.util.Collections;
import java.util.List;

import de.ibba.keepitup.R;
import de.ibba.keepitup.resources.PreferenceManager;
import de.ibba.keepitup.ui.dialog.SettingsInput;
import de.ibba.keepitup.ui.dialog.SettingsInputDialog;
import de.ibba.keepitup.ui.validation.PingCountFieldValidator;
import de.ibba.keepitup.util.NumberUtil;
import de.ibba.keepitup.util.StringUtil;

public class GlobalSettingsActivity extends SettingsInputActivity {

    private TextView pingCountText;
    private Switch notificationInactiveNetworkSwitch;
    private TextView notificationInactiveNetworkOnOffText;
    private Switch downloadExternalStorageSwitch;
    private TextView downloadExternalStorageOnOffText;
    private TextView downloadFolderText;
    private Switch downloadKeepSwitch;
    private TextView downloadKeepOnOffText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setContentView(R.layout.activity_global_settings);
        preparePingCountField();
        prepareNotificationInactiveNetworkSwitch();
        prepareDownloadExternalStorageSwitch();
        prepareDownloadFolderField();
        prepareDownloadKeepSwitch();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_global_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_action_global_settings_reset) {
            Log.d(GlobalSettingsActivity.class.getName(), "menu_action_reset triggered");
            PreferenceManager preferenceManager = new PreferenceManager(this);
            preferenceManager.removePreferencePingCount();
            preferenceManager.removePreferenceNotificationInactiveNetwork();
            preferenceManager.removePreferenceDownloadExternalStorage();
            preferenceManager.removePreferenceDownloadFolder();
            preferenceManager.removePreferenceDownloadKeep();
            recreateActivity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void preparePingCountField() {
        Log.d(GlobalSettingsActivity.class.getName(), "preparePingCountField");
        PreferenceManager preferenceManager = new PreferenceManager(this);
        pingCountText = findViewById(R.id.textview_global_settings_activity_ping_count);
        setPingCount(String.valueOf(preferenceManager.getPreferencePingCount()));
        CardView pingCountCardView = findViewById(R.id.cardview_global_settings_activity_ping_count);
        pingCountCardView.setOnClickListener(this::showPingCountInputDialog);
    }

    private void prepareNotificationInactiveNetworkSwitch() {
        Log.d(GlobalSettingsActivity.class.getName(), "prepareNotificationInactiveNetworkSwitch");
        PreferenceManager preferenceManager = new PreferenceManager(this);
        notificationInactiveNetworkSwitch = findViewById(R.id.switch_global_settings_activity_notification_inactive_network);
        notificationInactiveNetworkOnOffText = findViewById(R.id.textview_global_settings_activity_notification_inactive_network_on_off);
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
        downloadExternalStorageSwitch = findViewById(R.id.switch_global_settings_activity_download_external_storage);
        downloadExternalStorageOnOffText = findViewById(R.id.textview_global_settings_activity_download_external_storage_on_off);
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
    }

    private void prepareDownloadFolderField() {
        Log.d(GlobalSettingsActivity.class.getName(), "prepareDownloadFolderField");
        PreferenceManager preferenceManager = new PreferenceManager(this);
        downloadFolderText = findViewById(R.id.textview_global_settings_activity_download_folder);
        setDownloadFolder(String.valueOf(preferenceManager.getPreferenceDownloadFolder()));
        CardView downloadFolderCardView = findViewById(R.id.cardview_global_settings_activity_download_folder);
    }

    private void prepareDownloadKeepSwitch() {
        Log.d(GlobalSettingsActivity.class.getName(), "prepareDownloadKeepSwitch");
        PreferenceManager preferenceManager = new PreferenceManager(this);
        downloadKeepSwitch = findViewById(R.id.switch_global_settings_activity_download_keep);
        downloadKeepOnOffText = findViewById(R.id.textview_global_settings_activity_download_keep_on_off);
        downloadKeepSwitch.setOnCheckedChangeListener(null);
        downloadKeepSwitch.setChecked(preferenceManager.getPreferenceDownloadKeep());
        downloadKeepSwitch.setOnCheckedChangeListener(this::onDownloadKeepCheckedChanged);
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

    private String getPingCount() {
        return StringUtil.notNull(pingCountText.getText());
    }

    private void setPingCount(String pingCount) {
        pingCountText.setText(StringUtil.notNull(pingCount));
    }

    private String getDownloadFolder() {
        return StringUtil.notNull(downloadFolderText.getText());
    }

    private void setDownloadFolder(String downloadFolder) {
        downloadFolderText.setText(StringUtil.notNull(downloadFolder));
    }

    private void showPingCountInputDialog(View view) {
        Log.d(GlobalSettingsActivity.class.getName(), "showPingCountInputDialog");
        List<String> validators = Collections.singletonList(PingCountFieldValidator.class.getName());
        SettingsInput input = new SettingsInput(SettingsInput.Type.PINGCOUNT, getPingCount(), getResources().getString(R.string.label_global_settings_activity_ping_count), validators);
        showInputDialog(input.toBundle());
    }

    private void showInputDialog(Bundle bundle) {
        Log.d(GlobalSettingsActivity.class.getName(), "showInputDialog, opening SettingsInputDialog");
        SettingsInputDialog inputDialog = new SettingsInputDialog();
        inputDialog.setArguments(bundle);
        inputDialog.show(getSupportFragmentManager(), GlobalSettingsActivity.class.getName());
    }

    public void onInputDialogOkClicked(SettingsInputDialog inputDialog, SettingsInput.Type type) {
        Log.d(GlobalSettingsActivity.class.getName(), "onInputDialogOkClicked, type is " + type + ", value is " + inputDialog.getValue());
        PreferenceManager preferenceManager = new PreferenceManager(this);
        if (SettingsInput.Type.PINGCOUNT.equals(type)) {
            setPingCount(inputDialog.getValue());
            preferenceManager.setPreferencePingCount(NumberUtil.getIntValue(getPingCount(), getResources().getInteger(R.integer.ping_count_default)));
        } else {
            Log.e(GlobalSettingsActivity.class.getName(), "type " + type + " unknown");
        }
        inputDialog.dismiss();
    }

    public void onInputDialogCancelClicked(SettingsInputDialog inputDialog) {
        Log.d(GlobalSettingsActivity.class.getName(), "onInputDialogCancelClicked");
        inputDialog.dismiss();
    }
}