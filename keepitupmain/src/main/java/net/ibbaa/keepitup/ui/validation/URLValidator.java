/*
 * Copyright (c) 2021. Alwin Ibba
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

public class URLValidator extends BaseValidator implements Validator {

    public URLValidator(Context context) {
        super(context);
    }

    @Override
    public ValidationResult validateAddress(String address) {
        Log.d(URLValidator.class.getName(), "validateAddress, address is " + address);
        String fieldName = getResources().getString(R.string.task_url_field_name);
        ValidationResult result = new URLFieldValidator(fieldName, getContext()).validate(address);
        Log.d(URLValidator.class.getName(), URLFieldValidator.class.getSimpleName() + " returned " + result);
        return result;
    }
}
