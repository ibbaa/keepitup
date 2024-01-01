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

import android.content.Context;

import net.ibbaa.keepitup.model.LogEntry;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.service.SystemFileManager;
import net.ibbaa.keepitup.util.LogUtil;
import net.ibbaa.phonelog.ILogger;
import net.ibbaa.phonelog.LogLevel;

import java.util.concurrent.ConcurrentHashMap;

public class NetworkTaskLog {

    private final static ConcurrentHashMap<String, ILogger> loggers = new ConcurrentHashMap<>();

    public static void initialize(Context context, NetworkTask task) {
        if (task.getIndex() < 0) {
            return;
        }
        ILogger logger = LogUtil.getFileLogger(context, new SystemFileManager(context), task);
        if (logger != null) {
            loggers.put(LogUtil.getLogFileKey(context, task), logger);
        }
    }

    public static void clear() {
        loggers.clear();
    }

    public static ILogger getLogger(Context context, NetworkTask task) {
        String key = LogUtil.getLogFileKey(context, task);
        ILogger logger = loggers.get(key);
        if (logger == null) {
            initialize(context, task);
            logger = loggers.get(key);
        }
        return logger;
    }

    public static void log(Context context, NetworkTask task, LogEntry entry) {
        ILogger logger = getLogger(context, task);
        if (logger == null) {
            return;
        }
        String text = LogUtil.formatLogEntryLog(context, task.getIndex(), entry);
        logger.log("", text, null, LogLevel.DEBUG);
    }
}
