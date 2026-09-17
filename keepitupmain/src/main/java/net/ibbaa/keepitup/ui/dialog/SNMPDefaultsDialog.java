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
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.model.SNMPAuthAlgorithm;
import net.ibbaa.keepitup.model.SNMPPrivAlgorithm;
import net.ibbaa.keepitup.model.SNMPTransport;
import net.ibbaa.keepitup.model.SNMPVersion;
import net.ibbaa.keepitup.resources.PreferenceManager;
import net.ibbaa.keepitup.ui.ContextOptionsSupportManager;
import net.ibbaa.keepitup.ui.clipboard.IClipboardManager;
import net.ibbaa.keepitup.ui.clipboard.SystemClipboardManager;
import net.ibbaa.keepitup.ui.mapping.EnumMapping;
import net.ibbaa.keepitup.ui.support.ContextOptionsSupport;
import net.ibbaa.keepitup.ui.support.SNMPDefaultsSupport;
import net.ibbaa.keepitup.ui.validation.PortFieldValidator;
import net.ibbaa.keepitup.ui.validation.TextColorValidatingWatcher;
import net.ibbaa.keepitup.ui.validation.ValidationResult;
import net.ibbaa.keepitup.util.BundleUtil;
import net.ibbaa.keepitup.util.NumberUtil;
import net.ibbaa.keepitup.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings({"unused"})
public class SNMPDefaultsDialog extends DialogFragmentBase implements ContextOptionsSupport {

