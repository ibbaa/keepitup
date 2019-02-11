package de.ibba.keepitup.util;

import android.util.Log;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Log.class})
public class NumberUtilTest {

    @Before
    public void beforeEachTestMethod() {
        PowerMockito.mockStatic(Log.class);
    }

    @Test
    public void testLongValue() {
        assertTrue(NumberUtil.isValidLongValue("1"));
        assertTrue(NumberUtil.isValidLongValue(2));
        assertTrue(NumberUtil.isValidLongValue(Long.MAX_VALUE));
        assertFalse(NumberUtil.isValidLongValue("xyz"));
        assertFalse(NumberUtil.isValidLongValue(null));
        assertEquals(1, NumberUtil.getLongValue("1", 2));
        assertEquals(2, NumberUtil.getLongValue(2, 3));
        assertEquals(Long.MAX_VALUE, NumberUtil.getLongValue(Long.MAX_VALUE, 3));
        assertEquals(4, NumberUtil.getLongValue("xyz", 4));
        assertEquals(4, NumberUtil.getLongValue(null, 4));
    }

    @Test
    public void testIntValue() {
        assertTrue(NumberUtil.isValidIntValue("1"));
        assertTrue(NumberUtil.isValidIntValue(2));
        assertTrue(NumberUtil.isValidIntValue(Integer.MAX_VALUE));
        assertFalse(NumberUtil.isValidIntValue(Long.MAX_VALUE));
        assertFalse(NumberUtil.isValidIntValue("xyz"));
        assertFalse(NumberUtil.isValidIntValue(null));
        assertEquals(1, NumberUtil.getIntValue("1", 2));
        assertEquals(2, NumberUtil.getIntValue(2, 3));
        assertEquals(Integer.MAX_VALUE, NumberUtil.getIntValue(Integer.MAX_VALUE, 3));
        assertEquals(4, NumberUtil.getIntValue("xyz", 4));
        assertEquals(4, NumberUtil.getIntValue(null, 4));
    }
}
