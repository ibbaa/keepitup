package de.ibba.keepitup.test.mock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.ibba.keepitup.ui.adapter.NetworkTaskUIWrapper;
import de.ibba.keepitup.ui.sync.UISyncAsyncTask;
import de.ibba.keepitup.ui.sync.UISyncHolder;

public class MockUISyncAsyncTask extends UISyncAsyncTask {

    private final List<StartCall> startCalls;

    private boolean doCall;

    public MockUISyncAsyncTask() {
        startCalls = new ArrayList<>();
        doCall = true;
    }

    @Override
    public void start(UISyncHolder uiSyncHolder) {
        startCalls.add(new StartCall(uiSyncHolder));
        if (doCall) {
            onPreExecute();
            List<NetworkTaskUIWrapper> result = doInBackground(uiSyncHolder);
            onPostExecute(result);
        }
    }

    public List<StartCall> getStartCalls() {
        return Collections.unmodifiableList(startCalls);
    }

    public void setDoCall(boolean doCall) {
        this.doCall = doCall;
    }

    public void reset() {
        startCalls.clear();
    }

    public boolean wasStartCalled() {
        return !startCalls.isEmpty();
    }

    public static class StartCall {

        private final UISyncHolder uiSyncHolder;

        public StartCall(UISyncHolder uiSyncHolder) {
            this.uiSyncHolder = uiSyncHolder;
        }

        public UISyncHolder getUiSyncHolder() {
            return uiSyncHolder;
        }
    }
}
