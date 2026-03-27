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
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.ui.ContextOptionsSupportManager;
import net.ibbaa.keepitup.ui.clipboard.IClipboardManager;
import net.ibbaa.keepitup.ui.clipboard.SystemClipboardManager;
import net.ibbaa.keepitup.ui.support.BasicAuthSupport;
import net.ibbaa.keepitup.ui.support.ContextOptionsSupport;
import net.ibbaa.keepitup.ui.validation.BasicAuthPasswordFieldValidator;
import net.ibbaa.keepitup.ui.validation.BasicAuthUsernameFieldValidator;
import net.ibbaa.keepitup.ui.validation.TextColorValidatingWatcher;
import net.ibbaa.keepitup.ui.validation.ValidationResult;
import net.ibbaa.keepitup.util.BundleUtil;
import net.ibbaa.keepitup.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"unused", "SameReturnValue"})
public class BasicAuthDialog extends DialogFragmentBase implements ContextOptionsSupport {

    private View dialogView;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private TextColorValidatingWatcher usernameEditTextWatcher;
    private PasswordToggleTouchListener passwordToggleTouchListener;
    private String initialPassword;
    private boolean passwordToggleOpen;

    private IClipboardManager clipboardManager;

    public void injectClipboardManager(IClipboardManager clipboardManager) {
        this.clipboardManager = clipboardManager;
    }

