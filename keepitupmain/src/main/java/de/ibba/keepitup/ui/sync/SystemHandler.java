package de.ibba.keepitup.ui.sync;

import android.os.Handler;
import android.util.Log;

public class SystemHandler implements IHandler {

    private final Handler handler;

    public SystemHandler() {
        handler = new Handler();
    }

    @Override
    public boolean start(Runnable runnable) {
        Log.d(SystemHandler.class.getName(), "Posting runnable for immediate execution");
        return handler.post(runnable);
    }

    @Override
    public boolean startDelayed(Runnable runnable, long delay) {
        Log.d(SystemHandler.class.getName(), "Posting runnable for execution with a delay of " + delay);
        return handler.postDelayed(runnable, delay);
    }

    @Override
    public void stop(Runnable runnable) {
        Log.d(SystemHandler.class.getName(), "Removing callbacks for runnable");
        handler.removeCallbacks(runnable);
    }
}
