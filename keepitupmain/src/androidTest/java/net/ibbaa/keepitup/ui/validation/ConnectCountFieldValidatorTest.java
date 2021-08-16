package net.ibbaa.keepitup.ui.validation;

import org.junit.Test;

import net.ibbaa.keepitup.test.mock.TestRegistry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ConnectCountFieldValidatorTest {

    @Test
    public void testValidate() {
        ConnectCountFieldValidator validator = new ConnectCountFieldValidator("testconnectcount", TestRegistry.getContext());
        ValidationResult result = validator.validate("10");
        assertTrue(result.isValidationSuccessful());
        assertEquals("testconnectcount", result.getFieldName());
        assertEquals("Validation successful", result.getMessage());
        result = validator.validate("33x");
        assertFalse(result.isValidationSuccessful());
        assertEquals("testconnectcount", result.getFieldName());
        assertEquals("Invalid format", result.getMessage());
        result = validator.validate("0");
        assertFalse(result.isValidationSuccessful());
        assertEquals("testconnectcount", result.getFieldName());
        assertEquals("Minimum: 1", result.getMessage());
        result = validator.validate(String.valueOf(Long.MAX_VALUE));
        assertFalse(result.isValidationSuccessful());
        assertEquals("testconnectcount", result.getFieldName());
        assertEquals("Maximum: 10", result.getMessage());
        result = validator.validate("");
        assertFalse(result.isValidationSuccessful());
        assertEquals("testconnectcount", result.getFieldName());
        assertEquals("No value specified", result.getMessage());
        result = validator.validate(null);
        assertFalse(result.isValidationSuccessful());
        assertEquals("testconnectcount", result.getFieldName());
        assertEquals("No value specified", result.getMessage());
    }
}
