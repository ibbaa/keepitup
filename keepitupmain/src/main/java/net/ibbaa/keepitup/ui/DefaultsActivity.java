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
import net.ibbaa.keepitup.ui.validation.HostFieldValidator;
import net.ibbaa.keepitup.ui.validation.IntervalFieldValidator;
import net.ibbaa.keepitup.ui.validation.PortFieldValidator;
import net.ibbaa.keepitup.ui.validation.URLFieldValidator;
import net.ibbaa.keepitup.util.NumberUtil;
import net.ibbaa.keepitup.util.StringUtil;
import net.ibbaa.keepitup.util.UIUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DefaultsActivity extends SettingsInputActivity {

    private RadioGroup accessTypeGroup;
    private TextView addressText;
    private TextView portText;
    private TextView intervalText;
    private TextView intervalMinutesText;
    private SwitchMaterial onlyWifiSwitch;
    private TextView onlyWifiOnOffText;
    private SwitchMaterial notificationSwitch;
    private TextView notificationOnOffText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setContentView(R.layout.activity_defaults);
        prepareAccessTypeRadioButtons();
        prepareAddressField();
        preparePortField();
        prepareIntervalField();
        prepareOnlyWifiSwitch();
        prepareNotificationSwitch();
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
            newRadioButton.setTextColor(UIUtil.getStyledColor(this, android.R.attr.textColor));
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

    private void prepareOnlyWifiSwitch() {
        Log.d(DefaultsActivity.class.getName(), "prepareOnlyWifiSwitch");
        PreferenceManager preferenceManager = new PreferenceManager(this);
        onlyWifiSwitch = findViewById(R.id.switch_activity_defaults_onlywifi);
        onlyWifiOnOffText = findViewById(R.id.textview_activity_defaults_onlywifi_on_off);
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

    private void showInputDialog(Bundle bundle) {
        Log.d(DefaultsActivity.class.getName(), "showInputDialog, opening SettingsInputDialog");
        SettingsInputDialog inputDialog = new SettingsInputDialog();
        inputDialog.setArguments(bundle);
        inputDialog.show(getSupportFragmentManager(), DefaultsActivity.class.getName());
    }

    @Override
    public void onInputDialogOkClicked(SettingsInputDialog inputDialog, SettingsInput.Type type) {
        Log.d(DefaultsActivity.class.getName(), "onInputDialogOkClicked, type is " + type + ", value is " + inputDialog.getValue());
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
        } else {
            Log.e(DefaultsActivity.class.getName(), "type " + type + " unknown");
        }
        inputDialog.dismiss();
    }
}
