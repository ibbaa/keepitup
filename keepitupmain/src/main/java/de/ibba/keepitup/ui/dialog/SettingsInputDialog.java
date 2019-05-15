package de.ibba.keepitup.ui.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import de.ibba.keepitup.R;
import de.ibba.keepitup.ui.SettingsActivity;
import de.ibba.keepitup.ui.validation.FieldValidator;
import de.ibba.keepitup.util.StringUtil;

public class SettingsInputDialog extends DialogFragment {

    private View dialogView;
    private SettingsInput input;
    private EditText valueEditText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(SettingsInputDialog.class.getName(), "onCreate");
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.DialogTheme);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(SettingsInputDialog.class.getName(), "onCreateView");
        dialogView = inflater.inflate(R.layout.dialog_settings_input, container);
        input = new SettingsInput(Objects.requireNonNull(getArguments()));
        prepareValueTextField();
        prepareOkCancelImageButtons();
        return dialogView;
    }

    private void prepareValueTextField() {
        Log.d(SettingsInputDialog.class.getName(), "prepareValueTextField");
        valueEditText = dialogView.findViewById(R.id.edittext_dialog_settings_input_value);
        valueEditText.setText(StringUtil.notNull(input.getValue()));
    }

    private void prepareOkCancelImageButtons() {
        Log.d(SettingsInputDialog.class.getName(), "prepareOkCancelImageButtons");
        ImageView okImage = dialogView.findViewById(R.id.imageview_dialog_settings_input_ok);
        ImageView cancelImage = dialogView.findViewById(R.id.imageview_dialog_settings_input_cancel);
        okImage.setOnClickListener(this::onOkClicked);
        cancelImage.setOnClickListener(this::onCancelClicked);
    }

    private void onOkClicked(@SuppressWarnings("unused") View view) {
        Log.d(SettingsInputDialog.class.getName(), "onOkClicked");
        SettingsActivity activity = (SettingsActivity) getActivity();
        Bundle validationResult = validateInput();
        Objects.requireNonNull(activity).onInputDialogOkClicked(this);
    }

    private void onCancelClicked(@SuppressWarnings("unused") View view) {
        Log.d(SettingsInputDialog.class.getName(), "onCancelClicked");
        SettingsActivity activity = (SettingsActivity) getActivity();
        Objects.requireNonNull(activity).onInputDialogCancelClicked(this);
    }

    private Bundle validateInput() {
        Log.d(SettingsInputDialog.class.getName(), "validateInput");
        Bundle bundle = new Bundle();
        /*ListValidator validator = getValidator();
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
        }*/
        return bundle;
    }

    private List<FieldValidator> getValidators() {
        Log.d(SettingsInputDialog.class.getName(), "getValidators");
        List<String> validatorClassNames = input.getValidators();
        List<FieldValidator> validators = new ArrayList<>();
        if (validatorClassNames == null) {
            Log.d(SettingsInputDialog.class.getName(), "validatorClasses is null. Returning empty list.");
            return Collections.emptyList();
        }
        for (String validatorClassName : validatorClassNames) {
            Log.d(SettingsInputDialog.class.getName(), "Specified validator class is " + validatorClassName);
            FieldValidator validator = getValidator(validatorClassName);
            if (validator != null) {
                Log.d(SettingsInputDialog.class.getName(), "Validator class is " + validator.getClass().getName());
                validators.add(validator);
            } else {
                Log.d(SettingsInputDialog.class.getName(), "Validator class is null");
            }
        }
        if (validators.isEmpty()) {
            Log.d(SettingsInputDialog.class.getName(), "No validators specified. Returning empty list.");
        }
        return validators;
    }

    private FieldValidator getValidator(String validatorClassName) {
        try {
            Class<?> validatorClass = Objects.requireNonNull(getContext()).getClassLoader().loadClass(validatorClassName);
            Constructor<?> validatorClassConstructor = validatorClass.getConstructor(String.class, Context.class);
            return (FieldValidator) validatorClassConstructor.newInstance(input.getField(), getContext());
        } catch (Throwable exc) {
            Log.e(SettingsInputDialog.class.getName(), "Error instantiating validator class", exc);
        }
        return null;
    }
}
