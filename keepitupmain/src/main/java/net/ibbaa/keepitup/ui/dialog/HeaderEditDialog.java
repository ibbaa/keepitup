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

import android.app.Activity;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.model.Header;
import net.ibbaa.keepitup.model.HeaderType;
import net.ibbaa.keepitup.ui.ContextOptionsSupportManager;
import net.ibbaa.keepitup.ui.clipboard.IClipboardManager;
import net.ibbaa.keepitup.ui.clipboard.SystemClipboardManager;
import net.ibbaa.keepitup.ui.support.BasicAuthSupport;
import net.ibbaa.keepitup.ui.support.ConfirmSupport;
import net.ibbaa.keepitup.ui.support.ContextOptionsSupport;
import net.ibbaa.keepitup.ui.support.HeaderEditSupport;
import net.ibbaa.keepitup.ui.validation.BasicAuthPasswordFieldValidator;
import net.ibbaa.keepitup.ui.validation.BasicAuthUsernameFieldValidator;
import net.ibbaa.keepitup.ui.validation.HeaderValidator;
import net.ibbaa.keepitup.ui.validation.StandardHeaderValidator;
import net.ibbaa.keepitup.ui.validation.TextColorValidatingWatcher;
import net.ibbaa.keepitup.ui.validation.ValidationResult;
import net.ibbaa.keepitup.util.BundleUtil;
import net.ibbaa.keepitup.util.HTTPUtil;
import net.ibbaa.keepitup.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"unused", "SameReturnValue"})
public class HeaderEditDialog extends DialogFragmentBase implements ContextOptionsSupport, ConfirmSupport, BasicAuthSupport {

