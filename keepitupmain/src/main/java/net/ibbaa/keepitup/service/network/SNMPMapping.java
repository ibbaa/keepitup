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

package net.ibbaa.keepitup.service.network;

import android.content.Context;
import android.content.res.Resources;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.util.NumberUtil;
import net.ibbaa.keepitup.util.StringUtil;

import org.snmp4j.smi.Variable;

import java.util.HashMap;
import java.util.Map;

public class SNMPMapping {

    private final Map<String, String> oidLabels;
    private final Context context;

    public SNMPMapping(Context context) {
        this.oidLabels = new HashMap<>();
        this.context = context;
        initOIDMap();
    }

    private void initOIDMap() {
        oidLabels.put(getResources().getString(R.string.sys_descr_oid), getResources().getString(R.string.sys_descr_label));
        oidLabels.put(getResources().getString(R.string.sys_uptime_oid), getResources().getString(R.string.sys_uptime_label));
        oidLabels.put(getResources().getString(R.string.sys_object_id_oid), getResources().getString(R.string.sys_object_id_label));
        oidLabels.put(getResources().getString(R.string.sys_contact_oid), getResources().getString(R.string.sys_contact_label));
        oidLabels.put(getResources().getString(R.string.sys_name_oid), getResources().getString(R.string.sys_name_label));
        oidLabels.put(getResources().getString(R.string.sys_location_oid), getResources().getString(R.string.sys_location_label));
    }

    public String getSystemOID() {
        return getResources().getString(R.string.system_oid);
    }

    public String getSysUpTimeOID() {
        return getResources().getString(R.string.sys_uptime_oid);
    }

    public String getValueForOID(String oid, Variable variable) {
        if (StringUtil.isEmpty(oid) || variable == null) {
            return null;
        }
        if (getResources().getString(R.string.sys_uptime_oid).equals(oid)) {
            try {
                return String.valueOf(variable.toLong());
            } catch (Exception exc) {
                return variable.toString().trim();
            }
        }
        return variable.toString().trim();
    }

    public boolean isSysUpTimeOID(String oid) {
        if (StringUtil.isEmpty(oid)) {
            return false;
        }
        return getResources().getString(R.string.sys_uptime_oid).equals(oid.trim());
    }

    public long getSysUpTime(Map<String, String> values) {
        if (values == null) {
            return -1;
        }
        String sysUpString = values.get(getResources().getString(R.string.sys_uptime_oid));
        if (sysUpString != null) {
            return NumberUtil.getLongValue(sysUpString, -1);
        }
        return -1;
    }

    public boolean supportsOID(String oid) {
        return getLabelForOID(oid) != null;
    }

    public String getLabelForOID(String oid) {
        if (oid == null) {
            return null;
        }
        return oidLabels.get(oid);
    }

    private Context getContext() {
        return context;
    }

    private Resources getResources() {
        return getContext().getResources();
    }
}
