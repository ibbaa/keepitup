package de.ibba.keepitup.model;

import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

import de.ibba.keepitup.util.NumberUtil;

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
            this.valid = Boolean.parseBoolean(map.get("valid").toString());
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
