/*
 * Copyright (c) 2022. Alwin Ibba
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

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import com.google.common.base.Charsets;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class LogFormatterTest {

    private LogFormatter logFormatter;

    @Before
    public void beforeEachTestMethod() {
        logFormatter = new LogFormatter();
    }

    @Test
    public void testExceptionToString() {
        String exc = logFormatter.exceptionToString(new IllegalArgumentException(new NullPointerException()));
        assertTrue(exc.contains(IllegalArgumentException.class.getName()));
        assertTrue(exc.contains(NullPointerException.class.getName()));
        assertTrue(exc.contains("at net.ibbaa.keepitup.logging.LogFormatterTest.testExceptionToString"));
        assertTrue(exc.contains("Caused by: " + NullPointerException.class.getName()));
    }

    @Test
    public void testFormatLogFileEntry() {
        LogFileEntry entry = getTestEntry(getTestTimestamp(), "thread", LogLevel.DEBUG, "tag", "message", null);
        String message = logFormatter.formatLogFileEntry(entry);
        assertEquals("1985-12-24 01:01:01.999 [thread] DEBUG tag: message" + System.lineSeparator(), message);
        assertArrayEquals(message.getBytes(Charsets.UTF_8), logFormatter.formatLogFileEntry(entry, Charsets.UTF_8));
        try {
            throw new NullPointerException();
        } catch (Exception exc) {
            entry = getTestEntry(getTestTimestamp(), "thread", LogLevel.DEBUG, "tag", "message", exc);
            message = logFormatter.formatLogFileEntry(entry);
            assertTrue(message.startsWith("1985-12-24 01:01:01.999 [thread] DEBUG tag: message"));
            assertTrue(message.contains(NullPointerException.class.getName()));
            assertTrue(message.contains("at net.ibbaa.keepitup.logging.LogFormatterTest.testFormatLogFileEntry"));
            assertArrayEquals(message.getBytes(Charsets.UTF_8), logFormatter.formatLogFileEntry(entry, Charsets.UTF_8));
        }
    }

    private long getTestTimestamp() {
        Calendar calendar = new GregorianCalendar(1985, Calendar.DECEMBER, 24, 1, 1, 1);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTimeInMillis();
    }

    private LogFileEntry getTestEntry(long timestamp, String thread, LogLevel level, String tag, String message, Throwable exc) {
        return new LogFileEntry(timestamp, thread, level, tag, message, exc);
    }
}
