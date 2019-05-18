package de.ibba.keepitup.util;

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
            Log.d(NumberUtil.class.getName(), "Parsing error, value == " + value, exc);
        }
        return false;
    }

    public static long getLongValue(Object value, long defaultValue) {
        if (isValidLongValue(value)) {
            return Long.valueOf(value.toString());
        }
        return defaultValue;
    }

    public static boolean isValidIntValue(Object value) {
        if (value == null) {
            return false;
        }
        try {
            Integer.valueOf(value.toString());
            return true;
        } catch (NumberFormatException exc) {
            Log.d(NumberUtil.class.getName(), "Parsing error, value == " + value, exc);
        }
        return false;
    }

    public static int getIntValue(Object value, int defaultValue) {
        if (isValidIntValue(value)) {
            return Integer.valueOf(value.toString());
        }
        return defaultValue;
    }
}
