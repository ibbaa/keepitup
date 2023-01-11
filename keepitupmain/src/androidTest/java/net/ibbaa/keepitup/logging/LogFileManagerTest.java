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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import com.google.common.base.Charsets;

import net.ibbaa.keepitup.test.mock.TestRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class LogFileManagerTest {

    private LogFileManager logFileManager;

    @Before
    public void beforeEachTestMethod() {
        logFileManager = new LogFileManager();
        logFileManager.delete(getTestLogFileFolder());
    }

    @Test
    public void testGetFileNameExtension() {
        assertEquals("", logFileManager.getFileNameExtension(null));
        assertEquals("", logFileManager.getFileNameExtension(""));
        assertEquals("jpg", logFileManager.getFileNameExtension("image.jpg"));
        assertEquals("jpg", logFileManager.getFileNameExtension(".jpg"));
        assertEquals("", logFileManager.getFileNameExtension("test"));
        assertEquals("mp3", logFileManager.getFileNameExtension("test.wav.mp3"));
    }

    @Test
    public void testGetFileNameWithoutExtension() {
        assertEquals("", logFileManager.getFileNameWithoutExtension(null));
        assertEquals("", logFileManager.getFileNameWithoutExtension(""));
        assertEquals("image", logFileManager.getFileNameWithoutExtension("image.jpg"));
        assertEquals("", logFileManager.getFileNameWithoutExtension(".jpg"));
        assertEquals("test", logFileManager.getFileNameWithoutExtension("test"));
        assertEquals("test.wav", logFileManager.getFileNameWithoutExtension("test.wav.mp3"));
    }

    @Test
    public void testSuffixFileName() {
        assertEquals("", logFileManager.suffixFileName(null, "abc"));
        assertEquals("", logFileManager.suffixFileName("", "abc"));
        assertEquals("", logFileManager.suffixFileName("", null));
        assertEquals("", logFileManager.suffixFileName("", ""));
        assertEquals("test", logFileManager.suffixFileName("test", null));
        assertEquals("test", logFileManager.suffixFileName("test", ""));
        assertEquals("test.jpg", logFileManager.suffixFileName("test.jpg", null));
        assertEquals("test.jpg", logFileManager.suffixFileName("test.jpg", ""));
        assertEquals("test_test.jpg", logFileManager.suffixFileName("test.jpg", "test"));
        assertEquals("_test.jpg", logFileManager.suffixFileName(".jpg", "test"));
        assertEquals("test.wav_123.mp3", logFileManager.suffixFileName("test.wav.mp3", "123"));
        assertEquals("test.._test.jpg", logFileManager.suffixFileName("test...jpg", "test"));
        assertEquals("test_abc", logFileManager.suffixFileName("test", "abc"));
    }

    @Test
    public void testDeleteDirectory() throws Exception {
        File logDir = getTestLogFileFolder();
        assertEquals(0, logDir.listFiles().length);
        File testFile = new File(logDir, "testfile");
        assertTrue(testFile.createNewFile());
        File testDirectory = new File(logDir, "testdirectory");
        assertTrue(testDirectory.mkdir());
        assertEquals(2, logDir.listFiles().length);
        logFileManager.delete(logDir);
        assertFalse(logDir.exists());
    }

    @Test
    public void testDeleteOldest() throws Exception {
        File logDir = getTestLogFileFolder();
        File testFile1 = new File(logDir, "testfile1");
        File testFile2 = new File(logDir, "testfile2");
        File testFile3 = new File(logDir, "testfile3");
        assertTrue(testFile1.createNewFile());
        Thread.sleep(10);
        assertTrue(testFile2.createNewFile());
        Thread.sleep(10);
        assertTrue(testFile3.createNewFile());
        logFileManager.deleteOldest(new File[]{testFile1, testFile2, testFile3});
        assertFalse(testFile1.exists());
        assertTrue(testFile2.exists());
        assertTrue(testFile3.exists());
    }

    @Test
    public void testGetValidFileNameWithTimestamp() throws Exception {
        File logDir = getTestLogFileFolder();
        long testTimestamp = getTestTimestamp();
        String fileName = logFileManager.getValidFileName(logDir, "test.file", testTimestamp);
        assertEquals("test.file", fileName);
        File file = new File(logDir.getAbsolutePath(), fileName);
        assertTrue(file.createNewFile());
        fileName = logFileManager.getValidFileName(logDir, "test.file", testTimestamp);
        assertEquals("test_1985.12.24_01_01_01.999.file", fileName);
        file = new File(logDir.getAbsolutePath(), fileName);
        assertTrue(file.createNewFile());
        fileName = logFileManager.getValidFileName(logDir, "test.file", testTimestamp);
        assertEquals("test_1985.12.24_01_01_01.999_(1).file", fileName);
        file = new File(logDir.getAbsolutePath(), fileName);
        assertTrue(file.createNewFile());
        fileName = logFileManager.getValidFileName(logDir, "test.file", testTimestamp);
        assertEquals("test_1985.12.24_01_01_01.999_(2).file", fileName);
        file = new File(logDir, "test_1985.12.24_01_01_01.999.file");
        assertTrue(file.delete());
        fileName = logFileManager.getValidFileName(logDir, "test.file", testTimestamp);
        assertEquals("test_1985.12.24_01_01_01.999.file", fileName);
        file = new File(logDir.getAbsolutePath(), "test.file");
        assertTrue(file.delete());
        fileName = logFileManager.getValidFileName(logDir, "test.file", testTimestamp);
        assertEquals("test.file", fileName);
    }

    @Test
    public void testGetValidFileNameWithoutTimestamp() throws Exception {
        File logDir = getTestLogFileFolder();
        String fileName = logFileManager.getValidFileName(logDir, "test.file", null);
        assertEquals("test.file", fileName);
        File file = new File(logDir.getAbsolutePath(), fileName);
        assertTrue(file.createNewFile());
        fileName = logFileManager.getValidFileName(logDir, "test.file", null);
        assertEquals("test_(1).file", fileName);
        file = new File(logDir.getAbsolutePath(), fileName);
        assertTrue(file.createNewFile());
        fileName = logFileManager.getValidFileName(logDir, "test.file", null);
        assertEquals("test_(2).file", fileName);
        file = new File(logDir.getAbsolutePath(), "test_(1).file");
        assertTrue(file.delete());
        fileName = logFileManager.getValidFileName(logDir, "test.file", null);
        assertEquals("test_(1).file", fileName);
    }

    @Test
    public void testGetTimestampSuffix() {
        assertEquals("1985.12.24_01_01_01.999", logFileManager.getTimestampSuffix(getTestTimestamp()));
    }

    @Test
    public void testZipFiles() throws Exception {
        File logDir = getTestLogFileFolder();
        File file1 = createTestFile(logDir, "test1.txt", "Test1Text");
        File file2 = createTestFile(logDir, "test2.txt", "Test2Text");
        File file3 = createTestFile(logDir, "test3.txt", "Test3Text");
        File file4 = createTestFile(logDir, "test4.txt", "Test4Text");
        File file5 = createTestFile(logDir, "test5.txt", "Test5Text");
        assertTrue(file5.delete());
        File file6 = new File(logDir, "test6");
        assertTrue(file6.mkdirs());
        File zipFile = new File(logDir, "test.zip");
        logFileManager.zipFiles(Arrays.asList(file1, file2, file3, file4, file5, file6), zipFile);
        File[] files = logDir.listFiles();
        assertEquals(2, files.length);
        zipFile = files[0].getName().equals("test.zip") ? files[0] : files[1];
        assertEquals("test.zip", zipFile.getName());
        ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile));
        ZipEntry entry1 = zipInputStream.getNextEntry();
        byte[] content1 = getZipEntryContent(zipInputStream);
        ZipEntry entry2 = zipInputStream.getNextEntry();
        byte[] content2 = getZipEntryContent(zipInputStream);
        ZipEntry entry3 = zipInputStream.getNextEntry();
        byte[] content3 = getZipEntryContent(zipInputStream);
        ZipEntry entry4 = zipInputStream.getNextEntry();
        byte[] content4 = getZipEntryContent(zipInputStream);
        assertNull(zipInputStream.getNextEntry());
        assertEquals("test1.txt", entry1.getName());
        assertEquals("test2.txt", entry2.getName());
        assertEquals("test3.txt", entry3.getName());
        assertEquals("test4.txt", entry4.getName());
        assertArrayEquals("Test1Text".getBytes(Charsets.UTF_8), content1);
        assertArrayEquals("Test2Text".getBytes(Charsets.UTF_8), content2);
        assertArrayEquals("Test3Text".getBytes(Charsets.UTF_8), content3);
        assertArrayEquals("Test4Text".getBytes(Charsets.UTF_8), content4);
        zipInputStream.close();
    }

    @Test
    public void testWriteListToFile() throws Exception {
        String nl = System.lineSeparator();
        File logDir = getTestLogFileFolder();
        List<?> data = Arrays.asList("Test", 3, null, "12345");
        File file = new File(logDir, "test.txt");
        logFileManager.writeListToFile(null, null, data, file);
        File[] files = logDir.listFiles();
        assertEquals(1, files.length);
        String fileContent = getFileContent(new File(logDir, "test.txt"));
        assertEquals("Test" + nl + "3" + nl + "12345" + nl, fileContent);
        file = new File(logDir, "testwithheader.txt");
        logFileManager.writeListToFile("header", null, data, file);
        fileContent = getFileContent(new File(logDir, "testwithheader.txt"));
        assertEquals("header" + nl + "Test" + nl + "3" + nl + "12345" + nl, fileContent);
    }

    @Test
    public void testWriteListToFileEmpty() throws Exception {
        String nl = System.lineSeparator();
        File logDir = getTestLogFileFolder();
        File file = new File(logDir, "test.txt");
        logFileManager.writeListToFile(null, null, null, file);
        File[] files = logDir.listFiles();
        assertEquals(1, files.length);
        String fileContent = getFileContent(new File(logDir, "test.txt"));
        assertTrue(fileContent.isEmpty());
        file.delete();
        logFileManager.writeListToFile(null, null, new ArrayList<>(), file);
        assertEquals(1, files.length);
        fileContent = getFileContent(new File(logDir, "test.txt"));
        assertTrue(fileContent.isEmpty());
        file.delete();
        logFileManager.writeListToFile(null, "empty", new ArrayList<>(), file);
        assertEquals(1, files.length);
        fileContent = getFileContent(new File(logDir, "test.txt"));
        assertEquals("empty" + nl, fileContent);
        file.delete();
        logFileManager.writeListToFile("header", "empty", new ArrayList<>(), file);
        assertEquals(1, files.length);
        fileContent = getFileContent(new File(logDir, "test.txt"));
        assertEquals("header" + nl + "empty" + nl, fileContent);
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

    private String getFileContent(File file) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
        byte[] buffer = new byte[50];
        int read;
        while ((read = inputStream.read(buffer, 0, 50)) >= 0) {
            outputStream.write(buffer, 0, read);
        }
        inputStream.close();
        return new String(outputStream.toByteArray(), Charsets.UTF_8);
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

    private long getTestTimestamp() {
        Calendar calendar = new GregorianCalendar(1985, Calendar.DECEMBER, 24, 1, 1, 1);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTimeInMillis();
    }
}
