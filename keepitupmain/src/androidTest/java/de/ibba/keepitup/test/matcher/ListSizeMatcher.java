package de.ibba.keepitup.test.matcher;

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
