/*
 * Copyright (c) 2026 Alwin Ibba
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.ibbaa.keepitup.ui.validation;

import android.content.Context;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.util.StringUtil;

public class PasswordConfirmFieldValidator implements FieldValidator {

    private final String field;
    private final Context context;
    private final String password;

    public PasswordConfirmFieldValidator(String field, String password, Context context) {
        this.field = field;
        this.context = context;
        this.password = password;
    }

    @Override
    public ValidationResult validate(String value) {
        Log.d(PasswordConfirmFieldValidator.class.getName(), "validate");
        String successMessage = context.getResources().getString(R.string.validation_successful);
        String failedMessage = context.getResources().getString(R.string.invalid_no_match);
        String actualPassword = StringUtil.normalizeString(password);
        String confirmPassword = StringUtil.normalizeString(value);
        if (actualPassword.equals(confirmPassword)) {
            return new ValidationResult(true, field, successMessage);
        }
        return new ValidationResult(false, field, failedMessage);
    }
}
