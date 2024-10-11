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
import android.os.PowerManager;
import android.util.Log;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.db.NetworkTaskDAO;
import net.ibbaa.keepitup.model.AccessTypeData;
import net.ibbaa.keepitup.model.LogEntry;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.service.NetworkTaskWorker;
import net.ibbaa.keepitup.service.network.DNSLookupResult;
import net.ibbaa.keepitup.ui.permission.IFolderPermissionManager;
import net.ibbaa.keepitup.ui.permission.IPermissionManager;

import java.util.Objects;
import java.util.concurrent.Callable;

public class TestNetworkTaskWorker extends NetworkTaskWorker {

    private MockDNSLookup mockDNSLookup;
    private final boolean success;
    private final int maxInstances;
    private int instancesOnExecute;
    private final boolean interrupted;
    private NetworkTask task;
    private AccessTypeData data;
    private MockFolderPermissionManager folderPermissionManager;

    public TestNetworkTaskWorker(Context context, NetworkTask networkTask, PowerManager.WakeLock wakeLock, boolean success) {
        this(context, networkTask, wakeLock, success, 10);
    }

    public TestNetworkTaskWorker(Context context, NetworkTask networkTask, PowerManager.WakeLock wakeLock, boolean success, int maxInstances) {
        this(context, networkTask, wakeLock, success, maxInstances, false);
    }

    public TestNetworkTaskWorker(Context context, NetworkTask networkTask, PowerManager.WakeLock wakeLock, boolean success, int maxInstances, boolean interrupted) {
        super(context, networkTask, wakeLock);
        ((MockNetworkManager) getNetworkManager()).setConnected(true);
        ((MockNetworkManager) getNetworkManager()).setConnectedWithWiFi(true);
        this.success = success;
        this.maxInstances = maxInstances;
        this.instancesOnExecute = -1;
        this.interrupted = interrupted;
    }

    @Override
    public int getMaxInstances() {
        return maxInstances;
    }

    @Override
    public String getMaxInstancesErrorMessage(int activeInstances) {
        return "TestMaxInstancesError " + activeInstances;
    }

    public int getInstancesOnExecute() {
        return instancesOnExecute;
    }

    @Override
    public ExecutionResult execute(NetworkTask networkTask, AccessTypeData data) {
        Log.d(TestNetworkTaskWorker.class.getName(), "Executing TestNetworkTaskWorker for network task " + networkTask + " and access type data" + data);
        this.task = networkTask;
        this.data = data;
        NetworkTaskDAO networkTaskDAO = new NetworkTaskDAO(getContext());
        instancesOnExecute = networkTaskDAO.readNetworkTaskInstances(networkTask.getId());
        LogEntry logEntry = new LogEntry();
        logEntry.setNetworkTaskId(networkTask.getId());
        logEntry.setSuccess(success);
        logEntry.setTimestamp(getTimeService().getCurrentTimestamp());
        logEntry.setMessage(success ? getResources().getString(R.string.string_successful) : getResources().getString(R.string.string_not_successful));
        return new ExecutionResult(interrupted, logEntry);
    }

    public void setMockDNSLookup(MockDNSLookup mockDNSLookup) {
        this.mockDNSLookup = mockDNSLookup;
    }

    public void setFolderPermissionManager(MockFolderPermissionManager folderPermissionManager) {
        this.folderPermissionManager = folderPermissionManager;
    }

    @Override
    protected Callable<DNSLookupResult> getDNSLookup(String host) {
        return mockDNSLookup;
    }

    public NetworkTask getExecuteTask() {
        return task;
    }

    public AccessTypeData getExecuteData() {
        return data;
    }

    @Override
    public IPermissionManager getPermissionManager() {
        return new MockPermissionManager();
    }

    @Override
    public IFolderPermissionManager getFolderPermissionManager() {
        return Objects.requireNonNullElseGet(folderPermissionManager, MockFolderPermissionManager::new);
    }
}
