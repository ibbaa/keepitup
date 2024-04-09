/*
 * Copyright (c) 2024. Alwin Ibba
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
import net.ibbaa.keepitup.model.AccessTypeData;

public class AccessTypeDataValidator {

    private final Context context;

    public AccessTypeDataValidator(Context context) {
        this.context = context;
    }

    public boolean validate(AccessTypeData accessTypeData) {
        Log.d(AccessTypeDataValidator.class.getName(), "validate accessTypeData " + accessTypeData);
        return validatePingCount(accessTypeData) && validatePingPackageSize(accessTypeData) && validateConnectCount(accessTypeData);
    }

    public boolean validatePingCount(AccessTypeData accessTypeData) {
        Log.d(AccessTypeDataValidator.class.getName(), "validatePingCount of accessTypeData " + accessTypeData);
        int pingCountMin = context.getResources().getInteger(R.integer.ping_count_minimum);
        int pingCountMax = context.getResources().getInteger(R.integer.ping_count_maximum);
        if (accessTypeData.getPingCount() < pingCountMin || accessTypeData.getPingCount() > pingCountMax) {
            Log.d(AccessTypeDataValidator.class.getName(), "pingCount is invalid. Returning false.");
            return false;
        }
        Log.d(AccessTypeDataValidator.class.getName(), "pingCount is valid. Returning true.");
        return true;
    }

    public boolean validatePingPackageSize(AccessTypeData accessTypeData) {
        Log.d(AccessTypeDataValidator.class.getName(), "validatePingPackageSize of accessTypeData " + accessTypeData);
        int pingPackageSizeMin = context.getResources().getInteger(R.integer.ping_package_size_minimum);
        int pingPackageSizeMax = context.getResources().getInteger(R.integer.ping_package_size_maximum);
        if (accessTypeData.getPingPackageSize() < pingPackageSizeMin || accessTypeData.getPingPackageSize() > pingPackageSizeMax) {
            Log.d(AccessTypeDataValidator.class.getName(), "pingPackageSize is invalid. Returning false.");
            return false;
        }
        Log.d(AccessTypeDataValidator.class.getName(), "pingPackageSize is valid. Returning true.");
        return true;
    }

    public boolean validateConnectCount(AccessTypeData accessTypeData) {
        Log.d(AccessTypeDataValidator.class.getName(), "validateConnectCount of accessTypeData " + accessTypeData);
        int connectCountMin = context.getResources().getInteger(R.integer.connect_count_minimum);
        int connectCountMax = context.getResources().getInteger(R.integer.connect_count_maximum);
        if (accessTypeData.getConnectCount() < connectCountMin || accessTypeData.getConnectCount() > connectCountMax) {
            Log.d(AccessTypeDataValidator.class.getName(), "connectCount is invalid. Returning false.");
            return false;
        }
        Log.d(AccessTypeDataValidator.class.getName(), "connectCount is valid. Returning true.");
        return true;
    }
}
