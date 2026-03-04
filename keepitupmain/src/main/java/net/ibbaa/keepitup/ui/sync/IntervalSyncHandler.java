/*
 * Copyright (c) 2026 Alwin Ibba
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

package net.ibbaa.keepitup.ui.sync;

import android.content.Context;

import net.ibbaa.keepitup.db.IntervalDAO;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.model.Interval;

import java.util.List;

public class IntervalSyncHandler {

    private final Context context;

    public IntervalSyncHandler(Context context) {
        this.context = context;
    }

    public DBSyncResult synchronizeIntervals(List<Interval> newIntervals, List<Interval> dbIntervals) {
        Log.d(IntervalSyncHandler.class.getName(), "synchronizeIntervals");
        try {
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
                    Log.e(IntervalSyncHandler.class.getName(), "Unknown action " + actionWrapper.action());
                }
            }
            return new DBSyncResult(true, !intervalActions.isEmpty());
        } catch (Exception exc) {
            Log.e(IntervalSyncHandler.class.getName(), "Error synchronizing intervals.", exc);
            return new DBSyncResult(false, true);
        }
    }

    private void insertInterval(Interval interval) {
        Log.d(IntervalSyncHandler.class.getName(), "insertInterval, interval = " + interval);
        IntervalDAO intervalDAO = new IntervalDAO(context);
        intervalDAO.insertInterval(interval);
    }

    private void updateInterval(Interval interval) {
        Log.d(IntervalSyncHandler.class.getName(), "updateInterval, interval = " + interval);
        IntervalDAO intervalDAO = new IntervalDAO(context);
        intervalDAO.updateInterval(interval);
    }

    private void deleteInterval(Interval interval) {
        Log.d(IntervalSyncHandler.class.getName(), "deleteInterval, interval = " + interval);
        IntervalDAO intervalDAO = new IntervalDAO(context);
        intervalDAO.deleteInterval(interval);
    }
}
