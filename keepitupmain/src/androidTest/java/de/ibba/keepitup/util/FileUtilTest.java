package de.ibba.keepitup.util;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class FileUtilTest {

    @Test
    public void testGetFileNameExtension() {
        assertEquals("", FileUtil.getFileNameExtension(null));
        assertEquals("", FileUtil.getFileNameExtension(""));
        assertEquals("jpg", FileUtil.getFileNameExtension("image.jpg"));
        assertEquals("jpg", FileUtil.getFileNameExtension(".jpg"));
        assertEquals("", FileUtil.getFileNameExtension("test"));
        assertEquals("mp3", FileUtil.getFileNameExtension("test.wav.mp3"));
    }

    @Test
    public void testGetFileNameWithoutExtension() {
        assertEquals("", FileUtil.getFileNameWithoutExtension(null));
        assertEquals("", FileUtil.getFileNameWithoutExtension(""));
        assertEquals("image", FileUtil.getFileNameWithoutExtension("image.jpg"));
        assertEquals("", FileUtil.getFileNameWithoutExtension(".jpg"));
        assertEquals("test", FileUtil.getFileNameWithoutExtension("test"));
        assertEquals("test.wav", FileUtil.getFileNameWithoutExtension("test.wav.mp3"));
    }

    @Test
    public void testSuffixFileName() {
        assertEquals("", FileUtil.suffixFileName(null, "abc"));
        assertEquals("", FileUtil.suffixFileName("", "abc"));
        assertEquals("", FileUtil.suffixFileName("", null));
        assertEquals("", FileUtil.suffixFileName("", ""));
        assertEquals("test", FileUtil.suffixFileName("test", null));
        assertEquals("test", FileUtil.suffixFileName("test", ""));
        assertEquals("test.jpg", FileUtil.suffixFileName("test.jpg", null));
        assertEquals("test.jpg", FileUtil.suffixFileName("test.jpg", ""));
        assertEquals("test_test.jpg", FileUtil.suffixFileName("test.jpg", "test"));
        assertEquals("_test.jpg", FileUtil.suffixFileName(".jpg", "test"));
        assertEquals("test.wav_123.mp3", FileUtil.suffixFileName("test.wav.mp3", "123"));
        assertEquals("test.._test.jpg", FileUtil.suffixFileName("test...jpg", "test"));
        assertEquals("test_abc", FileUtil.suffixFileName("test", "abc"));
    }
}
