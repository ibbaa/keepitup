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

import androidx.recyclerview.widget.RecyclerView;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class ListSizeMatcher extends TypeSafeMatcher<View> {

    private final int size;

    public ListSizeMatcher(int size) {
        super(View.class);
        this.size = size;
    }

    @Override
    public boolean matchesSafely(final View view) {
        return ((RecyclerView) view).getChildCount() == size;
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("RecyclerView should have " + size + " items");
    }
}
