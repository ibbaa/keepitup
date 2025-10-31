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
import net.ibbaa.keepitup.ui.sync.DBSyncHandler;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ClassCanBeRecord")
public class IntervalHandler {

    private final GlobalSettingsActivity globalSettingsActivity;
    private final SuspensionIntervalsDialog intervalDialog;

    public IntervalHandler(GlobalSettingsActivity globalSettingsActivity, SuspensionIntervalsDialog intervalDialog) {
        this.globalSettingsActivity = globalSettingsActivity;
        this.intervalDialog = intervalDialog;
    }

    public boolean synchronizeIntervals() {
        Log.d(IntervalHandler.class.getName(), "synchronizeIntervals");
        try {
            List<Interval> newIntervals = intervalDialog.getAdapter().getAllItems();
            List<Interval> dbIntervals = new ArrayList<>(globalSettingsActivity.getTimeBasedSuspensionScheduler().getIntervals());
            DBSyncHandler<Interval> syncHandler = new DBSyncHandler<>();
            List<DBSyncHandler.ActionWrapper<Interval>> intervalActions = syncHandler.retrieveSyncList(newIntervals, dbIntervals);
            for (DBSyncHandler.ActionWrapper<Interval> actionWrapper : intervalActions) {
                if (DBSyncHandler.Action.INSERT.equals(actionWrapper.action())) {
                    insertInterval(actionWrapper.object());
                } else if (DBSyncHandler.Action.UPDATE.equals(actionWrapper.action())) {
                    updateInterval(actionWrapper.object());
                } else if (DBSyncHandler.Action.DELETE.equals(actionWrapper.action())) {
                    deleteInterval(actionWrapper.object());
                } else {
                    Log.e(IntervalHandler.class.getName(), "Unknown action " + actionWrapper.action());
                }
            }
            return !intervalActions.isEmpty();
        } catch (Exception exc) {
            Log.e(IntervalHandler.class.getName(), "Error synchronizing intervals.", exc);
            showMessageDialog(getResources().getString(R.string.text_dialog_general_message_synchronize_intervals));
        }
        return false;
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

    private void showMessageDialog(String errorMessage) {
        globalSettingsActivity.showMessageDialog(errorMessage);
    }

    private Resources getResources() {
        return globalSettingsActivity.getResources();
    }
}
