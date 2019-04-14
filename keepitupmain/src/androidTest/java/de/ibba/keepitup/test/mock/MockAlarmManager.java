package de.ibba.keepitup.test.mock;

import android.app.PendingIntent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.ibba.keepitup.service.IAlarmManager;

public class MockAlarmManager implements IAlarmManager {

    private List<SetAlarmCall> setAlarmCalls;
    private List<CancelAlarmCall> cancelAlarmCalls;

    public MockAlarmManager() {
        setAlarmCalls = new ArrayList<>();
        cancelAlarmCalls = new ArrayList<>();
    }

    public List<SetAlarmCall> getSetAlarmCalls() {
        return Collections.unmodifiableList(setAlarmCalls);
    }

    public List<CancelAlarmCall> getCancelAlarmCalls() {
        return Collections.unmodifiableList(cancelAlarmCalls);
    }

    public void reset() {
        setAlarmCalls.clear();
        cancelAlarmCalls.clear();
    }

    public boolean wasSetAlarmCalled() {
        return !setAlarmCalls.isEmpty();
    }

    public boolean wasCancelAlarmCalled() {
        return !cancelAlarmCalls.isEmpty();
    }

    @Override
    public void setAlarm(long delay, PendingIntent pendingIntent) {
        setAlarmCalls.add(new SetAlarmCall(delay, pendingIntent));
    }

    @Override
    public void cancelAlarm(PendingIntent pendingIntent) {
        cancelAlarmCalls.add(new CancelAlarmCall(pendingIntent));
    }

    public static class SetAlarmCall {

        private final long delay;
        private final PendingIntent pendingIntent;

        public SetAlarmCall(long delay, PendingIntent pendingIntent) {
            this.delay = delay;
            this.pendingIntent = pendingIntent;
        }

        public long getDelay() {
            return delay;
        }

        public PendingIntent getPendingIntent() {
            return pendingIntent;
        }
    }

    public static class CancelAlarmCall {

        private final PendingIntent pendingIntent;

        public CancelAlarmCall(PendingIntent pendingIntent) {
            this.pendingIntent = pendingIntent;
        }

        public PendingIntent getPendingIntent() {
            return pendingIntent;
        }
    }
}
