package de.ibba.keepitup.resources;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.Log;

import de.ibba.keepitup.R;
import de.ibba.keepitup.model.AccessType;

public class NetworkTaskPreferenceManager {

    private final Context context;

    public NetworkTaskPreferenceManager(Context context) {
        this.context = context;
    }

    public void removeAllPreferences() {
        Log.d(NetworkTaskPreferenceManager.class.getName(), "removeAllPreferences");
        SharedPreferences.Editor editor = getDefaultSharedPreferencesEditor();
        editor.clear();
        editor.commit();
    }

    public void removePreferenceValue(String key) {
        Log.d(NetworkTaskPreferenceManager.class.getName(), "removePreferenceValue, key is " + key);
        SharedPreferences.Editor editor = getDefaultSharedPreferencesEditor();
        editor.remove(key);
        editor.commit();
    }

    public boolean getPreferenceBoolean(String key, boolean defaultValue) {
        Log.d(NetworkTaskPreferenceManager.class.getName(), "getPreferenceBoolean, key is " + key + ", default is " + defaultValue);
        boolean value = getDefaultSharedPreferences().getBoolean(key, defaultValue);
        Log.d(NetworkTaskPreferenceManager.class.getName(), "resolved value is " + value);
        return value;
    }

    public void setPreferenceBoolean(String key, boolean value) {
        Log.d(NetworkTaskPreferenceManager.class.getName(), "setPreferenceBoolean, key is " + key + ", value is " + value);
        SharedPreferences.Editor editor = getDefaultSharedPreferencesEditor();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public int getPreferenceInt(String key, int defaultValue) {
        Log.d(NetworkTaskPreferenceManager.class.getName(), "getPreferenceInt, key is " + key + ", default is " + defaultValue);
        int value = getDefaultSharedPreferences().getInt(key, defaultValue);
        Log.d(NetworkTaskPreferenceManager.class.getName(), "resolved value is " + value);
        return value;
    }

    public void setPreferenceInt(String key, int value) {
        Log.d(NetworkTaskPreferenceManager.class.getName(), "setPreferenceInt, key is " + key + ", value is " + value);
        SharedPreferences.Editor editor = getDefaultSharedPreferencesEditor();
        editor.putInt(key, value);
        editor.commit();
    }

    public long getPreferenceLong(String key, long defaultValue) {
        Log.d(NetworkTaskPreferenceManager.class.getName(), "getPreferenceLong, key is " + key + ", default is " + defaultValue);
        long value = getDefaultSharedPreferences().getLong(key, defaultValue);
        Log.d(NetworkTaskPreferenceManager.class.getName(), "resolved value is " + value);
        return value;
    }

    public void setPreferenceLong(String key, long value) {
        Log.d(NetworkTaskPreferenceManager.class.getName(), "setPreferenceLong, key is " + key + ", value is " + value);
        SharedPreferences.Editor editor = getDefaultSharedPreferencesEditor();
        editor.putLong(key, value);
        editor.commit();
    }

    public String getPreferenceString(String key, String defaultValue) {
        Log.d(NetworkTaskPreferenceManager.class.getName(), "getPreferenceString, key is " + key + ", default is " + defaultValue);
        String value = getDefaultSharedPreferences().getString(key, defaultValue);
        Log.d(NetworkTaskPreferenceManager.class.getName(), "resolved value is " + value);
        return value;
    }

    public void setPreferenceString(String key, String value) {
        Log.d(NetworkTaskPreferenceManager.class.getName(), "setPreferenceLong, key is " + key + ", value is " + value);
        SharedPreferences.Editor editor = getDefaultSharedPreferencesEditor();
        editor.putString(key, value);
        editor.commit();
    }

    public AccessType getPreferenceAccessType() {
        Log.d(NetworkTaskPreferenceManager.class.getName(), "getPreferenceAccessType");
        return AccessType.valueOf(getPreferenceString(getResources().getString(R.string.task_accesstype_key), getResources().getString(R.string.task_accesstype_default)));
    }

    public void setPreferenceAccessType(AccessType accessType) {
        Log.d(NetworkTaskPreferenceManager.class.getName(), "setPreferenceAccessType, type is " + accessType);
        setPreferenceString(getResources().getString(R.string.task_accesstype_key), accessType.name());
    }

    public String getPreferenceAddress() {
        Log.d(NetworkTaskPreferenceManager.class.getName(), "getPreferenceAddress");
        return getPreferenceString(getResources().getString(R.string.task_address_key), getResources().getString(R.string.task_address_default));
    }

    public void setPreferenceAddress(String address) {
        Log.d(NetworkTaskPreferenceManager.class.getName(), "setPreferenceAddress, address is " + address);
        setPreferenceString(getResources().getString(R.string.task_address_key), address);
    }

    public int getPreferencePort() {
        Log.d(NetworkTaskPreferenceManager.class.getName(), "getPreferencePort");
        return getPreferenceInt(getResources().getString(R.string.task_port_key), getResources().getInteger(R.integer.task_port_default));
    }

    public void setPreferencePort(int port) {
        Log.d(NetworkTaskPreferenceManager.class.getName(), "setPreferencePort, port is " + port);
        setPreferenceInt(getResources().getString(R.string.task_port_key), port);
    }

    public int getPreferenceInterval() {
        Log.d(NetworkTaskPreferenceManager.class.getName(), "getPreferenceInterval");
        return getPreferenceInt(getResources().getString(R.string.task_interval_key), getResources().getInteger(R.integer.task_interval_default));
    }

    public void setPreferenceInterval(int interval) {
        Log.d(NetworkTaskPreferenceManager.class.getName(), "setPreferenceInterval, interval is " + interval);
        setPreferenceInt(getResources().getString(R.string.task_interval_key), interval);
    }

    public boolean getPreferenceOnlyWifi() {
        Log.d(NetworkTaskPreferenceManager.class.getName(), "getPreferenceOnlyWifi");
        return getPreferenceBoolean(getResources().getString(R.string.task_onlywifi_key), getResources().getBoolean(R.bool.task_onlywifi_default));
    }

    public void setPreferenceOnlyWifi(boolean onlyWifi) {
        Log.d(NetworkTaskPreferenceManager.class.getName(), "setPreferenceOnlyWifi, onlyWifi is " + onlyWifi);
        setPreferenceBoolean(getResources().getString(R.string.task_onlywifi_key), onlyWifi);
    }

    public boolean getPreferenceNotification() {
        Log.d(NetworkTaskPreferenceManager.class.getName(), "getPreferenceOnlyWifi");
        return getPreferenceBoolean(getResources().getString(R.string.task_notification_key), getResources().getBoolean(R.bool.task_notification_default));
    }

    public void setPreferenceNotification(boolean notification) {
        Log.d(NetworkTaskPreferenceManager.class.getName(), "setPreferenceNotification, notification is " + notification);
        setPreferenceBoolean(getResources().getString(R.string.task_notification_key), notification);
    }

    private SharedPreferences getDefaultSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(getContext());
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
