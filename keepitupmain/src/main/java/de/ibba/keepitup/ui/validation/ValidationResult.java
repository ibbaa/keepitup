package de.ibba.keepitup.ui.validation;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;

public class ValidationResult {

    private final boolean validationSuccessful;
    private final String fieldName;
    private final String message;
    private final boolean modifiedValue;
    private final String value;

    public ValidationResult(boolean validationSuccessful, String fieldName, String message) {
        this(validationSuccessful, fieldName, message, false, null);
    }

    public ValidationResult(boolean validationSuccessful, String fieldName, String message, boolean modifiedValue, String value) {
        this.validationSuccessful = validationSuccessful;
        this.fieldName = fieldName;
        this.message = message;
        this.modifiedValue = modifiedValue;
        this.value = value;
    }

    public ValidationResult(PersistableBundle bundle) {
        this(new Bundle(bundle));
    }

    public ValidationResult(Bundle bundle) {
        this.validationSuccessful = bundle.getInt("validationSuccessful") >= 1;
        this.fieldName = bundle.getString("fieldName");
        this.message = bundle.getString("message");
        this.modifiedValue = bundle.getInt("modifiedValue") >= 1;
        this.value = bundle.getString("value");
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

    public boolean modifiedValue() {
        return modifiedValue;
    }

    public String getValue() {
        return value;
    }

    public PersistableBundle toPersistableBundle() {
        PersistableBundle bundle = new PersistableBundle();
        bundle.putInt("validationSuccessful", validationSuccessful ? 1 : 0);
        bundle.putString("fieldName", fieldName);
        bundle.putString("message", message);
        bundle.putInt("modifiedValue", modifiedValue ? 1 : 0);
        bundle.putString("value", value);
        return bundle;
    }

    public Bundle toBundle() {
        return new Bundle(toPersistableBundle());
    }

    @NonNull
    @Override
    public String toString() {
        return "ValidationResult{" +
                "validationSuccessful=" + validationSuccessful +
                ", fieldName='" + fieldName + '\'' +
                ", message='" + message + '\'' +
                ", modifiedValue=" + modifiedValue +
                ", value='" + value + '\'' +
                '}';
    }
}
