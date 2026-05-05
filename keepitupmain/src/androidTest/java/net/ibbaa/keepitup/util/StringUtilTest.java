/*
 * Copyright (c) 2026 Alwin Ibba
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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import androidx.test.platform.app.InstrumentationRegistry;

import net.ibbaa.keepitup.test.mock.TestRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class StringUtilTest {

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
    public void testIsTrimmedEmpty() {
        assertTrue(StringUtil.isTrimmedEmpty(null));
        assertTrue(StringUtil.isTrimmedEmpty(""));
        assertTrue(StringUtil.isTrimmedEmpty(" "));
        assertFalse(StringUtil.isTrimmedEmpty("123"));
    }

    @Test
    public void testNotNull() {
        assertEquals("123", StringUtil.notNull("123"));
        assertEquals("", StringUtil.notNull(""));
        assertEquals("", StringUtil.notNull(null));
    }

    @Test
    public void testMaskSecret() {
        assertEquals("123", StringUtil.maskSecret("123", false));
        assertEquals("************", StringUtil.maskSecret("123", true));
    }

    @Test
    @SuppressWarnings("UnnecessaryUnicodeEscape")
    public void testNormalizeString() {
        assertEquals("", StringUtil.normalizeString(null));
        assertEquals("abc", StringUtil.normalizeString("abc"));
        assertEquals("Hello \uD83D\uDE0A", StringUtil.normalizeString("Hello \uD83D\uDE0A"));
        assertEquals("\u00E9", StringUtil.normalizeString("\u0065\u0301"));
        assertEquals("\u0031", StringUtil.normalizeString("\u2460"));
        assertEquals("\u00C5", StringUtil.normalizeString("\u212B"));
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

    @Test
    public void testBase64() {
        assertNull(StringUtil.byteArrayToBase64(null));
        assertNull(StringUtil.base64ToByteArray(null));
        assertEquals("SmF2YQ==", StringUtil.byteArrayToBase64(new byte[]{74, 97, 118, 97}));
        assertArrayEquals(new byte[]{74, 97, 118, 97}, StringUtil.base64ToByteArray("SmF2YQ=="));
        byte[] original = "This is a Test".getBytes(StandardCharsets.UTF_8);
        String base64 = StringUtil.byteArrayToBase64(original);
        byte[] result = StringUtil.base64ToByteArray(base64);
        assertArrayEquals(result, original);
    }

    @Test
    public void testSplitAtFirstColon() {
        assertArrayEquals(new String[]{"key", "value"}, StringUtil.splitAtFirstColon("key:value"));
        assertArrayEquals(new String[]{"key", "val:ue"}, StringUtil.splitAtFirstColon("key:val:ue"));
        assertArrayEquals(new String[]{"", "value"}, StringUtil.splitAtFirstColon(":value"));
        assertArrayEquals(new String[]{"key", ""}, StringUtil.splitAtFirstColon("key:"));
        assertArrayEquals(new String[]{"", ""}, StringUtil.splitAtFirstColon(":"));
        assertArrayEquals(new String[]{"", ""}, StringUtil.splitAtFirstColon("abcdefghijklmn"));
        assertArrayEquals(new String[]{"", ""}, StringUtil.splitAtFirstColon(""));
        assertArrayEquals(new String[]{"", ""}, StringUtil.splitAtFirstColon(null));
        assertArrayEquals(new String[]{"", ""}, StringUtil.splitAtFirstColon("   "));
        assertArrayEquals(new String[]{"key ", " value"}, StringUtil.splitAtFirstColon("key : value"));
        assertNotNull(StringUtil.splitAtFirstColon(null));
        assertNotNull(StringUtil.splitAtFirstColon(null)[0]);
        assertNotNull(StringUtil.splitAtFirstColon(null)[1]);
        assertEquals(2, StringUtil.splitAtFirstColon("key:value").length);
        assertEquals(2, StringUtil.splitAtFirstColon(null).length);
        assertArrayEquals(new String[]{"   ", "   "}, StringUtil.splitAtFirstColon("   :   "));
    }

    @Test
    public void testSplitAtLastColon() {
        assertArrayEquals(new String[]{"key", "value"}, StringUtil.splitAtLastColon("key:value"));
        assertArrayEquals(new String[]{"key:val", "ue"}, StringUtil.splitAtLastColon("key:val:ue"));
        assertArrayEquals(new String[]{"2001:0db8:85a3:08d3::0370:7344", "80"}, StringUtil.splitAtLastColon("2001:0db8:85a3:08d3::0370:7344:80"));
        assertArrayEquals(new String[]{"", "value"}, StringUtil.splitAtLastColon(":value"));
        assertArrayEquals(new String[]{"key", ""}, StringUtil.splitAtLastColon("key:"));
        assertArrayEquals(new String[]{"", ""}, StringUtil.splitAtLastColon(":"));
        assertArrayEquals(new String[]{"", ""}, StringUtil.splitAtLastColon("abcdefghijklmn"));
        assertArrayEquals(new String[]{"", ""}, StringUtil.splitAtLastColon(""));
        assertArrayEquals(new String[]{"", ""}, StringUtil.splitAtLastColon(null));
        assertArrayEquals(new String[]{"", ""}, StringUtil.splitAtLastColon("   "));
        assertArrayEquals(new String[]{"key ", " value"}, StringUtil.splitAtLastColon("key : value"));
        assertNotNull(StringUtil.splitAtLastColon(null));
        assertNotNull(StringUtil.splitAtLastColon(null)[0]);
        assertNotNull(StringUtil.splitAtLastColon(null)[1]);
        assertEquals(2, StringUtil.splitAtLastColon("key:value").length);
        assertEquals(2, StringUtil.splitAtLastColon(null).length);
        assertArrayEquals(new String[]{"   ", "   "}, StringUtil.splitAtLastColon("   :   "));
    }
}
