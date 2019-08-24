package de.ibba.keepitup.test.mock;

import android.content.Context;

import java.util.List;

import de.ibba.keepitup.model.LogEntry;
import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.ui.adapter.LogEntryAdapter;
import de.ibba.keepitup.ui.sync.LogEntryUIInitTask;

public class MockLogEntryUIInitTask extends LogEntryUIInitTask {

    private int startCalls;
    private boolean doCall;

    public MockLogEntryUIInitTask(Context context, LogEntryAdapter adapter) {
        super(context, adapter);
        startCalls = 0;
        doCall = true;
    }

    @Override
    public void start(NetworkTask task) {
        startCalls++;
        if (doCall) {
            onPreExecute();
            List<LogEntry> result = doInBackground(task);
            onPostExecute(result);
        }
    }

    public int getNumberStartCalls() {
        return startCalls;
    }

    public void setDoCall(boolean doCall) {
        this.doCall = doCall;
    }

    public void reset() {
        startCalls = 0;
        doCall = true;
    }

    public boolean wasStartCalled() {
        return startCalls > 0;
    }
}
