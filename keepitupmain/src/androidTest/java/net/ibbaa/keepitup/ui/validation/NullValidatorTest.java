package net.ibbaa.keepitup.ui.validation;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.ibbaa.keepitup.test.mock.TestRegistry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class NullValidatorTest {

    private NullValidator validator;

    @Before
    public void beforeEachTestMethod() {
        validator = new NullValidator(TestRegistry.getContext());
    }

    @Test
    public void testValidate() {
        ValidationResult result = validator.validateAddress("www.host.com");
        assertFalse(result.isValidationSuccessful());
        assertEquals("Address", result.getFieldName());
        assertEquals("No value specified", result.getMessage());
        result = validator.validatePort("23");
        assertFalse(result.isValidationSuccessful());
        assertEquals("Port", result.getFieldName());
        assertEquals("No value specified", result.getMessage());
        result = validator.validateInterval("1");
        assertFalse(result.isValidationSuccessful());
        assertEquals("Interval", result.getFieldName());
        assertEquals("No value specified", result.getMessage());
    }
}
