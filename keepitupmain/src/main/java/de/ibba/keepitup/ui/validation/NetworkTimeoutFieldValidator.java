package de.ibba.keepitup.ui.validation;

import android.content.Context;
import android.util.Log;

import de.ibba.keepitup.R;

public class NetworkTimeoutFieldValidator extends BaseIntegerValidator implements FieldValidator {

    public NetworkTimeoutFieldValidator(String field, Context context) {
        super(field, context);
    }

    @Override
    public ValidationResult validate(String value) {
        Log.d(NetworkTimeoutFieldValidator.class.getName(), "validate, value is " + value);
        int minimum = getResources().getInteger(R.integer.network_timeout_minimum);
        int maximum = getResources().getInteger(R.integer.network_timeout_maximum);
        int defaultValue = getResources().getInteger(R.integer.network_timeout_default);
        return validateIntNumber(value, defaultValue, minimum, maximum);
    }
}
