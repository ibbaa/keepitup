package de.ibba.keepitup.model;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;

public class NetworkTask {

    private int id;
    private int index;
    private String address;
    private int port;
    private AccessType accessType;
    private int interval;
    private boolean success;
    private long timestamp;
    private String message;
    private boolean notification;

    public NetworkTask() {
        this.id = -1;
        this.index = -1;
        this.address = null;
        this.port = 0;
        this.accessType = null;
        this.interval = 0;
        this.success = false;
        this.timestamp = -1;
        this.message = null;
        this.notification = false;
    }

    public NetworkTask(PersistableBundle bundle) {
        this(new Bundle(bundle));
    }

    public NetworkTask(Bundle bundle) {
        this.id = bundle.getInt("id");
        this.index = bundle.getInt("index");
        this.address = bundle.getString("address");
        this.port = bundle.getInt("port");
        if (bundle.containsKey("accessType")) {
            accessType = AccessType.forCode(bundle.getInt("accessType"));
        }
        this.interval = bundle.getInt("interval");
        this.success = bundle.getInt("success") >= 1;
        this.timestamp = bundle.getLong("timestamp");
        this.message = bundle.getString("message");
        this.notification = bundle.getInt("notification") >= 1;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
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

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isNotification() {
        return notification;
    }

    public void setNotification(boolean notification) {
        this.notification = notification;
    }

    public PersistableBundle toPersistableBundle() {
        PersistableBundle bundle = new PersistableBundle();
        bundle.putInt("id", id);
        bundle.putInt("index", index);
        if (address != null) {
            bundle.putString("address", address);
        }
        bundle.putInt("port", port);
        if (accessType != null) {
            bundle.putInt("accessType", accessType.getCode());
        }
        bundle.putInt("interval", interval);
        bundle.putInt("success", success ? 1 : 0);
        bundle.putLong("timestamp", timestamp);
        if (message != null) {
            bundle.putString("message", message);
        }
        bundle.putInt("notification", notification ? 1 : 0);
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
                ", address='" + address + '\'' +
                ", port=" + port +
                ", accessType=" + accessType +
                ", interval=" + interval +
                ", success=" + success +
                ", timestamp=" + timestamp +
                ", message='" + message + '\'' +
                ", notification=" + notification +
                '}';
    }
}
