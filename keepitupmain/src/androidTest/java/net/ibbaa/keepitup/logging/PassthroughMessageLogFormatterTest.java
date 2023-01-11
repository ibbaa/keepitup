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

package net.ibbaa.keepitup.logging;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import com.google.common.base.Charsets;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.GregorianCalendar;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class PassthroughMessageLogFormatterTest {

    private PassthroughMessageLogFormatter logFormatter;

    @Before
    public void beforeEachTestMethod() {
        logFormatter = new PassthroughMessageLogFormatter();
    }

    @Test
    public void testFormatLogFileEntry() {
        LogFileEntry entry = getTestEntry(getTestTimestamp(), "thread", LogLevel.DEBUG, "tag", "message", null);
        String message = logFormatter.formatLogFileEntry(entry);
        assertEquals("message" + System.lineSeparator(), message);
        assertArrayEquals(message.getBytes(Charsets.UTF_8), logFormatter.formatLogFileEntry(entry, Charsets.UTF_8));
        entry = getTestEntry(1, null, LogLevel.DEBUG, null, "message", null);
        message = logFormatter.formatLogFileEntry(entry);
        assertEquals("message" + System.lineSeparator(), message);
        assertArrayEquals(message.getBytes(Charsets.UTF_8), logFormatter.formatLogFileEntry(entry, Charsets.UTF_8));
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
