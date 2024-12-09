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
import android.widget.TextView;

import androidx.test.espresso.matcher.BoundedMatcher;

import org.hamcrest.Description;

public class FontSizeMatcher extends BoundedMatcher<View, View> {

    private final float expectedSize;

    public FontSizeMatcher(float expectedSize) {
        super(View.class);
        this.expectedSize = expectedSize;
    }

    @Override
    public boolean matchesSafely(View target) {
        if (!(target instanceof TextView targetEditText)) {
            return false;
        }
        float pixels = targetEditText.getTextSize();
        float actualSize = pixels / target.getResources().getDisplayMetrics().scaledDensity;
        return Float.compare(actualSize, expectedSize) == 0;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("with fontSize: ");
        description.appendValue(expectedSize);
    }
}
