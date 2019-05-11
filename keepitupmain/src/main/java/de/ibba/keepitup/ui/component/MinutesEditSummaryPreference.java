package de.ibba.keepitup.ui.component;

import android.content.Context;
import android.support.v7.preference.EditTextPreference;
import android.util.AttributeSet;

import de.ibba.keepitup.R;

public class MinutesEditSummaryPreference extends EditTextPreference {

    public MinutesEditSummaryPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public MinutesEditSummaryPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MinutesEditSummaryPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MinutesEditSummaryPreference(Context context) {
        super(context);
    }

    @Override
    public CharSequence getSummary() {
        return getText() + " " + getContext().getResources().getString(R.string.string_minutes);
    }
}
