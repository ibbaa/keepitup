package de.ibba.keepitup.util;

import android.os.Bundle;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import de.ibba.keepitup.ui.validation.ValidationResult;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class BundleUtilTest {

    @Test
    public void addValidationResultToIndexedBundle() {
        Bundle bundle = new Bundle();
        ValidationResult result1 = new ValidationResult(false, null, "");
        ValidationResult result2 = new ValidationResult(true, "result2", "message2");
        ValidationResult result3 = new ValidationResult(false, "result3", "xyz");
        BundleUtil.addValidationResultToIndexedBundle(bundle, result1);
        BundleUtil.addValidationResultToIndexedBundle(bundle, result2);
        BundleUtil.addValidationResultToIndexedBundle(bundle, result3);
        List<ValidationResult> result = BundleUtil.indexedBundleToValidationResultList(bundle);
        assertEquals(3, result.size());
        result1 = result.get(0);
        result2 = result.get(1);
        result3 = result.get(2);
        assertFalse(result1.isValidationSuccessful());
        assertNull(result1.getFieldName());
        assertEquals("", result1.getMessage());
        assertTrue(result2.isValidationSuccessful());
        assertEquals("result2", result2.getFieldName());
        assertEquals("message2", result2.getMessage());
        assertFalse(result3.isValidationSuccessful());
        assertEquals("result3", result3.getFieldName());
        assertEquals("xyz", result3.getMessage());
    }

    @Test
    public void testIndexedBundleToValidationResultList() {
        assertEquals(0, BundleUtil.indexedBundleToValidationResultList(null).size());
        Bundle bundle = new Bundle();
        bundle.putBundle("1", new ValidationResult(false, null, null).toBundle());
        bundle.putBundle("2", new ValidationResult(false, null, null).toBundle());
        bundle.putBundle("4", new ValidationResult(false, null, null).toBundle());
        bundle.putString("x", "x");
        List<ValidationResult> result = BundleUtil.indexedBundleToValidationResultList(bundle);
        assertEquals(2, result.size());
    }
}
