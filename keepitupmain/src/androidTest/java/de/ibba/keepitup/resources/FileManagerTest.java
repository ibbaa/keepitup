package de.ibba.keepitup.resources;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

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
        fileManager.deleteDirectory(fileManager.getExternalDownloadDirectory(fileManager.getDefaultDownloadDirectoryName()));
        fileManager.deleteDirectory(fileManager.getExternalDownloadDirectory("test"));
    }

    @After
    public void afterEachTestMethod() {
        fileManager.deleteDirectory(fileManager.getInternalDownloadDirectory());
        fileManager.deleteDirectory(fileManager.getExternalDownloadDirectory(fileManager.getDefaultDownloadDirectoryName()));
        fileManager.deleteDirectory(fileManager.getExternalDownloadDirectory(fileManager.getDefaultDownloadDirectoryName()));
        fileManager.deleteDirectory(fileManager.getExternalDownloadDirectory("test"));
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
    public void testGetExternalDownloadDirectoryDefault() {
        File externalRootDir = fileManager.getExternalRootDirectory();
        File downloadDir = new File(externalRootDir, fileManager.getDefaultDownloadDirectoryName());
        assertFalse(downloadDir.exists());
        File externalDownloadDir = fileManager.getExternalDownloadDirectory(fileManager.getDefaultDownloadDirectoryName());
        assertTrue(externalDownloadDir.exists());
        assertTrue(downloadDir.exists());
        assertEquals(externalDownloadDir, downloadDir);
        externalDownloadDir = fileManager.getExternalDownloadDirectory(fileManager.getDefaultDownloadDirectoryName());
        assertTrue(externalDownloadDir.exists());
        assertTrue(downloadDir.exists());
        assertEquals(externalDownloadDir, downloadDir);
    }

    @Test
    public void testGetExternalDownloadDirectory() {
        File externalRootDir = fileManager.getExternalRootDirectory();
        File downloadDir = new File(externalRootDir, "test");
        assertFalse(downloadDir.exists());
        File externalDownloadDir = fileManager.getExternalDownloadDirectory("test");
        assertTrue(externalDownloadDir.exists());
        assertTrue(downloadDir.exists());
        assertEquals(externalDownloadDir, downloadDir);
        externalDownloadDir = fileManager.getExternalDownloadDirectory("test");
        assertTrue(externalDownloadDir.exists());
        assertTrue(downloadDir.exists());
        assertEquals(externalDownloadDir, downloadDir);
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
    }
}
