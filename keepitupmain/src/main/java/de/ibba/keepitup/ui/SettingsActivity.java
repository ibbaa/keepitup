package de.ibba.keepitup.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import de.ibba.keepitup.R;
import de.ibba.keepitup.model.AccessType;
import de.ibba.keepitup.resources.PreferenceManager;
import de.ibba.keepitup.ui.dialog.NetworkTaskEditDialog;
import de.ibba.keepitup.ui.dialog.SettingsInput;
import de.ibba.keepitup.ui.dialog.SettingsInputDialog;
import de.ibba.keepitup.ui.mapping.EnumMapping;
import de.ibba.keepitup.ui.validation.ConnectionTimeoutFieldValidator;
import de.ibba.keepitup.ui.validation.HostFieldValidator;
import de.ibba.keepitup.ui.validation.IntervalFieldValidator;
import de.ibba.keepitup.ui.validation.PortFieldValidator;
import de.ibba.keepitup.ui.validation.ReadTimeoutFieldValidator;
import de.ibba.keepitup.ui.validation.URLFieldValidator;
import de.ibba.keepitup.util.NumberUtil;
import de.ibba.keepitup.util.StringUtil;

public class SettingsActivity extends AppCompatActivity {

    private RadioGroup accessTypeGroup;
    private TextView addressText;
    private TextView portText;
    private TextView intervalText;
    private TextView intervalMinutesText;
    private Switch onlyWifiSwitch;
    private TextView onlyWifiOnOffText;
    private Switch notificationSwitch;
    private TextView notificationOnOffText;
    private TextView connectionTimeoutText;
    private TextView connectionTimeoutSecondsText;
    private TextView readTimeoutText;
    private TextView readTimeoutSecondsText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_settings);
        prepareAccessTypeRadioButtons();
        prepareAddressField();
        preparePortField();
        prepareIntervalField();
        prepareOnlyWifiSwitch();
        prepareNotificationSwitch();
        prepareConnectionTimeoutField();
        prepareReadTimeoutField();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_action_reset) {
            Log.d(SettingsActivity.class.getName(), "menu_action_reset triggered");
            PreferenceManager preferenceManager = new PreferenceManager(this);
            preferenceManager.removePreferenceAccessType();
            preferenceManager.removePreferenceAddress();
            preferenceManager.removePreferencePort();
            preferenceManager.removePreferenceInterval();
            preferenceManager.removePreferenceOnlyWifi();
            preferenceManager.removePreferenceNotification();
            preferenceManager.removePreferenceConnectionTimeout();
            preferenceManager.removePreferenceReadTimeout();
            recreate();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void prepareAccessTypeRadioButtons() {
        Log.d(NetworkTaskEditDialog.class.getName(), "prepareAccessTypeRadioButtons");
        PreferenceManager preferenceManager = new PreferenceManager(this);
        accessTypeGroup = findViewById(R.id.radiogroup_settings_activity_accesstype);
        EnumMapping mapping = new EnumMapping(this);
        AccessType[] accessTypes = AccessType.values();
        AccessType type = preferenceManager.getPreferenceAccessType();
        for (int ii = 0; ii < accessTypes.length; ii++) {
            AccessType accessType = accessTypes[ii];
            RadioButton newRadioButton = new RadioButton(this);
            newRadioButton.setText(mapping.getAccessTypeText(accessType));
            newRadioButton.setId(View.generateViewId());
            if (type == null && ii == 0) {
                newRadioButton.setChecked(true);
            } else {
                newRadioButton.setChecked(accessType.equals(type));
            }
            newRadioButton.setTag(accessType);
            LinearLayout.LayoutParams layoutParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
            accessTypeGroup.addView(newRadioButton, ii, layoutParams);
        }
        accessTypeGroup.setOnCheckedChangeListener(this::onAccessTypeChanged);
    }

    private void onAccessTypeChanged(RadioGroup group, int checkedId) {
        Log.d(NetworkTaskEditDialog.class.getName(), "onAccessTypeChanged");
        PreferenceManager preferenceManager = new PreferenceManager(this);
        RadioButton selectedAccessTypeRadioButton = accessTypeGroup.findViewById(checkedId);
        if (selectedAccessTypeRadioButton != null) {
            AccessType accessType = (AccessType) selectedAccessTypeRadioButton.getTag();
            Log.d(NetworkTaskEditDialog.class.getName(), "checked access type radio button is " + accessType);
            if (accessType != null) {
                preferenceManager.setPreferenceAccessType(accessType);
            }
        }
    }

    private void prepareAddressField() {
        Log.d(SettingsActivity.class.getName(), "prepareAddressField");
        PreferenceManager preferenceManager = new PreferenceManager(this);
        addressText = findViewById(R.id.textview_settings_activity_address);
        setAddress(preferenceManager.getPreferenceAddress());
        CardView addressCardView = findViewById(R.id.cardview_settings_activity_address);
        addressCardView.setOnClickListener(this::showAddressInputDialog);
    }

    private void preparePortField() {
        Log.d(SettingsActivity.class.getName(), "preparePortField");
        PreferenceManager preferenceManager = new PreferenceManager(this);
        portText = findViewById(R.id.textview_settings_activity_port);
        setPort(String.valueOf(preferenceManager.getPreferencePort()));
        CardView portCardView = findViewById(R.id.cardview_settings_activity_port);
        portCardView.setOnClickListener(this::showPortInputDialog);
    }

    private void prepareIntervalField() {
        Log.d(SettingsActivity.class.getName(), "prepareIntervalField");
        PreferenceManager preferenceManager = new PreferenceManager(this);
        intervalText = findViewById(R.id.textview_settings_activity_interval);
        intervalMinutesText = findViewById(R.id.textview_settings_activity_interval_minutes);
        setInterval(String.valueOf(preferenceManager.getPreferenceInterval()));
        CardView intervalCardView = findViewById(R.id.cardview_settings_activity_interval);
        intervalCardView.setOnClickListener(this::showIntervalInputDialog);
    }

    private void prepareOnlyWifiSwitch() {
        Log.d(SettingsActivity.class.getName(), "prepareOnlyWifiSwitch");
        PreferenceManager preferenceManager = new PreferenceManager(this);
        onlyWifiSwitch = findViewById(R.id.switch_settings_activity_onlywifi);
        onlyWifiOnOffText = findViewById(R.id.textview_settings_activity_onlywifi_on_off);
        onlyWifiSwitch.setChecked(preferenceManager.getPreferenceOnlyWifi());
        onlyWifiSwitch.setOnCheckedChangeListener(this::onOnlyWifiCheckedChanged);
        prepareOnlyWifiOnOffText();
    }

    private void prepareOnlyWifiOnOffText() {
        onlyWifiOnOffText.setText(onlyWifiSwitch.isChecked() ? getResources().getString(R.string.string_yes) : getResources().getString(R.string.string_no));
    }

    private void onOnlyWifiCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.d(NetworkTaskEditDialog.class.getName(), "onOnlyWifiCheckedChanged, new value is " + isChecked);
        PreferenceManager preferenceManager = new PreferenceManager(this);
        preferenceManager.setPreferenceOnlyWifi(isChecked);
        prepareOnlyWifiOnOffText();
    }

    private void prepareNotificationSwitch() {
        Log.d(SettingsActivity.class.getName(), "prepareNotificationSwitch");
        PreferenceManager preferenceManager = new PreferenceManager(this);
        notificationSwitch = findViewById(R.id.switch_settings_activity_notification);
        notificationOnOffText = findViewById(R.id.textview_settings_activity_notification_on_off);
        notificationSwitch.setChecked(preferenceManager.getPreferenceNotification());
        notificationSwitch.setOnCheckedChangeListener(this::onNotificationCheckedChanged);
        prepareNotificationOnOffText();
    }

    private void prepareNotificationOnOffText() {
        notificationOnOffText.setText(notificationSwitch.isChecked() ? getResources().getString(R.string.string_yes) : getResources().getString(R.string.string_no));
    }

    private void onNotificationCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.d(NetworkTaskEditDialog.class.getName(), "onNotificationCheckedChanged, new value is " + isChecked);
        PreferenceManager preferenceManager = new PreferenceManager(this);
        preferenceManager.setPreferenceNotification(isChecked);
        prepareNotificationOnOffText();
    }

    private void prepareConnectionTimeoutField() {
        Log.d(SettingsActivity.class.getName(), "prepareIntervalField");
        PreferenceManager preferenceManager = new PreferenceManager(this);
        connectionTimeoutText = findViewById(R.id.textview_settings_activity_connection_timeout);
        connectionTimeoutSecondsText = findViewById(R.id.textview_settings_activity_connection_timeout_seconds);
        setConnectionTimeout(String.valueOf(preferenceManager.getPreferenceConnectionTimeout()));
        CardView connectionTimeoutCardView = findViewById(R.id.cardview_settings_activity_connection_timeout);
        connectionTimeoutCardView.setOnClickListener(this::showConnectionTimeoutInputDialog);
    }

    private void prepareReadTimeoutField() {
        Log.d(SettingsActivity.class.getName(), "prepareReadTimeoutField");
        PreferenceManager preferenceManager = new PreferenceManager(this);
        readTimeoutText = findViewById(R.id.textview_settings_activity_read_timeout);
        readTimeoutSecondsText = findViewById(R.id.textview_settings_activity_read_timeout_seconds);
        setReadTimeout(String.valueOf(preferenceManager.getPreferenceReadTimeout()));
        CardView readTimeoutCardView = findViewById(R.id.cardview_settings_activity_read_timeout);
        readTimeoutCardView.setOnClickListener(this::showReadTimeoutInputDialog);
    }

    private String getAddress() {
        return StringUtil.notNull(addressText.getText());
    }

    private void setAddress(String address) {
        addressText.setText(StringUtil.notNull(address));
    }

    private String getPort() {
        return StringUtil.notNull(portText.getText());
    }

    private void setPort(String port) {
        portText.setText(StringUtil.notNull(port));
    }

    private String getInterval() {
        return StringUtil.notNull(intervalText.getText());
    }

    private void setInterval(String interval) {
        intervalText.setText(StringUtil.notNull(interval));
        if (NumberUtil.isValidIntValue(interval)) {
            int value = NumberUtil.getIntValue(interval, getResources().getInteger(R.integer.task_interval_default));
            intervalMinutesText.setText(value == 1 ? getResources().getString(R.string.string_minute) : getResources().getString(R.string.string_minutes));
        }
    }

    private String getConnectionTimeout() {
        return StringUtil.notNull(connectionTimeoutText.getText());
    }

    private void setConnectionTimeout(String connectionTimeout) {
        connectionTimeoutText.setText(StringUtil.notNull(connectionTimeout));
        if (NumberUtil.isValidIntValue(connectionTimeout)) {
            int value = NumberUtil.getIntValue(connectionTimeout, getResources().getInteger(R.integer.socket_connection_timeout_default));
            connectionTimeoutSecondsText.setText(value == 1 ? getResources().getString(R.string.string_second) : getResources().getString(R.string.string_seconds));
        }
    }

    private String getReadTimeout() {
        return StringUtil.notNull(readTimeoutText.getText());
    }

    private void setReadTimeout(String readTimeout) {
        readTimeoutText.setText(StringUtil.notNull(readTimeout));
        if (NumberUtil.isValidIntValue(readTimeout)) {
            int value = NumberUtil.getIntValue(readTimeout, getResources().getInteger(R.integer.socket_read_timeout_default));
            readTimeoutSecondsText.setText(value == 1 ? getResources().getString(R.string.string_second) : getResources().getString(R.string.string_seconds));
        }
    }

    private void showAddressInputDialog(View view) {
        Log.d(SettingsActivity.class.getName(), "showAddressInputDialog");
        List<String> validators = Arrays.asList(HostFieldValidator.class.getName(), URLFieldValidator.class.getName());
        SettingsInput input = new SettingsInput(SettingsInput.Type.ADDRESS, getAddress(), getResources().getString(R.string.label_settings_activity_address), validators);
        showInputDialog(input.toBundle());
    }

    private void showPortInputDialog(View view) {
        Log.d(SettingsActivity.class.getName(), "showPortInputDialog");
        List<String> validators = Collections.singletonList(PortFieldValidator.class.getName());
        SettingsInput input = new SettingsInput(SettingsInput.Type.PORT, getPort(), getResources().getString(R.string.label_settings_activity_port), validators);
        showInputDialog(input.toBundle());
    }

    private void showIntervalInputDialog(View view) {
        Log.d(SettingsActivity.class.getName(), "showIntervalInputDialog");
        List<String> validators = Collections.singletonList(IntervalFieldValidator.class.getName());
        SettingsInput input = new SettingsInput(SettingsInput.Type.INTERVAL, getInterval(), getResources().getString(R.string.label_settings_activity_interval), validators);
        showInputDialog(input.toBundle());
    }

    private void showConnectionTimeoutInputDialog(View view) {
        Log.d(SettingsActivity.class.getName(), "showConnectionTimeoutInputDialog");
        List<String> validators = Collections.singletonList(ConnectionTimeoutFieldValidator.class.getName());
        SettingsInput input = new SettingsInput(SettingsInput.Type.CONNECTIONTIMEOUT, getConnectionTimeout(), getResources().getString(R.string.label_settings_activity_connection_timeout), validators);
        showInputDialog(input.toBundle());
    }

    private void showReadTimeoutInputDialog(View view) {
        Log.d(SettingsActivity.class.getName(), "showReadTimeoutInputDialog");
        List<String> validators = Collections.singletonList(ReadTimeoutFieldValidator.class.getName());
        SettingsInput input = new SettingsInput(SettingsInput.Type.READTIMEOUT, getReadTimeout(), getResources().getString(R.string.label_settings_activity_read_timeout), validators);
        showInputDialog(input.toBundle());
    }

    private void showInputDialog(Bundle bundle) {
        Log.d(SettingsActivity.class.getName(), "showInputDialog, opening SettingsInputDialog");
        SettingsInputDialog inputDialog = new SettingsInputDialog();
        inputDialog.setArguments(bundle);
        inputDialog.show(getSupportFragmentManager(), SettingsInputDialog.class.getName());
    }

    public void onInputDialogOkClicked(SettingsInputDialog inputDialog, SettingsInput.Type type) {
        Log.d(SettingsActivity.class.getName(), "onInputDialogOkClicked, type is " + type + ", value is " + inputDialog.getValue());
        PreferenceManager preferenceManager = new PreferenceManager(this);
        if (SettingsInput.Type.ADDRESS.equals(type)) {
            setAddress(inputDialog.getValue());
            preferenceManager.setPreferenceAddress(getAddress());
        } else if (SettingsInput.Type.PORT.equals(type)) {
            setPort(inputDialog.getValue());
            preferenceManager.setPreferencePort(NumberUtil.getIntValue(getPort(), getResources().getInteger(R.integer.task_port_default)));
        } else if (SettingsInput.Type.INTERVAL.equals(type)) {
            setInterval(inputDialog.getValue());
            preferenceManager.setPreferenceInterval(NumberUtil.getIntValue(getInterval(), getResources().getInteger(R.integer.task_interval_default)));
        } else if (SettingsInput.Type.CONNECTIONTIMEOUT.equals(type)) {
            setConnectionTimeout(inputDialog.getValue());
            preferenceManager.setPreferenceConnectionTimeout(NumberUtil.getIntValue(getConnectionTimeout(), getResources().getInteger(R.integer.socket_connection_timeout_default)));
        } else if (SettingsInput.Type.READTIMEOUT.equals(type)) {
            setReadTimeout(inputDialog.getValue());
            preferenceManager.setPreferenceReadTimeout(NumberUtil.getIntValue(getReadTimeout(), getResources().getInteger(R.integer.socket_read_timeout_default)));
        } else {
            Log.e(SettingsActivity.class.getName(), "type " + type + " unknown");
        }
        inputDialog.dismiss();
    }

    public void onInputDialogCancelClicked(SettingsInputDialog inputDialog) {
        Log.d(SettingsActivity.class.getName(), "onInputDialogCancelClicked");
        inputDialog.dismiss();
    }
}
