package de.ibba.keepitup.util;

import android.os.Bundle;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.ibba.keepitup.model.FileEntry;
import de.ibba.keepitup.ui.dialog.ContextOption;
import de.ibba.keepitup.ui.validation.ValidationResult;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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
        Bundle bundle = BundleUtil.stringListToBundle("key", null);
        assertNotNull(bundle);
        assertTrue(bundle.isEmpty());
        bundle = BundleUtil.stringListToBundle("key", Collections.emptyList());
        assertNotNull(bundle);
        assertTrue(bundle.isEmpty());
        bundle = BundleUtil.stringListToBundle(null, Arrays.asList("string1", "string2"));
        assertNotNull(bundle);
        assertTrue(bundle.isEmpty());
        bundle = BundleUtil.stringListToBundle("key", Arrays.asList("string1", "string2"));
        assertEquals("string1", bundle.getString("key0"));
        assertEquals("string2", bundle.getString("key1"));
    }

    @Test
    public void testStringListFromBundle() {
        List<String> list = BundleUtil.stringListFromBundle("key", null);
        assertNotNull(list);
        assertTrue(list.isEmpty());
        list = BundleUtil.stringListFromBundle("key", new Bundle());
        assertNotNull(list);
        assertTrue(list.isEmpty());
        Bundle bundle = new Bundle();
        bundle.putString("key0", "string1");
        bundle.putString("key1", "string2");
        list = BundleUtil.stringListFromBundle(null, bundle);
        assertNotNull(list);
        assertTrue(list.isEmpty());
        list = BundleUtil.stringListFromBundle("key", bundle);
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
        Bundle bundle = BundleUtil.bundleToBundle("key", null);
        assertNotNull(bundle);
        assertTrue(bundle.isEmpty());
        Bundle nestedBundle = new Bundle();
        nestedBundle.putString("key", "message");
        bundle = BundleUtil.bundleToBundle(null, nestedBundle);
        assertNotNull(bundle);
        assertTrue(bundle.isEmpty());
        bundle = BundleUtil.bundleToBundle("key", nestedBundle);
        assertEquals("message", bundle.getBundle("key").getString("key"));
        assertTrue(BundleUtil.bundleToBundle(null, nestedBundle).isEmpty());
        assertTrue(BundleUtil.bundleToBundle("", null).isEmpty());
    }

    @Test
    public void testBundleFromBundle() {
        assertNull(BundleUtil.bundleFromBundle(null, null));
        Bundle bundle = new Bundle();
        Bundle nestedBundle = new Bundle();
        nestedBundle.putString("key", "message");
        bundle.putBundle("key", nestedBundle);
        assertEquals("message", BundleUtil.bundleFromBundle("key", bundle).getString("key"));
        assertNull(BundleUtil.bundleFromBundle(null, bundle));
        assertNull(BundleUtil.bundleFromBundle("key", null));
    }

    @Test
    public void testEmptyBundleListToBundle() {
        Bundle nestedBundle1 = new Bundle();
        nestedBundle1.putString("key1", "message1");
        Bundle nestedBundle2 = new Bundle();
        nestedBundle2.putString("key2", "message2");
        Bundle bundle = BundleUtil.bundleListToBundle(null, Arrays.asList(nestedBundle1, nestedBundle2));
        assertNotNull(bundle);
        assertTrue(bundle.isEmpty());
        bundle = BundleUtil.bundleListToBundle("key", null);
        assertNotNull(bundle);
        assertTrue(bundle.isEmpty());
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
    public void testBundleListFromEmptyBundle() {
        Bundle bundle = new Bundle();
        Bundle nestedBundle1 = new Bundle();
        nestedBundle1.putString("key1", "message1");
        Bundle nestedBundle2 = new Bundle();
        nestedBundle2.putString("key2", "message2");
        bundle.putBundle("key0", nestedBundle1);
        bundle.putBundle("key1", nestedBundle2);
        List<Bundle> list = BundleUtil.bundleListFromBundle(null, bundle);
        assertNotNull(list);
        assertTrue(list.isEmpty());
        list = BundleUtil.bundleListFromBundle("key", null);
        assertNotNull(list);
        assertTrue(list.isEmpty());
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
    public void testEmptyValidationResultListToBundle() {
        Bundle bundle = BundleUtil.validationResultListToBundle("key", null);
        assertNotNull(bundle);
        assertTrue(bundle.isEmpty());
        bundle = BundleUtil.validationResultListToBundle("key", Collections.emptyList());
        assertNotNull(bundle);
        assertTrue(bundle.isEmpty());
    }

    @Test
    public void testValidationResultListFromEmptyBundle() {
        List<ValidationResult> validationResultList = BundleUtil.validationResultListFromBundle("key", null);
        assertNotNull(validationResultList);
        assertTrue(validationResultList.isEmpty());
        Bundle bundle = new Bundle();
        validationResultList = BundleUtil.validationResultListFromBundle("key", bundle);
        assertNotNull(validationResultList);
        assertTrue(validationResultList.isEmpty());
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

    @Test
    public void testEmptyFileEntryListToBundle() {
        Bundle bundle = BundleUtil.fileEntryListToBundle("key", null);
        assertNotNull(bundle);
        assertTrue(bundle.isEmpty());
        bundle = BundleUtil.fileEntryListToBundle("key", Collections.emptyList());
        assertNotNull(bundle);
        assertTrue(bundle.isEmpty());
    }

    @Test
    public void testFileEntryListFromEmptyBundle() {
        List<FileEntry> entryList = BundleUtil.fileEntryListFromBundle("key", null);
        assertNotNull(entryList);
        assertTrue(entryList.isEmpty());
        Bundle bundle = new Bundle();
        entryList = BundleUtil.fileEntryListFromBundle("key", bundle);
        assertNotNull(entryList);
        assertTrue(entryList.isEmpty());
    }

    @Test
    public void testFileEntryListToBundle() {
        FileEntry entry1 = getFileEntry("test1", true, false, false);
        FileEntry entry2 = getFileEntry("test2", false, true, true);
        Bundle bundle = BundleUtil.fileEntryListToBundle("key", Arrays.asList(entry1, entry2));
        FileEntry otherEntry1 = new FileEntry(bundle.getBundle("key0"));
        FileEntry otherEntry2 = new FileEntry(bundle.getBundle("key1"));
        assertTrue(entry1.isEqual(otherEntry1));
        assertTrue(entry2.isEqual(otherEntry2));
    }

    @Test
    public void testFileEntryListFromBundle() {
        Bundle bundle = new Bundle();
        FileEntry entry1 = getFileEntry("test1", true, false, false);
        FileEntry entry2 = getFileEntry("test2", false, true, true);
        bundle.putBundle("key0", entry1.toBundle());
        bundle.putBundle("key1", entry2.toBundle());
        List<FileEntry> entryList = BundleUtil.fileEntryListFromBundle("key", bundle);
        assertTrue(entry1.isEqual(entryList.get(0)));
        assertTrue(entry2.isEqual(entryList.get(1)));
    }

    @Test
    public void testFileEntryListToAndFromBundle() {
        FileEntry entry1 = getFileEntry("test1", true, false, false);
        FileEntry entry2 = getFileEntry("test2", false, true, true);
        Bundle bundle = BundleUtil.fileEntryListToBundle("key", Arrays.asList(entry1, entry2));
        List<FileEntry> entryList = BundleUtil.fileEntryListFromBundle("key", bundle);
        assertTrue(entry1.isEqual(entryList.get(0)));
        assertTrue(entry2.isEqual(entryList.get(1)));
    }

    @Test
    public void testEmptyContextOptionListToBundle() {
        Bundle bundle = BundleUtil.contextOptionListToBundle("key", null);
        assertNotNull(bundle);
        assertTrue(bundle.isEmpty());
        bundle = BundleUtil.contextOptionListToBundle("key", Collections.emptyList());
        assertNotNull(bundle);
        assertTrue(bundle.isEmpty());
    }

    @Test
    public void testContextOptionListFromEmptyBundle() {
        List<ContextOption> contextOptionList = BundleUtil.contextOptionListFromBundle("key", null);
        assertNotNull(contextOptionList);
        assertTrue(contextOptionList.isEmpty());
        Bundle bundle = new Bundle();
        contextOptionList = BundleUtil.contextOptionListFromBundle("key", bundle);
        assertNotNull(contextOptionList);
        assertTrue(contextOptionList.isEmpty());
    }

    @Test
    public void testContextOptionListToBundle() {
        Bundle bundle = BundleUtil.contextOptionListToBundle("key", Arrays.asList(ContextOption.COPY, ContextOption.PASTE));
        ContextOption otherContextOption1 = ContextOption.fromBundle(bundle.getBundle("key0"));
        ContextOption otherContextOption2 = ContextOption.fromBundle(bundle.getBundle("key1"));
        assertEquals(ContextOption.COPY, otherContextOption1);
        assertEquals(ContextOption.PASTE, otherContextOption2);
    }

    @Test
    public void testContextOptionListFromBundle() {
        Bundle bundle = new Bundle();
        bundle.putBundle("key0", ContextOption.COPY.toBundle());
        bundle.putBundle("key1", ContextOption.PASTE.toBundle());
        List<ContextOption> contextOptionList = BundleUtil.contextOptionListFromBundle("key", bundle);
        assertEquals(ContextOption.COPY, contextOptionList.get(0));
        assertEquals(ContextOption.PASTE, contextOptionList.get(1));
    }

    @Test
    public void testContextOptionListToAndFromBundle() {
        Bundle bundle = BundleUtil.contextOptionListToBundle("key", Arrays.asList(ContextOption.COPY, ContextOption.PASTE));
        List<ContextOption> contextOptionList = BundleUtil.contextOptionListFromBundle("key", bundle);
        assertEquals(ContextOption.COPY, contextOptionList.get(0));
        assertEquals(ContextOption.PASTE, contextOptionList.get(1));
    }

    private FileEntry getFileEntry(String name, boolean directory, boolean parent, boolean canVisit) {
        FileEntry fileEntry = new FileEntry();
        fileEntry.setName(name);
        fileEntry.setDirectory(directory);
        fileEntry.setParent(parent);
        fileEntry.setCanVisit(canVisit);
        return fileEntry;
    }
}
