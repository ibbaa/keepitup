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
import android.content.res.Resources;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Log;

import java.util.List;

public class StandardHeaderValidator implements HeaderValidator {

    private final Context context;

    public StandardHeaderValidator(Context context) {
        this.context = context;
    }

    @Override
    public ValidationResult validateName(String name) {
        Log.d(StandardHeaderValidator.class.getName(), "validateName, name is " + name);
        String fieldName = getResources().getString(R.string.header_name_field_name);
        ValidationResult result = new HeaderNameFieldValidator(fieldName, getContext()).validate(name);
        Log.d(StandardHeaderValidator.class.getName(), HeaderNameFieldValidator.class.getSimpleName() + " returned " + result);
        return result;
    }

    @Override
    public ValidationResult validateNameExists(List<String> names, String name) {
        Log.d(StandardHeaderValidator.class.getName(), "validateNameExists, name is " + name);
        String fieldName = getResources().getString(R.string.header_name_field_name);
        ValidationResult result = new HeaderNameExistsFieldValidator(fieldName, names, getContext()).validate(name);
        Log.d(StandardHeaderValidator.class.getName(), HeaderNameExistsFieldValidator.class.getSimpleName() + " returned " + result);
        return result;
    }

    @Override
    public ValidationResult validateValue(String value) {
        Log.d(StandardHeaderValidator.class.getName(), "validateValue");
        String fieldName = getResources().getString(R.string.header_value_field_name);
        ValidationResult result = new HeaderValueFieldValidator(fieldName, getContext()).validate(value);
        Log.d(StandardHeaderValidator.class.getName(), HeaderValueFieldValidator.class.getSimpleName() + " returned " + result);
        return result;
    }

    protected Context getContext() {
        return context;
    }

    protected Resources getResources() {
        return getContext().getResources();
    }
}
