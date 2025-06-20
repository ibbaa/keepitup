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
import net.ibbaa.keepitup.util.StringUtil;
import net.ibbaa.keepitup.util.URLUtil;

public class URLFieldValidator implements FieldValidator {

    private final String field;
    private final Context context;

    public URLFieldValidator(String field, Context context) {
        this.field = field;
        this.context = context;
    }

    @Override
    public ValidationResult validate(String value) {
        Log.d(URLFieldValidator.class.getName(), "validate, value is " + value);
        String successMessage = getResources().getString(R.string.validation_successful);
        String failedMessage = getResources().getString(R.string.invalid_url_format);
        String failedMessageNoValue = getResources().getString(R.string.invalid_no_value);
        if (StringUtil.isEmpty(value)) {
            Log.d(URLFieldValidator.class.getName(), "No value specified. Validation failed.");
            return new ValidationResult(false, field, failedMessageNoValue);
        }
        Log.d(URLFieldValidator.class.getName(), "Encoding and modifying URL.");
        if (URLUtil.isValidURL(value)) {
            Log.d(URLFieldValidator.class.getName(), "Valid URL. Validation successful.");
            return new ValidationResult(true, field, successMessage);
        }
        Log.d(URLFieldValidator.class.getName(), "Invalid URL. Validation failed.");
        return new ValidationResult(false, field, failedMessage);
    }

    private Context getContext() {
        return context;
    }

    private Resources getResources() {
        return getContext().getResources();
    }
}
