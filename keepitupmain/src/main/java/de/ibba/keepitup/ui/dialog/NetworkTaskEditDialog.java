package de.ibba.keepitup.ui.dialog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
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
import de.ibba.keepitup.ui.NetworkTaskMainActivity;
import de.ibba.keepitup.ui.mapping.EnumMapping;
import de.ibba.keepitup.ui.validation.TextColorValidatingWatcher;
import de.ibba.keepitup.ui.validation.ValidationResult;
import de.ibba.keepitup.ui.validation.Validator;
import de.ibba.keepitup.util.BundleUtil;
import de.ibba.keepitup.util.NumberUtil;
import de.ibba.keepitup.util.StringUtil;

public class NetworkTaskEditDialog extends DialogFragment {

    private View dialogView;
    private NetworkTask task;
    private RadioGroup accessTypeGroup;
    private EditText addressEditText;
    private TextColorValidatingWatcher addressEditTextWatcher;
    private EditText portEditText;
    private TextColorValidatingWatcher portEditTextWatcher;
    private EditText intervalEditText;
    private TextColorValidatingWatcher intervalEditTextWatcher;
    private Switch notificationSwitch;
    private TextView notificationOnOffText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(NetworkTaskEditDialog.class.getName(), "onCreate");
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.DialogTheme);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(NetworkTaskEditDialog.class.getName(), "onCreateView");
        dialogView = inflater.inflate(R.layout.dialog_edit_network_task, container);
        task = new NetworkTask(Objects.requireNonNull(getArguments()));
        prepareAccessTypeRadioButtons();
        prepareAddressTextFields();
        prepareIntervalTextField();
        prepareNotificationSwitch();
        prepareOkCancelImageButtons();
        return dialogView;
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

    private void prepareAccessTypeRadioButtons() {
        Log.d(NetworkTaskEditDialog.class.getName(), "prepareAccessTypeRadioButtons with access type of " + task.getAccessType());
        accessTypeGroup = dialogView.findViewById(R.id.radiogroup_dialog_edit_network_task_accesstype);
        EnumMapping mapping = new EnumMapping(requireContext());
        AccessType[] accessTypes = AccessType.values();
        for (int ii = 0; ii < accessTypes.length; ii++) {
            AccessType accessType = accessTypes[ii];
            RadioButton newRadioButton = new RadioButton(requireContext());
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
        accessTypeGroup.setOnCheckedChangeListener(this::onAccessTypeChanged);
    }

    private void onAccessTypeChanged(RadioGroup group, int checkedId) {
        prepareAddressTextFields();
    }

    private void prepareAddressTextFields() {
        Log.d(NetworkTaskEditDialog.class.getName(), "prepareAddressTextFields with address of " + task.getAddress() + " and port of " + task.getPort());
        EnumMapping mapping = new EnumMapping(requireContext());
        RadioGroup accessTypeGroup = dialogView.findViewById(R.id.radiogroup_dialog_edit_network_task_accesstype);
        int selectedId = accessTypeGroup.getCheckedRadioButtonId();
        RadioButton selectedAccessTypeRadioButton = dialogView.findViewById(selectedId);
        if (selectedAccessTypeRadioButton == null) {
            Log.d(NetworkTaskEditDialog.class.getName(), "prepareAddressTextFields, selectedAccessTypeRadioButton is null, no access type selected");
            return;
        }
        AccessType accessType = (AccessType) selectedAccessTypeRadioButton.getTag();
        TextView addressTextView = dialogView.findViewById(R.id.textview_dialog_edit_network_task_address_label);
        addressTextView.setText(mapping.getAccessTypeAddressLabel(accessType));
        addressEditText = dialogView.findViewById(R.id.edittext_dialog_edit_network_task_address);
        addressEditText.setText(StringUtil.notNull(task.getAddress()));
        prepareAddressEditTextListener();
        TextView portTextView = dialogView.findViewById(R.id.textview_dialog_edit_network_task_port_label);
        portEditText = dialogView.findViewById(R.id.edittext_dialog_edit_network_task_port);
        LinearLayout portLinearLayout = dialogView.findViewById(R.id.linearlayout_dialog_edit_network_task_port);
        if (accessType != null && accessType.needsPort()) {
            portTextView.setText(mapping.getAccessTypePortLabel(accessType));
            portEditText.setText(String.valueOf(task.getPort()));
            portTextView.setVisibility(View.VISIBLE);
            portEditText.setVisibility(View.VISIBLE);
            portLinearLayout.setVisibility(View.VISIBLE);
            preparePortEditTextListener();
        } else {
            portTextView.setVisibility(View.GONE);
            portEditText.setVisibility(View.GONE);
            portLinearLayout.setVisibility(View.GONE);
            if (portEditTextWatcher != null) {
                portEditText.removeTextChangedListener(portEditTextWatcher);
                portEditTextWatcher = null;
            }
        }
    }

    private void prepareAddressEditTextListener() {
        if (addressEditTextWatcher != null) {
            addressEditText.removeTextChangedListener(addressEditTextWatcher);
            addressEditTextWatcher = null;
        }
        addressEditTextWatcher = new TextColorValidatingWatcher(addressEditText, this::validateAddress, getColor(R.color.textColor), getColor(R.color.textErrorColor));
        addressEditText.addTextChangedListener(addressEditTextWatcher);
    }

    private void preparePortEditTextListener() {
        if (portEditTextWatcher != null) {
            portEditText.removeTextChangedListener(portEditTextWatcher);
            portEditTextWatcher = null;
        }
        portEditTextWatcher = new TextColorValidatingWatcher(portEditText, this::validatePort, getColor(R.color.textColor), getColor(R.color.textErrorColor));
        portEditText.addTextChangedListener(portEditTextWatcher);
    }

    private void prepareIntervalTextField() {
        Log.d(NetworkTaskEditDialog.class.getName(), "prepareIntervalTextField with interval of " + task.getInterval());
        intervalEditText = dialogView.findViewById(R.id.edittext_dialog_edit_network_task_interval);
        intervalEditText.setText(String.valueOf(task.getInterval()));
        prepareIntervalEditTextListener();
    }

    private void prepareIntervalEditTextListener() {
        if (intervalEditTextWatcher != null) {
            intervalEditText.removeTextChangedListener(intervalEditTextWatcher);
            intervalEditTextWatcher = null;
        }
        intervalEditTextWatcher = new TextColorValidatingWatcher(intervalEditText, this::validateInterval, getColor(R.color.textColor), getColor(R.color.textErrorColor));
        intervalEditText.addTextChangedListener(intervalEditTextWatcher);
    }

    private void prepareNotificationSwitch() {
        Log.d(NetworkTaskEditDialog.class.getName(), "prepareNotificationSwitch with notification setting of " + task.isNotification());
        notificationSwitch = dialogView.findViewById(R.id.switch_dialog_edit_network_task_notification);
        notificationOnOffText = dialogView.findViewById(R.id.textview_dialog_edit_network_task_notification_label_on_off);
        notificationSwitch.setChecked(task.isNotification());
        notificationSwitch.setOnCheckedChangeListener(this::onNotificationCheckedChanged);
        prepareNotificationOnOffText();
    }

    private void prepareNotificationOnOffText() {
        notificationOnOffText.setText(notificationSwitch.isChecked() ? getResources().getString(R.string.string_yes) : getResources().getString(R.string.string_no));
    }

    private void prepareOkCancelImageButtons() {
        Log.d(NetworkTaskEditDialog.class.getName(), "prepareOkCancelImageButtons");
        ImageView okImage = dialogView.findViewById(R.id.imageview_dialog_edit_network_task_ok);
        ImageView cancelImage = dialogView.findViewById(R.id.imageview_dialog_edit_network_task_cancel);
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
        Bundle validationResult = validateInput();
        if (!hasErrors(validationResult)) {
            Log.d(NetworkTaskEditDialog.class.getName(), "Validation was successful");
            Objects.requireNonNull(activity).onEditDialogOkClicked(this);
        } else {
            Log.d(NetworkTaskEditDialog.class.getName(), "Validation failed");
            showErrorDialog(validationResult);
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

    private boolean hasErrors(Bundle bundle) {
        return !bundle.isEmpty();
    }

    private Bundle validateInput() {
        Log.d(NetworkTaskEditDialog.class.getName(), "validateInput");
        Bundle bundle = new Bundle();
        Validator validator = getValidator();
        ValidationResult result = validator.validateAddress(getAddress());
        Log.d(NetworkTaskEditDialog.class.getName(), "address validation result: " + result);
        if (!result.isValidationSuccessful()) {
            BundleUtil.addValidationResultToIndexedBundle(bundle, result);
        }
        if (isPortVisible()) {
            result = validator.validatePort(getPort());
            Log.d(NetworkTaskEditDialog.class.getName(), "port validation result: " + result);
            if (!result.isValidationSuccessful()) {
                BundleUtil.addValidationResultToIndexedBundle(bundle, result);
            }
        } else {
            Log.d(NetworkTaskEditDialog.class.getName(), "port validation skipped");
        }
        result = validator.validateInterval(getInterval());
        Log.d(NetworkTaskEditDialog.class.getName(), "interval validation result: " + result);
        if (!result.isValidationSuccessful()) {
            BundleUtil.addValidationResultToIndexedBundle(bundle, result);
        }
        return bundle;
    }

    private boolean validateAddress(EditText editText) {
        Log.d(NetworkTaskEditDialog.class.getName(), "validateAddress");
        Validator validator = getValidator();
        ValidationResult result = validator.validateAddress(getAddress());
        Log.d(NetworkTaskEditDialog.class.getName(), "address validation result: " + result);
        return result.isValidationSuccessful();
    }

    private boolean validatePort(EditText editText) {
        Log.d(NetworkTaskEditDialog.class.getName(), "validatePort");
        Validator validator = getValidator();
        ValidationResult result = validator.validatePort(getPort());
        Log.d(NetworkTaskEditDialog.class.getName(), "port validation result: " + result);
        return result.isValidationSuccessful();
    }

    private boolean validateInterval(EditText editText) {
        Log.d(NetworkTaskEditDialog.class.getName(), "validateInterval");
        Validator validator = getValidator();
        ValidationResult result = validator.validateInterval(getInterval());
        Log.d(NetworkTaskEditDialog.class.getName(), "interval validation result: " + result);
        return result.isValidationSuccessful();
    }

    @NonNull
    private Validator getValidator() {
        EnumMapping mapping = new EnumMapping(requireContext());
        AccessType accessType = getAccessType();
        Validator validator = mapping.getValidator(accessType);
        Log.d(NetworkTaskEditDialog.class.getName(), "Validator is " + validator.getClass().getSimpleName() + " for access type " + accessType);
        return validator;
    }

    private void showErrorDialog(Bundle bundle) {
        Log.d(NetworkTaskEditDialog.class.getName(), "showErrorDialog, opening NetworkTaskEditErrorDialog");
        NetworkTaskEditErrorDialog errorDialog = new NetworkTaskEditErrorDialog();
        errorDialog.setArguments(bundle);
        errorDialog.show(Objects.requireNonNull(getFragmentManager()), NetworkTaskEditErrorDialog.class.getName());
    }

    private int getColor(int colorid) {
        return ContextCompat.getColor(requireContext(), colorid);
    }
}
