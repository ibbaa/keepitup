package net.ibbaa.keepitup.ui.sync;

import android.app.Activity;

import java.lang.ref.WeakReference;
import java.util.concurrent.Callable;

public abstract class UIBackgroundTask<T> implements Callable<T> {

    private final WeakReference<Activity> activityRef;

    public UIBackgroundTask(Activity activity) {
        this.activityRef = new WeakReference<>(activity);
    }

    @Override
    public T call() {
        T result = runInBackground();
        Activity activity = getActivity();
        if (activity != null) {
            activity.runOnUiThread(() -> runOnUIThread(result));
        }
        return result;
    }

    protected abstract T runInBackground();

    protected abstract void runOnUIThread(T t);

    protected Activity getActivity() {
        return activityRef.get();
    }
}
