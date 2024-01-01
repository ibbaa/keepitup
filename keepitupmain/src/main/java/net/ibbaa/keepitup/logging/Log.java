/*
 * Copyright (c) 2024. Alwin Ibba
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

package net.ibbaa.keepitup.logging;

import net.ibbaa.keepitup.BuildConfig;
import net.ibbaa.phonelog.ILogger;
import net.ibbaa.phonelog.LogLevel;

import java.util.concurrent.locks.ReentrantReadWriteLock;

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
