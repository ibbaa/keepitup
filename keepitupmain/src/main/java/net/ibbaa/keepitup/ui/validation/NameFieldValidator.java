/*
 * Copyright (c) 2025 Alwin Ibba
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
import android.content.res.Resources;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.model.validation.NetworkTaskValidator;
import net.ibbaa.keepitup.util.StringUtil;

public class NameFieldValidator implements FieldValidator {

    private final String field;
    private final Context context;

    public NameFieldValidator(String field, Context context) {
        this.field = field;
        this.context = context;
    }

    @Override
    public ValidationResult validate(String value) {
        Log.d(NameFieldValidator.class.getName(), "validate, value is " + value);
        String successMessage = getResources().getString(R.string.validation_successful);
        if (!StringUtil.isEmpty(value)) {
            int nameMaxLength = getResources().getInteger(R.integer.task_name_max_length);
            if (value.length() > nameMaxLength) {
                Log.d(NetworkTaskValidator.class.getName(), "Invalid name. Name too long. Validation failed.");
                String formattedFailedMessage = getResources().getString(R.string.invalid_length_maximum, nameMaxLength);
                return new ValidationResult(false, field, formattedFailedMessage);
            }
        }
        Log.d(NameFieldValidator.class.getName(), "Valid name. Validation successful.");
        return new ValidationResult(true, field, successMessage);
    }

    private Context getContext() {
        return context;
    }

    private Resources getResources() {
        return getContext().getResources();
    }
}
