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
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.model.Interval;
import net.ibbaa.keepitup.model.Time;
import net.ibbaa.keepitup.service.TimeBasedSuspensionScheduler;
import net.ibbaa.keepitup.ui.NetworkTaskMainActivity;
import net.ibbaa.keepitup.ui.SettingsInputActivity;
import net.ibbaa.keepitup.ui.adapter.DeleteSwipeCallback;
import net.ibbaa.keepitup.ui.adapter.SuspensionIntervalAdapter;
import net.ibbaa.keepitup.ui.support.ConfirmSupport;
import net.ibbaa.keepitup.ui.support.SuspensionIntervalSelectSupport;
import net.ibbaa.keepitup.ui.support.SuspensionIntervalsSupport;
import net.ibbaa.keepitup.ui.support.SwipeDeleteSupport;
import net.ibbaa.keepitup.ui.validation.IntervalValidator;
import net.ibbaa.keepitup.ui.validation.StandardIntervalValidator;
import net.ibbaa.keepitup.ui.validation.ValidationResult;
import net.ibbaa.keepitup.util.BundleUtil;
import net.ibbaa.keepitup.util.TimeUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@SuppressWarnings({"unused"})
public class SuspensionIntervalsDialog extends DialogFragmentBase implements ConfirmSupport, SwipeDeleteSupport, SuspensionIntervalSelectSupport, IntervalValidator {

