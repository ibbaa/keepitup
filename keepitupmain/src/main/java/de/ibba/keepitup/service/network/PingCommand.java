package de.ibba.keepitup.service.network;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.google.common.base.Charsets;

import java.util.concurrent.Callable;

import de.ibba.keepitup.R;
import de.ibba.keepitup.resources.PreferenceManager;
import de.ibba.keepitup.util.StreamUtil;
import de.ibba.keepitup.util.StringUtil;

public class PingCommand implements Callable<PingCommandResult> {

    private final Context context;
    private final String address;
    private final boolean ip6;

    public PingCommand(Context context, String address, boolean ip6) {
        this.context = context;
        this.address = address;
        this.ip6 = ip6;
    }

    @Override
    public PingCommandResult call() {
        Log.d(PingCommand.class.getName(), "call");
        String output = null;
        int returnCode = -1;
        Process process = null;
        try {
            PreferenceManager preferenceManager = new PreferenceManager(context);
            Runtime runtime = Runtime.getRuntime();
            String command = ip6 ? getResources().getString(R.string.ping6_command_line) : getResources().getString(R.string.ping_command_line);
            int count = preferenceManager.getPreferencePingCount();
            int timeout = getResources().getInteger(R.integer.ping_timeout);
            String formattedCommand = String.format(command, count, timeout, address);
            Log.d(PingCommand.class.getName(), "Executing ping command: " + formattedCommand);
            process = runtime.exec(formattedCommand);
            output = StreamUtil.inputStreamToString(process.getInputStream(), Charsets.US_ASCII);
            output = StringUtil.trim(output);
            if (StringUtil.isEmpty(output)) {
                output = StreamUtil.inputStreamToString(process.getErrorStream(), Charsets.US_ASCII);
                output = StringUtil.trim(output);
            }
            Log.d(PingCommand.class.getName(), "Ping output: " + output);
            returnCode = process.waitFor();
            Log.d(PingCommand.class.getName(), "Ping proccess return code: " + returnCode);
            return new PingCommandResult(returnCode, output, null);
        } catch (Exception exc) {
            Log.e(PingCommand.class.getName(), "Error executing ping command", exc);
            return new PingCommandResult(returnCode, output, exc);
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
    }

    private Context getContext() {
        return context;
    }

    private Resources getResources() {
        return getContext().getResources();
    }
}
