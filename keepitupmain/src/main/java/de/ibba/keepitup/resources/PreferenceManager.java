package de.ibba.keepitup.resources;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;

import de.ibba.keepitup.R;
import de.ibba.keepitup.model.AccessType;

public class PreferenceManager {

    private final Context context;

    public PreferenceManager(Context context) {
        this.context = context;
    }

    public void removeAllPreferences() {
        Log.d(PreferenceManager.class.getName(), "removeAllPreferences");
        SharedPreferences.Editor editor = getDefaultSharedPreferencesEditor();
        editor.clear();
        editor.commit();
    }

    public void removePreferenceValue(String key) {
        Log.d(PreferenceManager.class.getName(), "removePreferenceValue, key is " + key);
        SharedPreferences.Editor editor = getDefaultSharedPreferencesEditor();
        editor.remove(key);
        editor.commit();
    }

    public boolean getPreferenceBoolean(String key, boolean defaultValue) {
        Log.d(PreferenceManager.class.getName(), "getPreferenceBoolean, key is " + key + ", default is " + defaultValue);
        boolean value = getDefaultSharedPreferences().getBoolean(key, defaultValue);
        Log.d(PreferenceManager.class.getName(), "resolved value is " + value);
        return value;
    }

    public void setPreferenceBoolean(String key, boolean value) {
        Log.d(PreferenceManager.class.getName(), "setPreferenceBoolean, key is " + key + ", value is " + value);
        SharedPreferences.Editor editor = getDefaultSharedPreferencesEditor();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public int getPreferenceInt(String key, int defaultValue) {
        Log.d(PreferenceManager.class.getName(), "getPreferenceInt, key is " + key + ", default is " + defaultValue);
        int value = getDefaultSharedPreferences().getInt(key, defaultValue);
        Log.d(PreferenceManager.class.getName(), "resolved value is " + value);
        return value;
    }

    public void setPreferenceInt(String key, int value) {
        Log.d(PreferenceManager.class.getName(), "setPreferenceInt, key is " + key + ", value is " + value);
        SharedPreferences.Editor editor = getDefaultSharedPreferencesEditor();
        editor.putInt(key, value);
        editor.commit();
    }

    public long getPreferenceLong(String key, long defaultValue) {
        Log.d(PreferenceManager.class.getName(), "getPreferenceLong, key is " + key + ", default is " + defaultValue);
        long value = getDefaultSharedPreferences().getLong(key, defaultValue);
        Log.d(PreferenceManager.class.getName(), "resolved value is " + value);
        return value;
    }

    public void setPreferenceLong(String key, long value) {
        Log.d(PreferenceManager.class.getName(), "setPreferenceLong, key is " + key + ", value is " + value);
        SharedPreferences.Editor editor = getDefaultSharedPreferencesEditor();
        editor.putLong(key, value);
        editor.commit();
    }

    public String getPreferenceString(String key, String defaultValue) {
        Log.d(PreferenceManager.class.getName(), "getPreferenceString, key is " + key + ", default is " + defaultValue);
        String value = getDefaultSharedPreferences().getString(key, defaultValue);
        Log.d(PreferenceManager.class.getName(), "resolved value is " + value);
        return value;
    }

    public void setPreferenceString(String key, String value) {
        Log.d(PreferenceManager.class.getName(), "setPreferenceLong, key is " + key + ", value is " + value);
        SharedPreferences.Editor editor = getDefaultSharedPreferencesEditor();
        editor.putString(key, value);
        editor.commit();
    }

    public AccessType getPreferenceAccessType() {
        Log.d(PreferenceManager.class.getName(), "getPreferenceAccessType");
        return AccessType.valueOf(getPreferenceString(getResources().getString(R.string.task_accesstype_key), getResources().getString(R.string.task_accesstype_default)));
    }

    public void setPreferenceAccessType(AccessType accessType) {
        Log.d(PreferenceManager.class.getName(), "setPreferenceAccessType, type is " + accessType);
        setPreferenceString(getResources().getString(R.string.task_accesstype_key), accessType.name());
    }

    public void removePreferenceAccessType() {
        Log.d(PreferenceManager.class.getName(), "removePreferenceAccessType");
        removePreferenceValue(getResources().getString(R.string.task_accesstype_key));
    }

    public String getPreferenceAddress() {
        Log.d(PreferenceManager.class.getName(), "getPreferenceAddress");
        return getPreferenceString(getResources().getString(R.string.task_address_key), getResources().getString(R.string.task_address_default));
    }

    public void setPreferenceAddress(String address) {
        Log.d(PreferenceManager.class.getName(), "setPreferenceAddress, address is " + address);
        setPreferenceString(getResources().getString(R.string.task_address_key), address);
    }

    public void removePreferenceAddress() {
        Log.d(PreferenceManager.class.getName(), "removePreferenceAddress");
        removePreferenceValue(getResources().getString(R.string.task_address_key));
    }

    public int getPreferencePort() {
        Log.d(PreferenceManager.class.getName(), "getPreferencePort");
        return getPreferenceInt(getResources().getString(R.string.task_port_key), getResources().getInteger(R.integer.task_port_default));
    }

    public void setPreferencePort(int port) {
        Log.d(PreferenceManager.class.getName(), "setPreferencePort, port is " + port);
        setPreferenceInt(getResources().getString(R.string.task_port_key), port);
    }

    public void removePreferencePort() {
        Log.d(PreferenceManager.class.getName(), "removePreferencePort");
        removePreferenceValue(getResources().getString(R.string.task_port_key));
    }

    public int getPreferenceInterval() {
        Log.d(PreferenceManager.class.getName(), "getPreferenceInterval");
        return getPreferenceInt(getResources().getString(R.string.task_interval_key), getResources().getInteger(R.integer.task_interval_default));
    }

    public void setPreferenceInterval(int interval) {
        Log.d(PreferenceManager.class.getName(), "setPreferenceInterval, interval is " + interval);
        setPreferenceInt(getResources().getString(R.string.task_interval_key), interval);
    }

    public void removePreferenceInterval() {
        Log.d(PreferenceManager.class.getName(), "removePreferenceInterval");
        removePreferenceValue(getResources().getString(R.string.task_interval_key));
    }

    public boolean getPreferenceOnlyWifi() {
        Log.d(PreferenceManager.class.getName(), "getPreferenceOnlyWifi");
        return getPreferenceBoolean(getResources().getString(R.string.task_onlywifi_key), getResources().getBoolean(R.bool.task_onlywifi_default));
    }

    public void setPreferenceOnlyWifi(boolean onlyWifi) {
        Log.d(PreferenceManager.class.getName(), "setPreferenceOnlyWifi, onlyWifi is " + onlyWifi);
        setPreferenceBoolean(getResources().getString(R.string.task_onlywifi_key), onlyWifi);
    }

    public void removePreferenceOnlyWifi() {
        Log.d(PreferenceManager.class.getName(), "removePreferenceOnlyWifi");
        removePreferenceValue(getResources().getString(R.string.task_onlywifi_key));
    }

    public boolean getPreferenceNotification() {
        Log.d(PreferenceManager.class.getName(), "getPreferenceOnlyWifi");
        return getPreferenceBoolean(getResources().getString(R.string.task_notification_key), getResources().getBoolean(R.bool.task_notification_default));
    }

    public void setPreferenceNotification(boolean notification) {
        Log.d(PreferenceManager.class.getName(), "setPreferenceNotification, notification is " + notification);
        setPreferenceBoolean(getResources().getString(R.string.task_notification_key), notification);
    }

    public void removePreferenceNotification() {
        Log.d(PreferenceManager.class.getName(), "removePreferenceNotification");
        removePreferenceValue(getResources().getString(R.string.task_notification_key));
    }

    public int getPreferencePingCount() {
        Log.d(PreferenceManager.class.getName(), "getPreferencePingCount");
        return getPreferenceInt(getResources().getString(R.string.ping_count_key), getResources().getInteger(R.integer.ping_count_default));
    }

    public void setPreferencePingCount(int timeout) {
        Log.d(PreferenceManager.class.getName(), "setPreferencePingCount, timeout is " + timeout);
        setPreferenceInt(getResources().getString(R.string.ping_count_key), timeout);
    }

    public void removePreferencePingCount() {
        Log.d(PreferenceManager.class.getName(), "removePreferencePingCount");
        removePreferenceValue(getResources().getString(R.string.ping_count_key));
    }

    private SharedPreferences getDefaultSharedPreferences() {
        return android.preference.PreferenceManager.getDefaultSharedPreferences(getContext());
    }

    private SharedPreferences.Editor getDefaultSharedPreferencesEditor() {
        return getDefaultSharedPreferences().edit();
    }

    private Context getContext() {
        return context;
    }

    private Resources getResources() {
        return getContext().getResources();
    }
}
