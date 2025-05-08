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
    private boolean stopOnSuccess;
    private boolean ignoreSSLError;

    public AccessTypeData() {
        this.id = -1;
        this.networktaskid = -1;
        this.pingCount = 3;
        this.pingPackageSize = 56;
        this.connectCount = 1;
        this.stopOnSuccess = false;
        this.ignoreSSLError = false;
    }

    public AccessTypeData(Context context) {
        this();
        PreferenceManager preferenceManager = new PreferenceManager(context);
        this.pingCount = preferenceManager.getPreferencePingCount();
        this.pingPackageSize = preferenceManager.getPreferencePingPackageSize();
        this.connectCount = preferenceManager.getPreferenceConnectCount();
        this.stopOnSuccess = preferenceManager.getPreferenceStopOnSuccess();
        this.ignoreSSLError = preferenceManager.getPreferenceIgnoreSSLError();
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
        this.stopOnSuccess = bundle.getInt("stopOnSuccess") >= 1;
        this.ignoreSSLError = bundle.getInt("ignoreSSLError") >= 1;
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
        if (map.get("stopOnSuccess") != null) {
            this.stopOnSuccess = Boolean.parseBoolean(Objects.requireNonNull(map.get("stopOnSuccess")).toString());
        }
        if (map.get("ignoreSSLError") != null) {
            this.ignoreSSLError = Boolean.parseBoolean(Objects.requireNonNull(map.get("ignoreSSLError")).toString());
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

    public boolean isStopOnSuccess() {
        return stopOnSuccess;
    }

    public void setStopOnSuccess(boolean stopOnSuccess) {
        this.stopOnSuccess = stopOnSuccess;
    }

    public boolean isIgnoreSSLError() {
        return ignoreSSLError;
    }

    public void setIgnoreSSLError(boolean ignoreSSLError) {
        this.ignoreSSLError = ignoreSSLError;
    }

    public PersistableBundle toPersistableBundle() {
        PersistableBundle bundle = new PersistableBundle();
        bundle.putLong("id", id);
        bundle.putLong("networktaskid", networktaskid);
        bundle.putInt("pingCount", pingCount);
        bundle.putInt("pingPackageSize", pingPackageSize);
        bundle.putInt("connectCount", connectCount);
        bundle.putInt("stopOnSuccess", stopOnSuccess ? 1 : 0);
        bundle.putInt("ignoreSSLError", ignoreSSLError ? 1 : 0);
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
        map.put("stopOnSuccess", stopOnSuccess);
        map.put("ignoreSSLError", ignoreSSLError);
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
        if (connectCount != other.connectCount) {
            return false;
        }
        if (stopOnSuccess != other.stopOnSuccess) {
            return false;
        }
        return Objects.equals(ignoreSSLError, other.ignoreSSLError);
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
        if (connectCount != other.connectCount) {
            return false;
        }
        if (stopOnSuccess != other.stopOnSuccess) {
            return false;
        }
        return Objects.equals(ignoreSSLError, other.ignoreSSLError);
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
                ", stopOnSuccess=" + stopOnSuccess +
                ", ignoreSSLError=" + ignoreSSLError +
                '}';
    }
}
