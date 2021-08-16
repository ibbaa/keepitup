package net.ibbaa.keepitup.util;

import android.content.Context;

import java.text.NumberFormat;

import net.ibbaa.keepitup.R;

public class StringUtil {

    public static String getStringValue(Object value, String defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        return value.toString();
    }

    public static String trim(String value) {
        if (value == null) {
            return null;
        }
        return value.trim();
    }

    public static boolean isEmpty(CharSequence value) {
        return value == null || value.length() <= 0;
    }

    public static boolean isEmpty(String value) {
        return isEmpty((CharSequence) value);
    }

    public static String notNull(String value) {
        return notNull((CharSequence) value);
    }

    public static String notNull(CharSequence value) {
        return value == null ? "" : value.toString();
    }

    public static String formatTimeRange(double timeRange, Context context) {
        String unit = context.getResources().getString(R.string.string_msec);
        if (timeRange >= 1000) {
            timeRange /= 1000;
            unit = context.getResources().getString(R.string.string_sec);
        }
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMaximumFractionDigits(2);
        return numberFormat.format(timeRange) + " " + unit;
    }

    public static boolean isTextSelected(String text, int selectionStart, int selectionEnd) {
        if (selectionStart < 0 || selectionEnd < 0 || selectionStart > selectionEnd) {
            return false;
        }
        return selectionStart <= text.length() && selectionEnd <= text.length() && selectionStart != selectionEnd;
    }
}
