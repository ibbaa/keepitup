package de.ibba.keepitup.ui.validation;

import android.content.Context;
import android.util.Log;

import de.ibba.keepitup.R;

public class ConnectionTimeoutFieldValidator extends BaseIntegerValidator implements FieldValidator {

    public ConnectionTimeoutFieldValidator(String field, Context context) {
        super(field, context);
    }

    @Override
    public ValidationResult validate(String value) {
        Log.d(ConnectionTimeoutFieldValidator.class.getName(), "validate, value is " + value);
        int minimum = getResources().getInteger(R.integer.socket_connection_timeout_minimum);
        int maximum = getResources().getInteger(R.integer.socket_connection_timeout_maximum);
        int defaultValue = getResources().getInteger(R.integer.socket_connection_timeout_default);
        return validateIntNumber(value, defaultValue, minimum, maximum);
    }
}
