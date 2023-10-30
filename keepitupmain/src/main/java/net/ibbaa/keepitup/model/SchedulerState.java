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

package net.ibbaa.keepitup.model;

public class SchedulerState {

    private final long id;
    private final boolean suspended;
    private final long timestamp;

    public SchedulerState(long id, boolean suspended, long timestamp) {
        this.id = id;
        this.suspended = suspended;
        this.timestamp = timestamp;
    }

    public long getId() {
        return id;
    }

    public boolean isSuspended() {
        return suspended;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "SchedulerState{" +
                "id=" + id +
                ", suspended=" + suspended +
                ", timestamp=" + timestamp +
                '}';
    }
}
