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

package net.ibbaa.keepitup.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import androidx.test.platform.app.InstrumentationRegistry;

import net.ibbaa.keepitup.test.mock.TestRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Locale;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class StringUtilTest {

    @Test
    public void testGetStringValue() {
        assertEquals("123", StringUtil.getStringValue("123", ""));
        assertEquals("123", StringUtil.getStringValue(new StringBuilder("123"), ""));
        assertEquals("1", StringUtil.getStringValue(1, ""));
        assertEquals("", StringUtil.getStringValue(null, ""));
    }

    @Test
    public void testTrim() {
        assertNull(StringUtil.trim(null));
        assertEquals("", StringUtil.trim(""));
        assertEquals("", StringUtil.trim(" "));
        assertEquals("abc", StringUtil.trim(" abc "));
    }

    @Test
    public void testIsEmpty() {
        assertTrue(StringUtil.isEmpty(null));
        assertTrue(StringUtil.isEmpty(""));
        assertFalse(StringUtil.isEmpty(" "));
        assertFalse(StringUtil.isEmpty("123"));
    }

    @Test
    public void testNotNull() {
        assertEquals("123", StringUtil.notNull("123"));
        assertEquals("", StringUtil.notNull(""));
        assertEquals("", StringUtil.notNull(null));
    }

    @Test
    public void testFormatTimeRange() {
        InstrumentationRegistry.getInstrumentation().getTargetContext().getResources().getConfiguration().setLocale(Locale.US);
        assertEquals("123 msec", StringUtil.formatTimeRange(123, TestRegistry.getContext()));
        assertEquals("0 msec", StringUtil.formatTimeRange(0, TestRegistry.getContext()));
        assertEquals("0 msec", StringUtil.formatTimeRange(0.0, TestRegistry.getContext()));
        assertEquals("0 msec", StringUtil.formatTimeRange(0.001, TestRegistry.getContext()));
        assertEquals("0.01 msec", StringUtil.formatTimeRange(0.01, TestRegistry.getContext()));
        assertEquals("1 sec", StringUtil.formatTimeRange(1000, TestRegistry.getContext()));
        assertEquals("1,000 sec", StringUtil.formatTimeRange(1000000, TestRegistry.getContext()));
        assertEquals("12.34 sec", StringUtil.formatTimeRange(12345, TestRegistry.getContext()));
        assertEquals("5.55 sec", StringUtil.formatTimeRange(5550, TestRegistry.getContext()));
        assertEquals("12.57 msec", StringUtil.formatTimeRange(12.5678999999, TestRegistry.getContext()));
    }

    @Test
    public void testIsTextSelected() {
        assertTrue(StringUtil.isTextSelected("1", 0, 1));
        assertTrue(StringUtil.isTextSelected("Test", 0, 4));
        assertTrue(StringUtil.isTextSelected("Test", 1, 2));
        assertFalse(StringUtil.isTextSelected("", 0, 0));
        assertFalse(StringUtil.isTextSelected("", -1, 0));
        assertFalse(StringUtil.isTextSelected("", 0, -1));
        assertFalse(StringUtil.isTextSelected("", -1, -1));
        assertFalse(StringUtil.isTextSelected("", 5, 10));
        assertFalse(StringUtil.isTextSelected("Test", -1, 0));
        assertFalse(StringUtil.isTextSelected("Test", 0, -1));
        assertFalse(StringUtil.isTextSelected("Test", -1, -1));
        assertFalse(StringUtil.isTextSelected("Test", 1, 0));
        assertFalse(StringUtil.isTextSelected("Test", 0, 5));
        assertFalse(StringUtil.isTextSelected("TestTest", 5, 5));
    }
}
