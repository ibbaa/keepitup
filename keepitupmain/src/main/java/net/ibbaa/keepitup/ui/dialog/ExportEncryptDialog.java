/*
 * Copyright (c) 2026 Alwin Ibba
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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.ui.support.CredentialInfoSupport;
import net.ibbaa.keepitup.ui.support.ExportEncryptSupport;
import net.ibbaa.keepitup.ui.validation.CredentialInfo;
import net.ibbaa.keepitup.ui.validation.PasswordConfirmFieldValidator;
import net.ibbaa.keepitup.ui.validation.PasswordFieldValidator;
import net.ibbaa.keepitup.ui.validation.TextDescriptionColorValidatingWatcher;
import net.ibbaa.keepitup.ui.validation.ValidationResult;
import net.ibbaa.keepitup.util.BundleUtil;
import net.ibbaa.keepitup.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings({"unused", "SameReturnValue"})
public class ExportEncryptDialog extends DialogFragmentBase implements CredentialInfoSupport {

    private View dialogView;
    private CheckBox encryptCheckBox;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private TextView passwordTextView;
    private TextView confirmPasswordTextView;
    private PasswordToggleTouchListener passwordToggleTouchListener;
    private PasswordToggleTouchListener confirmPasswordToggleTouchListener;
    private TextDescriptionColorValidatingWatcher passwordEditTextWatcher;
    private TextDescriptionColorValidatingWatcher confirmPasswordEditTextWatcher;
    private boolean credentialInfoShown;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(ExportEncryptDialog.class.getName(), "onCreate");
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.DialogTheme);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(ExportEncryptDialog.class.getName(), "onCreateView");
        dialogView = inflater.inflate(R.layout.dialog_export_encrypt, container);
        initEdgeToEdgeInsets(dialogView);
        prepareEncryptCheckBox(savedInstanceState);
        preparePasswordTextField(savedInstanceState);
        prepareConfirmPasswordTextField(savedInstanceState);
        prepareOkCancelImageButtons();
        return dialogView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (passwordToggleTouchListener != null) {
            outState.putBoolean(getPasswordVisibleKey(), passwordToggleTouchListener.isVisible());
        }
        if (confirmPasswordToggleTouchListener != null) {
            outState.putBoolean(getConfirmPasswordVisibleKey(), confirmPasswordToggleTouchListener.isVisible());
        }
        outState.putBoolean(getCredentialInfoShownKey(), credentialInfoShown);
    }

    public String getPasswordVisibleKey() {
        return ExportEncryptDialog.class.getSimpleName() + ".PasswordVisible";
    }

    public String getConfirmPasswordVisibleKey() {
        return ExportEncryptDialog.class.getSimpleName() + ".ConfirmPasswordVisible";
    }

    public String getCredentialInfoShownKey() {
        return ExportEncryptDialog.class.getSimpleName() + ".CredentialInfoShown";
    }

    private List<CredentialInfo> getCredentials() {
        if (getArguments() != null) {
            return BundleUtil.credentialInfoListFromBundle(getCredentialInfoBaseKey(), requireArguments());
        }
        return Collections.emptyList();
    }

    public String getCredentialInfoBaseKey() {
        return ExportEncryptDialog.class.getSimpleName() + ".getCredentialInfo";
    }

    private void prepareEncryptCheckBox(Bundle savedInstanceState) {
        Log.d(ExportEncryptDialog.class.getName(), "prepareEncryptCheckBox");
        encryptCheckBox = dialogView.findViewById(R.id.checkbox_dialog_export_encrypt_encrypt);
        credentialInfoShown = savedInstanceState != null && BundleUtil.booleanFromBundle(getCredentialInfoShownKey(), savedInstanceState);
        encryptCheckBox.setOnCheckedChangeListener(this::onEncryptCheckboxCheckedChanged);
    }

    private void onEncryptCheckboxCheckedChanged(@NonNull CompoundButton checkBox, boolean checked) {
        Log.d(ExportEncryptDialog.class.getName(), "onEncryptCheckboxCheckedChanged, checked is " + checked);
        LinearLayout passwordLayout = dialogView.findViewById(R.id.linearlayout_dialog_export_encrypt_password);
        LinearLayout confirmPasswordLayout = dialogView.findViewById(R.id.linearlayout_dialog_export_encrypt_password_confirm);
        int visibility = checked ? View.VISIBLE : View.GONE;
        passwordLayout.setVisibility(visibility);
        confirmPasswordLayout.setVisibility(visibility);
        if (!checked) {
            String tag = CredentialInfoDialog.class.getName();
            FragmentManager fragmentManager = getParentFragmentManager();
            if (fragmentManager.findFragmentByTag(tag) == null && !credentialInfoShown) {
                credentialInfoShown = true;
                showCredentialInfoDialog();
            }
        }
    }

    private void showCredentialInfoDialog() {
        Log.d(ExportEncryptDialog.class.getName(), "showCredentialInfoDialog");
        List<CredentialInfo> credentials = getCredentials();
        if (credentials.isEmpty()) {
            return;
        }
        CredentialInfoDialog infoDialog = new CredentialInfoDialog();
        Bundle bundle = BundleUtil.credentialInfoListToBundle(infoDialog.getCredentialInfoBaseKey(), credentials);
        String title = getResources().getString(R.string.text_dialog_credential_info_title_encrypt);
        BundleUtil.stringToBundle(infoDialog.getTitleKey(), title, bundle);
        String message = getResources().getString(R.string.text_dialog_credential_info_message_encrypt);
        BundleUtil.stringToBundle(infoDialog.getMessageKey(), message, bundle);
        infoDialog.setArguments(bundle);
        infoDialog.show(getParentFragmentManager(), CredentialInfoDialog.class.getName());
    }

    private void preparePasswordTextField(Bundle savedInstanceState) {
        Log.d(ExportEncryptDialog.class.getName(), "preparePasswordTextField");
        passwordEditText = dialogView.findViewById(R.id.edittext_dialog_export_encrypt_password);
        passwordTextView = dialogView.findViewById(R.id.textview_dialog_export_encrypt_password);
        preparePasswordToggleTouchListener(savedInstanceState);
        preparePasswordEditTextListener();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void preparePasswordToggleTouchListener(Bundle savedInstanceState) {
        Log.d(ExportEncryptDialog.class.getName(), "preparePasswordToggleTouchListener");
        passwordToggleTouchListener = new PasswordToggleTouchListener(passwordEditText);
        if (savedInstanceState != null) {
            boolean wasVisible = savedInstanceState.getBoolean(getPasswordVisibleKey(), false);
            passwordToggleTouchListener.setVisible(wasVisible);
        }
        passwordEditText.setOnTouchListener(passwordToggleTouchListener);
    }

    private void preparePasswordEditTextListener() {
        Log.d(ExportEncryptDialog.class.getName(), "preparePasswordEditTextListener");
        if (passwordEditTextWatcher != null) {
            passwordEditText.removeTextChangedListener(passwordEditTextWatcher);
            passwordEditTextWatcher = null;
        }
        int minLength = getResources().getInteger(R.integer.password_min_length);
        String okText = getResources().getString(R.string.text_dialog_export_password_ok);
        int okColor = getColor(R.color.textOkColor);
        int errorColor = getColor(R.color.textErrorColor);
        passwordEditTextWatcher = new TextDescriptionColorValidatingWatcher(passwordEditText, passwordTextView, this::validatePassword, okText, okColor, errorColor);
        passwordEditText.addTextChangedListener(passwordEditTextWatcher);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void prepareConfirmPasswordTextField(Bundle savedInstanceState) {
        Log.d(ExportEncryptDialog.class.getName(), "prepareConfirmPasswordTextField");
        confirmPasswordEditText = dialogView.findViewById(R.id.edittext_dialog_export_encrypt_password_confirm);
        confirmPasswordTextView = dialogView.findViewById(R.id.textview_dialog_export_encrypt_password_confirm);
        prepareConfirmPasswordToggleTouchListener(savedInstanceState);
        prepareConfirmPasswordEditTextListener();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void prepareConfirmPasswordToggleTouchListener(Bundle savedInstanceState) {
        Log.d(ExportEncryptDialog.class.getName(), "prepareConfirmPasswordToggleTouchListener");
        confirmPasswordToggleTouchListener = new PasswordToggleTouchListener(confirmPasswordEditText);
        if (savedInstanceState != null) {
            boolean wasVisible = savedInstanceState.getBoolean(getConfirmPasswordVisibleKey(), false);
            confirmPasswordToggleTouchListener.setVisible(wasVisible);
        }
        confirmPasswordEditText.setOnTouchListener(confirmPasswordToggleTouchListener);
    }

    private void prepareConfirmPasswordEditTextListener() {
        Log.d(ExportEncryptDialog.class.getName(), "prepareConfirmPasswordEditTextListener");
        if (confirmPasswordEditTextWatcher != null) {
            confirmPasswordEditText.removeTextChangedListener(confirmPasswordEditTextWatcher);
            confirmPasswordEditTextWatcher = null;
        }
        String okText = getResources().getString(R.string.text_dialog_export_confirm_password_match);
        String errorText = getResources().getString(R.string.text_dialog_export_confirm_password_no_match);
        int okColor = getColor(R.color.textOkColor);
        int errorColor = getColor(R.color.textErrorColor);
        confirmPasswordEditTextWatcher = new TextDescriptionColorValidatingWatcher(confirmPasswordEditText, confirmPasswordTextView, this::validateConfirmPassword, okText, errorText, okColor, errorColor);
        confirmPasswordEditText.addTextChangedListener(confirmPasswordEditTextWatcher);
        passwordEditTextWatcher.addDependentWatcher(confirmPasswordEditTextWatcher);
    }

    private void prepareOkCancelImageButtons() {
        Log.d(ExportEncryptDialog.class.getName(), "prepareOkCancelImageButtons");
        ImageView okImage = dialogView.findViewById(R.id.imageview_dialog_export_encrypt_ok);
        ImageView cancelImage = dialogView.findViewById(R.id.imageview_dialog_export_encrypt_cancel);
        okImage.setOnClickListener(this::onOkClicked);
        cancelImage.setOnClickListener(this::onCancelClicked);
    }

    public boolean isEncrypt() {
        return encryptCheckBox.isChecked();
    }

    public String getPassword() {
        return StringUtil.notNull(passwordEditText.getText());
    }

    public String getConfirmPassword() {
        return StringUtil.notNull(confirmPasswordEditText.getText());
    }

    private void onOkClicked(View view) {
        Log.d(ExportEncryptDialog.class.getName(), "onOkClicked");
        List<ValidationResult> validationResult = validateInput();
        if (!hasErrors(validationResult)) {
            Log.d(ExportEncryptDialog.class.getName(), "Validation was successful");
            ExportEncryptSupport exportEncryptSupport = getExportEncryptSupport();
            if (exportEncryptSupport != null) {
                exportEncryptSupport.onExportEncryptDialogOkClicked(this);
            } else {
                Log.e(ExportEncryptDialog.class.getName(), "exportEncryptSupport is null");
                dismiss();
            }
        } else {
            Log.d(ExportEncryptDialog.class.getName(), "Validation failed");
            showValidationErrorDialog(validationResult);
        }
    }

    private void onCancelClicked(View view) {
        Log.d(ExportEncryptDialog.class.getName(), "onCancelClicked");
        ExportEncryptSupport exportEncryptSupport = getExportEncryptSupport();
        if (exportEncryptSupport != null) {
            exportEncryptSupport.onExportEncryptDialogCancelClicked(this);
        } else {
            Log.e(ExportEncryptDialog.class.getName(), "exportEncryptSupport is null");
            dismiss();
        }
    }

    private boolean hasErrors(List<ValidationResult> validationResult) {
        return !validationResult.isEmpty();
    }

    private String validatePassword(EditText editText) {
        Log.d(ExportEncryptDialog.class.getName(), "validatePassword");
        PasswordFieldValidator validator = new PasswordFieldValidator(getResources().getString(R.string.password_field_name), getContext());
        ValidationResult result = validator.validate(getPassword());
        Log.d(ExportEncryptDialog.class.getName(), "Validation result: " + result);
        if (result.isValidationSuccessful()) {
            return "";
        }
        return result.getMessage();
    }

    private String validateConfirmPassword(EditText editText) {
        Log.d(ExportEncryptDialog.class.getName(), "validateConfirmPassword");
        PasswordConfirmFieldValidator validator = new PasswordConfirmFieldValidator(getResources().getString(R.string.password_confirm_field_name), getPassword(), getContext());
        ValidationResult result = validator.validate(getConfirmPassword());
        Log.d(ExportEncryptDialog.class.getName(), "Validation result: " + result);
        if (result.isValidationSuccessful()) {
            return "";
        }
        return result.getMessage();
    }

    private List<ValidationResult> validateInput() {
        Log.d(ExportEncryptDialog.class.getName(), "validateInput");
        List<ValidationResult> validationResults = new ArrayList<>();
        if (!isEncrypt()) {
            return validationResults;
        }
        PasswordFieldValidator passwordValidator = new PasswordFieldValidator(getResources().getString(R.string.password_field_name), getContext());
        ValidationResult passwordResult = passwordValidator.validate(getPassword());
        if (!passwordResult.isValidationSuccessful()) {
            validationResults.add(passwordResult);
        }
        if (passwordResult.isValidationSuccessful() || !StringUtil.isEmpty(getConfirmPassword())) {
            PasswordConfirmFieldValidator passwordConfirmValidator = new PasswordConfirmFieldValidator(getResources().getString(R.string.password_confirm_field_name), getPassword(), getContext());
            ValidationResult passwordConfirmResult = passwordConfirmValidator.validate(getConfirmPassword());
            if (!passwordConfirmResult.isValidationSuccessful()) {
                validationResults.add(passwordConfirmResult);
            }
        }
        return validationResults;
    }

    private void showValidationErrorDialog(List<ValidationResult> validationResult) {
        Log.d(ExportEncryptDialog.class.getName(), "showValidationErrorDialog");
        ValidatorErrorDialog errorDialog = new ValidatorErrorDialog();
        errorDialog.setArguments(BundleUtil.validationResultListToBundle(errorDialog.getValidationResultBaseKey(), validationResult));
        errorDialog.show(getParentFragmentManager(), ValidatorErrorDialog.class.getName());
    }

    private int getColor(int colorid) {
        return ContextCompat.getColor(requireContext(), colorid);
    }

    @Override
    public void onCredentialInfoDialogOkClicked(CredentialInfoDialog credentialInfoDialog) {
        Log.d(ExportEncryptDialog.class.getName(), "onCredentialInfoDialogOkClicked");
        credentialInfoDialog.dismiss();
    }

    private ExportEncryptSupport getExportEncryptSupport() {
        Log.d(ExportEncryptDialog.class.getName(), "getExportEncryptSupport");
        List<Fragment> fragments = getParentFragmentManager().getFragments();
        for (Fragment fragment : fragments) {
            if (fragment instanceof ExportEncryptSupport) {
                return (ExportEncryptSupport) fragment;
            }
        }
        Activity activity = getActivity();
        if (activity == null) {
            Log.e(ExportEncryptDialog.class.getName(), "getExportEncryptSupport, activity is null");
            return null;
        }
        if (!(activity instanceof ExportEncryptSupport)) {
            Log.e(ExportEncryptDialog.class.getName(), "getExportEncryptSupport, activity is not an instance of " + ExportEncryptSupport.class.getSimpleName());
            return null;
        }
        return (ExportEncryptSupport) activity;
    }
}
