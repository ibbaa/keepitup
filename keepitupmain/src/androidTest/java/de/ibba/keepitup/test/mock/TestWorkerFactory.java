package de.ibba.keepitup.test.mock;

import android.content.Context;
import android.os.PowerManager;

import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.resources.WorkerFactory;
import de.ibba.keepitup.service.NetworkTaskWorker;

public class TestWorkerFactory implements WorkerFactory {

    @Override
    public NetworkTaskWorker createWorker(Context context, NetworkTask networkTask, PowerManager.WakeLock wakeLock) {
        return new TestNetworkTaskWorker(context, networkTask, wakeLock, true);
    }
}
