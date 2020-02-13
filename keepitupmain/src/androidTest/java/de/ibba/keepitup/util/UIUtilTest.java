package de.ibba.keepitup.util;

import android.text.InputType;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class UIUtilTest {

    @Test
    public void testIsInpuTypeNumber() {
        assertFalse(UIUtil.isInpuTypeNumber(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_URI | InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS));
        assertFalse(UIUtil.isInpuTypeNumber(InputType.TYPE_CLASS_TEXT));
        assertTrue(UIUtil.isInpuTypeNumber(InputType.TYPE_CLASS_NUMBER));
        assertTrue(UIUtil.isInpuTypeNumber(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD | InputType.TYPE_TEXT_VARIATION_URI));
        assertTrue(UIUtil.isInpuTypeNumber(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD));
        assertTrue(UIUtil.isInpuTypeNumber(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL));
    }
}
