/*
 * Copyright (c) 2023. Alwin Ibba
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

package net.ibbaa.keepitup.logging;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import com.google.common.base.Charsets;

import net.ibbaa.keepitup.test.mock.TestRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class HousekeeperTest {

    private LogFileManager logFileManager;

    @Before
    public void beforeEachTestMethod() {
        logFileManager = new LogFileManager();
        logFileManager.delete(getTestLogFileFolder());
    }

    @Test
    public void testArchiveLimitNotExceeded() throws Exception {
        File logDir = getTestLogFileFolder();
        File[] files = logDir.listFiles();
        assertEquals(0, files.length);
        Housekeeper housekeeper = new Housekeeper(logDir.getAbsolutePath(), "test.log", 5, -1, null);
        housekeeper.doHousekeepingNow();
        files = logDir.listFiles();
        assertEquals(0, files.length);
        createTestFile(logDir, "test1.txt", "Test1Text");
        createTestFile(logDir, "test2.txt", "Test2Text");
        files = logDir.listFiles();
        assertEquals(2, files.length);
        housekeeper.doHousekeepingNow();
        files = logDir.listFiles();
        assertEquals(2, files.length);
        createTestFile(logDir, "test3.txt", "Test3Text");
        createTestFile(logDir, "test4.txt", "Test4Text");
        files = logDir.listFiles();
        assertEquals(4, files.length);
        housekeeper.doHousekeepingNow();
        files = logDir.listFiles();
        assertEquals(4, files.length);
        createTestFile(logDir, "test5.log", "Test3Text");
        createTestFile(logDir, "test6.log", "Test4Text");
        files = logDir.listFiles();
        assertEquals(6, files.length);
        housekeeper = new Housekeeper(logDir.getAbsolutePath(), "test.log", 5, -1, (File dir, String name) -> name.endsWith("txt"));
        housekeeper.doHousekeepingNow();
        files = logDir.listFiles();
        assertEquals(6, files.length);
    }

    @Test
    public void testZipCreated() throws Exception {
        File logDir = getTestLogFileFolder();
        Housekeeper housekeeper = new Housekeeper(logDir.getAbsolutePath(), "test.log", 5, -1, (File dir, String name) -> name.endsWith("txt"));
        createTestFile(logDir, "test1.txt", "Test1Text");
        createTestFile(logDir, "test2.txt", "Test2Text");
        createTestFile(logDir, "test3.txt", "Test3Text");
        createTestFile(logDir, "test4.txt", "Test4Text");
        createTestFile(logDir, "test5.txt", "Test5Text");
        createTestFile(logDir, "test6.log", "Test6Text");
        createTestFile(logDir, "test7.log", "Test7Text");
        housekeeper.doHousekeepingNow();
        File[] files = logDir.listFiles();
        assertEquals(3, files.length);
        assertTrue(containsFile(files, "test6", "log"));
        assertTrue(containsFile(files, "test7", "log"));
        assertTrue(containsFile(files, "test", "zip"));
        File zipFile = getFile(files, "test", "zip");
        assertNotNull(zipFile);
        Map<String, byte[]> contentMap = new HashMap<>();
        ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile));
        ZipEntry entry1 = zipInputStream.getNextEntry();
        byte[] content1 = getZipEntryContent(zipInputStream);
        ZipEntry entry2 = zipInputStream.getNextEntry();
        byte[] content2 = getZipEntryContent(zipInputStream);
        ZipEntry entry3 = zipInputStream.getNextEntry();
        byte[] content3 = getZipEntryContent(zipInputStream);
        ZipEntry entry4 = zipInputStream.getNextEntry();
        byte[] content4 = getZipEntryContent(zipInputStream);
        ZipEntry entry5 = zipInputStream.getNextEntry();
        byte[] content5 = getZipEntryContent(zipInputStream);
        assertNull(zipInputStream.getNextEntry());
        contentMap.put(entry1.getName(), content1);
        contentMap.put(entry2.getName(), content2);
        contentMap.put(entry3.getName(), content3);
        contentMap.put(entry4.getName(), content4);
        contentMap.put(entry5.getName(), content5);
        assertArrayEquals("Test1Text".getBytes(Charsets.UTF_8), contentMap.get("test1.txt"));
        assertArrayEquals("Test2Text".getBytes(Charsets.UTF_8), contentMap.get("test2.txt"));
        assertArrayEquals("Test3Text".getBytes(Charsets.UTF_8), contentMap.get("test3.txt"));
        assertArrayEquals("Test4Text".getBytes(Charsets.UTF_8), contentMap.get("test4.txt"));
        assertArrayEquals("Test5Text".getBytes(Charsets.UTF_8), contentMap.get("test5.txt"));
        zipInputStream.close();
    }

    @Test
    public void testDeleteArchive() throws Exception {
        File logDir = getTestLogFileFolder();
        Housekeeper housekeeper = new Housekeeper(logDir.getAbsolutePath(), "test.txt", 3, 3, (File dir, String name) -> name.endsWith("txt"));
        createTestFile(logDir, "test1.txt", "Test1Text");
        createTestFile(logDir, "test2.txt", "Test2Text");
        createTestFile(logDir, "test3.txt", "Test3Text");
        Thread.sleep(10);
        housekeeper.doHousekeepingNow();
        File[] files = logDir.listFiles();
        assertEquals(1, files.length);
        assertTrue(containsFile(files, "test", "zip"));
        File oldest = files[0];
        createTestFile(logDir, "test1.txt", "Test1Text");
        createTestFile(logDir, "test2.txt", "Test2Text");
        createTestFile(logDir, "test3.txt", "Test3Text");
        Thread.sleep(10);
        housekeeper.doHousekeepingNow();
        files = logDir.listFiles();
        assertEquals(2, files.length);
        createTestFile(logDir, "test1.txt", "Test1Text");
        createTestFile(logDir, "test2.txt", "Test2Text");
        createTestFile(logDir, "test3.txt", "Test3Text");
        Thread.sleep(10);
        housekeeper.doHousekeepingNow();
        files = logDir.listFiles();
        assertEquals(2, files.length);
        assertFalse(oldest.exists());
    }

    private File getTestLogFileFolder() {
        File dir = TestRegistry.getContext().getFilesDir();
        File logDir = new File(dir, "logdir");
        if (!logDir.exists()) {
            assertTrue(logDir.mkdirs());
        }
        return logDir;
    }

    private File createTestFile(File dir, String name, String content) throws Exception {
        File file = new File(dir, name);
        FileOutputStream outputStream = new FileOutputStream(file);
        outputStream.write(content.getBytes(Charsets.UTF_8));
        outputStream.flush();
        outputStream.close();
        return file;
    }

    private byte[] getZipEntryContent(ZipInputStream zipInputStream) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[20];
        int read;
        while ((read = zipInputStream.read(buffer, 0, 20)) >= 0) {
            outputStream.write(buffer, 0, read);
        }
        return outputStream.toByteArray();
    }

    private File getFile(File[] files, String baseName, String extension) {
        for (File file : files) {
            String name = file.getName();
            if (name.startsWith(baseName) && name.endsWith(extension)) {
                return file;
            }
        }
        return null;
    }

    private boolean containsFile(File[] files, String baseName, String extension) {
        return getFile(files, baseName, extension) != null;
    }
}
