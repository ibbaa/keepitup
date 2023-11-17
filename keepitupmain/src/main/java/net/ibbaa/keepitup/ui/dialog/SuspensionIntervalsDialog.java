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
import net.ibbaa.keepitup.service.TimeBasedSuspensionScheduler;
import net.ibbaa.keepitup.ui.ConfirmSupport;
import net.ibbaa.keepitup.ui.SettingsInputActivity;
import net.ibbaa.keepitup.ui.SuspensionIntervalsSupport;
import net.ibbaa.keepitup.ui.adapter.SuspensionIntervalAdapter;
import net.ibbaa.keepitup.util.BundleUtil;

import java.util.Collections;

public class SuspensionIntervalsDialog extends DialogFragment implements ConfirmSupport {

    private View dialogView;
    private RecyclerView suspensionIntervalsRecyclerView;

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
        prepareIntervalsRecyclerView(adapterState);
        prepareOkCancelImageButtons();
        return dialogView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Log.d(SuspensionIntervalsDialog.class.getName(), "onSaveInstanceState");
        super.onSaveInstanceState(outState);
        Bundle adapterBundle = getAdapter().saveStateToBundle();
        outState.putBundle(getSuspensionIntervalsAdapterKey(), adapterBundle);
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

    private void prepareIntervalsRecyclerView(Bundle adapterState) {
        Log.d(FileChooseDialog.class.getName(), "prepareIntervalsRecyclerView");
        suspensionIntervalsRecyclerView = dialogView.findViewById(R.id.listview_dialog_file_suspension_intervals_intervals);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        suspensionIntervalsRecyclerView.setLayoutManager(layoutManager);
        suspensionIntervalsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        RecyclerView.Adapter<?> adapter = adapterState == null ? createAdapter() : restoreAdapter(adapterState);
        suspensionIntervalsRecyclerView.setAdapter(adapter);
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

    private void showDialog(DialogFragment dialog, String name) {
        try {
            dialog.show(getParentFragmentManager(), name);
        } catch (Exception exc) {
            Log.e(SettingsInputActivity.class.getName(), "Error opening dialog", exc);
        }
    }
}
