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

public class BasicAuthPasswordFieldValidator extends BaseStringValidator implements FieldValidator {

    public BasicAuthPasswordFieldValidator(String field, Context context) {
        super(field, context, true);
    }

    @Override
    public ValidationResult validate(String value) {
        Log.d(BasicAuthPasswordFieldValidator.class.getName(), "validate");
        int maximum = (getResources().getInteger(R.integer.http_header_value_max_length) / 2) - 1;
        return validateString(value, -1, maximum, false, false);
    }
}
