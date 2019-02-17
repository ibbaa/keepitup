package de.ibba.keepitup.ui.validation;

import android.support.annotation.NonNull;

public class ValidationResult {

    private final boolean validationSuccessful;
    private final String fieldName;
    private final String message;

    public ValidationResult(boolean validationSuccessful, String fieldName, String message) {
        this.validationSuccessful = validationSuccessful;
        this.fieldName = fieldName;
        this.message = message;
    }

    public boolean isValidationSuccessful() {
        return validationSuccessful;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getMessage() {
        return message;
    }

    @NonNull
    @Override
    public String toString() {
        return "ValidationResult{" +
                "validationSuccessful=" + validationSuccessful +
                ", fieldName='" + fieldName + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
