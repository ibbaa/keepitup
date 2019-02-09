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
import de.ibba.keepitup.util.StringUtil;

public class NetworkTaskEditDialog extends DialogFragment {

    private RadioGroup accessTypeGroup;
    private EditText addressEditText;
    private EditText portEditText;
    private EditText intervalEditText;
    private Switch notificationSwitch;
    private TextView notifiactionOnOffText;

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

    private void prepareAccessTypeRadioButtons(NetworkTask task, View view) {
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
        intervalEditText = view.findViewById(R.id.edittext_dialog_edit_network_task_interval);
        intervalEditText.setText(String.valueOf(task.getInterval()));
    }

    private void prepareNotificationSwitch(NetworkTask task, View view) {
        notificationSwitch = view.findViewById(R.id.switch_dialog_edit_network_task_notification);
        notifiactionOnOffText = view.findViewById(R.id.textview_dialog_edit_network_task_notification_label_on_off);
        notificationSwitch.setChecked(task.isNotification());
        notificationSwitch.setOnCheckedChangeListener(this::onNotificationCheckedChanged);
        prepareNotificationOnOffText();
    }

    private void prepareNotificationOnOffText() {
        notifiactionOnOffText.setText(notificationSwitch.isChecked() ? getResources().getString(R.string.string_yes) : getResources().getString(R.string.string_no));
    }

    private void prepareOkCancelImageButtons(View view) {
        ImageView okImage = view.findViewById(R.id.imageview_dialog_edit_network_task_ok);
        ImageView cancelImage = view.findViewById(R.id.imageview_dialog_edit_network_task_cancel);
        okImage.setOnClickListener(this::onOkClicked);
        cancelImage.setOnClickListener(this::onCancelClicked);
    }

    private void onOkClicked(@SuppressWarnings("unused") View view) {
        Log.d(NetworkTaskEditDialog.class.getName(), "onOkClicked");
        NetworkTaskMainActivity activity = (NetworkTaskMainActivity) getActivity();
        Objects.requireNonNull(activity).onEditDialogOkClicked(this);
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
}
