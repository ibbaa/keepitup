package de.ibba.keepitup.test.matcher;

import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.test.espresso.matcher.BoundedMatcher;

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
