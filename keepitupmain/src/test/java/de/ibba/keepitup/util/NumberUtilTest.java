package de.ibba.keepitup.util;

import android.util.Log;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Log.class})
public class NumberUtilTest {

    @Before
    public void beforeEachTestMethod() {
        PowerMockito.mockStatic(Log.class);
    }

    @Test
    public void testLongValue() {
        Assert.assertTrue(NumberUtil.isValidLongValue("1"));
        Assert.assertTrue(NumberUtil.isValidLongValue(2));
        Assert.assertTrue(NumberUtil.isValidLongValue(Long.MAX_VALUE));
        Assert.assertFalse(NumberUtil.isValidLongValue("xyz"));
        Assert.assertFalse(NumberUtil.isValidLongValue(null));
        Assert.assertEquals(1, NumberUtil.getLongValue("1", 2));
        Assert.assertEquals(2, NumberUtil.getLongValue(2, 3));
        Assert.assertEquals(Long.MAX_VALUE, NumberUtil.getLongValue(Long.MAX_VALUE, 3));
        Assert.assertEquals(4, NumberUtil.getLongValue("xyz", 4));
        Assert.assertEquals(4, NumberUtil.getLongValue(null, 4));
    }

    @Test
    public void testIntValue() {
        Assert.assertTrue(NumberUtil.isValidIntValue("1"));
        Assert.assertTrue(NumberUtil.isValidIntValue(2));
        Assert.assertTrue(NumberUtil.isValidIntValue(Integer.MAX_VALUE));
        Assert.assertFalse(NumberUtil.isValidIntValue(Long.MAX_VALUE));
        Assert.assertFalse(NumberUtil.isValidIntValue("xyz"));
        Assert.assertFalse(NumberUtil.isValidIntValue(null));
        Assert.assertEquals(1, NumberUtil.getIntValue("1", 2));
        Assert.assertEquals(2, NumberUtil.getIntValue(2, 3));
        Assert.assertEquals(Integer.MAX_VALUE, NumberUtil.getIntValue(Integer.MAX_VALUE, 3));
        Assert.assertEquals(4, NumberUtil.getIntValue("xyz", 4));
        Assert.assertEquals(4, NumberUtil.getIntValue(null, 4));
    }
}
