package net.ibbaa.keepitup.ui.validation;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import org.junit.Test;
import org.junit.runner.RunWith;

import net.ibbaa.keepitup.test.mock.TestRegistry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class URLFieldValidatorTest {

    @Test
    public void testValidate() {
        URLFieldValidator validator = new URLFieldValidator("testurl", TestRegistry.getContext());
        ValidationResult result = validator.validate("http://www.host.com");
        assertTrue(result.isValidationSuccessful());
        assertEquals("testurl", result.getFieldName());
        assertEquals("Validation successful", result.getMessage());
        result = validator.validate("https://te st/");
        assertFalse(result.isValidationSuccessful());
        assertEquals("testurl", result.getFieldName());
        assertEquals("No valid URL", result.getMessage());
        result = validator.validate("");
        assertFalse(result.isValidationSuccessful());
        assertEquals("testurl", result.getFieldName());
        assertEquals("No value specified", result.getMessage());
        result = validator.validate(null);
        assertFalse(result.isValidationSuccessful());
        assertEquals("testurl", result.getFieldName());
        assertEquals("No value specified", result.getMessage());
    }
}