    private View dialogView;
    private RecyclerView suspensionIntervalsRecyclerView;
    private ItemTouchHelper itemTouchHelper;
    private Interval currentInterval;
    private boolean keepEnd;
    private int position;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(SuspensionIntervalsDialog.class.getName(), "onCreate");
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.DialogTheme);
    }

    @Override
    @SuppressWarnings({"SimplifiableConditionalExpression"})
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(SuspensionIntervalsDialog.class.getName(), "onCreateView");
        dialogView = inflater.inflate(R.layout.dialog_suspension_intervals, container);
        initEdgeToEdgeInsets(dialogView);
        boolean containsSavedState = containsSavedState(savedInstanceState);
        Log.d(SuspensionIntervalsDialog.class.getName(), "containsSavedState is " + containsSavedState);
        Bundle adapterState = containsSavedState ? savedInstanceState.getBundle(getSuspensionIntervalsAdapterKey()) : null;
        Bundle currentIntervalState = containsSavedState ? savedInstanceState.getBundle(getCurrentSuspensionIntervalKey()) : null;
        boolean keepEndState = containsSavedState ? savedInstanceState.getBoolean(getKeepEndKey()) : false;
        prepareIntervalsRecyclerView(adapterState);
        prepareCurrentInterval(currentIntervalState, keepEndState);
        preparePosition(savedInstanceState);
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
        outState.putInt(getPositionKey(), position);
        outState.putBoolean(getKeepEndKey(), keepEnd);
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

    private String getPositionKey() {
        return SuspensionIntervalsDialog.class.getSimpleName() + "Position";
    }

    private String getKeepEndKey() {
        return SuspensionIntervalsDialog.class.getSimpleName() + "KeepEnd";
    }

    private void prepareIntervalsRecyclerView(Bundle adapterState) {
        Log.d(SuspensionIntervalsDialog.class.getName(), "prepareIntervalsRecyclerView");
        suspensionIntervalsRecyclerView = dialogView.findViewById(R.id.listview_dialog_suspension_intervals_intervals);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        suspensionIntervalsRecyclerView.setLayoutManager(layoutManager);
        suspensionIntervalsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        RecyclerView.Adapter<?> adapter = adapterState == null ? createAdapter() : restoreAdapter(adapterState);
        suspensionIntervalsRecyclerView.setAdapter(adapter);
        itemTouchHelper = new ItemTouchHelper(new DeleteSwipeCallback(this));
        itemTouchHelper.attachToRecyclerView(suspensionIntervalsRecyclerView);
    }

    private void prepareCurrentInterval(Bundle currentIntervalState, boolean keepEndState) {
        Log.d(SuspensionIntervalsDialog.class.getName(), "prepareCurrentInterval");
        if (currentIntervalState != null) {
            currentInterval = new Interval(currentIntervalState);
            keepEnd = keepEndState;
            Log.d(SuspensionIntervalsDialog.class.getName(), "Current saved interval is " + currentInterval);
            Log.d(SuspensionIntervalsDialog.class.getName(), "Current saved keep end is " + keepEnd);
            return;
        }
        initializeCurrentInterval();
    }

    private void preparePosition(Bundle savedInstanceState) {
        Log.d(SuspensionIntervalsDialog.class.getName(), "preparePosition");
        if (savedInstanceState != null) {
            position = BundleUtil.integerFromBundle(getPositionKey(), savedInstanceState);
        } else {
            position = -1;
        }
    }

    private boolean noIntervalsDefined() {
        return ((SuspensionIntervalAdapter) Objects.requireNonNull(suspensionIntervalsRecyclerView.getAdapter())).getAllItems().isEmpty();
    }

    private void prepareAddImageButton() {
        Log.d(SuspensionIntervalsDialog.class.getName(), "prepareAddImageButton");
        ImageView addImage = dialogView.findViewById(R.id.imageview_dialog_suspension_intervals_interval_add);
        addImage.setOnClickListener(this::onIntervalAddClicked);
    }

    private void prepareOkCancelImageButtons() {
        Log.d(SuspensionIntervalsDialog.class.getName(), "prepareOkCancelImageButtons");
        ImageView okImage = dialogView.findViewById(R.id.imageview_dialog_suspension_intervals_ok);
        ImageView cancelImage = dialogView.findViewById(R.id.imageview_dialog_suspension_intervals_cancel);
        okImage.setOnClickListener(this::onOkClicked);
        cancelImage.setOnClickListener(this::onCancelClicked);
    }

    public void onIntervalOpenClicked(View view, int index) {
        Log.d(SuspensionIntervalsDialog.class.getName(), "onIntervalOpenClicked for index " + index);
        Interval interval = getAdapter().getItem(index);
        if (interval == null) {
            Log.e(SuspensionIntervalsDialog.class.getName(), "onIntervalOpenClicked, interval is null");
            return;
        }
        position = index;
        currentInterval = new Interval();
        currentInterval.setStart(interval.getStart());
        currentInterval.setEnd(interval.getEnd());
        showSuspensionIntervalSelectDialog(SuspensionIntervalSelectDialog.Mode.START, currentInterval.getStart(), null);
    }

    public void onIntervalDeleteClicked(View view, int index) {
        Log.d(SuspensionIntervalsDialog.class.getName(), "onIntervalDeleteClicked for index " + index);
        if (index < 0) {
            Log.e(SuspensionIntervalsDialog.class.getName(), "index " + index + " is invalid");
            return;
        }
        openConfirmDialog(index, ConfirmDialog.Type.DELETEINTERVAL);
    }

    public void onDeleteSwiped(int index) {
        Log.d(SuspensionIntervalsDialog.class.getName(), "onIntervalDeleteSwiped for index " + index);
        if (index < 0) {
            Log.e(SuspensionIntervalsDialog.class.getName(), "index " + index + " is invalid");
            return;
        }
        openConfirmDialog(index, ConfirmDialog.Type.DELETEINTERVALSWIPE);
    }

    private void openConfirmDialog(int index, ConfirmDialog.Type type) {
        ConfirmDialog confirmDialog = new ConfirmDialog();
        String confirmMessage = getResources().getString(R.string.text_dialog_confirm_delete_interval);
        Bundle bundle = BundleUtil.stringsToBundle(new String[]{confirmDialog.getMessageKey(), confirmDialog.getTypeKey()}, new String[]{confirmMessage, type.name()});
        bundle.putInt(confirmDialog.getPositionKey(), index);
        confirmDialog.setArguments(bundle);
        showDialog(confirmDialog, ConfirmDialog.class.getName());
    }

    @Override
    public void onConfirmDialogOkClicked(ConfirmDialog confirmDialog, ConfirmDialog.Type type) {
        Log.d(SuspensionIntervalsDialog.class.getName(), "onConfirmDialogOkClicked for type " + type);
        if (ConfirmDialog.Type.DELETEINTERVAL.equals(type) || ConfirmDialog.Type.DELETEINTERVALSWIPE.equals(type)) {
            int deletePosition = confirmDialog.getPosition();
            if (deletePosition >= 0) {
                Log.d(SuspensionIntervalsDialog.class.getName(), "deleting interval at deletePosition " + deletePosition);
                getAdapter().removeItem(deletePosition);
                getAdapter().notifyItemRemoved(deletePosition);
                initializeCurrentInterval();
                if (ConfirmDialog.Type.DELETEINTERVALSWIPE.equals(type)) {
                    reattachItemTouchHelper();
                }
            } else {
                Log.e(SuspensionIntervalsDialog.class.getName(), ConfirmDialog.class.getSimpleName() + " arguments do not contain deletePosition key " + confirmDialog.getPositionKey());
            }
        } else {
            Log.e(SuspensionIntervalsDialog.class.getName(), "Unknown type " + type);
        }
        confirmDialog.dismiss();
    }

    @Override
    public void onConfirmDialogCancelClicked(ConfirmDialog confirmDialog, ConfirmDialog.Type type) {
        Log.d(SuspensionIntervalsDialog.class.getName(), "onConfirmDialogCancelClicked");
        if (ConfirmDialog.Type.DELETEINTERVALSWIPE.equals(type)) {
            int position = confirmDialog.getPosition();
            getAdapter().notifyItemChanged(position);
            reattachItemTouchHelper();
        }
        confirmDialog.dismiss();
    }

    private void reattachItemTouchHelper() {
        Log.d(NetworkTaskMainActivity.class.getName(), "reattachItemTouchHelper");
        RecyclerView recyclerView = dialogView.findViewById(R.id.listview_dialog_suspension_intervals_intervals);
        if (itemTouchHelper != null) {
            itemTouchHelper.attachToRecyclerView(null);
        }
        recyclerView.post(() -> {
            itemTouchHelper = new ItemTouchHelper(new DeleteSwipeCallback(this));
            itemTouchHelper.attachToRecyclerView(recyclerView);
        });
    }

    @Override
    @SuppressWarnings("NotifyDataSetChanged")
    public void onSuspensionIntervalSelectDialogOkClicked(SuspensionIntervalSelectDialog intervalSelectDialog, SuspensionIntervalSelectDialog.Mode mode) {
        Log.d(SuspensionIntervalsDialog.class.getName(), "onSuspensionIntervalSelectDialogOkClicked with mode ");
        if (SuspensionIntervalSelectDialog.Mode.START.equals(mode)) {
            prepareCurrentInterval(intervalSelectDialog);
            intervalSelectDialog.dismiss();
            showSuspensionIntervalSelectDialog(SuspensionIntervalSelectDialog.Mode.END, currentInterval.getEnd(), currentInterval.getStart());
        } else {
            currentInterval.setEnd(intervalSelectDialog.getSelectedTime());
            if (position >= 0) {
                getAdapter().removeItem(position);
                position = -1;
            }
            getAdapter().addItem(currentInterval);
            List<Interval> intervals = TimeUtil.sortIntervalList(getAdapter().getAllItems());
            getAdapter().replaceItems(intervals);
            getAdapter().notifyDataSetChanged();
            initializeCurrentInterval();
            intervalSelectDialog.dismiss();
        }
    }

    @Override
    public void onSuspensionIntervalSelectDialogCancelClicked(SuspensionIntervalSelectDialog intervalSelectDialog, SuspensionIntervalSelectDialog.Mode mode) {
        Log.d(SuspensionIntervalsDialog.class.getName(), "onSuspensionIntervalSelectDialogCancelClicked with mode " + mode);
        if (position >= 0) {
            position = -1;
            initializeCurrentInterval();
        } else {
            if (SuspensionIntervalSelectDialog.Mode.START.equals(mode)) {
                prepareCurrentInterval(intervalSelectDialog);
            } else {
                currentInterval.setEnd(intervalSelectDialog.getSelectedTime());
                keepEnd = true;
            }
        }
        intervalSelectDialog.dismiss();
    }

    private void prepareCurrentInterval(SuspensionIntervalSelectDialog intervalSelectDialog) {
        Log.d(SuspensionIntervalsDialog.class.getName(), "prepareCurrentInterval");
        Log.d(SuspensionIntervalsDialog.class.getName(), "position is " + position);
        Time start = intervalSelectDialog.getSelectedTime();
        Time end;
        if (position >= 0) {
            Interval interval = getAdapter().getItem(position);
            if (interval == null) {
                Log.e(SuspensionIntervalsDialog.class.getName(), "prepareCurrentInterval, interval at position " + position + " is null");
                end = getEnd(start);
            } else {
                end = isIntervalEndStillValid(interval, start) ? interval.getEnd() : getEnd(start);
            }
        } else {
            end = keepEnd && isIntervalEndStillValid(currentInterval, start) ? currentInterval.getEnd() : getEnd(start);
            getEnd(start);
        }
        Log.d(SuspensionIntervalsDialog.class.getName(), "start is " + start);
        Log.d(SuspensionIntervalsDialog.class.getName(), "end is " + end);
        currentInterval.setStart(start);
        currentInterval.setEnd(end);
    }

    private boolean isIntervalEndStillValid(Interval interval, Time start) {
        Interval newInterval = new Interval();
        newInterval.setStart(start);
        newInterval.setEnd(interval.getEnd());
        ValidationResult resultOverlap = validateOverlap(newInterval);
        ValidationResult resultDuration = validateDuration(newInterval);
        return resultOverlap.isValidationSuccessful() && resultDuration.isValidationSuccessful();
    }

    private Time getEnd(Time start) {
        Log.d(SuspensionIntervalsDialog.class.getName(), "getEnd for start " + start);
        if (noIntervalsDefined()) {
            Log.d(SuspensionIntervalsDialog.class.getName(), "No intervals defined.");
            return TimeUtil.addMinutes(start, getResources().getInteger(R.integer.suspension_interval_default_duration));
        }
        Interval currentGap = TimeUtil.getCurrentGap(getAdapter().getAllItems(), start);
        Log.d(SuspensionIntervalsDialog.class.getName(), "Current gap is " + currentGap);
        int distance = TimeUtil.getDistance(start, currentGap.getEnd());
        int thresholdDistance = getResources().getInteger(R.integer.suspension_interval_current_gap_threshold_distance);
        int minDistance = getResources().getInteger(R.integer.suspension_interval_current_gap_min_distance);
        Log.d(SuspensionIntervalsDialog.class.getName(), "Distance is " + distance);
        Log.d(SuspensionIntervalsDialog.class.getName(), "Min distance is " + minDistance);
        Log.d(SuspensionIntervalsDialog.class.getName(), "Threshold distance is " + thresholdDistance);
        if (distance >= thresholdDistance) {
            return TimeUtil.addMinutes(start, getResources().getInteger(R.integer.suspension_interval_threshold_duration));
        } else if (distance < minDistance) {
            return TimeUtil.addMinutes(start, getResources().getInteger(R.integer.suspension_interval_min_duration));
        }
        return TimeUtil.substractMinutes(currentGap.getEnd(), getResources().getInteger(R.integer.suspension_interval_min_distance));
    }

    @Override
    public ValidationResult validateDuration() {
        Log.d(SuspensionIntervalsDialog.class.getName(), "validateDuration");
        IntervalValidator validator = new StandardIntervalValidator(getContext(), getAdapter().getAllItems());
        return validator.validateDuration();
    }

    @Override
    public ValidationResult validateOverlapSorted() {
        Log.d(SuspensionIntervalsDialog.class.getName(), "validateOverlapSorted");
        IntervalValidator validator = new StandardIntervalValidator(getContext(), getAdapter().getAllItems());
        return validator.validateOverlapSorted();
    }

    @Override
    public ValidationResult validateDuration(Interval interval) {
        Log.d(SuspensionIntervalsDialog.class.getName(), "validateDuration for interval " + interval);
        IntervalValidator validator = new StandardIntervalValidator(getContext(), getValidationItems());
        return validator.validateDuration(interval);
    }

    @Override
    public ValidationResult validateOverlap(Interval interval) {
        Log.d(SuspensionIntervalsDialog.class.getName(), "validateOverlap for interval " + interval);
        IntervalValidator validator = new StandardIntervalValidator(getContext(), getValidationItems());
        return validator.validateOverlap(interval);
    }

    private List<Interval> getValidationItems() {
        Log.d(SuspensionIntervalsDialog.class.getName(), "getValidationItems");
        Log.d(SuspensionIntervalsDialog.class.getName(), "position is " + position);
        if (position < 0) {
            return getAdapter().getAllItems();
        }
        List<Interval> validationItems = new ArrayList<>(getAdapter().getAllItems());
        if (position >= 0 && position < validationItems.size()) {
            validationItems.remove(position);
        }
        return validationItems;
    }

    private void onIntervalAddClicked(View view) {
        Log.d(SuspensionIntervalsDialog.class.getName(), "onIntervalAddClicked");
        position = -1;
        showSuspensionIntervalSelectDialog(SuspensionIntervalSelectDialog.Mode.START, currentInterval.getStart(), null);
    }

    private void onOkClicked(View view) {
        Log.d(SuspensionIntervalsDialog.class.getName(), "onOkClicked");
        List<ValidationResult> resultList = validateFinalIntervals();
        if (!resultList.isEmpty()) {
            Log.d(SuspensionIntervalsDialog.class.getName(), "Validation failed. Opening error dialog.");
            showValidatorErrorDialog(resultList);
            return;
        }
        SuspensionIntervalsSupport intervalsSupport = getSuspensionIntervalsSupport();
        if (intervalsSupport != null) {
            intervalsSupport.onSuspensionIntervalsDialogOkClicked(this);
        } else {
            Log.e(SuspensionIntervalsDialog.class.getName(), "intervalsSupport is null");
            dismiss();
        }
    }

    private List<ValidationResult> validateFinalIntervals() {
        Log.d(SuspensionIntervalsDialog.class.getName(), "validateFinalIntervals");
        ValidationResult validateOverlapSorted = validateOverlapSorted();
        ValidationResult validateDuration = validateDuration();
        Log.d(SuspensionIntervalsDialog.class.getName(), "validateFinalIntervals, validateOverlapSorted result = " + validateOverlapSorted);
        Log.d(SuspensionIntervalsDialog.class.getName(), "validateFinalIntervals, validateDuration result = " + validateDuration);
        List<ValidationResult> resultList = new ArrayList<>();
        if (!validateOverlapSorted.isValidationSuccessful()) {
            resultList.add(new ValidationResult(validateOverlapSorted.isValidationSuccessful(), validateOverlapSorted.getFieldName(), validateOverlapSorted.getMessage()));
        }
        if (!validateDuration.isValidationSuccessful()) {
            resultList.add(new ValidationResult(validateDuration.isValidationSuccessful(), validateDuration.getFieldName(), validateDuration.getMessage()));
        }
        return resultList;
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

    private void initializeCurrentInterval() {
        Log.d(SuspensionIntervalsDialog.class.getName(), "initializeCurrentInterval");
        keepEnd = false;
        if (noIntervalsDefined()) {
            currentInterval = getDefaultInterval();
            Log.d(SuspensionIntervalsDialog.class.getName(), "No intervals defined. Returning " + currentInterval);
        } else {
            Interval largestGap = TimeUtil.getLargestGap(getAdapter().getAllItems());
            Log.d(SuspensionIntervalsDialog.class.getName(), "Largest gap: " + largestGap);
            int largestGapDuration = TimeUtil.getDuration(largestGap);
            int thresholdDuration = getResources().getInteger(R.integer.suspension_interval_largest_gap_threshold_duration);
            int minDuration = getResources().getInteger(R.integer.suspension_interval_largest_gap_min_duration);
            Log.d(SuspensionIntervalsDialog.class.getName(), "Largest gap duration: " + largestGapDuration);
            Log.d(SuspensionIntervalsDialog.class.getName(), "Threshold duration: " + thresholdDuration);
            Log.d(SuspensionIntervalsDialog.class.getName(), "Min duration: " + minDuration);
            if (largestGapDuration >= thresholdDuration) {
                currentInterval = getThresholdInterval(largestGap);
            } else if (largestGapDuration < minDuration) {
                currentInterval = getDefaultInterval();
            } else {
                currentInterval = getMinInterval(largestGap);
            }
            Log.d(SuspensionIntervalsDialog.class.getName(), "Returning " + currentInterval);
        }
    }

    private Interval getDefaultInterval() {
        Log.d(SuspensionIntervalsDialog.class.getName(), "getDefaultInterval");
        Interval interval = new Interval();
        Time start = new Time();
        start.setHour(getResources().getInteger(R.integer.suspension_interval_default_start_hour));
        start.setMinute(getResources().getInteger(R.integer.suspension_interval_default_start_minute));
        Time end = TimeUtil.addMinutes(start, getResources().getInteger(R.integer.suspension_interval_default_duration));
        interval.setStart(start);
        interval.setEnd(end);
        Log.d(SuspensionIntervalsDialog.class.getName(), "Default interval is " + interval);
        return interval;
    }

    private Interval getThresholdInterval(Interval largestGap) {
        Log.d(SuspensionIntervalsDialog.class.getName(), "getThresholdInterval for gap " + largestGap);
        Interval interval = new Interval();
        interval.setStart(TimeUtil.addMinutes(largestGap.getStart(), getResources().getInteger(R.integer.suspension_interval_threshold_distance)));
        interval.setEnd(TimeUtil.addMinutes(interval.getStart(), getResources().getInteger(R.integer.suspension_interval_threshold_duration)));
        Log.d(SuspensionIntervalsDialog.class.getName(), "Threshold interval is " + interval);
        return interval;
    }

    private Interval getMinInterval(Interval largestGap) {
        Log.d(SuspensionIntervalsDialog.class.getName(), "getMinInterval for gap " + largestGap);
        Interval interval = new Interval();
        interval.setStart(TimeUtil.addMinutes(largestGap.getStart(), getResources().getInteger(R.integer.suspension_interval_min_distance)));
        interval.setEnd(TimeUtil.substractMinutes(largestGap.getEnd(), getResources().getInteger(R.integer.suspension_interval_min_distance)));
        Log.d(SuspensionIntervalsDialog.class.getName(), "Min interval is " + interval);
        return interval;
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

    private void showSuspensionIntervalSelectDialog(SuspensionIntervalSelectDialog.Mode mode, Time defaultTime, Time startTime) {
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

    private void showValidatorErrorDialog(List<ValidationResult> validationResult) {
        Log.d(SuspensionIntervalsDialog.class.getName(), "showValidatorErrorDialog");
        ValidatorErrorDialog errorDialog = new ValidatorErrorDialog();
        Bundle bundle = BundleUtil.validationResultListToBundle(errorDialog.getValidationResultBaseKey(), validationResult);
        bundle = BundleUtil.integerToBundle(errorDialog.getMessageWidthKey(), getResources().getDimensionPixelSize(R.dimen.textview_dialog_validator_error_message_width), bundle);
        errorDialog.setArguments(bundle);
        errorDialog.show(getParentFragmentManager(), ValidatorErrorDialog.class.getName());
    }

    private void showDialog(DialogFragment dialog, String name) {
        try {
            dialog.show(getParentFragmentManager(), name);
        } catch (Exception exc) {
            Log.e(SettingsInputActivity.class.getName(), "Error opening dialog", exc);
        }
    }
}
