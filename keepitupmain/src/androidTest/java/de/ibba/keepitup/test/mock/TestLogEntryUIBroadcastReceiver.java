package de.ibba.keepitup.test.mock;


import android.content.Context;

import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.ui.adapter.LogEntryAdapter;
import de.ibba.keepitup.ui.sync.LogEntryUIBroadcastReceiver;

public class TestLogEntryUIBroadcastReceiver extends LogEntryUIBroadcastReceiver {

    private boolean doSyncCalled;

    public TestLogEntryUIBroadcastReceiver(LogEntryAdapter adapter) {
        super(adapter);
        doSyncCalled = false;
    }

    public void reset() {
        doSyncCalled = false;
    }

    public boolean wasDoSyncCalled() {
        return doSyncCalled;
    }

    @Override
    protected void doSync(Context context, NetworkTask task) {
        doSyncCalled = true;
    }
}
