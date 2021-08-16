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
            showErrorDialog(getResources().getString(R.string.text_dialog_general_error_delete_logs));
        }
    }

    private void showErrorDialog(String errorMessage) {
        logActivity.showErrorDialog(errorMessage);
    }

    private LogEntryAdapter getAdapter() {
        return (LogEntryAdapter) logActivity.getAdapter();
    }

    private Resources getResources() {
        return logActivity.getResources();
    }
}
