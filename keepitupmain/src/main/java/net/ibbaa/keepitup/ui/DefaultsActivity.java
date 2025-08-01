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
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.switchmaterial.SwitchMaterial;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.model.AccessType;
import net.ibbaa.keepitup.resources.PreferenceManager;
import net.ibbaa.keepitup.resources.PreferenceSetup;
import net.ibbaa.keepitup.ui.dialog.NetworkTaskEditDialog;
import net.ibbaa.keepitup.ui.dialog.SettingsInput;
import net.ibbaa.keepitup.ui.dialog.SettingsInputDialog;
import net.ibbaa.keepitup.ui.mapping.EnumMapping;
import net.ibbaa.keepitup.ui.validation.ConnectCountFieldValidator;
import net.ibbaa.keepitup.ui.validation.HostFieldValidator;
import net.ibbaa.keepitup.ui.validation.IntervalFieldValidator;
import net.ibbaa.keepitup.ui.validation.PingCountFieldValidator;
import net.ibbaa.keepitup.ui.validation.PingPackageSizeFieldValidator;
import net.ibbaa.keepitup.ui.validation.PortFieldValidator;
import net.ibbaa.keepitup.ui.validation.URLFieldValidator;
import net.ibbaa.keepitup.util.NumberUtil;
import net.ibbaa.keepitup.util.StringUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SuppressWarnings({"unused"})
public class DefaultsActivity extends SettingsInputActivity {

