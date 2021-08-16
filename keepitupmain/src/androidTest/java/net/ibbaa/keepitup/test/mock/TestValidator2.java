package net.ibbaa.keepitup.test.mock;

import android.content.Context;

import net.ibbaa.keepitup.ui.validation.FieldValidator;
import net.ibbaa.keepitup.ui.validation.ValidationResult;

public class TestValidator2 implements FieldValidator {

    private final String field;

    public TestValidator2(String field, Context context) {
        this.field = field;
    }

    @Override
    public ValidationResult validate(String value) {
        if ("success".equals(value)) {
            return new ValidationResult(true, field, "testsuccess2");
        }
        return new ValidationResult(false, field, "testfailed2");
    }
}
