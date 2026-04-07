/*
 * Copyright (c) 2026 Alwin Ibba
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

package net.ibbaa.keepitup.resources;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Log;

public class NoBackupPreferenceManager {

    private final Context context;

    public NoBackupPreferenceManager(Context context) {
        this.context = context;
    }

    public void removeAllPreferences() {
        Log.d(NoBackupPreferenceManager.class.getName(), "removeAllPreferences");
        SharedPreferences.Editor editor = getNoBackupPreferencesEditor();
        editor.clear();
        editor.commit();
    }

    public void removePreferenceValue(String key) {
        Log.d(NoBackupPreferenceManager.class.getName(), "removePreferenceValue, key is " + key);
        SharedPreferences.Editor editor = getNoBackupPreferencesEditor();
        editor.remove(key);
        editor.commit();
    }

    public boolean getPreferenceBoolean(String key, boolean defaultValue) {
        Log.d(NoBackupPreferenceManager.class.getName(), "getPreferenceBoolean, key is " + key + ", default is " + defaultValue);
        boolean value = getNoBackupPreferences().getBoolean(key, defaultValue);
        Log.d(NoBackupPreferenceManager.class.getName(), "resolved value is " + value);
        return value;
    }

    public void setPreferenceBoolean(String key, boolean value) {
        Log.d(NoBackupPreferenceManager.class.getName(), "setPreferenceBoolean, key is " + key + ", value is " + value);
        SharedPreferences.Editor editor = getNoBackupPreferencesEditor();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public boolean getPreferenceAskedNotificationPermission() {
        Log.d(NoBackupPreferenceManager.class.getName(), "getPreferenceAskedNotificationPermission");
        return getPreferenceBoolean(getResources().getString(R.string.asked_notification_permission_key), getResources().getBoolean(R.bool.asked_notification_permission_default));
    }

    public void setPreferenceAskedNotificationPermission(boolean askedNotificationPermission) {
        Log.d(NoBackupPreferenceManager.class.getName(), "setPreferenceAskedNotificationPermission, askedNotificationPermission is " + askedNotificationPermission);
        setPreferenceBoolean(getResources().getString(R.string.asked_notification_permission_key), askedNotificationPermission);
    }

    public void removePreferenceAskedNotificationPermission() {
        Log.d(NoBackupPreferenceManager.class.getName(), "removePreferenceAskedNotificationPermission");
        removePreferenceValue(getResources().getString(R.string.asked_notification_permission_key));
    }

    private SharedPreferences getNoBackupPreferences() {
        String no_backup_prefs_file = getResources().getString(R.string.no_backup_prefs_file);
        return context.getSharedPreferences(no_backup_prefs_file, Context.MODE_PRIVATE);
    }

    private SharedPreferences.Editor getNoBackupPreferencesEditor() {
        return getNoBackupPreferences().edit();
    }

    private Context getContext() {
        return context;
    }

    private Resources getResources() {
        return getContext().getResources();
    }
}
