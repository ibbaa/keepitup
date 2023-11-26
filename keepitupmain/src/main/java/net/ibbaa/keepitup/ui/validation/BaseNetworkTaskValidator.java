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

public abstract class BaseNetworkTaskValidator implements NetworkTaskValidator {

    private final Context context;

    public BaseNetworkTaskValidator(Context context) {
        this.context = context;
    }

    public ValidationResult validatePort(String port) {
        Log.d(BaseNetworkTaskValidator.class.getName(), "validatePort, port is " + port);
        String fieldName = getResources().getString(R.string.task_port_field_name);
        ValidationResult result = new PortFieldValidator(fieldName, getContext()).validate(port);
        Log.d(BaseNetworkTaskValidator.class.getName(), PortFieldValidator.class.getSimpleName() + " returned " + result);
        return result;
    }

    public ValidationResult validateInterval(String interval) {
        Log.d(BaseNetworkTaskValidator.class.getName(), "validateInterval, interval is " + interval);
        String fieldName = getResources().getString(R.string.task_interval_field_name);
        ValidationResult result = new IntervalFieldValidator(fieldName, getContext()).validate(interval);
        Log.d(BaseNetworkTaskValidator.class.getName(), IntervalFieldValidator.class.getSimpleName() + " returned " + result);
        return result;
    }

    protected Context getContext() {
        return context;
    }

    protected Resources getResources() {
        return getContext().getResources();
    }
}
