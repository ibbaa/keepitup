package de.ibba.keepitup.ui.validation;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class ValidationResultTest {

    @Test
    public void testToBundleNullValues() {
        ValidationResult result = new ValidationResult(false, null, null);
        assertFalse(result.isValidationSuccessful());
        assertNull(result.getFieldName());
        assertNull(result.getMessage());
        PersistableBundle persistableBundle = result.toPersistableBundle();
        assertNotNull(persistableBundle);
        result = new ValidationResult(persistableBundle);
        assertFalse(result.isValidationSuccessful());
        assertNull(result.getFieldName());
        assertNull(result.getMessage());
        Bundle bundle = result.toBundle();
        assertNotNull(bundle);
        result = new ValidationResult(bundle);
        assertFalse(result.isValidationSuccessful());
        assertNull(result.getFieldName());
        assertNull(result.getMessage());
    }

    @Test
    public void testToBundle() {
        ValidationResult result = new ValidationResult(true, "testfield", "testmessage");
        assertTrue(result.isValidationSuccessful());
        assertEquals("testfield", result.getFieldName());
        assertEquals("testmessage", result.getMessage());
        PersistableBundle persistableBundle = result.toPersistableBundle();
        assertNotNull(persistableBundle);
        result = new ValidationResult(persistableBundle);
        assertTrue(result.isValidationSuccessful());
        assertEquals("testfield", result.getFieldName());
        assertEquals("testmessage", result.getMessage());
        Bundle bundle = result.toBundle();
        assertNotNull(bundle);
        result = new ValidationResult(bundle);
        assertTrue(result.isValidationSuccessful());
        assertEquals("testfield", result.getFieldName());
        assertEquals("testmessage", result.getMessage());
    }
}
