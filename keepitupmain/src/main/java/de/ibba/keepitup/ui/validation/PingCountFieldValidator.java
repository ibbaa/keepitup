package de.ibba.keepitup.ui.validation;

import android.content.Context;

import de.ibba.keepitup.R;
import de.ibba.keepitup.logging.Log;

public class PingCountFieldValidator extends BaseIntegerValidator implements FieldValidator {

    public PingCountFieldValidator(String field, Context context) {
        super(field, context);
    }

    @Override
    public ValidationResult validate(String value) {
        Log.d(PingCountFieldValidator.class.getName(), "validate, value is " + value);
        int minimum = getResources().getInteger(R.integer.ping_count_minimum);
        int maximum = getResources().getInteger(R.integer.ping_count_maximum);
        int defaultValue = getResources().getInteger(R.integer.ping_count_default);
        return validateIntNumber(value, defaultValue, minimum, maximum);
    }
}
