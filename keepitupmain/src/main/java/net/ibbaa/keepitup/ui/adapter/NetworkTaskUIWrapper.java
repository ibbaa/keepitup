/*
 * Copyright (c) 2023. Alwin Ibba
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

import net.ibbaa.keepitup.model.LogEntry;
import net.ibbaa.keepitup.model.NetworkTask;

public class NetworkTaskUIWrapper {

    private final NetworkTask networkTask;
    private final LogEntry logEntry;

    public NetworkTaskUIWrapper(NetworkTask networkTask, LogEntry logEntry) {
        this.networkTask = networkTask;
        this.logEntry = logEntry;
    }

    public long getId() {
        return networkTask.getId();
    }

    public NetworkTask getNetworkTask() {
        return networkTask;
    }

    public LogEntry getLogEntry() {
        return logEntry;
    }

    @NonNull
    @Override
    public String toString() {
        return "NetworkTaskUIWrapper{" +
                "networkTask=" + networkTask +
                ", logEntry=" + logEntry +
                '}';
    }
}
