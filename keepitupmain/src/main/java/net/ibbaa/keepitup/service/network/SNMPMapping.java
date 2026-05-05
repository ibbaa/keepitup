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
        oidLabels.put(getResources().getString(R.string.sysDescr_oid), getResources().getString(R.string.sysDescr_label));
        oidLabels.put(getResources().getString(R.string.sysUpTime_oid), getResources().getString(R.string.sysUpTime_label));
        oidLabels.put(getResources().getString(R.string.sysObjectID_oid), getResources().getString(R.string.sysObjectID_label));
        oidLabels.put(getResources().getString(R.string.sysContact_oid), getResources().getString(R.string.sysContact_label));
        oidLabels.put(getResources().getString(R.string.sysName_oid), getResources().getString(R.string.sysName_label));
        oidLabels.put(getResources().getString(R.string.sysLocation_oid), getResources().getString(R.string.sysLocation_label));
    }

    public String getValueForOID(String oid, Variable variable) {
        if (StringUtil.isEmpty(oid) || variable == null) {
            return null;
        }
        if (getResources().getString(R.string.sysUpTime_oid).equals(oid)) {
            try {
                return String.valueOf(variable.toLong());
            } catch (Exception exc) {
                return variable.toString();
            }
        }
        return variable.toString();
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
