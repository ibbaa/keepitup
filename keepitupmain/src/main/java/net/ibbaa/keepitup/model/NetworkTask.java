package net.ibbaa.keepitup.model;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.resources.PreferenceManager;
import net.ibbaa.keepitup.util.NumberUtil;

public class NetworkTask {

    private long id;
    private int index;
    private int schedulerid;
    private int instances;
    private String address;
    private int port;
    private AccessType accessType;
    private int interval;
    private boolean onlyWifi;
    private boolean notification;
    private boolean running;
    private long lastScheduled;

    public NetworkTask() {
        this.id = -1;
        this.index = -1;
        this.schedulerid = -1;
        this.instances = 0;
        this.address = null;
        this.port = 0;
        this.accessType = null;
        this.interval = 0;
        this.onlyWifi = false;
        this.notification = false;
        this.running = false;
        this.lastScheduled = -1;
    }

    public NetworkTask(Context context) {
        this();
        Resources resources = context.getResources();
        PreferenceManager preferenceManager = new PreferenceManager(context);
        this.address = preferenceManager.getPreferenceAddress();
        this.port = preferenceManager.getPreferencePort();
        this.accessType = preferenceManager.getPreferenceAccessType();
        this.interval = preferenceManager.getPreferenceInterval();
        this.onlyWifi = preferenceManager.getPreferenceOnlyWifi();
        this.notification = preferenceManager.getPreferenceNotification();
        this.running = resources.getBoolean(R.bool.task_running_default);
    }

    public NetworkTask(PersistableBundle bundle) {
        this(new Bundle(bundle));
    }

    public NetworkTask(Bundle bundle) {
        this();
        this.id = bundle.getLong("id");
        this.index = bundle.getInt("index");
        this.schedulerid = bundle.getInt("schedulerid");
        this.instances = bundle.getInt("instances");
        this.address = bundle.getString("address");
        this.port = bundle.getInt("port");
        if (bundle.containsKey("accessType")) {
            accessType = AccessType.forCode(bundle.getInt("accessType"));
        }
        this.interval = bundle.getInt("interval");
        this.onlyWifi = bundle.getInt("onlywifi") >= 1;
        this.notification = bundle.getInt("notification") >= 1;
        this.running = bundle.getInt("running") >= 1;
        this.lastScheduled = bundle.getLong("lastScheduled");
    }

