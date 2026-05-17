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

package net.ibbaa.keepitup.model.validation;

import android.content.Context;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.model.SNMPInterfaceInfo;
import net.ibbaa.keepitup.util.SNMPUtil;
import net.ibbaa.keepitup.util.StringUtil;

public class SNMPInterfaceInfoValidator {

    private final Context context;

    public SNMPInterfaceInfoValidator(Context context) {
        this.context = context;
    }

    public boolean validate(SNMPInterfaceInfo info) {
        Log.d(SNMPInterfaceInfoValidator.class.getName(), "validate for snmp interface info " + info);
        return validateDescr(info) && validateAlias(info) && validateType(info) && validateStatus(info);
    }

    public boolean validateDescr(SNMPInterfaceInfo info) {
        Log.d(SNMPInterfaceInfoValidator.class.getName(), "validateDescr for snmp interface info " + info);
        return validateDescrValue(info.getDescr());
    }

    public boolean validateAlias(SNMPInterfaceInfo info) {
        Log.d(SNMPInterfaceInfoValidator.class.getName(), "validateAlias for snmp interface info " + info);
        return validateDescrValue(info.getAlias());
    }

    public boolean validateType(SNMPInterfaceInfo info) {
        Log.d(SNMPInterfaceInfoValidator.class.getName(), "validateType for snmp interface info " + info);
        int typeMinValue = context.getResources().getInteger(R.integer.snmp_iftype_minimum);
        if (info.getType() < typeMinValue) {
            Log.d(SNMPInterfaceInfoValidator.class.getName(), "type is below " + typeMinValue + ". Returning false.");
            return false;
        }
        return true;
    }

    public boolean validateStatus(SNMPInterfaceInfo info) {
        Log.d(SNMPInterfaceInfoValidator.class.getName(), "validateStatus for snmp interface info " + info);
        int statusMinValue = context.getResources().getInteger(R.integer.snmp_ifoperstatus_minimum);
        if (info.getStatus() < statusMinValue) {
            Log.d(SNMPInterfaceInfoValidator.class.getName(), "status is below " + statusMinValue + ". Returning false.");
            return false;
        }
        return true;
    }

    private boolean validateDescrValue(String value) {
        Log.d(SNMPInterfaceInfoValidator.class.getName(), "validateDescrValue for value " + value);
        if (StringUtil.isEmpty(value)) {
            Log.d(SNMPInterfaceInfoValidator.class.getName(), "value is empty. Returning true.");
            return true;
        }
        int descrMaxLength = context.getResources().getInteger(R.integer.snmp_ifdescr_max_length);
        if (value.length() > descrMaxLength) {
            Log.d(SNMPInterfaceInfoValidator.class.getName(), "value is too long. Returning false.");
            return false;
        }
        if (!SNMPUtil.validateInterfaceDescr(value)) {
            Log.d(SNMPInterfaceInfoValidator.class.getName(), "value has invalid characters. Returning false.");
            return false;
        }
        return true;
    }
}
