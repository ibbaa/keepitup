package de.ibba.keepitup.ui;

import android.content.res.Resources;
import android.util.Log;

import de.ibba.keepitup.R;
import de.ibba.keepitup.db.LogDAO;
import de.ibba.keepitup.db.NetworkTaskDAO;
import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.service.NetworkKeepAliveServiceScheduler;
import de.ibba.keepitup.ui.adapter.NetworkTaskAdapter;
import de.ibba.keepitup.ui.adapter.NetworkTaskUIWrapper;

class NetworkTaskHandler {

    private final NetworkTaskMainActivity mainActivity;

    public NetworkTaskHandler(NetworkTaskMainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public void startNetworkTask(NetworkTask task) {
        Log.d(NetworkTaskHandler.class.getName(), "startNetworkTask for task " + task);
        NetworkKeepAliveServiceScheduler scheduler = new NetworkKeepAliveServiceScheduler(mainActivity);
        try {
            scheduler.start(task);
        } catch (Exception exc) {
            Log.e(NetworkTaskHandler.class.getName(), "Error starting network task. Showing error dialog.", exc);
            showErrorDialog(getResources().getString(R.string.text_dialog_general_error_start_network_task));
        }
    }

    public void stopNetworkTask(NetworkTask task) {
        Log.d(NetworkTaskHandler.class.getName(), "stopNetworkTask for task " + task);
        NetworkKeepAliveServiceScheduler scheduler = new NetworkKeepAliveServiceScheduler(mainActivity);
        try {
            scheduler.stop(task);
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
            if (task.isRunning()) {
                Log.d(NetworkTaskHandler.class.getName(), "Network task is running. Restarting.");
                task = scheduler.stop(task);
                task = scheduler.start(task);
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
            if (task.isRunning()) {
                Log.d(NetworkTaskHandler.class.getName(), "Network task is running. Stopping.");
                task = scheduler.stop(task);
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
        return (NetworkTaskAdapter) mainActivity.getAdapter();
    }

    private Resources getResources() {
        return mainActivity.getResources();
    }
}
