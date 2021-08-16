package net.ibbaa.keepitup.ui.validation;

import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

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

    @Test
    public void testIsEqual() {
        ValidationResult result = new ValidationResult(true, "testfield", "testmessage");
        assertTrue(result.isEqual(result));
        assertFalse(result.isEqual(null));
        ValidationResult otherResult = new ValidationResult(true, "testfield", "testmessage") {
        };
        assertFalse(result.isEqual(otherResult));
        otherResult = new ValidationResult(true, "testfield", "testmessage");
        assertTrue(result.isEqual(otherResult));
        otherResult = new ValidationResult(false, "testfield", "testmessage");
        assertFalse(result.isEqual(otherResult));
        otherResult = new ValidationResult(true, null, "testmessage");
        assertFalse(result.isEqual(otherResult));
        otherResult = new ValidationResult(true, "1", "testmessage");
        assertFalse(result.isEqual(otherResult));
        otherResult = new ValidationResult(true, "1", "testmessage1");
        assertFalse(result.isEqual(otherResult));
        otherResult = new ValidationResult(true, "1", null);
        assertFalse(result.isEqual(otherResult));
        otherResult = new ValidationResult(true, "", "");
        assertFalse(result.isEqual(otherResult));
    }
}
