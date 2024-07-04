/*
 * Copyright (c) 2024. Alwin Ibba
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.ibbaa.keepitup.service.network;

import android.content.Context;
import android.content.res.Resources;

import com.google.common.base.Charsets;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.util.StreamUtil;
import net.ibbaa.keepitup.util.StringUtil;

import java.util.concurrent.Callable;

public class PingCommand implements Callable<PingCommandResult> {

    private final Context context;
    private final String address;
    private final int pingCount;
    private final boolean defaultPackageSize;
    private final int packageSize;
    private final boolean stopOnSuccess;
    private final boolean ip6;

    public PingCommand(Context context, String address, int pingCount, boolean defaultPackageSize, int packageSize, boolean stopOnSuccess, boolean ip6) {
        this.context = context;
        this.address = address;
        this.pingCount = pingCount;
        this.defaultPackageSize = defaultPackageSize;
        this.packageSize = packageSize;
        this.stopOnSuccess = stopOnSuccess;
        this.ip6 = ip6;
    }

    @Override
    public PingCommandResult call() {
        Log.d(PingCommand.class.getName(), "call");
        PingCommandResult result = new PingCommandResult(-1, 1, null, null);
        if (stopOnSuccess) {
            for (int ii = 1; ii <= pingCount; ii++) {
                result = executePing();
                if (result.exception() != null) {
                    return new PingCommandResult(result.processReturnCode(), ii, result.output(), result.exception());
                }
                if (result.processReturnCode() == 0) {
                    return new PingCommandResult(0, ii, result.output(), null);
                }
            }
            return new PingCommandResult(result.processReturnCode(), pingCount, result.output(), null);
        }
        return executePing();
    }

    protected PingCommandResult executePing() {
        Process process = null;
        try {
            Runtime runtime = Runtime.getRuntime();
            String command = getPingCommand();
            Log.d(PingCommand.class.getName(), "Executing ping command: " + command);
            process = runtime.exec(command);
            String output = StreamUtil.inputStreamToString(process.getInputStream(), Charsets.US_ASCII);
            output = StringUtil.trim(output);
            if (StringUtil.isEmpty(output)) {
                output = StreamUtil.inputStreamToString(process.getErrorStream(), Charsets.US_ASCII);
                output = StringUtil.trim(output);
            }
            Log.d(PingCommand.class.getName(), "Ping output: " + output);
            int returnCode = process.waitFor();
            Log.d(PingCommand.class.getName(), "Ping process return code: " + returnCode);
            return new PingCommandResult(returnCode, 1, output, null);
        } catch (Exception exc) {
            Log.e(PingCommand.class.getName(), "Error executing ping command", exc);
            return new PingCommandResult(-1, 1, null, exc);
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
    }

    private String getPingCommand() {
        String command = ip6 ? getResources().getString(R.string.ping6_command) : getResources().getString(R.string.ping_command);
        String countOption = getResources().getString(R.string.ping_command_count_option);
        String timeoutOption = getResources().getString(R.string.ping_command_timeout_option);
        String packageSizeOption = getResources().getString(R.string.ping_command_package_size_option);
        int timeout = getResources().getInteger(R.integer.ping_timeout);
        int effectiveCount = stopOnSuccess ? 1 : pingCount;
        String commandLine = command + " " + countOption + " " + effectiveCount + " " + timeoutOption + " " + timeout;
        if (!defaultPackageSize) {
            commandLine += " " + packageSizeOption + " " + packageSize;
        }
        return commandLine + " " + address;
    }

    private Context getContext() {
        return context;
    }

    private Resources getResources() {
        return getContext().getResources();
    }
}
