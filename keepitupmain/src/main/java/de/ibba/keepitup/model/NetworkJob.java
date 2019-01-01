package de.ibba.keepitup.model;

import android.support.annotation.NonNull;

public class NetworkJob {

    private long id;
    private long index;
    private String address;
    private AccessType accessType;
    private long interval;
    private boolean success;
    private String message;
    private boolean notification;
    private boolean running;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getIndex() {
        return index;
    }

    public void setIndex(long index) {
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

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
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

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
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
                ", running=" + running +
                '}';
    }
}
