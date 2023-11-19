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
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.ui.SuspensionIntervalAddSupport;
import net.ibbaa.keepitup.util.BundleUtil;
import net.ibbaa.keepitup.util.StringUtil;

import java.util.List;

public class SuspensionIntervalAddDialog extends DialogFragment {

    public enum Mode {
        START,
        END
    }

    private View dialogView;
    private TextView timeLabelTextView;
    private NumberPicker timeHourPicker;
    private NumberPicker timeMinutePicker;

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
        prepareModeLabel();
        prepareTimePicker();
        prepareOkCancelImageButtons();
        return dialogView;
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

    private void prepareModeLabel() {
        Log.d(SuspensionIntervalAddDialog.class.getName(), "prepareModeLabel with mode");
        timeLabelTextView = dialogView.findViewById(R.id.textview_dialog_suspension_interval_add_time_label);
        if (Mode.END.equals(getMode())) {
            timeLabelTextView.setText(getResources().getString(R.string.text_dialog_suspension_interval_add_end));
        } else {
            timeLabelTextView.setText(getResources().getString(R.string.text_dialog_suspension_interval_add_start));
        }
    }

    private void prepareTimePicker() {
        Log.d(SuspensionIntervalAddDialog.class.getName(), "prepareTimePicker");
        timeHourPicker = dialogView.findViewById(R.id.picker_dialog_suspension_interval_add_time_hour);
        timeMinutePicker = dialogView.findViewById(R.id.picker_dialog_suspension_interval_add_time_minute);
        timeHourPicker.setMinValue(0);
        timeHourPicker.setMaxValue(23);
        timeMinutePicker.setMinValue(0);
        timeMinutePicker.setMaxValue(59);
    }

    private void prepareOkCancelImageButtons() {
        Log.d(SuspensionIntervalAddDialog.class.getName(), "prepareOkCancelImageButtons");
        ImageView okImage = dialogView.findViewById(R.id.imageview_dialog_suspension_interval_add_ok);
        ImageView cancelImage = dialogView.findViewById(R.id.imageview_dialog_suspension_interval_add_cancel);
        okImage.setOnClickListener(this::onOkClicked);
        cancelImage.setOnClickListener(this::onCancelClicked);
    }

    public String getModeKey() {
        return SuspensionIntervalAddDialog.Mode.class.getSimpleName();
    }

    private void onOkClicked(View view) {
        Log.d(SuspensionIntervalAddDialog.class.getName(), "onOkClicked");
        SuspensionIntervalAddSupport intervalAddSupport = getSuspensionIntervalAddSupport();
        if (intervalAddSupport != null) {
            intervalAddSupport.onSuspensionIntervalAddDialogOkClicked(this, getMode());
        } else {
            Log.e(SuspensionIntervalAddDialog.class.getName(), "intervalAddSupport is null");
            dismiss();
        }
    }

    private void onCancelClicked(View view) {
        Log.d(SuspensionIntervalAddDialog.class.getName(), "onCancelClicked");
        SuspensionIntervalAddSupport intervalAddSupport = getSuspensionIntervalAddSupport();
        if (intervalAddSupport != null) {
            intervalAddSupport.onSuspensionIntervalAddDialogCancelClicked(this);
        } else {
            Log.e(SuspensionIntervalAddDialog.class.getName(), "intervalAddSupport is null");
            dismiss();
        }
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
}
