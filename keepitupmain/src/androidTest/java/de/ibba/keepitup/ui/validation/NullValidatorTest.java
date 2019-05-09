package de.ibba.keepitup.ui.validation;

import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.ibba.keepitup.test.mock.TestRegistry;

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
        assertFalse(result.modifiedValue());
        assertEquals("www.host.com", result.getValue());
        result = validator.validatePort("23");
        assertFalse(result.isValidationSuccessful());
        assertEquals("Port", result.getFieldName());
        assertEquals("No value specified", result.getMessage());
        assertFalse(result.modifiedValue());
        assertEquals("23", result.getValue());
        result = validator.validateInterval("1");
        assertFalse(result.isValidationSuccessful());
        assertEquals("Interval", result.getFieldName());
        assertEquals("No value specified", result.getMessage());
        assertFalse(result.modifiedValue());
        assertEquals("1", result.getValue());
    }
}
