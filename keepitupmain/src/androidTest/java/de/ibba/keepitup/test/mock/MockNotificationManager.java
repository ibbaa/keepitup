package de.ibba.keepitup.test.mock;

import android.app.Notification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.ibba.keepitup.notification.INotificationManager;

public class MockNotificationManager implements INotificationManager {

    private final List<NotifyCall> notifyCalls;

    public MockNotificationManager() {
        notifyCalls = new ArrayList<>();
    }

    public List<NotifyCall> getNotifyCalls() {
        return Collections.unmodifiableList(notifyCalls);
    }

    public void reset() {
        notifyCalls.clear();
    }

    public boolean wasNotifyCalled() {
        return !notifyCalls.isEmpty();
    }

    @Override
    public void notify(int id, Notification notification) {
        notifyCalls.add(new NotifyCall(id, notification));
    }

    public static class NotifyCall {

        private final int id;
        private final Notification notification;

        public NotifyCall(int id, Notification notification) {
            this.id = id;
            this.notification = notification;
        }

        public int getId() {
            return id;
        }

        public Notification getNotification() {
            return notification;
        }
    }
}