    public IClipboardManager getClipboardManager() {
        if (clipboardManager != null) {
            return clipboardManager;
        }
        return new SystemClipboardManager(requireContext());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(BasicAuthDialog.class.getName(), "onCreate");
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.DialogTheme);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(BasicAuthDialog.class.getName(), "onCreateView");
        dialogView = inflater.inflate(R.layout.dialog_basic_auth, container);
        initEdgeToEdgeInsets(dialogView);
        prepareUsernameTextField();
        preparePasswordTextField(savedInstanceState);
        prepareInitialUsernameAndPassword(savedInstanceState);
        prepareOkCancelImageButtons();
        return dialogView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (passwordToggleTouchListener != null) {
            outState.putBoolean(getPasswordVisibleKey(), passwordToggleTouchListener.isVisible());
        }
        if (initialPassword != null) {
            outState.putString(getInitialPasswordKey(), initialPassword);
        }
        outState.putBoolean(getPasswordToggleOpenKey(), passwordToggleOpen);
    }

    public String getPasswordVisibleKey() {
        return BasicAuthDialog.class.getSimpleName() + ".PasswordVisible";
    }

    private String getPasswordToggleOpenKey() {
        return BasicAuthDialog.class.getSimpleName() + ".PasswordToggleOpen";
    }

    private String getInitialPasswordKey() {
        return BasicAuthDialog.class.getSimpleName() + ".InitialPassword";
    }

    public String getInitialUsernameAndPasswordKey() {
        return BasicAuthDialog.class.getSimpleName() + ".InitialUsernameAndPassword";
    }

    private void prepareUsernameTextField() {
        Log.d(BasicAuthDialog.class.getName(), "prepareUsernameTextField");
        usernameEditText = dialogView.findViewById(R.id.edittext_dialog_basic_auth_username);
        usernameEditText.setOnLongClickListener(this::onUsernameEditTextLongClicked);
        prepareUsernameEditTextListener();
    }

    private void prepareUsernameEditTextListener() {
        Log.d(BasicAuthDialog.class.getName(), "prepareUsernameEditTextListener");
        if (usernameEditTextWatcher != null) {
            usernameEditText.removeTextChangedListener(usernameEditTextWatcher);
            usernameEditTextWatcher = null;
        }
        usernameEditTextWatcher = new TextColorValidatingWatcher(usernameEditText, this::validateUsername, getColor(R.color.textColor), getColor(R.color.textErrorColor));
        usernameEditText.addTextChangedListener(usernameEditTextWatcher);
    }

    private void preparePasswordTextField(Bundle savedInstanceState) {
        Log.d(BasicAuthDialog.class.getName(), "preparePasswordTextField");
        passwordEditText = dialogView.findViewById(R.id.edittext_dialog_basic_auth_password);
        preparePasswordToggleTouchListener(savedInstanceState);
    }

    private void prepareInitialUsernameAndPassword(Bundle savedInstanceState) {
        Log.d(BasicAuthDialog.class.getName(), "prepareInitialUsernameAndPassword");
        if (savedInstanceState == null) {
            if (hasInitialUsernameAndPassword()) {
                Log.d(BasicAuthDialog.class.getName(), "prepareInitialUsernameAndPassword, initializing dialog with provided data");
                String initialUsernameAndPassword = BundleUtil.stringFromBundle(getInitialUsernameAndPasswordKey(), requireArguments());
                String[] usernameAndPassword = StringUtil.splitAtFirstColon(initialUsernameAndPassword);
                usernameEditText.setText(usernameAndPassword[0]);
                passwordEditText.setText(StringUtil.notNull(usernameAndPassword[1]));
                initialPassword = StringUtil.isEmpty(usernameAndPassword[1]) ? null : usernameAndPassword[1];
                passwordToggleOpen = initialPassword == null;
                if (passwordToggleTouchListener != null) {
                    passwordToggleTouchListener.setEnabled(passwordToggleOpen);
                }
            } else {
                Log.d(BasicAuthDialog.class.getName(), "prepareInitialUsernameAndPassword, initializing dialog with empty data");
                usernameEditText.setText("");
                passwordEditText.setText("");
                initialPassword = null;
                passwordToggleOpen = true;
                if (passwordToggleTouchListener != null) {
                    passwordToggleTouchListener.setEnabled(true);
                }
            }
        } else {
            Log.d(BasicAuthDialog.class.getName(), "prepareInitialUsernameAndPassword, restoring dialog");
            if (savedInstanceState.containsKey(getInitialPasswordKey())) {
                initialPassword = savedInstanceState.getString(getInitialPasswordKey());
            }
            passwordToggleOpen = savedInstanceState.getBoolean(getPasswordToggleOpenKey());
            if (passwordToggleTouchListener != null) {
                passwordToggleTouchListener.setEnabled(passwordToggleOpen);
            }
        }
    }

    private boolean hasInitialUsernameAndPassword() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            return arguments.containsKey(getInitialUsernameAndPasswordKey());
        }
        return false;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void preparePasswordToggleTouchListener(Bundle savedInstanceState) {
        Log.d(BasicAuthDialog.class.getName(), "preparePasswordToggleTouchListener");
        passwordToggleTouchListener = new PasswordToggleTouchListener(passwordEditText);
        if (savedInstanceState != null) {
            boolean wasVisible = savedInstanceState.getBoolean(getPasswordVisibleKey(), false);
            passwordToggleTouchListener.setVisible(wasVisible);
        }
        passwordEditText.setOnTouchListener(passwordToggleTouchListener);
        passwordEditText.setOnFocusChangeListener(this::onPasswordFieldClicked);
    }

    private void onPasswordFieldClicked(View view, boolean hasFocus) {
        Log.d(BasicAuthDialog.class.getName(), "onPasswordFieldClicked, hasFocus is " + hasFocus);
        if (!passwordToggleOpen) {
            passwordEditText.setText("");
        }
        passwordToggleOpen = true;
        if (passwordToggleTouchListener != null) {
            passwordToggleTouchListener.setEnabled(true);
        }
    }

    private void prepareOkCancelImageButtons() {
        Log.d(BasicAuthDialog.class.getName(), "prepareOkCancelImageButtons");
        ImageView okImage = dialogView.findViewById(R.id.imageview_dialog_basic_auth_ok);
        ImageView cancelImage = dialogView.findViewById(R.id.imageview_dialog_basic_auth_cancel);
        okImage.setOnClickListener(this::onOkClicked);
        cancelImage.setOnClickListener(this::onCancelClicked);
    }

    public String getUsername() {
        return StringUtil.notNull(usernameEditText.getText());
    }

    public String getPassword() {
        return StringUtil.notNull(passwordEditText.getText());
    }

    public String getUsernameAndPassword() {
        String username = getUsername();
        String password = getPassword();
        if (StringUtil.isEmpty(password) && initialPassword != null) {
            password = initialPassword;
        }
        return username + ":" + password;
    }

    private boolean onUsernameEditTextLongClicked(View view) {
        Log.d(BasicAuthDialog.class.getName(), "onUsernameEditTextLongClicked");
        showContextOptionsDialog((EditText) view);
        return true;
    }

    private void onOkClicked(View view) {
        Log.d(BasicAuthDialog.class.getName(), "onOkClicked");
        List<ValidationResult> validationResult = validateInput();
        if (!hasErrors(validationResult)) {
            Log.d(BasicAuthDialog.class.getName(), "Validation was successful");
            BasicAuthSupport basicAuthSupport = getBasicAuthSupport();
            if (basicAuthSupport != null) {
                basicAuthSupport.onBasicAuthDialogOkClicked(this);
            } else {
                Log.e(BasicAuthDialog.class.getName(), "basicAuthSupport is null");
                dismiss();
            }
        } else {
            Log.d(BasicAuthDialog.class.getName(), "Validation failed");
            showValidationErrorDialog(validationResult);
        }
    }

    private boolean hasErrors(List<ValidationResult> validationResult) {
        return !validationResult.isEmpty();
    }

    private void onCancelClicked(View view) {
        Log.d(BasicAuthDialog.class.getName(), "onCancelClicked");
        BasicAuthSupport basicAuthSupport = getBasicAuthSupport();
        if (basicAuthSupport != null) {
            basicAuthSupport.onBasicAuthDialogCancelClicked(this);
        } else {
            Log.e(BasicAuthDialog.class.getName(), "basicAuthSupport is null");
            dismiss();
        }
    }

    private boolean validateUsername(EditText editText) {
        Log.d(BasicAuthDialog.class.getName(), "validateUsername");
        BasicAuthUsernameFieldValidator usernameValidator = new BasicAuthUsernameFieldValidator(getResources().getString(R.string.username_field_name), getContext());
        ValidationResult usernameResult = usernameValidator.validate(getUsername());
        return usernameResult.isValidationSuccessful();
    }

    private List<ValidationResult> validateInput() {
        Log.d(BasicAuthDialog.class.getName(), "validateInput");
        List<ValidationResult> validationResults = new ArrayList<>();
        BasicAuthUsernameFieldValidator usernameValidator = new BasicAuthUsernameFieldValidator(getResources().getString(R.string.username_field_name), getContext());
        ValidationResult usernameResult = usernameValidator.validate(getUsername());
        if (!usernameResult.isValidationSuccessful()) {
            validationResults.add(usernameResult);
        }
        if (!StringUtil.isEmpty(getPassword()) || StringUtil.isEmpty(initialPassword)) {
            BasicAuthPasswordFieldValidator passwordValidator = new BasicAuthPasswordFieldValidator(getResources().getString(R.string.password_field_name), getContext());
            ValidationResult passwordResult = passwordValidator.validate(getPassword());
            if (!passwordResult.isValidationSuccessful()) {
                validationResults.add(passwordResult);
            }
        }
        return validationResults;
    }

    private void showValidationErrorDialog(List<ValidationResult> validationResult) {
        Log.d(BasicAuthDialog.class.getName(), "showValidationErrorDialog");
        ValidatorErrorDialog errorDialog = new ValidatorErrorDialog();
        errorDialog.setArguments(BundleUtil.validationResultListToBundle(errorDialog.getValidationResultBaseKey(), validationResult));
        errorDialog.show(getParentFragmentManager(), ValidatorErrorDialog.class.getName());
    }

    private void showContextOptionsDialog(EditText editText) {
        Log.d(BasicAuthDialog.class.getName(), "showContextOptionsDialog");
        new ContextOptionsSupportManager(getParentFragmentManager(), getClipboardManager()).showContextOptionsDialog(editText);
    }

    @Override
    public void onContextOptionsDialogClicked(ContextOptionsDialog contextOptionsDialog, int sourceResourceId, ContextOption option) {
        Log.d(BasicAuthDialog.class.getName(), "onContextOptionsDialogClicked, sourceResourceId is " + sourceResourceId + ", option is " + option);
        ContextOptionsSupportManager contextOptionsSupportManager = new ContextOptionsSupportManager(getParentFragmentManager(), getClipboardManager());
        if (usernameEditText.getId() == sourceResourceId) {
            Log.e(BasicAuthDialog.class.getName(), "Source field is the correct value input field.");
            contextOptionsSupportManager.handleContextOption(usernameEditText, option);
            usernameEditText.setSelection(usernameEditText.getText().length());
        } else {
            Log.e(BasicAuthDialog.class.getName(), "Source field is undefined.");
        }
        contextOptionsDialog.dismiss();
    }

    private int getColor(int colorid) {
        return ContextCompat.getColor(requireContext(), colorid);
    }

    private BasicAuthSupport getBasicAuthSupport() {
        Log.d(BasicAuthDialog.class.getName(), "getBasicAuthSupport");
        List<Fragment> fragments = getParentFragmentManager().getFragments();
        for (Fragment fragment : fragments) {
            if (fragment instanceof BasicAuthSupport) {
                return (BasicAuthSupport) fragment;
            }
        }
        Activity activity = getActivity();
        if (activity == null) {
            Log.e(BasicAuthDialog.class.getName(), "getBasicAuthSupport, activity is null");
            return null;
        }
        if (!(activity instanceof BasicAuthSupport)) {
            Log.e(BasicAuthDialog.class.getName(), "getBasicAuthSupport, activity is not an instance of " + BasicAuthSupport.class.getSimpleName());
            return null;
        }
        return (BasicAuthSupport) activity;
    }
}
