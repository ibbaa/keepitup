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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.model.AccessTypeData;
import net.ibbaa.keepitup.model.SNMPAuthAlgorithm;
import net.ibbaa.keepitup.model.SNMPPrivAlgorithm;
import net.ibbaa.keepitup.ui.ContextOptionsSupportManager;
import net.ibbaa.keepitup.ui.clipboard.IClipboardManager;
import net.ibbaa.keepitup.ui.clipboard.SystemClipboardManager;
import net.ibbaa.keepitup.ui.mapping.EnumMapping;
import net.ibbaa.keepitup.ui.support.ContextOptionsSupport;
import net.ibbaa.keepitup.ui.support.SNMPAuthSupport;
import net.ibbaa.keepitup.ui.validation.SNMPAuthPassphraseFieldValidator;
import net.ibbaa.keepitup.ui.validation.SNMPPrivPassphraseFieldValidator;
import net.ibbaa.keepitup.ui.validation.SNMPUserNameFieldValidator;
import net.ibbaa.keepitup.ui.validation.TextColorValidatingWatcher;
import net.ibbaa.keepitup.ui.validation.ValidationResult;
import net.ibbaa.keepitup.util.BundleUtil;
import net.ibbaa.keepitup.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"unused"})
public class SNMPAuthDialog extends DialogFragmentBase implements ContextOptionsSupport {

