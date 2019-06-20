package de.ibba.keepitup.resources;

import android.content.Context;
import android.os.PowerManager;

import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.service.NetworkTaskWorker;

public interface WorkerFactory {

    NetworkTaskWorker createWorker(Context context, NetworkTask networkTask, PowerManager.WakeLock wakeLock);
}
