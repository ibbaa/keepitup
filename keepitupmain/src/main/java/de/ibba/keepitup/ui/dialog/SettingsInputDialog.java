package de.ibba.keepitup.ui.dialog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.Objects;

import de.ibba.keepitup.R;
import de.ibba.keepitup.util.BundleUtil;

public class SettingsInputDialog extends DialogFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(SettingsInputDialog.class.getName(), "onCreate");
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.DialogTheme);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(SettingsInputDialog.class.getName(), "onCreateView");
        View view = inflater.inflate(R.layout.dialog_settings_input, container);
        String value = BundleUtil.bundleToMessage(SettingsInputDialog.class.getSimpleName(), Objects.requireNonNull(getArguments()));
        prepareConfirmMessage(view, value);
        prepareOkCancelImageButtons(view);
        return view;
    }

    private void prepareConfirmMessage(View view, String message) {
        Log.d(SettingsInputDialog.class.getName(), "prepareConfirmMessage");
        EditText valueEditText = view.findViewById(R.id.edittext_dialog_settings_input_value);
        valueEditText.setText(message);
    }

    private void prepareOkCancelImageButtons(View view) {
        Log.d(SettingsInputDialog.class.getName(), "prepareOkCancelImageButtons");
        ImageView okImage = view.findViewById(R.id.imageview_dialog_settings_input_ok);
        ImageView cancelImage = view.findViewById(R.id.imageview_dialog_settings_input_cancel);
        okImage.setOnClickListener(this::onOkClicked);
        cancelImage.setOnClickListener(this::onCancelClicked);
    }

    private void onOkClicked(@SuppressWarnings("unused") View view) {
        Log.d(SettingsInputDialog.class.getName(), "onOkClicked");
    }

    private void onCancelClicked(@SuppressWarnings("unused") View view) {
        Log.d(SettingsInputDialog.class.getName(), "onCancelClicked");
    }
}