    private View dialogView;
    private EditText snmpUserNameEditText;
    private EditText snmpAuthPassphraseEditText;
    private EditText snmpPrivPassphraseEditText;
    private Spinner snmpAuthAlgorithmSpinner;
    private Spinner snmpPrivAlgorithmSpinner;
    private TextColorValidatingWatcher snmpUserNameEditTextWatcher;
    private PasswordToggleTouchListener snmpAuthPassphraseToggleTouchListener;
    private PasswordToggleTouchListener snmpPrivPassphraseToggleTouchListener;
    private String initialSNMPAuthPassphrase;
    private String initialSNMPPrivPassphrase;
    private boolean snmpAuthPassphraseToggleOpen;
    private boolean snmpPrivPassphraseToggleOpen;
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
        Log.d(SNMPAuthDialog.class.getName(), "onCreate");
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.DialogTheme);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(SNMPAuthDialog.class.getName(), "onCreateView");
        dialogView = inflater.inflate(R.layout.dialog_snmp_auth, container);
        initEdgeToEdgeInsets(dialogView);
        AccessTypeData accessTypeData = getAccessTypeData();
        prepareSNMPUserNameTextField(accessTypeData, savedInstanceState);
        prepareAuthPassphraseTextField(accessTypeData, savedInstanceState);
        prepareSNMPAuthAlgorithmSpinner(accessTypeData, savedInstanceState);
        preparePrivPassphraseTextField(accessTypeData, savedInstanceState);
        prepareSNMPPrivAlgorithmSpinner(accessTypeData, savedInstanceState);
        prepareAuthAlgorithmSpinnerListener();
        prepareOkCancelImageButtons();
        return dialogView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Log.d(SNMPAuthDialog.class.getName(), "onSaveInstanceState");
        super.onSaveInstanceState(outState);
        if (snmpAuthPassphraseToggleTouchListener != null) {
            outState.putBoolean(getAuthPassphraseVisibleKey(), snmpAuthPassphraseToggleTouchListener.isVisible());
        }
        outState.putBoolean(getAuthPassphraseToggleOpenKey(), snmpAuthPassphraseToggleOpen);
        if (initialSNMPAuthPassphrase != null) {
            outState.putString(getInitialAuthPassphraseKey(), initialSNMPAuthPassphrase);
        }
        if (snmpPrivPassphraseToggleTouchListener != null) {
            outState.putBoolean(getPrivPassphraseVisibleKey(), snmpPrivPassphraseToggleTouchListener.isVisible());
        }
        outState.putBoolean(getPrivPassphraseToggleOpenKey(), snmpPrivPassphraseToggleOpen);
        if (initialSNMPPrivPassphrase != null) {
            outState.putString(getInitialPrivPassphraseKey(), initialSNMPPrivPassphrase);
        }
        if (snmpAuthAlgorithmSpinner != null) {
            outState.putInt(getAuthAlgorithmPositionKey(), snmpAuthAlgorithmSpinner.getSelectedItemPosition());
        }
        if (snmpPrivAlgorithmSpinner != null) {
            outState.putInt(getPrivAlgorithmPositionKey(), snmpPrivAlgorithmSpinner.getSelectedItemPosition());
        }
    }

    private void prepareSNMPUserNameTextField(AccessTypeData accessTypeData, Bundle savedInstanceState) {
        Log.d(SNMPAuthDialog.class.getName(), "prepareSNMPUserNameTextField");
        snmpUserNameEditText = dialogView.findViewById(R.id.edittext_dialog_snmp_auth_snmp_user_name);
        snmpUserNameEditText.setOnLongClickListener(this::onSNMPUserNameEditTextLongClicked);
        if (savedInstanceState == null) {
            String userName = accessTypeData.getSnmpUserName();
            snmpUserNameEditText.setText(userName != null ? userName : "");
        }
        prepareSNMPUserNameEditTextListener();
    }

    private void prepareSNMPUserNameEditTextListener() {
        Log.d(SNMPAuthDialog.class.getName(), "prepareSNMPUserNameEditTextListener");
        if (snmpUserNameEditTextWatcher != null) {
            snmpUserNameEditText.removeTextChangedListener(snmpUserNameEditTextWatcher);
            snmpUserNameEditTextWatcher = null;
        }
        snmpUserNameEditTextWatcher = new TextColorValidatingWatcher(snmpUserNameEditText, this::validateSNMPUserName, getColor(R.color.textColor), getColor(R.color.textErrorColor));
        snmpUserNameEditText.addTextChangedListener(snmpUserNameEditTextWatcher);
    }

    private boolean validateSNMPUserName(EditText editText) {
        Log.d(SNMPAuthDialog.class.getName(), "validateSNMPUserName");
        String fieldName = getResources().getString(R.string.label_dialog_snmp_auth_snmp_user_name);
        ValidationResult result = new SNMPUserNameFieldValidator(fieldName, requireContext()).validate(StringUtil.notNull(snmpUserNameEditText.getText()));
        Log.d(SNMPAuthDialog.class.getName(), "SNMP username validation result: " + result);
        return result.isValidationSuccessful();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void prepareAuthPassphraseTextField(AccessTypeData accessTypeData, Bundle savedInstanceState) {
        Log.d(SNMPAuthDialog.class.getName(), "prepareAuthPassphraseTextField");
        TextView snmpAuthPassphraseLabel = dialogView.findViewById(R.id.textview_dialog_snmp_auth_snmp_auth_passphrase_label);
        snmpAuthPassphraseLabel.setTextColor(accessTypeData.isSnmpAuthPassphraseValid() ? getColor(R.color.textColor) : getColor(R.color.textErrorColor));
        snmpAuthPassphraseEditText = dialogView.findViewById(R.id.edittext_dialog_snmp_auth_snmp_auth_passphrase);
        snmpAuthPassphraseToggleTouchListener = new PasswordToggleTouchListener(snmpAuthPassphraseEditText);
        if (savedInstanceState == null) {
            String passphrase = accessTypeData.getSnmpAuthPassphrase();
            if (passphrase != null) {
                initialSNMPAuthPassphrase = passphrase;
                snmpAuthPassphraseToggleOpen = false;
                snmpAuthPassphraseEditText.setText(StringUtil.getSecretPlaceholder());
            } else {
                initialSNMPAuthPassphrase = null;
                snmpAuthPassphraseToggleOpen = true;
                snmpAuthPassphraseEditText.setText("");
            }
        } else {
            boolean wasVisible = savedInstanceState.getBoolean(getAuthPassphraseVisibleKey(), false);
            snmpAuthPassphraseToggleTouchListener.setVisible(wasVisible);
            snmpAuthPassphraseToggleOpen = savedInstanceState.getBoolean(getAuthPassphraseToggleOpenKey(), false);
            if (savedInstanceState.containsKey(getInitialAuthPassphraseKey())) {
                initialSNMPAuthPassphrase = savedInstanceState.getString(getInitialAuthPassphraseKey());
            }
            if (!snmpAuthPassphraseToggleOpen && initialSNMPAuthPassphrase != null) {
                snmpAuthPassphraseEditText.setText(StringUtil.getSecretPlaceholder());
            }
        }
        snmpAuthPassphraseToggleTouchListener.setEnabled(snmpAuthPassphraseToggleOpen);
        snmpAuthPassphraseEditText.setOnTouchListener(snmpAuthPassphraseToggleTouchListener);
        snmpAuthPassphraseEditText.setOnFocusChangeListener(this::onAuthPassphraseFieldFocusChanged);
    }

    private void onAuthPassphraseFieldFocusChanged(View view, boolean hasFocus) {
        Log.d(SNMPAuthDialog.class.getName(), "onAuthPassphraseFieldFocusChanged, hasFocus is " + hasFocus);
        if (!hasFocus) {
            return;
        }
        if (!snmpAuthPassphraseToggleOpen) {
            snmpAuthPassphraseEditText.setText("");
        }
        snmpAuthPassphraseToggleOpen = true;
        if (snmpAuthPassphraseToggleTouchListener != null) {
            snmpAuthPassphraseToggleTouchListener.setEnabled(true);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void preparePrivPassphraseTextField(AccessTypeData accessTypeData, Bundle savedInstanceState) {
        Log.d(SNMPAuthDialog.class.getName(), "preparePrivPassphraseTextField");
        TextView snmpPrivPassphraseLabel = dialogView.findViewById(R.id.textview_dialog_snmp_auth_snmp_priv_passphrase_label);
        snmpPrivPassphraseLabel.setTextColor(accessTypeData.isSnmpPrivPassphraseValid() ? getColor(R.color.textColor) : getColor(R.color.textErrorColor));
        snmpPrivPassphraseEditText = dialogView.findViewById(R.id.edittext_dialog_snmp_auth_snmp_priv_passphrase);
        snmpPrivPassphraseToggleTouchListener = new PasswordToggleTouchListener(snmpPrivPassphraseEditText);
        if (savedInstanceState == null) {
            String passphrase = accessTypeData.getSnmpPrivPassphrase();
            if (passphrase != null) {
                initialSNMPPrivPassphrase = passphrase;
                snmpPrivPassphraseToggleOpen = false;
                snmpPrivPassphraseEditText.setText(StringUtil.getSecretPlaceholder());
            } else {
                initialSNMPPrivPassphrase = null;
                snmpPrivPassphraseToggleOpen = true;
                snmpPrivPassphraseEditText.setText("");
            }
        } else {
            boolean wasVisible = savedInstanceState.getBoolean(getPrivPassphraseVisibleKey(), false);
            snmpPrivPassphraseToggleTouchListener.setVisible(wasVisible);
            snmpPrivPassphraseToggleOpen = savedInstanceState.getBoolean(getPrivPassphraseToggleOpenKey(), false);
            if (savedInstanceState.containsKey(getInitialPrivPassphraseKey())) {
                initialSNMPPrivPassphrase = savedInstanceState.getString(getInitialPrivPassphraseKey());
            }
            if (!snmpPrivPassphraseToggleOpen && initialSNMPPrivPassphrase != null) {
                snmpPrivPassphraseEditText.setText(StringUtil.getSecretPlaceholder());
            }
        }
        snmpPrivPassphraseToggleTouchListener.setEnabled(snmpPrivPassphraseToggleOpen);
        snmpPrivPassphraseEditText.setOnTouchListener(snmpPrivPassphraseToggleTouchListener);
        snmpPrivPassphraseEditText.setOnFocusChangeListener(this::onPrivPassphraseFieldFocusChanged);
    }

    private void onPrivPassphraseFieldFocusChanged(View view, boolean hasFocus) {
        Log.d(SNMPAuthDialog.class.getName(), "onPrivPassphraseFieldFocusChanged, hasFocus is " + hasFocus);
        if (!hasFocus) {
            return;
        }
        if (!snmpPrivPassphraseToggleOpen) {
            snmpPrivPassphraseEditText.setText("");
        }
        snmpPrivPassphraseToggleOpen = true;
        if (snmpPrivPassphraseToggleTouchListener != null) {
            snmpPrivPassphraseToggleTouchListener.setEnabled(true);
        }
    }

    private void prepareSNMPAuthAlgorithmSpinner(AccessTypeData accessTypeData, Bundle savedInstanceState) {
        Log.d(SNMPAuthDialog.class.getName(), "prepareSNMPAuthAlgorithmSpinner");
        snmpAuthAlgorithmSpinner = dialogView.findViewById(R.id.spinner_dialog_snmp_auth_snmp_auth_algorithm);
        EnumMapping mapping = new EnumMapping(requireContext());
        SNMPAuthAlgorithm[] values = SNMPAuthAlgorithm.values();
        List<String> names = new ArrayList<>();
        for (SNMPAuthAlgorithm value : values) {
            names.add(mapping.getSNMPAuthAlgorithmName(value));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, names);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        snmpAuthAlgorithmSpinner.setAdapter(adapter);
        if (savedInstanceState != null && savedInstanceState.containsKey(getAuthAlgorithmPositionKey())) {
            snmpAuthAlgorithmSpinner.setSelection(savedInstanceState.getInt(getAuthAlgorithmPositionKey()));
        } else {
            SNMPAuthAlgorithm algorithm = accessTypeData.getSnmpAuthAlgorithm();
            if (algorithm != null) {
                snmpAuthAlgorithmSpinner.setSelection(algorithm.ordinal());
            }
        }
    }

    private void prepareSNMPPrivAlgorithmSpinner(AccessTypeData accessTypeData, Bundle savedInstanceState) {
        Log.d(SNMPAuthDialog.class.getName(), "prepareSNMPPrivAlgorithmSpinner");
        snmpPrivAlgorithmSpinner = dialogView.findViewById(R.id.spinner_dialog_snmp_auth_snmp_priv_algorithm);
        EnumMapping mapping = new EnumMapping(requireContext());
        SNMPPrivAlgorithm[] values = SNMPPrivAlgorithm.values();
        List<String> names = new ArrayList<>();
        for (SNMPPrivAlgorithm value : values) {
            names.add(mapping.getSNMPPrivAlgorithmName(value));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, names);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        snmpPrivAlgorithmSpinner.setAdapter(adapter);
        if (savedInstanceState != null && savedInstanceState.containsKey(getPrivAlgorithmPositionKey())) {
            snmpPrivAlgorithmSpinner.setSelection(savedInstanceState.getInt(getPrivAlgorithmPositionKey()));
        } else {
            SNMPPrivAlgorithm algorithm = accessTypeData.getSnmpPrivAlgorithm();
            if (algorithm != null) {
                snmpPrivAlgorithmSpinner.setSelection(algorithm.ordinal());
            }
        }
    }

    private void prepareAuthAlgorithmSpinnerListener() {
        Log.d(SNMPAuthDialog.class.getName(), "prepareAuthAlgorithmSpinnerListener");
        snmpAuthAlgorithmSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updatePrivFieldsEnabledState();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        updatePrivFieldsEnabledState();
    }

    private void updatePrivFieldsEnabledState() {
        Log.d(SNMPAuthDialog.class.getName(), "updatePrivFieldsEnabledState");
        boolean enabled = getSNMPAuthAlgorithm() != SNMPAuthAlgorithm.NONE;
        snmpPrivPassphraseEditText.setEnabled(enabled);
        snmpPrivAlgorithmSpinner.setEnabled(enabled);
    }

    private void prepareOkCancelImageButtons() {
        Log.d(SNMPAuthDialog.class.getName(), "prepareOkCancelImageButtons");
        ImageView okImage = dialogView.findViewById(R.id.imageview_dialog_snmp_auth_ok);
        ImageView cancelImage = dialogView.findViewById(R.id.imageview_dialog_snmp_auth_cancel);
        okImage.setOnClickListener(this::onOkClicked);
        cancelImage.setOnClickListener(this::onCancelClicked);
    }

    public String getAccessTypeDataKey() {
        return SNMPAuthDialog.class.getName() + ".AccessTypeData";
    }

    private String getAuthPassphraseVisibleKey() {
        return SNMPAuthDialog.class.getSimpleName() + ".AuthPassphraseVisible";
    }

    private String getAuthPassphraseToggleOpenKey() {
        return SNMPAuthDialog.class.getSimpleName() + ".AuthPassphraseToggleOpen";
    }

    private String getInitialAuthPassphraseKey() {
        return SNMPAuthDialog.class.getSimpleName() + ".InitialAuthPassphrase";
    }

    private String getPrivPassphraseVisibleKey() {
        return SNMPAuthDialog.class.getSimpleName() + ".PrivPassphraseVisible";
    }

    private String getPrivPassphraseToggleOpenKey() {
        return SNMPAuthDialog.class.getSimpleName() + ".PrivPassphraseToggleOpen";
    }

    private String getInitialPrivPassphraseKey() {
        return SNMPAuthDialog.class.getSimpleName() + ".InitialPrivPassphrase";
    }

    private String getAuthAlgorithmPositionKey() {
        return SNMPAuthDialog.class.getSimpleName() + ".AuthAlgorithmPosition";
    }

    private String getPrivAlgorithmPositionKey() {
        return SNMPAuthDialog.class.getSimpleName() + ".PrivAlgorithmPosition";
    }

    public String getSNMPUserName() {
        return StringUtil.notNull(snmpUserNameEditText.getText());
    }

    public String getSNMPAuthPassphrase() {
        return snmpAuthPassphraseToggleOpen ? StringUtil.notNull(snmpAuthPassphraseEditText.getText()) : StringUtil.notNull(initialSNMPAuthPassphrase);
    }

    public SNMPAuthAlgorithm getSNMPAuthAlgorithm() {
        return SNMPAuthAlgorithm.values()[snmpAuthAlgorithmSpinner.getSelectedItemPosition()];
    }

    public String getSNMPPrivPassphrase() {
        return snmpPrivPassphraseToggleOpen ? StringUtil.notNull(snmpPrivPassphraseEditText.getText()) : StringUtil.notNull(initialSNMPPrivPassphrase);
    }

    public SNMPPrivAlgorithm getSNMPPrivAlgorithm() {
        return SNMPPrivAlgorithm.values()[snmpPrivAlgorithmSpinner.getSelectedItemPosition()];
    }

    private AccessTypeData getAccessTypeData() {
        Log.d(SNMPAuthDialog.class.getName(), "getAccessTypeData");
        Bundle accessTypeDataBundle = BundleUtil.bundleFromBundle(getAccessTypeDataKey(), requireArguments());
        return accessTypeDataBundle != null ? new AccessTypeData(accessTypeDataBundle) : new AccessTypeData();
    }

    private void onOkClicked(View view) {
        Log.d(SNMPAuthDialog.class.getName(), "onOkClicked");
        List<ValidationResult> validationResultList = new ArrayList<>();
        String userNameFieldName = getResources().getString(R.string.label_dialog_snmp_auth_snmp_user_name);
        ValidationResult userNameResult = new SNMPUserNameFieldValidator(userNameFieldName, requireContext()).validate(getSNMPUserName());
        Log.d(SNMPAuthDialog.class.getName(), "SNMP username validation result: " + userNameResult);
        if (!userNameResult.isValidationSuccessful()) {
            validationResultList.add(userNameResult);
        }
        if (snmpAuthPassphraseToggleOpen) {
            String authPassphraseFieldName = getResources().getString(R.string.label_dialog_snmp_auth_snmp_auth_passphrase);
            ValidationResult authPassphraseResult = new SNMPAuthPassphraseFieldValidator(authPassphraseFieldName, requireContext()).validate(StringUtil.notNull(snmpAuthPassphraseEditText.getText()));
            Log.d(SNMPAuthDialog.class.getName(), "SNMP auth passphrase validation result: " + authPassphraseResult);
            if (!authPassphraseResult.isValidationSuccessful()) {
                validationResultList.add(authPassphraseResult);
            }
        }
        if (snmpPrivPassphraseToggleOpen) {
            String privPassphraseFieldName = getResources().getString(R.string.label_dialog_snmp_auth_snmp_priv_passphrase);
            ValidationResult privPassphraseResult = new SNMPPrivPassphraseFieldValidator(privPassphraseFieldName, requireContext()).validate(StringUtil.notNull(snmpPrivPassphraseEditText.getText()));
            Log.d(SNMPAuthDialog.class.getName(), "SNMP priv passphrase validation result: " + privPassphraseResult);
            if (!privPassphraseResult.isValidationSuccessful()) {
                validationResultList.add(privPassphraseResult);
            }
        }
        if (!validationResultList.isEmpty()) {
            Log.d(SNMPAuthDialog.class.getName(), "Validation failed");
            showValidationErrorDialog(validationResultList);
            return;
        }
        Log.d(SNMPAuthDialog.class.getName(), "Validation was successful");
        SNMPAuthSupport snmpAuthSupport = getSNMPAuthSupport();
        if (snmpAuthSupport != null) {
            snmpAuthSupport.onSNMPAuthDialogOkClicked(this);
        } else {
            Log.e(SNMPAuthDialog.class.getName(), "snmpAuthSupport is null");
            dismiss();
        }
    }

    private void onCancelClicked(View view) {
        Log.d(SNMPAuthDialog.class.getName(), "onCancelClicked");
        SNMPAuthSupport snmpAuthSupport = getSNMPAuthSupport();
        if (snmpAuthSupport != null) {
            snmpAuthSupport.onSNMPAuthDialogCancelClicked(this);
        } else {
            Log.e(SNMPAuthDialog.class.getName(), "snmpAuthSupport is null");
            dismiss();
        }
    }

    private void showValidationErrorDialog(List<ValidationResult> validationResult) {
        Log.d(SNMPAuthDialog.class.getName(), "showValidationErrorDialog");
        ValidatorErrorDialog errorDialog = new ValidatorErrorDialog();
        errorDialog.setArguments(BundleUtil.validationResultListToBundle(errorDialog.getValidationResultBaseKey(), validationResult));
        errorDialog.show(getParentFragmentManager(), ValidatorErrorDialog.class.getName());
    }

    @SuppressWarnings("SameReturnValue")
    private boolean onSNMPUserNameEditTextLongClicked(View view) {
        Log.d(SNMPAuthDialog.class.getName(), "onSNMPUserNameEditTextLongClicked");
        showContextOptionsDialog((EditText) view);
        return true;
    }

    private void showContextOptionsDialog(EditText editText) {
        Log.d(SNMPAuthDialog.class.getName(), "showContextOptionsDialog");
        new ContextOptionsSupportManager(getChildFragmentManager(), getClipboardManager()).showContextOptionsDialog(editText);
    }

    @Override
    public void onContextOptionsDialogClicked(ContextOptionsDialog contextOptionsDialog, int sourceResourceId, ContextOption option) {
        Log.d(SNMPAuthDialog.class.getName(), "onContextOptionsDialogClicked, sourceResourceId is " + sourceResourceId + ", option is " + option);
        if (snmpUserNameEditText.getId() == sourceResourceId) {
            ContextOptionsSupportManager contextOptionsSupportManager = new ContextOptionsSupportManager(getParentFragmentManager(), getClipboardManager());
            contextOptionsSupportManager.handleContextOption(snmpUserNameEditText, option);
            snmpUserNameEditText.setSelection(snmpUserNameEditText.getText().length());
        } else {
            Log.e(SNMPAuthDialog.class.getName(), "Source field is undefined.");
        }
        contextOptionsDialog.dismiss();
    }

    private int getColor(int colorId) {
        return ContextCompat.getColor(requireContext(), colorId);
    }

    private SNMPAuthSupport getSNMPAuthSupport() {
        Log.d(SNMPAuthDialog.class.getName(), "getSNMPAuthSupport");
        List<Fragment> fragments = getParentFragmentManager().getFragments();
        for (Fragment fragment : fragments) {
            if (fragment instanceof SNMPAuthSupport) {
                return (SNMPAuthSupport) fragment;
            }
        }
        Activity activity = getActivity();
        if (activity == null) {
            Log.e(SNMPAuthDialog.class.getName(), "getSNMPAuthSupport, activity is null");
            return null;
        }
        if (!(activity instanceof SNMPAuthSupport)) {
            Log.e(SNMPAuthDialog.class.getName(), "getSNMPAuthSupport, activity is not an instance of " + SNMPAuthSupport.class.getSimpleName());
            return null;
        }
        return (SNMPAuthSupport) activity;
    }
}
