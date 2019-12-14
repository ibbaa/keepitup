package de.ibba.keepitup.logging;

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
