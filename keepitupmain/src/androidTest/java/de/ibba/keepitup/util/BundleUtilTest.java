package de.ibba.keepitup.util;

import android.os.Bundle;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import de.ibba.keepitup.ui.validation.ValidationResult;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class BundleUtilTest {

    @Test
    public void testStringToBundle() {
        Bundle bundle = BundleUtil.stringToBundle("key", "message");
        assertEquals("message", bundle.getString("key"));
        assertTrue(BundleUtil.stringToBundle(null, "message").isEmpty());
        assertTrue(BundleUtil.stringToBundle("", null).isEmpty());
    }

    @Test
    public void testStringsToBundle() {
        Bundle bundle = BundleUtil.stringsToBundle(new String[]{"key1", "key2"}, new String[]{"message1", "message2"});
        assertEquals("message1", bundle.getString("key1"));
        assertEquals("message2", bundle.getString("key2"));
        bundle = BundleUtil.stringsToBundle(new String[]{"key1", "key2"}, new String[]{"message1"});
        assertEquals("message1", bundle.getString("key1"));
        assertTrue(BundleUtil.stringsToBundle(null, new String[0]).isEmpty());
        assertTrue(BundleUtil.stringsToBundle(new String[0], null).isEmpty());
        assertTrue(BundleUtil.stringsToBundle(new String[0], new String[0]).isEmpty());
    }

    @Test
    public void testStringFromBundle() {
        Bundle bundle = new Bundle();
        bundle.putString("key", "message");
        assertEquals("message", BundleUtil.stringFromBundle("key", bundle));
        assertNull(BundleUtil.stringFromBundle("xyz", bundle));
        assertNull(BundleUtil.stringFromBundle(null, bundle));
        assertNull(BundleUtil.stringFromBundle("key", null));
    }

    @Test
    public void testStringListToBundle() {
        Bundle bundle = BundleUtil.stringListToBundle("key", Arrays.asList("string1", "string2"));
        assertEquals("string1", bundle.getString("key0"));
        assertEquals("string2", bundle.getString("key1"));
    }

    @Test
    public void testStringListFromBundle() {
        Bundle bundle = new Bundle();
        bundle.putString("key0", "string1");
        bundle.putString("key1", "string2");
        List<String> list = BundleUtil.stringListFromBundle("key", bundle);
        assertEquals("string1", list.get(0));
        assertEquals("string2", list.get(1));
    }

    @Test
    public void testStringListToAndFromBundle() {
        Bundle bundle = BundleUtil.stringListToBundle("key", Arrays.asList("string1", "string2"));
        List<String> list = BundleUtil.stringListFromBundle("key", bundle);
        assertEquals("string1", list.get(0));
        assertEquals("string2", list.get(1));
    }

    @Test
    public void testBundleToBundle() {
        Bundle nestedBundle = new Bundle();
        nestedBundle.putString("key", "message");
        Bundle bundle = BundleUtil.bundleToBundle("key", nestedBundle);
        assertEquals("message", bundle.getBundle("key").getString("key"));
        assertTrue(BundleUtil.bundleToBundle(null, nestedBundle).isEmpty());
        assertTrue(BundleUtil.bundleToBundle("", null).isEmpty());
    }

    @Test
    public void testBundleFromBundle() {
        Bundle bundle = new Bundle();
        Bundle nestedBundle = new Bundle();
        nestedBundle.putString("key", "message");
        bundle.putBundle("key", nestedBundle);
        assertEquals("message", BundleUtil.bundleFromBundle("key", bundle).getString("key"));
        assertNull(BundleUtil.bundleFromBundle(null, bundle));
        assertNull(BundleUtil.bundleFromBundle("key", null));
    }

    @Test
    public void testBundleListToBundle() {
        Bundle nestedBundle1 = new Bundle();
        nestedBundle1.putString("key1", "message1");
        Bundle nestedBundle2 = new Bundle();
        nestedBundle2.putString("key2", "message2");
        Bundle bundle = BundleUtil.bundleListToBundle("key", Arrays.asList(nestedBundle1, nestedBundle2));
        assertEquals("message1", bundle.getBundle("key0").getString("key1"));
        assertEquals("message2", bundle.getBundle("key1").getString("key2"));
        assertTrue(BundleUtil.bundleListToBundle(null, Arrays.asList(nestedBundle1, nestedBundle2)).isEmpty());
        assertTrue(BundleUtil.bundleListToBundle("key", null).isEmpty());
    }

    @Test
    public void testBundleListFromBundle() {
        Bundle bundle = new Bundle();
        Bundle nestedBundle1 = new Bundle();
        nestedBundle1.putString("key1", "message1");
        Bundle nestedBundle2 = new Bundle();
        nestedBundle2.putString("key2", "message2");
        bundle.putBundle("key0", nestedBundle1);
        bundle.putBundle("key1", nestedBundle2);
        assertEquals("message1", BundleUtil.bundleListFromBundle("key", bundle).get(0).getString("key1"));
        assertEquals("message2", BundleUtil.bundleListFromBundle("key", bundle).get(1).getString("key2"));
    }

    @Test
    public void testBundleListToAndFromBundle() {
        Bundle nestedBundle1 = new Bundle();
        nestedBundle1.putString("key1", "message1");
        Bundle nestedBundle2 = new Bundle();
        nestedBundle2.putString("key2", "message2");
        List<Bundle> list = Arrays.asList(nestedBundle1, nestedBundle2);
        Bundle bundle = BundleUtil.bundleListToBundle("key", list);
        list = BundleUtil.bundleListFromBundle("key", bundle);
        assertEquals(2, list.size());
        assertEquals("message1", list.get(0).getString("key1"));
        assertEquals("message2", list.get(1).getString("key2"));
    }

    @Test
    public void testValidationResultListToBundle() {
        ValidationResult result1 = new ValidationResult(true, "field1", "message1");
        ValidationResult result2 = new ValidationResult(false, "field2", "message2");
        Bundle bundle = BundleUtil.validationResultListToBundle("key", Arrays.asList(result1, result2));
        ValidationResult otherResult1 = new ValidationResult(bundle.getBundle("key0"));
        ValidationResult otherResult2 = new ValidationResult(bundle.getBundle("key1"));
        assertTrue(result1.isEqual(otherResult1));
        assertTrue(result2.isEqual(otherResult2));
    }

    @Test
    public void testValidationResultListFromBundle() {
        Bundle bundle = new Bundle();
        ValidationResult result1 = new ValidationResult(true, "field1", "message1");
        ValidationResult result2 = new ValidationResult(false, "field2", "message2");
        bundle.putBundle("key0", result1.toBundle());
        bundle.putBundle("key1", result2.toBundle());
        List<ValidationResult> validationResultList = BundleUtil.validationResultListFromBundle("key", bundle);
        assertTrue(result1.isEqual(validationResultList.get(0)));
        assertTrue(result2.isEqual(validationResultList.get(1)));
    }

    @Test
    public void testValidationResultListToAndFromBundle() {
        ValidationResult result1 = new ValidationResult(true, "field1", "message1");
        ValidationResult result2 = new ValidationResult(false, "field2", "message2");
        Bundle bundle = BundleUtil.validationResultListToBundle("key", Arrays.asList(result1, result2));
        List<ValidationResult> validationResultList = BundleUtil.validationResultListFromBundle("key", bundle);
        assertTrue(result1.isEqual(validationResultList.get(0)));
        assertTrue(result2.isEqual(validationResultList.get(1)));
    }
}
