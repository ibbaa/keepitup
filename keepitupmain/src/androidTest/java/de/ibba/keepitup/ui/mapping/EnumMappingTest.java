package de.ibba.keepitup.ui.mapping;

import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.ibba.keepitup.model.AccessType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
        assertEquals("Ping", enumMapping.getAccessTypeText(AccessType.PING));
        assertTrue(enumMapping.getAccessTypeAddressText(AccessType.PING).contains("Host"));
        assertTrue(enumMapping.getAccessTypeAddressText(AccessType.PING).contains("Port"));
        assertEquals("Host:", enumMapping.getAccessTypeAddressLabel(AccessType.PING));
        assertEquals("Port:", enumMapping.getAccessTypePortLabel(AccessType.PING));
        assertEquals("No type", enumMapping.getAccessTypeText(null));
        assertEquals("Host: not applicable", enumMapping.getAccessTypeAddressText(null));
    }
}
