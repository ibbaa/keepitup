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

import static org.junit.Assert.assertEquals;

import android.widget.EditText;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import net.ibbaa.keepitup.test.mock.TestRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class PlaceholderFocusChangeListenerTest {

    private PlaceholderFocusChangeListener focusChangeListener;
    private EditText editText;

    @Before
    public void beforeEachTestMethod() {
        editText = new EditText(TestRegistry.getContext());
        focusChangeListener = new PlaceholderFocusChangeListener(editText, "not set");
    }

    @Test
    public void testOnFocusChangeNotSet() {
        editText.setText("not set");
        focusChangeListener.onFocusChange(editText, true);
        assertEquals("", editText.getText().toString());
        focusChangeListener.onFocusChange(editText, false);
        assertEquals("not set", editText.getText().toString());
    }

    @Test
    public void testOnFocusChangeWithValue() {
        editText.setText("123");
        focusChangeListener.onFocusChange(editText, true);
        assertEquals("123", editText.getText().toString());
        focusChangeListener.onFocusChange(editText, false);
        assertEquals("123", editText.getText().toString());
    }
}
