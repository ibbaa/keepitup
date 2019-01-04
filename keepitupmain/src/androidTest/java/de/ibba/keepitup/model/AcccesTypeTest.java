package de.ibba.keepitup.model;

import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class AcccesTypeTest {

    @Test
    public void testGetTextAndCode() {
        Assert.assertEquals(1, AccessType.PING.getCode());
        Assert.assertEquals("Ping", AccessType.PING.getTypeText(InstrumentationRegistry.getTargetContext()));
        Assert.assertTrue(AccessType.PING.getAddressText(InstrumentationRegistry.getTargetContext()).contains("Host"));
        Assert.assertTrue(AccessType.PING.getAddressText(InstrumentationRegistry.getTargetContext()).contains("Port"));
    }
}
