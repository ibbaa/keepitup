/*
 * Copyright (c) 2021. Alwin Ibba
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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LogFormatter {

    private final static String LOG_TIMESTAMP_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";

    public String formatLogFileEntry(LogFileEntry entry) {
        SimpleDateFormat logTimestampDateFormat = new SimpleDateFormat(LOG_TIMESTAMP_PATTERN, Locale.US);
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append(logTimestampDateFormat.format(new Date(entry.getTimestamp())));
        String threadName = entry.getThread();
        if (threadName != null && !threadName.isEmpty()) {
            messageBuilder.append(" [");
            messageBuilder.append(threadName);
            messageBuilder.append("]");
        }
        messageBuilder.append(" ");
        messageBuilder.append(entry.getLevel().name());
        messageBuilder.append(" ");
        messageBuilder.append(entry.getTag());
        messageBuilder.append(": ");
        messageBuilder.append(entry.getMessage());
        Throwable exception = entry.getThrowable();
        if (exception != null) {
            messageBuilder.append(System.lineSeparator());
            messageBuilder.append(exceptionToString(exception));
        }
        messageBuilder.append(System.lineSeparator());
        return messageBuilder.toString();
    }

    public String exceptionToString(Throwable exc) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        exc.printStackTrace(printWriter);
        return stringWriter.toString();
    }

    public byte[] formatLogFileEntry(LogFileEntry entry, Charset encoding) {
        return formatLogFileEntry(entry).getBytes(encoding);
    }
}
