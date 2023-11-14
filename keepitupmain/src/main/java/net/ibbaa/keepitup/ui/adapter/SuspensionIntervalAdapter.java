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

package net.ibbaa.keepitup.ui.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.model.Interval;
import net.ibbaa.keepitup.ui.dialog.SuspensionIntervalsDialog;

import java.util.ArrayList;
import java.util.List;

public class SuspensionIntervalAdapter extends RecyclerView.Adapter<SuspensionIntervalViewHolder> {

    private final List<Interval> intervals;
    private final SuspensionIntervalsDialog intervalsDialog;

    public SuspensionIntervalAdapter(List<Interval> intervals, SuspensionIntervalsDialog intervalsDialog) {
        this.intervals = new ArrayList<>();
        this.intervalsDialog = intervalsDialog;
    }

    @NonNull
    @Override
    public SuspensionIntervalViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Log.d(SuspensionIntervalAdapter.class.getName(), "onCreateViewHolder");
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull SuspensionIntervalViewHolder suspensionIntervalViewHolder, int position) {
        Log.d(SuspensionIntervalAdapter.class.getName(), "onBindViewHolder");
    }

    public Bundle saveStateToBundle() {
        Log.d(SuspensionIntervalAdapter.class.getName(), "saveStateToBundle");
        return null;
    }

    public void restoreStateFromBundle(Bundle bundle) {
        Log.d(SuspensionIntervalAdapter.class.getName(), "restoreStateFromBundle");
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    private Context getContext() {
        return intervalsDialog.getActivity();
    }

    private Resources getResources() {
        return getContext().getResources();
    }
}
