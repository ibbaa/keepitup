/*
 * Copyright (c) 2024. Alwin Ibba
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

package net.ibbaa.keepitup.ui.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.switchmaterial.SwitchMaterial;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.model.AccessType;
import net.ibbaa.keepitup.model.AccessTypeData;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.ui.ContextOptionsSupport;
import net.ibbaa.keepitup.ui.ContextOptionsSupportManager;
import net.ibbaa.keepitup.ui.NetworkTaskMainActivity;
import net.ibbaa.keepitup.ui.clipboard.IClipboardManager;
import net.ibbaa.keepitup.ui.clipboard.SystemClipboardManager;
import net.ibbaa.keepitup.ui.mapping.EnumMapping;
import net.ibbaa.keepitup.ui.permission.IPermissionManager;
import net.ibbaa.keepitup.ui.permission.PermissionManager;
import net.ibbaa.keepitup.ui.validation.AccessTypeDataValidator;
import net.ibbaa.keepitup.ui.validation.NetworkTaskValidator;
import net.ibbaa.keepitup.ui.validation.TextColorValidatingWatcher;
import net.ibbaa.keepitup.ui.validation.ValidationResult;
import net.ibbaa.keepitup.util.BundleUtil;
import net.ibbaa.keepitup.util.NumberUtil;
import net.ibbaa.keepitup.util.StringUtil;
import net.ibbaa.keepitup.util.UIUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings({"unused"})
public class NetworkTaskEditDialog extends DialogFragment implements ContextOptionsSupport {

    private View dialogView;
    private NetworkTask task;
    private AccessTypeData accessTypeData;
    private RadioGroup accessTypeGroup;
    private EditText addressEditText;
    private TextColorValidatingWatcher addressEditTextWatcher;
    private EditText portEditText;
    private TextColorValidatingWatcher portEditTextWatcher;
    private EditText intervalEditText;
    private TextColorValidatingWatcher intervalEditTextWatcher;
    private EditText pingCountEditText;
    private TextColorValidatingWatcher pingCountEditTextWatcher;
    private EditText connectCountEditText;
    private TextColorValidatingWatcher connectCountEditTextWatcher;
    private EditText pingPackageSizeEditText;
    private TextColorValidatingWatcher pingPackageSizeEditTextWatcher;
    private SwitchMaterial onlyWifiSwitch;
    private SwitchMaterial notificationSwitch;
    private TextView onlyWifiOnOffText;
    private TextView notificationOnOffText;

    private IClipboardManager clipboardManager;
    private IPermissionManager permissionManager;

    public void injectClipboardManager(IClipboardManager clipboardManager) {
        this.clipboardManager = clipboardManager;
    }

    public IClipboardManager getClipboardManager() {
        if (clipboardManager != null) {
            return clipboardManager;
        }
        return new SystemClipboardManager(requireContext());
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
    public void onCreate(Bundle savedInstanceState) {
        Log.d(NetworkTaskEditDialog.class.getName(), "onCreate");
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.DialogTheme);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(NetworkTaskEditDialog.class.getName(), "onCreateView");
        dialogView = inflater.inflate(R.layout.dialog_network_task_edit, container);
        Bundle taskBundle = BundleUtil.bundleFromBundle(getTaskKey(), requireArguments());
        task = taskBundle != null ? new NetworkTask(taskBundle) : new NetworkTask();
        Bundle accessTypeDataBundle = BundleUtil.bundleFromBundle(getAccessTypeDataKey(), requireArguments());
        accessTypeData = accessTypeDataBundle != null ? new AccessTypeData(accessTypeDataBundle) : new AccessTypeData();
        prepareAccessTypeRadioButtons(savedInstanceState);
        prepareAddressTextFields();
        prepareAddressTextFieldsVisibility();
        prepareIntervalTextField();
        prepareAccessTypeDataFields();
        prepareAccessTypeDataFieldsVisibility();
        prepareOnlyWifiSwitch();
        prepareNotificationSwitch();
        prepareOkCancelImageButtons();
        return dialogView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        int selectedId = accessTypeGroup.getCheckedRadioButtonId();
        RadioButton selectedAccessTypeRadioButton = dialogView.findViewById(selectedId);
        if (selectedAccessTypeRadioButton != null) {
            AccessType accessType = (AccessType) selectedAccessTypeRadioButton.getTag();
            outState.putInt(getAccessTypeBundleKey(), accessType.getCode());
        }
    }

    public int getPosition() {
        Log.d(NetworkTaskEditDialog.class.getName(), "getPosition");
        return BundleUtil.integerFromBundle(getPositionKey(), requireArguments());
    }

    public String getPositionKey() {
        return NetworkTaskEditDialog.class.getName() + ".Position";
    }

    public String getTaskKey() {
        return NetworkTaskEditDialog.class.getName() + ".Task";
    }

    public String getAccessTypeDataKey() {
        return NetworkTaskEditDialog.class.getName() + ".AccessTypeData";
    }

    private String getAccessTypeBundleKey() {
        return NetworkTaskEditDialog.class.getName() + ".AccessType";
    }

    private AccessType getAccessType() {
        int selectedId = accessTypeGroup.getCheckedRadioButtonId();
        RadioButton selectedAccessTypeRadioButton = accessTypeGroup.findViewById(selectedId);
        AccessType accessType = null;
        if (selectedAccessTypeRadioButton != null) {
            accessType = (AccessType) selectedAccessTypeRadioButton.getTag();
        }
        return accessType;
    }

    private String getAddress() {
        return StringUtil.notNull(addressEditText.getText());
    }

    private String getPort() {
        return StringUtil.notNull(portEditText.getText());
    }

    private String getInterval() {
        return StringUtil.notNull(intervalEditText.getText());
    }

    private String getPingCount() {
        return StringUtil.notNull(pingCountEditText.getText());
    }

    private String getConnectCount() {
        return StringUtil.notNull(connectCountEditText.getText());
    }

    private String getPingPackageSize() {
        return StringUtil.notNull(pingPackageSizeEditText.getText());
    }

    private boolean isPortVisible() {
        return portEditText.getVisibility() == View.VISIBLE;
    }

    private boolean isPingCountVisible() {
        return pingCountEditText.getVisibility() == View.VISIBLE;
    }

    private boolean isConnectCountVisible() {
        return connectCountEditText.getVisibility() == View.VISIBLE;
    }

    private boolean isPingPackageSizeVisible() {
        return pingPackageSizeEditText.getVisibility() == View.VISIBLE;
    }

    private void prepareAccessTypeRadioButtons(Bundle savedInstanceState) {
        Log.d(NetworkTaskEditDialog.class.getName(), "prepareAccessTypeRadioButtons with access type of " + task.getAccessType());
        accessTypeGroup = dialogView.findViewById(R.id.radiogroup_dialog_network_task_edit_accesstype);
        EnumMapping mapping = new EnumMapping(requireContext());
        AccessType[] accessTypes = AccessType.values();
        AccessType savedSelectedAccessType = null;
        if (savedInstanceState != null) {
            int selectedCode = savedInstanceState.getInt(getAccessTypeBundleKey(), -1);
            if (selectedCode >= 0) {
                savedSelectedAccessType = AccessType.forCode(selectedCode);
            }
            savedInstanceState.remove(getAccessTypeBundleKey());
        }
        for (int ii = 0; ii < accessTypes.length; ii++) {
            AccessType accessType = accessTypes[ii];
            RadioButton newRadioButton = new RadioButton(requireContext());
            newRadioButton.setText(mapping.getAccessTypeText(accessType));
            newRadioButton.setTextColor(UIUtil.getStyledColor(requireActivity(), android.R.attr.textColor));
            newRadioButton.setId(View.generateViewId());
            if (savedSelectedAccessType != null) {
                newRadioButton.setChecked(accessType.equals(savedSelectedAccessType));
            } else {
                if (task.getAccessType() == null && ii == 0) {
                    newRadioButton.setChecked(true);
                } else {
                    newRadioButton.setChecked(accessType.equals(task.getAccessType()));
                }
            }
            newRadioButton.setTag(accessType);
            LinearLayout.LayoutParams layoutParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
            accessTypeGroup.addView(newRadioButton, ii, layoutParams);
        }
        accessTypeGroup.setOnCheckedChangeListener(this::onAccessTypeChanged);
    }

    private void onAccessTypeChanged(RadioGroup group, int checkedId) {
        prepareAddressTextFieldsVisibility();
        prepareAccessTypeDataFieldsVisibility();
        validateInput();
    }

    private void prepareAddressTextFields() {
        Log.d(NetworkTaskEditDialog.class.getName(), "prepareAddressTextFields with address of " + task.getAddress() + " and port of " + task.getPort());
        addressEditText = dialogView.findViewById(R.id.edittext_dialog_network_task_edit_address);
        prepareAddressEditTextListener();
        addressEditText.setOnLongClickListener(this::onEditTextLongClicked);
        addressEditText.setText(StringUtil.notNull(task.getAddress()));
        portEditText = dialogView.findViewById(R.id.edittext_dialog_network_task_edit_port);
        portEditText.setOnLongClickListener(this::onEditTextLongClicked);
        preparePortEditTextListener();
        portEditText.setText(String.valueOf(task.getPort()));
    }

    private void prepareAddressTextFieldsVisibility() {
        Log.d(NetworkTaskEditDialog.class.getName(), "prepareAddressTextFieldsVisibility with address of " + task.getAddress() + " and port of " + task.getPort());
        EnumMapping mapping = new EnumMapping(requireContext());
        RadioGroup accessTypeGroup = dialogView.findViewById(R.id.radiogroup_dialog_network_task_edit_accesstype);
        int selectedId = accessTypeGroup.getCheckedRadioButtonId();
        RadioButton selectedAccessTypeRadioButton = dialogView.findViewById(selectedId);
        if (selectedAccessTypeRadioButton == null) {
            Log.d(NetworkTaskEditDialog.class.getName(), "prepareAddressTextFieldsVisibility, selectedAccessTypeRadioButton is null, no access type selected");
            return;
        }
        AccessType accessType = (AccessType) selectedAccessTypeRadioButton.getTag();
        TextView addressTextView = dialogView.findViewById(R.id.textview_dialog_network_task_edit_address_label);
        addressTextView.setText(mapping.getAccessTypeAddressLabel(accessType));
        TextView portTextView = dialogView.findViewById(R.id.textview_dialog_network_task_edit_port_label);
        LinearLayout portLinearLayout = dialogView.findViewById(R.id.linearlayout_dialog_network_task_edit_port);
        if (accessType.needsPort()) {
            portTextView.setText(mapping.getAccessTypePortLabel(accessType));
            portTextView.setVisibility(View.VISIBLE);
            portEditText.setVisibility(View.VISIBLE);
            portLinearLayout.setVisibility(View.VISIBLE);
        } else {
            portTextView.setVisibility(View.GONE);
            portEditText.setVisibility(View.GONE);
            portLinearLayout.setVisibility(View.GONE);
        }
    }

    private void prepareAddressEditTextListener() {
        Log.d(NetworkTaskEditDialog.class.getName(), "prepareAddressEditTextListener");
        if (addressEditTextWatcher != null) {
            addressEditText.removeTextChangedListener(addressEditTextWatcher);
            addressEditTextWatcher = null;
        }
        addressEditTextWatcher = new TextColorValidatingWatcher(addressEditText, this::validateAddress, getColor(R.color.textColor), getColor(R.color.textErrorColor));
        addressEditText.addTextChangedListener(addressEditTextWatcher);
    }

    private void preparePortEditTextListener() {
        Log.d(NetworkTaskEditDialog.class.getName(), "preparePortEditTextListener");
        if (portEditTextWatcher != null) {
            portEditText.removeTextChangedListener(portEditTextWatcher);
            portEditTextWatcher = null;
        }
        portEditTextWatcher = new TextColorValidatingWatcher(portEditText, this::validatePort, getColor(R.color.textColor), getColor(R.color.textErrorColor));
        portEditText.addTextChangedListener(portEditTextWatcher);
    }

    private void prepareIntervalTextField() {
        Log.d(NetworkTaskEditDialog.class.getName(), "prepareIntervalTextField with interval of " + task.getInterval());
        intervalEditText = dialogView.findViewById(R.id.edittext_dialog_network_task_edit_interval);
        prepareIntervalEditTextListener();
        intervalEditText.setOnLongClickListener(this::onEditTextLongClicked);
        intervalEditText.setText(String.valueOf(task.getInterval()));
    }

    private void prepareIntervalEditTextListener() {
        Log.d(NetworkTaskEditDialog.class.getName(), "prepareIntervalEditTextListener");
        if (intervalEditTextWatcher != null) {
            intervalEditText.removeTextChangedListener(intervalEditTextWatcher);
            intervalEditTextWatcher = null;
        }
        intervalEditTextWatcher = new TextColorValidatingWatcher(intervalEditText, this::validateInterval, getColor(R.color.textColor), getColor(R.color.textErrorColor));
        intervalEditText.addTextChangedListener(intervalEditTextWatcher);
    }

    private void prepareAccessTypeDataFields() {
        Log.d(NetworkTaskEditDialog.class.getName(), "prepareAccessTypeDataFields with acccess type data of " + accessTypeData);
        pingCountEditText = dialogView.findViewById(R.id.edittext_dialog_network_task_edit_ping_count);
        preparePingCountEditTextListener();
        pingCountEditText.setOnLongClickListener(this::onEditTextLongClicked);
        pingCountEditText.setText(String.valueOf(accessTypeData.getPingCount()));
        connectCountEditText = dialogView.findViewById(R.id.edittext_dialog_network_task_edit_connect_count);
        prepareConnectCountEditTextListener();
        connectCountEditText.setOnLongClickListener(this::onEditTextLongClicked);
        connectCountEditText.setText(String.valueOf(accessTypeData.getConnectCount()));
        pingPackageSizeEditText = dialogView.findViewById(R.id.edittext_dialog_network_task_edit_ping_package_size);
        preparePingPackageSizeEditTextListener();
        pingPackageSizeEditText.setOnLongClickListener(this::onEditTextLongClicked);
        pingPackageSizeEditText.setText(String.valueOf(accessTypeData.getPingPackageSize()));
    }

    private void prepareAccessTypeDataFieldsVisibility() {
        Log.d(NetworkTaskEditDialog.class.getName(), "prepareAccessTypeDataFieldsVisibility with acccess type data of " + accessTypeData);
        EnumMapping mapping = new EnumMapping(requireContext());
        RadioGroup accessTypeGroup = dialogView.findViewById(R.id.radiogroup_dialog_network_task_edit_accesstype);
        int selectedId = accessTypeGroup.getCheckedRadioButtonId();
        RadioButton selectedAccessTypeRadioButton = dialogView.findViewById(selectedId);
        if (selectedAccessTypeRadioButton == null) {
            Log.d(NetworkTaskEditDialog.class.getName(), "prepareAccessTypeDataFieldsVisibility, selectedAccessTypeRadioButton is null, no access type selected");
            return;
        }
        AccessType accessType = (AccessType) selectedAccessTypeRadioButton.getTag();
        LinearLayout pingCountLinearLayout = dialogView.findViewById(R.id.linearlayout_dialog_network_task_edit_ping_count);
        LinearLayout connectCountLinearLayout = dialogView.findViewById(R.id.linearlayout_dialog_network_task_edit_connect_count);
        LinearLayout pingPackageSizeLinearLayout = dialogView.findViewById(R.id.linearlayout_dialog_network_task_edit_ping_package_size);
        TextView pingCountTextView = dialogView.findViewById(R.id.textview_dialog_network_task_edit_ping_count_label);
        TextView connectCountTextView = dialogView.findViewById(R.id.textview_dialog_network_task_edit_connect_count_label);
        TextView pingPackageSizeTextView = dialogView.findViewById(R.id.textview_dialog_network_task_edit_ping_package_size_label);
        if (accessType.isPing()) {
            pingCountTextView.setVisibility(View.VISIBLE);
            pingCountEditText.setVisibility(View.VISIBLE);
            pingCountLinearLayout.setVisibility(View.VISIBLE);
            pingPackageSizeTextView.setVisibility(View.VISIBLE);
            pingPackageSizeEditText.setVisibility(View.VISIBLE);
            pingPackageSizeLinearLayout.setVisibility(View.VISIBLE);
        } else {
            pingCountTextView.setVisibility(View.GONE);
            pingCountEditText.setVisibility(View.GONE);
            pingCountLinearLayout.setVisibility(View.GONE);
            pingPackageSizeTextView.setVisibility(View.GONE);
            pingPackageSizeEditText.setVisibility(View.GONE);
            pingPackageSizeLinearLayout.setVisibility(View.GONE);
        }
        if (accessType.isConnect()) {
            connectCountTextView.setVisibility(View.VISIBLE);
            connectCountEditText.setVisibility(View.VISIBLE);
            connectCountLinearLayout.setVisibility(View.VISIBLE);
        } else {
            connectCountTextView.setVisibility(View.GONE);
            connectCountEditText.setVisibility(View.GONE);
            connectCountLinearLayout.setVisibility(View.GONE);
        }
    }

    private void preparePingCountEditTextListener() {
        Log.d(NetworkTaskEditDialog.class.getName(), "preparePingCountEditTextListener");
        if (pingCountEditTextWatcher != null) {
            pingCountEditText.removeTextChangedListener(pingCountEditTextWatcher);
            pingCountEditTextWatcher = null;
        }
        pingCountEditTextWatcher = new TextColorValidatingWatcher(pingCountEditText, this::validatePingCount, getColor(R.color.textColor), getColor(R.color.textErrorColor));
        pingCountEditText.addTextChangedListener(pingCountEditTextWatcher);
    }

    private void prepareConnectCountEditTextListener() {
        Log.d(NetworkTaskEditDialog.class.getName(), "prepareConnectCountEditTextListener");
        if (connectCountEditTextWatcher != null) {
            connectCountEditText.removeTextChangedListener(connectCountEditTextWatcher);
            connectCountEditTextWatcher = null;
        }
        connectCountEditTextWatcher = new TextColorValidatingWatcher(connectCountEditText, this::validateConnectCount, getColor(R.color.textColor), getColor(R.color.textErrorColor));
        connectCountEditText.addTextChangedListener(connectCountEditTextWatcher);
    }

    private void preparePingPackageSizeEditTextListener() {
        Log.d(NetworkTaskEditDialog.class.getName(), "preparePingPackageSizeEditTextListener");
        if (pingPackageSizeEditTextWatcher != null) {
            pingPackageSizeEditText.removeTextChangedListener(pingPackageSizeEditTextWatcher);
            pingPackageSizeEditTextWatcher = null;
        }
        pingPackageSizeEditTextWatcher = new TextColorValidatingWatcher(connectCountEditText, this::validatePingPackageSize, getColor(R.color.textColor), getColor(R.color.textErrorColor));
        pingPackageSizeEditText.addTextChangedListener(pingPackageSizeEditTextWatcher);
    }

    private void prepareOnlyWifiSwitch() {
        Log.d(NetworkTaskEditDialog.class.getName(), "prepareOnlyWifiSwitch with only wifi setting of " + task.isOnlyWifi());
        onlyWifiSwitch = dialogView.findViewById(R.id.switch_dialog_network_task_edit_onlywifi);
        onlyWifiOnOffText = dialogView.findViewById(R.id.textview_dialog_network_task_edit_onlywifi_on_off);
        onlyWifiSwitch.setChecked(task.isOnlyWifi());
        onlyWifiSwitch.setOnCheckedChangeListener(this::onOnlyWifiCheckedChanged);
        prepareOnlyWifiOnOffText();
    }

    private void prepareNotificationSwitch() {
        Log.d(NetworkTaskEditDialog.class.getName(), "prepareNotificationSwitch with notification setting of " + task.isNotification());
        notificationSwitch = dialogView.findViewById(R.id.switch_dialog_network_task_edit_notification);
        notificationOnOffText = dialogView.findViewById(R.id.textview_dialog_network_task_edit_notification_on_off);
        boolean hasPostNotificationsPermission = getPermissionManager().hasPostNotificationsPermission(requireContext());
        Log.d(NetworkTaskEditDialog.class.getName(), "hasPostNotificationsPermission is " + hasPostNotificationsPermission);
        if (hasPostNotificationsPermission) {
            notificationSwitch.setEnabled(true);
            notificationSwitch.setChecked(task.isNotification());
            notificationSwitch.setOnCheckedChangeListener(this::onNotificationCheckedChanged);
        } else {
            notificationSwitch.setEnabled(false);
            notificationSwitch.setChecked(false);
            notificationSwitch.setOnCheckedChangeListener(null);
        }
        prepareNotificationOnOffText();
    }

    private void prepareOnlyWifiOnOffText() {
        onlyWifiOnOffText.setText(onlyWifiSwitch.isChecked() ? getResources().getString(R.string.string_yes) : getResources().getString(R.string.string_no));
    }

    private void prepareNotificationOnOffText() {
        notificationOnOffText.setText(notificationSwitch.isChecked() ? getResources().getString(R.string.string_yes) : getResources().getString(R.string.string_no));
    }

    private void prepareOkCancelImageButtons() {
        Log.d(NetworkTaskEditDialog.class.getName(), "prepareOkCancelImageButtons");
        ImageView okImage = dialogView.findViewById(R.id.imageview_dialog_network_task_edit_ok);
        ImageView cancelImage = dialogView.findViewById(R.id.imageview_dialog_network_task_edit_cancel);
        okImage.setOnClickListener(this::onOkClicked);
        cancelImage.setOnClickListener(this::onCancelClicked);
    }

    public NetworkTask getInitialNetworkTask() {
        return task;
    }

    public AccessTypeData getInitialAccessTypeData() {
        return accessTypeData;
    }

    public NetworkTask getNetworkTask() {
        Bundle taskBundle = BundleUtil.bundleFromBundle(getTaskKey(), requireArguments());
        NetworkTask task = taskBundle != null ? new NetworkTask(taskBundle) : new NetworkTask();
        AccessType accessType = getAccessType();
        if (accessType != null) {
            task.setAccessType(accessType);
        }
        task.setAddress(getAddress());
        if (isPortVisible()) {
            if (NumberUtil.isValidIntValue(getPort())) {
                task.setPort(NumberUtil.getIntValue(getPort(), task.getPort()));
            }
        }
        if (NumberUtil.isValidIntValue(getInterval())) {
            task.setInterval(NumberUtil.getIntValue(getInterval(), task.getInterval()));
        }
        task.setOnlyWifi(onlyWifiSwitch.isChecked());
        task.setNotification(notificationSwitch.isChecked());
        Log.d(NetworkTaskEditDialog.class.getName(), "getNetworkTask, network task is " + task);
        return task;
    }

    public AccessTypeData getAccessTypeData() {
        Bundle accessTypeDataBundle = BundleUtil.bundleFromBundle(getAccessTypeDataKey(), requireArguments());
        AccessTypeData accessTypeData = accessTypeDataBundle != null ? new AccessTypeData(accessTypeDataBundle) : new AccessTypeData();
        if (isPingCountVisible()) {
            if (NumberUtil.isValidIntValue(getPingCount())) {
                accessTypeData.setPingCount(NumberUtil.getIntValue(getPingCount(), accessTypeData.getPingCount()));
            }
        }
        if (isConnectCountVisible()) {
            if (NumberUtil.isValidIntValue(getConnectCount())) {
                accessTypeData.setConnectCount(NumberUtil.getIntValue(getConnectCount(), accessTypeData.getConnectCount()));
            }
        }
        if (isPingPackageSizeVisible()) {
            if (NumberUtil.isValidIntValue(getPingPackageSize())) {
                accessTypeData.setPingPackageSize(NumberUtil.getIntValue(getPingPackageSize(), accessTypeData.getPingPackageSize()));
            }
        }
        Log.d(NetworkTaskEditDialog.class.getName(), "getAccessTypeData, access type data task is " + accessTypeData);
        return accessTypeData;
    }

    private void onOkClicked(View view) {
        Log.d(NetworkTaskEditDialog.class.getName(), "onOkClicked");
        NetworkTaskMainActivity activity = (NetworkTaskMainActivity) getActivity();
        List<ValidationResult> validationResult = validateFinalInput();
        if (!hasErrors(validationResult)) {
            Log.d(NetworkTaskEditDialog.class.getName(), "Validation was successful");
            Objects.requireNonNull(activity).onEditDialogOkClicked(this);
        } else {
            Log.d(NetworkTaskEditDialog.class.getName(), "Validation failed");
            showValidatorErrorDialog(validationResult);
        }
    }

    private void onCancelClicked(View view) {
        Log.d(NetworkTaskEditDialog.class.getName(), "onCancelClicked");
        NetworkTaskMainActivity activity = (NetworkTaskMainActivity) getActivity();
        Objects.requireNonNull(activity).onEditDialogCancelClicked(this);
    }

    private void onOnlyWifiCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.d(NetworkTaskEditDialog.class.getName(), "onOnlyWifiCheckedChanged, new value is " + isChecked);
        prepareOnlyWifiOnOffText();
    }

    private void onNotificationCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.d(NetworkTaskEditDialog.class.getName(), "onCheckedChanged, new value is " + isChecked);
        prepareNotificationOnOffText();
    }

    private boolean hasErrors(List<ValidationResult> validationResult) {
        return !validationResult.isEmpty();
    }

    private void validateInput() {
        Log.d(NetworkTaskEditDialog.class.getName(), "validateInput");
        NetworkTaskValidator networkTaskValidator = getNetworkTaskValidator();
        AccessTypeDataValidator accessTypeDataValidator = getAccessTypeDataValidator();
        setValidationResultColor(addressEditText, networkTaskValidator.validateAddress(getAddress()).isValidationSuccessful());
        if (isPortVisible()) {
            setValidationResultColor(portEditText, networkTaskValidator.validatePort(getPort()).isValidationSuccessful());
        }
        setValidationResultColor(intervalEditText, networkTaskValidator.validateInterval(getInterval()).isValidationSuccessful());
        if (isPingCountVisible()) {
            setValidationResultColor(pingCountEditText, accessTypeDataValidator.validatePingCount(getPingCount()).isValidationSuccessful());
        }
        if (isConnectCountVisible()) {
            setValidationResultColor(connectCountEditText, accessTypeDataValidator.validateConnectCount(getConnectCount()).isValidationSuccessful());
        }
        if (isPingPackageSizeVisible()) {
            setValidationResultColor(pingPackageSizeEditText, accessTypeDataValidator.validatePingPackageSize(getPingPackageSize()).isValidationSuccessful());
        }
    }

    private void setValidationResultColor(EditText editText, boolean success) {
        if (success) {
            editText.setTextColor(getColor(R.color.textColor));
        } else {
            editText.setTextColor(getColor(R.color.textErrorColor));
        }
    }

    private List<ValidationResult> validateFinalInput() {
        Log.d(NetworkTaskEditDialog.class.getName(), "validateFinalInput");
        List<ValidationResult> validationResultList = new ArrayList<>();
        validateFinalNetworkTask(validationResultList);
        validateFinalAccessTypeData(validationResultList);
        return validationResultList;
    }

    private void validateFinalNetworkTask(List<ValidationResult> validationResultList) {
        Log.d(NetworkTaskEditDialog.class.getName(), "validateFinalNetworkTask");
        NetworkTaskValidator networkTaskValidator = getNetworkTaskValidator();
        ValidationResult result = networkTaskValidator.validateAddress(getAddress());
        Log.d(NetworkTaskEditDialog.class.getName(), "address validation result: " + result);
        if (!result.isValidationSuccessful()) {
            validationResultList.add(result);
        }
        if (isPortVisible()) {
            result = networkTaskValidator.validatePort(getPort());
            Log.d(NetworkTaskEditDialog.class.getName(), "port validation result: " + result);
            if (!result.isValidationSuccessful()) {
                validationResultList.add(result);
            }
        } else {
            Log.d(NetworkTaskEditDialog.class.getName(), "port validation skipped");
        }
        result = networkTaskValidator.validateInterval(getInterval());
        Log.d(NetworkTaskEditDialog.class.getName(), "interval validation result: " + result);
        if (!result.isValidationSuccessful()) {
            validationResultList.add(result);
        }
    }

    private void validateFinalAccessTypeData(List<ValidationResult> validationResultList) {
        Log.d(NetworkTaskEditDialog.class.getName(), "validateFinalAccessTypeData");
        AccessTypeDataValidator accessTypeDataValidator = getAccessTypeDataValidator();
        if (isPingCountVisible()) {
            ValidationResult result = accessTypeDataValidator.validatePingCount(getPingCount());
            Log.d(NetworkTaskEditDialog.class.getName(), "ping count validation result: " + result);
            if (!result.isValidationSuccessful()) {
                validationResultList.add(result);
            }
        } else {
            Log.d(NetworkTaskEditDialog.class.getName(), "ping count validation skipped");
        }
        if (isConnectCountVisible()) {
            ValidationResult result = accessTypeDataValidator.validateConnectCount(getConnectCount());
            Log.d(NetworkTaskEditDialog.class.getName(), "connect count validation result: " + result);
            if (!result.isValidationSuccessful()) {
                validationResultList.add(result);
            }
        } else {
            Log.d(NetworkTaskEditDialog.class.getName(), "connect count validation skipped");
        }
        if (isPingPackageSizeVisible()) {
            ValidationResult result = accessTypeDataValidator.validatePingPackageSize(getPingPackageSize());
            Log.d(NetworkTaskEditDialog.class.getName(), "ping package size validation result: " + result);
            if (!result.isValidationSuccessful()) {
                validationResultList.add(result);
            }
        } else {
            Log.d(NetworkTaskEditDialog.class.getName(), "ping package size count validation skipped");
        }
    }

    private boolean validateAddress(EditText editText) {
        Log.d(NetworkTaskEditDialog.class.getName(), "validateAddress");
        NetworkTaskValidator validator = getNetworkTaskValidator();
        ValidationResult result = validator.validateAddress(getAddress());
        Log.d(NetworkTaskEditDialog.class.getName(), "address validation result: " + result);
        return result.isValidationSuccessful();
    }

    private boolean validatePort(EditText editText) {
        Log.d(NetworkTaskEditDialog.class.getName(), "validatePort");
        NetworkTaskValidator validator = getNetworkTaskValidator();
        ValidationResult result = validator.validatePort(getPort());
        Log.d(NetworkTaskEditDialog.class.getName(), "port validation result: " + result);
        return result.isValidationSuccessful();
    }

    private boolean validateInterval(EditText editText) {
        Log.d(NetworkTaskEditDialog.class.getName(), "validateInterval");
        NetworkTaskValidator validator = getNetworkTaskValidator();
        ValidationResult result = validator.validateInterval(getInterval());
        Log.d(NetworkTaskEditDialog.class.getName(), "interval validation result: " + result);
        return result.isValidationSuccessful();
    }

    private boolean validatePingCount(EditText editText) {
        Log.d(NetworkTaskEditDialog.class.getName(), "validatePingCount");
        AccessTypeDataValidator validator = getAccessTypeDataValidator();
        ValidationResult result = validator.validatePingCount(getPingCount());
        Log.d(NetworkTaskEditDialog.class.getName(), "ping count validation result: " + result);
        return result.isValidationSuccessful();
    }

    private boolean validateConnectCount(EditText editText) {
        Log.d(NetworkTaskEditDialog.class.getName(), "validateConnectCount");
        AccessTypeDataValidator validator = getAccessTypeDataValidator();
        ValidationResult result = validator.validateConnectCount(getConnectCount());
        Log.d(NetworkTaskEditDialog.class.getName(), "connect count validation result: " + result);
        return result.isValidationSuccessful();
    }

    private boolean validatePingPackageSize(EditText editText) {
        Log.d(NetworkTaskEditDialog.class.getName(), "validatePingPackageSize");
        AccessTypeDataValidator validator = getAccessTypeDataValidator();
        ValidationResult result = validator.validatePingPackageSize(getPingPackageSize());
        Log.d(NetworkTaskEditDialog.class.getName(), "ping package size validation result: " + result);
        return result.isValidationSuccessful();
    }

    @NonNull
    private NetworkTaskValidator getNetworkTaskValidator() {
        EnumMapping mapping = new EnumMapping(requireContext());
        AccessType accessType = getAccessType();
        NetworkTaskValidator validator = mapping.getNetworkTaskValidator(accessType);
        Log.d(NetworkTaskEditDialog.class.getName(), "Validator is " + validator.getClass().getSimpleName() + " for access type " + accessType);
        return validator;
    }

    @NonNull
    private AccessTypeDataValidator getAccessTypeDataValidator() {
        EnumMapping mapping = new EnumMapping(requireContext());
        AccessType accessType = getAccessType();
        AccessTypeDataValidator validator = mapping.getAccessTypeDataValidator(accessType);
        Log.d(NetworkTaskEditDialog.class.getName(), "Validator is " + validator.getClass().getSimpleName() + " for access type " + accessType);
        return validator;
    }

    private void showValidatorErrorDialog(List<ValidationResult> validationResult) {
        Log.d(NetworkTaskEditDialog.class.getName(), "showValidatorErrorDialog");
        ValidatorErrorDialog errorDialog = new ValidatorErrorDialog();
        errorDialog.setArguments(BundleUtil.validationResultListToBundle(errorDialog.getValidationResultBaseKey(), validationResult));
        errorDialog.show(getParentFragmentManager(), ValidatorErrorDialog.class.getName());
    }

    private void showContextOptionsDialog(EditText editText) {
        Log.d(NetworkTaskEditDialog.class.getName(), "showContextOptionsDialog");
        new ContextOptionsSupportManager(getParentFragmentManager(), getClipboardManager()).showContextOptionsDialog(editText);
    }

    private boolean onEditTextLongClicked(View view) {
        Log.d(NetworkTaskEditDialog.class.getName(), "onEditTextLongClicked");
        if (view instanceof EditText) {
            showContextOptionsDialog((EditText) view);
            return true;
        }
        Log.e(NetworkTaskEditDialog.class.getName(), "view is not an instance of EditTest");
        return false;
    }

    @Override
    public void onContextOptionsDialogClicked(ContextOptionsDialog contextOptionsDialog, int sourceResourceId, ContextOption option) {
        Log.d(NetworkTaskEditDialog.class.getName(), "onContextOptionsDialogEntryClicked, sourceResourceId is " + sourceResourceId + ", option is " + option);
        ContextOptionsSupportManager contextOptionsSupportManager = new ContextOptionsSupportManager(getParentFragmentManager(), getClipboardManager());
        EditText editText = null;
        if (addressEditText.getId() == sourceResourceId) {
            Log.d(NetworkTaskEditDialog.class.getName(), "Source field is the address field");
            editText = addressEditText;
        } else if (portEditText.getId() == sourceResourceId) {
            Log.d(NetworkTaskEditDialog.class.getName(), "Source field is the port field");
            editText = portEditText;
        } else if (intervalEditText.getId() == sourceResourceId) {
            Log.d(NetworkTaskEditDialog.class.getName(), "Source field is the interval field");
            editText = intervalEditText;
        }
        if (editText != null) {
            contextOptionsSupportManager.handleContextOption(editText, option);
            editText.setSelection(editText.getText().length());
        } else {
            Log.e(NetworkTaskEditDialog.class.getName(), "Source field is undefined.");
        }
        contextOptionsDialog.dismiss();
    }

    private int getColor(int colorid) {
        return ContextCompat.getColor(requireContext(), colorid);
    }
}
