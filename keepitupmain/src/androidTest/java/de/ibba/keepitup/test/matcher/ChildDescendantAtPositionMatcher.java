package de.ibba.keepitup.test.matcher;

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
