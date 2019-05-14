package de.ibba.keepitup.ui.validation;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import de.ibba.keepitup.R;
import de.ibba.keepitup.util.StringUtil;
import de.ibba.keepitup.util.URLUtil;

public class HostFieldValidator implements FieldValidator {

    private final String field;
    private final Context context;

    public HostFieldValidator(String field, Context context) {
        this.field = field;
        this.context = context;
    }

    @Override
    public ValidationResult validate(String value) {
        Log.d(HostFieldValidator.class.getName(), "validate, value is " + value);
        String successMessage = getResources().getString(R.string.validation_successful);
        String failedMessage = getResources().getString(R.string.invalid_host_format);
        if (StringUtil.isEmpty(value)) {
            Log.d(StandardHostPortValidator.class.getName(), "No value specified. Validation failed.");
            return new ValidationResult(false, field, failedMessage);
        }
        if (URLUtil.isValidIPAddress(value)) {
            Log.d(StandardHostPortValidator.class.getName(), "Valid IP address. Validation successful.");
            return new ValidationResult(true, field, successMessage);
        }
        if (URLUtil.isValidHostName(value)) {
            Log.d(StandardHostPortValidator.class.getName(), "Valid host name. Validation successful.");
            return new ValidationResult(true, field, successMessage);
        }
        Log.d(StandardHostPortValidator.class.getName(), "Neither IP address nor host name. Validation failed.");
        return new ValidationResult(false, field, failedMessage);
    }

    private Context getContext() {
        return context;
    }

    private Resources getResources() {
        return getContext().getResources();
    }
}
