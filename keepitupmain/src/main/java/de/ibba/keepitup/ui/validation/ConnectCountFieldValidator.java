package de.ibba.keepitup.ui.validation;

import android.content.Context;

import de.ibba.keepitup.R;
import de.ibba.keepitup.logging.Log;

public class ConnectCountFieldValidator extends BaseIntegerValidator implements FieldValidator {

    public ConnectCountFieldValidator(String field, Context context) {
        super(field, context);
    }

    @Override
    public ValidationResult validate(String value) {
        Log.d(ConnectCountFieldValidator.class.getName(), "validate, value is " + value);
        int minimum = getResources().getInteger(R.integer.connect_count_minimum);
        int maximum = getResources().getInteger(R.integer.connect_count_maximum);
        int defaultValue = getResources().getInteger(R.integer.connect_count_default);
        return validateIntNumber(value, defaultValue, minimum, maximum);
    }
}
