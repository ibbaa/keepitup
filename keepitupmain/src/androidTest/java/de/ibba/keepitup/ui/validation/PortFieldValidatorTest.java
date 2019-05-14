package de.ibba.keepitup.ui.validation;

import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import de.ibba.keepitup.test.mock.TestRegistry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class PortFieldValidatorTest {

    @Test
    public void testValidate() {
        PortFieldValidator validator = new PortFieldValidator("testport", TestRegistry.getContext());
        ValidationResult result = validator.validate("80");
        assertTrue(result.isValidationSuccessful());
        assertEquals("testport", result.getFieldName());
        assertEquals("Validation successful", result.getMessage());
        result = validator.validate("x5");
        assertFalse(result.isValidationSuccessful());
        assertEquals("testport", result.getFieldName());
        assertEquals("Invalid format", result.getMessage());
        result = validator.validate("-1000");
        assertFalse(result.isValidationSuccessful());
        assertEquals("testport", result.getFieldName());
        assertEquals("Minimum: 0", result.getMessage());
        result = validator.validate("65536");
        assertFalse(result.isValidationSuccessful());
        assertEquals("testport", result.getFieldName());
        assertEquals("Maximum: 65535", result.getMessage());
    }
}
