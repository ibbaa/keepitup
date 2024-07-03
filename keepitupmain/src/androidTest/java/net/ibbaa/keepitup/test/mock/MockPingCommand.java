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

package net.ibbaa.keepitup.test.mock;

import android.content.Context;

import net.ibbaa.keepitup.service.network.PingCommand;
import net.ibbaa.keepitup.service.network.PingCommandResult;

public class MockPingCommand extends PingCommand {

    private final PingCommandResult pingCommandResult;

    public MockPingCommand(Context context, String address, int pingCount, boolean defaultPackageSize, int packageSize, boolean ip6, boolean stopOnSuccess, PingCommandResult pingCommandResult) {
        super(context, address, pingCount, defaultPackageSize, packageSize, stopOnSuccess, ip6);
        this.pingCommandResult = pingCommandResult;
    }

    @Override
    public PingCommandResult call() {
        return pingCommandResult;
    }
}
