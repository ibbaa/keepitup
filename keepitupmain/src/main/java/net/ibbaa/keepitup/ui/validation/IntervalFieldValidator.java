package net.ibbaa.keepitup.ui.validation;

import android.content.Context;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Log;

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
