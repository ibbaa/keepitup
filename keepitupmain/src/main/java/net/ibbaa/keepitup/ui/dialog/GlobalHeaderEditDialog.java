/*
 * Copyright (c) 2025 Alwin Ibba
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
import net.ibbaa.keepitup.model.Header;
import net.ibbaa.keepitup.ui.ContextOptionsSupportManager;
import net.ibbaa.keepitup.ui.clipboard.IClipboardManager;
import net.ibbaa.keepitup.ui.clipboard.SystemClipboardManager;
import net.ibbaa.keepitup.ui.support.ContextOptionsSupport;
import net.ibbaa.keepitup.ui.support.GlobalHeaderEditSupport;
import net.ibbaa.keepitup.ui.validation.HeaderNameFieldValidator;
import net.ibbaa.keepitup.ui.validation.HeaderValueFieldValidator;
import net.ibbaa.keepitup.ui.validation.TextColorValidatingWatcher;
import net.ibbaa.keepitup.ui.validation.ValidationResult;
import net.ibbaa.keepitup.util.BundleUtil;
import net.ibbaa.keepitup.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"unused", "SameReturnValue"})
public class GlobalHeaderEditDialog extends DialogFragmentBase implements ContextOptionsSupport {

    private View dialogView;
    private EditText nameEditText;
    private EditText valueEditText;
    private TextColorValidatingWatcher nameEditTextWatcher;
    private TextColorValidatingWatcher valueEditTextWatcher;

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
        Log.d(GlobalHeaderEditDialog.class.getName(), "onCreate");
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.DialogTheme);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(GlobalHeaderEditDialog.class.getName(), "onCreateView");
        dialogView = inflater.inflate(R.layout.dialog_global_header_edit, container);
        initEdgeToEdgeInsets(dialogView);
        Bundle headerBundle = BundleUtil.bundleFromBundle(getHeaderKey(), requireArguments());
        Header header = headerBundle != null ? new Header(headerBundle) : new Header();
        prepareNameTextField(header);
        prepareValueTextField(header);
        prepareOkCancelImageButtons();
        return dialogView;
    }

    private void prepareNameTextField(Header header) {
        Log.d(GlobalHeaderEditDialog.class.getName(), "prepareNameTextField");
        nameEditText = dialogView.findViewById(R.id.edittext_dialog_global_header_edit_name);
        nameEditText.setOnLongClickListener(this::onNameEditTextLongClicked);
        nameEditText.setText(StringUtil.notNull(header.getName()));
        prepareNameEditTextListener();
    }

    private void prepareNameEditTextListener() {
        Log.d(GlobalHeaderEditDialog.class.getName(), "prepareNameEditTextListener");
        if (nameEditTextWatcher != null) {
            nameEditText.removeTextChangedListener(nameEditTextWatcher);
            nameEditTextWatcher = null;
        }
        nameEditTextWatcher = new TextColorValidatingWatcher(nameEditText, this::validateName, getColor(R.color.textColor), getColor(R.color.textErrorColor));
        nameEditText.addTextChangedListener(nameEditTextWatcher);
    }

    private void prepareValueTextField(Header header) {
        Log.d(GlobalHeaderEditDialog.class.getName(), "prepareValueTextField");
        valueEditText = dialogView.findViewById(R.id.edittext_dialog_global_header_edit_value);
        valueEditText.setOnLongClickListener(this::onValueEditTextLongClicked);
        valueEditText.setText(StringUtil.notNull(header.getValue()));
        prepareValueEditTextListener();
    }

    private void prepareValueEditTextListener() {
        Log.d(GlobalHeaderEditDialog.class.getName(), "prepareValueEditTextListener");
        if (valueEditTextWatcher != null) {
            valueEditText.removeTextChangedListener(valueEditTextWatcher);
            valueEditTextWatcher = null;
        }
        valueEditTextWatcher = new TextColorValidatingWatcher(valueEditText, this::validateValue, getColor(R.color.textColor), getColor(R.color.textErrorColor));
        valueEditText.addTextChangedListener(valueEditTextWatcher);
    }

    private void prepareOkCancelImageButtons() {
        Log.d(GlobalHeaderEditDialog.class.getName(), "prepareOkCancelImageButtons");
        ImageView okImage = dialogView.findViewById(R.id.imageview_dialog_global_header_edit_ok);
        ImageView cancelImage = dialogView.findViewById(R.id.imageview_dialog_global_header_edit_cancel);
        okImage.setOnClickListener(this::onOkClicked);
        cancelImage.setOnClickListener(this::onCancelClicked);
    }

    public String getHeaderKey() {
        return GlobalHeaderEditDialog.class.getName() + ".Header";
    }

    public String getPositionKey() {
        return GlobalHeaderEditDialog.class.getSimpleName() + ".Position";
    }

    public String getName() {
        return StringUtil.notNull(nameEditText.getText()).trim();
    }

    public String getValue() {
        return StringUtil.notNull(valueEditText.getText());
    }

    private void onOkClicked(View view) {
        Log.d(GlobalHeaderEditDialog.class.getName(), "onOkClicked");
        List<ValidationResult> validationResult = validateInput();
        if (!hasErrors(validationResult)) {
            Log.d(GlobalHeaderEditDialog.class.getName(), "Validation was successful");
            GlobalHeaderEditSupport globalHeaderEditSupport = getGlobalHeaderEditSupport();
            if (globalHeaderEditSupport != null) {
                globalHeaderEditSupport.onGlobalHeaderEditDialogOkClicked(this, BundleUtil.integerFromBundle(getPositionKey(), requireArguments()));
            } else {
                Log.e(GlobalHeaderEditDialog.class.getName(), "globalHeaderEditSupport is null");
                dismiss();
            }
        } else {
            Log.d(GlobalHeaderEditDialog.class.getName(), "Validation failed");
            showMessageDialog(validationResult);
        }
    }

    private void onCancelClicked(View view) {
        Log.d(GlobalHeaderEditDialog.class.getName(), "onCancelClicked");
        GlobalHeaderEditSupport globalHeaderEditSupport = getGlobalHeaderEditSupport();
        if (globalHeaderEditSupport != null) {
            globalHeaderEditSupport.onGlobalHeaderEditDialogCancelClicked(this);
        } else {
            Log.e(GlobalHeaderEditDialog.class.getName(), "settingsInputSupport is null");
            dismiss();
        }
    }

    public Header getHeader() {
        Log.d(GlobalHeaderEditDialog.class.getName(), "getHeader");
        Bundle headerBundle = BundleUtil.bundleFromBundle(getHeaderKey(), requireArguments());
        Header header = headerBundle != null ? new Header(headerBundle) : new Header();
        header.setName(getName());
        header.setValue(getValue());
        return header;
    }

    private boolean hasErrors(List<ValidationResult> validationResult) {
        return !validationResult.isEmpty();
    }

    private boolean validateName(EditText editText) {
        Log.d(GlobalHeaderEditDialog.class.getName(), "validateName");
        HeaderNameFieldValidator validator = new HeaderNameFieldValidator(getResources().getString(R.string.label_dialog_global_header_edit_name), getContext());
        ValidationResult result = validator.validate(getName());
        Log.d(GlobalHeaderEditDialog.class.getName(), "Validation result: " + result);
        return result.isValidationSuccessful();
    }

    private boolean validateValue(EditText editText) {
        Log.d(GlobalHeaderEditDialog.class.getName(), "validateValue");
        HeaderValueFieldValidator validator = new HeaderValueFieldValidator(getResources().getString(R.string.label_dialog_global_header_edit_value), getContext());
        ValidationResult result = validator.validate(getValue());
        Log.d(GlobalHeaderEditDialog.class.getName(), "Validation result: " + result);
        return result.isValidationSuccessful();
    }

    private List<ValidationResult> validateInput() {
        Log.d(GlobalHeaderEditDialog.class.getName(), "validateInput");
        List<ValidationResult> validationResults = new ArrayList<>();
        HeaderNameFieldValidator nameFieldValidator = new HeaderNameFieldValidator(getResources().getString(R.string.label_dialog_global_header_edit_name), getContext());
        HeaderValueFieldValidator valueFieldValidator = new HeaderValueFieldValidator(getResources().getString(R.string.label_dialog_global_header_edit_value), getContext());
        ValidationResult nameResult = nameFieldValidator.validate(getName());
        ValidationResult valueResult = valueFieldValidator.validate(getValue());
        Log.d(GlobalHeaderEditDialog.class.getName(), "Validation of name field result: " + nameResult);
        Log.d(GlobalHeaderEditDialog.class.getName(), "Validation of value field result: " + valueResult);
        if (!nameResult.isValidationSuccessful()) {
            validationResults.add(nameResult);
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

    private void showMessageDialog(List<ValidationResult> validationResult) {
        Log.d(GlobalHeaderEditDialog.class.getName(), "showMessageDialog, opening ValidatorErrorDialog");
        ValidatorErrorDialog errorDialog = new ValidatorErrorDialog();
        errorDialog.setArguments(BundleUtil.validationResultListToBundle(errorDialog.getValidationResultBaseKey(), validationResult));
        errorDialog.show(getParentFragmentManager(), ValidatorErrorDialog.class.getName());
    }

    private boolean onNameEditTextLongClicked(View view) {
        Log.d(GlobalHeaderEditDialog.class.getName(), "onNameEditTextLongClicked");
        showContextOptionsDialog((EditText) view);
        return true;
    }

    private boolean onValueEditTextLongClicked(View view) {
        Log.d(GlobalHeaderEditDialog.class.getName(), "onValueEditTextLongClicked");
        showContextOptionsDialog((EditText) view);
        return true;
    }

    private void showContextOptionsDialog(EditText editText) {
        Log.d(GlobalHeaderEditDialog.class.getName(), "showContextOptionsDialog");
        new ContextOptionsSupportManager(getParentFragmentManager(), getClipboardManager()).showContextOptionsDialog(editText);
    }

    @Override
    public void onContextOptionsDialogClicked(ContextOptionsDialog contextOptionsDialog, int sourceResourceId, ContextOption option) {
        Log.d(GlobalHeaderEditDialog.class.getName(), "onContextOptionsDialogEntryClicked, sourceResourceId is " + sourceResourceId + ", option is " + option);
        ContextOptionsSupportManager contextOptionsSupportManager = new ContextOptionsSupportManager(getParentFragmentManager(), getClipboardManager());
        if (nameEditText.getId() == sourceResourceId) {
            Log.e(GlobalHeaderEditDialog.class.getName(), "Source field is the name input field.");
            contextOptionsSupportManager.handleContextOption(nameEditText, option);
            nameEditText.setSelection(nameEditText.getText().length());
        } else if (valueEditText.getId() == sourceResourceId) {
            Log.e(GlobalHeaderEditDialog.class.getName(), "Source field is the value input field.");
            contextOptionsSupportManager.handleContextOption(valueEditText, option);
            valueEditText.setSelection(valueEditText.getText().length());
        } else {
            Log.e(GlobalHeaderEditDialog.class.getName(), "Source field is undefined.");
        }
        contextOptionsDialog.dismiss();
    }

    private int getColor(int colorid) {
        return ContextCompat.getColor(requireContext(), colorid);
    }

    private GlobalHeaderEditSupport getGlobalHeaderEditSupport() {
        Log.d(GlobalHeaderEditDialog.class.getName(), "getGlobalHeaderEditSupport");
        List<Fragment> fragments = getParentFragmentManager().getFragments();
        for (Fragment fragment : fragments) {
            if (fragment instanceof GlobalHeaderEditSupport) {
                return (GlobalHeaderEditSupport) fragment;
            }
        }
        Activity activity = getActivity();
        if (activity == null) {
            Log.e(GlobalHeaderEditDialog.class.getName(), "getGlobalHeaderEditSupport, activity is null");
            return null;
        }
        if (!(activity instanceof GlobalHeaderEditSupport)) {
            Log.e(GlobalHeaderEditDialog.class.getName(), "getGlobalHeaderEditSupport, activity is not an instance of " + GlobalHeaderEditSupport.class.getSimpleName());
            return null;
        }
        return (GlobalHeaderEditSupport) activity;
    }
}
