package de.ibba.keepitup.test.mock;

import android.content.Context;

import java.util.List;

import de.ibba.keepitup.ui.adapter.NetworkTaskAdapter;
import de.ibba.keepitup.ui.adapter.NetworkTaskUIWrapper;
import de.ibba.keepitup.ui.sync.NetworkTaskMainUIInitTask;

public class MockNetworkTaskMainUIInitTask extends NetworkTaskMainUIInitTask {

    private int startCalls;
    private boolean doCall;

    public MockNetworkTaskMainUIInitTask(Context context, NetworkTaskAdapter adapter) {
        super(context, adapter);
        startCalls = 0;
        doCall = true;
    }

    @Override
    public void start() {
        startCalls++;
        if (doCall) {
            onPreExecute();
            List<NetworkTaskUIWrapper> result = doInBackground();
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
