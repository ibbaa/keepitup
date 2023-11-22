/*
 * Copyright (c) 2023. Alwin Ibba
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

import androidx.test.espresso.matcher.BoundedMatcher;

import org.hamcrest.Description;

public class NumberPickerValueMatcher extends BoundedMatcher<View, NumberPicker> {

    private final int expectedValue;

    public NumberPickerValueMatcher(int expectedValue) {
        super(NumberPicker.class);
        this.expectedValue = expectedValue;
    }

    @Override
    protected boolean matchesSafely(NumberPicker numberPicker) {
        return numberPicker.getValue() == expectedValue;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("with vaule: ");
        description.appendValue(expectedValue);
    }
}
