package de.ibba.keepitup.ui.sync;

import android.os.Handler;
import android.util.Log;

import de.ibba.keepitup.R;
import de.ibba.keepitup.db.LogDAO;
import de.ibba.keepitup.ui.adapter.NetworkTaskAdapter;

public class UISyncController {

    private static final Handler handler = new Handler();
    private static Runnable runnable;
    private static NetworkTaskAdapter adapter;

    public static void start(NetworkTaskAdapter adapter) {
        Log.d(UISyncController.class.getName(), "starting UI sync");
        if (isRunning()) {
            stop();
        }
        UISyncController.adapter = adapter;
        UISyncController.runnable = new Runnable() {
            @Override
            public void run() {
                LogDAO logDAO = new LogDAO(UISyncController.adapter.getContext());
                UISyncAsyncTask uiSyncAsyncTask = new UISyncAsyncTask();
                uiSyncAsyncTask.execute(new UISyncHolder(adapter.getAllItems(), adapter, logDAO));
                long refreshInterval = UISyncController.adapter.getResources().getInteger(R.integer.ui_sync_refresh_interval);
                Log.d(UISyncController.class.getName(), "loaded refreshInterval setting " + refreshInterval);
                UISyncController.refresh(this, refreshInterval);
            }
        };
        Log.d(UISyncController.class.getName(), "Starting...");
        handler.post(runnable);
    }

    private static synchronized void refresh(Runnable runnable, long refreshInterval) {
        Log.d(UISyncController.class.getName(), "refreshing ui sync with interval of " + refreshInterval);
        UISyncController.runnable = runnable;
        handler.postDelayed(runnable, refreshInterval * 1000);
    }

    public static synchronized void stop() {
        Log.d(UISyncController.class.getName(), "stopping UI sync");
        if (isRunning()) {
            Log.d(UISyncController.class.getName(), "Stopping...");
            handler.removeCallbacks(runnable);
            runnable = null;
            adapter = null;
        } else {
            Log.d(UISyncController.class.getName(), "Not running.");
        }
    }

    public static synchronized boolean isRunning() {
        Log.d(UISyncController.class.getName(), "ui sync running: " + (UISyncController.runnable != null));
        return runnable != null;
    }
}
