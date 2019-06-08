package de.ibba.keepitup.test.mock;

import android.content.Context;

import de.ibba.keepitup.ui.validation.FieldValidator;
import de.ibba.keepitup.ui.validation.ValidationResult;

public class TestValidator implements FieldValidator {

    private final String field;

    public TestValidator(String field, Context context) {
        this.field = field;
    }

    @Override
    public ValidationResult validate(String value) {
        if ("success".equals(value)) {
            return new ValidationResult(true, field, "testsuccess");
        }
        return new ValidationResult(false, field, "testfailed");
    }
}
