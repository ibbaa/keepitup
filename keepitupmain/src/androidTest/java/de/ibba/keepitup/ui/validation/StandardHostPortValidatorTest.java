package de.ibba.keepitup.ui.validation;

import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.ibba.keepitup.test.mock.TestRegistry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class StandardHostPortValidatorTest {

    private StandardHostPortValidator validator;

    @Before
    public void beforeEachTestMethod() {
        validator = new StandardHostPortValidator(TestRegistry.getContext());
    }

    @Test
    public void testValidateAddress() {
        ValidationResult result = validator.validateAddress("www.host.com");
        assertTrue(result.isValidationSuccessful());
        assertEquals("Host", result.getFieldName());
        assertEquals("Validation successful", result.getMessage());
        assertFalse(result.modifiedValue());
        assertEquals("www.host.com", result.getValue());
        result = validator.validateAddress("3ffe:1900:4545:3:200:f8ff:fe21:67cf");
        assertTrue(result.isValidationSuccessful());
        assertEquals("Host", result.getFieldName());
        assertEquals("Validation successful", result.getMessage());
        assertFalse(result.modifiedValue());
        assertEquals("3ffe:1900:4545:3:200:f8ff:fe21:67cf", result.getValue());
        result = validator.validateAddress("192.168.178.100");
        assertTrue(result.isValidationSuccessful());
        assertEquals("Host", result.getFieldName());
        assertEquals("Validation successful", result.getMessage());
        assertFalse(result.modifiedValue());
        assertEquals("192.168.178.100", result.getValue());
        result = validator.validateAddress("not valid");
        assertFalse(result.isValidationSuccessful());
        assertEquals("Host", result.getFieldName());
        assertEquals("No valid host or IP address", result.getMessage());
        assertFalse(result.modifiedValue());
        assertEquals("not valid", result.getValue());
    }

    @Test
    public void testValidatePort() {
        ValidationResult result = validator.validatePort("80");
        assertTrue(result.isValidationSuccessful());
        assertEquals("Port", result.getFieldName());
        assertEquals("Validation successful", result.getMessage());
        assertFalse(result.modifiedValue());
        assertEquals("80", result.getValue());
        result = validator.validatePort("abc");
        assertFalse(result.isValidationSuccessful());
        assertEquals("Port", result.getFieldName());
        assertEquals("Invalid format", result.getMessage());
        assertFalse(result.modifiedValue());
        assertEquals("abc", result.getValue());
        result = validator.validatePort("-1");
        assertFalse(result.isValidationSuccessful());
        assertEquals("Port", result.getFieldName());
        assertEquals("Minimum: 0", result.getMessage());
        assertFalse(result.modifiedValue());
        assertEquals("-1", result.getValue());
        result = validator.validatePort("12345678");
        assertFalse(result.isValidationSuccessful());
        assertEquals("Port", result.getFieldName());
        assertEquals("Maximum: 65535", result.getMessage());
        assertFalse(result.modifiedValue());
        assertEquals("12345678", result.getValue());
    }

    @Test
    public void testValidateInterval() {
        ValidationResult result = validator.validateInterval("15");
        assertTrue(result.isValidationSuccessful());
        assertEquals("Interval", result.getFieldName());
        assertEquals("Validation successful", result.getMessage());
        assertFalse(result.modifiedValue());
        assertEquals("15", result.getValue());
        result = validator.validateInterval("33x");
        assertFalse(result.isValidationSuccessful());
        assertEquals("Interval", result.getFieldName());
        assertEquals("Invalid format", result.getMessage());
        assertFalse(result.modifiedValue());
        assertEquals("33x", result.getValue());
        result = validator.validateInterval("0");
        assertFalse(result.isValidationSuccessful());
        assertEquals("Interval", result.getFieldName());
        assertEquals("Minimum: 1", result.getMessage());
        assertFalse(result.modifiedValue());
        assertEquals("0", result.getValue());
        result = validator.validateInterval(String.valueOf((long) Integer.MAX_VALUE + 1));
        assertFalse(result.isValidationSuccessful());
        assertEquals("Interval", result.getFieldName());
        assertEquals("Maximum: " + Integer.MAX_VALUE, result.getMessage());
        assertFalse(result.modifiedValue());
        assertEquals(String.valueOf((long) Integer.MAX_VALUE + 1), result.getValue());
    }
}
