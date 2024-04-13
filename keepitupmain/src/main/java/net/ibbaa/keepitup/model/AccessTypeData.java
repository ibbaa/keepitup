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

package net.ibbaa.keepitup.model;

import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.NonNull;

import net.ibbaa.keepitup.resources.PreferenceManager;
import net.ibbaa.keepitup.util.NumberUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AccessTypeData {

    private long id;
    private long networktaskid;
    private int pingCount;
    private int pingPackageSize;
    private int connectCount;

    public AccessTypeData() {
        this.id = -1;
        this.networktaskid = -1;
        this.pingCount = 3;
        this.pingPackageSize = 56;
        this.connectCount = 1;
    }

    public AccessTypeData(Context context) {
        this();
        PreferenceManager preferenceManager = new PreferenceManager(context);
        this.pingCount = preferenceManager.getPreferencePingCount();
        this.pingPackageSize = preferenceManager.getPreferencePingPackageSize();
        this.connectCount = preferenceManager.getPreferenceConnectCount();
    }

    public AccessTypeData(PersistableBundle bundle) {
        this(new Bundle(bundle));
    }

    public AccessTypeData(Bundle bundle) {
        this();
        this.id = bundle.getLong("id");
        this.networktaskid = bundle.getLong("networktaskid");
        this.pingCount = bundle.getInt("pingCount");
        this.pingPackageSize = bundle.getInt("pingPackageSize");
        this.connectCount = bundle.getInt("connectCount");
    }

    public AccessTypeData(Map<String, ?> map) {
        this();
        if (NumberUtil.isValidLongValue(map.get("id"))) {
            this.id = NumberUtil.getLongValue(map.get("id"), -1);
        }
        if (NumberUtil.isValidLongValue(map.get("networktaskid"))) {
            this.networktaskid = NumberUtil.getLongValue(map.get("networktaskid"), -1);
        }
        if (NumberUtil.isValidIntValue(map.get("pingCount"))) {
            this.pingCount = NumberUtil.getIntValue(map.get("pingCount"), 3);
        }
        if (NumberUtil.isValidIntValue(map.get("pingPackageSize"))) {
            this.pingPackageSize = NumberUtil.getIntValue(map.get("pingPackageSize"), 56);
        }
        if (NumberUtil.isValidIntValue(map.get("connectCount"))) {
            this.connectCount = NumberUtil.getIntValue(map.get("connectCount"), 1);
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getNetworkTaskId() {
        return networktaskid;
    }

    public void setNetworkTaskId(long networktaskid) {
        this.networktaskid = networktaskid;
    }

    public int getPingCount() {
        return pingCount;
    }

    public void setPingCount(int pingCount) {
        this.pingCount = pingCount;
    }

    public int getPingPackageSize() {
        return pingPackageSize;
    }

    public void setPingPackageSize(int pingPackageSize) {
        this.pingPackageSize = pingPackageSize;
    }

    public int getConnectCount() {
        return connectCount;
    }

    public void setConnectCount(int connectCount) {
        this.connectCount = connectCount;
    }

    public PersistableBundle toPersistableBundle() {
        PersistableBundle bundle = new PersistableBundle();
        bundle.putLong("id", id);
        bundle.putLong("networktaskid", networktaskid);
        bundle.putInt("pingCount", pingCount);
        bundle.putInt("pingPackageSize", pingPackageSize);
        bundle.putInt("connectCount", connectCount);
        return bundle;
    }

    public Bundle toBundle() {
        return new Bundle(toPersistableBundle());
    }

    public Map<String, ?> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("networktaskid", networktaskid);
        map.put("pingCount", pingCount);
        map.put("pingPackageSize", pingPackageSize);
        map.put("connectCount", connectCount);
        return map;
    }

    public boolean isEqual(AccessTypeData other) {
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        if (id != other.id) {
            return false;
        }
        if (networktaskid != other.networktaskid) {
            return false;
        }
        if (pingCount != other.pingCount) {
            return false;
        }
        if (pingPackageSize != other.pingPackageSize) {
            return false;
        }
        return Objects.equals(connectCount, other.connectCount);
    }

    public boolean isTechnicallyEqual(AccessTypeData other) {
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        if (networktaskid != other.networktaskid) {
            return false;
        }
        if (pingCount != other.pingCount) {
            return false;
        }
        if (pingPackageSize != other.pingPackageSize) {
            return false;
        }
        return Objects.equals(connectCount, other.connectCount);
    }

    @NonNull
    @Override
    public String toString() {
        return "AccessTypeData{" +
                "id=" + id +
                ", networktaskid=" + networktaskid +
                ", pingCount=" + pingCount +
                ", pingPackageSize=" + pingPackageSize +
                ", connectCount=" + connectCount +
                '}';
    }
}
