package de.ibba.keepitup.ui.validation;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import org.junit.Test;
import org.junit.runner.RunWith;

import de.ibba.keepitup.test.mock.TestRegistry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class FilenameFieldValidatorTest {

    @Test
    public void testValidateEmptyNotAllowed() {
        FilenameFieldValidator validator = new FilenameFieldValidator("testfilename", false, TestRegistry.getContext());
        ValidationResult result = validator.validate("file");
        assertTrue(result.isValidationSuccessful());
        assertEquals("testfilename", result.getFieldName());
        assertEquals("Validation successful", result.getMessage());
        result = validator.validate("");
        assertFalse(result.isValidationSuccessful());
        assertEquals("testfilename", result.getFieldName());
        assertEquals("No value specified", result.getMessage());
        result = validator.validate(null);
        assertFalse(result.isValidationSuccessful());
        assertEquals("testfilename", result.getFieldName());
        assertEquals("No value specified", result.getMessage());
        result = validator.validate("fil/e");
        assertFalse(result.isValidationSuccessful());
        assertEquals("testfilename", result.getFieldName());
        assertEquals("No valid filename", result.getMessage());
    }

    @Test
    public void testValidateEmptyAllowed() {
        FilenameFieldValidator validator = new FilenameFieldValidator("testfilename", true, TestRegistry.getContext());
        ValidationResult result = validator.validate("file");
        assertTrue(result.isValidationSuccessful());
        assertEquals("testfilename", result.getFieldName());
        assertEquals("Validation successful", result.getMessage());
        result = validator.validate("");
        assertTrue(result.isValidationSuccessful());
        assertEquals("testfilename", result.getFieldName());
        assertEquals("Validation successful", result.getMessage());
        result = validator.validate(null);
        assertTrue(result.isValidationSuccessful());
        assertEquals("testfilename", result.getFieldName());
        assertEquals("Validation successful", result.getMessage());
        result = validator.validate("fil/e");
        assertFalse(result.isValidationSuccessful());
        assertEquals("testfilename", result.getFieldName());
        assertEquals("No valid filename", result.getMessage());
    }
}