    private View dialogView;
    private EditText nameEditText;
    private EditText valueEditText;
    private CheckBox basicAuthCheckBox;
    private TextColorValidatingWatcher nameEditTextWatcher;
    private TextColorValidatingWatcher valueEditTextWatcher;
    private String lastBasicAuthCredentials;

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
        Log.d(HeaderEditDialog.class.getName(), "onCreate");
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.DialogTheme);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(HeaderEditDialog.class.getName(), "onCreateView");
        dialogView = inflater.inflate(R.layout.dialog_header_edit, container);
        initEdgeToEdgeInsets(dialogView);
        Bundle headerBundle = BundleUtil.bundleFromBundle(getHeaderKey(), requireArguments());
        Header header = headerBundle != null ? new Header(headerBundle) : new Header();
        setLastBasicAuthCredentials(savedInstanceState, header);
        prepareNameTextField(header);
        prepareValueTextField(header);
        prepareBasicAuthCheckBox(header);
        prepareOkCancelImageButtons();
        return dialogView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Log.d(HeaderEditDialog.class.getName(), "onSaveInstanceState");
        super.onSaveInstanceState(outState);
        if (lastBasicAuthCredentials != null) {
            outState.putString(getLastBasicAuthCredentialsKey(), lastBasicAuthCredentials);
        }
    }

    private void setLastBasicAuthCredentials(Bundle savedInstanceState, Header header) {
        if (HTTPUtil.isBasicAuthHeader(header)) {
            lastBasicAuthCredentials = header.getValue();
        } else if (savedInstanceState != null) {
            lastBasicAuthCredentials = savedInstanceState.getString(getLastBasicAuthCredentialsKey());
        }
    }

    private void prepareNameTextField(Header header) {
        Log.d(HeaderEditDialog.class.getName(), "prepareNameTextField");
        nameEditText = dialogView.findViewById(R.id.edittext_dialog_header_edit_name);
        nameEditText.setOnLongClickListener(this::onNameEditTextLongClicked);
        nameEditText.setText(StringUtil.notNull(header.getName()));
        prepareNameEditTextListener();
    }

    private void prepareNameEditTextListener() {
        Log.d(HeaderEditDialog.class.getName(), "prepareNameEditTextListener");
        if (nameEditTextWatcher != null) {
            nameEditText.removeTextChangedListener(nameEditTextWatcher);
            nameEditTextWatcher = null;
        }
        nameEditTextWatcher = new TextColorValidatingWatcher(nameEditText, this::validateName, getColor(R.color.textColor), getColor(R.color.textErrorColor));
        nameEditText.addTextChangedListener(nameEditTextWatcher);
    }

    private void prepareValueTextField(Header header) {
        Log.d(HeaderEditDialog.class.getName(), "prepareValueTextField");
        valueEditText = dialogView.findViewById(R.id.edittext_dialog_header_edit_value);
        valueEditText.setOnLongClickListener(this::onValueEditTextLongClicked);
        valueEditText.setText(StringUtil.notNull(header.getValue()));
        prepareValueEditTextListener();
    }

    private void prepareValueEditTextListener() {
        Log.d(HeaderEditDialog.class.getName(), "prepareValueEditTextListener");
        if (valueEditTextWatcher != null) {
            valueEditText.removeTextChangedListener(valueEditTextWatcher);
            valueEditTextWatcher = null;
        }
        valueEditTextWatcher = new TextColorValidatingWatcher(valueEditText, this::validateValue, getColor(R.color.textColor), getColor(R.color.textErrorColor));
        valueEditText.addTextChangedListener(valueEditTextWatcher);
    }

    private void prepareBasicAuthCheckBox(Header header) {
        Log.d(HeaderEditDialog.class.getName(), "prepareBasicAuthCheckBox");
        basicAuthCheckBox = dialogView.findViewById(R.id.checkbox_dialog_header_edit_basic_auth);
        basicAuthCheckBox.setChecked(HTTPUtil.isBasicAuthHeader(header));
        basicAuthCheckBox.setOnClickListener(this::onBasicAuthCheckBoxClicked);
        prepareBasicAuthCheckBoxVisibility();
    }

    private void prepareBasicAuthCheckBoxVisibility() {
        Log.d(HeaderEditDialog.class.getName(), "prepareBasicAuthCheckBoxVisibility");
        if (basicAuthCheckBox.isChecked()) {
            disableNameAndValueFields();
            nameEditText.setOnClickListener(this::onBasicAuthFieldsClicked);
            valueEditText.setOnClickListener(this::onBasicAuthFieldsClicked);
            valueEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        } else {
            nameEditText.setOnClickListener(null);
            valueEditText.setOnClickListener(null);
            enableNameAndValueFields();
            valueEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        }
    }

    private void disableNameAndValueFields() {
        Log.d(HeaderEditDialog.class.getName(), "disableNameAndValueFields");
        nameEditText.setFocusable(false);
        nameEditText.setClickable(true);
        nameEditText.setCursorVisible(false);
        nameEditText.setAlpha(0.5f);
        valueEditText.setFocusable(false);
        valueEditText.setClickable(true);
        valueEditText.setCursorVisible(false);
        valueEditText.setAlpha(0.5f);
    }

    private void enableNameAndValueFields() {
        Log.d(HeaderEditDialog.class.getName(), "enableNameAndValueFields");
        nameEditText.setFocusable(true);
        nameEditText.setFocusableInTouchMode(true);
        nameEditText.setClickable(true);
        nameEditText.setCursorVisible(true);
        nameEditText.setAlpha(1.0f);
        valueEditText.setFocusable(true);
        valueEditText.setFocusableInTouchMode(true);
        valueEditText.setClickable(true);
        valueEditText.setCursorVisible(true);
        valueEditText.setAlpha(1.0f);
    }

    private void onBasicAuthFieldsClicked(@NonNull View checkBox) {
        Log.d(HeaderEditDialog.class.getName(), "onBasicAuthFieldsClicked");
        showBasicAuthDialog();
    }

    private void onBasicAuthCheckBoxClicked(@NonNull View checkBox) {
        Log.d(HeaderEditDialog.class.getName(), "onBasicAuthCheckBoxClicked");
        if (basicAuthCheckBox.isChecked()) {
            basicAuthCheckBox.setChecked(false);
            showBasicAuthDialog();
        } else {
            nameEditText.setText("");
            valueEditText.setText("");
            prepareBasicAuthCheckBoxVisibility();
        }
    }

    private void prepareOkCancelImageButtons() {
        Log.d(HeaderEditDialog.class.getName(), "prepareOkCancelImageButtons");
        ImageView okImage = dialogView.findViewById(R.id.imageview_dialog_header_edit_ok);
        ImageView cancelImage = dialogView.findViewById(R.id.imageview_dialog_header_edit_cancel);
        okImage.setOnClickListener(this::onOkClicked);
        cancelImage.setOnClickListener(this::onCancelClicked);
    }

    public String getHeaderKey() {
        return HeaderEditDialog.class.getName() + ".Header";
    }

    public String getPositionKey() {
        return HeaderEditDialog.class.getSimpleName() + ".Position";
    }

    public String getLastBasicAuthCredentialsKey() {
        return HeaderEditDialog.class.getSimpleName() + ".LastBasicAuthCredentials";
    }

    public String getName() {
        return StringUtil.notNull(nameEditText.getText()).trim();
    }

    public String getValue() {
        return StringUtil.notNull(valueEditText.getText());
    }

    private void onOkClicked(View view) {
        Log.d(HeaderEditDialog.class.getName(), "onOkClicked");
        int position = BundleUtil.integerFromBundle(getPositionKey(), requireArguments());
        List<ValidationResult> validationResult = validateInput(position);
        if (!hasErrors(validationResult)) {
            Log.d(HeaderEditDialog.class.getName(), "Validation was successful");
            if (getHeader().isValueSecret()) {
                Log.d(HeaderEditDialog.class.getName(), "Header is an authorization header");
                showConfirmDialog(position);
                return;
            }
            HeaderEditSupport headerEditSupport = getGlobalHeaderEditSupport();
            if (headerEditSupport != null) {
                headerEditSupport.onHeaderEditDialogOkClicked(this, position);
            } else {
                Log.e(HeaderEditDialog.class.getName(), "globalHeaderEditSupport is null");
                dismiss();
            }
        } else {
            Log.d(HeaderEditDialog.class.getName(), "Validation failed");
            showValidationErrorDialog(validationResult);
        }
    }

    private void onCancelClicked(View view) {
        Log.d(HeaderEditDialog.class.getName(), "onCancelClicked");
        HeaderEditSupport headerEditSupport = getGlobalHeaderEditSupport();
        if (headerEditSupport != null) {
            headerEditSupport.onHeaderEditDialogCancelClicked(this);
        } else {
            Log.e(HeaderEditDialog.class.getName(), "settingsInputSupport is null");
            dismiss();
        }
    }

    @Override
    public void onConfirmDialogOkClicked(ConfirmDialog confirmDialog, ConfirmDialog.Type type) {
        Log.d(HeaderEditDialog.class.getName(), "onConfirmDialogOkClicked for type " + type);
        if (ConfirmDialog.Type.CONFIRMAUTHORIZATIONHEADER.equals(type)) {
            int position = confirmDialog.getPosition();
            HeaderEditSupport headerEditSupport = getGlobalHeaderEditSupport();
            if (headerEditSupport != null) {
                headerEditSupport.onHeaderEditDialogOkClicked(this, position);
            } else {
                Log.e(HeaderEditDialog.class.getName(), "globalHeaderEditSupport is null");
                dismiss();
            }
        }
        confirmDialog.dismiss();
    }

    @Override
    public void onConfirmDialogCancelClicked(ConfirmDialog confirmDialog, ConfirmDialog.Type type) {
        Log.d(HeaderEditDialog.class.getName(), "onConfirmDialogCancelClicked for type " + type);
        confirmDialog.dismiss();
    }

    @Override
    public void onBasicAuthDialogOkClicked(BasicAuthDialog basicAuthDialog) {
        Log.d(HeaderEditDialog.class.getName(), "onBasicAuthDialogOkClicked");
        basicAuthCheckBox.setChecked(true);
        nameEditText.setText(getResources().getString(R.string.http_header_authorization));
        lastBasicAuthCredentials = basicAuthDialog.getUsernameAndPassword();
        valueEditText.setText(lastBasicAuthCredentials);
        basicAuthDialog.dismiss();
        prepareBasicAuthCheckBoxVisibility();
    }

    @Override
    public void onBasicAuthDialogCancelClicked(BasicAuthDialog basicAuthDialog) {
        Log.d(HeaderEditDialog.class.getName(), "onBasicAuthDialogCancelClicked");
        basicAuthDialog.dismiss();
        prepareBasicAuthCheckBoxVisibility();
    }

    public Header getHeader() {
        Log.d(HeaderEditDialog.class.getName(), "getHeader");
        Bundle headerBundle = BundleUtil.bundleFromBundle(getHeaderKey(), requireArguments());
        Header header = headerBundle != null ? new Header(headerBundle) : new Header();
        header.setName(getName());
        header.setValue(getValue());
        HeaderType headerType = getHeaderType();
        header.setHeaderType(headerType);
        return header;
    }

    private HeaderType getHeaderType() {
        HeaderType headerType = HeaderType.GENERIC;
        if (basicAuthCheckBox.isChecked()) {
            headerType = HeaderType.BASICAUTH;
        } else if (HTTPUtil.isAuthorizationHeader(getContext(), getName())) {
            headerType = HeaderType.GENERICAUTH;
        }
        return headerType;
    }

    private boolean hasErrors(List<ValidationResult> validationResult) {
        return !validationResult.isEmpty();
    }

    private boolean validateName(EditText editText) {
        Log.d(HeaderEditDialog.class.getName(), "validateName");
        HeaderValidator validator = new StandardHeaderValidator(getContext());
        ValidationResult result = validator.validateName(getName());
        Log.d(HeaderEditDialog.class.getName(), "Validation result: " + result);
        return result.isValidationSuccessful();
    }

    private boolean validateValue(EditText editText) {
        Log.d(HeaderEditDialog.class.getName(), "validateValue");
        HeaderValidator validator = new StandardHeaderValidator(getContext());
        ValidationResult result = validator.validateValue(getValue());
        Log.d(HeaderEditDialog.class.getName(), "Validation result: " + result);
        return result.isValidationSuccessful();
    }

    private List<ValidationResult> validateInput(int position) {
        Log.d(HeaderEditDialog.class.getName(), "validateInput for position " + position);
        List<ValidationResult> validationResults = new ArrayList<>();
        HeaderValidator validator = new StandardHeaderValidator(getContext());
        ValidationResult nameResult = validator.validateName(getName());
        ValidationResult valueResult = validator.validateValue(getValue());
        if (!nameResult.isValidationSuccessful()) {
            validationResults.add(nameResult);
        }
        if (getHeaderType().isBasicAuth()) {
            String[] usernameAndPassword = StringUtil.splitAtFirstColon(getValue());
            String username = usernameAndPassword[0];
            String password = usernameAndPassword[1];
            BasicAuthUsernameFieldValidator usernameValidator = new BasicAuthUsernameFieldValidator(getResources().getString(R.string.basic_auth_username_field_name), getContext());
            ValidationResult usernameResult = usernameValidator.validate(username);
            if (!usernameResult.isValidationSuccessful()) {
                validationResults.add(usernameResult);
            }
            BasicAuthPasswordFieldValidator passwordValidator = new BasicAuthPasswordFieldValidator(getResources().getString(R.string.basic_auth_password_field_name), getContext());
            ValidationResult passwordResult = passwordValidator.validate(password);
            if (!passwordResult.isValidationSuccessful()) {
                validationResults.add(passwordResult);
            }
        }
        HeaderEditSupport headerEditSupport = getGlobalHeaderEditSupport();
        if (headerEditSupport != null) {
            List<String> currentHeaderNames = headerEditSupport.getExistingHeaderNames();
            if (position >= 0 && position < currentHeaderNames.size()) {
                currentHeaderNames.remove(position);
            }
            ValidationResult nameExistsResult = validator.validateNameExists(currentHeaderNames, getName());
            if (!nameExistsResult.isValidationSuccessful()) {
                validationResults.add(nameExistsResult);
            }
        } else {
            Log.e(HeaderEditDialog.class.getName(), "globalHeaderEditSupport is null");
        }
        if (!valueResult.isValidationSuccessful()) {
            validationResults.add(valueResult);
        }
        return validationResults;
    }

    private boolean containsValidationResult(List<ValidationResult> validationResults, ValidationResult result) {
        for (ValidationResult currentResult : validationResults) {
            if (currentResult.isEqual(result)) {
                return true;
            }
        }
        return false;
    }

    private void showValidationErrorDialog(List<ValidationResult> validationResult) {
        Log.d(HeaderEditDialog.class.getName(), "showValidationErrorDialog");
        ValidatorErrorDialog errorDialog = new ValidatorErrorDialog();
        errorDialog.setArguments(BundleUtil.validationResultListToBundle(errorDialog.getValidationResultBaseKey(), validationResult));
        errorDialog.show(getParentFragmentManager(), ValidatorErrorDialog.class.getName());
    }

    protected void showConfirmDialog(int position) {
        Log.d(HeaderEditDialog.class.getName(), "showConfirmDialog for position " + position);
        String message = getResources().getString(R.string.text_dialog_confirm_confirm_authorization_header);
        String description = getResources().getString(R.string.text_dialog_confirm_add_authorization_header_description);
        ConfirmDialog.Type type = ConfirmDialog.Type.CONFIRMAUTHORIZATIONHEADER;
        ConfirmDialog confirmDialog = new ConfirmDialog();
        Bundle bundle = BundleUtil.stringsToBundle(new String[]{confirmDialog.getMessageKey(), confirmDialog.getDescriptionKey(), confirmDialog.getTypeKey()}, new String[]{message, description, type.name()});
        bundle.putInt(confirmDialog.getPositionKey(), position);
        confirmDialog.setArguments(bundle);
        confirmDialog.show(getParentFragmentManager(), ConfirmDialog.class.getName());
    }

    private void showBasicAuthDialog() {
        Log.d(HeaderEditDialog.class.getName(), "showBasicAuthDialog");
        BasicAuthDialog basicAuthDialog = new BasicAuthDialog();
        if (lastBasicAuthCredentials != null) {
            Bundle bundle = BundleUtil.stringToBundle(basicAuthDialog.getInitialUsernameAndPasswordKey(), lastBasicAuthCredentials);
            basicAuthDialog.setArguments(bundle);
        }
        basicAuthDialog.show(getParentFragmentManager(), BasicAuthDialog.class.getName());
    }

    private boolean onNameEditTextLongClicked(View view) {
        Log.d(HeaderEditDialog.class.getName(), "onNameEditTextLongClicked");
        showContextOptionsDialog((EditText) view);
        return true;
    }

    private boolean onValueEditTextLongClicked(View view) {
        Log.d(HeaderEditDialog.class.getName(), "onValueEditTextLongClicked");
        showContextOptionsDialog((EditText) view);
        return true;
    }

    private void showContextOptionsDialog(EditText editText) {
        Log.d(HeaderEditDialog.class.getName(), "showContextOptionsDialog");
        new ContextOptionsSupportManager(getParentFragmentManager(), getClipboardManager()).showContextOptionsDialog(editText);
    }

    @Override
    public void onContextOptionsDialogClicked(ContextOptionsDialog contextOptionsDialog, int sourceResourceId, ContextOption option) {
        Log.d(HeaderEditDialog.class.getName(), "onContextOptionsDialogClicked, sourceResourceId is " + sourceResourceId + ", option is " + option);
        ContextOptionsSupportManager contextOptionsSupportManager = new ContextOptionsSupportManager(getParentFragmentManager(), getClipboardManager());
        if (nameEditText.getId() == sourceResourceId) {
            Log.e(HeaderEditDialog.class.getName(), "Source field is the name input field.");
            contextOptionsSupportManager.handleContextOption(nameEditText, option);
            nameEditText.setSelection(nameEditText.getText().length());
        } else if (valueEditText.getId() == sourceResourceId) {
            Log.e(HeaderEditDialog.class.getName(), "Source field is the value input field.");
            contextOptionsSupportManager.handleContextOption(valueEditText, option);
            valueEditText.setSelection(valueEditText.getText().length());
        } else {
            Log.e(HeaderEditDialog.class.getName(), "Source field is undefined.");
        }
        contextOptionsDialog.dismiss();
    }

    private int getColor(int colorid) {
        return ContextCompat.getColor(requireContext(), colorid);
    }

    private HeaderEditSupport getGlobalHeaderEditSupport() {
        Log.d(HeaderEditDialog.class.getName(), "getGlobalHeaderEditSupport");
        List<Fragment> fragments = getParentFragmentManager().getFragments();
        for (Fragment fragment : fragments) {
            if (fragment instanceof HeaderEditSupport) {
                return (HeaderEditSupport) fragment;
            }
        }
        Activity activity = getActivity();
        if (activity == null) {
            Log.e(HeaderEditDialog.class.getName(), "getGlobalHeaderEditSupport, activity is null");
            return null;
        }
        if (!(activity instanceof HeaderEditSupport)) {
            Log.e(HeaderEditDialog.class.getName(), "getGlobalHeaderEditSupport, activity is not an instance of " + HeaderEditSupport.class.getSimpleName());
            return null;
        }
        return (HeaderEditSupport) activity;
    }
}
