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

package net.ibbaa.keepitup.ui;

import android.content.res.Resources;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.db.IntervalDAO;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.model.Interval;
import net.ibbaa.keepitup.ui.dialog.SuspensionIntervalsDialog;

import java.util.ArrayList;
import java.util.List;

public class IntervalHandler {

    private final GlobalSettingsActivity globalSettingsActivity;
    private final SuspensionIntervalsDialog intervalDialog;

    public IntervalHandler(GlobalSettingsActivity globalSettingsActivity, SuspensionIntervalsDialog intervalDialog) {
        this.globalSettingsActivity = globalSettingsActivity;
        this.intervalDialog = intervalDialog;
    }

    public boolean synchronizeIntervals() {
        Log.d(IntervalHandler.class.getName(), "synchronizeIntervals");
        boolean didChanges = false;
        try {
            List<Interval> newIntervals = intervalDialog.getAdapter().getAllItems();
            List<Interval> dbIntervals = new ArrayList<>(globalSettingsActivity.getTimeBasedSuspensionScheduler().getIntervals());
            for (Interval interval : newIntervals) {
                Log.d(IntervalHandler.class.getName(), "Processing interval " + interval);
                if (interval.getId() < 0) {
                    insertInterval(interval);
                    didChanges = true;
                } else {
                    Interval dbInterval = findById(interval.getId(), dbIntervals);
                    Log.d(IntervalHandler.class.getName(), "Found dbInterval = " + dbInterval);
                    if (dbInterval != null) {
                        if (!interval.isEqual(dbInterval)) {
                            updateInterval(interval);
                            didChanges = true;
                        }
                        dbIntervals.remove(dbInterval);
                    } else {
                        Log.e(IntervalHandler.class.getName(), "No interval with id " + interval.getId() + " found");
                    }
                }
            }
            for (Interval interval : dbIntervals) {
                deleteInterval(interval);
                didChanges = true;
            }
        } catch (Exception exc) {
            Log.e(IntervalHandler.class.getName(), "Error synchronizing intervals.", exc);
            showErrorDialog(getResources().getString(R.string.text_dialog_general_error_synchronize_intervals));
        }
        return didChanges;
    }

    private void insertInterval(Interval interval) {
        Log.d(IntervalHandler.class.getName(), "insertInterval, interval = " + interval);
        IntervalDAO intervalDAO = new IntervalDAO(globalSettingsActivity);
        intervalDAO.insertInterval(interval);
    }

    private void updateInterval(Interval interval) {
        Log.d(IntervalHandler.class.getName(), "updateInterval, interval = " + interval);
        IntervalDAO intervalDAO = new IntervalDAO(globalSettingsActivity);
        intervalDAO.updateInterval(interval);
    }

    private void deleteInterval(Interval interval) {
        Log.d(IntervalHandler.class.getName(), "deleteInterval, interval = " + interval);
        IntervalDAO intervalDAO = new IntervalDAO(globalSettingsActivity);
        intervalDAO.deleteInterval(interval);
    }

    private Interval findById(long id, List<Interval> dbIntervals) {
        Log.d(IntervalHandler.class.getName(), "findById for id " + id);
        for (Interval interval : dbIntervals) {
            if (id == interval.getId()) {
                return interval;
            }
        }
        return null;
    }

    private void showErrorDialog(String errorMessage) {
        globalSettingsActivity.showErrorDialog(errorMessage);
    }

    private Resources getResources() {
        return globalSettingsActivity.getResources();
    }
}
