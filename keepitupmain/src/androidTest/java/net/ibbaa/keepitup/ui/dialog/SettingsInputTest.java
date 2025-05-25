/*
 * Copyright (c) 2025 Alwin Ibba
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

package net.ibbaa.keepitup.ui.dialog;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.os.Bundle;
import android.text.InputType;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Collections;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class SettingsInputTest {

    @Test
    public void testToBundle() {
        SettingsInput settingsInput = new SettingsInput(null, null, null, null);
        Bundle bundle = settingsInput.toBundle();
        settingsInput = new SettingsInput(bundle);
        assertNull(settingsInput.getType());
        assertNull(settingsInput.getTitle());
        assertNull(settingsInput.getValue());
        assertNull(settingsInput.getField());
        assertEquals(-1, settingsInput.getPosition());
        assertNull(settingsInput.getValidators());
        settingsInput = new SettingsInput(SettingsInput.Type.ADDRESS, "", "", "", 5, Collections.emptyList());
        bundle = settingsInput.toBundle();
        settingsInput = new SettingsInput(bundle);
        assertEquals(SettingsInput.Type.ADDRESS, settingsInput.getType());
        assertEquals(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_URI | InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS, settingsInput.getType().getInputType());
        assertEquals("", settingsInput.getTitle());
        assertEquals("", settingsInput.getValue());
        assertEquals("", settingsInput.getField());
        assertEquals(5, settingsInput.getPosition());
        assertTrue(settingsInput.getValidators().isEmpty());
        settingsInput = new SettingsInput(SettingsInput.Type.PORT, "test", "testfield", Arrays.asList("1", "2", "3"));
        bundle = settingsInput.toBundle();
        settingsInput = new SettingsInput(bundle);
        assertEquals(SettingsInput.Type.PORT, settingsInput.getType());
        assertEquals(InputType.TYPE_CLASS_NUMBER, settingsInput.getType().getInputType());
        assertEquals("test", settingsInput.getValue());
        assertEquals("testfield", settingsInput.getField());
        assertEquals(-1, settingsInput.getPosition());
        assertThat(settingsInput.getValidators(), is(Arrays.asList("1", "2", "3")));
        settingsInput = new SettingsInput(SettingsInput.Type.INTERVAL, "title", "xyz", "testfield", 8, Collections.singletonList("1"));
        bundle = settingsInput.toBundle();
        settingsInput = new SettingsInput(bundle);
        assertEquals(SettingsInput.Type.INTERVAL, settingsInput.getType());
        assertEquals(InputType.TYPE_CLASS_NUMBER, settingsInput.getType().getInputType());
        assertEquals("title", settingsInput.getTitle());
        assertEquals("xyz", settingsInput.getValue());
        assertEquals("testfield", settingsInput.getField());
        assertEquals(8, settingsInput.getPosition());
        assertThat(settingsInput.getValidators(), is(Collections.singletonList("1")));
        settingsInput = new SettingsInput(SettingsInput.Type.PINGCOUNT, "test", "testfield", Arrays.asList("abc", "def"));
        bundle = settingsInput.toBundle();
        settingsInput = new SettingsInput(bundle);
        assertEquals(SettingsInput.Type.PINGCOUNT, settingsInput.getType());
        assertEquals(InputType.TYPE_CLASS_NUMBER, settingsInput.getType().getInputType());
        assertEquals("test", settingsInput.getValue());
        assertEquals("testfield", settingsInput.getField());
        assertEquals(-1, settingsInput.getPosition());
        assertThat(settingsInput.getValidators(), is(Arrays.asList("abc", "def")));
        settingsInput = new SettingsInput(SettingsInput.Type.CONNECTCOUNT, null, "test", "testfield", 1, Arrays.asList("abc", "def"));
        bundle = settingsInput.toBundle();
        settingsInput = new SettingsInput(bundle);
        assertEquals(SettingsInput.Type.CONNECTCOUNT, settingsInput.getType());
        assertEquals(InputType.TYPE_CLASS_NUMBER, settingsInput.getType().getInputType());
        assertNull(settingsInput.getTitle());
        assertEquals("test", settingsInput.getValue());
        assertEquals("testfield", settingsInput.getField());
        assertEquals(1, settingsInput.getPosition());
        assertThat(settingsInput.getValidators(), is(Arrays.asList("abc", "def")));
        settingsInput = new SettingsInput(SettingsInput.Type.PINGPACKAGESIZE, "test", "test", "testfield", 1, Arrays.asList("1", "1"));
        bundle = settingsInput.toBundle();
        settingsInput = new SettingsInput(bundle);
        assertEquals(SettingsInput.Type.PINGPACKAGESIZE, settingsInput.getType());
        assertEquals(InputType.TYPE_CLASS_NUMBER, settingsInput.getType().getInputType());
        assertEquals("test", settingsInput.getTitle());
        assertEquals("test", settingsInput.getValue());
        assertEquals("testfield", settingsInput.getField());
        assertEquals(1, settingsInput.getPosition());
        assertThat(settingsInput.getValidators(), is(Arrays.asList("1", "1")));
        settingsInput = new SettingsInput(SettingsInput.Type.NOTIFICATIONAFTER, "test", "testfield", Arrays.asList("1", "1"));
        bundle = settingsInput.toBundle();
        settingsInput = new SettingsInput(bundle);
        assertEquals(SettingsInput.Type.NOTIFICATIONAFTER, settingsInput.getType());
        assertEquals(InputType.TYPE_CLASS_NUMBER, settingsInput.getType().getInputType());
        assertEquals("test", settingsInput.getValue());
        assertEquals("testfield", settingsInput.getField());
        assertEquals(-1, settingsInput.getPosition());
        assertThat(settingsInput.getValidators(), is(Arrays.asList("1", "1")));
        settingsInput = new SettingsInput(SettingsInput.Type.TASKNAME, "test", "test", "testfield", 3, Arrays.asList("1", "1"));
        bundle = settingsInput.toBundle();
        settingsInput = new SettingsInput(bundle);
        assertEquals(SettingsInput.Type.TASKNAME, settingsInput.getType());
        assertEquals(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS, settingsInput.getType().getInputType());
        assertEquals("test", settingsInput.getTitle());
        assertEquals("test", settingsInput.getValue());
        assertEquals("testfield", settingsInput.getField());
        assertEquals(3, settingsInput.getPosition());
        assertThat(settingsInput.getValidators(), is(Arrays.asList("1", "1")));
    }
}
