package de.ibba.keepitup.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.Objects;

import de.ibba.keepitup.R;
import de.ibba.keepitup.model.AccessType;
import de.ibba.keepitup.ui.mapping.EnumMapping;

public class NetworkTaskEditDialog extends DialogFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.DialogTheme);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_edit_network_task, container);
        prepareAccessTypeRadioButtons(view);
        prepareAddressTextFields(view);
        prepareOkCancelImageButtons(view);
        return view;
    }

    private void prepareAddressTextFields(View view) {
        EnumMapping mapping = new EnumMapping(getContext());
        RadioGroup accessTypeGroup = view.findViewById(R.id.radiogroup_dialog_edit_network_task_accesstype);
        int selectedId = accessTypeGroup.getCheckedRadioButtonId();
        RadioButton selectedAccessTypeRadioButton = view.findViewById(selectedId);
        AccessType accessType = (AccessType) selectedAccessTypeRadioButton.getTag();
        TextView hostTextView = view.findViewById(R.id.textview_dialog_edit_network_task_host_label);
        hostTextView.setText(mapping.getAccessTypeAddressLabel(accessType));
        TextView portTextView = view.findViewById(R.id.textview_dialog_edit_network_task_port_label);
        portTextView.setText(mapping.getAccessTypePortLabel(accessType));
    }

    private void prepareAccessTypeRadioButtons(View view) {
        RadioGroup accessTypeGroup = view.findViewById(R.id.radiogroup_dialog_edit_network_task_accesstype);
        EnumMapping mapping = new EnumMapping(getContext());
        AccessType[] accessTypes = AccessType.values();
        for (int ii = 0; ii < accessTypes.length; ii++) {
            AccessType accessType = accessTypes[ii];
            RadioButton newRadioButton = new RadioButton(getContext());
            newRadioButton.setText(mapping.getAccessTypeText(accessType));
            newRadioButton.setId(View.generateViewId());
            newRadioButton.setChecked(ii == 0);
            newRadioButton.setTag(accessType);
            LinearLayout.LayoutParams layoutParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
            accessTypeGroup.addView(newRadioButton, ii, layoutParams);
        }
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
}
