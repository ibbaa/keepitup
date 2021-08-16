package net.ibbaa.keepitup.ui.validation;

import android.content.Context;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Log;

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
