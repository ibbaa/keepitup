package de.ibba.keepitup.model;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;

import de.ibba.keepitup.R;
import de.ibba.keepitup.resources.NetworkTaskPreferenceManager;

public class NetworkTask {

    private long id;
    private int index;
    private int schedulerid;
    private String address;
    private int port;
    private AccessType accessType;
    private int interval;
    private boolean onlyWifi;
    private boolean notification;
    private boolean running;

    public NetworkTask() {
        this.id = -1;
        this.index = -1;
        this.schedulerid = -1;
        this.address = null;
        this.port = 0;
        this.accessType = null;
        this.interval = 0;
        this.onlyWifi = false;
        this.notification = false;
        this.running = false;
    }

    public NetworkTask(Context context) {
        Resources resources = context.getResources();
        NetworkTaskPreferenceManager preferenceManager = new NetworkTaskPreferenceManager(context);
        this.id = -1;
        this.index = -1;
        this.schedulerid = -1;
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
        this.id = bundle.getLong("id");
        this.index = bundle.getInt("index");
        this.schedulerid = bundle.getInt("schedulerid");
        this.address = bundle.getString("address");
        this.port = bundle.getInt("port");
        if (bundle.containsKey("accessType")) {
            accessType = AccessType.forCode(bundle.getInt("accessType"));
        }
        this.interval = bundle.getInt("interval");
        this.onlyWifi = bundle.getInt("onlywifi") >= 1;
        this.notification = bundle.getInt("notification") >= 1;
        this.running = bundle.getInt("running") >= 1;
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

    public PersistableBundle toPersistableBundle() {
        PersistableBundle bundle = new PersistableBundle();
        bundle.putLong("id", id);
        bundle.putInt("index", index);
        bundle.putInt("schedulerid", schedulerid);
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
        return bundle;
    }

    public Bundle toBundle() {
        return new Bundle(toPersistableBundle());
    }

    @NonNull
    @Override
    public String toString() {
        return "NetworkTask{" +
                "id=" + id +
                ", index=" + index +
                ", schedulerid=" + schedulerid +
                ", address='" + address + '\'' +
                ", port=" + port +
                ", accessType=" + accessType +
                ", interval=" + interval +
                ", onlyWifi=" + onlyWifi +
                ", notification=" + notification +
                ", running=" + running +
                '}';
    }
}
