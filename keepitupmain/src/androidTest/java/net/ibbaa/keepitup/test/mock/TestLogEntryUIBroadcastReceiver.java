package net.ibbaa.keepitup.test.mock;


import android.app.Activity;

import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.ui.adapter.LogEntryAdapter;
import net.ibbaa.keepitup.ui.sync.LogEntryUIBroadcastReceiver;

public class TestLogEntryUIBroadcastReceiver extends LogEntryUIBroadcastReceiver {

    private boolean doSyncCalled;

    public TestLogEntryUIBroadcastReceiver(Activity activity, LogEntryAdapter adapter) {
        super(activity, adapter);
        doSyncCalled = false;
    }

    public void reset() {
        doSyncCalled = false;
    }

    public boolean wasDoSyncCalled() {
        return doSyncCalled;
    }

    @Override
    protected void doSync(NetworkTask task) {
        doSyncCalled = true;
    }
}
