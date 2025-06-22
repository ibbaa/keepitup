/*
 * Copyright (c) 2025 Alwin Ibba
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.ibbaa.keepitup.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import net.ibbaa.keepitup.model.FileEntry;
import net.ibbaa.keepitup.test.mock.MockTimeService;
import net.ibbaa.keepitup.test.mock.TestRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class SystemFileManagerTest {

    private SystemFileManager fileManager;

    @Before
    public void beforeEachTestMethod() {
        fileManager = new SystemFileManager(TestRegistry.getContext());
        MockTimeService timeService = (MockTimeService) fileManager.getTimeService();
        timeService.setTimestamp(getTestTimestamp());
        timeService.setTimestamp2(getTestTimestamp());
        fileManager.delete(fileManager.getInternalRootDirectory());
        fileManager.delete(fileManager.getExternalRootDirectory(0));
        fileManager.delete(fileManager.getExternalRootDirectory(1));
    }

    @After
    public void afterEachTestMethod() {
        fileManager.delete(fileManager.getInternalRootDirectory());
        fileManager.delete(fileManager.getExternalRootDirectory(0));
        fileManager.delete(fileManager.getExternalRootDirectory(1));
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
    public void testGetInternalDownloadDirectoryError() throws IOException {
        File internalRootDir = fileManager.getInternalRootDirectory();
        File downloadDir = new File(internalRootDir, fileManager.getDefaultDownloadDirectoryName());
        assertTrue(downloadDir.createNewFile());
        File internalDownloadDir = fileManager.getInternalDownloadDirectory();
        assertNull(internalDownloadDir);
    }

    @Test
    public void testGetExternalDirectoryDefaultDownloadDirectory() {
        File externalRootDir = fileManager.getExternalRootDirectory(0);
        File downloadDir = new File(externalRootDir, fileManager.getDefaultDownloadDirectoryName());
        assertFalse(downloadDir.exists());
        File externalDir = fileManager.getExternalDirectory(fileManager.getDefaultDownloadDirectoryName(), 0);
        assertTrue(externalDir.exists());
        assertTrue(downloadDir.exists());
        assertEquals(externalDir, downloadDir);
        externalDir = fileManager.getExternalDirectory(fileManager.getDefaultDownloadDirectoryName(), 0);
        assertTrue(externalDir.exists());
        assertTrue(downloadDir.exists());
        assertEquals(externalDir, downloadDir);
    }

    @Test
    public void testGetExternalDirectory() {
        File externalRootDir = fileManager.getExternalRootDirectory(0);
        File dir = new File(externalRootDir, "test/download");
        assertFalse(dir.exists());
        File externalDir = fileManager.getExternalDirectory("test/download", 0);
        assertTrue(externalDir.exists());
        assertTrue(dir.exists());
        assertEquals(externalDir, dir);
        externalDir = fileManager.getExternalDirectory("test/download", 0);
        assertTrue(externalDir.exists());
        assertTrue(dir.exists());
        assertEquals(externalDir, dir);
    }

    @Test
    public void testGetExternalDirectoryError() throws IOException {
        File externalRootDir = fileManager.getExternalRootDirectory(0);
        File dir = new File(externalRootDir, "test");
        assertTrue(dir.mkdir());
        File file = new File(dir, "download");
        assertTrue(file.createNewFile());
        File externalDir = fileManager.getExternalDirectory("test/download", 0);
        assertNull(externalDir);
    }

    @Test
    public void testGetExternalDirectoryEmpty() {
        File externalRootDir = fileManager.getExternalRootDirectory(0);
        File dir = new File(externalRootDir, "");
        assertTrue(dir.exists());
        File externalDir = fileManager.getExternalDirectory("", 0);
        assertTrue(externalDir.exists());
        assertEquals(externalDir, dir);
    }

    @Test
    public void testGetDifferentExternalDirectories() {
        File externalRootDir1 = fileManager.getExternalRootDirectory(0);
        File externalRootDir2 = fileManager.getExternalRootDirectory(1);
        assertNotEquals(externalRootDir1, externalRootDir2);
        File dir1 = new File(externalRootDir1, "test/download");
        File dir2 = new File(externalRootDir2, "test/download");
        File externalDir1 = fileManager.getExternalDirectory("test/download", 0);
        File externalDir2 = fileManager.getExternalDirectory("test/download", 1);
        assertNotEquals(externalDir1, externalDir2);
        assertTrue(externalDir1.exists());
        assertTrue(externalDir2.exists());
        assertEquals(externalDir1, dir1);
        assertEquals(externalDir2, dir2);
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
        File externalRootDir = fileManager.getExternalRootDirectory(0);
        String parent = fileManager.getAbsoluteParent(externalRootDir.getAbsolutePath(), externalRootDir.getAbsolutePath());
        assertEquals(new File(parent), externalRootDir);
        File externalDir = fileManager.getExternalDirectory(fileManager.getDefaultDownloadDirectoryName(), 0);
        parent = fileManager.getAbsoluteParent(externalRootDir.getAbsolutePath(), externalDir.getAbsolutePath());
        assertEquals(new File(parent), externalRootDir);
        externalDir = fileManager.getExternalDirectory("test/download", 0);
        parent = fileManager.getAbsoluteParent(externalRootDir.getAbsolutePath(), externalDir.getAbsolutePath());
        assertEquals(new File(parent), new File(externalRootDir, "test"));
    }

    @Test
    public void testGetAbsolutePath() {
        assertEquals("root", fileManager.getAbsolutePath("root", null));
        assertEquals("root", fileManager.getAbsolutePath("root", ""));
        assertEquals("root/folder", fileManager.getAbsolutePath("root", "folder"));
        assertEquals("root/folder", fileManager.getAbsolutePath("root/", "folder"));
        assertEquals("root/folder/download", fileManager.getAbsolutePath("root/", "folder/download"));
        assertEquals("root/folder/download/file.txt", fileManager.getAbsolutePath("root/folder/download", "file.txt"));
    }

    @Test
    public void testGetNestedPath() {
        assertEquals("", fileManager.getNestedPath(null, null));
        assertEquals("", fileManager.getNestedPath("", ""));
        assertEquals("folder", fileManager.getNestedPath("folder", null));
        assertEquals("folder", fileManager.getNestedPath("", "folder"));
        assertEquals("folder/folder", fileManager.getNestedPath("folder", "folder"));
        assertEquals("test/xyz/folder", fileManager.getNestedPath("test/xyz/", "folder"));
        assertEquals("test/xyz/file.txt", fileManager.getNestedPath("test/xyz/", "file.txt"));
    }

    @Test
    public void testGetFilesRoot() throws Exception {
        File externalRootDir = fileManager.getExternalRootDirectory(0);
        File file1 = new File(externalRootDir, "file1");
        File dir1 = new File(externalRootDir, "dir1");
        File dir2 = new File(externalRootDir, "dir2");
        File file2 = new File(externalRootDir, "file2");
        assertTrue(file1.createNewFile());
        assertTrue(dir1.mkdir());
        assertTrue(dir2.mkdir());
        assertTrue(file2.createNewFile());
        List<FileEntry> entries = fileManager.getFiles(externalRootDir.getAbsolutePath(), externalRootDir.getAbsolutePath());
        assertEquals(5, entries.size());
        assertTrue(areEntriesEqual(entries.get(0), getFileEntry("..", true, true, false)));
        assertTrue(areEntriesEqual(entries.get(1), getFileEntry("dir1", true, false, true)));
        assertTrue(areEntriesEqual(entries.get(2), getFileEntry("dir2", true, false, true)));
        assertTrue(areEntriesEqual(entries.get(3), getFileEntry("file1", false, false, false)));
        assertTrue(areEntriesEqual(entries.get(4), getFileEntry("file2", false, false, false)));
    }

    @Test
    public void testGetFilesNonRoot() throws Exception {
        File externalRootDir = fileManager.getExternalRootDirectory(0);
        File externalDir = fileManager.getExternalDirectory("test/download", 0);
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
        assertTrue(areEntriesEqual(entries.get(0), getFileEntry("..", true, true, true)));
        assertTrue(areEntriesEqual(entries.get(1), getFileEntry("dir1", true, false, true)));
        assertTrue(areEntriesEqual(entries.get(2), getFileEntry("dir2", true, false, true)));
        assertTrue(areEntriesEqual(entries.get(3), getFileEntry("file1", false, false, false)));
        assertTrue(areEntriesEqual(entries.get(4), getFileEntry("file2", false, false, false)));
    }

    @Test
    public void testDeleteDirectory() throws Exception {
        File internalDownloadDir = fileManager.getInternalDownloadDirectory();
        assertEquals(0, Objects.requireNonNull(internalDownloadDir.listFiles()).length);
        File testFile = new File(internalDownloadDir, "testfile");
        assertTrue(testFile.createNewFile());
        File testDirectory = new File(internalDownloadDir, "testdirectory");
        assertTrue(testDirectory.mkdir());
        assertEquals(2, Objects.requireNonNull(internalDownloadDir.listFiles()).length);
        fileManager.delete(fileManager.getInternalDownloadDirectory());
        assertFalse(internalDownloadDir.exists());
        File externalDir = fileManager.getExternalDirectory("test/download", 0);
        fileManager.delete(externalDir);
        assertFalse(externalDir.exists());
        assertTrue(new File(fileManager.getExternalRootDirectory(0), "test").exists());
    }

    @Test
    public void testDeleteFile() throws Exception {
        File internalDownloadDir = fileManager.getInternalDownloadDirectory();
        assertEquals(0, Objects.requireNonNull(internalDownloadDir.listFiles()).length);
        File testFile = new File(internalDownloadDir, "testfile");
        assertTrue(testFile.createNewFile());
        fileManager.delete(testFile);
        assertFalse(testFile.exists());
        assertTrue(internalDownloadDir.exists());
    }

    @Test
    public void testGetDownloadFileNameSpecified() throws Exception {
        assertEquals("xyz.jpg", fileManager.getDownloadFileName(new URL("http://www.host.com"), "xyz.jpg", "image/jpeg"));
        assertEquals("xyz.jpg", fileManager.getDownloadFileName(new URL("http://www.host.com"), "xyz", "image/jpeg"));
        assertEquals("  xyz .jpg", fileManager.getDownloadFileName(new URL("http://www.host.com"), "  xyz ", "image/jpeg"));
        assertEquals(" abc_xyz_xyz.mp3", fileManager.getDownloadFileName(new URL("http://www.host.com"), " abc/xyz/xyz", "audio/mpeg"));
        assertEquals("x y z.txt", fileManager.getDownloadFileName(new URL("http://www.host.com"), "x y z", "text/plain"));
        assertEquals("xy z.ab c", fileManager.getDownloadFileName(new URL("http://www.host.com"), "xy z.ab c", "text/plain"));
        assertEquals("  .ab c", fileManager.getDownloadFileName(new URL("http://www.host.com"), "  .ab c", "text/plain"));
        assertEquals("downloadfile.txt", fileManager.getDownloadFileName(null, "..", "text/plain"));
        assertEquals("downloadfile.txt", fileManager.getDownloadFileName(null, "....//..", "text/plain"));
        assertTrue(verifyHTMLFile("downloadfile", fileManager.getDownloadFileName(null, null, "text/html")));
        assertTrue(verifyHTMLFile("downloadfile", fileManager.getDownloadFileName(null, "", "text/html")));

    }

    @Test
    public void testGetDownloadFileNameURL() throws Exception {
        assertEquals("xyz.jpg", fileManager.getDownloadFileName(new URL("http://www.host.com/xyz.jpg"), null, null));
        assertEquals("xyz", fileManager.getDownloadFileName(new URL("http://www.host.com/xyz/"), null, null));
        assertEquals("xyz.pdf", fileManager.getDownloadFileName(new URL("http://www.host.com/xyz/"), null, "application/pdf"));
        assertEquals("xyz.abc", fileManager.getDownloadFileName(new URL("http://www.host.com/xyz.abc"), "..", null));
        assertEquals("xyz.abc", fileManager.getDownloadFileName(new URL("http://www.host.com/123/xyz.abc"), "/", "image/jpeg"));
        assertEquals("xyz.a b c", fileManager.getDownloadFileName(new URL("http://www.host.com/123/xyz.a b c"), "", "image/jpeg"));
        assertEquals("xyz.a bc", fileManager.getDownloadFileName(new URL("http://www.host.com/123/xyz.a%20bc"), "", "image/jpeg"));
        assertEquals("xy z.abc", fileManager.getDownloadFileName(new URL("http://www.host.com/123/xy z.abc"), "", "image/jpeg"));
        assertEquals("www_host_com.jpg", fileManager.getDownloadFileName(new URL("http://www.host.com/.."), null, "image/jpeg"));
        assertTrue(verifyHTMLFile("www_host_com", fileManager.getDownloadFileName(new URL("http://www.host.com/abc/.."), null, "text/html")));
        assertEquals("www_host_com.jpg", fileManager.getDownloadFileName(new URL("http://www.host.com/......"), null, "image/jpeg"));
        assertEquals("www_host_com.jpg", fileManager.getDownloadFileName(new URL("http://www.host.com/xyz//......"), null, "image/jpeg"));
    }

    @Test
    public void testGetDownloadFileNameHost() throws Exception {
        assertEquals("www_host_com", fileManager.getDownloadFileName(new URL("http://www.host.com"), null, null));
        assertEquals("www_host_com.css", fileManager.getDownloadFileName(new URL("http://www.host.com"), null, "text/css"));
        assertEquals("abcd.css", fileManager.getDownloadFileName(new URL("http://abcd"), "/", "text/css"));
        assertEquals("127_0_0_1.css", fileManager.getDownloadFileName(new URL("http://127.0.0.1///......"), "...////...///", "text/css"));
        assertEquals("[3ffe:1900:4545:3:200:f8ff:fe21:67cf].zip", fileManager.getDownloadFileName(new URL("ftp://[3ffe:1900:4545:3:200:f8ff:fe21:67cf]"), "", "application/zip"));
        assertEquals("www_h ost_com.rtf", fileManager.getDownloadFileName(new URL("http://www.h ost.com"), "", "text/rtf"));
    }

    @Test
    public void testGetLogFileName() {
        assertEquals("test_3_id_00000001.log", fileManager.getLogFileName("test", null, ".log", 1, 2, null));
        assertEquals("test_3_id_0000000F.log", fileManager.getLogFileName("test", "", ".log", 15, 2, ""));
        assertEquals("test_2_name_www_host_com_id_0000000A.log", fileManager.getLogFileName("test", "name", ".log", 10, 1, "www.host.com"));
        assertEquals("tes_t_2_www_host_com_id_000000FF.log", fileManager.getLogFileName("tes/t", "", ".log", 255, 1, "www.host.com"));
        assertEquals("test_2_123_www_host_com_id_00000001log", fileManager.getLogFileName("test", "123", "log", 1, 1, "www.host.com/download.html"));
        assertEquals("xyz_-2_name_127_0_0_1_id_00000002.log", fileManager.getLogFileName("xyz", "name", ".log", 2, -3, "127.0.0.1"));
        assertEquals("xyz_51_127_0_0_1_id_00000003.log", fileManager.getLogFileName("xyz", null, ".log", 3, 50, "ftp://127.0.0.1"));
        assertEquals("xyz_124___192_168_178_1_id_00000001.txt", fileManager.getLogFileName("xyz", " ", ".txt", 1, 123, "http://192.168.178.1/abc"));
        assertEquals("test_2_nam_e_www__host__com_id_0000000A.log", fileManager.getLogFileName("test", "nam  e", ".log", 10, 1, "www. host..com"));
    }

    @Test
    public void testDoesFileExist() throws Exception {
        File externalDir = fileManager.getExternalDirectory("test/config", 0);
        File file = new File(externalDir, "test");
        assertFalse(fileManager.doesFileExist(externalDir, file.getName()));
        assertTrue(file.createNewFile());
        assertTrue(fileManager.doesFileExist(externalDir, file.getName()));
    }

    @Test
    public void testGetValidFileName() throws Exception {
        String fileName = fileManager.getValidFileName(fileManager.getInternalDownloadDirectory(), "//test/test.file");
        assertEquals("testtest.file", fileName);
        fileName = fileManager.getValidFileName(fileManager.getInternalDownloadDirectory(), "test.file");
        assertEquals("test.file", fileName);
        File file = new File(fileManager.getInternalDownloadDirectory().getAbsolutePath(), fileName);
        assertTrue(file.createNewFile());
        fileName = fileManager.getValidFileName(fileManager.getInternalDownloadDirectory(), "test.file");
        assertEquals("test_1985.12.24_01_01_01.file", fileName);
        file = new File(fileManager.getInternalDownloadDirectory().getAbsolutePath(), fileName);
        assertTrue(file.createNewFile());
        fileName = fileManager.getValidFileName(fileManager.getInternalDownloadDirectory(), "test.file");
        assertEquals("test_1985.12.24_01_01_01_(1).file", fileName);
        file = new File(fileManager.getInternalDownloadDirectory().getAbsolutePath(), fileName);
        assertTrue(file.createNewFile());
        fileName = fileManager.getValidFileName(fileManager.getInternalDownloadDirectory(), "test.file");
        assertEquals("test_1985.12.24_01_01_01_(2).file", fileName);
        file = new File(fileManager.getInternalDownloadDirectory().getAbsolutePath(), "test_1985.12.24_01_01_01.file");
        assertTrue(file.delete());
        fileName = fileManager.getValidFileName(fileManager.getInternalDownloadDirectory(), "test.file");
        assertEquals("test_1985.12.24_01_01_01.file", fileName);
        file = new File(fileManager.getInternalDownloadDirectory().getAbsolutePath(), "test.file");
        assertTrue(file.delete());
        fileName = fileManager.getValidFileName(fileManager.getInternalDownloadDirectory(), "test.file");
        assertEquals("test.file", fileName);
    }

    private boolean verifyHTMLFile(String baseName, String file) {
        if (file.endsWith("htm") || file.endsWith("html")) {
            return file.startsWith(baseName);
        }
        return false;
    }

    private long getTestTimestamp() {
        Calendar calendar = new GregorianCalendar(1985, Calendar.DECEMBER, 24, 1, 1, 1);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTimeInMillis();
    }

    private FileEntry getFileEntry(String name, boolean directory, boolean parent, boolean canVisit) {
        FileEntry fileEntry = new FileEntry();
        fileEntry.setName(name);
        fileEntry.setDirectory(directory);
        fileEntry.setParent(parent);
        fileEntry.setCanVisit(canVisit);
        return fileEntry;
    }

    private boolean areEntriesEqual(FileEntry entry1, FileEntry entry2) {
        if (!entry1.getName().equals(entry2.getName())) {
            return false;
        }
        if (entry1.isDirectory() != entry2.isDirectory()) {
            return false;
        }
        if (entry1.canVisit() != entry2.canVisit()) {
            return false;
        }
        return entry1.isParent() == entry2.isParent();
    }
}
