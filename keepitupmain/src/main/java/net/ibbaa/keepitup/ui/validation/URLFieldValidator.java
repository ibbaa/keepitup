package net.ibbaa.keepitup.ui.validation;

import android.content.Context;
import android.content.res.Resources;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.util.StringUtil;
import net.ibbaa.keepitup.util.URLUtil;

public class URLFieldValidator implements FieldValidator {

    private final String field;
    private final Context context;

    public URLFieldValidator(String field, Context context) {
        this.field = field;
        this.context = context;
    }

    @Override
    public ValidationResult validate(String value) {
        Log.d(URLFieldValidator.class.getName(), "validate, value is " + value);
        String successMessage = getResources().getString(R.string.validation_successful);
        String failedMessage = getResources().getString(R.string.invalid_url_format);
        String failedMessageNoValue = getResources().getString(R.string.invalid_no_value);
        if (StringUtil.isEmpty(value)) {
            Log.d(URLFieldValidator.class.getName(), "No value specified. Validation failed.");
            return new ValidationResult(false, field, failedMessageNoValue);
        }
        Log.d(URLFieldValidator.class.getName(), "Encoding and modifying URL.");
        String encodedURL = URLUtil.encodeURL(value);
        Log.d(URLFieldValidator.class.getName(), "Modified URL is " + encodedURL);
        if (URLUtil.isValidURL(encodedURL)) {
            Log.d(URLFieldValidator.class.getName(), "Valid URL. Validation successful.");
            return new ValidationResult(true, field, successMessage);
        }
        Log.d(URLFieldValidator.class.getName(), "Invalid URL. Validation failed.");
        return new ValidationResult(false, field, failedMessage);
    }

    private Context getContext() {
        return context;
    }

    private Resources getResources() {
        return getContext().getResources();
    }
}
