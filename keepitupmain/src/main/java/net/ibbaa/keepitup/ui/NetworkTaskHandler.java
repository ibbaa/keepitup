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
import net.ibbaa.keepitup.db.AccessTypeDataDAO;
import net.ibbaa.keepitup.db.LogDAO;
import net.ibbaa.keepitup.db.NetworkTaskDAO;
import net.ibbaa.keepitup.db.SchedulerIdGenerator;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.logging.NetworkTaskLog;
import net.ibbaa.keepitup.model.AccessTypeData;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.service.NetworkTaskProcessServiceScheduler;
import net.ibbaa.keepitup.ui.adapter.NetworkTaskAdapter;
import net.ibbaa.keepitup.ui.adapter.NetworkTaskUIWrapper;

class NetworkTaskHandler {

    private final NetworkTaskMainActivity mainActivity;
    private final NetworkTaskProcessServiceScheduler scheduler;

    public NetworkTaskHandler(NetworkTaskMainActivity mainActivity) {
        this.mainActivity = mainActivity;
        this.scheduler = new NetworkTaskProcessServiceScheduler(mainActivity);
    }

    public void startNetworkTask(NetworkTask task, AccessTypeData data) {
        Log.d(NetworkTaskHandler.class.getName(), "startNetworkTask for task " + task + " and access type data " + data);
        try {
            scheduler.start(task);
            getAdapter().replaceNetworkTask(task, data);
        } catch (Exception exc) {
            Log.e(NetworkTaskHandler.class.getName(), "Error starting network task. Showing error dialog.", exc);
            showMessageDialog(getResources().getString(R.string.text_dialog_general_message_start_network_task));
        }
    }

    public void stopNetworkTask(NetworkTask task, AccessTypeData data) {
        Log.d(NetworkTaskHandler.class.getName(), "stopNetworkTask for task " + task + " and access type data " + data);
        try {
            scheduler.cancel(task);
            getAdapter().replaceNetworkTask(task, data);
        } catch (Exception exc) {
            Log.e(NetworkTaskHandler.class.getName(), "Error stopping network task. Showing error dialog.", exc);
            showMessageDialog(getResources().getString(R.string.text_dialog_general_message_stop_network_task));
        }
    }

    public void insertNetworkTask(NetworkTask task, AccessTypeData data) {
        Log.d(NetworkTaskHandler.class.getName(), "insertNetworkTask for task " + task + " and access type data " + data);
        int index = getAdapter().getNextIndex();
        task.setIndex(index);
        try {
            NetworkTaskDAO dao = new NetworkTaskDAO(mainActivity);
            task = dao.insertNetworkTask(task);
            if (task.getId() < 0) {
                Log.e(NetworkTaskHandler.class.getName(), "Error inserting task into database. Showing error dialog.");
                mainActivity.showMessageDialog(getResources().getString(R.string.text_dialog_general_message_insert_network_task));
            } else {
                AccessTypeDataDAO accessTypeDataDAO = new AccessTypeDataDAO(mainActivity);
                data.setNetworkTaskId(task.getId());
                accessTypeDataDAO.insertAccessTypeData(data);
                LogDAO logDAO = new LogDAO(mainActivity);
                logDAO.deleteAllLogsForNetworkTask(task.getId());
                getAdapter().addItem(new NetworkTaskUIWrapper(task, data, null));
            }
        } catch (Exception exc) {
            Log.e(NetworkTaskHandler.class.getName(), "Error inserting task into database. Showing error dialog.", exc);
            showMessageDialog(getResources().getString(R.string.text_dialog_general_message_insert_network_task));
        }
    }

    public void updateNetworkTaskName(NetworkTask task, AccessTypeData data, String name) {
        Log.d(NetworkTaskHandler.class.getName(), "updateNetworkTask for task " + task + " and name " + name);
        try {
            NetworkTaskDAO networkTaskDAO = new NetworkTaskDAO(mainActivity);
            networkTaskDAO.updateNetworkTaskName(task.getId(), name);
            task.setName(name);
            getAdapter().replaceNetworkTask(task, data);
        } catch (Exception exc) {
            Log.e(NetworkTaskHandler.class.getName(), "Error updating task name. Showing error dialog.", exc);
            showMessageDialog(getResources().getString(R.string.text_dialog_general_message_update_network_task));
        } finally {
            NetworkTaskLog.clear();
        }
    }

    public void updateNetworkTask(NetworkTask task, AccessTypeData data) {
        Log.d(NetworkTaskHandler.class.getName(), "updateNetworkTask for task " + task + " and access type data " + data);
        try {
            boolean running = task.isRunning();
            if (running) {
                Log.d(NetworkTaskHandler.class.getName(), "Network task is running. Cancelling.");
                task = scheduler.cancel(task);
            }
            NetworkTaskDAO networkTaskDAO = new NetworkTaskDAO(mainActivity);
            task = networkTaskDAO.updateNetworkTask(task);
            if (task.getSchedulerId() == SchedulerIdGenerator.ERROR_SCHEDULER_ID) {
                Log.e(NetworkTaskHandler.class.getName(), "Error updating task. Showing error dialog.");
                mainActivity.showMessageDialog(getResources().getString(R.string.text_dialog_general_message_update_network_task));
                return;
            }
            AccessTypeDataDAO accessTypeDataDAO = new AccessTypeDataDAO(mainActivity);
            data.setNetworkTaskId(task.getId());
            data = accessTypeDataDAO.updateAccessTypeData(data);
            if (running) {
                Log.d(NetworkTaskHandler.class.getName(), "Network task is running. Restarting.");
                task = scheduler.start(task);
            }
            getAdapter().replaceNetworkTask(task, data);
        } catch (Exception exc) {
            Log.e(NetworkTaskHandler.class.getName(), "Error updating task. Showing error dialog.", exc);
            showMessageDialog(getResources().getString(R.string.text_dialog_general_message_update_network_task));
        } finally {
            NetworkTaskLog.clear();
        }
    }

    public void deleteNetworkTask(NetworkTask task) {
        Log.d(NetworkTaskHandler.class.getName(), "deleteNetworkTask for task " + task);
        try {
            NetworkTaskDAO networkTaskDAO = new NetworkTaskDAO(mainActivity);
            AccessTypeDataDAO accessTypeDataDAO = new AccessTypeDataDAO(mainActivity);
            LogDAO logDAO = new LogDAO(mainActivity);
            if (task.isRunning()) {
                Log.d(NetworkTaskHandler.class.getName(), "Network task is running. Stopping.");
                task = scheduler.cancel(task);
            }
            logDAO.deleteAllLogsForNetworkTask(task.getId());
            accessTypeDataDAO.deleteAccessTypeDataForNetworkTask(task.getId());
            networkTaskDAO.deleteNetworkTask(task);
            getAdapter().removeItem(new NetworkTaskUIWrapper(task, null, null));
        } catch (Exception exc) {
            Log.e(NetworkTaskHandler.class.getName(), "Error deleting network task.", exc);
            showMessageDialog(getResources().getString(R.string.text_dialog_general_message_delete_network_task));
        } finally {
            NetworkTaskLog.clear();
        }
    }

    private void showMessageDialog(String errorMessage) {
        mainActivity.showMessageDialog(errorMessage);
    }

    private NetworkTaskAdapter getAdapter() {
        return (NetworkTaskAdapter) mainActivity.getAdapter();
    }

    private Resources getResources() {
        return mainActivity.getResources();
    }
}
