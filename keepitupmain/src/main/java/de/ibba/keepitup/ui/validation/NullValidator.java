package de.ibba.keepitup.ui.validation;

import android.content.Context;
import android.content.res.Resources;

import de.ibba.keepitup.R;
import de.ibba.keepitup.logging.Log;

public class NullValidator implements Validator {

    private final Context context;

    public NullValidator(Context context) {
        this.context = context;
    }

    @Override
    public ValidationResult validateAddress(String address) {
        Log.d(NullValidator.class.getName(), "validateAddress, address is " + address);
        String fieldName = getResources().getString(R.string.task_address_field_name);
        String failedMessage = getResources().getString(R.string.invalid_no_value);
        return new ValidationResult(false, fieldName, failedMessage);
    }

    @Override
    public ValidationResult validatePort(String port) {
        Log.d(NullValidator.class.getName(), "validatePort, port is " + port);
        String fieldName = getResources().getString(R.string.task_port_field_name);
        String failedMessage = getResources().getString(R.string.invalid_no_value);
        return new ValidationResult(false, fieldName, failedMessage);
    }

    @Override
    public ValidationResult validateInterval(String interval) {
        Log.d(NullValidator.class.getName(), "validateInterval, interval is " + interval);
        String fieldName = getResources().getString(R.string.task_interval_field_name);
        String failedMessage = getResources().getString(R.string.invalid_no_value);
        return new ValidationResult(false, fieldName, failedMessage);
    }

    private Context getContext() {
        return context;
    }

    private Resources getResources() {
        return getContext().getResources();
    }
}
