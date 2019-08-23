package de.ibba.keepitup.ui.dialog;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import de.ibba.keepitup.R;
import de.ibba.keepitup.ui.SettingsActivity;
import de.ibba.keepitup.ui.validation.FieldValidator;
import de.ibba.keepitup.ui.validation.TextColorValidatingWatcher;
import de.ibba.keepitup.ui.validation.ValidationResult;
import de.ibba.keepitup.util.BundleUtil;
import de.ibba.keepitup.util.StringUtil;

public class SettingsInputDialog extends DialogFragment {

    private View dialogView;
    private SettingsInput input;
    private EditText valueEditText;
    private TextColorValidatingWatcher valueEditTextWatcher;

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
        Log.d(SettingsInputDialog.class.getName(), "settings input is " + input);
        prepareValueTextField();
        prepareOkCancelImageButtons();
        return dialogView;
    }

    private void prepareValueTextField() {
        Log.d(SettingsInputDialog.class.getName(), "prepareValueTextField");
        valueEditText = dialogView.findViewById(R.id.edittext_dialog_settings_input_value);
        valueEditText.setText(StringUtil.notNull(input.getValue()));
        prepareValueEditTextListener();
    }

    private void prepareValueEditTextListener() {
        if (valueEditTextWatcher != null) {
            valueEditText.removeTextChangedListener(valueEditTextWatcher);
            valueEditTextWatcher = null;
        }
        valueEditTextWatcher = new TextColorValidatingWatcher(valueEditText, this::validateValue, getColor(R.color.textColor), getColor(R.color.textErrorColor));
        valueEditText.addTextChangedListener(valueEditTextWatcher);
    }

    private void prepareOkCancelImageButtons() {
        Log.d(SettingsInputDialog.class.getName(), "prepareOkCancelImageButtons");
        ImageView okImage = dialogView.findViewById(R.id.imageview_dialog_settings_input_ok);
        ImageView cancelImage = dialogView.findViewById(R.id.imageview_dialog_settings_input_cancel);
        okImage.setOnClickListener(this::onOkClicked);
        cancelImage.setOnClickListener(this::onCancelClicked);
    }

    public String getValue() {
        return StringUtil.notNull(valueEditText.getText());
    }

    private void onOkClicked(@SuppressWarnings("unused") View view) {
        Log.d(SettingsInputDialog.class.getName(), "onOkClicked");
        SettingsActivity activity = (SettingsActivity) getActivity();
        Bundle validationResult = validateInput();
        if (!hasErrors(validationResult)) {
            Log.d(SettingsInputDialog.class.getName(), "Validation was successful");
            Objects.requireNonNull(activity).onInputDialogOkClicked(this, input.getType());
        } else {
            Log.d(SettingsInputDialog.class.getName(), "Validation failed");
            showErrorDialog(validationResult);
        }
    }

    private void onCancelClicked(@SuppressWarnings("unused") View view) {
        Log.d(SettingsInputDialog.class.getName(), "onCancelClicked");
        SettingsActivity activity = (SettingsActivity) getActivity();
        Objects.requireNonNull(activity).onInputDialogCancelClicked(this);
    }

    private boolean hasErrors(Bundle bundle) {
        return !bundle.isEmpty();
    }

    private boolean validateValue(EditText editText) {
        Log.d(SettingsInputDialog.class.getName(), "validateValue");
        List<FieldValidator> validators = getValidators();
        for (FieldValidator validator : validators) {
            Log.d(SettingsInputDialog.class.getName(), "Current validator: " + validator.getClass().getName());
            ValidationResult result = validator.validate(getValue());
            Log.d(SettingsInputDialog.class.getName(), "Validation result: " + result);
            if (result.isValidationSuccessful()) {
                return true;
            }
        }
        Log.d(SettingsInputDialog.class.getName(), "Validation failed");
        return false;
    }

    private Bundle validateInput() {
        Log.d(SettingsInputDialog.class.getName(), "validateInput");
        Bundle bundle = new Bundle();
        List<FieldValidator> validators = getValidators();
        List<ValidationResult> validationResults = new ArrayList<>();
        for (FieldValidator validator : validators) {
            Log.d(SettingsInputDialog.class.getName(), "Current validator: " + validator.getClass().getName());
            ValidationResult result = validator.validate(getValue());
            Log.d(SettingsInputDialog.class.getName(), "Validation result: " + result);
            if (result.isValidationSuccessful()) {
                Log.d(SettingsInputDialog.class.getName(), "Validation successful.");
                return new Bundle();
            }
            if (!result.isValidationSuccessful() && !containsValidationResult(validationResults, result)) {
                validationResults.add(result);
            }
        }
        for (ValidationResult currentResult : validationResults) {
            BundleUtil.addValidationResultToIndexedBundle(bundle, currentResult);
        }
        return bundle;
    }

    private boolean containsValidationResult(List<ValidationResult> validationResults, ValidationResult result) {
        for (ValidationResult currentResult : validationResults) {
            if (currentResult.isEqual(result)) {
                return true;
            }
        }
        return false;
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

    private void showErrorDialog(Bundle bundle) {
        Log.d(SettingsInputDialog.class.getName(), "showErrorDialog, opening ValidatorErrorDialog");
        ValidatorErrorDialog errorDialog = new ValidatorErrorDialog();
        errorDialog.setArguments(bundle);
        errorDialog.show(Objects.requireNonNull(getFragmentManager()), ValidatorErrorDialog.class.getName());
    }

    private int getColor(int colorid) {
        return ContextCompat.getColor(requireContext(), colorid);
    }
}
