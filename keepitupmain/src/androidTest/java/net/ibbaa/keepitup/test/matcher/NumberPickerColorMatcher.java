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

package net.ibbaa.keepitup.test.matcher;

import android.view.View;
import android.widget.NumberPicker;

import androidx.core.content.ContextCompat;
import androidx.test.espresso.matcher.BoundedMatcher;

import org.hamcrest.Description;

public class NumberPickerColorMatcher extends BoundedMatcher<View, NumberPicker> {

    private final int expectedId;

    public NumberPickerColorMatcher(int expectedId) {
        super(NumberPicker.class);
        this.expectedId = expectedId;
    }

    @Override
    protected boolean matchesSafely(NumberPicker numberPicker) {
        int colorId = ContextCompat.getColor(numberPicker.getContext(), expectedId);
        int currentColor = numberPicker.getTextColor();
        return currentColor == colorId;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("with color: ");
        description.appendValue(expectedId);
    }
}