    private View dialogView;
    private RadioGroup snmpVersionGroup;
    private EditText snmpPortEditText;
    private RadioGroup snmpTransportGroup;
    private Spinner snmpAuthAlgorithmSpinner;
    private Spinner snmpPrivAlgorithmSpinner;
    private TextColorValidatingWatcher snmpPortEditTextWatcher;
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
        Log.d(SNMPDefaultsDialog.class.getName(), "onCreate");
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.DialogTheme);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(SNMPDefaultsDialog.class.getName(), "onCreateView");
        dialogView = inflater.inflate(R.layout.dialog_snmp_defaults, container);
        initEdgeToEdgeInsets(dialogView);
        PreferenceManager preferenceManager = new PreferenceManager(requireContext());
        prepareSNMPVersionRadioButtons(preferenceManager, savedInstanceState);
        prepareSNMPPortTextField(preferenceManager, savedInstanceState);
        prepareSNMPTransportRadioButtons(preferenceManager, savedInstanceState);
        prepareSNMPAuthAlgorithmSpinner(preferenceManager, savedInstanceState);
        prepareSNMPPrivAlgorithmSpinner(preferenceManager, savedInstanceState);
        prepareOkCancelImageButtons();
        return dialogView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Log.d(SNMPDefaultsDialog.class.getName(), "onSaveInstanceState");
        super.onSaveInstanceState(outState);
        if (snmpAuthAlgorithmSpinner != null) {
            outState.putInt(getSNMPAuthAlgorithmKey(), snmpAuthAlgorithmSpinner.getSelectedItemPosition());
        }
        if (snmpPrivAlgorithmSpinner != null) {
            outState.putInt(getSNMPPrivAlgorithmKey(), snmpPrivAlgorithmSpinner.getSelectedItemPosition());
        }
    }

    private void prepareSNMPVersionRadioButtons(PreferenceManager preferenceManager, Bundle savedInstanceState) {
        Log.d(SNMPDefaultsDialog.class.getName(), "prepareSNMPVersionRadioButtons");
        snmpVersionGroup = dialogView.findViewById(R.id.radiogroup_dialog_snmp_defaults_snmp_version);
        RadioButton v1RadioButton = dialogView.findViewById(R.id.radiobutton_dialog_snmp_defaults_snmp_version_v1);
        RadioButton v2cRadioButton = dialogView.findViewById(R.id.radiobutton_dialog_snmp_defaults_snmp_version_v2c);
        RadioButton v3RadioButton = dialogView.findViewById(R.id.radiobutton_dialog_snmp_defaults_snmp_version_v3);
        int textColor = getColor(R.color.textColor);
        ColorStateList tintList = ColorStateList.valueOf(ResourcesCompat.getColor(getResources(), R.color.textColor, null));
        v1RadioButton.setTextColor(textColor);
        v1RadioButton.setButtonTintList(tintList);
        v2cRadioButton.setTextColor(textColor);
        v2cRadioButton.setButtonTintList(tintList);
        v3RadioButton.setTextColor(textColor);
        v3RadioButton.setButtonTintList(tintList);
        if (savedInstanceState == null) {
            snmpVersionGroup.setOnCheckedChangeListener(null);
            SNMPVersion version = preferenceManager.getPreferenceSNMPVersion();
            v1RadioButton.setChecked(version == null || version.isV1());
            v2cRadioButton.setChecked(version != null && version.isV2C());
            v3RadioButton.setChecked(version != null && version.isV3());
        }
    }

    private void prepareSNMPPortTextField(PreferenceManager preferenceManager, Bundle savedInstanceState) {
        Log.d(SNMPDefaultsDialog.class.getName(), "prepareSNMPPortTextField");
        snmpPortEditText = dialogView.findViewById(R.id.edittext_dialog_snmp_defaults_snmp_port);
        snmpPortEditText.setOnLongClickListener(this::onEditTextLongClicked);
        if (savedInstanceState == null) {
            snmpPortEditText.setText(String.valueOf(preferenceManager.getPreferenceSNMPPort()));
        }
        prepareSNMPPortEditTextListener();
    }

    private void prepareSNMPPortEditTextListener() {
        Log.d(SNMPDefaultsDialog.class.getName(), "prepareSNMPPortEditTextListener");
        if (snmpPortEditTextWatcher != null) {
            snmpPortEditText.removeTextChangedListener(snmpPortEditTextWatcher);
            snmpPortEditTextWatcher = null;
        }
        snmpPortEditTextWatcher = new TextColorValidatingWatcher(snmpPortEditText, this::validateSNMPPort, getColor(R.color.textColor), getColor(R.color.textErrorColor));
        snmpPortEditText.addTextChangedListener(snmpPortEditTextWatcher);
    }

    private boolean validateSNMPPort(EditText editText) {
        Log.d(SNMPDefaultsDialog.class.getName(), "validateSNMPPort");
        String fieldName = getResources().getString(R.string.label_dialog_snmp_defaults_snmp_port);
        ValidationResult result = new PortFieldValidator(fieldName, requireContext()).validate(StringUtil.notNull(snmpPortEditText.getText()));
        Log.d(SNMPDefaultsDialog.class.getName(), "SNMP port validation result: " + result);
        return result.isValidationSuccessful();
    }

    private void prepareSNMPTransportRadioButtons(PreferenceManager preferenceManager, Bundle savedInstanceState) {
        Log.d(SNMPDefaultsDialog.class.getName(), "prepareSNMPTransportRadioButtons");
        snmpTransportGroup = dialogView.findViewById(R.id.radiogroup_dialog_snmp_defaults_snmp_transport);
        RadioButton udpRadioButton = dialogView.findViewById(R.id.radiobutton_dialog_snmp_defaults_snmp_transport_udp);
        RadioButton tcpRadioButton = dialogView.findViewById(R.id.radiobutton_dialog_snmp_defaults_snmp_transport_tcp);
        int textColor = getColor(R.color.textColor);
        ColorStateList tintList = ColorStateList.valueOf(ResourcesCompat.getColor(getResources(), R.color.textColor, null));
        udpRadioButton.setTextColor(textColor);
        udpRadioButton.setButtonTintList(tintList);
        tcpRadioButton.setTextColor(textColor);
        tcpRadioButton.setButtonTintList(tintList);
        if (savedInstanceState == null) {
            snmpTransportGroup.setOnCheckedChangeListener(null);
            SNMPTransport transport = preferenceManager.getPreferenceSNMPTransport();
            udpRadioButton.setChecked(transport == null || transport.isUDP());
            tcpRadioButton.setChecked(transport != null && transport.isTCP());
        }
    }

    private void prepareSNMPAuthAlgorithmSpinner(PreferenceManager preferenceManager, Bundle savedInstanceState) {
        Log.d(SNMPDefaultsDialog.class.getName(), "prepareSNMPAuthAlgorithmSpinner");
        snmpAuthAlgorithmSpinner = dialogView.findViewById(R.id.spinner_dialog_snmp_defaults_snmp_auth_algorithm);
        EnumMapping mapping = new EnumMapping(requireContext());
        SNMPAuthAlgorithm[] values = SNMPAuthAlgorithm.values();
        List<String> names = new ArrayList<>();
        for (SNMPAuthAlgorithm value : values) {
            names.add(mapping.getSNMPAuthAlgorithmName(value));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, names);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        snmpAuthAlgorithmSpinner.setAdapter(adapter);
        if (savedInstanceState != null && savedInstanceState.containsKey(getSNMPAuthAlgorithmKey())) {
            snmpAuthAlgorithmSpinner.setSelection(savedInstanceState.getInt(getSNMPAuthAlgorithmKey()));
        } else {
            SNMPAuthAlgorithm algorithm = preferenceManager.getPreferenceSNMPAuthAlgorithm();
            if (algorithm != null) {
                snmpAuthAlgorithmSpinner.setSelection(algorithm.ordinal());
            }
        }
    }

    private void prepareSNMPPrivAlgorithmSpinner(PreferenceManager preferenceManager, Bundle savedInstanceState) {
        Log.d(SNMPDefaultsDialog.class.getName(), "prepareSNMPPrivAlgorithmSpinner");
        snmpPrivAlgorithmSpinner = dialogView.findViewById(R.id.spinner_dialog_snmp_defaults_snmp_priv_algorithm);
        EnumMapping mapping = new EnumMapping(requireContext());
        SNMPPrivAlgorithm[] values = SNMPPrivAlgorithm.values();
        List<String> names = new ArrayList<>();
        for (SNMPPrivAlgorithm value : values) {
            names.add(mapping.getSNMPPrivAlgorithmName(value));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, names);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        snmpPrivAlgorithmSpinner.setAdapter(adapter);
        if (savedInstanceState != null && savedInstanceState.containsKey(getSNMPPrivAlgorithmKey())) {
            snmpPrivAlgorithmSpinner.setSelection(savedInstanceState.getInt(getSNMPPrivAlgorithmKey()));
        } else {
            SNMPPrivAlgorithm algorithm = preferenceManager.getPreferenceSNMPPrivAlgorithm();
            if (algorithm != null) {
                snmpPrivAlgorithmSpinner.setSelection(algorithm.ordinal());
            }
        }
    }

    private void prepareOkCancelImageButtons() {
        Log.d(SNMPDefaultsDialog.class.getName(), "prepareOkCancelImageButtons");
        ImageView okImage = dialogView.findViewById(R.id.imageview_dialog_snmp_defaults_ok);
        ImageView cancelImage = dialogView.findViewById(R.id.imageview_dialog_snmp_defaults_cancel);
        okImage.setOnClickListener(this::onOkClicked);
        cancelImage.setOnClickListener(this::onCancelClicked);
    }

    private String getSNMPAuthAlgorithmKey() {
        return SNMPDefaultsDialog.class.getName() + ".SNMPAuthAlgorithm";
    }

    private String getSNMPPrivAlgorithmKey() {
        return SNMPDefaultsDialog.class.getName() + ".SNMPPrivAlgorithm";
    }

    public SNMPVersion getSNMPVersion() {
        int checkedId = snmpVersionGroup.getCheckedRadioButtonId();
        if (checkedId == R.id.radiobutton_dialog_snmp_defaults_snmp_version_v2c) {
            return SNMPVersion.V2C;
        }
        if (checkedId == R.id.radiobutton_dialog_snmp_defaults_snmp_version_v3) {
            return SNMPVersion.V3;
        }
        return SNMPVersion.V1;
    }

    public int getSNMPPort() {
        String portText = StringUtil.notNull(snmpPortEditText.getText());
        int defaultPort = getResources().getInteger(R.integer.task_snmp_port_default);
        return NumberUtil.getIntValue(portText, defaultPort);
    }

    public SNMPTransport getSNMPTransport() {
        int checkedId = snmpTransportGroup.getCheckedRadioButtonId();
        if (checkedId == R.id.radiobutton_dialog_snmp_defaults_snmp_transport_tcp) {
            return SNMPTransport.TCP;
        }
        return SNMPTransport.UDP;
    }

    public SNMPAuthAlgorithm getSNMPAuthAlgorithm() {
        return SNMPAuthAlgorithm.values()[snmpAuthAlgorithmSpinner.getSelectedItemPosition()];
    }

    public SNMPPrivAlgorithm getSNMPPrivAlgorithm() {
        return SNMPPrivAlgorithm.values()[snmpPrivAlgorithmSpinner.getSelectedItemPosition()];
    }

    private void onOkClicked(View view) {
        Log.d(SNMPDefaultsDialog.class.getName(), "onOkClicked");
        String fieldName = getResources().getString(R.string.label_dialog_snmp_defaults_snmp_port);
        ValidationResult portResult = new PortFieldValidator(fieldName, requireContext()).validate(StringUtil.notNull(snmpPortEditText.getText()));
        if (!portResult.isValidationSuccessful()) {
            Log.d(SNMPDefaultsDialog.class.getName(), "Validation failed");
            showValidationErrorDialog(Collections.singletonList(portResult));
            return;
        }
        Log.d(SNMPDefaultsDialog.class.getName(), "Validation was successful");
        SNMPDefaultsSupport snmpDefaultsSupport = getSNMPDefaultsSupport();
        if (snmpDefaultsSupport != null) {
            snmpDefaultsSupport.onSNMPDefaultsDialogOkClicked(this);
        } else {
            Log.e(SNMPDefaultsDialog.class.getName(), "snmpDefaultsSupport is null");
            dismiss();
        }
    }

    private void showValidationErrorDialog(List<ValidationResult> validationResult) {
        Log.d(SNMPDefaultsDialog.class.getName(), "showValidationErrorDialog");
        ValidatorErrorDialog errorDialog = new ValidatorErrorDialog();
        errorDialog.setArguments(BundleUtil.validationResultListToBundle(errorDialog.getValidationResultBaseKey(), validationResult));
        errorDialog.show(getParentFragmentManager(), ValidatorErrorDialog.class.getName());
    }

    private void onCancelClicked(View view) {
        Log.d(SNMPDefaultsDialog.class.getName(), "onCancelClicked");
        SNMPDefaultsSupport snmpDefaultsSupport = getSNMPDefaultsSupport();
        if (snmpDefaultsSupport != null) {
            snmpDefaultsSupport.onSNMPDefaultsDialogCancelClicked(this);
        } else {
            Log.e(SNMPDefaultsDialog.class.getName(), "snmpDefaultsSupport is null");
            dismiss();
        }
    }

    @SuppressWarnings("SameReturnValue")
    private boolean onEditTextLongClicked(View view) {
        Log.d(SNMPDefaultsDialog.class.getName(), "onEditTextLongClicked");
        showContextOptionsDialog((EditText) view);
        return true;
    }

    private void showContextOptionsDialog(EditText editText) {
        Log.d(SNMPDefaultsDialog.class.getName(), "showContextOptionsDialog");
        new ContextOptionsSupportManager(getChildFragmentManager(), getClipboardManager()).showContextOptionsDialog(editText);
    }

    @Override
    public void onContextOptionsDialogClicked(ContextOptionsDialog contextOptionsDialog, int sourceResourceId, ContextOption option) {
        Log.d(SNMPDefaultsDialog.class.getName(), "onContextOptionsDialogClicked, sourceResourceId is " + sourceResourceId + ", option is " + option);
        if (snmpPortEditText.getId() == sourceResourceId) {
            ContextOptionsSupportManager contextOptionsSupportManager = new ContextOptionsSupportManager(getParentFragmentManager(), getClipboardManager());
            contextOptionsSupportManager.handleContextOption(snmpPortEditText, option);
            snmpPortEditText.setSelection(snmpPortEditText.getText().length());
        } else {
            Log.e(SNMPDefaultsDialog.class.getName(), "Source field is undefined.");
        }
        contextOptionsDialog.dismiss();
    }

    private int getColor(int colorId) {
        return ContextCompat.getColor(requireContext(), colorId);
    }

    private SNMPDefaultsSupport getSNMPDefaultsSupport() {
        Log.d(SNMPDefaultsDialog.class.getName(), "getSNMPDefaultsSupport");
        List<Fragment> fragments = getParentFragmentManager().getFragments();
        for (Fragment fragment : fragments) {
            if (fragment instanceof SNMPDefaultsSupport) {
                return (SNMPDefaultsSupport) fragment;
            }
        }
        Activity activity = getActivity();
        if (activity == null) {
            Log.e(SNMPDefaultsDialog.class.getName(), "getSNMPDefaultsSupport, activity is null");
            return null;
        }
        if (!(activity instanceof SNMPDefaultsSupport)) {
            Log.e(SNMPDefaultsDialog.class.getName(), "getSNMPDefaultsSupport, activity is not an instance of " + SNMPDefaultsSupport.class.getSimpleName());
            return null;
        }
        return (SNMPDefaultsSupport) activity;
    }
}
