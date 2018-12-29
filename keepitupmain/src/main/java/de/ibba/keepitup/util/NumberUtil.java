package de.ibba.keepitup.util;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

public class NumberUtil {

    public static boolean isValidLongValue(Object value) {
        if (value == null) {
            return false;
        }
        try {
            Long.valueOf(value.toString());
            return true;
        } catch (NumberFormatException exc) {
            Log.e(NumberUtil.class.getName(), "Parsing error, value == " + value, exc);
        }
        return false;
    }

    public static long getLongValue(Object value, long defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        try {
            return Long.valueOf(value.toString());
        } catch (NumberFormatException exc) {
            Log.e(NumberUtil.class.getName(), "Parsing error, value == " + value, exc);
        }
        return defaultValue;
    }

    public static long getPreferenceLongSetting(String key, int defaultValue, Context context) {
        String value = PreferenceManager.getDefaultSharedPreferences(context).getString(key, String.valueOf(defaultValue));
        if (value == null) {
            return defaultValue;
        }
        try {
            return Long.valueOf(value);
        } catch (NumberFormatException exc) {
            Log.e(NumberUtil.class.getName(), "Parsing error, key == " + key + ", value == " + value, exc);
        }
        return defaultValue;
    }
}
