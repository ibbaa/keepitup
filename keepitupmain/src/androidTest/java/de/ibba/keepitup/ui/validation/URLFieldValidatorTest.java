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
