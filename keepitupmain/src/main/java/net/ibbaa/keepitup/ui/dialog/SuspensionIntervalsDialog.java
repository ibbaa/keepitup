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

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.model.Interval;
import net.ibbaa.keepitup.model.Time;
import net.ibbaa.keepitup.service.TimeBasedSuspensionScheduler;
import net.ibbaa.keepitup.ui.ConfirmSupport;
import net.ibbaa.keepitup.ui.SettingsInputActivity;
import net.ibbaa.keepitup.ui.SuspensionIntervalSelectSupport;
import net.ibbaa.keepitup.ui.SuspensionIntervalsSupport;
import net.ibbaa.keepitup.ui.adapter.SuspensionIntervalAdapter;
import net.ibbaa.keepitup.ui.validation.IntervalValidator;
import net.ibbaa.keepitup.ui.validation.StandardIntervalValidator;
import net.ibbaa.keepitup.ui.validation.ValidationResult;
import net.ibbaa.keepitup.util.BundleUtil;
import net.ibbaa.keepitup.util.TimeUtil;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class SuspensionIntervalsDialog extends DialogFragment implements ConfirmSupport, SuspensionIntervalSelectSupport, IntervalValidator {

    private View dialogView;
    private RecyclerView suspensionIntervalsRecyclerView;
    private Interval currentInterval;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(SuspensionIntervalsDialog.class.getName(), "onCreate");
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.DialogTheme);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(SuspensionIntervalsDialog.class.getName(), "onCreateView");
        dialogView = inflater.inflate(R.layout.dialog_suspension_intervals, container);
        boolean containsSavedState = containsSavedState(savedInstanceState);
        Log.d(SuspensionIntervalsDialog.class.getName(), "containsSavedState is " + containsSavedState);
        Bundle adapterState = containsSavedState ? savedInstanceState.getBundle(getSuspensionIntervalsAdapterKey()) : null;
        Bundle currentIntervalState = containsSavedState ? savedInstanceState.getBundle(getCurrentSuspensionIntervalKey()) : null;
        prepareIntervalsRecyclerView(adapterState);
        prepareCurrentInterval(currentIntervalState);
        prepareAddImageButton();
        prepareOkCancelImageButtons();
        return dialogView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Log.d(SuspensionIntervalsDialog.class.getName(), "onSaveInstanceState");
        super.onSaveInstanceState(outState);
        Bundle adapterBundle = getAdapter().saveStateToBundle();
        outState.putBundle(getSuspensionIntervalsAdapterKey(), adapterBundle);
        if (currentInterval != null) {
            Bundle currentIntervalState = currentInterval.toBundle();
            outState.putBundle(getCurrentSuspensionIntervalKey(), currentIntervalState);
        }
    }

    private boolean containsSavedState(Bundle savedInstanceState) {
        Log.d(SuspensionIntervalsDialog.class.getName(), "containsSavedState");
        if (savedInstanceState == null) {
            Log.d(SuspensionIntervalsDialog.class.getName(), "savedInstanceState bundle is null");
            return false;
        }
        return savedInstanceState.containsKey(getSuspensionIntervalsAdapterKey());
    }

    private String getSuspensionIntervalsAdapterKey() {
        return SuspensionIntervalsDialog.class.getSimpleName() + "SuspensionIntervalsAdapter";
    }

    private String getCurrentSuspensionIntervalKey() {
        return SuspensionIntervalsDialog.class.getSimpleName() + "CurrentSuspensionInterval";
    }

    private void prepareIntervalsRecyclerView(Bundle adapterState) {
        Log.d(SuspensionIntervalsDialog.class.getName(), "prepareIntervalsRecyclerView");
        suspensionIntervalsRecyclerView = dialogView.findViewById(R.id.listview_dialog_suspension_intervals_intervals);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        suspensionIntervalsRecyclerView.setLayoutManager(layoutManager);
        suspensionIntervalsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        RecyclerView.Adapter<?> adapter = adapterState == null ? createAdapter() : restoreAdapter(adapterState);
        suspensionIntervalsRecyclerView.setAdapter(adapter);
    }

    private void prepareCurrentInterval(Bundle currentIntervalState) {
        Log.d(SuspensionIntervalsDialog.class.getName(), "prepareIntervalsRecyclerView");
        if (currentIntervalState != null) {
            currentInterval = new Interval(currentIntervalState);
            Log.d(SuspensionIntervalsDialog.class.getName(), "Current saved interval is " + currentInterval);
            return;
        }
        reinitializeCurrentInterval();
    }

    private Time getDefaultStart() {
        Log.d(SuspensionIntervalsDialog.class.getName(), "getDefaultStart");
        List<Interval> list = ((SuspensionIntervalAdapter) Objects.requireNonNull(suspensionIntervalsRecyclerView.getAdapter())).getAllItems();
        Time start = new Time();
        if (list.isEmpty()) {
            start.setHour(getResources().getInteger(R.integer.suspension_interval_default_start_hour));
            start.setMinute(getResources().getInteger(R.integer.suspension_interval_default_start_minute));
        } else {
            Interval latest = list.get(list.size() - 1);
            start = TimeUtil.addMinutes(latest.getEnd(), getResources().getInteger(R.integer.suspension_interval_default_distance));
        }
        Log.d(SuspensionIntervalsDialog.class.getName(), "default start is " + start);
        return start;
    }

    private Time getDefaultEnd(Time start) {
        Log.d(SuspensionIntervalsDialog.class.getName(), "getDefaultEnd for start time " + start);
        boolean hasIntervals = !((SuspensionIntervalAdapter) Objects.requireNonNull(suspensionIntervalsRecyclerView.getAdapter())).getAllItems().isEmpty();
        Time end;
        if (hasIntervals) {
            end = TimeUtil.addMinutes(start, getResources().getInteger(R.integer.suspension_interval_default_length_following));
        } else {
            end = TimeUtil.addMinutes(start, getResources().getInteger(R.integer.suspension_interval_default_length_first));
        }
        Log.d(SuspensionIntervalsDialog.class.getName(), "default end is " + end);
        return end;
    }

    private void prepareAddImageButton() {
        Log.d(SuspensionIntervalsDialog.class.getName(), "prepareAddImageButton");
        ImageView addImage = dialogView.findViewById(R.id.imageview_dialog_suspension_intervals_interval_select);
        addImage.setOnClickListener(this::onIntervalAddClicked);
    }

    private void prepareOkCancelImageButtons() {
        Log.d(SuspensionIntervalsDialog.class.getName(), "prepareOkCancelImageButtons");
        ImageView okImage = dialogView.findViewById(R.id.imageview_dialog_suspension_intervals_ok);
        ImageView cancelImage = dialogView.findViewById(R.id.imageview_dialog_suspension_intervals_cancel);
        okImage.setOnClickListener(this::onOkClicked);
        cancelImage.setOnClickListener(this::onCancelClicked);
    }

    public void onIntervalDeleteClicked(View view, int index) {
        Log.d(SuspensionIntervalsDialog.class.getName(), "onIntervalDeleteClicked for index " + index);
        ConfirmDialog confirmDialog = new ConfirmDialog();
        String confirmMessage = getResources().getString(R.string.text_dialog_confirm_delete_interval);
        Bundle bundle = BundleUtil.stringsToBundle(new String[]{confirmDialog.getMessageKey(), confirmDialog.getTypeKey()}, new String[]{confirmMessage, ConfirmDialog.Type.DELETEINTERVAL.name()});
        bundle.putInt(confirmDialog.getPositionKey(), index);
        confirmDialog.setArguments(bundle);
        showDialog(confirmDialog, ConfirmDialog.class.getName());
    }

    @Override
    public void onConfirmDialogOkClicked(ConfirmDialog confirmDialog, ConfirmDialog.Type type) {
        Log.d(SuspensionIntervalsDialog.class.getName(), "onConfirmDialogOkClicked for type " + type);
        if (ConfirmDialog.Type.DELETEINTERVAL.equals(type)) {
            int position = confirmDialog.getPosition();
            if (position >= 0) {
                Log.d(SuspensionIntervalsDialog.class.getName(), "deleting interval at position " + position);
                getAdapter().removeItem(position);
                getAdapter().notifyItemRemoved(position);
                reinitializeCurrentInterval();
            } else {
                Log.e(SuspensionIntervalsDialog.class.getName(), ConfirmDialog.class.getSimpleName() + " arguments do not contain position key " + confirmDialog.getPositionKey());
            }
        } else {
            Log.e(SuspensionIntervalsDialog.class.getName(), "Unknown type " + type);
        }
        confirmDialog.dismiss();
    }

    @Override
    public void onConfirmDialogCancelClicked(ConfirmDialog confirmDialog) {
        Log.d(SuspensionIntervalsDialog.class.getName(), "onConfirmDialogCancelClicked");
        confirmDialog.dismiss();
    }

    @Override
    public void onSuspensionIntervalSelectDialogOkClicked(SuspensionIntervalSelectDialog intervalSelectDialog, SuspensionIntervalSelectDialog.Mode mode) {
        Log.d(SuspensionIntervalsDialog.class.getName(), "onSuspensionIntervalSelectDialogOkClicked with mode " + mode);
        if (SuspensionIntervalSelectDialog.Mode.START.equals(mode)) {
            Time start = intervalSelectDialog.getSelectedTime();
            Time end = getDefaultEnd(start);
            currentInterval.setStart(start);
            currentInterval.setEnd(end);
            intervalSelectDialog.dismiss();
            showSuspensionIntervallSelectDialog(SuspensionIntervalSelectDialog.Mode.END, end, start);
        } else {
            currentInterval.setEnd(intervalSelectDialog.getSelectedTime());
            getAdapter().addItem(currentInterval);
            List<Interval> intervals = TimeUtil.sortIntervalList(getAdapter().getAllItems());
            getAdapter().replaceItems(intervals);
            getAdapter().notifyDataSetChanged();
            reinitializeCurrentInterval();
            intervalSelectDialog.dismiss();
        }
    }

    @Override
    public void onSuspensionIntervalSelectDialogCancelClicked(SuspensionIntervalSelectDialog intervalSelectDialog, SuspensionIntervalSelectDialog.Mode mode) {
        Log.d(SuspensionIntervalsDialog.class.getName(), "onSuspensionIntervalSelectDialogCancelClicked with mode " + mode);
        if (SuspensionIntervalSelectDialog.Mode.START.equals(mode)) {
            Time start = intervalSelectDialog.getSelectedTime();
            Time end = getDefaultEnd(start);
            currentInterval.setStart(start);
            currentInterval.setEnd(end);
        } else {
            currentInterval.setEnd(intervalSelectDialog.getSelectedTime());
        }
        intervalSelectDialog.dismiss();
    }

    @Override
    public ValidationResult validateDuration(Interval interval) {
        Log.d(SuspensionIntervalsDialog.class.getName(), "validateDuration for interval " + interval);
        IntervalValidator validator = new StandardIntervalValidator(getContext(), getAdapter().getAllItems());
        return validator.validateDuration(interval);
    }

    @Override
    public ValidationResult validateOverlap(Interval interval) {
        Log.d(SuspensionIntervalsDialog.class.getName(), "validateOverlap for interval " + interval);
        IntervalValidator validator = new StandardIntervalValidator(getContext(), getAdapter().getAllItems());
        return validator.validateOverlap(interval);
    }

    @Override
    public ValidationResult validateInInterval(Time time) {
        Log.d(SuspensionIntervalsDialog.class.getName(), "validateInInterval for time " + time);
        IntervalValidator validator = new StandardIntervalValidator(getContext(), getAdapter().getAllItems());
        return validator.validateInInterval(time);
    }

    private void onIntervalAddClicked(View view) {
        Log.d(SuspensionIntervalsDialog.class.getName(), "onIntervalAddClicked");
        showSuspensionIntervallSelectDialog(SuspensionIntervalSelectDialog.Mode.START, currentInterval.getStart(), null);
    }

    private void onOkClicked(View view) {
        Log.d(SuspensionIntervalsDialog.class.getName(), "onOkClicked");
        SuspensionIntervalsSupport intervalsSupport = getSuspensionIntervalsSupport();
        if (intervalsSupport != null) {
            intervalsSupport.onSuspensionIntervalsDialogOkClicked(this);
        } else {
            Log.e(SuspensionIntervalsDialog.class.getName(), "intervalsSupport is null");
            dismiss();
        }
    }

    private void onCancelClicked(View view) {
        Log.d(SuspensionIntervalsDialog.class.getName(), "onCancelClicked");
        SuspensionIntervalsSupport intervalsSupport = getSuspensionIntervalsSupport();
        if (intervalsSupport != null) {
            intervalsSupport.onSuspensionIntervalsDialogCancelClicked(this);
        } else {
            Log.e(SuspensionIntervalsDialog.class.getName(), "intervalsSupport is null");
            dismiss();
        }
    }

    private void reinitializeCurrentInterval() {
        currentInterval = new Interval();
        currentInterval.setStart(getDefaultStart());
        currentInterval.setEnd(getDefaultEnd(currentInterval.getStart()));
    }

    public RecyclerView getSuspensionIntervalsRecyclerView() {
        return suspensionIntervalsRecyclerView;
    }

    private RecyclerView.Adapter<?> restoreAdapter(Bundle adapterState) {
        Log.d(SuspensionIntervalsDialog.class.getName(), "restoreAdapter");
        SuspensionIntervalAdapter adapter = new SuspensionIntervalAdapter(Collections.emptyList(), this);
        adapter.restoreStateFromBundle(adapterState);
        return adapter;
    }

    private RecyclerView.Adapter<?> createAdapter() {
        Log.d(SuspensionIntervalsDialog.class.getName(), "createAdapter");
        TimeBasedSuspensionScheduler scheduler = getTimeBasedSuspensionScheduler();
        if (scheduler == null) {
            Log.e(SuspensionIntervalsDialog.class.getName(), "createAdapter, scheduler is null, cannot get intervals");
            return new SuspensionIntervalAdapter(Collections.emptyList(), this);
        }
        return new SuspensionIntervalAdapter(scheduler.getIntervals(), this);
    }

    public SuspensionIntervalAdapter getAdapter() {
        return (SuspensionIntervalAdapter) getSuspensionIntervalsRecyclerView().getAdapter();
    }

    public TimeBasedSuspensionScheduler getTimeBasedSuspensionScheduler() {
        Log.d(SuspensionIntervalsDialog.class.getName(), "getTimeBasedSuspensionScheduler");
        Activity activity = getActivity();
        if (activity == null) {
            Log.e(SuspensionIntervalsDialog.class.getName(), "getTimeBasedSuspensionScheduler, activity is null");
            return null;
        }
        if (!(activity instanceof SettingsInputActivity)) {
            Log.e(SuspensionIntervalsDialog.class.getName(), "getTimeBasedSuspensionScheduler, activity is not an instance of " + SettingsInputActivity.class.getSimpleName());
            return null;
        }
        return ((SettingsInputActivity) activity).getTimeBasedSuspensionScheduler();
    }

    private SuspensionIntervalsSupport getSuspensionIntervalsSupport() {
        Log.d(SuspensionIntervalsDialog.class.getName(), "getSuspensionIntervalsSupport");
        Activity activity = getActivity();
        if (activity == null) {
            Log.e(SuspensionIntervalsDialog.class.getName(), "getSuspensionIntervalsSupport, activity is null");
            return null;
        }
        if (!(activity instanceof SuspensionIntervalsSupport)) {
            Log.e(SuspensionIntervalsDialog.class.getName(), "getSuspensionIntervalsSupport, activity is not an instance of " + SuspensionIntervalsSupport.class.getSimpleName());
            return null;
        }
        return (SuspensionIntervalsSupport) activity;
    }


    private void showSuspensionIntervallSelectDialog(SuspensionIntervalSelectDialog.Mode mode, Time defaultTime, Time startTime) {
        Log.d(SuspensionIntervalsDialog.class.getName(), "showSuspensionIntervallSelectDialog with mode " + mode + " and defaultTime " + defaultTime);
        SuspensionIntervalSelectDialog intervalSelectDialog = new SuspensionIntervalSelectDialog();
        Bundle bundle = BundleUtil.stringToBundle(intervalSelectDialog.getModeKey(), mode.name());
        bundle = BundleUtil.bundleToBundle(intervalSelectDialog.getDefaultTimeKey(), defaultTime.toBundle(), bundle);
        if (startTime != null) {
            bundle = BundleUtil.bundleToBundle(intervalSelectDialog.getStartTimeKey(), startTime.toBundle(), bundle);
        }
        intervalSelectDialog.setArguments(bundle);
        showDialog(intervalSelectDialog, SuspensionIntervalSelectDialog.class.getName());
    }

    private void showDialog(DialogFragment dialog, String name) {
        try {
            dialog.show(getParentFragmentManager(), name);
        } catch (Exception exc) {
            Log.e(SettingsInputActivity.class.getName(), "Error opening dialog", exc);
        }
    }
}
