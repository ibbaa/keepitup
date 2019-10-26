package de.ibba.keepitup.ui.dialog;

import android.os.Bundle;
import android.text.InputType;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class SettingsInputTest {

    @Test
    public void testToBundle() {
        SettingsInput settingsInput = new SettingsInput(null, null, null, null);
        Bundle bundle = settingsInput.toBundle();
        settingsInput = new SettingsInput(bundle);
        assertNull(settingsInput.getType());
        assertNull(settingsInput.getValue());
        assertNull(settingsInput.getField());
        assertNull(settingsInput.getValidators());
        settingsInput = new SettingsInput(SettingsInput.Type.ADDRESS, "", "", Collections.emptyList());
        bundle = settingsInput.toBundle();
        settingsInput = new SettingsInput(bundle);
        assertEquals(SettingsInput.Type.ADDRESS, settingsInput.getType());
        assertEquals(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_URI | InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS, settingsInput.getType().getInputType());
        assertEquals("", settingsInput.getValue());
        assertEquals("", settingsInput.getField());
        assertTrue(settingsInput.getValidators().isEmpty());
        settingsInput = new SettingsInput(SettingsInput.Type.PORT, "test", "testfield", Arrays.asList("1", "2", "3"));
        bundle = settingsInput.toBundle();
        settingsInput = new SettingsInput(bundle);
        assertEquals(SettingsInput.Type.PORT, settingsInput.getType());
        assertEquals(InputType.TYPE_CLASS_NUMBER, settingsInput.getType().getInputType());
        assertEquals("test", settingsInput.getValue());
        assertEquals("testfield", settingsInput.getField());
        assertThat(settingsInput.getValidators(), is(Arrays.asList("1", "2", "3")));
        settingsInput = new SettingsInput(SettingsInput.Type.INTERVAL, "xyz", "testfield", Collections.singletonList("1"));
        bundle = settingsInput.toBundle();
        settingsInput = new SettingsInput(bundle);
        assertEquals(SettingsInput.Type.INTERVAL, settingsInput.getType());
        assertEquals(InputType.TYPE_CLASS_NUMBER, settingsInput.getType().getInputType());
        assertEquals("xyz", settingsInput.getValue());
        assertEquals("testfield", settingsInput.getField());
        assertThat(settingsInput.getValidators(), is(Collections.singletonList("1")));
        settingsInput = new SettingsInput(SettingsInput.Type.PINGCOUNT, "test", "testfield", Arrays.asList("abc", "def"));
        bundle = settingsInput.toBundle();
        settingsInput = new SettingsInput(bundle);
        assertEquals(SettingsInput.Type.PINGCOUNT, settingsInput.getType());
        assertEquals(InputType.TYPE_CLASS_NUMBER, settingsInput.getType().getInputType());
        assertEquals("test", settingsInput.getValue());
        assertEquals("testfield", settingsInput.getField());
        assertThat(settingsInput.getValidators(), is(Arrays.asList("abc", "def")));
    }
}
