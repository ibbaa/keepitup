/*
 * Copyright (c) 2023. Alwin Ibba
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

public class NullValidator implements Validator {

    private final Context context;

    public NullValidator(Context context) {
        this.context = context;
    }

    @Override
    public ValidationResult validateAddress(String address) {
        Log.d(NullValidator.class.getName(), "validateAddress, address is " + address);
        String fieldName = getResources().getString(R.string.task_address_field_name);
        String failedMessage = getResources().getString(R.string.invalid_no_value);
        return new ValidationResult(false, fieldName, failedMessage);
    }

    @Override
    public ValidationResult validatePort(String port) {
        Log.d(NullValidator.class.getName(), "validatePort, port is " + port);
        String fieldName = getResources().getString(R.string.task_port_field_name);
        String failedMessage = getResources().getString(R.string.invalid_no_value);
        return new ValidationResult(false, fieldName, failedMessage);
    }

    @Override
    public ValidationResult validateInterval(String interval) {
        Log.d(NullValidator.class.getName(), "validateInterval, interval is " + interval);
        String fieldName = getResources().getString(R.string.task_interval_field_name);
        String failedMessage = getResources().getString(R.string.invalid_no_value);
        return new ValidationResult(false, fieldName, failedMessage);
    }

    private Context getContext() {
        return context;
    }

    private Resources getResources() {
        return getContext().getResources();
    }
}
