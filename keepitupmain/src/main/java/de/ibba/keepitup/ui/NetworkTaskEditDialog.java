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

import java.util.Objects;

import de.ibba.keepitup.R;

public class NetworkTaskEditDialog extends DialogFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.DialogTheme);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_edit_network_task, container);
        RadioGroup accessTypeGroup = view.findViewById(R.id.radiogroup_dialog_edit_network_task_accesstype);
        RadioButton newRadioButton = new RadioButton(getContext());
        newRadioButton.setText("Test");
        newRadioButton.setId(View.generateViewId());
        newRadioButton.setChecked(true);
        LinearLayout.LayoutParams layoutParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
        accessTypeGroup.addView(newRadioButton, 0, layoutParams);
        ImageView okImage = view.findViewById(R.id.imageview_dialog_edit_network_task_ok);
        ImageView cancelImage = view.findViewById(R.id.imageview_dialog_edit_network_task_cancel);
        okImage.setOnClickListener(this::onOkClicked);
        cancelImage.setOnClickListener(this::onCancelClicked);
        return view;
    }

    private void onOkClicked(View view) {
        Log.d(NetworkTaskEditDialog.class.getName(), "onOkClicked");
        NetworkTaskMainActivity activity = (NetworkTaskMainActivity) getActivity();
        Objects.requireNonNull(activity).onEditDialogOkClicked(this);
    }

    private void onCancelClicked(View view) {
        Log.d(NetworkTaskEditDialog.class.getName(), "onCancelClicked");
        NetworkTaskMainActivity activity = (NetworkTaskMainActivity) getActivity();
        Objects.requireNonNull(activity).onEditDialogCancelClicked(this);
    }
}
