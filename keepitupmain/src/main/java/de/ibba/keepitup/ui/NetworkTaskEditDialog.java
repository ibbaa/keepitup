package de.ibba.keepitup.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Objects;

import de.ibba.keepitup.R;
import de.ibba.keepitup.model.AccessType;
import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.ui.mapping.EnumMapping;
import de.ibba.keepitup.ui.validation.ValidationResult;
import de.ibba.keepitup.ui.validation.Validator;
import de.ibba.keepitup.util.NumberUtil;
import de.ibba.keepitup.util.StringUtil;

public class NetworkTaskEditDialog extends DialogFragment {

    private RadioGroup accessTypeGroup;
    private EditText addressEditText;
    private EditText portEditText;
    private EditText intervalEditText;
    private Switch notificationSwitch;
    private TextView notificationOnOffText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.DialogTheme);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_edit_network_task, container);
        NetworkTask task = new NetworkTask(Objects.requireNonNull(getArguments()));
        prepareAccessTypeRadioButtons(task, view);
        prepareAddressTextFields(task, view);
        prepareIntervalTextField(task, view);
        prepareNotificationSwitch(task, view);
        prepareOkCancelImageButtons(view);
        return view;
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

    private boolean isPortVisible() {
        return portEditText.getVisibility() == View.VISIBLE;
    }

    private void prepareAccessTypeRadioButtons(NetworkTask task, View view) {
        Log.d(NetworkTaskEditDialog.class.getName(), "prepareAccessTypeRadioButtons with access type of " + task.getAccessType());
        accessTypeGroup = view.findViewById(R.id.radiogroup_dialog_edit_network_task_accesstype);
        EnumMapping mapping = new EnumMapping(getContext());
        AccessType[] accessTypes = AccessType.values();
        for (int ii = 0; ii < accessTypes.length; ii++) {
            AccessType accessType = accessTypes[ii];
            RadioButton newRadioButton = new RadioButton(getContext());
            newRadioButton.setText(mapping.getAccessTypeText(accessType));
            newRadioButton.setId(View.generateViewId());
            if (task.getAccessType() == null && ii == 0) {
                newRadioButton.setChecked(true);
            } else {
                newRadioButton.setChecked(accessType.equals(task.getAccessType()));
            }
            newRadioButton.setTag(accessType);
            LinearLayout.LayoutParams layoutParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
            accessTypeGroup.addView(newRadioButton, ii, layoutParams);
        }
    }

    private void prepareAddressTextFields(NetworkTask task, View view) {
        Log.d(NetworkTaskEditDialog.class.getName(), "prepareAddressTextFields with address of " + task.getAddress() + " and port of " + task.getPort());
        EnumMapping mapping = new EnumMapping(getContext());
        RadioGroup accessTypeGroup = view.findViewById(R.id.radiogroup_dialog_edit_network_task_accesstype);
        int selectedId = accessTypeGroup.getCheckedRadioButtonId();
        RadioButton selectedAccessTypeRadioButton = view.findViewById(selectedId);
        AccessType accessType = null;
        if (selectedAccessTypeRadioButton != null) {
            accessType = (AccessType) selectedAccessTypeRadioButton.getTag();
        }
        TextView addressTextView = view.findViewById(R.id.textview_dialog_edit_network_task_address_label);
        addressTextView.setText(mapping.getAccessTypeAddressLabel(accessType));
        addressEditText = view.findViewById(R.id.edittext_dialog_edit_network_task_address);
        addressEditText.setText(StringUtil.notNull(task.getAddress()));
        TextView portTextView = view.findViewById(R.id.textview_dialog_edit_network_task_port_label);
        portEditText = view.findViewById(R.id.edittext_dialog_edit_network_task_port);
        if (accessType != null && accessType.needsPort()) {
            portTextView.setText(mapping.getAccessTypePortLabel(accessType));
            portEditText.setText(String.valueOf(task.getPort()));
            portTextView.setVisibility(View.VISIBLE);
            portEditText.setVisibility(View.VISIBLE);
        } else {
            portTextView.setVisibility(View.GONE);
            portEditText.setVisibility(View.GONE);
        }
    }


    private void prepareIntervalTextField(NetworkTask task, View view) {
        Log.d(NetworkTaskEditDialog.class.getName(), "prepareIntervalTextField with interval of " + task.getInterval());
        intervalEditText = view.findViewById(R.id.edittext_dialog_edit_network_task_interval);
        intervalEditText.setText(String.valueOf(task.getInterval()));
    }

    private void prepareNotificationSwitch(NetworkTask task, View view) {
        Log.d(NetworkTaskEditDialog.class.getName(), "prepareNotificationSwitch with notification setting of " + task.isNotification());
        notificationSwitch = view.findViewById(R.id.switch_dialog_edit_network_task_notification);
        notificationOnOffText = view.findViewById(R.id.textview_dialog_edit_network_task_notification_label_on_off);
        notificationSwitch.setChecked(task.isNotification());
        notificationSwitch.setOnCheckedChangeListener(this::onNotificationCheckedChanged);
        prepareNotificationOnOffText();
    }

    private void prepareNotificationOnOffText() {
        notificationOnOffText.setText(notificationSwitch.isChecked() ? getResources().getString(R.string.string_yes) : getResources().getString(R.string.string_no));
    }

    private void prepareOkCancelImageButtons(View view) {
        Log.d(NetworkTaskEditDialog.class.getName(), "prepareOkCancelImageButtons");
        ImageView okImage = view.findViewById(R.id.imageview_dialog_edit_network_task_ok);
        ImageView cancelImage = view.findViewById(R.id.imageview_dialog_edit_network_task_cancel);
        okImage.setOnClickListener(this::onOkClicked);
        cancelImage.setOnClickListener(this::onCancelClicked);
    }

    public NetworkTask getNetworkTask() {
        NetworkTask task = new NetworkTask(Objects.requireNonNull(getArguments()));
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
        task.setNotification(notificationSwitch.isChecked());
        Log.d(NetworkTaskEditDialog.class.getName(), "getNetworkTask, network task is " + task);
        return task;
    }

    private void onOkClicked(@SuppressWarnings("unused") View view) {
        Log.d(NetworkTaskEditDialog.class.getName(), "onOkClicked");
        NetworkTaskMainActivity activity = (NetworkTaskMainActivity) getActivity();
        if (validateInput()) {
            Log.d(NetworkTaskEditDialog.class.getName(), "Validation was successful");
            Objects.requireNonNull(activity).onEditDialogOkClicked(this);
        } else {
            Log.d(NetworkTaskEditDialog.class.getName(), "Validation failed");
            showErrorDialog();
        }
    }

    private void onCancelClicked(@SuppressWarnings("unused") View view) {
        Log.d(NetworkTaskEditDialog.class.getName(), "onCancelClicked");
        NetworkTaskMainActivity activity = (NetworkTaskMainActivity) getActivity();
        Objects.requireNonNull(activity).onEditDialogCancelClicked(this);
    }

    private void onNotificationCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.d(NetworkTaskEditDialog.class.getName(), "onCheckedChanged, new value is " + isChecked);
        prepareNotificationOnOffText();
    }

    private boolean validateInput() {
        Log.d(NetworkTaskEditDialog.class.getName(), "validateInput");
        EnumMapping mapping = new EnumMapping(getContext());
        AccessType accessType = getAccessType();
        Validator validator = mapping.getValidator(accessType);
        Log.d(NetworkTaskEditDialog.class.getName(), "Validator is " + validator.getClass().getSimpleName() + " for access type " + accessType);
        StringBuilder message = new StringBuilder();
        boolean success = true;
        ValidationResult result = validator.validateAddress(getAddress());
        Log.d(NetworkTaskEditDialog.class.getName(), "address validation result: " + result);
        if (!result.isValidationSuccessful()) {
            success = false;
        }
        if (isPortVisible()) {
            result = validator.validatePort(getPort());
            Log.d(NetworkTaskEditDialog.class.getName(), "port validation result: " + result);
            if (!result.isValidationSuccessful()) {
                success = false;
            }
        } else {
            Log.d(NetworkTaskEditDialog.class.getName(), "port validation skippedd");
        }
        result = validator.validateInterval(getInterval());
        Log.d(NetworkTaskEditDialog.class.getName(), "interval validation result: " + result);
        if (!result.isValidationSuccessful()) {
            success = false;
        }
        return success;
    }

    private void showErrorDialog() {
        Log.d(NetworkTaskEditDialog.class.getName(), "showErrorDialog, opening NetworkTaskEditErrorDialog");
        NetworkTaskEditErrorDialog errorDialog = new NetworkTaskEditErrorDialog();
        errorDialog.show(Objects.requireNonNull(getFragmentManager()), NetworkTaskEditErrorDialog.class.getName());
    }
}
