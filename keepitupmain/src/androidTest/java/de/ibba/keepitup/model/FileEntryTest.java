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
        PersistableBundle persistableBundle = fileEntry.toPersistableBundle();
        assertNotNull(persistableBundle);
        fileEntry = new FileEntry(persistableBundle);
        assertTrue(fileEntry.getName().isEmpty());
        assertFalse(fileEntry.isDirectory());
        assertFalse(fileEntry.isParent());
        Bundle bundle = fileEntry.toBundle();
        assertNotNull(bundle);
        fileEntry = new FileEntry(bundle);
        assertTrue(fileEntry.getName().isEmpty());
        assertFalse(fileEntry.isDirectory());
        assertFalse(fileEntry.isParent());
    }

    @Test
    public void testToBundleValues() {
        FileEntry fileEntry = new FileEntry();
        fileEntry.setName("directory");
        fileEntry.setDirectory(true);
        fileEntry.setParent(true);
        assertEquals("directory", fileEntry.getName());
        assertTrue(fileEntry.isDirectory());
        assertTrue(fileEntry.isParent());
        PersistableBundle persistableBundle = fileEntry.toPersistableBundle();
        assertNotNull(persistableBundle);
        fileEntry = new FileEntry(persistableBundle);
        assertEquals("directory", fileEntry.getName());
        assertTrue(fileEntry.isDirectory());
        assertTrue(fileEntry.isParent());
        Bundle bundle = fileEntry.toBundle();
        assertNotNull(bundle);
        fileEntry = new FileEntry(bundle);
        assertEquals("directory", fileEntry.getName());
        assertTrue(fileEntry.isDirectory());
        assertTrue(fileEntry.isParent());
    }
}
