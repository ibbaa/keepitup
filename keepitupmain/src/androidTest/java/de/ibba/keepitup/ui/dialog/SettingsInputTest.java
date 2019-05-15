package de.ibba.keepitup.ui.dialog;

import android.os.Bundle;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

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
        SettingsInput settingsInput = new SettingsInput(null, null, null);
        Bundle bundle = settingsInput.toBundle();
        settingsInput = new SettingsInput(bundle);
        assertNull(settingsInput.getValue());
        assertNull(settingsInput.getField());
        assertNull(settingsInput.getValidators());
        settingsInput = new SettingsInput("", "", Collections.emptyList());
        bundle = settingsInput.toBundle();
        settingsInput = new SettingsInput(bundle);
        assertEquals("", settingsInput.getValue());
        assertEquals("", settingsInput.getField());
        assertTrue(settingsInput.getValidators().isEmpty());
        settingsInput = new SettingsInput("test", "testfield", Arrays.asList("1", "2", "3"));
        bundle = settingsInput.toBundle();
        settingsInput = new SettingsInput(bundle);
        assertEquals("test", settingsInput.getValue());
        assertEquals("testfield", settingsInput.getField());
        assertThat(settingsInput.getValidators(), is(Arrays.asList("1", "2", "3")));
    }
}
