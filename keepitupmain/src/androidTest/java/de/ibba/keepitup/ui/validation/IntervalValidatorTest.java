package de.ibba.keepitup.ui.validation;

import org.junit.Test;

import de.ibba.keepitup.test.mock.TestRegistry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class IntervalValidatorTest {

    @Test
    public void testValidate() {
        IntervalFieldValidator validator = new IntervalFieldValidator("testinterval", TestRegistry.getContext());
        ValidationResult result = validator.validate("15");
        assertTrue(result.isValidationSuccessful());
        assertEquals("testinterval", result.getFieldName());
        assertEquals("Validation successful", result.getMessage());
        result = validator.validate("33x");
        assertFalse(result.isValidationSuccessful());
        assertEquals("testinterval", result.getFieldName());
        assertEquals("Invalid format", result.getMessage());
        result = validator.validate("-1");
        assertFalse(result.isValidationSuccessful());
        assertEquals("testinterval", result.getFieldName());
        assertEquals("Minimum: 1", result.getMessage());
        result = validator.validate(String.valueOf(Long.MAX_VALUE));
        assertFalse(result.isValidationSuccessful());
        assertEquals("testinterval", result.getFieldName());
        assertEquals("Maximum: " + Integer.MAX_VALUE, result.getMessage());
        result = validator.validate("");
        assertFalse(result.isValidationSuccessful());
        assertEquals("testinterval", result.getFieldName());
        assertEquals("No value specified", result.getMessage());
        result = validator.validate(null);
        assertFalse(result.isValidationSuccessful());
        assertEquals("testinterval", result.getFieldName());
        assertEquals("No value specified", result.getMessage());
    }
}
