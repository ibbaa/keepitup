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

public abstract class BaseHostValidator {

    private final String field;
    private final Context context;

    public BaseHostValidator(String field, Context context) {
        this.field = field;
        this.context = context;
    }

    public ValidationResult validateHost(String value, boolean emptyIsValid) {
        Log.d(BaseHostValidator.class.getName(), "validateHost, value is " + value);
        String successMessage = getResources().getString(R.string.validation_successful);
        String failedMessage = getResources().getString(R.string.invalid_host_format);
        String failedMessageNoValue = getResources().getString(R.string.invalid_no_value);
        if (StringUtil.isEmpty(value)) {
            if (emptyIsValid) {
                Log.d(BaseHostValidator.class.getName(), "No value specified. Validation successful.");
                return new ValidationResult(true, field, successMessage);
            } else {
                Log.d(BaseHostValidator.class.getName(), "No value specified. Validation failed.");
                return new ValidationResult(false, field, failedMessageNoValue);
            }
        }
        if (URLUtil.isValidIPAddress(value)) {
            Log.d(BaseHostValidator.class.getName(), "Valid IP address. Validation successful.");
            return new ValidationResult(true, field, successMessage);
        }
        if (URLUtil.isValidHostName(value)) {
            Log.d(BaseHostValidator.class.getName(), "Valid host name. Validation successful.");
            return new ValidationResult(true, field, successMessage);
        }
        Log.d(BaseHostValidator.class.getName(), "Neither IP address nor host name. Validation failed.");
        return new ValidationResult(false, field, failedMessage);
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
