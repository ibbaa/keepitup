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

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Log;

public class PingPackageSizeFieldValidator extends BaseIntegerValidator implements FieldValidator {

    public PingPackageSizeFieldValidator(String field, Context context) {
        super(field, context);
    }

    @Override
    public ValidationResult validate(String value) {
        Log.d(PingPackageSizeFieldValidator.class.getName(), "validate, value is " + value);
        int minimum = getResources().getInteger(R.integer.ping_package_size_minimum);
        int maximum = getResources().getInteger(R.integer.ping_package_size_maximum);
        int defaultValue = getResources().getInteger(R.integer.ping_package_size_default);
        return validateIntNumber(value, defaultValue, minimum, maximum);
    }
}
