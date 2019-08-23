package de.ibba.keepitup.test.mock;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

public class MockNotificationBuilder extends NotificationCompat.Builder {

    private CharSequence contentTitle;
    private CharSequence contentText;
    private NotificationCompat.Style style;
    private int priority;
    private int smallIcon;

    public MockNotificationBuilder(@NonNull Context context, @NonNull String channelId) {
        super(context, channelId);
    }

    @Override
    public NotificationCompat.Builder setContentTitle(CharSequence title) {
        this.contentTitle = title;
        return super.setContentTitle(title);
    }

    @Override
    public NotificationCompat.Builder setContentText(CharSequence text) {
        this.contentText = text;
        return super.setContentText(text);
    }

    @Override
    public NotificationCompat.Builder setStyle(NotificationCompat.Style style) {
        this.style = style;
        return super.setStyle(style);
    }

    @Override
    public NotificationCompat.Builder setPriority(int priority) {
        this.priority = priority;
        return super.setPriority(priority);
    }

    @Override
    public NotificationCompat.Builder setSmallIcon(int icon) {
        this.smallIcon = icon;
        return super.setSmallIcon(icon);
    }

    public CharSequence getContentTitle() {
        return contentTitle;
    }

    public CharSequence getContentText() {
        return contentText;
    }

    public NotificationCompat.Style getStyle() {
        return style;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    public int getSmallIcon() {
        return smallIcon;
    }
}
