package de.ibba.keepitup.ui;

import android.content.res.Resources;

import de.ibba.keepitup.R;
import de.ibba.keepitup.db.LogDAO;
import de.ibba.keepitup.logging.Log;
import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.ui.adapter.LogEntryAdapter;

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
