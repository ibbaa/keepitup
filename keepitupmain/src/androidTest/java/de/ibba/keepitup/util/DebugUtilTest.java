package de.ibba.keepitup.util;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import de.ibba.keepitup.test.mock.MockFileManager;
import de.ibba.keepitup.test.mock.TestRegistry;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class DebugUtilTest {

    private MockFileManager fileManager;

    @Before
    public void beforeEachTestMethod() {
        fileManager = new MockFileManager();
    }

    @Test
    public void testGetFileLogger() {
        fileManager.setExternalDirectory(null, 0);
        assertNull(DebugUtil.getFileLogger(TestRegistry.getContext(), fileManager));
        fileManager.setExternalDirectory(new File("Test"), 0);
        assertNotNull(DebugUtil.getFileLogger(TestRegistry.getContext(), fileManager));
    }

    @Test
    public void testGetFileDump() {
        fileManager.setExternalDirectory(null, 0);
        assertNull(DebugUtil.getFileDump(TestRegistry.getContext(), fileManager));
        fileManager.setExternalDirectory(new File("Test"), 0);
        assertNotNull(DebugUtil.getFileDump(TestRegistry.getContext(), fileManager));
    }
}
