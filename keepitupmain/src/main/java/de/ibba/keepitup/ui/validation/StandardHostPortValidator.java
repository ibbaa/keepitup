package de.ibba.keepitup.ui.validation;

import android.content.Context;
import android.util.Log;

import de.ibba.keepitup.R;
import de.ibba.keepitup.util.StringUtil;
import de.ibba.keepitup.util.URLUtil;

public class StandardHostPortValidator extends BaseValidator implements Validator {

    public StandardHostPortValidator(Context context) {
        super(context);
    }

    @Override
    public ValidationResult validateAddress(String address) {
        Log.d(StandardHostPortValidator.class.getName(), "validateAddress, address is " + address);
        String fieldName = getResources().getString(R.string.task_host_field_name);
        String successMessage = getResources().getString(R.string.validation_successful);
        String failedMessage = getResources().getString(R.string.invalid_host_format);
        if (StringUtil.isEmpty(address)) {
            Log.d(StandardHostPortValidator.class.getName(), "No value specified. Validation failed.");
            return new ValidationResult(false, fieldName, failedMessage);
        }
        if (URLUtil.isValidIPAddress(address)) {
            Log.d(StandardHostPortValidator.class.getName(), "Valid IP address. Validation successful.");
            return new ValidationResult(true, fieldName, successMessage);
        }
        if (URLUtil.isValidHostName(address)) {
            Log.d(StandardHostPortValidator.class.getName(), "Valid host name. Validation successful.");
            return new ValidationResult(true, fieldName, successMessage);
        }
        Log.d(StandardHostPortValidator.class.getName(), "Neither IP address nor host name. Validation failed.");
        return new ValidationResult(false, fieldName, failedMessage);
    }
}
