package de.ibba.keepitup.service;

import android.content.Context;
import android.os.PowerManager;
import android.util.Log;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import de.ibba.keepitup.model.LogEntry;
import de.ibba.keepitup.model.NetworkTask;

public class PingNetworkTaskWorker extends NetworkTaskWorker {

    public PingNetworkTaskWorker(Context context, NetworkTask networkTask, PowerManager.WakeLock wakeLock) {
        super(context, networkTask, wakeLock);
    }

    @Override
    public LogEntry execute(NetworkTask networkTask) {
        Log.d(PingNetworkTaskWorker.class.getName(), "Executing PingNetworkTaskWorker for " + networkTask);
        LogEntry logEntry = new LogEntry();
        logEntry.setNetworkTaskId(networkTask.getId());
        logEntry.setSuccess(true);
        logEntry.setTimestamp(System.currentTimeMillis());

        try {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            Future<PingCommandResult> pingResultFuture = executorService.submit(getPingCommandExecutionCallable(networkTask));
            PingCommandResult pingResult = pingResultFuture.get();
            logEntry.setMessage(pingResult.getOutput());
            executorService.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return logEntry;
    }

    protected Callable<PingCommandResult> getPingCommandExecutionCallable(NetworkTask networkTask) {
        return new PingCommandExecutionCallable(getContext(), networkTask);
    }
}
