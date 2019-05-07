package de.ibba.keepitup.ui.mapping;

import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.ibba.keepitup.model.AccessType;
import de.ibba.keepitup.test.mock.TestRegistry;
import de.ibba.keepitup.ui.validation.NullValidator;
import de.ibba.keepitup.ui.validation.StandardHostPortValidator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class EnumMappingTest {

    private EnumMapping enumMapping;

    @Before
    public void beforeEachTestMethod() {
        enumMapping = new EnumMapping(TestRegistry.getContext());
    }

    @Test
    public void testGetAccessTypeText() {
        assertEquals("Ping", enumMapping.getAccessTypeText(AccessType.PING));
        assertEquals("Connect", enumMapping.getAccessTypeText(AccessType.CONNECT));
        assertEquals("Host: %s", enumMapping.getAccessTypeAddressText(AccessType.PING));
        assertEquals("Host: %s Port: %d", enumMapping.getAccessTypeAddressText(AccessType.CONNECT));
        assertEquals("Host:", enumMapping.getAccessTypeAddressLabel(AccessType.PING));
        assertEquals("Port:", enumMapping.getAccessTypePortLabel(AccessType.PING));
        assertEquals("Host:", enumMapping.getAccessTypeAddressLabel(AccessType.CONNECT));
        assertEquals("Port:", enumMapping.getAccessTypePortLabel(AccessType.CONNECT));
        assertEquals("No type", enumMapping.getAccessTypeText(null));
        assertEquals("Host: not applicable", enumMapping.getAccessTypeAddressText(null));
    }

    @Test
    public void testGetValidator() {
        assertTrue(enumMapping.getValidator(null) instanceof NullValidator);
        assertTrue(enumMapping.getValidator(AccessType.PING) instanceof StandardHostPortValidator);
    }
}
