package de.ibba.keepitup.util;

import android.util.Log;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

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

    public static boolean isValidDoubleValue(Object value) {
        if (value == null) {
            return false;
        }
        try {
            NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
            numberFormat.parse(value.toString());
            return true;
        } catch (ParseException exc) {
            Log.d(NumberUtil.class.getName(), "Parsing error, value == " + value, exc);
        }
        return false;
    }

    public static double getDoubleValue(Object value, double defaultValue) {
        if (isValidDoubleValue(value)) {
            NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
            try {
                return numberFormat.parse(value.toString()).doubleValue();
            } catch (ParseException exc) {
                Log.d(NumberUtil.class.getName(), "Parsing error, value == " + value, exc);
            }
        }
        return defaultValue;
    }
}
