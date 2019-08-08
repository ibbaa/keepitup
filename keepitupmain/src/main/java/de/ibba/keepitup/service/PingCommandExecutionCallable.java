package de.ibba.keepitup.service;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.google.common.base.Charsets;

import java.util.concurrent.Callable;

import de.ibba.keepitup.R;
import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.resources.PreferenceManager;
import de.ibba.keepitup.util.StreamUtil;

public class PingCommandExecutionCallable implements Callable<PingCommandResult> {

    private final Context context;
    private final NetworkTask networkTask;

    public PingCommandExecutionCallable(Context context, NetworkTask networkTask) {
        this.context = context;
        this.networkTask = networkTask;
    }

    @Override
    public PingCommandResult call() {
        Log.d(PingCommandExecutionCallable.class.getName(), "call");
        String output = null;
        int returnCode = -1;
        Process process = null;
        try {
            PreferenceManager preferenceManager = new PreferenceManager(context);
            Runtime runtime = Runtime.getRuntime();
            String command = getResources().getString(R.string.ping_command_line);
            int count = preferenceManager.getPreferencePingCount();
            int timeout = getResources().getInteger(R.integer.ping_timeout);
            String host = networkTask.getAddress();
            String formattedCommand = String.format(command, count, timeout, host);
            Log.d(PingCommandExecutionCallable.class.getName(), "Executing ping command: " + formattedCommand);
            process = runtime.exec(formattedCommand);
            output = StreamUtil.inputStreamToString(process.getInputStream(), Charsets.US_ASCII);
            Log.d(PingCommandExecutionCallable.class.getName(), "Ping output: " + output);
            returnCode = process.waitFor();
            Log.d(PingCommandExecutionCallable.class.getName(), "Ping proccess return code: " + returnCode);
            return new PingCommandResult(returnCode, output, null);
        } catch (Exception exc) {
            Log.e(PingCommandExecutionCallable.class.getName(), "Error executing ping command", exc);
            return new PingCommandResult(returnCode, output, exc);
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
    }

    public Context getContext() {
        return context;
    }

    public Resources getResources() {
        return getContext().getResources();
    }
}
