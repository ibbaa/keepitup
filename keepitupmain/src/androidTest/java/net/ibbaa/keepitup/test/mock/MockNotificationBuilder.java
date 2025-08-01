/*
 * Copyright (c) 2025 Alwin Ibba
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.ibbaa.keepitup.test.mock;

import android.app.PendingIntent;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import org.jspecify.annotations.Nullable;

public class MockNotificationBuilder extends NotificationCompat.Builder {

    private CharSequence contentTitle;
    private CharSequence contentText;
    private NotificationCompat.Style style;
    private int priority;
    private int smallIcon;
    private CharSequence actionText;

    public MockNotificationBuilder(@NonNull Context context, @NonNull String channelId) {
        super(context, channelId);
    }

    @NonNull
    @Override
    public NotificationCompat.Builder setContentTitle(CharSequence title) {
        this.contentTitle = title;
        return super.setContentTitle(title);
    }

    @NonNull
    @Override
    public NotificationCompat.Builder setContentText(CharSequence text) {
        this.contentText = text;
        return super.setContentText(text);
    }

    @NonNull
    @Override
    public NotificationCompat.Builder setStyle(NotificationCompat.Style style) {
        this.style = style;
        return super.setStyle(style);
    }

    @NonNull
    @Override
    public NotificationCompat.Builder setPriority(int priority) {
        this.priority = priority;
        return super.setPriority(priority);
    }

    @NonNull
    @Override
    public NotificationCompat.Builder setSmallIcon(int icon) {
        this.smallIcon = icon;
        return super.setSmallIcon(icon);
    }

    @Override
    public NotificationCompat.@org.jspecify.annotations.NonNull Builder addAction(int icon, @Nullable CharSequence title, @Nullable PendingIntent intent) {
        this.actionText = title;
        return super.addAction(icon, title, intent);
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

    public CharSequence getActionText() {
        return actionText;
    }
}
