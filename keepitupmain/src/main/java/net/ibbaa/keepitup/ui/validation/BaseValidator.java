package net.ibbaa.keepitup.ui.validation;

import android.content.Context;
import android.content.res.Resources;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Log;

public abstract class BaseValidator {

    private final Context context;

    public BaseValidator(Context context) {
        this.context = context;
    }

    public ValidationResult validatePort(String port) {
        Log.d(BaseValidator.class.getName(), "validatePort, port is " + port);
        String fieldName = getResources().getString(R.string.task_port_field_name);
        ValidationResult result = new PortFieldValidator(fieldName, getContext()).validate(port);
        Log.d(BaseValidator.class.getName(), PortFieldValidator.class.getSimpleName() + " returned " + result);
        return result;
    }

    public ValidationResult validateInterval(String interval) {
        Log.d(BaseValidator.class.getName(), "validateInterval, interval is " + interval);
        String fieldName = getResources().getString(R.string.task_interval_field_name);
        ValidationResult result = new IntervalFieldValidator(fieldName, getContext()).validate(interval);
        Log.d(BaseValidator.class.getName(), IntervalFieldValidator.class.getSimpleName() + " returned " + result);
        return result;
    }

    protected Context getContext() {
        return context;
    }

    protected Resources getResources() {
        return getContext().getResources();
    }
}
