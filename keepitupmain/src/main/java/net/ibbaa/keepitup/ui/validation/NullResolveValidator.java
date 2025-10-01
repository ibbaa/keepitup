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

public class NullResolveValidator implements ResolveValidator {

    private final Context context;

    public NullResolveValidator(Context context) {
        this.context = context;
    }

    @Override
    public ValidationResult validateAddress(String address) {
        Log.d(NullResolveValidator.class.getName(), "validateAddress, address is " + address);
        String fieldName = getResources().getString(R.string.resolve_host_field_name);
        String failedMessage = getResources().getString(R.string.invalid_no_value);
        return new ValidationResult(false, fieldName, failedMessage);
    }

    @Override
    public ValidationResult validatePort(String port) {
        Log.d(NullResolveValidator.class.getName(), "validatePort, port is " + port);
        String fieldName = getResources().getString(R.string.resolve_port_field_name);
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
