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

public abstract class BaseStringValidator {

    private final String field;
    private final Context context;

    public BaseStringValidator(String field, Context context) {
        this.field = field;
        this.context = context;
    }

    protected ValidationResult validateString(String value, int maximum) {
        Log.d(BaseIntegerValidator.class.getName(), "validateString for field " + field);
        Log.d(BaseIntegerValidator.class.getName(), "value is " + value);
        if (!StringUtil.isEmpty(value)) {
            if (value.length() > maximum) {
                Log.d(NetworkTaskValidator.class.getName(), "Value too long. Validation failed.");
                String formattedFailedMessage = getResources().getString(R.string.invalid_length_maximum, maximum);
                return new ValidationResult(false, field, formattedFailedMessage);
            }
        }
        Log.d(BaseIntegerValidator.class.getName(), "Validation successful");
        return new ValidationResult(true, field, getResources().getString(R.string.validation_successful));
    }

    @SuppressWarnings({"unused"})
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
