/*
 * Copyright (c) 2022. Alwin Ibba
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
import net.ibbaa.keepitup.db.NetworkTaskDAO;
import net.ibbaa.keepitup.db.SchedulerIdGenerator;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.service.NetworkTaskProcessServiceScheduler;
import net.ibbaa.keepitup.service.log.NetworkTaskLog;
import net.ibbaa.keepitup.ui.adapter.NetworkTaskAdapter;
import net.ibbaa.keepitup.ui.adapter.NetworkTaskUIWrapper;

class NetworkTaskHandler {

    private final NetworkTaskMainActivity mainActivity;
    private final NetworkTaskProcessServiceScheduler scheduler;

    public NetworkTaskHandler(NetworkTaskMainActivity mainActivity) {
        this.mainActivity = mainActivity;
        this.scheduler = new NetworkTaskProcessServiceScheduler(mainActivity);
    }

    public void startNetworkTask(NetworkTask task) {
        Log.d(NetworkTaskHandler.class.getName(), "startNetworkTask for task " + task);
        try {
            scheduler.schedule(task);
            getAdapter().replaceNetworkTask(task);
        } catch (Exception exc) {
            Log.e(NetworkTaskHandler.class.getName(), "Error starting network task. Showing error dialog.", exc);
            showErrorDialog(getResources().getString(R.string.text_dialog_general_error_start_network_task));
        }
    }

    public void stopNetworkTask(NetworkTask task) {
        Log.d(NetworkTaskHandler.class.getName(), "stopNetworkTask for task " + task);
        try {
            scheduler.cancel(task);
            getAdapter().replaceNetworkTask(task);
        } catch (Exception exc) {
            Log.e(NetworkTaskHandler.class.getName(), "Error stopping network task. Showing error dialog.", exc);
            showErrorDialog(getResources().getString(R.string.text_dialog_general_error_stop_network_task));
        }
    }

    public void insertNetworkTask(NetworkTask task) {
        Log.d(NetworkTaskHandler.class.getName(), "insertNetworkTask for task " + task);
        int index = getAdapter().getNextIndex();
        task.setIndex(index);
        try {
            NetworkTaskDAO dao = new NetworkTaskDAO(mainActivity);
            task = dao.insertNetworkTask(task);
            if (task.getId() < 0) {
                Log.e(NetworkTaskHandler.class.getName(), "Error inserting task into database. Showing error dialog.");
                mainActivity.showErrorDialog(getResources().getString(R.string.text_dialog_general_error_insert_network_task));
            } else {
                LogDAO logDAO = new LogDAO(mainActivity);
                logDAO.deleteAllLogsForNetworkTask(task.getId());
                getAdapter().addItem(new NetworkTaskUIWrapper(task, null));
            }
        } catch (Exception exc) {
            Log.e(NetworkTaskHandler.class.getName(), "Error inserting task into database. Showing error dialog.", exc);
            showErrorDialog(getResources().getString(R.string.text_dialog_general_error_insert_network_task));
        }
    }

    public void updateNetworkTask(NetworkTask task) {
        Log.d(NetworkTaskHandler.class.getName(), "updateNetworkTask for task " + task);
        try {
            boolean running = task.isRunning();
            if (running) {
                Log.d(NetworkTaskHandler.class.getName(), "Network task is running. Cancelling.");
                task = scheduler.cancel(task);
            }
            NetworkTaskDAO dao = new NetworkTaskDAO(mainActivity);
            task = dao.updateNetworkTask(task);
            if (task.getSchedulerId() == SchedulerIdGenerator.ERROR_SCHEDULER_ID) {
                Log.e(NetworkTaskHandler.class.getName(), "Error updating task. Showing error dialog.");
                mainActivity.showErrorDialog(getResources().getString(R.string.text_dialog_general_error_update_network_task));
                return;
            }
            if (running) {
                Log.d(NetworkTaskHandler.class.getName(), "Network task is running. Restarting.");
                task = scheduler.schedule(task);
            }
            getAdapter().replaceNetworkTask(task);
        } catch (Exception exc) {
            Log.e(NetworkTaskHandler.class.getName(), "Error updating task. Showing error dialog.", exc);
            showErrorDialog(getResources().getString(R.string.text_dialog_general_error_update_network_task));
        } finally {
            NetworkTaskLog.clear();
        }
    }

    public void deleteNetworkTask(NetworkTask task) {
        Log.d(NetworkTaskHandler.class.getName(), "deleteNetworkTask for task " + task);
        try {
            NetworkTaskDAO networkTaskDAO = new NetworkTaskDAO(mainActivity);
            LogDAO logDAO = new LogDAO(mainActivity);
            if (task.isRunning()) {
                Log.d(NetworkTaskHandler.class.getName(), "Network task is running. Stopping.");
                task = scheduler.cancel(task);
            }
            logDAO.deleteAllLogsForNetworkTask(task.getId());
            networkTaskDAO.deleteNetworkTask(task);
            getAdapter().removeItem(new NetworkTaskUIWrapper(task, null));
        } catch (Exception exc) {
            Log.e(NetworkTaskHandler.class.getName(), "Error deleting network task.", exc);
            showErrorDialog(getResources().getString(R.string.text_dialog_general_error_delete_network_task));
        } finally {
            NetworkTaskLog.clear();
        }
    }

    private void showErrorDialog(String errorMessage) {
        mainActivity.showErrorDialog(errorMessage);
    }

    private NetworkTaskAdapter getAdapter() {
        return (NetworkTaskAdapter) mainActivity.getAdapter();
    }

    private Resources getResources() {
        return mainActivity.getResources();
    }
}
