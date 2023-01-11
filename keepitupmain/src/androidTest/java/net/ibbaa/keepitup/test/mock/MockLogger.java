/*
 * Copyright (c) 2023. Alwin Ibba
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

package net.ibbaa.keepitup.test.mock;

import net.ibbaa.keepitup.logging.ILogger;
import net.ibbaa.keepitup.logging.LogFileEntry;
import net.ibbaa.keepitup.logging.LogLevel;

import java.util.ArrayList;
import java.util.List;

public class MockLogger implements ILogger {

    private final List<LogFileEntry> logEntries;

    public MockLogger() {
        logEntries = new ArrayList<>();
    }

    @Override
    public void log(String tag, String message, Throwable throwable, LogLevel level) {
        LogFileEntry logEntry = new LogFileEntry(1, "thread", level, tag, message, throwable);
        logEntries.add(logEntry);
    }

    public void reset() {
        logEntries.clear();
    }

    public boolean wasLogCalled() {
        return numberLogEntries() > 0;
    }

    public int numberLogEntries() {
        return logEntries.size();
    }

    public LogFileEntry getEntry(int index) {
        return logEntries.get(index);
    }
}
