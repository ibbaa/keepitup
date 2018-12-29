package de.ibba.keepitup.ui.component;

import android.content.Context;
import android.preference.EditTextPreference;
import android.util.AttributeSet;

public class EditSummaryPreference extends EditTextPreference {

    public EditSummaryPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public EditSummaryPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public EditSummaryPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EditSummaryPreference(Context context) {
        super(context);
    }

    @Override
    public CharSequence getSummary() {
        return getText();
    }
}