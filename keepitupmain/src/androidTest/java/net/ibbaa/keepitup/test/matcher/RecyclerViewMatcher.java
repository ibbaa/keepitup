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
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class RecyclerViewMatcher {
    private final int recyclerViewId;

    public RecyclerViewMatcher(int recyclerViewId) {
        this.recyclerViewId = recyclerViewId;
    }

    public Matcher<View> atPosition(final int position) {
        return atPositionOnView(position, -1);
    }

    public Matcher<View> atPositionOnView(final int position, final int targetViewId) {
        return new TypeSafeMatcher<>() {

            @Override
            public void describeTo(Description description) {
                description.appendText("with id: " + recyclerViewId + " at position: " + position);
            }

            @Override
            public boolean matchesSafely(View view) {
                RecyclerView recyclerView = view.getRootView().findViewById(recyclerViewId);
                if (recyclerView == null || recyclerView.getId() != recyclerViewId) {
                    return false;
                }
                RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(position);
                if (viewHolder == null) {
                    return false;
                }
                if (targetViewId == -1) {
                    return view == viewHolder.itemView;
                } else {
                    View targetView = viewHolder.itemView.findViewById(targetViewId);
                    return view == targetView;
                }
            }
        };
    }
}