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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.google.android.material.switchmaterial.SwitchMaterial;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.ui.ContextOptionsSupportManager;
import net.ibbaa.keepitup.ui.clipboard.IClipboardManager;
import net.ibbaa.keepitup.ui.clipboard.SystemClipboardManager;
import net.ibbaa.keepitup.ui.support.CertificateSettingsSupport;
import net.ibbaa.keepitup.ui.support.ContextOptionsSupport;
import net.ibbaa.keepitup.ui.validation.FailureOnCertificateExpiryDaysFieldValidator;
import net.ibbaa.keepitup.ui.validation.TextColorValidatingWatcher;
import net.ibbaa.keepitup.ui.validation.ValidationResult;
import net.ibbaa.keepitup.util.BundleUtil;
import net.ibbaa.keepitup.util.NumberUtil;
import net.ibbaa.keepitup.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"unused"})
public class CertificateSettingsDialog extends DialogFragmentBase implements ContextOptionsSupport {

    private View dialogView;
    private SwitchMaterial ignoreSSLErrorSwitch;
    private TextView ignoreSSLErrorOnOffText;
    private SwitchMaterial failureOnCertificateExpirySwitch;
    private TextView failureOnCertificateExpiryOnOffText;
    private View failureOnCertificateExpiryDaysContainer;
    private EditText failureOnCertificateExpiryDaysEditText;
    private TextColorValidatingWatcher failureOnCertificateExpiryDaysEditTextWatcher;
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
        Log.d(CertificateSettingsDialog.class.getName(), "onCreate");
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.DialogTheme);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(CertificateSettingsDialog.class.getName(), "onCreateView");
        dialogView = inflater.inflate(R.layout.dialog_certificate_settings, container);
        initEdgeToEdgeInsets(dialogView);
        prepareIgnoreSSLErrorSwitch(savedInstanceState);
        prepareFailureOnCertificateExpirySwitch(savedInstanceState);
        prepareFailureOnCertificateExpiryDaysTextField(savedInstanceState);
        updateDependentFieldsEnabledState();
        updateFailureOnCertificateExpiryDaysFieldVisibility();
        prepareOkCancelImageButtons();
        return dialogView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Log.d(CertificateSettingsDialog.class.getName(), "onSaveInstanceState");
        super.onSaveInstanceState(outState);
        if (ignoreSSLErrorSwitch != null) {
            outState.putBoolean(getIgnoreSSLErrorCheckedKey(), ignoreSSLErrorSwitch.isChecked());
        }
        if (failureOnCertificateExpirySwitch != null) {
            outState.putBoolean(getFailureOnCertificateExpiryCheckedKey(), failureOnCertificateExpirySwitch.isChecked());
        }
    }

    private void prepareIgnoreSSLErrorSwitch(Bundle savedInstanceState) {
        Log.d(CertificateSettingsDialog.class.getName(), "prepareIgnoreSSLErrorSwitch");
        ignoreSSLErrorSwitch = dialogView.findViewById(R.id.switch_dialog_certificate_settings_ignore_ssl_error);
        ignoreSSLErrorOnOffText = dialogView.findViewById(R.id.textview_dialog_certificate_settings_ignore_ssl_error_on_off);
        ignoreSSLErrorSwitch.setOnCheckedChangeListener(null);
        boolean ignoreSSLError = savedInstanceState != null && savedInstanceState.containsKey(getIgnoreSSLErrorCheckedKey()) ? savedInstanceState.getBoolean(getIgnoreSSLErrorCheckedKey()) : getInitialIgnoreSSLError();
        ignoreSSLErrorSwitch.setChecked(ignoreSSLError);
        ignoreSSLErrorSwitch.setOnCheckedChangeListener(this::onIgnoreSSLErrorCheckedChanged);
        prepareIgnoreSSLErrorOnOffText();
    }

    private void onIgnoreSSLErrorCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.d(CertificateSettingsDialog.class.getName(), "onIgnoreSSLErrorCheckedChanged, new value is " + isChecked);
        prepareIgnoreSSLErrorOnOffText();
        updateDependentFieldsEnabledState();
    }

    private void prepareIgnoreSSLErrorOnOffText() {
        ignoreSSLErrorOnOffText.setText(ignoreSSLErrorSwitch.isChecked() ? getResources().getString(R.string.string_yes) : getResources().getString(R.string.string_no));
    }

    private void prepareFailureOnCertificateExpirySwitch(Bundle savedInstanceState) {
        Log.d(CertificateSettingsDialog.class.getName(), "prepareFailureOnCertificateExpirySwitch");
        failureOnCertificateExpirySwitch = dialogView.findViewById(R.id.switch_dialog_certificate_settings_failure_on_certificate_expiry);
        failureOnCertificateExpiryOnOffText = dialogView.findViewById(R.id.textview_dialog_certificate_settings_failure_on_certificate_expiry_on_off);
        failureOnCertificateExpirySwitch.setOnCheckedChangeListener(null);
        boolean failureOnCertificateExpiry = savedInstanceState != null && savedInstanceState.containsKey(getFailureOnCertificateExpiryCheckedKey()) ? savedInstanceState.getBoolean(getFailureOnCertificateExpiryCheckedKey()) : getInitialFailureOnCertificateExpiry();
        failureOnCertificateExpirySwitch.setChecked(failureOnCertificateExpiry);
        failureOnCertificateExpirySwitch.setOnCheckedChangeListener(this::onFailureOnCertificateExpiryCheckedChanged);
        prepareFailureOnCertificateExpiryOnOffText();
    }

    private void onFailureOnCertificateExpiryCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.d(CertificateSettingsDialog.class.getName(), "onFailureOnCertificateExpiryCheckedChanged, new value is " + isChecked);
        prepareFailureOnCertificateExpiryOnOffText();
        updateFailureOnCertificateExpiryDaysFieldVisibility();
    }

    private void prepareFailureOnCertificateExpiryOnOffText() {
        failureOnCertificateExpiryOnOffText.setText(failureOnCertificateExpirySwitch.isChecked() ? getResources().getString(R.string.string_yes) : getResources().getString(R.string.string_no));
    }

    private void prepareFailureOnCertificateExpiryDaysTextField(Bundle savedInstanceState) {
        Log.d(CertificateSettingsDialog.class.getName(), "prepareFailureOnCertificateExpiryDaysTextField");
        failureOnCertificateExpiryDaysContainer = dialogView.findViewById(R.id.linearlayout_dialog_certificate_settings_expiry_days);
        failureOnCertificateExpiryDaysEditText = dialogView.findViewById(R.id.edittext_dialog_certificate_settings_expiry_days);
        failureOnCertificateExpiryDaysEditText.setOnLongClickListener(this::onEditTextLongClicked);
        if (savedInstanceState == null) {
            failureOnCertificateExpiryDaysEditText.setText(String.valueOf(getInitialFailureOnCertificateExpiryDays()));
        }
        prepareFailureOnCertificateExpiryDaysEditTextListener();
    }

    private void prepareFailureOnCertificateExpiryDaysEditTextListener() {
        Log.d(CertificateSettingsDialog.class.getName(), "prepareFailureOnCertificateExpiryDaysEditTextListener");
        if (failureOnCertificateExpiryDaysEditTextWatcher != null) {
            failureOnCertificateExpiryDaysEditText.removeTextChangedListener(failureOnCertificateExpiryDaysEditTextWatcher);
            failureOnCertificateExpiryDaysEditTextWatcher = null;
        }
        failureOnCertificateExpiryDaysEditTextWatcher = new TextColorValidatingWatcher(failureOnCertificateExpiryDaysEditText, this::validateFailureOnCertificateExpiryDays, getColor(R.color.textColor), getColor(R.color.textErrorColor));
        failureOnCertificateExpiryDaysEditText.addTextChangedListener(failureOnCertificateExpiryDaysEditTextWatcher);
    }

    private boolean validateFailureOnCertificateExpiryDays(EditText editText) {
        Log.d(CertificateSettingsDialog.class.getName(), "validateFailureOnCertificateExpiryDays");
        String fieldName = getResources().getString(R.string.label_dialog_certificate_settings_expiry_days);
        ValidationResult result = new FailureOnCertificateExpiryDaysFieldValidator(fieldName, requireContext()).validate(StringUtil.notNull(failureOnCertificateExpiryDaysEditText.getText()));
        Log.d(CertificateSettingsDialog.class.getName(), "Expiry days validation result: " + result);
        return result.isValidationSuccessful();
    }

    private void updateDependentFieldsEnabledState() {
        Log.d(CertificateSettingsDialog.class.getName(), "updateDependentFieldsEnabledState");
        if (!isFieldDependenciesEnabled()) {
            return;
        }
        boolean enabled = !ignoreSSLErrorSwitch.isChecked();
        failureOnCertificateExpirySwitch.setEnabled(enabled);
        failureOnCertificateExpiryDaysEditText.setEnabled(enabled);
    }

    private void updateFailureOnCertificateExpiryDaysFieldVisibility() {
        Log.d(CertificateSettingsDialog.class.getName(), "updateFailureOnCertificateExpiryDaysFieldVisibility");
        if (!isFieldDependenciesEnabled()) {
            return;
        }
        failureOnCertificateExpiryDaysContainer.setVisibility(failureOnCertificateExpirySwitch.isChecked() ? View.VISIBLE : View.GONE);
    }

    private void prepareOkCancelImageButtons() {
        Log.d(CertificateSettingsDialog.class.getName(), "prepareOkCancelImageButtons");
        ImageView okImage = dialogView.findViewById(R.id.imageview_dialog_certificate_settings_ok);
        ImageView cancelImage = dialogView.findViewById(R.id.imageview_dialog_certificate_settings_cancel);
        okImage.setOnClickListener(this::onOkClicked);
        cancelImage.setOnClickListener(this::onCancelClicked);
    }

    public String getIgnoreSSLErrorKey() {
        return CertificateSettingsDialog.class.getName() + ".IgnoreSSLError";
    }

    public String getFailureOnCertificateExpiryKey() {
        return CertificateSettingsDialog.class.getName() + ".FailureOnCertificateExpiry";
    }

    public String getFailureOnCertificateExpiryDaysKey() {
        return CertificateSettingsDialog.class.getName() + ".FailureOnCertificateExpiryDays";
    }

    public String getFieldDependenciesEnabledKey() {
        return CertificateSettingsDialog.class.getName() + ".FieldDependenciesEnabled";
    }

    private String getIgnoreSSLErrorCheckedKey() {
        return CertificateSettingsDialog.class.getName() + ".IgnoreSSLErrorChecked";
    }

    private String getFailureOnCertificateExpiryCheckedKey() {
        return CertificateSettingsDialog.class.getName() + ".FailureOnCertificateExpiryChecked";
    }

    private boolean getInitialIgnoreSSLError() {
        return BundleUtil.booleanFromBundle(getIgnoreSSLErrorKey(), requireArguments());
    }

    private boolean getInitialFailureOnCertificateExpiry() {
        return BundleUtil.booleanFromBundle(getFailureOnCertificateExpiryKey(), requireArguments());
    }

    private int getInitialFailureOnCertificateExpiryDays() {
        Bundle arguments = requireArguments();
        if (arguments.containsKey(getFailureOnCertificateExpiryDaysKey())) {
            return arguments.getInt(getFailureOnCertificateExpiryDaysKey());
        }
        return getResources().getInteger(R.integer.failure_on_certificate_expiry_days_default);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean isFieldDependenciesEnabled() {
        return BundleUtil.booleanFromBundle(getFieldDependenciesEnabledKey(), requireArguments());
    }

    public boolean isIgnoreSSLError() {
        return ignoreSSLErrorSwitch.isChecked();
    }

    public boolean isFailureOnCertificateExpiry() {
        return failureOnCertificateExpirySwitch.isChecked();
    }

    public int getFailureOnCertificateExpiryDays() {
        String daysText = StringUtil.notNull(failureOnCertificateExpiryDaysEditText.getText());
        int defaultDays = getResources().getInteger(R.integer.failure_on_certificate_expiry_days_default);
        return NumberUtil.getIntValue(daysText, defaultDays);
    }

    private void onOkClicked(View view) {
        Log.d(CertificateSettingsDialog.class.getName(), "onOkClicked");
        List<ValidationResult> validationResultList = new ArrayList<>();
        boolean expiryDaysFieldRelevant = !isFieldDependenciesEnabled() || (failureOnCertificateExpirySwitch.isChecked() && !ignoreSSLErrorSwitch.isChecked());
        if (expiryDaysFieldRelevant) {
            String fieldName = getResources().getString(R.string.label_dialog_certificate_settings_expiry_days);
            ValidationResult daysResult = new FailureOnCertificateExpiryDaysFieldValidator(fieldName, requireContext()).validate(StringUtil.notNull(failureOnCertificateExpiryDaysEditText.getText()));
            Log.d(CertificateSettingsDialog.class.getName(), "Expiry days validation result: " + daysResult);
            if (!daysResult.isValidationSuccessful()) {
                validationResultList.add(daysResult);
            }
        }
        if (!validationResultList.isEmpty()) {
            Log.d(CertificateSettingsDialog.class.getName(), "Validation failed");
            showValidationErrorDialog(validationResultList);
            return;
        }
        Log.d(CertificateSettingsDialog.class.getName(), "Validation was successful");
        CertificateSettingsSupport certificateSettingsSupport = getCertificateSettingsSupport();
        if (certificateSettingsSupport != null) {
            certificateSettingsSupport.onCertificateSettingsDialogOkClicked(this);
        } else {
            Log.e(CertificateSettingsDialog.class.getName(), "certificateSettingsSupport is null");
            dismiss();
        }
    }

    private void showValidationErrorDialog(List<ValidationResult> validationResult) {
        Log.d(CertificateSettingsDialog.class.getName(), "showValidationErrorDialog");
        ValidatorErrorDialog errorDialog = new ValidatorErrorDialog();
        errorDialog.setArguments(BundleUtil.validationResultListToBundle(errorDialog.getValidationResultBaseKey(), validationResult));
        errorDialog.show(getParentFragmentManager(), ValidatorErrorDialog.class.getName());
    }

    private void onCancelClicked(View view) {
        Log.d(CertificateSettingsDialog.class.getName(), "onCancelClicked");
        CertificateSettingsSupport certificateSettingsSupport = getCertificateSettingsSupport();
        if (certificateSettingsSupport != null) {
            certificateSettingsSupport.onCertificateSettingsDialogCancelClicked(this);
        } else {
            Log.e(CertificateSettingsDialog.class.getName(), "certificateSettingsSupport is null");
            dismiss();
        }
    }

    @SuppressWarnings("SameReturnValue")
    private boolean onEditTextLongClicked(View view) {
        Log.d(CertificateSettingsDialog.class.getName(), "onEditTextLongClicked");
        showContextOptionsDialog((EditText) view);
        return true;
    }

    private void showContextOptionsDialog(EditText editText) {
        Log.d(CertificateSettingsDialog.class.getName(), "showContextOptionsDialog");
        new ContextOptionsSupportManager(getChildFragmentManager(), getClipboardManager()).showContextOptionsDialog(editText);
    }

    @Override
    public void onContextOptionsDialogClicked(ContextOptionsDialog contextOptionsDialog, int sourceResourceId, ContextOption option) {
        Log.d(CertificateSettingsDialog.class.getName(), "onContextOptionsDialogClicked, sourceResourceId is " + sourceResourceId + ", option is " + option);
        if (failureOnCertificateExpiryDaysEditText.getId() == sourceResourceId) {
            ContextOptionsSupportManager contextOptionsSupportManager = new ContextOptionsSupportManager(getParentFragmentManager(), getClipboardManager());
            contextOptionsSupportManager.handleContextOption(failureOnCertificateExpiryDaysEditText, option);
            failureOnCertificateExpiryDaysEditText.setSelection(failureOnCertificateExpiryDaysEditText.getText().length());
        } else {
            Log.e(CertificateSettingsDialog.class.getName(), "Source field is undefined.");
        }
        contextOptionsDialog.dismiss();
    }

    private int getColor(int colorId) {
        return ContextCompat.getColor(requireContext(), colorId);
    }

    private CertificateSettingsSupport getCertificateSettingsSupport() {
        Log.d(CertificateSettingsDialog.class.getName(), "getCertificateSettingsSupport");
        List<Fragment> fragments = getParentFragmentManager().getFragments();
        for (Fragment fragment : fragments) {
            if (fragment instanceof CertificateSettingsSupport) {
                return (CertificateSettingsSupport) fragment;
            }
        }
        Activity activity = getActivity();
        if (activity == null) {
            Log.e(CertificateSettingsDialog.class.getName(), "getCertificateSettingsSupport, activity is null");
            return null;
        }
        if (!(activity instanceof CertificateSettingsSupport)) {
            Log.e(CertificateSettingsDialog.class.getName(), "getCertificateSettingsSupport, activity is not an instance of " + CertificateSettingsSupport.class.getSimpleName());
            return null;
        }
        return (CertificateSettingsSupport) activity;
    }
}
