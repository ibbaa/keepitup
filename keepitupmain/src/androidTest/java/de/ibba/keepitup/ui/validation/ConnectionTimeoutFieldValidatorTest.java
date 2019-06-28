package de.ibba.keepitup.ui.validation;

import org.junit.Test;

import de.ibba.keepitup.test.mock.TestRegistry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ConnectionTimeoutFieldValidatorTest {

    @Test
    public void testValidate() {
        ConnectionTimeoutFieldValidator validator = new ConnectionTimeoutFieldValidator("testconnectiontimeout", TestRegistry.getContext());
        ValidationResult result = validator.validate("30");
        assertTrue(result.isValidationSuccessful());
        assertEquals("testconnectiontimeout", result.getFieldName());
        assertEquals("Validation successful", result.getMessage());
        result = validator.validate("33x");
        assertFalse(result.isValidationSuccessful());
        assertEquals("testconnectiontimeout", result.getFieldName());
        assertEquals("Invalid format", result.getMessage());
        result = validator.validate("0");
        assertFalse(result.isValidationSuccessful());
        assertEquals("testconnectiontimeout", result.getFieldName());
        assertEquals("Minimum: 1", result.getMessage());
        result = validator.validate(String.valueOf(Long.MAX_VALUE));
        assertFalse(result.isValidationSuccessful());
        assertEquals("testconnectiontimeout", result.getFieldName());
        assertEquals("Maximum: " + Integer.MAX_VALUE, result.getMessage());
        result = validator.validate("");
        assertFalse(result.isValidationSuccessful());
        assertEquals("testconnectiontimeout", result.getFieldName());
        assertEquals("No value specified", result.getMessage());
        result = validator.validate(null);
        assertFalse(result.isValidationSuccessful());
        assertEquals("testconnectiontimeout", result.getFieldName());
        assertEquals("No value specified", result.getMessage());
    }
}
