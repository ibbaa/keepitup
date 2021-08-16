package net.ibbaa.keepitup.ui.validation;

import android.content.Context;
import android.content.res.Resources;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.util.NumberUtil;
import net.ibbaa.keepitup.util.StringUtil;

public abstract class BaseIntegerValidator {

    private final String field;
    private final Context context;

    public BaseIntegerValidator(String field, Context context) {
        this.field = field;
        this.context = context;
    }

    protected ValidationResult validateIntNumber(String value, int defaultValue, int minimum, int maximum) {
        Log.d(BaseIntegerValidator.class.getName(), "validateIntNumber for field " + field);
        Log.d(BaseIntegerValidator.class.getName(), "value is " + value);
        if (StringUtil.isEmpty(value)) {
            Log.d(BaseIntegerValidator.class.getName(), "No value specified. Validation failed.");
            return new ValidationResult(false, field, getResources().getString(R.string.invalid_no_value));
        }
        if (!NumberUtil.isValidLongValue(value)) {
            Log.d(BaseIntegerValidator.class.getName(), "invalid number format");
            return new ValidationResult(false, field, getResources().getString(R.string.invalid_number_format));
        }
        long numberValue = NumberUtil.getLongValue(value, defaultValue);
        Log.d(BaseIntegerValidator.class.getName(), "validateIntNumber, parsed numeric value is " + numberValue);
        if (numberValue < minimum) {
            Log.d(BaseIntegerValidator.class.getName(), "Out of range. Value less than minimum of " + minimum);
            String formattedMessage = String.format(getResources().getString(R.string.invalid_range_minimim), minimum);
            return new ValidationResult(false, field, formattedMessage);
        }
        if (numberValue > maximum) {
            Log.d(BaseIntegerValidator.class.getName(), "Out of range. Value greater than maximum of " + maximum);
            String formattedMessage = String.format(getResources().getString(R.string.invalid_range_maximum), maximum);
            return new ValidationResult(false, field, formattedMessage);
        }
        Log.d(BaseIntegerValidator.class.getName(), "Validation successful");
        return new ValidationResult(true, field, getResources().getString(R.string.validation_successful));
    }

    protected String getField() {
        return field;
    }

    protected Context getContext() {
        return context;
    }

    protected Resources getResources() {
        return getContext().getResources();
    }
}
