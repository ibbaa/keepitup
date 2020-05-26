package de.ibba.keepitup.model;

import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class FileEntryTest {

    @Test
    public void testToBundleDefaultValues() {
        FileEntry fileEntry = new FileEntry();
        assertTrue(fileEntry.getName().isEmpty());
        assertFalse(fileEntry.isDirectory());
        assertFalse(fileEntry.isParent());
        assertTrue(fileEntry.canVisit());
        PersistableBundle persistableBundle = fileEntry.toPersistableBundle();
        assertNotNull(persistableBundle);
        fileEntry = new FileEntry(persistableBundle);
        assertTrue(fileEntry.getName().isEmpty());
        assertFalse(fileEntry.isDirectory());
        assertFalse(fileEntry.isParent());
        assertTrue(fileEntry.canVisit());
        Bundle bundle = fileEntry.toBundle();
        assertNotNull(bundle);
        fileEntry = new FileEntry(bundle);
        assertTrue(fileEntry.getName().isEmpty());
        assertFalse(fileEntry.isDirectory());
        assertFalse(fileEntry.isParent());
        assertTrue(fileEntry.canVisit());
    }

    @Test
    public void testToBundleValues() {
        FileEntry fileEntry = new FileEntry();
        fileEntry.setName("directory");
        fileEntry.setDirectory(true);
        fileEntry.setParent(true);
        fileEntry.setCanVisit(false);
        assertEquals("directory", fileEntry.getName());
        assertTrue(fileEntry.isDirectory());
        assertTrue(fileEntry.isParent());
        assertFalse(fileEntry.canVisit());
        PersistableBundle persistableBundle = fileEntry.toPersistableBundle();
        assertNotNull(persistableBundle);
        fileEntry = new FileEntry(persistableBundle);
        assertEquals("directory", fileEntry.getName());
        assertTrue(fileEntry.isDirectory());
        assertTrue(fileEntry.isParent());
        assertFalse(fileEntry.canVisit());
        Bundle bundle = fileEntry.toBundle();
        assertNotNull(bundle);
        fileEntry = new FileEntry(bundle);
        assertEquals("directory", fileEntry.getName());
        assertTrue(fileEntry.isDirectory());
        assertTrue(fileEntry.isParent());
        assertFalse(fileEntry.canVisit());
    }

    @Test
    public void testIsEqual() {
        FileEntry fileEntry1 = new FileEntry();
        FileEntry fileEntry2 = new FileEntry();
        assertTrue(fileEntry1.isEqual(fileEntry2));
        fileEntry1.setName("name");
        assertFalse(fileEntry1.isEqual(fileEntry2));
        fileEntry2.setName("name");
        assertTrue(fileEntry1.isEqual(fileEntry2));
        fileEntry1.setDirectory(true);
        assertFalse(fileEntry1.isEqual(fileEntry2));
        fileEntry2.setDirectory(true);
        assertTrue(fileEntry1.isEqual(fileEntry2));
        fileEntry1.setParent(true);
        assertFalse(fileEntry1.isEqual(fileEntry2));
        fileEntry2.setParent(true);
        assertTrue(fileEntry1.isEqual(fileEntry2));
        fileEntry1.setCanVisit(false);
        assertFalse(fileEntry1.isEqual(fileEntry2));
        fileEntry2.setCanVisit(false);
        assertTrue(fileEntry1.isEqual(fileEntry2));
    }
}
