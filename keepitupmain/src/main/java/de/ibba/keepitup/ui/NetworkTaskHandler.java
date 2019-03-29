package de.ibba.keepitup.ui;

import android.content.res.Resources;
import android.util.Log;

import de.ibba.keepitup.R;
import de.ibba.keepitup.db.LogDAO;
import de.ibba.keepitup.db.NetworkTaskDAO;
import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.service.NetworkKeepAliveServiceScheduler;
import de.ibba.keepitup.service.SchedulerIdGenerator;
import de.ibba.keepitup.ui.adapter.NetworkTaskAdapter;
import de.ibba.keepitup.ui.adapter.NetworkTaskUIWrapper;

class NetworkTaskHandler {

    private final NetworkTaskMainActivity mainActivity;

    public NetworkTaskHandler(NetworkTaskMainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public void startNetworkTask(NetworkTask networkTask) {
        Log.d(NetworkTaskHandler.class.getName(), "startNetworkTask");
        NetworkKeepAliveServiceScheduler scheduler = new NetworkKeepAliveServiceScheduler(mainActivity);
        NetworkTaskDAO dao = new NetworkTaskDAO(mainActivity);
        SchedulerIdGenerator idGenerator = new SchedulerIdGenerator(mainActivity);
        int schedulerId = idGenerator.createSchedulerId();
        try {
            dao.updateNetworkTaskSchedulerId(networkTask.getId(), schedulerId);
            networkTask.setSchedulerid(schedulerId);
            scheduler.start(networkTask);
        } catch (Exception exc) {
            Log.e(NetworkTaskHandler.class.getName(), "Error updating scheduler id to " + schedulerId + ". Showing error dialog.", exc);
            showErrorDialog(getResources().getString(R.string.text_dialog_general_error_start_network_task));
        }
    }

    public void stopNetworkTask(NetworkTask task) {
        Log.d(NetworkTaskHandler.class.getName(), "stopNetworkTask for task " + task);
        NetworkKeepAliveServiceScheduler scheduler = new NetworkKeepAliveServiceScheduler(mainActivity);
        NetworkTaskDAO dao = new NetworkTaskDAO(mainActivity);
        scheduler.stop(task);
        task.setSchedulerid(-1);
        try {
            dao.updateNetworkTaskSchedulerId(task.getId(), -1);
        } catch (Exception exc) {
            Log.e(NetworkTaskHandler.class.getName(), "Error updating scheduler id to -1", exc);
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
            NetworkTaskDAO dao = new NetworkTaskDAO(mainActivity);
            dao.updateNetworkTask(task);
            NetworkKeepAliveServiceScheduler scheduler = new NetworkKeepAliveServiceScheduler(mainActivity);
            if (scheduler.isRunning(task)) {
                Log.d(NetworkTaskHandler.class.getName(), "Network task is running. Restarting.");
                scheduler.stop(task);
                scheduler.start(task);
            }
            getAdapter().replaceItem(new NetworkTaskUIWrapper(task, null));
        } catch (Exception exc) {
            Log.e(NetworkTaskHandler.class.getName(), "Error updating task. Showing error dialog.", exc);
            showErrorDialog(getResources().getString(R.string.text_dialog_general_error_update_network_task));
        }
    }

    public void deleteNetworkTask(NetworkTask task) {
        Log.d(NetworkTaskHandler.class.getName(), "deleteNetworkTask for task " + task);
        try {
            NetworkTaskDAO networkTaskDAO = new NetworkTaskDAO(mainActivity);
            LogDAO logDAO = new LogDAO(mainActivity);
            NetworkKeepAliveServiceScheduler scheduler = new NetworkKeepAliveServiceScheduler(mainActivity);
            if (scheduler.isRunning(task)) {
                Log.d(NetworkTaskHandler.class.getName(), "Network task is running. Stopping.");
                scheduler.stop(task);
            }
            logDAO.deleteAllLogsForNetworkTask(task.getId());
            networkTaskDAO.deleteNetworkTask(task);
            getAdapter().removeItem(new NetworkTaskUIWrapper(task, null));
            getAdapter().notifyDataSetChanged();
        } catch (Exception exc) {
            Log.e(NetworkTaskHandler.class.getName(), "Error deleting network task.", exc);
            showErrorDialog(getResources().getString(R.string.text_dialog_general_error_delete_network_task));
        }
    }

    private void showErrorDialog(String errorMessage) {
        mainActivity.showErrorDialog(errorMessage);
    }

    private NetworkTaskAdapter getAdapter() {
        return mainActivity.getAdapter();
    }

    private Resources getResources() {
        return mainActivity.getResources();
    }
}
