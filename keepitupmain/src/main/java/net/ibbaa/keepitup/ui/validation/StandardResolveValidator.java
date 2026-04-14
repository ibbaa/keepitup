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
import net.ibbaa.keepitup.model.Resolve;

import java.net.URL;
import java.util.List;

public class StandardResolveValidator implements ResolveValidator {

    private final Context context;

    public StandardResolveValidator(Context context) {
        this.context = context;
    }

    @Override
    public ValidationResult validateSourceExists(List<Resolve> resolves, URL url, String value) {
        Log.d(StandardResolveValidator.class.getName(), "validateSourceExists, url is " + url + ", value is " + value);
        String fieldName = getResources().getString(R.string.resolve_match_host_port_field_name);
        ValidationResult result = new ResolveHostMatchExistsFieldValidator(fieldName, resolves, url, getContext()).validate(value);
        Log.d(StandardHeaderValidator.class.getName(), ResolveHostMatchExistsFieldValidator.class.getSimpleName() + " returned " + result);
        return result;
    }

    @Override
    public ValidationResult validateValueSet(Resolve resolve) {
        Log.d(StandardResolveValidator.class.getName(), "validateValueSet, resolve object is " + resolve);
        String fieldName = getResources().getString(R.string.resolve_all_fields);
        ValidationResult result;
        if (resolve.isEmpty()) {
            String failedMessage = getResources().getString(R.string.invalid_must_exist);
            result = new ValidationResult(false, fieldName, failedMessage);
        } else {
            String successMessage = getResources().getString(R.string.validation_successful);
            result = new ValidationResult(true, fieldName, successMessage);
        }
        Log.d(StandardResolveValidator.class.getName(), "validateValueSet returned " + result);
        return result;
    }

    @Override
    public ValidationResult validateSourceAddress(String address) {
        Log.d(StandardResolveValidator.class.getName(), "validateSourceAddress, address is " + address);
        String fieldName = getResources().getString(R.string.resolve_match_host_field_name);
        ValidationResult result = new ResolveHostMatchFieldValidator(fieldName, getContext()).validate(address);
        Log.d(StandardResolveValidator.class.getName(), ResolveHostMatchFieldValidator.class.getSimpleName() + " returned " + result);
        return result;
    }

    @Override
    public ValidationResult validateSourcePort(String port) {
        Log.d(StandardResolveValidator.class.getName(), "validateSourcePort, port is " + port);
        String fieldName = getResources().getString(R.string.resolve_match_port_field_name);
        ValidationResult result = new ResolvePortMatchFieldValidator(fieldName, getContext()).validate(port);
        Log.d(StandardResolveValidator.class.getName(), ResolvePortMatchFieldValidator.class.getSimpleName() + " returned " + result);
        return result;
    }

    @Override
    public ValidationResult validateTargetAddress(String address) {
        Log.d(StandardResolveValidator.class.getName(), "validateTargetAddress, address is " + address);
        String fieldName = getResources().getString(R.string.resolve_host_field_name);
        ValidationResult result = new ResolveHostFieldValidator(fieldName, getContext()).validate(address);
        Log.d(StandardResolveValidator.class.getName(), ResolveHostFieldValidator.class.getSimpleName() + " returned " + result);
        return result;
    }

    @Override
    public ValidationResult validateTargetPort(String port) {
        Log.d(StandardResolveValidator.class.getName(), "validateTargetPort, port is " + port);
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
