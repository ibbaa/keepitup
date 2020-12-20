package de.ibba.keepitup.model;

import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LogEntry {

    private long id;
    private long networktaskid;
    private long timestamp;
    private boolean success;
    private String message;

    public LogEntry() {
        this.id = -1;
        this.networktaskid = -1;
        this.success = false;
        this.timestamp = -1;
        this.message = null;
    }

    public LogEntry(PersistableBundle bundle) {
        this(new Bundle(bundle));
    }

    public LogEntry(Bundle bundle) {
        this();
        this.id = bundle.getLong("id");
        this.networktaskid = bundle.getLong("networktaskid");
        this.success = bundle.getInt("success") >= 1;
        this.timestamp = bundle.getLong("timestamp");
        this.message = bundle.getString("message");
    }

    public LogEntry(Map<String, ?> map) {
        this();
        if (map.get("id") != null) {
            this.id = (Long) map.get("id");
        }
        if (map.get("networktaskid") != null) {
            this.networktaskid = (Long) map.get("networktaskid");
        }
        if (map.get("success") != null) {
            this.success = (Boolean) map.get("success");
        }
        if (map.get("timestamp") != null) {
            this.timestamp = (Long) map.get("timestamp");
        }
        if (map.get("message") != null) {
            this.message = (String) map.get("message");
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

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
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

    public PersistableBundle toPersistableBundle() {
        PersistableBundle bundle = new PersistableBundle();
        bundle.putLong("id", id);
        bundle.putLong("networktaskid", networktaskid);
        bundle.putInt("success", success ? 1 : 0);
        bundle.putLong("timestamp", timestamp);
        if (message != null) {
            bundle.putString("message", message);
        }
        return bundle;
    }

    public Bundle toBundle() {
        return new Bundle(toPersistableBundle());
    }

    public Map<String, ?> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("networktaskid", networktaskid);
        map.put("success", success);
        map.put("timestamp", timestamp);
        if (message != null) {
            map.put("message", message);
        }
        return map;
    }

    public boolean isEqual(LogEntry other) {
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        if (id != other.id) {
            return false;
        }
        if (networktaskid != other.networktaskid) {
            return false;
        }
        if (success != other.success) {
            return false;
        }
        if (timestamp != other.timestamp) {
            return false;
        }
        return Objects.equals(message, other.message);
    }

    @NonNull
    @Override
    public String toString() {
        return "LogEntry{" +
                "id=" + id +
                ", networktaskid=" + networktaskid +
                ", timestamp=" + timestamp +
                ", success=" + success +
                ", message='" + message + '\'' +
                '}';
    }
}
