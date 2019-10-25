package de.ibba.keepitup.resources;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.List;

import de.ibba.keepitup.model.FileEntry;
import de.ibba.keepitup.test.mock.TestRegistry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class FileManagerTest {

    private FileManager fileManager;

    @Before
    public void beforeEachTestMethod() {
        fileManager = new FileManager(TestRegistry.getContext());
        fileManager.deleteDirectory(fileManager.getInternalDownloadDirectory());
        fileManager.deleteDirectory(fileManager.getExternalDirectory("test/download"));
        fileManager.deleteDirectory(fileManager.getExternalDirectory("test"));
        fileManager.deleteDirectory(fileManager.getExternalDirectory(fileManager.getDefaultDownloadDirectoryName()));
        fileManager.deleteDirectory(fileManager.getExternalRootDirectory());

    }

    @After
    public void afterEachTestMethod() {
        fileManager.deleteDirectory(fileManager.getInternalDownloadDirectory());
        fileManager.deleteDirectory(fileManager.getExternalDirectory("test/download"));
        fileManager.deleteDirectory(fileManager.getExternalDirectory("test"));
        fileManager.deleteDirectory(fileManager.getExternalDirectory(fileManager.getDefaultDownloadDirectoryName()));
        fileManager.deleteDirectory(fileManager.getExternalRootDirectory());
    }

    @Test
    public void testGetInternalDownloadDirectory() {
        File internalRootDir = fileManager.getInternalRootDirectory();
        File downloadDir = new File(internalRootDir, fileManager.getDefaultDownloadDirectoryName());
        assertFalse(downloadDir.exists());
        File internalDownloadDir = fileManager.getInternalDownloadDirectory();
        assertTrue(internalDownloadDir.exists());
        assertTrue(downloadDir.exists());
        assertEquals(internalDownloadDir, downloadDir);
        internalDownloadDir = fileManager.getInternalDownloadDirectory();
        assertTrue(internalDownloadDir.exists());
        assertTrue(downloadDir.exists());
        assertEquals(internalDownloadDir, downloadDir);
    }

    @Test
    public void testGetExternalDirectoryDefaultDownloadDirectory() {
        File externalRootDir = fileManager.getExternalRootDirectory();
        File downloadDir = new File(externalRootDir, fileManager.getDefaultDownloadDirectoryName());
        assertFalse(downloadDir.exists());
        File externalDir = fileManager.getExternalDirectory(fileManager.getDefaultDownloadDirectoryName());
        assertTrue(externalDir.exists());
        assertTrue(downloadDir.exists());
        assertEquals(externalDir, downloadDir);
        externalDir = fileManager.getExternalDirectory(fileManager.getDefaultDownloadDirectoryName());
        assertTrue(externalDir.exists());
        assertTrue(downloadDir.exists());
        assertEquals(externalDir, downloadDir);
    }

    @Test
    public void testGetExternalDirectory() {
        File externalRootDir = fileManager.getExternalRootDirectory();
        File dir = new File(externalRootDir, "test/download");
        assertFalse(dir.exists());
        File externalDir = fileManager.getExternalDirectory("test/download");
        assertTrue(externalDir.exists());
        assertTrue(dir.exists());
        assertEquals(externalDir, dir);
        externalDir = fileManager.getExternalDirectory("test/download");
        assertTrue(externalDir.exists());
        assertTrue(dir.exists());
        assertEquals(externalDir, dir);
    }

    @Test
    public void testGetExternalDirectoryEmpty() {
        File externalRootDir = fileManager.getExternalRootDirectory();
        File dir = new File(externalRootDir, "");
        assertTrue(dir.exists());
        File externalDir = fileManager.getExternalDirectory("");
        assertTrue(externalDir.exists());
        assertEquals(externalDir, dir);
    }

    @Test
    public void testGetRelativeSibling() {
        assertEquals("", fileManager.getRelativeSibling(null, null));
        assertEquals("", fileManager.getRelativeSibling("", ""));
        assertEquals("download", fileManager.getRelativeSibling("download", ""));
        assertEquals("xyz", fileManager.getRelativeSibling("download", "xyz"));
        assertEquals("download/test/xyz", fileManager.getRelativeSibling("download/test/test", "xyz"));
    }

    @Test
    public void testGetRelativeParent() {
        assertEquals("", fileManager.getRelativeParent(null));
        assertEquals("", fileManager.getRelativeParent(""));
        assertEquals("", fileManager.getRelativeParent("folder"));
        assertEquals("/", fileManager.getRelativeParent("/folder"));
        assertEquals("/folder", fileManager.getRelativeParent("/folder/test"));
        assertEquals("folder", fileManager.getRelativeParent("folder/test/"));
    }

    @Test
    public void testGetAbsoluteParent() {
        File externalRootDir = fileManager.getExternalRootDirectory();
        String parent = fileManager.getAbsoluteParent(externalRootDir.getAbsolutePath(), externalRootDir.getAbsolutePath());
        assertEquals(new File(parent), externalRootDir);
        File externalDir = fileManager.getExternalDirectory(fileManager.getDefaultDownloadDirectoryName());
        parent = fileManager.getAbsoluteParent(externalRootDir.getAbsolutePath(), externalDir.getAbsolutePath());
        assertEquals(new File(parent), externalRootDir);
        externalDir = fileManager.getExternalDirectory("test/download");
        parent = fileManager.getAbsoluteParent(externalRootDir.getAbsolutePath(), externalDir.getAbsolutePath());
        assertEquals(new File(parent), new File(externalRootDir, "test"));
    }

    @Test
    public void testGetAbsoluteFolder() {
        assertEquals("root", fileManager.getAbsoluteFolder("root", null));
        assertEquals("root", fileManager.getAbsoluteFolder("root", ""));
        assertEquals("root/folder", fileManager.getAbsoluteFolder("root", "folder"));
        assertEquals("root/folder", fileManager.getAbsoluteFolder("root/", "folder"));
        assertEquals("root/folder/download", fileManager.getAbsoluteFolder("root/", "folder/download"));
    }

    @Test
    public void testGetNestedFolder() {
        assertEquals("", fileManager.getNestedFolder(null, null));
        assertEquals("", fileManager.getNestedFolder("", ""));
        assertEquals("folder", fileManager.getNestedFolder("folder", null));
        assertEquals("folder", fileManager.getNestedFolder("", "folder"));
        assertEquals("folder/folder", fileManager.getNestedFolder("folder", "folder"));
        assertEquals("test/xyz/folder", fileManager.getNestedFolder("test/xyz/", "folder"));
    }

    @Test
    public void testGetFilesRoot() throws Exception {
        File externalRootDir = fileManager.getExternalRootDirectory();
        File file1 = new File(externalRootDir, "file1");
        File dir1 = new File(externalRootDir, "dir1");
        File dir2 = new File(externalRootDir, "dir2");
        File file2 = new File(externalRootDir, "file2");
        assertTrue(file1.createNewFile());
        assertTrue(dir1.mkdir());
        assertTrue(dir2.mkdir());
        assertTrue(file2.createNewFile());
        List<FileEntry> entries = fileManager.getFiles(externalRootDir.getAbsolutePath(), externalRootDir.getAbsolutePath());
        assertEquals(4, entries.size());
        assertTrue(containsEntry(entries, getFileEntry("file1", false, false)));
        assertTrue(containsEntry(entries, getFileEntry("dir1", true, false)));
        assertTrue(containsEntry(entries, getFileEntry("dir2", true, false)));
        assertTrue(containsEntry(entries, getFileEntry("file2", false, false)));
    }

    @Test
    public void testGetFilesNonRoot() throws Exception {
        File externalRootDir = fileManager.getExternalRootDirectory();
        File externalDir = fileManager.getExternalDirectory("test/download");
        File file1 = new File(externalDir, "file1");
        File dir1 = new File(externalDir, "dir1");
        File dir2 = new File(externalDir, "dir2");
        File file2 = new File(externalDir, "file2");
        assertTrue(file1.createNewFile());
        assertTrue(dir1.mkdir());
        assertTrue(dir2.mkdir());
        assertTrue(file2.createNewFile());
        List<FileEntry> entries = fileManager.getFiles(externalRootDir.getAbsolutePath(), externalDir.getAbsolutePath());
        assertEquals(5, entries.size());
        FileEntry entry0 = entries.get(0);
        assertTrue(areEnrtriesEqual(entry0, getFileEntry("..", true, true)));
        assertTrue(containsEntry(entries, getFileEntry("file1", false, false)));
        assertTrue(containsEntry(entries, getFileEntry("dir1", true, false)));
        assertTrue(containsEntry(entries, getFileEntry("dir2", true, false)));
        assertTrue(containsEntry(entries, getFileEntry("file2", false, false)));
    }

    @Test
    public void testDeleteDirectory() throws Exception {
        File internalDownloadDir = fileManager.getInternalDownloadDirectory();
        assertEquals(0, internalDownloadDir.listFiles().length);
        File testFile = new File(internalDownloadDir, "testfile");
        assertTrue(testFile.createNewFile());
        File testDirectory = new File(internalDownloadDir, "testdirectory");
        assertTrue(testDirectory.mkdir());
        assertEquals(2, internalDownloadDir.listFiles().length);
        fileManager.deleteDirectory(fileManager.getInternalDownloadDirectory());
        assertFalse(internalDownloadDir.exists());
        File externalDir = fileManager.getExternalDirectory("test/download");
        fileManager.deleteDirectory(externalDir);
        assertFalse(externalDir.exists());
        assertTrue(new File(fileManager.getExternalRootDirectory(), "test").exists());
    }

    private FileEntry getFileEntry(String name, boolean directory, boolean parent) {
        FileEntry fileEntry = new FileEntry();
        fileEntry.setName(name);
        fileEntry.setDirectory(directory);
        fileEntry.setParent(parent);
        return fileEntry;
    }

    private boolean containsEntry(List<FileEntry> entries, FileEntry entry) {
        for (FileEntry currentEntry : entries) {
            if (areEnrtriesEqual(currentEntry, entry)) {
                return true;
            }
        }
        return false;
    }

    private boolean areEnrtriesEqual(FileEntry entry1, FileEntry entry2) {
        if (!entry1.getName().equals(entry2.getName())) {
            return false;
        }
        if (entry1.isDirectory() != entry2.isDirectory()) {
            return false;
        }
        return entry1.isParent() == entry2.isParent();
    }
}
