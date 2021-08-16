package net.ibbaa.keepitup.logging;

import java.util.concurrent.locks.ReentrantReadWriteLock;

import net.ibbaa.keepitup.BuildConfig;

public class Log {

    private static final ReentrantReadWriteLock debugLoggerLock = new ReentrantReadWriteLock();

    private static ILogger debugLogger;

    public static void initialize(ILogger debugLogger) {
        debugLoggerLock.writeLock().lock();
        Log.debugLogger = debugLogger;
        debugLoggerLock.writeLock().unlock();
    }

    public static ILogger getLogger() {
        debugLoggerLock.readLock().lock();
        try {
            return debugLogger;
        } finally {
            debugLoggerLock.readLock().unlock();
        }
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

    public static void i(String tag, String message, Throwable throwable) {
        if (BuildConfig.DEBUG) {
            ILogger logger = getLogger();
            if (logger != null) {
                logger.log(tag, message, throwable, LogLevel.INFO);
            }
        }
        android.util.Log.i(tag, message, throwable);
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

    public static void d(String tag, String message, Throwable throwable) {
        if (BuildConfig.DEBUG) {
            ILogger logger = getLogger();
            if (logger != null) {
                logger.log(tag, message, throwable, LogLevel.DEBUG);
            }
        }
        android.util.Log.d(tag, message, throwable);
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

    public static void e(String tag, String message, Throwable throwable) {
        if (BuildConfig.DEBUG) {
            ILogger logger = getLogger();
            if (logger != null) {
                logger.log(tag, message, throwable, LogLevel.ERROR);
            }
        }
        android.util.Log.e(tag, message, throwable);
    }
}