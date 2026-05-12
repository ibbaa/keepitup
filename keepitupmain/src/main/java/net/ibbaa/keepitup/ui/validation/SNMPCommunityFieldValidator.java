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
import net.ibbaa.keepitup.util.SNMPUtil;

public class SNMPCommunityFieldValidator extends BaseStringValidator implements FieldValidator {

    public SNMPCommunityFieldValidator(String field, Context context) {
        super(field, context);
    }

    @Override
    public ValidationResult validate(String value) {
        Log.d(SNMPCommunityFieldValidator.class.getName(), "validate, value is " + value);
        int maximum = getResources().getInteger(R.integer.snmp_community_max_length);
        ValidationResult result = validateString(value, -1, maximum, true, true);
        if (!result.isValidationSuccessful()) {
            return result;
        }
        if (value != null && !value.trim().isEmpty() && !SNMPUtil.validateCommunity(value.trim())) {
            Log.d(SNMPCommunityFieldValidator.class.getName(), "Value has invalid characters. Validation failed.");
            return new ValidationResult(false, getField(), getResources().getString(R.string.invalid_characters));
        }
        return result;
    }
}
