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

package net.ibbaa.keepitup.test.matcher;

import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.view.View;
import android.widget.EditText;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class PasswordVisibilityMatcher extends TypeSafeMatcher<View> {

    private final boolean hidden;

    public PasswordVisibilityMatcher(boolean hidden) {
        this.hidden = hidden;
    }

    @Override
    protected boolean matchesSafely(View view) {
        if (!(view instanceof EditText)) {
            return false;
        }
        TransformationMethod method = ((EditText) view).getTransformationMethod();
        if (!hidden) {
            return method instanceof HideReturnsTransformationMethod;
        } else {
            return method instanceof PasswordTransformationMethod;
        }
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("Has password visibility: " + hidden);
    }
}