    private RadioGroup accessTypeGroup;
    private TextView addressText;
    private TextView portText;
    private TextView intervalText;
    private TextView intervalMinutesText;
    private TextView pingCountText;
    private TextView pingPackageSizeText;
    private TextView connectCountText;
    private SwitchMaterial stopOnSuccessSwitch;
    private TextView stopOnSuccessOnOffText;
    private SwitchMaterial ignoreSSLErrorSwitch;
    private TextView ignoreSSLErrorOnOffText;
    private SwitchMaterial onlyWifiSwitch;
    private TextView onlyWifiOnOffText;
    private SwitchMaterial notificationSwitch;
    private TextView notificationOnOffText;
    private SwitchMaterial highPrioSwitch;
    private TextView highPrioOnOffText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setContentView(R.layout.activity_defaults);
        initEdgeToEdgeInsets(R.id.layout_activity_defaults);
        prepareAccessTypeRadioButtons();
        prepareAddressField();
        preparePortField();
        prepareIntervalField();
        preparePingCountField();
        preparePingPackageSizeField();
        prepareConnectCountField();
        prepareStopOnSuccessSwitch();
        prepareIgnoreSSLErrorSwitch();
        prepareOnlyWifiSwitch();
        prepareNotificationSwitch();
        prepareHighPrioSwitch();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_defaults, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_action_activity_defaults_reset) {
            Log.d(DefaultsActivity.class.getName(), "menu_action_activity_defaults_reset triggered");
            PreferenceSetup preferenceSetup = new PreferenceSetup(this);
            preferenceSetup.removeDefaults();
            recreateActivity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void prepareAccessTypeRadioButtons() {
        Log.d(DefaultsActivity.class.getName(), "prepareAccessTypeRadioButtons");
        PreferenceManager preferenceManager = new PreferenceManager(this);
        accessTypeGroup = findViewById(R.id.radiogroup_activity_defaults_accesstype);
        accessTypeGroup.setOnCheckedChangeListener(null);
        EnumMapping mapping = new EnumMapping(this);
        AccessType[] accessTypes = AccessType.values();
        AccessType type = preferenceManager.getPreferenceAccessType();
        for (int ii = 0; ii < accessTypes.length; ii++) {
            AccessType accessType = accessTypes[ii];
            RadioButton newRadioButton = new RadioButton(this);
            newRadioButton.setText(mapping.getAccessTypeText(accessType));
            newRadioButton.setTextColor(getResources().getColor(R.color.textColor));
            newRadioButton.setButtonTintList(ColorStateList.valueOf(ResourcesCompat.getColor(getResources(), R.color.textColor, null)));
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
        Log.d(DefaultsActivity.class.getName(), "prepareAddressField");
        PreferenceManager preferenceManager = new PreferenceManager(this);
        addressText = findViewById(R.id.textview_activity_defaults_address);
        setAddress(preferenceManager.getPreferenceAddress());
        CardView addressCardView = findViewById(R.id.cardview_activity_defaults_address);
        addressCardView.setOnClickListener(this::showAddressInputDialog);
    }

    private void preparePortField() {
        Log.d(DefaultsActivity.class.getName(), "preparePortField");
        PreferenceManager preferenceManager = new PreferenceManager(this);
        portText = findViewById(R.id.textview_activity_defaults_port);
        setPort(String.valueOf(preferenceManager.getPreferencePort()));
        CardView portCardView = findViewById(R.id.cardview_activity_defaults_port);
        portCardView.setOnClickListener(this::showPortInputDialog);
    }

    private void prepareIntervalField() {
        Log.d(DefaultsActivity.class.getName(), "prepareIntervalField");
        PreferenceManager preferenceManager = new PreferenceManager(this);
        intervalText = findViewById(R.id.textview_activity_defaults_interval);
        intervalMinutesText = findViewById(R.id.textview_activity_defaults_interval_minutes);
        setInterval(String.valueOf(preferenceManager.getPreferenceInterval()));
        CardView intervalCardView = findViewById(R.id.cardview_activity_defaults_interval);
        intervalCardView.setOnClickListener(this::showIntervalInputDialog);
    }

    private void preparePingCountField() {
        Log.d(DefaultsActivity.class.getName(), "preparePingCountField");
        PreferenceManager preferenceManager = new PreferenceManager(this);
        pingCountText = findViewById(R.id.textview_activity_defaults_ping_count);
        setPingCount(String.valueOf(preferenceManager.getPreferencePingCount()));
        CardView pingCountCardView = findViewById(R.id.cardview_activity_defaults_ping_count);
        pingCountCardView.setOnClickListener(this::showPingCountInputDialog);
    }

    private void preparePingPackageSizeField() {
        Log.d(DefaultsActivity.class.getName(), "preparePingPackageSizeField");
        PreferenceManager preferenceManager = new PreferenceManager(this);
        pingPackageSizeText = findViewById(R.id.textview_activity_defaults_ping_package_size);
        setPingPackageSize(String.valueOf(preferenceManager.getPreferencePingPackageSize()));
        CardView pingPackageSizeCardView = findViewById(R.id.cardview_activity_defaults_ping_package_size);
        pingPackageSizeCardView.setOnClickListener(this::showPingPackageSizeInputDialog);
    }

    private void prepareConnectCountField() {
        Log.d(DefaultsActivity.class.getName(), "prepareConnectCountField");
        PreferenceManager preferenceManager = new PreferenceManager(this);
        connectCountText = findViewById(R.id.textview_activity_defaults_connect_count);
        setConnectCount(String.valueOf(preferenceManager.getPreferenceConnectCount()));
        CardView connectCountCardView = findViewById(R.id.cardview_activity_defaults_connect_count);
        connectCountCardView.setOnClickListener(this::showConnectCountInputDialog);
    }

    private void prepareStopOnSuccessSwitch() {
        Log.d(DefaultsActivity.class.getName(), "prepareStopOnSuccessSwitch");
        PreferenceManager preferenceManager = new PreferenceManager(this);
        stopOnSuccessSwitch = findViewById(R.id.switch_activity_defaults_stop_on_success);
        stopOnSuccessOnOffText = findViewById(R.id.textview_activity_defaults_stop_on_success_on_off);
        stopOnSuccessSwitch.setOnCheckedChangeListener(null);
        stopOnSuccessSwitch.setChecked(preferenceManager.getPreferenceStopOnSuccess());
        stopOnSuccessSwitch.setOnCheckedChangeListener(this::onStopOnSuccessCheckedChanged);
        prepareStopOnSuccessOnOffText();
    }

    private void prepareStopOnSuccessOnOffText() {
        stopOnSuccessOnOffText.setText(stopOnSuccessSwitch.isChecked() ? getResources().getString(R.string.string_yes) : getResources().getString(R.string.string_no));
    }

    private void onStopOnSuccessCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.d(DefaultsActivity.class.getName(), "onStopOnSuccessCheckedChanged, new value is " + isChecked);
        PreferenceManager preferenceManager = new PreferenceManager(this);
        preferenceManager.setPreferenceStopOnSuccess(isChecked);
        prepareStopOnSuccessOnOffText();
    }

    private void prepareIgnoreSSLErrorSwitch() {
        Log.d(DefaultsActivity.class.getName(), "prepareIgnoreSSLErrorSwitch");
        PreferenceManager preferenceManager = new PreferenceManager(this);
        ignoreSSLErrorSwitch = findViewById(R.id.switch_activity_defaults_ignore_ssl_error);
        ignoreSSLErrorOnOffText = findViewById(R.id.textview_activity_defaults_ignore_ssl_error_on_off);
        ignoreSSLErrorSwitch.setOnCheckedChangeListener(null);
        ignoreSSLErrorSwitch.setChecked(preferenceManager.getPreferenceIgnoreSSLError());
        ignoreSSLErrorSwitch.setOnCheckedChangeListener(this::onIgnoreSSLErrorCheckedChanged);
        prepareIgnoreSSLErrorOnOffText();
    }

    private void prepareIgnoreSSLErrorOnOffText() {
        ignoreSSLErrorOnOffText.setText(ignoreSSLErrorSwitch.isChecked() ? getResources().getString(R.string.string_yes) : getResources().getString(R.string.string_no));
    }

    private void onIgnoreSSLErrorCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.d(DefaultsActivity.class.getName(), "onIgnoreSSLErrorCheckedChanged, new value is " + isChecked);
        PreferenceManager preferenceManager = new PreferenceManager(this);
        preferenceManager.setPreferenceIgnoreSSLError(isChecked);
        prepareIgnoreSSLErrorOnOffText();
    }

    private void prepareOnlyWifiSwitch() {
        Log.d(DefaultsActivity.class.getName(), "prepareOnlyWifiSwitch");
        PreferenceManager preferenceManager = new PreferenceManager(this);
        onlyWifiSwitch = findViewById(R.id.switch_activity_defaults_only_wifi);
        onlyWifiOnOffText = findViewById(R.id.textview_activity_defaults_only_wifi_on_off);
        onlyWifiSwitch.setOnCheckedChangeListener(null);
        onlyWifiSwitch.setChecked(preferenceManager.getPreferenceOnlyWifi());
        onlyWifiSwitch.setOnCheckedChangeListener(this::onOnlyWifiCheckedChanged);
        prepareOnlyWifiOnOffText();
    }

    private void prepareOnlyWifiOnOffText() {
        onlyWifiOnOffText.setText(onlyWifiSwitch.isChecked() ? getResources().getString(R.string.string_yes) : getResources().getString(R.string.string_no));
    }

    private void onOnlyWifiCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.d(DefaultsActivity.class.getName(), "onOnlyWifiCheckedChanged, new value is " + isChecked);
        PreferenceManager preferenceManager = new PreferenceManager(this);
        preferenceManager.setPreferenceOnlyWifi(isChecked);
        prepareOnlyWifiOnOffText();
    }

    private void prepareNotificationSwitch() {
        Log.d(DefaultsActivity.class.getName(), "prepareNotificationSwitch");
        PreferenceManager preferenceManager = new PreferenceManager(this);
        notificationSwitch = findViewById(R.id.switch_activity_defaults_notification);
        notificationOnOffText = findViewById(R.id.textview_activity_defaults_notification_on_off);
        notificationSwitch.setOnCheckedChangeListener(null);
        notificationSwitch.setChecked(preferenceManager.getPreferenceNotification());
        notificationSwitch.setOnCheckedChangeListener(this::onNotificationCheckedChanged);
        prepareNotificationOnOffText();
    }

    private void prepareNotificationOnOffText() {
        notificationOnOffText.setText(notificationSwitch.isChecked() ? getResources().getString(R.string.string_yes) : getResources().getString(R.string.string_no));
    }

    private void onNotificationCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.d(DefaultsActivity.class.getName(), "onNotificationCheckedChanged, new value is " + isChecked);
        PreferenceManager preferenceManager = new PreferenceManager(this);
        preferenceManager.setPreferenceNotification(isChecked);
        prepareNotificationOnOffText();
    }

    private void prepareHighPrioSwitch() {
        Log.d(DefaultsActivity.class.getName(), "prepareHighPrioSwitch");
        PreferenceManager preferenceManager = new PreferenceManager(this);
        highPrioSwitch = findViewById(R.id.switch_activity_defaults_high_prio);
        highPrioOnOffText = findViewById(R.id.textview_activity_defaults_high_prio_on_off);
        highPrioSwitch.setOnCheckedChangeListener(null);
        highPrioSwitch.setChecked(preferenceManager.getPreferenceHighPrio());
        highPrioSwitch.setOnCheckedChangeListener(this::onHighPrioCheckedChanged);
        prepareHighPrioOnOffText();
    }

    private void prepareHighPrioOnOffText() {
        highPrioOnOffText.setText(highPrioSwitch.isChecked() ? getResources().getString(R.string.string_yes) : getResources().getString(R.string.string_no));
    }

    private void onHighPrioCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.d(DefaultsActivity.class.getName(), "onHighPrioCheckedChanged, new value is " + isChecked);
        PreferenceManager preferenceManager = new PreferenceManager(this);
        preferenceManager.setPreferenceHighPrio(isChecked);
        prepareHighPrioOnOffText();
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
            intervalMinutesText.setText(getResources().getQuantityString(R.plurals.string_minute, value));
        }
    }

    private String getPingCount() {
        return StringUtil.notNull(pingCountText.getText());
    }

    private void setPingCount(String pingCount) {
        pingCountText.setText(StringUtil.notNull(pingCount));
    }

    private String getPingPackageSize() {
        return StringUtil.notNull(pingPackageSizeText.getText());
    }

    private void setPingPackageSize(String pingPackageSize) {
        pingPackageSizeText.setText(StringUtil.notNull(pingPackageSize));
    }

    private String getConnectCount() {
        return StringUtil.notNull(connectCountText.getText());
    }

    private void setConnectCount(String connectCount) {
        connectCountText.setText(StringUtil.notNull(connectCount));
    }

    private void showAddressInputDialog(View view) {
        Log.d(DefaultsActivity.class.getName(), "showAddressInputDialog");
        List<String> validators = Arrays.asList(HostFieldValidator.class.getName(), URLFieldValidator.class.getName());
        SettingsInput input = new SettingsInput(SettingsInput.Type.ADDRESS, getAddress(), getResources().getString(R.string.label_activity_defaults_address), validators);
        showInputDialog(input.toBundle());
    }

    private void showPortInputDialog(View view) {
        Log.d(DefaultsActivity.class.getName(), "showPortInputDialog");
        List<String> validators = Collections.singletonList(PortFieldValidator.class.getName());
        SettingsInput input = new SettingsInput(SettingsInput.Type.PORT, getPort(), getResources().getString(R.string.label_activity_defaults_port), validators);
        showInputDialog(input.toBundle());
    }

    private void showIntervalInputDialog(View view) {
        Log.d(DefaultsActivity.class.getName(), "showIntervalInputDialog");
        List<String> validators = Collections.singletonList(IntervalFieldValidator.class.getName());
        SettingsInput input = new SettingsInput(SettingsInput.Type.INTERVAL, getInterval(), getResources().getString(R.string.label_activity_defaults_interval), validators);
        showInputDialog(input.toBundle());
    }

    private void showPingCountInputDialog(View view) {
        Log.d(DefaultsActivity.class.getName(), "showPingCountInputDialog");
        List<String> validators = Collections.singletonList(PingCountFieldValidator.class.getName());
        SettingsInput input = new SettingsInput(SettingsInput.Type.PINGCOUNT, getPingCount(), getResources().getString(R.string.label_activity_defaults_ping_count), validators);
        showInputDialog(input.toBundle());
    }

    private void showPingPackageSizeInputDialog(View view) {
        Log.d(DefaultsActivity.class.getName(), "showPingPackageSizeInputDialog");
        List<String> validators = Collections.singletonList(PingPackageSizeFieldValidator.class.getName());
        SettingsInput input = new SettingsInput(SettingsInput.Type.PINGPACKAGESIZE, getPingPackageSize(), getResources().getString(R.string.label_activity_defaults_ping_package_size), validators);
        showInputDialog(input.toBundle());
    }

    private void showConnectCountInputDialog(View view) {
        Log.d(DefaultsActivity.class.getName(), "showConnectCountInputDialog");
        List<String> validators = Collections.singletonList(ConnectCountFieldValidator.class.getName());
        SettingsInput input = new SettingsInput(SettingsInput.Type.CONNECTCOUNT, getConnectCount(), getResources().getString(R.string.label_activity_defaults_connect_count), validators);
        showInputDialog(input.toBundle());
    }

    private void showInputDialog(Bundle bundle) {
        Log.d(DefaultsActivity.class.getName(), "showInputDialog, opening SettingsInputDialog");
        SettingsInputDialog inputDialog = new SettingsInputDialog();
        inputDialog.setArguments(bundle);
        inputDialog.show(getSupportFragmentManager(), DefaultsActivity.class.getName());
    }

    @Override
    public void onInputDialogOkClicked(SettingsInputDialog inputDialog, SettingsInput type) {
        Log.d(DefaultsActivity.class.getName(), "onInputDialogOkClicked, type is " + type + ", value is " + inputDialog.getValue());
        PreferenceManager preferenceManager = new PreferenceManager(this);
        if (SettingsInput.Type.ADDRESS.equals(type.getType())) {
            String address = StringUtil.notNull(inputDialog.getValue()).trim();
            setAddress(address);
            preferenceManager.setPreferenceAddress(address);
        } else if (SettingsInput.Type.PORT.equals(type.getType())) {
            setPort(inputDialog.getValue());
            preferenceManager.setPreferencePort(NumberUtil.getIntValue(getPort(), getResources().getInteger(R.integer.task_port_default)));
        } else if (SettingsInput.Type.INTERVAL.equals(type.getType())) {
            setInterval(inputDialog.getValue());
            preferenceManager.setPreferenceInterval(NumberUtil.getIntValue(getInterval(), getResources().getInteger(R.integer.task_interval_default)));
        } else if (SettingsInput.Type.PINGCOUNT.equals(type.getType())) {
            setPingCount(inputDialog.getValue());
            preferenceManager.setPreferencePingCount(NumberUtil.getIntValue(getPingCount(), getResources().getInteger(R.integer.ping_count_default)));
        } else if (SettingsInput.Type.PINGPACKAGESIZE.equals(type.getType())) {
            setPingPackageSize(inputDialog.getValue());
            preferenceManager.setPreferencePingPackageSize(NumberUtil.getIntValue(getPingPackageSize(), getResources().getInteger(R.integer.ping_package_size_default)));
        } else if (SettingsInput.Type.CONNECTCOUNT.equals(type.getType())) {
            setConnectCount(inputDialog.getValue());
            preferenceManager.setPreferenceConnectCount(NumberUtil.getIntValue(getConnectCount(), getResources().getInteger(R.integer.connect_count_default)));
        } else {
            Log.e(DefaultsActivity.class.getName(), "type " + type.getType() + " unknown");
        }
        inputDialog.dismiss();
    }
}
