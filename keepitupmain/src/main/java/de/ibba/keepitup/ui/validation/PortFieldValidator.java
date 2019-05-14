package de.ibba.keepitup.ui.validation;

import android.content.Context;
import android.util.Log;

import de.ibba.keepitup.R;

public class PortFieldValidator extends BaseIntegerValidator implements FieldValidator {

    public PortFieldValidator(String field, Context context) {
        super(field, context);
    }

    @Override
    public ValidationResult validate(String value) {
        Log.d(PortFieldValidator.class.getName(), "validate, value is " + value);
        int minimum = getResources().getInteger(R.integer.task_port_minimum);
        int maximum = getResources().getInteger(R.integer.task_port_maximum);
        int defaultValue = getResources().getInteger(R.integer.task_port_default);
        return validateIntNumber(value, defaultValue, minimum, maximum);
    }
}
