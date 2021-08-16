package net.ibbaa.keepitup.ui.mapping;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.ibbaa.keepitup.model.AccessType;
import net.ibbaa.keepitup.test.mock.TestRegistry;
import net.ibbaa.keepitup.ui.dialog.ContextOption;
import net.ibbaa.keepitup.ui.validation.NullValidator;
import net.ibbaa.keepitup.ui.validation.StandardHostPortValidator;

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

    @Test
    public void testGetContextOptionName() {
        assertEquals("Copy", enumMapping.getContextOptionName(ContextOption.COPY));
        assertEquals("Paste", enumMapping.getContextOptionName(ContextOption.PASTE));
    }
}