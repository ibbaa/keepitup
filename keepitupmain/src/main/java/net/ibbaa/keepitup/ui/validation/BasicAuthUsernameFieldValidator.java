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

public class BasicAuthUsernameFieldValidator extends BaseStringValidator implements FieldValidator {

    public BasicAuthUsernameFieldValidator(String field, Context context) {
        super(field, context, false);
    }

    @Override
    public ValidationResult validate(String value) {
        Log.d(BasicAuthUsernameFieldValidator.class.getName(), "validate");
        String successMessage = getResources().getString(R.string.validation_successful);
        String failedMessageInvalidCharacters = getResources().getString(R.string.invalid_characters);
        int maximum = (getResources().getInteger(R.integer.http_header_value_max_length) / 2) - 1;
        ValidationResult result = validateString(value, -1, maximum, true, false);
        if (!result.isValidationSuccessful()) {
            return result;
        }
        if (!StringUtil.isEmpty(value) && value.contains(":")) {
            Log.d(BasicAuthUsernameFieldValidator.class.getName(), "Value has invalid characters. Validation failed.");
            return new ValidationResult(false, getField(), failedMessageInvalidCharacters);
        }
        return new ValidationResult(true, getField(), successMessage);
    }
}
