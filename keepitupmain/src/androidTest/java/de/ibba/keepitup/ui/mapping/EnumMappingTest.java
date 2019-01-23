package de.ibba.keepitup.ui.mapping;

import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.ibba.keepitup.model.AccessType;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class EnumMappingTest {

    private EnumMapping enumMapping;

    @Before
    public void beforeEachTestMethod() {
        enumMapping = new EnumMapping(InstrumentationRegistry.getTargetContext());
    }

    @Test
    public void testAccessTypeText() {
        Assert.assertEquals("Ping", enumMapping.getAccessTypeText(AccessType.PING));
        Assert.assertTrue(enumMapping.getAccessTypeAddressText(AccessType.PING).contains("Host"));
        Assert.assertTrue(enumMapping.getAccessTypeAddressText(AccessType.PING).contains("Port"));
        Assert.assertEquals("Host:", enumMapping.getAccessTypeAddressLabel(AccessType.PING));
        Assert.assertEquals("Port:", enumMapping.getAccessTypePortLabel(AccessType.PING));
        Assert.assertEquals("No type", enumMapping.getAccessTypeText(null));
        Assert.assertEquals("Host: not applicable", enumMapping.getAccessTypeAddressText(null));
    }
}
