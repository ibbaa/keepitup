package de.ibba.keepitup.model;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;

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
        this.id = bundle.getLong("id");
        this.networktaskid = bundle.getLong("networktaskid");
        this.success = bundle.getInt("success") >= 1;
        this.timestamp = bundle.getLong("timestamp");
        this.message = bundle.getString("message");
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
