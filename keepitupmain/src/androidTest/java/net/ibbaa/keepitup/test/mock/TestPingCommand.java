/*
 * Copyright (c) 2025 Alwin Ibba
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

package net.ibbaa.keepitup.test.mock;

import android.content.Context;

import net.ibbaa.keepitup.service.network.PingCommand;
import net.ibbaa.keepitup.service.network.PingCommandResult;

import java.util.List;

public class TestPingCommand extends PingCommand {

    private List<PingCommandResult> pingResults;
    private int call;

    public TestPingCommand(Context context, String address, int pingCount, boolean defaultPackageSize, int packageSize, boolean stopOnSuccess, boolean ip6) {
        super(context, address, pingCount, defaultPackageSize, packageSize, stopOnSuccess, ip6);
        reset();
    }

    public void reset() {
        call = 0;
    }

    public void setPingResults(List<PingCommandResult> pingResults) {
        this.pingResults = pingResults;
    }

    @Override
    protected PingCommandResult executePing() {
        if (pingResults != null && call < pingResults.size()) {
            PingCommandResult result = pingResults.get(call);
            call++;
            return result;
        }
        return null;
    }
}
