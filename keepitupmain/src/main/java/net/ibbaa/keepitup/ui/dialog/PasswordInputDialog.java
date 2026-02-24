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
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.ui.support.ExportEncryptSupport;
import net.ibbaa.keepitup.ui.support.PasswordInputSupport;
import net.ibbaa.keepitup.ui.validation.PasswordInputFieldValidator;
import net.ibbaa.keepitup.ui.validation.ValidationResult;
import net.ibbaa.keepitup.util.BundleUtil;
import net.ibbaa.keepitup.util.StringUtil;

import org.jetbrains.annotations.NotNull;

import java.util.List;

@SuppressWarnings({"unused", "SameReturnValue"})
public class PasswordInputDialog extends DialogFragmentBase { // implements ContextOptionsSupport, ConfirmSupport {

    private View dialogView;
    private EditText passwordEditText;
    private PasswordToggleTouchListener passwordToggleTouchListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(PasswordInputDialog.class.getName(), "onCreate");
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.DialogTheme);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(PasswordInputDialog.class.getName(), "onCreateView");
        dialogView = inflater.inflate(R.layout.dialog_password_input, container);
        initEdgeToEdgeInsets(dialogView);
        preparePasswordTextField(savedInstanceState);
        prepareOkCancelImageButtons();
        return dialogView;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        passwordEditText.requestFocus();
        Window window = getWindow();
        if (window != null) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
        super.onViewCreated(view, savedInstanceState);
    }

    private Window getWindow() {
        Dialog dialog = getDialog();
        if (dialog != null) {
            return dialog.getWindow();
        }
        return null;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (passwordToggleTouchListener != null) {
            outState.putBoolean(getPasswordVisibleKey(), passwordToggleTouchListener.isVisible());
        }
    }

    public String getPasswordVisibleKey() {
        return PasswordInputDialog.class.getSimpleName() + "PasswordVisible";
    }

    public String getExtraDataKey() {
        return PasswordInputDialog.class.getSimpleName() + "ExtraData";
    }

    public Bundle getExtraData() {
        return BundleUtil.bundleFromBundle(getExtraDataKey(), requireArguments());
    }

    private void preparePasswordTextField(Bundle savedInstanceState) {
        Log.d(PasswordInputDialog.class.getName(), "preparePasswordTextField");
        passwordEditText = dialogView.findViewById(R.id.edittext_dialog_password_input_password);
        preparePasswordToggleTouchListener(savedInstanceState);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void preparePasswordToggleTouchListener(Bundle savedInstanceState) {
        Log.d(PasswordInputDialog.class.getName(), "preparePasswordToggleTouchListener");
        passwordToggleTouchListener = new PasswordToggleTouchListener(passwordEditText);
        if (savedInstanceState != null) {
            boolean wasVisible = savedInstanceState.getBoolean(getPasswordVisibleKey(), false);
            passwordToggleTouchListener.setVisible(wasVisible);
        }
        passwordEditText.setOnTouchListener(passwordToggleTouchListener);
    }

    private void prepareOkCancelImageButtons() {
        Log.d(PasswordInputDialog.class.getName(), "prepareOkCancelImageButtons");
        ImageView okImage = dialogView.findViewById(R.id.imageview_dialog_password_input_ok);
        ImageView cancelImage = dialogView.findViewById(R.id.imageview_dialog_password_input_cancel);
        okImage.setOnClickListener(this::onOkClicked);
        cancelImage.setOnClickListener(this::onCancelClicked);
    }

    public String getPassword() {
        return StringUtil.notNull(passwordEditText.getText());
    }

    private void onOkClicked(View view) {
        Log.d(PasswordInputDialog.class.getName(), "onOkClicked");
        ValidationResult validationResult = validateInput();
        if (validationResult.isValidationSuccessful()) {
            Log.d(PasswordInputDialog.class.getName(), "Validation was successful");
            PasswordInputSupport passwordInputSupport = getPasswordInputSupport();
            if (passwordInputSupport != null) {
                passwordInputSupport.onPasswordInputDialogOkClicked(this);
            } else {
                Log.e(PasswordInputDialog.class.getName(), "passwordInputSupport is null");
                dismiss();
            }
        } else {
            Log.d(PasswordInputDialog.class.getName(), "Validation failed");
            showValidationMessageDialog(validationResult);
        }

    }

    private boolean hasErrors(List<ValidationResult> validationResult) {
        return !validationResult.isEmpty();
    }

    private void onCancelClicked(View view) {
        Log.d(PasswordInputDialog.class.getName(), "onCancelClicked");
        PasswordInputSupport passwordInputSupport = getPasswordInputSupport();
        if (passwordInputSupport != null) {
            passwordInputSupport.onPasswordInputDialogCancelClicked(this);
        } else {
            Log.e(PasswordInputDialog.class.getName(), "passwordInputSupport is null");
            dismiss();
        }
    }

    private ValidationResult validateInput() {
        Log.d(PasswordInputDialog.class.getName(), "validateInput");
        PasswordInputFieldValidator passwordValidator = new PasswordInputFieldValidator(getResources().getString(R.string.password_field_name), getContext());
        return passwordValidator.validate(getPassword());
    }

    private void showValidationMessageDialog(ValidationResult validationResult) {
        Log.d(PasswordInputDialog.class.getName(), "showMessageDialog, opening ValidatorErrorDialog");
        ValidatorErrorDialog errorDialog = new ValidatorErrorDialog();
        errorDialog.setArguments(BundleUtil.validationResultListToBundle(errorDialog.getValidationResultBaseKey(), List.of(validationResult)));
        errorDialog.show(getParentFragmentManager(), ValidatorErrorDialog.class.getName());
    }

    private PasswordInputSupport getPasswordInputSupport() {
        Log.d(PasswordInputDialog.class.getName(), "getPasswordInputSupport");
        List<Fragment> fragments = getParentFragmentManager().getFragments();
        for (Fragment fragment : fragments) {
            if (fragment instanceof ExportEncryptSupport) {
                return (PasswordInputSupport) fragment;
            }
        }
        Activity activity = getActivity();
        if (activity == null) {
            Log.e(PasswordInputDialog.class.getName(), "getPasswordInputSupport, activity is null");
            return null;
        }
        if (!(activity instanceof PasswordInputSupport)) {
            Log.e(PasswordInputDialog.class.getName(), "getPasswordInputSupport, activity is not an instance of " + PasswordInputSupport.class.getSimpleName());
            return null;
        }
        return (PasswordInputSupport) activity;
    }
}
