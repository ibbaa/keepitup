package net.ibbaa.keepitup.test.mock;

import android.app.Activity;

import net.ibbaa.keepitup.ui.sync.DBPurgeTask;

public class MockDBPurgeTask extends DBPurgeTask {

    private final boolean result;

    public MockDBPurgeTask(Activity activity, boolean result) {
        super(activity);
        this.result = result;
    }

    @Override
    protected Boolean runInBackground() {
        return result;
    }
}
