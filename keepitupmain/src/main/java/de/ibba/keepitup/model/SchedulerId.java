package de.ibba.keepitup.model;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;

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
        this.id = bundle.getLong("id");
        this.valid = bundle.getInt("valid") >= 1;
        this.schedulerid = bundle.getInt("schedulerid");
        this.timestamp = bundle.getLong("timestamp");
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
