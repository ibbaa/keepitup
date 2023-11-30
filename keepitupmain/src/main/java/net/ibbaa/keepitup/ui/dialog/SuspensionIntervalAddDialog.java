/*
 * Copyright (c) 2023. Alwin Ibba
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
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.model.Interval;
import net.ibbaa.keepitup.model.Time;
import net.ibbaa.keepitup.ui.SuspensionIntervalAddSupport;
import net.ibbaa.keepitup.ui.validation.IntervalValidator;
import net.ibbaa.keepitup.ui.validation.NumberPickerColorListener;
import net.ibbaa.keepitup.ui.validation.ValidationResult;
import net.ibbaa.keepitup.util.BundleUtil;
import net.ibbaa.keepitup.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class SuspensionIntervalAddDialog extends DialogFragment {

    public enum Mode {
        START,
        END
    }

    private View dialogView;
    private TextView timeLabelTextView;
    private NumberPicker timeHourPicker;
    private NumberPicker timeMinutePicker;
    private NumberPickerColorListener colorListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(SuspensionIntervalAddDialog.class.getName(), "onCreate");
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.DialogTheme);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(SuspensionIntervalAddDialog.class.getName(), "onCreateView");
        dialogView = inflater.inflate(R.layout.dialog_suspension_interval_add, container);
        Time savedTime = null;
        if (savedInstanceState != null && savedInstanceState.containsKey(getSavedTimeKey())) {
            savedTime = new Time(Objects.requireNonNull(savedInstanceState.getBundle(getSavedTimeKey())));
        }
        prepareModeLabel();
        prepareTimePicker(savedTime);
        prepareOkCancelImageButtons();
        return dialogView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Time time = getSelectedTime();
        outState.putBundle(getSavedTimeKey(), time.toBundle());
    }

    private Mode getMode() {
        Log.d(SuspensionIntervalAddDialog.class.getName(), "getModeString");
        String modeString = BundleUtil.stringFromBundle(getModeKey(), requireArguments());
        Log.d(SuspensionIntervalAddDialog.class.getName(), "mode string is " + modeString);
        if (StringUtil.isEmpty(modeString)) {
            Log.e(SuspensionIntervalAddDialog.class.getName(), SuspensionIntervalAddDialog.Mode.class.getSimpleName() + " not specified. Returning " + Mode.START);
            return Mode.START;
        }
        try {
            return SuspensionIntervalAddDialog.Mode.valueOf(modeString);
        } catch (IllegalArgumentException exc) {
            Log.e(SuspensionIntervalAddDialog.class.getName(), SuspensionIntervalAddDialog.Mode.class.getSimpleName() + "." + modeString + " does not exist. Returning " + Mode.START);
        }
        return Mode.START;
    }

    private Time getDefaultTime() {
        Log.d(SuspensionIntervalAddDialog.class.getName(), "getDefaultTime");
        Bundle bundle = BundleUtil.bundleFromBundle(getDefaultTimeKey(), requireArguments());
        if (bundle == null) {
            Log.d(SuspensionIntervalAddDialog.class.getName(), "no default time settings provided");
            Time time = new Time();
            time.setHour(getResources().getInteger(R.integer.suspension_interval_default_start_hour));
            time.setMinute(getResources().getInteger(R.integer.suspension_interval_default_start_minute));
            Log.d(SuspensionIntervalAddDialog.class.getName(), "Returning " + time);
            return time;
        }
        Time time = new Time(bundle);
        Log.d(SuspensionIntervalAddDialog.class.getName(), "Default time settings provided: " + time);
        return time;
    }

    private Time getStartTime() {
        Log.d(SuspensionIntervalAddDialog.class.getName(), "getStartTime");
        Bundle bundle = BundleUtil.bundleFromBundle(getStartTimeKey(), requireArguments());
        if (bundle == null) {
            Log.d(SuspensionIntervalAddDialog.class.getName(), "Start time is null");
            return null;
        }
        Time time = new Time(bundle);
        Log.d(SuspensionIntervalAddDialog.class.getName(), "Start time: " + time);
        return time;
    }

    private void prepareModeLabel() {
        Log.d(SuspensionIntervalAddDialog.class.getName(), "prepareModeLabel with mode");
        timeLabelTextView = dialogView.findViewById(R.id.textview_dialog_suspension_interval_add_time_label);
        if (Mode.END.equals(getMode())) {
            timeLabelTextView.setText(getResources().getString(R.string.text_dialog_suspension_interval_add_end));
        } else {
            timeLabelTextView.setText(getResources().getString(R.string.text_dialog_suspension_interval_add_start));
        }
    }

    private void prepareTimePicker(Time savedTime) {
        Log.d(SuspensionIntervalAddDialog.class.getName(), "prepareTimePicker");
        timeHourPicker = dialogView.findViewById(R.id.picker_dialog_suspension_interval_add_time_hour);
        timeMinutePicker = dialogView.findViewById(R.id.picker_dialog_suspension_interval_add_time_minute);
        timeHourPicker.setFormatter(new TimeNumberPickerFormatter());
        timeMinutePicker.setFormatter(new TimeNumberPickerFormatter());
        timeHourPicker.setMinValue(0);
        timeHourPicker.setMaxValue(23);
        timeMinutePicker.setMinValue(0);
        timeMinutePicker.setMaxValue(59);
        if (savedTime != null) {
            timeHourPicker.setValue(savedTime.getHour());
            timeMinutePicker.setValue(savedTime.getMinute());
        } else {
            Time time = getDefaultTime();
            timeHourPicker.setValue(time.getHour());
            timeMinutePicker.setValue(time.getMinute());
        }
        prepareNumberPickerColorListener();
        setNumberPickerColor();
    }

    private void setNumberPickerColor() {
        Log.d(SuspensionIntervalAddDialog.class.getName(), "setNumberPickerColor");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            boolean success = Mode.START.equals(getMode()) ? validateInInterval(timeHourPicker) : validateOverlapAndDuration(timeHourPicker);
            Log.d(SuspensionIntervalAddDialog.class.getName(), "validation successful: " + success);
            if (success) {
                timeHourPicker.setTextColor(getColor(R.color.textColor));
                timeMinutePicker.setTextColor(getColor(R.color.textColor));
            } else {
                timeHourPicker.setTextColor(getColor(R.color.textErrorColor));
                timeMinutePicker.setTextColor(getColor(R.color.textErrorColor));
            }
        }
    }

    private void prepareNumberPickerColorListener() {
        Log.d(SuspensionIntervalAddDialog.class.getName(), "prepareNumberPickerColorListener");
        if (Mode.START.equals(getMode())) {
            colorListener = new NumberPickerColorListener(Arrays.asList(timeHourPicker, timeMinutePicker), this::validateInInterval, getColor(R.color.textColor), getColor(R.color.textErrorColor));
            timeHourPicker.setOnValueChangedListener(colorListener);
            timeMinutePicker.setOnValueChangedListener(colorListener);
        } else {
            colorListener = new NumberPickerColorListener(Arrays.asList(timeHourPicker, timeMinutePicker), this::validateOverlapAndDuration, getColor(R.color.textColor), getColor(R.color.textErrorColor));
            timeHourPicker.setOnValueChangedListener(colorListener);
            timeMinutePicker.setOnValueChangedListener(colorListener);
        }
    }

    private void prepareOkCancelImageButtons() {
        Log.d(SuspensionIntervalAddDialog.class.getName(), "prepareOkCancelImageButtons");
        ImageView okImage = dialogView.findViewById(R.id.imageview_dialog_suspension_interval_add_ok);
        ImageView cancelImage = dialogView.findViewById(R.id.imageview_dialog_suspension_interval_add_cancel);
        okImage.setOnClickListener(this::onOkClicked);
        cancelImage.setOnClickListener(this::onCancelClicked);
    }

    public String getModeKey() {
        return SuspensionIntervalAddDialog.Mode.class.getSimpleName() + "Mode";
    }

    public String getDefaultTimeKey() {
        return SuspensionIntervalAddDialog.Mode.class.getSimpleName() + "DefaultTime";
    }

    public String getStartTimeKey() {
        return SuspensionIntervalAddDialog.Mode.class.getSimpleName() + "StartTime";
    }

    public String getSavedTimeKey() {
        return SuspensionIntervalAddDialog.Mode.class.getSimpleName() + "SavedTime";
    }

    public Time getSelectedTime() {
        Time time = new Time();
        time.setHour(timeHourPicker.getValue());
        time.setMinute(timeMinutePicker.getValue());
        return time;
    }

    private boolean validateInInterval(NumberPicker picker) {
        Log.d(SuspensionIntervalAddDialog.class.getName(), "validateInInterval");
        ValidationResult validateInInterval = validateInInterval();
        Log.d(SuspensionIntervalAddDialog.class.getName(), "validateInInterval, validateInInterval result = " + validateInInterval);
        if (validateInInterval == null) {
            return true;
        }
        return validateInInterval.isValidationSuccessful();
    }

    private boolean validateOverlapAndDuration(NumberPicker picker) {
        Log.d(SuspensionIntervalAddDialog.class.getName(), "validateOverlapAndDuration");
        ValidationResult validateOverlap = validateOverlap();
        ValidationResult validateDuration = validateDuration();
        Log.d(SuspensionIntervalAddDialog.class.getName(), "validateOverlapAndDuration, validateOverlap result = " + validateOverlap);
        Log.d(SuspensionIntervalAddDialog.class.getName(), "validateOverlapAndDuration, validateDuration result = " + validateDuration);
        if (validateOverlap == null || validateDuration == null) {
            return true;
        }
        return validateOverlap.isValidationSuccessful() && validateDuration.isValidationSuccessful();
    }

    private Interval createIntervalFromSelectedTime() {
        Log.d(SuspensionIntervalAddDialog.class.getName(), "createIntervalFromSelectedTime");
        Interval interval = new Interval();
        Time start = getStartTime();
        if (start == null) {
            Log.e(SuspensionIntervalAddDialog.class.getName(), "createIntervalFromSelectedTime, start time is null");
            return null;
        }
        Time end = getSelectedTime();
        interval.setStart(start);
        interval.setEnd(end);
        return interval;
    }

    private ValidationResult validateInInterval() {
        Log.d(SuspensionIntervalAddDialog.class.getName(), "validateInInterval");
        IntervalValidator validator = getIntervalValidator();
        if (validator == null) {
            Log.d(SuspensionIntervalAddDialog.class.getName(), "validateInInterval, validator is null");
            return null;
        }
        return validator.validateInInterval(getSelectedTime());
    }

    private ValidationResult validateDuration() {
        return validate(IntervalValidator::validateDuration);
    }

    private ValidationResult validateOverlap() {
        return validate(IntervalValidator::validateOverlap);
    }

    private ValidationResult validate(Validator validator) {
        Log.d(SuspensionIntervalAddDialog.class.getName(), "validate");
        IntervalValidator intervalvalidator = getIntervalValidator();
        if (validator == null) {
            Log.d(SuspensionIntervalAddDialog.class.getName(), "validate, validator is null");
            return null;
        }
        Interval interval = createIntervalFromSelectedTime();
        if (interval == null) {
            Log.d(SuspensionIntervalAddDialog.class.getName(), "validate, interval is null");
            return null;
        }
        return validator.validate(intervalvalidator, interval);
    }

    private void onOkClicked(View view) {
        Log.d(SuspensionIntervalAddDialog.class.getName(), "onOkClicked");
        List<ValidationResult> resultList;
        if (Mode.START.equals(getMode())) {
            resultList = validateStartMode();
        } else {
            resultList = validateEndMode();
        }
        if (!resultList.isEmpty()) {
            Log.d(SuspensionIntervalAddDialog.class.getName(), "Validation failed. Opening error dialog.");
            showValidatorErrorDialog(resultList);
            return;
        }
        SuspensionIntervalAddSupport intervalAddSupport = getSuspensionIntervalAddSupport();
        if (intervalAddSupport != null) {
            intervalAddSupport.onSuspensionIntervalAddDialogOkClicked(this, getMode());
        } else {
            Log.e(SuspensionIntervalAddDialog.class.getName(), "intervalAddSupport is null");
            dismiss();
        }
    }

    private List<ValidationResult> validateStartMode() {
        Log.d(SuspensionIntervalAddDialog.class.getName(), "validateStartMode");
        ValidationResult validateInInterval = validateInInterval();
        Log.d(SuspensionIntervalAddDialog.class.getName(), "validateStartMode, validateInInterval result = " + validateInInterval);
        List<ValidationResult> resultList = new ArrayList<>();
        if (validateInInterval != null && !validateInInterval.isValidationSuccessful()) {
            String start = getResources().getString(R.string.text_dialog_suspension_interval_add_start);
            resultList.add(new ValidationResult(validateInInterval.isValidationSuccessful(), start, validateInInterval.getMessage()));
        }
        return resultList;
    }

    private List<ValidationResult> validateEndMode() {
        Log.d(SuspensionIntervalAddDialog.class.getName(), "validateEndMode");
        ValidationResult validateOverlap = validateOverlap();
        ValidationResult validateDuration = validateDuration();
        Log.d(SuspensionIntervalAddDialog.class.getName(), "validateEndMode, validateOverlap result = " + validateOverlap);
        Log.d(SuspensionIntervalAddDialog.class.getName(), "validateEndMode, validateDuration result = " + validateDuration);
        List<ValidationResult> resultList = new ArrayList<>();
        String end = getResources().getString(R.string.text_dialog_suspension_interval_add_end);
        if (validateOverlap != null && !validateOverlap.isValidationSuccessful()) {
            resultList.add(new ValidationResult(validateOverlap.isValidationSuccessful(), end, validateOverlap.getMessage()));
        }
        if (validateDuration != null && !validateDuration.isValidationSuccessful()) {
            resultList.add(new ValidationResult(validateDuration.isValidationSuccessful(), end, validateDuration.getMessage()));
        }
        return resultList;
    }

    private void onCancelClicked(View view) {
        Log.d(SuspensionIntervalAddDialog.class.getName(), "onCancelClicked");
        SuspensionIntervalAddSupport intervalAddSupport = getSuspensionIntervalAddSupport();
        if (intervalAddSupport != null) {
            intervalAddSupport.onSuspensionIntervalAddDialogCancelClicked(this, getMode());
        } else {
            Log.e(SuspensionIntervalAddDialog.class.getName(), "intervalAddSupport is null");
            dismiss();
        }
    }

    private void showValidatorErrorDialog(List<ValidationResult> validationResult) {
        Log.d(SuspensionIntervalAddDialog.class.getName(), "showValidatorErrorDialog");
        ValidatorErrorDialog errorDialog = new ValidatorErrorDialog();
        Bundle bundle = BundleUtil.validationResultListToBundle(errorDialog.getValidationResultBaseKey(), validationResult);
        bundle = BundleUtil.integerToBundle(errorDialog.getMessageWidthKey(), getResources().getDimensionPixelSize(R.dimen.textview_dialog_validator_error_message_width), bundle);
        errorDialog.setArguments(bundle);
        errorDialog.show(getParentFragmentManager(), ValidatorErrorDialog.class.getName());
    }

    private SuspensionIntervalAddSupport getSuspensionIntervalAddSupport() {
        Log.d(SuspensionIntervalsDialog.class.getName(), "getSuspensionIntervalAddSupport");
        List<Fragment> fragments = getParentFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment instanceof SuspensionIntervalAddSupport) {
                    return (SuspensionIntervalAddSupport) fragment;
                }
            }
        }
        Log.d(SuspensionIntervalsDialog.class.getName(), "getSuspensionIntervalAddSupport, no parent fragment implementing " + SuspensionIntervalsDialog.class.getSimpleName());
        Activity activity = getActivity();
        if (activity == null) {
            Log.e(SuspensionIntervalsDialog.class.getName(), "getSuspensionIntervalAddSupport, activity is null");
            return null;
        }
        if (!(activity instanceof SuspensionIntervalAddSupport)) {
            Log.e(ConfirmDialog.class.getName(), "getSuspensionIntervalAddSupport, activity is not an instance of " + SuspensionIntervalAddSupport.class.getSimpleName());
            return null;
        }
        return (SuspensionIntervalAddSupport) activity;
    }

    private IntervalValidator getIntervalValidator() {
        Log.d(SuspensionIntervalsDialog.class.getName(), "getIntervalValidator");
        List<Fragment> fragments = getParentFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment instanceof IntervalValidator) {
                    return (IntervalValidator) fragment;
                }
            }
        }
        Log.d(SuspensionIntervalsDialog.class.getName(), "getIntervalValidator, no parent fragment implementing " + IntervalValidator.class.getSimpleName());
        Activity activity = getActivity();
        if (activity == null) {
            Log.e(SuspensionIntervalsDialog.class.getName(), "getIntervalValidator, activity is null");
            return null;
        }
        if (!(activity instanceof IntervalValidator)) {
            Log.e(ConfirmDialog.class.getName(), "getIntervalValidator, activity is not an instance of " + IntervalValidator.class.getSimpleName());
            return null;
        }
        return (IntervalValidator) activity;
    }

    private int getColor(int colorid) {
        return ContextCompat.getColor(requireContext(), colorid);
    }

    protected NumberPickerColorListener getColorListener() {
        return colorListener;
    }

    private interface Validator {
        ValidationResult validate(IntervalValidator validator, Interval interval);
    }
}
