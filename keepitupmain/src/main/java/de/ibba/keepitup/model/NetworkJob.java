package de.ibba.keepitup.model;

import android.support.annotation.NonNull;

public class NetworkJob {

    private long id;
    private String address;
    private AccessType accessType;
    private long interval;
    private boolean running;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
                ", address='" + address + '\'' +
                ", accessType=" + accessType +
                ", interval=" + interval +
                ", running=" + running +
                '}';
    }
}
