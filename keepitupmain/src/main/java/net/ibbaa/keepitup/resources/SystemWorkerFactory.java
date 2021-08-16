package net.ibbaa.keepitup.resources;

import android.content.Context;
import android.os.PowerManager;

import java.lang.reflect.Constructor;

import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.model.AccessType;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.service.NetworkTaskWorker;
import net.ibbaa.keepitup.service.NullNetworkTaskWorker;

public class SystemWorkerFactory implements WorkerFactory {

    @Override
    public NetworkTaskWorker createWorker(Context context, NetworkTask networkTask, PowerManager.WakeLock wakeLock) {
        AccessType accessType = networkTask.getAccessType();
        Log.d(SystemWorkerFactory.class.getName(), "createWorker for access type " + accessType);
        if (accessType == null) {
            Log.d(SystemWorkerFactory.class.getName(), "returning NullNetworkTaskWorker");
            return new NullNetworkTaskWorker(context, networkTask, wakeLock);
        }
        String workerClassName = context.getResources().getString(context.getResources().getIdentifier(accessType.getClass().getSimpleName() + "_" + accessType.name() + "_worker", "string", context.getPackageName()));
        Log.d(SystemWorkerFactory.class.getName(), "specified worker class is " + workerClassName);
        try {
            Class<?> validatorClass = context.getClassLoader().loadClass(workerClassName);
            Constructor<?> workerClassConstructor = validatorClass.getConstructor(Context.class, NetworkTask.class, PowerManager.WakeLock.class);
            return (NetworkTaskWorker) workerClassConstructor.newInstance(context, networkTask, wakeLock);
        } catch (Throwable exc) {
            Log.e(SystemWorkerFactory.class.getName(), "Error instantiating worker class", exc);
        }
        Log.d(SystemWorkerFactory.class.getName(), "returning NullValidator");
        return new NullNetworkTaskWorker(context, networkTask, wakeLock);
    }
}
