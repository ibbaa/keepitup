package de.ibba.keepitup.ui.validation;

import android.content.Context;
import android.util.Log;

import de.ibba.keepitup.R;

public class IntervalFieldValidator extends BaseIntegerValidator implements FieldValidator {

    public IntervalFieldValidator(String field, Context context) {
        super(field, context);
    }

    @Override
    public ValidationResult validate(String value) {
        Log.d(IntervalFieldValidator.class.getName(), "validate, value is " + value);
        String fieldName = getResources().getString(R.string.task_interval_field_name);
        int minimum = getResources().getInteger(R.integer.task_interval_minimum);
        int maximum = getResources().getInteger(R.integer.task_interval_maximum);
        int defaultValue = getResources().getInteger(R.integer.task_interval_default);
        return validateIntNumber(value, defaultValue, minimum, maximum);
    }
}
