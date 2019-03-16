package de.ibba.keepitup.test.matcher;

import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import org.hamcrest.Description;

public class TextColorMatcher extends BoundedMatcher<View, TextView> {

    private final int expectedId;

    public TextColorMatcher(int expectedId) {
        super(TextView.class);
        this.expectedId = expectedId;
    }

    @Override
    protected boolean matchesSafely(TextView textView) {
        int colorId = ContextCompat.getColor(textView.getContext(), expectedId);
        return textView.getCurrentTextColor() == colorId;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("with text color: ");
        description.appendValue(expectedId);
    }
}
