package de.ibba.keepitup.ui.validation;

public class ValidationResult {

    private boolean validationSuccessful;
    private String fieldName;
    private String message;

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
}
