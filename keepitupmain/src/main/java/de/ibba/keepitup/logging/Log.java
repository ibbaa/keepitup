package de.ibba.keepitup.logging;

import de.ibba.keepitup.BuildConfig;

public class Log {

    private static ILogger debugLogger;

    public static synchronized void initialize(ILogger debugLogger) {
        Log.debugLogger = debugLogger;
    }

    private static synchronized ILogger getLogger() {
        return debugLogger;
    }

    public static void i(String tag, String message) {
        if (BuildConfig.DEBUG) {
            ILogger logger = getLogger();
            if (logger != null) {
                logger.log(tag, message, null, LogLevel.INFO);
            }
        }
        android.util.Log.i(tag, message);
    }

    public static void i(String tag, String message, Throwable throwabler) {
        if (BuildConfig.DEBUG) {
            ILogger logger = getLogger();
            if (logger != null) {
                logger.log(tag, message, throwabler, LogLevel.INFO);
            }
        }
        android.util.Log.i(tag, message, throwabler);
    }

    public static void d(String tag, String message) {
        if (BuildConfig.DEBUG) {
            ILogger logger = getLogger();
            if (logger != null) {
                logger.log(tag, message, null, LogLevel.DEBUG);
            }
        }
        android.util.Log.d(tag, message);
    }

    public static void d(String tag, String message, Throwable throwabler) {
        if (BuildConfig.DEBUG) {
            ILogger logger = getLogger();
            if (logger != null) {
                logger.log(tag, message, throwabler, LogLevel.DEBUG);
            }
        }
        android.util.Log.d(tag, message, throwabler);
    }

    public static void e(String tag, String message) {
        if (BuildConfig.DEBUG) {
            ILogger logger = getLogger();
            if (logger != null) {
                logger.log(tag, message, null, LogLevel.ERROR);
            }
        }
        android.util.Log.e(tag, message);
    }

    public static void e(String tag, String message, Throwable throwabler) {
        if (BuildConfig.DEBUG) {
            ILogger logger = getLogger();
            if (logger != null) {
                logger.log(tag, message, throwabler, LogLevel.ERROR);
            }
        }
        android.util.Log.e(tag, message, throwabler);
    }
}
