package de.ibba.keepitup.ui.clipboard;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.ibba.keepitup.test.mock.TestRegistry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class SystemClipboardManagerTest {

    private IClipboardManager clipboardManager;

    @Before
    public void beforeEachTestMethod() {
        clipboardManager = new SystemClipboardManager(TestRegistry.getContext());
    }

    @Test
    public void testHasNumericIntegerData() {
        clipboardManager.putData("Test");
        assertFalse(clipboardManager.hasNumericIntegerData());
        clipboardManager.putData("11");
        assertTrue(clipboardManager.hasNumericIntegerData());
    }

    @Test
    public void testPutAndGetData() {
        clipboardManager.putData("Test");
        assertTrue(clipboardManager.hasData());
        assertEquals("Test", clipboardManager.getData());
        clipboardManager.putData("123");
        assertTrue(clipboardManager.hasData());
        assertEquals("123", clipboardManager.getData());
    }
}
