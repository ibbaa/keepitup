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
import android.widget.GridLayout;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class GridLayoutPositionMatcher extends TypeSafeMatcher<View> {

    private final int row;
    private final int rowSpan;
    private final GridLayout.Alignment rowAlignment;
    private final int column;
    private final int columnSpan;
    private final GridLayout.Alignment columnAlignment;

    public GridLayoutPositionMatcher(int row, int rowSpan, GridLayout.Alignment rowAlignment, int column, int columnSpan, GridLayout.Alignment columnAlignment) {
        super(View.class);
        this.row = row;
        this.rowSpan = rowSpan;
        this.rowAlignment = rowAlignment;
        this.column = column;
        this.columnSpan = columnSpan;
        this.columnAlignment = columnAlignment;
    }

    @Override
    public boolean matchesSafely(View view) {
        if (view.getLayoutParams() != null && view.getLayoutParams() instanceof GridLayout.LayoutParams layoutParams) {
            GridLayout.Spec rowSpec = GridLayout.spec(row, rowSpan, rowAlignment);
            GridLayout.Spec columnSpec = GridLayout.spec(column, columnSpan, columnAlignment);
            return rowSpec.equals(layoutParams.rowSpec) && columnSpec.equals(layoutParams.columnSpec);
        }
        return false;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("with row " + row + " and row span " + rowSpan + " and column " + column + " and column span " + columnSpan);
    }
}
