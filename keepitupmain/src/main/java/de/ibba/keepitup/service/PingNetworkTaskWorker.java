package de.ibba.keepitup.service;

import android.content.Context;
import android.os.PowerManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;

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
            Runtime runtime = Runtime.getRuntime();
            String command = String.format("/system/bin/ping -c 3 -W 10 %s", networkTask.getAddress());
            Process process = runtime.exec(command);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String s;
            String res = "";
            while ((s = stdInput.readLine()) != null) {
                res += s + "\n";
            }
            logEntry.setMessage(res);
            int ret = process.waitFor();
            process.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return logEntry;
    }
}
