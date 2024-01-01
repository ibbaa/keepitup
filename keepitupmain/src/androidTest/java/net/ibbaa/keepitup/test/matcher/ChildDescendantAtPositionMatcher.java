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

package net.ibbaa.keepitup.test.matcher;

import android.view.View;
import android.view.ViewGroup;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class ChildDescendantAtPositionMatcher extends TypeSafeMatcher<View> {

    private final Matcher<View> parentMatcher;
    private final int childPosition;

    public ChildDescendantAtPositionMatcher(Matcher<View> parentMatcher, int childPosition) {
        super(View.class);
        this.parentMatcher = parentMatcher;
        this.childPosition = childPosition;
    }

    @Override
    public boolean matchesSafely(View view) {
        while (view.getParent() != null) {
            if (parentMatcher.matches(view.getParent())) {
                return view.equals(((ViewGroup) view.getParent()).getChildAt(childPosition));
            }
            view = (View) view.getParent();
        }
        return false;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("with " + childPosition + " child view of type parentMatcher");
    }
}
