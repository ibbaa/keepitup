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

import net.ibbaa.keepitup.service.network.ConnectCommand;
import net.ibbaa.keepitup.service.network.ConnectCommandResult;

import java.net.InetAddress;

public class MockConnectCommand extends ConnectCommand {

    private final ConnectCommandResult connectCommandResult;

    public MockConnectCommand(Context context, InetAddress address, int port, int connectCount, boolean stopOnSuccess, ConnectCommandResult connectCommandResult) {
        super(context, address, port, connectCount, stopOnSuccess);
        this.connectCommandResult = connectCommandResult;
    }

    @Override
    public ConnectCommandResult call() {
        return connectCommandResult;
    }
}
