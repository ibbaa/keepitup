/*
 * Copyright (c) 2024. Alwin Ibba
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
import android.widget.EditText;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import net.ibbaa.keepitup.test.mock.TestRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class TextColorValidatingWatcherTest {

    @Test
    public void testChangeTextColor() {
        EditText testEditText = new EditText(TestRegistry.getContext());
        TextColorValidatingWatcher watcher = new TextColorValidatingWatcher(testEditText, this::validateTrue, Color.BLACK, Color.RED);
        watcher.afterTextChanged(null);
        assertEquals(Color.BLACK, testEditText.getCurrentTextColor());
        watcher = new TextColorValidatingWatcher(testEditText, this::validateFalse, Color.BLACK, Color.RED);
        watcher.afterTextChanged(null);
        assertEquals(Color.RED, testEditText.getCurrentTextColor());
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
