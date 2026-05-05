/*
 * Copyright (c) 2026 Alwin Ibba
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

import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.model.SNMPVersion;
import net.ibbaa.keepitup.resources.ServiceFactoryContributor;
import net.ibbaa.keepitup.service.ITimeService;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.concurrent.Callable;

public class SNMPCommand implements Callable<SNMPCommandResult> {

    private final Context context;
    private final InetAddress address;
    private final int port;
    private final SNMPVersion snmpVersion;
    private final String community;
    private final boolean ip6;
    private final ITimeService timeService;

    public SNMPCommand(Context context, InetAddress address, int port, SNMPVersion snmpVersion, String community, boolean ip6) {
        this.context = context;
        this.address = address;
        this.port = port;
        this.snmpVersion = snmpVersion;
        this.community = community;
        this.ip6 = ip6;
        this.timeService = createTimeService();
    }

    @Override
    public SNMPCommandResult call() {
        Log.d(SNMPCommand.class.getName(), "call");
        return new SNMPCommandResult(true, new HashMap<>(), null);
    }

    private ITimeService createTimeService() {
        ServiceFactoryContributor factoryContributor = new ServiceFactoryContributor(getContext());
        return factoryContributor.createServiceFactory().createTimeService();
    }

    private Context getContext() {
        return context;
    }

    private Resources getResources() {
        return getContext().getResources();
    }
}
