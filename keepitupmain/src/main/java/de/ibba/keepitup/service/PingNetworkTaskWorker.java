package de.ibba.keepitup.service;

import android.content.Context;
import android.os.PowerManager;
import android.util.Log;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import de.ibba.keepitup.R;
import de.ibba.keepitup.model.LogEntry;
import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.resources.PreferenceManager;
import de.ibba.keepitup.service.network.PingCommand;
import de.ibba.keepitup.service.network.PingCommandResult;
import de.ibba.keepitup.util.ExceptionUtil;

public class PingNetworkTaskWorker extends NetworkTaskWorker {

    public PingNetworkTaskWorker(Context context, NetworkTask networkTask, PowerManager.WakeLock wakeLock) {
        super(context, networkTask, wakeLock);
    }

    @Override
    public LogEntry execute(NetworkTask networkTask) {
        Log.d(PingNetworkTaskWorker.class.getName(), "Executing PingNetworkTaskWorker for " + networkTask);
        LogEntry logEntry = new LogEntry();
        logEntry.setNetworkTaskId(networkTask.getId());
        logEntry.setTimestamp(System.currentTimeMillis());
        Callable<PingCommandResult> pingCommand = getPingCommand(networkTask);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        try {
            PreferenceManager preferenceManager = new PreferenceManager(getContext());
            int count = preferenceManager.getPreferencePingCount();
            int timeout = getResources().getInteger(R.integer.ping_timeout) * count * 2;
            Log.d(PingNetworkTaskWorker.class.getName(), "Executing " + pingCommand.getClass().getSimpleName() + " with a timeout of " + timeout);
            Future<PingCommandResult> pingResultFuture = executorService.submit(pingCommand);
            PingCommandResult pingResult = pingResultFuture.get(timeout, TimeUnit.SECONDS);
            Log.d(PingNetworkTaskWorker.class.getName(), pingCommand.getClass().getSimpleName() + " returned " + pingResult);
            if (pingResult.getException() == null && pingResult.getProcessReturnCode() == 0) {
                Log.d(PingNetworkTaskWorker.class.getName(), "Ping was successful");
                logEntry.setSuccess(true);
                logEntry.setMessage(pingResult.getOutput());
            } else if (pingResult.getException() != null) {
                Log.d(PingNetworkTaskWorker.class.getName(), "Ping was not successful because of an exception", pingResult.getException());
                logEntry.setSuccess(false);
                logEntry.setMessage(getMessageFromException(pingResult.getException()));
            } else {
                Log.d(PingNetworkTaskWorker.class.getName(), "Ping was not successful because the ping command returned " + pingResult.getProcessReturnCode());
                logEntry.setSuccess(false);
                logEntry.setMessage(pingResult.getOutput());
            }
        } catch (Throwable exc) {
            Log.d(PingNetworkTaskWorker.class.getName(), "Error executing " + pingCommand.getClass().getName(), exc);
            logEntry.setSuccess(false);
            logEntry.setMessage(getMessageFromException(exc));
        } finally {
            Log.d(PingNetworkTaskWorker.class.getName(), "Shutting down ExecutorService");
            executorService.shutdownNow();
        }
        Log.d(PingNetworkTaskWorker.class.getName(), "Returning " + logEntry);
        return logEntry;
    }

    private String getMessageFromException(Throwable exc) {
        return ExceptionUtil.getLogableMessage(ExceptionUtil.getRootCause(exc));
    }

    protected Callable<PingCommandResult> getPingCommand(NetworkTask networkTask) {
        return new PingCommand(getContext(), networkTask);
    }
}
