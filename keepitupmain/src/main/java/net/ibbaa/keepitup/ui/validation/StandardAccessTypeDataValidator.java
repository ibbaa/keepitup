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
public class StandardAccessTypeDataValidator implements AccessTypeDataValidator {

    private final Context context;

    public StandardAccessTypeDataValidator(Context context) {
        this.context = context;
    }

    @Override
    public ValidationResult validatePingCount(String pingCount) {
        Log.d(StandardAccessTypeDataValidator.class.getName(), "validatePingCount, pingCount is " + pingCount);
        String fieldName = getResources().getString(R.string.accesstypedata_ping_count_field_name);
        ValidationResult result = new PingCountFieldValidator(fieldName, getContext()).validate(pingCount);
        Log.d(StandardAccessTypeDataValidator.class.getName(), PingCountFieldValidator.class.getSimpleName() + " returned " + result);
        return result;
    }

    @Override
    public ValidationResult validatePingPackageSize(String pingPackageSize) {
        Log.d(StandardAccessTypeDataValidator.class.getName(), "validatePingPackageSize, pingPackageSize is " + pingPackageSize);
        String fieldName = getResources().getString(R.string.accesstypedata_ping_package_size_field_name);
        ValidationResult result = new PingPackageSizeFieldValidator(fieldName, getContext()).validate(pingPackageSize);
        Log.d(StandardAccessTypeDataValidator.class.getName(), PingPackageSizeFieldValidator.class.getSimpleName() + " returned " + result);
        return result;
    }

    @Override
    public ValidationResult validateConnectCount(String connectCount) {
        Log.d(StandardAccessTypeDataValidator.class.getName(), "validateConnectCount, connectCount is " + connectCount);
        String fieldName = getResources().getString(R.string.accesstypedata_connect_count_field_name);
        ValidationResult result = new ConnectCountFieldValidator(fieldName, getContext()).validate(connectCount);
        Log.d(StandardAccessTypeDataValidator.class.getName(), ConnectCountFieldValidator.class.getSimpleName() + " returned " + result);
        return result;
    }

    protected Context getContext() {
        return context;
    }

    protected Resources getResources() {
        return getContext().getResources();
    }
}
