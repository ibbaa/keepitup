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
import net.ibbaa.keepitup.model.Resolve;
import net.ibbaa.keepitup.ui.ContextOptionsSupportManager;
import net.ibbaa.keepitup.ui.clipboard.IClipboardManager;
import net.ibbaa.keepitup.ui.clipboard.SystemClipboardManager;
import net.ibbaa.keepitup.ui.support.ContextOptionsSupport;
import net.ibbaa.keepitup.ui.support.ResolveEditSupport;
import net.ibbaa.keepitup.ui.validation.ResolveValidator;
import net.ibbaa.keepitup.ui.validation.StandardResolveValidator;
import net.ibbaa.keepitup.ui.validation.TextColorValidatingWatcher;
import net.ibbaa.keepitup.ui.validation.ValidationResult;
import net.ibbaa.keepitup.util.BundleUtil;
import net.ibbaa.keepitup.util.StringUtil;
import net.ibbaa.keepitup.util.UIUtil;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"unused", "SameReturnValue"})
public class ResolveEditDialog extends DialogFragmentBase implements ContextOptionsSupport {

    private View dialogView;
    private EditText matchHostEditText;
    private TextColorValidatingWatcher matchHostEditTextWatcher;
    private EditText matchPortEditText;
    private TextColorValidatingWatcher matchPortEditTextWatcher;
    private EditText connectToHostEditText;
    private TextColorValidatingWatcher connectToHostEditTextWatcher;
    private EditText connectToPortEditText;
    private TextColorValidatingWatcher connectToPortEditTextWatcher;

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
        Log.d(ResolveEditDialog.class.getName(), "onCreate");
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.DialogTheme);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(ResolveEditDialog.class.getName(), "onCreateView");
        dialogView = inflater.inflate(R.layout.dialog_resolve_edit, container);
        initEdgeToEdgeInsets(dialogView);
        Bundle resolveBundle = BundleUtil.bundleFromBundle(getResolveKey(), requireArguments());
        Resolve resolve = resolveBundle != null ? new Resolve(resolveBundle) : new Resolve();
        prepareMatchHostTextField(resolve);
        prepareMatchPortTextField(resolve);
        prepareConnectToHostTextField(resolve);
        prepareConnectToPortTextField(resolve);
        prepareOkCancelImageButtons();
        return dialogView;
    }

    private void prepareMatchHostTextField(Resolve resolve) {
        Log.d(ResolveEditDialog.class.getName(), "prepareMatchHostTextField");
        matchHostEditText = dialogView.findViewById(R.id.edittext_dialog_resolve_edit_match_host);
        matchHostEditText.setOnLongClickListener(this::onEditTextLongClicked);
        matchHostEditText.setOnFocusChangeListener(new PlaceholderFocusChangeListener(matchHostEditText, getResources().getString(R.string.string_not_set)));
        matchHostEditText.setText(UIUtil.getNotSetIfEmpty(requireContext(), resolve.getSourceAddress()));
        prepareMatchHostEditTextListener();
    }

    private void prepareMatchHostEditTextListener() {
        Log.d(ResolveEditDialog.class.getName(), "prepareMatchHostEditTextListener");
        if (matchHostEditTextWatcher != null) {
            matchHostEditText.removeTextChangedListener(matchHostEditTextWatcher);
            matchHostEditTextWatcher = null;
        }
        matchHostEditTextWatcher = new TextColorValidatingWatcher(matchHostEditText, this::validateMatchHost, getColor(R.color.textColor), getColor(R.color.textErrorColor));
        matchHostEditText.addTextChangedListener(matchHostEditTextWatcher);
    }

    private void prepareMatchPortTextField(Resolve resolve) {
        Log.d(ResolveEditDialog.class.getName(), "prepareMatchPortTextField");
        matchPortEditText = dialogView.findViewById(R.id.edittext_dialog_resolve_edit_match_port);
        matchPortEditText.setOnLongClickListener(this::onEditTextLongClicked);
        matchPortEditText.setOnFocusChangeListener(new PlaceholderFocusChangeListener(matchPortEditText, getResources().getString(R.string.string_not_set)));
        matchPortEditText.setText(UIUtil.getNotSetIfNegative(requireContext(), resolve.getSourcePort()));
        prepareMatchPortEditTextListener();
    }

    private void prepareMatchPortEditTextListener() {
        Log.d(ResolveEditDialog.class.getName(), "prepareMatchPortEditTextListener");
        if (matchPortEditTextWatcher != null) {
            matchPortEditText.removeTextChangedListener(matchPortEditTextWatcher);
            matchPortEditTextWatcher = null;
        }
        matchPortEditTextWatcher = new TextColorValidatingWatcher(matchPortEditText, this::validateMatchPort, getColor(R.color.textColor), getColor(R.color.textErrorColor));
        matchPortEditText.addTextChangedListener(matchPortEditTextWatcher);
    }

    private void prepareConnectToHostTextField(Resolve resolve) {
        Log.d(ResolveEditDialog.class.getName(), "prepareConnectToHostTextField");
        connectToHostEditText = dialogView.findViewById(R.id.edittext_dialog_resolve_edit_connect_to_host);
        connectToHostEditText.setOnLongClickListener(this::onEditTextLongClicked);
        connectToHostEditText.setOnFocusChangeListener(new PlaceholderFocusChangeListener(connectToHostEditText, getResources().getString(R.string.string_not_set)));
        connectToHostEditText.setText(UIUtil.getNotSetIfEmpty(requireContext(), resolve.getTargetAddress()));
        prepareConnectToHostEditTextListener();
    }

    private void prepareConnectToHostEditTextListener() {
        Log.d(ResolveEditDialog.class.getName(), "prepareConnectToHostEditTextListener");
        if (connectToHostEditTextWatcher != null) {
            connectToHostEditText.removeTextChangedListener(connectToHostEditTextWatcher);
            connectToHostEditTextWatcher = null;
        }
        connectToHostEditTextWatcher = new TextColorValidatingWatcher(connectToHostEditText, this::validateConnectToHost, getColor(R.color.textColor), getColor(R.color.textErrorColor));
        connectToHostEditText.addTextChangedListener(connectToHostEditTextWatcher);
    }

    private void prepareConnectToPortTextField(Resolve resolve) {
        Log.d(ResolveEditDialog.class.getName(), "prepareConnectToPortTextField");
        connectToPortEditText = dialogView.findViewById(R.id.edittext_dialog_resolve_edit_connect_to_port);
        connectToPortEditText.setOnLongClickListener(this::onEditTextLongClicked);
        connectToPortEditText.setOnFocusChangeListener(new PlaceholderFocusChangeListener(connectToPortEditText, getResources().getString(R.string.string_not_set)));
        connectToPortEditText.setText(UIUtil.getNotSetIfNegative(requireContext(), resolve.getTargetPort()));
        prepareConnectToPortEditTextListener();
    }

    private void prepareConnectToPortEditTextListener() {
        Log.d(ResolveEditDialog.class.getName(), "prepareConnectToPortEditTextListener");
        if (connectToPortEditTextWatcher != null) {
            connectToPortEditText.removeTextChangedListener(connectToPortEditTextWatcher);
            connectToPortEditTextWatcher = null;
        }
        connectToPortEditTextWatcher = new TextColorValidatingWatcher(connectToPortEditText, this::validateConnectToPort, getColor(R.color.textColor), getColor(R.color.textErrorColor));
        connectToPortEditText.addTextChangedListener(connectToPortEditTextWatcher);
    }

    private void prepareOkCancelImageButtons() {
        Log.d(ResolveEditDialog.class.getName(), "prepareOkCancelImageButtons");
        ImageView okImage = dialogView.findViewById(R.id.imageview_dialog_resolve_edit_ok);
        ImageView cancelImage = dialogView.findViewById(R.id.imageview_dialog_resolve_edit_cancel);
        okImage.setOnClickListener(this::onOkClicked);
        cancelImage.setOnClickListener(this::onCancelClicked);
    }

    public String getResolveKey() {
        return ResolveEditDialog.class.getName() + ".Resolve";
    }

    public String getPositionKey() {
        return ResolveEditDialog.class.getSimpleName() + ".Position";
    }

    private String getMatchHost() {
        return StringUtil.notNull(matchHostEditText.getText()).trim();
    }

    private String getMatchPort() {
        return StringUtil.notNull(matchPortEditText.getText());
    }

    private String getConnectToHost() {
        return StringUtil.notNull(connectToHostEditText.getText()).trim();
    }

    private String getConnectToPort() {
        return StringUtil.notNull(connectToPortEditText.getText());
    }

    public Resolve getResolve() {
        Bundle resolveBundle = BundleUtil.bundleFromBundle(getResolveKey(), requireArguments());
        Resolve resolve = resolveBundle != null ? new Resolve(resolveBundle) : new Resolve();
        resolve.setSourceAddress(UIUtil.getEmptyIfNotSet(requireContext(), getMatchHost()));
        resolve.setSourcePort(UIUtil.getNegativeIfNotSet(requireContext(), getMatchPort()));
        resolve.setTargetAddress(UIUtil.getEmptyIfNotSet(requireContext(), getConnectToHost()));
        resolve.setTargetPort(UIUtil.getNegativeIfNotSet(requireContext(), getConnectToPort()));
        Log.d(ResolveEditDialog.class.getName(), "getResolve, resolve object is " + resolve);
        return resolve;
    }

    private void onOkClicked(View view) {
        Log.d(ResolveEditDialog.class.getName(), "onOkClicked");
        int position = BundleUtil.integerFromBundle(getPositionKey(), requireArguments());
        List<ValidationResult> validationResult = validateInput();
        if (!hasErrors(validationResult)) {
            Log.d(ResolveEditDialog.class.getName(), "Validation was successful");
            ResolveEditSupport resolveEditSupport = getResolveEditSupport();
            if (resolveEditSupport != null) {
                resolveEditSupport.onResolveEditDialogOkClicked(this, position);
            } else {
                Log.e(ResolveEditDialog.class.getName(), "resolveEditSupport is null");
                dismiss();
            }
        } else {
            Log.d(ResolveEditDialog.class.getName(), "Validation failed");
            showValidationErrorDialog(validationResult);
        }
    }

    private void onCancelClicked(View view) {
        Log.d(ResolveEditDialog.class.getName(), "onCancelClicked");
        ResolveEditSupport resolveEditSupport = getResolveEditSupport();
        if (resolveEditSupport != null) {
            resolveEditSupport.onResolveEditDialogCancelClicked(this);
        } else {
            Log.e(ResolveEditDialog.class.getName(), "resolveEditSupport is null");
            dismiss();
        }
    }

    private boolean hasErrors(List<ValidationResult> validationResult) {
        return !validationResult.isEmpty();
    }

    private boolean validateMatchHost(EditText editText) {
        Log.d(ResolveEditDialog.class.getName(), "validateMatchHost");
        ResolveValidator validator = new StandardResolveValidator(requireContext());
        ValidationResult result = validator.validateSourceAddress(getMatchHost());
        Log.d(ResolveEditDialog.class.getName(), "match host validation result: " + result);
        return result.isValidationSuccessful();
    }

    private boolean validateMatchPort(EditText editText) {
        Log.d(ResolveEditDialog.class.getName(), "validateMatchPort");
        ResolveValidator validator = new StandardResolveValidator(requireContext());
        ValidationResult result = validator.validateSourcePort(getMatchPort());
        Log.d(ResolveEditDialog.class.getName(), "match port validation result: " + result);
        return result.isValidationSuccessful();
    }

    private boolean validateConnectToHost(EditText editText) {
        Log.d(ResolveEditDialog.class.getName(), "validateConnectToHost");
        ResolveValidator validator = new StandardResolveValidator(requireContext());
        ValidationResult result = validator.validateTargetAddress(getConnectToHost());
        Log.d(ResolveEditDialog.class.getName(), "connect-to host validation result: " + result);
        return result.isValidationSuccessful();
    }

    private boolean validateConnectToPort(EditText editText) {
        Log.d(ResolveEditDialog.class.getName(), "validateConnectToPort");
        ResolveValidator validator = new StandardResolveValidator(requireContext());
        ValidationResult result = validator.validateTargetPort(getConnectToPort());
        Log.d(ResolveEditDialog.class.getName(), "connect-to port validation result: " + result);
        return result.isValidationSuccessful();
    }

    private List<ValidationResult> validateInput() {
        Log.d(ResolveEditDialog.class.getName(), "validateInput");
        List<ValidationResult> validationResults = new ArrayList<>();
        ResolveValidator validator = new StandardResolveValidator(requireContext());
        ValidationResult matchHostResult = validator.validateSourceAddress(getMatchHost());
        ValidationResult matchPortResult = validator.validateSourcePort(getMatchPort());
        ValidationResult connectToHostResult = validator.validateTargetAddress(getConnectToHost());
        ValidationResult connectToPortResult = validator.validateTargetPort(getConnectToPort());
        if (!matchHostResult.isValidationSuccessful()) {
            validationResults.add(matchHostResult);
        }
        if (!matchPortResult.isValidationSuccessful()) {
            validationResults.add(matchPortResult);
        }
        if (!connectToHostResult.isValidationSuccessful()) {
            validationResults.add(connectToHostResult);
        }
        if (!connectToPortResult.isValidationSuccessful()) {
            validationResults.add(connectToPortResult);
        }
        return validationResults;
    }

    private void showValidationErrorDialog(List<ValidationResult> validationResult) {
        Log.d(ResolveEditDialog.class.getName(), "showValidationErrorDialog");
        ValidatorErrorDialog errorDialog = new ValidatorErrorDialog();
        errorDialog.setArguments(BundleUtil.validationResultListToBundle(errorDialog.getValidationResultBaseKey(), validationResult));
        errorDialog.show(getParentFragmentManager(), ValidatorErrorDialog.class.getName());
    }

    private boolean onEditTextLongClicked(View view) {
        Log.d(ResolveEditDialog.class.getName(), "onEditTextLongClicked");
        showContextOptionsDialog((EditText) view);
        return true;
    }

    private void showContextOptionsDialog(EditText editText) {
        Log.d(ResolveEditDialog.class.getName(), "showContextOptionsDialog");
        new ContextOptionsSupportManager(getParentFragmentManager(), getClipboardManager()).showContextOptionsDialog(editText);
    }

    @Override
    public void onContextOptionsDialogClicked(ContextOptionsDialog contextOptionsDialog, int sourceResourceId, ContextOption option) {
        Log.d(ResolveEditDialog.class.getName(), "onContextOptionsDialogClicked, sourceResourceId is " + sourceResourceId + ", option is " + option);
        ContextOptionsSupportManager contextOptionsSupportManager = new ContextOptionsSupportManager(getParentFragmentManager(), getClipboardManager());
        EditText editText = null;
        if (matchHostEditText.getId() == sourceResourceId) {
            Log.d(ResolveEditDialog.class.getName(), "Source field is the match host field.");
            editText = matchHostEditText;
        } else if (matchPortEditText.getId() == sourceResourceId) {
            Log.d(ResolveEditDialog.class.getName(), "Source field is the match port field.");
            editText = matchPortEditText;
        } else if (connectToHostEditText.getId() == sourceResourceId) {
            Log.d(ResolveEditDialog.class.getName(), "Source field is the connect-to host field.");
            editText = connectToHostEditText;
        } else if (connectToPortEditText.getId() == sourceResourceId) {
            Log.d(ResolveEditDialog.class.getName(), "Source field is the connect-to port field.");
            editText = connectToPortEditText;
        } else {
            Log.e(ResolveEditDialog.class.getName(), "Source field is undefined.");
        }
        if (editText != null) {
            contextOptionsSupportManager.handleContextOption(editText, option);
            editText.setSelection(editText.getText().length());
        }
        contextOptionsDialog.dismiss();
    }

    private int getColor(int colorId) {
        return ContextCompat.getColor(requireContext(), colorId);
    }

    private ResolveEditSupport getResolveEditSupport() {
        Log.d(ResolveEditDialog.class.getName(), "getResolveEditSupport");
        List<Fragment> fragments = getParentFragmentManager().getFragments();
        for (Fragment fragment : fragments) {
            if (fragment instanceof ResolveEditSupport) {
                return (ResolveEditSupport) fragment;
            }
        }
        Activity activity = getActivity();
        if (activity == null) {
            Log.e(ResolveEditDialog.class.getName(), "getResolveEditSupport, activity is null");
            return null;
        }
        if (!(activity instanceof ResolveEditSupport)) {
            Log.e(ResolveEditDialog.class.getName(), "getResolveEditSupport, activity is not an instance of " + ResolveEditSupport.class.getSimpleName());
            return null;
        }
        return (ResolveEditSupport) activity;
    }
}
