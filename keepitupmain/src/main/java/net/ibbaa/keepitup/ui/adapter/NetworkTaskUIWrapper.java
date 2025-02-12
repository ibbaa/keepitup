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

package net.ibbaa.keepitup.ui.adapter;

import androidx.annotation.NonNull;

import net.ibbaa.keepitup.model.AccessTypeData;
import net.ibbaa.keepitup.model.LogEntry;
import net.ibbaa.keepitup.model.NetworkTask;

@SuppressWarnings({"ClassCanBeRecord"})
public class NetworkTaskUIWrapper {

    private final NetworkTask networkTask;
    private final AccessTypeData accessTypeData;
    private final LogEntry logEntry;

    public NetworkTaskUIWrapper(NetworkTask networkTask, AccessTypeData accessTypeData, LogEntry logEntry) {
        this.networkTask = networkTask;
        this.accessTypeData = accessTypeData;
        this.logEntry = logEntry;
    }

    public long getId() {
        return networkTask.getId();
    }

    public NetworkTask getNetworkTask() {
        return networkTask;
    }

    public AccessTypeData getAccessTypeData() {
        return accessTypeData;
    }

    public LogEntry getLogEntry() {
        return logEntry;
    }

    public boolean isEqual(NetworkTaskUIWrapper other) {
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        if (networkTask == null && other.getNetworkTask() != null) {
            return false;
        }
        if (networkTask != null && !networkTask.isEqual(other.getNetworkTask())) {
            return false;
        }
        if (accessTypeData == null && other.getAccessTypeData() != null) {
            return false;
        }
        if (accessTypeData != null && !accessTypeData.isEqual(other.getAccessTypeData())) {
            return false;
        }
        if (logEntry == null && other.getLogEntry() != null) {
            return false;
        }
        return logEntry == null || logEntry.isEqual(other.getLogEntry());
    }

    @NonNull
    @Override
    public String toString() {
        return "NetworkTaskUIWrapper{" +
                "networkTask=" + networkTask +
                ", accessTypeData=" + accessTypeData +
                ", logEntry=" + logEntry +
                '}';
    }
}
