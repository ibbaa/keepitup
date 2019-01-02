package de.ibba.keepitup.model;

import android.os.PersistableBundle;
import android.support.annotation.NonNull;

public class NetworkJob {

    private int id;
    private int index;
    private String address;
    private AccessType accessType;
    private int interval;
    private boolean success;
    private String message;
    private boolean notification;

    public NetworkJob() {
    }

    public NetworkJob(PersistableBundle bundle) {
        this.id = bundle.getInt("id");
        this.index = bundle.getInt("index");
        this.address = bundle.getString("address");
        if (bundle.containsKey("accessType")) {
            accessType = AccessType.forCode(bundle.getInt("accessType"));
        }
        this.interval = bundle.getInt("interval");
        this.success = bundle.getInt("success") >= 1;
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
        if (accessType != null) {
            bundle.putInt("accessType", accessType.getCode());
        }
        bundle.putInt("interval", interval);
        bundle.putInt("success", success ? 1 : 0);
        if (message != null) {
            bundle.putString("message", message);
        }
        bundle.putInt("notification", notification ? 1 : 0);
        return bundle;
    }

    @NonNull
    @Override
    public String toString() {
        return "NetworkJob{" +
                "id=" + id +
                ", index=" + index +
                ", address='" + address + '\'' +
                ", accessType=" + accessType +
                ", interval=" + interval +
                ", success=" + success +
                ", message='" + message + '\'' +
                ", notification=" + notification +
                '}';
    }
}
