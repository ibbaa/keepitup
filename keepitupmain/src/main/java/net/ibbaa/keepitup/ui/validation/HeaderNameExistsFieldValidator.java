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

import java.util.List;

@SuppressWarnings("ClassCanBeRecord")
public class HeaderNameExistsFieldValidator implements FieldValidator {

    private final String field;
    private final Context context;
    private final List<String> names;

    public HeaderNameExistsFieldValidator(String field, List<String> names, Context context) {
        this.field = field;
        this.names = names;
        this.context = context;
    }

    @Override
    public ValidationResult validate(String value) {
        Log.d(HeaderNameExistsFieldValidator.class.getName(), "validate, value is " + value);
        String successMessage = getResources().getString(R.string.validation_successful);
        String failedMessage = getResources().getString(R.string.invalid_exists);
        value = StringUtil.notNull(value).trim();
        for (String currentName : names) {
            currentName = StringUtil.notNull(currentName).trim();
            if (currentName.equals(value)) {
                Log.d(HeaderNameExistsFieldValidator.class.getName(), "validate, name exists");
                return new ValidationResult(false, field, failedMessage);
            }
        }
        return new ValidationResult(true, field, successMessage);
    }

    private Context getContext() {
        return context;
    }

    private Resources getResources() {
        return getContext().getResources();
    }
}
