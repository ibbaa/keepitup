/*
 * Copyright (c) 2026 Alwin Ibba
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

package net.ibbaa.keepitup.ui.validation;

import static org.junit.Assert.assertEquals;

import android.graphics.Color;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import net.ibbaa.keepitup.test.mock.TestRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class TextDescriptionColorValidatingWatcherTest {

    @Test
    public void testValidateTrue() {
        EditText testEditText = new EditText(TestRegistry.getContext());
        TextView testTextView = new TextView(TestRegistry.getContext());
        TextDescriptionColorValidatingWatcher watcher = new TextDescriptionColorValidatingWatcher(testEditText, testTextView, this::validateTrue, "ok", "error", Color.GREEN, Color.RED);
        testEditText.setText("");
        watcher.afterTextChanged(null);
        assertEquals(View.GONE, testTextView.getVisibility());
        assertEquals("", testTextView.getText());
        testEditText.setText("xyz");
        watcher.afterTextChanged(null);
        assertEquals(View.VISIBLE, testTextView.getVisibility());
        assertEquals(Color.GREEN, testTextView.getCurrentTextColor());
        assertEquals("ok", testTextView.getText());
    }

    @Test
    public void testValidateFalse() {
        EditText testEditText = new EditText(TestRegistry.getContext());
        TextView testTextView = new TextView(TestRegistry.getContext());
        TextDescriptionColorValidatingWatcher watcher = new TextDescriptionColorValidatingWatcher(testEditText, testTextView, this::validateFalse, "ok", "error", Color.GREEN, Color.RED);
        testEditText.setText("");
        watcher.afterTextChanged(null);
        assertEquals(View.GONE, testTextView.getVisibility());
        assertEquals("", testTextView.getText());
        testEditText.setText("xyz");
        watcher.afterTextChanged(null);
        assertEquals(View.VISIBLE, testTextView.getVisibility());
        assertEquals(Color.RED, testTextView.getCurrentTextColor());
        assertEquals("error", testTextView.getText());
    }

    @Test
    public void testDependentWatcher() {
        EditText parentEditText = new EditText(TestRegistry.getContext());
        TextView parentTextView = new TextView(TestRegistry.getContext());
        TextDescriptionColorValidatingWatcher parentWatcher = new TextDescriptionColorValidatingWatcher(parentEditText, parentTextView, this::validateTrue, "okParent", "errorParent", Color.GREEN, Color.RED);
        EditText dependentEditText = new EditText(TestRegistry.getContext());
        TextView dependentTextView = new TextView(TestRegistry.getContext());
        TextDescriptionColorValidatingWatcher dependentWatcher = new TextDescriptionColorValidatingWatcher(dependentEditText, dependentTextView, this::validateFalse, "okDependent", "errorDependent", Color.GREEN, Color.RED);
        parentWatcher.addDependentWatcher(dependentWatcher);
        parentEditText.setText("");
        dependentEditText.setText("");
        parentWatcher.afterTextChanged(null);
        assertEquals(View.GONE, parentTextView.getVisibility());
        assertEquals(View.GONE, dependentTextView.getVisibility());
        parentEditText.setText("something");
        dependentEditText.setText("abc");
        parentWatcher.afterTextChanged(null);
        assertEquals(View.VISIBLE, parentTextView.getVisibility());
        assertEquals(Color.GREEN, parentTextView.getCurrentTextColor());
        assertEquals("okParent", parentTextView.getText());
        assertEquals(View.VISIBLE, dependentTextView.getVisibility());
        assertEquals(Color.RED, dependentTextView.getCurrentTextColor());
        assertEquals("errorDependent", dependentTextView.getText());
    }

    @SuppressWarnings({"SameReturnValue", "unused"})
    private boolean validateTrue(EditText editText) {
        return true;
    }

    @SuppressWarnings({"SameReturnValue", "unused"})
    private boolean validateFalse(EditText editText) {
        return false;
    }
}
