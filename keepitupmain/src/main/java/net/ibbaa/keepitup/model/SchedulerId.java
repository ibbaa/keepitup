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

package net.ibbaa.keepitup.model;

import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.NonNull;

import net.ibbaa.keepitup.util.NumberUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SchedulerId {

    private long id;
    private boolean valid;
    private int schedulerid;
    private long timestamp;

    public SchedulerId() {
        this.id = -1;
        this.valid = false;
        this.schedulerid = -1;
        this.timestamp = -1;
    }

    public SchedulerId(PersistableBundle bundle) {
        this(new Bundle(bundle));
    }

    public SchedulerId(Bundle bundle) {
        this();
        this.id = bundle.getLong("id");
        this.valid = bundle.getInt("valid") >= 1;
        this.schedulerid = bundle.getInt("schedulerid");
        this.timestamp = bundle.getLong("timestamp");
    }

    public SchedulerId(Map<String, ?> map) {
        this();
        if (NumberUtil.isValidLongValue(map.get("id"))) {
            this.id = NumberUtil.getLongValue(map.get("id"), -1);
        }
        if (map.get("valid") != null) {
            this.valid = Boolean.parseBoolean(Objects.requireNonNull(map.get("valid")).toString());
        }
        if (NumberUtil.isValidIntValue(map.get("schedulerid"))) {
            this.schedulerid = NumberUtil.getIntValue(map.get("schedulerid"), 0);
        }
        if (NumberUtil.isValidLongValue(map.get("timestamp"))) {
            this.timestamp = NumberUtil.getLongValue(map.get("timestamp"), -1);
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public int getSchedulerId() {
        return schedulerid;
    }

    public void setSchedulerId(int schedulerid) {
        this.schedulerid = schedulerid;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public PersistableBundle toPersistableBundle() {
        PersistableBundle bundle = new PersistableBundle();
        bundle.putLong("id", id);
        bundle.putInt("schedulerid", schedulerid);
        bundle.putInt("valid", valid ? 1 : 0);
        bundle.putLong("timestamp", timestamp);
        return bundle;
    }

    public Bundle toBundle() {
        return new Bundle(toPersistableBundle());
    }

    public Map<String, ?> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("schedulerid", schedulerid);
        map.put("valid", valid);
        map.put("timestamp", timestamp);
        return map;
    }

    @NonNull
    @Override
    public String toString() {
        return "SchedulerId{" +
                "id=" + id +
                ", valid=" + valid +
                ", schedulerid=" + schedulerid +
                ", timestamp=" + timestamp +
                '}';
    }
}
