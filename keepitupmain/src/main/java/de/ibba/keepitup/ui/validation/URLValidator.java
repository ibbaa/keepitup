package de.ibba.keepitup.ui.validation;

import android.content.Context;
import android.util.Log;

import de.ibba.keepitup.R;
import de.ibba.keepitup.util.StringUtil;
import de.ibba.keepitup.util.URLUtil;

public class URLValidator extends BaseValidator implements Validator {

    public URLValidator(Context context) {
        super(context);
    }

    @Override
    public ValidationResult validateAddress(String address) {
        Log.d(URLValidator.class.getName(), "validateAddress, address is " + address);
        String fieldName = getResources().getString(R.string.task_url_field_name);
        String successMessage = getResources().getString(R.string.validation_successful);
        String failedMessage = getResources().getString(R.string.invalid_url_format);
        if (StringUtil.isEmpty(address)) {
            Log.d(URLValidator.class.getName(), "No value specified. Validation failed.");
            return new ValidationResult(false, fieldName, failedMessage);
        }
        Log.d(URLValidator.class.getName(), "Encoding and modifying URL.");
        String prefixedURL = URLUtil.prefixHTTPProtocol(address);
        String encodedURL = URLUtil.encodeURL(prefixedURL);
        Log.d(URLValidator.class.getName(), "Modified URL is " + encodedURL);
        if (URLUtil.isValidURL(encodedURL)) {
            Log.d(URLValidator.class.getName(), "Valid URL. Validation successful.");
            return new ValidationResult(true, fieldName, successMessage);
        }
        Log.d(URLValidator.class.getName(), "Invalid URL. Validation failed.");
        return new ValidationResult(false, fieldName, failedMessage);
    }
}
