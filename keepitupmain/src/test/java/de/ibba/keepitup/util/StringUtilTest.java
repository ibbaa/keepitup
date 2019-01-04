package de.ibba.keepitup.util;

import org.junit.Assert;
import org.junit.Test;

@SuppressWarnings("ConstantConditions")
public class StringUtilTest {

    @Test
    public void testGetStringValue() {
        Assert.assertEquals("123", StringUtil.getStringValue("123", ""));
        Assert.assertEquals("123", StringUtil.getStringValue(new StringBuilder("123"), ""));
        Assert.assertEquals("1", StringUtil.getStringValue(1, ""));
        Assert.assertEquals("", StringUtil.getStringValue(null, ""));
    }

    @Test
    public void testIsEmpty() {
        Assert.assertTrue(StringUtil.isEmpty(null));
        Assert.assertTrue(StringUtil.isEmpty(""));
        Assert.assertFalse(StringUtil.isEmpty(" "));
        Assert.assertFalse(StringUtil.isEmpty("123"));
    }
}
