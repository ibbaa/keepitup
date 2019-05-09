package de.ibba.keepitup.ui.validation;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import de.ibba.keepitup.R;
import de.ibba.keepitup.util.NumberUtil;
import de.ibba.keepitup.util.StringUtil;
import de.ibba.keepitup.util.URLUtil;

public class StandardHostPortValidator implements Validator {

    private final Context context;

    public StandardHostPortValidator(Context context) {
        this.context = context;
    }

    @Override
    public ValidationResult validateAddress(String address) {
        Log.d(StandardHostPortValidator.class.getName(), "validateAddress, address is " + address);
        String fieldName = getResources().getString(R.string.task_host_field_name);
        String successMessage = getResources().getString(R.string.validation_successful);
        String failedMessage = getResources().getString(R.string.invalid_host_format);
        if (StringUtil.isEmpty(address)) {
            Log.d(StandardHostPortValidator.class.getName(), "No value specified. Validation failed.");
            return new ValidationResult(false, fieldName, failedMessage, false, address);
        }
        if (URLUtil.isValidIPAddress(address)) {
            Log.d(StandardHostPortValidator.class.getName(), "Valid IP address. Validation successful.");
            return new ValidationResult(true, fieldName, successMessage, false, address);
        }
        if (URLUtil.isValidHostName(address)) {
            Log.d(StandardHostPortValidator.class.getName(), "Valid host name. Validation successful.");
            return new ValidationResult(true, fieldName, successMessage, false, address);
        }
        Log.d(StandardHostPortValidator.class.getName(), "Neither IP address nor host name. Validation failed.");
        return new ValidationResult(false, fieldName, failedMessage, false, address);
    }

    @Override
    public ValidationResult validatePort(String port) {
        Log.d(StandardHostPortValidator.class.getName(), "validatePort, port is " + port);
        String fieldName = getResources().getString(R.string.task_port_field_name);
        int minimum = getResources().getInteger(R.integer.task_port_minimum);
        int maximum = getResources().getInteger(R.integer.task_port_maximum);
        int defaultValue = getResources().getInteger(R.integer.task_port_default);
        return validateIntNumber(port, defaultValue, minimum, maximum, fieldName);
    }

    @Override
    public ValidationResult validateInterval(String interval) {
        Log.d(StandardHostPortValidator.class.getName(), "validateInterval, interval is " + interval);
        String fieldName = getResources().getString(R.string.task_interval_field_name);
        int minimum = getResources().getInteger(R.integer.task_interval_minimum);
        int maximum = getResources().getInteger(R.integer.task_interval_maximum);
        int defaultValue = getResources().getInteger(R.integer.task_interval_default);
        return validateIntNumber(interval, defaultValue, minimum, maximum, fieldName);
    }

    private ValidationResult validateIntNumber(String value, int defaultValue, int minimum, int maximum, String fieldName) {
        Log.d(StandardHostPortValidator.class.getName(), "validateIntNumber for field " + fieldName);
        Log.d(StandardHostPortValidator.class.getName(), "value is " + value);
        if (!NumberUtil.isValidLongValue(value)) {
            Log.d(StandardHostPortValidator.class.getName(), "invalid number format");
            return new ValidationResult(false, fieldName, getResources().getString(R.string.invalid_number_format), false, value);
        }
        long numberValue = NumberUtil.getLongValue(value, defaultValue);
        Log.d(StandardHostPortValidator.class.getName(), "validateIntNumber, parsed numeric value is " + numberValue);
        if (numberValue < minimum) {
            Log.d(StandardHostPortValidator.class.getName(), "Out of range. Value less than minimum of " + minimum);
            String formattedMessage = String.format(getResources().getString(R.string.invalid_range_minimim), minimum);
            return new ValidationResult(false, fieldName, formattedMessage, false, value);
        }
        if (numberValue > maximum) {
            Log.d(StandardHostPortValidator.class.getName(), "Out of range. Value greater than maximum of " + maximum);
            String formattedMessage = String.format(getResources().getString(R.string.invalid_range_maximum), maximum);
            return new ValidationResult(false, fieldName, formattedMessage, false, value);
        }
        Log.d(StandardHostPortValidator.class.getName(), "Validation successful");
        return new ValidationResult(true, fieldName, getResources().getString(R.string.validation_successful), false, value);
    }

    private Context getContext() {
        return context;
    }

    private Resources getResources() {
        return getContext().getResources();
    }
}
