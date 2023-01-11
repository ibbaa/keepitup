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

package net.ibbaa.keepitup.util;

import static org.junit.Assert.assertEquals;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import net.ibbaa.keepitup.logging.Dump;
import net.ibbaa.keepitup.resources.PreferenceManager;
import net.ibbaa.keepitup.test.mock.MockFileManager;
import net.ibbaa.keepitup.test.mock.TestRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class FileUtilTest {

    private MockFileManager fileManager;
    private PreferenceManager preferenceManager;

    @Before
    public void beforeEachTestMethod() {
        Dump.initialize(null);
        fileManager = new MockFileManager();
        fileManager.setExternalDirectory(new File("test0"), 0);
        fileManager.setExternalDirectory(new File("test1"), 1);
        fileManager.setExternalRootDirectory(new File("test0"), 0);
        fileManager.setExternalRootDirectory(new File("test1"), 1);
        fileManager.setSDCardSupported(false);
        preferenceManager = new PreferenceManager(TestRegistry.getContext());
        preferenceManager.removeAllPreferences();
    }

    @After
    public void afterEachTestMethod() {
        fileManager.reset();
        preferenceManager.removeAllPreferences();
    }

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

    @Test
    public void getExternalDirectory() {
        fileManager.setSDCardSupported(false);
        preferenceManager.setPreferenceExternalStorageType(1);
        assertEquals("test0", FileUtil.getExternalDirectory(fileManager, preferenceManager, "test").getName());
        fileManager.setSDCardSupported(true);
        preferenceManager.setPreferenceExternalStorageType(1);
        assertEquals("test0", FileUtil.getExternalDirectory(fileManager, preferenceManager, "test", true).getName());
        assertEquals("test1", FileUtil.getExternalDirectory(fileManager, preferenceManager, "test", false).getName());
        assertEquals("test1", FileUtil.getExternalDirectory(fileManager, preferenceManager, "test").getName());
        fileManager.setSDCardSupported(true);
        preferenceManager.setPreferenceExternalStorageType(0);
        assertEquals("test0", FileUtil.getExternalDirectory(fileManager, preferenceManager, "test").getName());
    }

    @Test
    public void getExternalRootDirectory() {
        fileManager.setSDCardSupported(false);
        preferenceManager.setPreferenceExternalStorageType(1);
        assertEquals("test0", FileUtil.getExternalRootDirectory(fileManager, preferenceManager).getName());
        fileManager.setSDCardSupported(true);
        preferenceManager.setPreferenceExternalStorageType(1);
        assertEquals("test0", FileUtil.getExternalRootDirectory(fileManager, preferenceManager, true).getName());
        assertEquals("test1", FileUtil.getExternalRootDirectory(fileManager, preferenceManager, false).getName());
        assertEquals("test1", FileUtil.getExternalRootDirectory(fileManager, preferenceManager).getName());
        fileManager.setSDCardSupported(true);
        preferenceManager.setPreferenceExternalStorageType(0);
        assertEquals("test0", FileUtil.getExternalRootDirectory(fileManager, preferenceManager).getName());
    }
}
