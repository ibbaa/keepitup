package net.ibbaa.keepitup.ui.validation;

import android.content.Context;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Log;

public class URLValidator extends BaseValidator implements Validator {

    public URLValidator(Context context) {
        super(context);
    }

    @Override
    public ValidationResult validateAddress(String address) {
        Log.d(URLValidator.class.getName(), "validateAddress, address is " + address);
        String fieldName = getResources().getString(R.string.task_url_field_name);
        ValidationResult result = new URLFieldValidator(fieldName, getContext()).validate(address);
        Log.d(URLValidator.class.getName(), URLFieldValidator.class.getSimpleName() + " returned " + result);
        return result;
    }
}
