package net.ibbaa.keepitup.test.mock;

import android.content.Context;
import android.os.PowerManager;

import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.resources.WorkerFactory;
import net.ibbaa.keepitup.service.NetworkTaskWorker;

public class TestWorkerFactory implements WorkerFactory {

    @Override
    public NetworkTaskWorker createWorker(Context context, NetworkTask networkTask, PowerManager.WakeLock wakeLock) {
        return new TestNetworkTaskWorker(context, networkTask, wakeLock, true);
    }
}