    public NetworkTask(Map<String, ?> map) {
        this();
        if (NumberUtil.isValidLongValue(map.get("id"))) {
            this.id = NumberUtil.getLongValue(map.get("id"), -1);
        }
        if (NumberUtil.isValidIntValue(map.get("index"))) {
            this.index = NumberUtil.getIntValue(map.get("index"), -1);
        }
        if (NumberUtil.isValidIntValue(map.get("schedulerid"))) {
            this.schedulerid = NumberUtil.getIntValue(map.get("schedulerid"), -1);
        }
        if (NumberUtil.isValidIntValue(map.get("instances"))) {
            this.instances = NumberUtil.getIntValue(map.get("instances"), 0);
        }
        if (map.get("address") != null) {
            this.address = Objects.requireNonNull(map.get("address")).toString();
        }
        if (NumberUtil.isValidIntValue(map.get("port"))) {
            this.port = NumberUtil.getIntValue(map.get("port"), 0);
        }
        if (NumberUtil.isValidIntValue(map.get("accessType"))) {
            accessType = AccessType.forCode(NumberUtil.getIntValue(map.get("accessType"), -1));
        }
        if (NumberUtil.isValidIntValue(map.get("interval"))) {
            this.interval = NumberUtil.getIntValue(map.get("interval"), 0);
        }
        if (map.get("onlyWifi") != null) {
            this.onlyWifi = Boolean.parseBoolean(Objects.requireNonNull(map.get("onlyWifi")).toString());
        }
        if (map.get("notification") != null) {
            this.notification = Boolean.parseBoolean(Objects.requireNonNull(map.get("notification")).toString());
        }
        if (map.get("running") != null) {
            this.running = Boolean.parseBoolean(Objects.requireNonNull(map.get("running")).toString());
        }
        if (NumberUtil.isValidLongValue(map.get("lastScheduled"))) {
            this.lastScheduled = NumberUtil.getLongValue(map.get("lastScheduled"), -1);
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getSchedulerId() {
        return schedulerid;
    }

    public void setSchedulerId(int schedulerid) {
        this.schedulerid = schedulerid;
    }

    public int getInstances() {
        return instances;
    }

    public void setInstances(int instances) {
        this.instances = instances;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public AccessType getAccessType() {
        return accessType;
    }

    public void setAccessType(AccessType accessType) {
        this.accessType = accessType;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public boolean isOnlyWifi() {
        return onlyWifi;
    }

    public void setOnlyWifi(boolean onlyWifi) {
        this.onlyWifi = onlyWifi;
    }

    public boolean isNotification() {
        return notification;
    }

    public void setNotification(boolean notification) {
        this.notification = notification;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public long getLastScheduled() {
        return lastScheduled;
    }

    public void setLastScheduled(long lastScheduled) {
        this.lastScheduled = lastScheduled;
    }

    public PersistableBundle toPersistableBundle() {
        PersistableBundle bundle = new PersistableBundle();
        bundle.putLong("id", id);
        bundle.putInt("index", index);
        bundle.putInt("schedulerid", schedulerid);
        bundle.putInt("instances", instances);
        if (address != null) {
            bundle.putString("address", address);
        }
        bundle.putInt("port", port);
        if (accessType != null) {
            bundle.putInt("accessType", accessType.getCode());
        }
        bundle.putInt("interval", interval);
        bundle.putInt("onlywifi", onlyWifi ? 1 : 0);
        bundle.putInt("notification", notification ? 1 : 0);
        bundle.putInt("running", running ? 1 : 0);
        bundle.putLong("lastScheduled", lastScheduled);
        return bundle;
    }

    public Bundle toBundle() {
        return new Bundle(toPersistableBundle());
    }

    public Map<String, ?> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("index", index);
        map.put("schedulerid", schedulerid);
        map.put("instances", instances);
        if (address != null) {
            map.put("address", address);
        }
        map.put("port", port);
        if (accessType != null) {
            map.put("accessType", accessType.getCode());
        }
        map.put("interval", interval);
        map.put("onlyWifi", onlyWifi);
        map.put("notification", notification);
        map.put("running", running);
        map.put("lastScheduled", lastScheduled);
        return map;
    }

    public boolean isEqual(NetworkTask other) {
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        if (id != other.id) {
            return false;
        }
        if (index != other.index) {
            return false;
        }
        if (schedulerid != other.schedulerid) {
            return false;
        }
        if (instances != other.instances) {
            return false;
        }
        if (lastScheduled != other.lastScheduled) {
            return false;
        }
        if (port != other.port) {
            return false;
        }
        if (interval != other.interval) {
            return false;
        }
        if (onlyWifi != other.onlyWifi) {
            return false;
        }
        if (notification != other.notification) {
            return false;
        }
        if (running != other.running) {
            return false;
        }
        if (!Objects.equals(address, other.address)) {
            return false;
        }
        return Objects.equals(accessType, other.accessType);
    }

    public boolean isTechnicallyEqual(NetworkTask other) {
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        if (port != other.port) {
            return false;
        }
        if (interval != other.interval) {
            return false;
        }
        if (onlyWifi != other.onlyWifi) {
            return false;
        }
        if (notification != other.notification) {
            return false;
        }
        if (!Objects.equals(address, other.address)) {
            return false;
        }
        return Objects.equals(accessType, other.accessType);
    }

    @NonNull
    @Override
    public String toString() {
        return "NetworkTask{" +
                "id=" + id +
                ", index=" + index +
                ", schedulerid=" + schedulerid +
                ", instances=" + instances +
                ", address='" + address + '\'' +
                ", port=" + port +
                ", accessType=" + accessType +
                ", interval=" + interval +
                ", onlyWifi=" + onlyWifi +
                ", notification=" + notification +
                ", running=" + running +
                ", lastScheduled=" + lastScheduled +
                '}';
    }
}
