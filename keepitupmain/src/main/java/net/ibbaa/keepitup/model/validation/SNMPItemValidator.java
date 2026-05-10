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
import net.ibbaa.keepitup.model.SNMPItem;
import net.ibbaa.keepitup.util.SNMPUtil;
import net.ibbaa.keepitup.util.StringUtil;

public class SNMPItemValidator {

    private final Context context;

    public SNMPItemValidator(Context context) {
        this.context = context;
    }

    public boolean validate(SNMPItem item) {
        Log.d(SNMPItemValidator.class.getName(), "validate item " + item);
        return validateName(item) && validateOID(item) && validateSNMPItemType(item);
    }

    public boolean validateName(SNMPItem item) {
        Log.d(SNMPItemValidator.class.getName(), "validateName for item " + item);
        String name = item.getName();
        if (StringUtil.isEmpty(name)) {
            Log.d(SNMPItemValidator.class.getName(), "name is empty. Returning true.");
            return true;
        }
        int nameMaxLength = context.getResources().getInteger(R.integer.snmp_name_max_length);
        if (name.length() > nameMaxLength) {
            Log.d(SNMPItemValidator.class.getName(), "name is too long. Returning false.");
            return false;
        }
        if (!SNMPUtil.validateName(name)) {
            Log.d(SNMPItemValidator.class.getName(), "name has invalid characters. Returning false.");
            return false;
        }
        return true;
    }

    public boolean validateOID(SNMPItem item) {
        Log.d(SNMPItemValidator.class.getName(), "validateOID for item " + item);
        String oid = item.getOid();
        if (StringUtil.isEmpty(oid)) {
            Log.d(SNMPItemValidator.class.getName(), "oid is empty. Returning true.");
            return false;
        }
        int oidMaxLength = context.getResources().getInteger(R.integer.snmp_oid_max_length);
        if (oid.length() > oidMaxLength) {
            Log.d(SNMPItemValidator.class.getName(), "oid is too long. Returning false.");
            return false;
        }
        if (!SNMPUtil.validateOID(oid)) {
            Log.d(SNMPItemValidator.class.getName(), "oid has invalid characters. Returning false.");
            return false;
        }
        return true;
    }

    public boolean validateSNMPItemType(SNMPItem item) {
        Log.d(SNMPItemValidator.class.getName(), "validateSNMPItemType for item " + item);
        if (item.getSnmpItemType() == null) {
            Log.d(SNMPItemValidator.class.getName(), "SNMPItemType is null. Returning false.");
            return false;
        }
        Log.d(SNMPItemValidator.class.getName(), "SNMPItemType is valid. Returning true.");
        return true;
    }
}
