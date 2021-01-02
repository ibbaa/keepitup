package de.ibba.keepitup.ui.validation;

import android.content.Context;
import android.content.res.Resources;

import de.ibba.keepitup.R;
import de.ibba.keepitup.logging.Log;
import de.ibba.keepitup.util.StringUtil;

public class FilenameFieldValidator implements FieldValidator {

    private final String field;
    private final Context context;
    private final boolean allowEmpty;

    public FilenameFieldValidator(String field, boolean allowEmpty, Context context) {
        this.field = field;
        this.allowEmpty = allowEmpty;
        this.context = context;
    }

    @Override
    public ValidationResult validate(String value) {
        Log.d(FilenameFieldValidator.class.getName(), "validate, value is " + value);
        String emptyMessage = getResources().getString(R.string.invalid_no_value);
        String failedMessage = getResources().getString(R.string.invalid_file_name);
        String successMessage = getResources().getString(R.string.validation_successful);
        if (StringUtil.isEmpty(value)) {
            Log.d(HostFieldValidator.class.getName(), "No value specified.");
            if (allowEmpty) {
                return new ValidationResult(true, field, successMessage);
            } else {
                return new ValidationResult(false, field, emptyMessage);
            }
        }
        if (value.contains("/")) {
            Log.d(FilenameFieldValidator.class.getName(), "Filename invalid. Validation failed.");
            return new ValidationResult(false, field, failedMessage);
        }
        return new ValidationResult(true, field, successMessage);
    }

    private Context getContext() {
        return context;
    }

    private Resources getResources() {
        return getContext().getResources();
    }
}
