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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.model.Interval;
import net.ibbaa.keepitup.ui.dialog.SuspensionIntervalsDialog;
import net.ibbaa.keepitup.util.BundleUtil;
import net.ibbaa.keepitup.util.TimeUtil;

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
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_suspension_interval, viewGroup, false);
        return new SuspensionIntervalViewHolder(itemView, intervalsDialog);
    }

    @Override
    public void onBindViewHolder(@NonNull SuspensionIntervalViewHolder suspensionIntervalViewHolder, int position) {
        Log.d(SuspensionIntervalAdapter.class.getName(), "onBindViewHolder");
        Interval interval = intervals.get(position);
        bindIntervalText(suspensionIntervalViewHolder, interval);
    }

    private void bindIntervalText(@NonNull SuspensionIntervalViewHolder suspensionIntervalViewHolder, Interval interval) {
        Log.d(LogEntryAdapter.class.getName(), "bindIntervalText");
        suspensionIntervalViewHolder.setIntervalText(TimeUtil.formatSuspensionIntervalText(interval, getContext()));
    }

    public void replaceItems(List<Interval> intervals) {
        this.intervals.clear();
        this.intervals.addAll(intervals);
    }


    public Bundle saveStateToBundle() {
        Log.d(SuspensionIntervalAdapter.class.getName(), "saveStateToBundle");
        return BundleUtil.suspensionIntervalListToBundle(getSuspensionIntervalsKey(), intervals);
    }

    public void restoreStateFromBundle(Bundle bundle) {
        Log.d(SuspensionIntervalAdapter.class.getName(), "restoreStateFromBundle");
        replaceItems(BundleUtil.suspensionIntervalListFromBundle(getSuspensionIntervalsKey(), bundle));
    }

    private String getSuspensionIntervalsKey() {
        return SuspensionIntervalAdapter.class.getSimpleName() + "SuspensionIntervals";
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
