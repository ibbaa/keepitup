package de.ibba.keepitup.util;

@SuppressWarnings("unused")
public class StringUtil {

    public static String getStringValue(Object value, String defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        return value.toString();
    }

    public static boolean isEmpty(String value) {
        return value == null || value.isEmpty();
    }

    public static String notNull(String value) {
        return value == null ? "" : value;
    }
}
