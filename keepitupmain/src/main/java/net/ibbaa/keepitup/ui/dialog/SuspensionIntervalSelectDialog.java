/*
 * Copyright (c) 2024. Alwin Ibba
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
import net.ibbaa.keepitup.ui.SuspensionIntervalSelectSupport;
import net.ibbaa.keepitup.ui.validation.IntervalValidator;
import net.ibbaa.keepitup.ui.validation.NumberPickerColorListener;
import net.ibbaa.keepitup.ui.validation.ValidationResult;
import net.ibbaa.keepitup.util.BundleUtil;
import net.ibbaa.keepitup.util.StringUtil;
import net.ibbaa.keepitup.util.TimeUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class SuspensionIntervalSelectDialog extends DialogFragment {

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
        Log.d(SuspensionIntervalSelectDialog.class.getName(), "onCreate");
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.DialogTheme);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(SuspensionIntervalSelectDialog.class.getName(), "onCreateView");
        dialogView = inflater.inflate(R.layout.dialog_suspension_interval_select, container);
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
        Log.d(SuspensionIntervalSelectDialog.class.getName(), "getMode");
        String modeString = BundleUtil.stringFromBundle(getModeKey(), requireArguments());
        Log.d(SuspensionIntervalSelectDialog.class.getName(), "mode is " + modeString);
        if (StringUtil.isEmpty(modeString)) {
            Log.e(SuspensionIntervalSelectDialog.class.getName(), SuspensionIntervalSelectDialog.Mode.class.getSimpleName() + " not specified. Returning " + Mode.START);
            return Mode.START;
        }
        try {
            return SuspensionIntervalSelectDialog.Mode.valueOf(modeString);
        } catch (IllegalArgumentException exc) {
            Log.e(SuspensionIntervalSelectDialog.class.getName(), SuspensionIntervalSelectDialog.Mode.class.getSimpleName() + "." + modeString + " does not exist. Returning " + Mode.START);
        }
        return Mode.START;
    }

    private Time getDefaultTime() {
        Log.d(SuspensionIntervalSelectDialog.class.getName(), "getDefaultTime");
        Bundle bundle = BundleUtil.bundleFromBundle(getDefaultTimeKey(), requireArguments());
        if (bundle == null) {
            Log.d(SuspensionIntervalSelectDialog.class.getName(), "no default time settings provided");
            Time time = new Time();
            time.setHour(getResources().getInteger(R.integer.suspension_interval_default_start_hour));
            time.setMinute(getResources().getInteger(R.integer.suspension_interval_default_start_minute));
            Log.d(SuspensionIntervalSelectDialog.class.getName(), "Returning " + time);
            return time;
        }
        Time time = new Time(bundle);
        Log.d(SuspensionIntervalSelectDialog.class.getName(), "Default time settings provided: " + time);
        return time;
    }

    private Time getStartTime() {
        Log.d(SuspensionIntervalSelectDialog.class.getName(), "getStartTime");
        Bundle bundle = BundleUtil.bundleFromBundle(getStartTimeKey(), requireArguments());
        if (bundle == null) {
            Log.d(SuspensionIntervalSelectDialog.class.getName(), "Start time is null");
            return null;
        }
        Time time = new Time(bundle);
        Log.d(SuspensionIntervalSelectDialog.class.getName(), "Start time: " + time);
        return time;
    }

    private void prepareModeLabel() {
        Log.d(SuspensionIntervalSelectDialog.class.getName(), "prepareModeLabel with mode");
        timeLabelTextView = dialogView.findViewById(R.id.textview_dialog_suspension_interval_select_time_label);
        if (Mode.END.equals(getMode())) {
            timeLabelTextView.setText(getResources().getString(R.string.string_end));
        } else {
            timeLabelTextView.setText(getResources().getString(R.string.string_start));
        }
    }

    private void prepareTimePicker(Time savedTime) {
        Log.d(SuspensionIntervalSelectDialog.class.getName(), "prepareTimePicker");
        timeHourPicker = dialogView.findViewById(R.id.picker_dialog_suspension_interval_select_time_hour);
        timeMinutePicker = dialogView.findViewById(R.id.picker_dialog_suspension_interval_select_time_minute);
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
        Log.d(SuspensionIntervalSelectDialog.class.getName(), "setNumberPickerColor");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            boolean success = validateOverlapAndDuration(timeHourPicker);
            Log.d(SuspensionIntervalSelectDialog.class.getName(), "validation successful: " + success);
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
        Log.d(SuspensionIntervalSelectDialog.class.getName(), "prepareNumberPickerColorListener");
        colorListener = new NumberPickerColorListener(Arrays.asList(timeHourPicker, timeMinutePicker), this::validateOverlapAndDuration, getColor(R.color.textColor), getColor(R.color.textErrorColor));
        timeHourPicker.setOnValueChangedListener(colorListener);
        timeMinutePicker.setOnValueChangedListener(colorListener);
    }

    private void prepareOkCancelImageButtons() {
        Log.d(SuspensionIntervalSelectDialog.class.getName(), "prepareOkCancelImageButtons");
        ImageView okImage = dialogView.findViewById(R.id.imageview_dialog_suspension_interval_select_ok);
        ImageView cancelImage = dialogView.findViewById(R.id.imageview_dialog_suspension_interval_select_cancel);
        okImage.setOnClickListener(this::onOkClicked);
        cancelImage.setOnClickListener(this::onCancelClicked);
    }

    public String getModeKey() {
        return SuspensionIntervalSelectDialog.Mode.class.getSimpleName() + "Mode";
    }

    public String getDefaultTimeKey() {
        return SuspensionIntervalSelectDialog.Mode.class.getSimpleName() + "DefaultTime";
    }

    public String getStartTimeKey() {
        return SuspensionIntervalSelectDialog.Mode.class.getSimpleName() + "StartTime";
    }

    public String getSavedTimeKey() {
        return SuspensionIntervalSelectDialog.Mode.class.getSimpleName() + "SavedTime";
    }

    public Time getSelectedTime() {
        Time time = new Time();
        time.setHour(timeHourPicker.getValue());
        time.setMinute(timeMinutePicker.getValue());
        return time;
    }

    private boolean validateOverlapAndDuration(NumberPicker picker) {
        Log.d(SuspensionIntervalSelectDialog.class.getName(), "validateOverlapAndDuration");
        ValidationResult validateOverlap = validateOverlap();
        ValidationResult validateDuration = validateDuration();
        Log.d(SuspensionIntervalSelectDialog.class.getName(), "validateOverlapAndDuration, validateOverlap result = " + validateOverlap);
        Log.d(SuspensionIntervalSelectDialog.class.getName(), "validateOverlapAndDuration, validateDuration result = " + validateDuration);
        if (validateOverlap == null || validateDuration == null) {
            return true;
        }
        return validateOverlap.isValidationSuccessful() && validateDuration.isValidationSuccessful();
    }

    private Interval createIntervalFromSelectedTime() {
        Log.d(SuspensionIntervalSelectDialog.class.getName(), "createIntervalFromSelectedTime");
        Interval interval = new Interval();
        Time start = getStartTime();
        Time end;
        if (start == null) {
            start = getSelectedTime();
            end = TimeUtil.addMinutes(start, getResources().getInteger(R.integer.suspension_interval_min_duration));
        } else {
            end = getSelectedTime();
        }
        interval.setStart(start);
        interval.setEnd(end);
        return interval;
    }

    private ValidationResult validateDuration() {
        return validate(IntervalValidator::validateDuration);
    }

    private ValidationResult validateOverlap() {
        return validate(IntervalValidator::validateOverlap);
    }

    private ValidationResult validate(Validator validator) {
        Log.d(SuspensionIntervalSelectDialog.class.getName(), "validate");
        IntervalValidator intervalValidator = getIntervalValidator();
        if (intervalValidator == null) {
            Log.d(SuspensionIntervalSelectDialog.class.getName(), "validate, validator is null");
            return null;
        }
        Interval interval = createIntervalFromSelectedTime();
        if (interval == null) {
            Log.d(SuspensionIntervalSelectDialog.class.getName(), "validate, interval is null");
            return null;
        }
        return validator.validate(intervalValidator, interval);
    }

    private void onOkClicked(View view) {
        Log.d(SuspensionIntervalSelectDialog.class.getName(), "onOkClicked");
        List<ValidationResult> resultList;
        if (Mode.START.equals(getMode())) {
            resultList = validate(getResources().getString(R.string.string_start));
        } else {
            resultList = validate(getResources().getString(R.string.string_end));
        }
        if (!resultList.isEmpty()) {
            Log.d(SuspensionIntervalSelectDialog.class.getName(), "Validation failed. Opening error dialog.");
            showValidatorErrorDialog(resultList);
            return;
        }
        SuspensionIntervalSelectSupport intervalSelectSupport = getSuspensionIntervalSelectSupport();
        if (intervalSelectSupport != null) {
            intervalSelectSupport.onSuspensionIntervalSelectDialogOkClicked(this, getMode());
        } else {
            Log.e(SuspensionIntervalSelectDialog.class.getName(), "intervalSelectSupport is null");
            dismiss();
        }
    }

    private List<ValidationResult> validate(String field) {
        Log.d(SuspensionIntervalSelectDialog.class.getName(), "validateEndMode");
        ValidationResult validateOverlap = validateOverlap();
        ValidationResult validateDuration = validateDuration();
        Log.d(SuspensionIntervalSelectDialog.class.getName(), "validateEndMode, validateOverlap result = " + validateOverlap);
        Log.d(SuspensionIntervalSelectDialog.class.getName(), "validateEndMode, validateDuration result = " + validateDuration);
        List<ValidationResult> resultList = new ArrayList<>();
        if (validateOverlap != null && !validateOverlap.isValidationSuccessful()) {
            resultList.add(new ValidationResult(validateOverlap.isValidationSuccessful(), field, validateOverlap.getMessage()));
        }
        if (validateDuration != null && !validateDuration.isValidationSuccessful()) {
            resultList.add(new ValidationResult(validateDuration.isValidationSuccessful(), field, validateDuration.getMessage()));
        }
        return resultList;
    }

    private void onCancelClicked(View view) {
        Log.d(SuspensionIntervalSelectDialog.class.getName(), "onCancelClicked");
        SuspensionIntervalSelectSupport intervalSelectSupport = getSuspensionIntervalSelectSupport();
        if (intervalSelectSupport != null) {
            intervalSelectSupport.onSuspensionIntervalSelectDialogCancelClicked(this, getMode());
        } else {
            Log.e(SuspensionIntervalSelectDialog.class.getName(), "intervalSelectSupport is null");
            dismiss();
        }
    }

    private void showValidatorErrorDialog(List<ValidationResult> validationResult) {
        Log.d(SuspensionIntervalSelectDialog.class.getName(), "showValidatorErrorDialog");
        ValidatorErrorDialog errorDialog = new ValidatorErrorDialog();
        Bundle bundle = BundleUtil.validationResultListToBundle(errorDialog.getValidationResultBaseKey(), validationResult);
        bundle = BundleUtil.integerToBundle(errorDialog.getMessageWidthKey(), getResources().getDimensionPixelSize(R.dimen.textview_dialog_validator_error_message_width), bundle);
        errorDialog.setArguments(bundle);
        errorDialog.show(getParentFragmentManager(), ValidatorErrorDialog.class.getName());
    }

    private SuspensionIntervalSelectSupport getSuspensionIntervalSelectSupport() {
        Log.d(SuspensionIntervalsDialog.class.getName(), "getSuspensionIntervalSelectSupport");
        List<Fragment> fragments = getParentFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment instanceof SuspensionIntervalSelectSupport) {
                    return (SuspensionIntervalSelectSupport) fragment;
                }
            }
        }
        Log.d(SuspensionIntervalsDialog.class.getName(), "getSuspensionIntervalSelectSupport, no parent fragment implementing " + SuspensionIntervalsDialog.class.getSimpleName());
        Activity activity = getActivity();
        if (activity == null) {
            Log.e(SuspensionIntervalsDialog.class.getName(), "getSuspensionIntervalSelectSupport, activity is null");
            return null;
        }
        if (!(activity instanceof SuspensionIntervalSelectSupport)) {
            Log.e(ConfirmDialog.class.getName(), "getSuspensionIntervalSelectSupport, activity is not an instance of " + SuspensionIntervalSelectSupport.class.getSimpleName());
            return null;
        }
        return (SuspensionIntervalSelectSupport) activity;
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
