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
import net.ibbaa.keepitup.db.LogDAO;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.ui.adapter.LogEntryAdapter;

public class LogHandler {

    private final NetworkTaskLogActivity logActivity;

    public LogHandler(NetworkTaskLogActivity logActivity) {
        this.logActivity = logActivity;
    }

    public void deleteLogsForNetworkTask(NetworkTask task) {
        Log.d(LogHandler.class.getName(), "deleteLogsForNetworkTask for task " + task);
        try {
            LogDAO logDAO = new LogDAO(logActivity);
            logDAO.deleteAllLogsForNetworkTask(task.getId());
            getAdapter().removeItems();
        } catch (Exception exc) {
            Log.e(NetworkTaskHandler.class.getName(), "Error deleting logs.", exc);
            showMessageDialog(getResources().getString(R.string.text_dialog_general_message_delete_logs));
        }
    }

    private void showMessageDialog(String errorMessage) {
        logActivity.showMessageDialog(errorMessage);
    }

    private LogEntryAdapter getAdapter() {
        return (LogEntryAdapter) logActivity.getAdapter();
    }

    private Resources getResources() {
        return logActivity.getResources();
    }
}
