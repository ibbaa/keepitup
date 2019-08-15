package de.ibba.keepitup.util;

import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
        assertEquals(null, StringUtil.trim(null));
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
}
