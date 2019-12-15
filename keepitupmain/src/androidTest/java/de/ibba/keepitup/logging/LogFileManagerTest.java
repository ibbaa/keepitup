package de.ibba.keepitup.logging;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import com.google.common.base.Charsets;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;

import de.ibba.keepitup.test.mock.TestRegistry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
    public void testZipFiles() {
        File logDir = getTestLogFileFolder();
        //TODO
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

    private long getTestTimestamp() {
        Calendar calendar = new GregorianCalendar(1985, Calendar.DECEMBER, 24, 1, 1, 1);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTimeInMillis();
    }
}
