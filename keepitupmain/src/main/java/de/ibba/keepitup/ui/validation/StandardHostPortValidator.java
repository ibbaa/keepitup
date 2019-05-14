package de.ibba.keepitup.ui.validation;

import android.content.Context;
import android.util.Log;

import de.ibba.keepitup.R;

public class StandardHostPortValidator extends BaseValidator implements Validator {

    public StandardHostPortValidator(Context context) {
        super(context);
    }

    @Override
    public ValidationResult validateAddress(String address) {
        Log.d(StandardHostPortValidator.class.getName(), "validateAddress, address is " + address);
        String fieldName = getResources().getString(R.string.task_host_field_name);
        ValidationResult result = new HostFieldValidator(fieldName, getContext()).validate(address);
        Log.d(StandardHostPortValidator.class.getName(), HostFieldValidator.class.getSimpleName() + " returned " + result);
        return result;
    }
}
