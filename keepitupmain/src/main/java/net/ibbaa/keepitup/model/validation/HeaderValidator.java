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

package net.ibbaa.keepitup.model.validation;

import android.content.Context;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.model.Header;
import net.ibbaa.keepitup.util.StringUtil;

import java.util.regex.Pattern;

@SuppressWarnings("ClassCanBeRecord")
public class HeaderValidator {

    private static final Pattern HEADER_NAME_PATTERN = Pattern.compile("^[!#$%&'*+\\-.^_`|~0-9A-Za-z]+$");

    private static final Pattern HEADER_VALUE_PATTERN = Pattern.compile("^[\\t\\x20-\\x7E\\x80-\\uFFFF]*$");

    private final Context context;

    public HeaderValidator(Context context) {
        this.context = context;
    }

    public boolean validate(Header header) {
        Log.d(HeaderValidator.class.getName(), "validate header " + header);
        return validateName(header) && validateValue(header);
    }

    public boolean validateName(Header header) {
        Log.d(HeaderValidator.class.getName(), "validateName of header " + header);
        String name = header.getName();
        if (StringUtil.isEmpty(name) || name.trim().isEmpty()) {
            return false;
        }
        name = name.trim();
        int nameMaxLength = context.getResources().getInteger(R.integer.http_header_name_max_length);
        if (name.length() > nameMaxLength) {
            Log.d(HeaderValidator.class.getName(), "name is too long. Returning false.");
            return false;
        }
        if (!HEADER_NAME_PATTERN.matcher(name).matches()) {
            Log.d(HeaderValidator.class.getName(), "name has invalid characters. Returning false.");
            return false;
        }
        return true;
    }

    public boolean validateValue(Header header) {
        Log.d(HeaderValidator.class.getName(), "validateValue of header " + header);
        String value = header.getValue();
        if (value == null) {
            return false;
        }
        int valueMaxLength = context.getResources().getInteger(R.integer.http_header_value_max_length);
        if (value.length() > valueMaxLength) {
            Log.d(HeaderValidator.class.getName(), "value is too long. Returning false.");
            return false;
        }
        if (!HEADER_VALUE_PATTERN.matcher(value).matches()) {
            Log.d(HeaderValidator.class.getName(), "value has invalid characters. Returning false.");
            return false;
        }
        return true;
    }
}
