package de.ibba.keepitup.ui.validation;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import de.ibba.keepitup.R;
import de.ibba.keepitup.util.NumberUtil;

public abstract class BaseValidator {

    private final Context context;

    public BaseValidator(Context context) {
        this.context = context;
    }

    protected ValidationResult validateIntNumber(String value, int defaultValue, int minimum, int maximum, String fieldName) {
        Log.d(BaseValidator.class.getName(), "validateIntNumber for field " + fieldName);
        Log.d(BaseValidator.class.getName(), "value is " + value);
        if (!NumberUtil.isValidLongValue(value)) {
            Log.d(BaseValidator.class.getName(), "invalid number format");
            return new ValidationResult(false, fieldName, getResources().getString(R.string.invalid_number_format));
        }
        long numberValue = NumberUtil.getLongValue(value, defaultValue);
        Log.d(BaseValidator.class.getName(), "validateIntNumber, parsed numeric value is " + numberValue);
        if (numberValue < minimum) {
            Log.d(BaseValidator.class.getName(), "Out of range. Value less than minimum of " + minimum);
            String formattedMessage = String.format(getResources().getString(R.string.invalid_range_minimim), minimum);
            return new ValidationResult(false, fieldName, formattedMessage);
        }
        if (numberValue > maximum) {
            Log.d(BaseValidator.class.getName(), "Out of range. Value greater than maximum of " + maximum);
            String formattedMessage = String.format(getResources().getString(R.string.invalid_range_maximum), maximum);
            return new ValidationResult(false, fieldName, formattedMessage);
        }
        Log.d(BaseValidator.class.getName(), "Validation successful");
        return new ValidationResult(true, fieldName, getResources().getString(R.string.validation_successful));
    }

    public ValidationResult validatePort(String port) {
        Log.d(BaseValidator.class.getName(), "validatePort, port is " + port);
        String fieldName = getResources().getString(R.string.task_port_field_name);
        int minimum = getResources().getInteger(R.integer.task_port_minimum);
        int maximum = getResources().getInteger(R.integer.task_port_maximum);
        int defaultValue = getResources().getInteger(R.integer.task_port_default);
        return validateIntNumber(port, defaultValue, minimum, maximum, fieldName);
    }

    public ValidationResult validateInterval(String interval) {
        Log.d(BaseValidator.class.getName(), "validateInterval, interval is " + interval);
        String fieldName = getResources().getString(R.string.task_interval_field_name);
        int minimum = getResources().getInteger(R.integer.task_interval_minimum);
        int maximum = getResources().getInteger(R.integer.task_interval_maximum);
        int defaultValue = getResources().getInteger(R.integer.task_interval_default);
        return validateIntNumber(interval, defaultValue, minimum, maximum, fieldName);
    }

    protected Context getContext() {
        return context;
    }

    protected Resources getResources() {
        return getContext().getResources();
    }
}
