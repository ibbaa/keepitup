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

@SuppressWarnings("ClassCanBeRecord")
public class StandardResolveValidator implements ResolveValidator {

    private final Context context;

    public StandardResolveValidator(Context context) {
        this.context = context;
    }

    @Override
    public ValidationResult validateTargetAddress(String address) {
        Log.d(StandardResolveValidator.class.getName(), "validateAddress, address is " + address);
        String fieldName = getResources().getString(R.string.resolve_host_field_name);
        ValidationResult result = new ResolveHostFieldValidator(fieldName, getContext()).validate(address);
        Log.d(StandardResolveValidator.class.getName(), ResolveHostFieldValidator.class.getSimpleName() + " returned " + result);
        return result;
    }

    @Override
    public ValidationResult validateTargetPort(String port) {
        Log.d(StandardResolveValidator.class.getName(), "validatePort, port is " + port);
        String fieldName = getResources().getString(R.string.resolve_port_field_name);
        ValidationResult result = new ResolvePortFieldValidator(fieldName, getContext()).validate(port);
        Log.d(StandardResolveValidator.class.getName(), ResolvePortFieldValidator.class.getSimpleName() + " returned " + result);
        return result;
    }

    protected Context getContext() {
        return context;
    }

    protected Resources getResources() {
        return getContext().getResources();
    }
}
