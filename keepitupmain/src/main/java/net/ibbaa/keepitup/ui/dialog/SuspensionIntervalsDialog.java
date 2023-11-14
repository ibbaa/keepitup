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
import androidx.recyclerview.widget.RecyclerView;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.ui.SuspensionIntervalsSupport;
import net.ibbaa.keepitup.ui.adapter.SuspensionIntervalAdapter;

public class SuspensionIntervalsDialog extends DialogFragment {

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

    private void prepareOkCancelImageButtons() {
        Log.d(SuspensionIntervalsDialog.class.getName(), "prepareOkCancelImageButtons");
        ImageView okImage = dialogView.findViewById(R.id.imageview_dialog_suspension_intervals_ok);
        ImageView cancelImage = dialogView.findViewById(R.id.imageview_dialog_suspension_intervals_cancel);
        okImage.setOnClickListener(this::onOkClicked);
        cancelImage.setOnClickListener(this::onCancelClicked);
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

    public SuspensionIntervalAdapter getAdapter() {
        return (SuspensionIntervalAdapter) getSuspensionIntervalsRecyclerView().getAdapter();
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
}
