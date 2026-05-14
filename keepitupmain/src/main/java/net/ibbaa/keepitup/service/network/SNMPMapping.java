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
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.model.SNMPInterfaceInfo;
import net.ibbaa.keepitup.model.SNMPItem;
import net.ibbaa.keepitup.model.SNMPItemType;
import net.ibbaa.keepitup.util.NumberUtil;
import net.ibbaa.keepitup.util.StringUtil;

import org.snmp4j.smi.OID;
import org.snmp4j.smi.Variable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SNMPMapping {

    private final Map<String, String> oidLabels;
    private final Map<Integer, String> ifStatusLabels;
    private final Context context;

    public SNMPMapping(Context context) {
        this.oidLabels = new HashMap<>();
        this.ifStatusLabels = new HashMap<>();
        this.context = context;
        initSystemOIDMap();
    }

    private void initSystemOIDMap() {
        oidLabels.put(getResources().getString(R.string.sys_descr_oid), getResources().getString(R.string.sys_descr_label));
        oidLabels.put(getResources().getString(R.string.sys_uptime_oid), getResources().getString(R.string.sys_uptime_label));
        oidLabels.put(getResources().getString(R.string.sys_object_id_oid), getResources().getString(R.string.sys_object_id_label));
        oidLabels.put(getResources().getString(R.string.sys_contact_oid), getResources().getString(R.string.sys_contact_label));
        oidLabels.put(getResources().getString(R.string.sys_name_oid), getResources().getString(R.string.sys_name_label));
        oidLabels.put(getResources().getString(R.string.sys_location_oid), getResources().getString(R.string.sys_location_label));
        ifStatusLabels.put(getResources().getInteger(R.integer.interface_operstatus_up), getResources().getString(R.string.interface_operstatus_up_label));
        ifStatusLabels.put(getResources().getInteger(R.integer.interface_operstatus_down), getResources().getString(R.string.interface_operstatus_down_label));
        ifStatusLabels.put(getResources().getInteger(R.integer.interface_operstatus_testing), getResources().getString(R.string.interface_operstatus_testing_label));
        ifStatusLabels.put(getResources().getInteger(R.integer.interface_operstatus_unknown), getResources().getString(R.string.interface_operstatus_unknown_label));
        ifStatusLabels.put(getResources().getInteger(R.integer.interface_operstatus_dormant), getResources().getString(R.string.interface_operstatus_dormant_label));
        ifStatusLabels.put(getResources().getInteger(R.integer.interface_operstatus_notpresent), getResources().getString(R.string.interface_operstatus_notpresent_label));
        ifStatusLabels.put(getResources().getInteger(R.integer.interface_operstatus_lowerlayerdown), getResources().getString(R.string.interface_operstatus_lowerlayerdown_label));
    }

    public String getSystemOID() {
        return getResources().getString(R.string.system_oid);
    }

    public String getSysUpTimeOID() {
        return getResources().getString(R.string.sys_uptime_oid);
    }

    public String getInterfaceDescrOID() {
        return getResources().getString(R.string.interface_descr_oid);
    }

    public String getInterfaceTypeOID() {
        return getResources().getString(R.string.interface_type_oid);
    }

    public String getInterfaceOperStatusOID() {
        return getResources().getString(R.string.interface_operstatus_oid);
    }

    public String getInterfaceAliasOID() {
        return getResources().getString(R.string.interface_alias_oid);
    }

    public Map<String, SNMPInterfaceInfo> toSNMPInterfaceInfo(List<SNMPItem> ifDescrList, Map<String, String> values) {
        Log.d(SNMPMapping.class.getName(), "toSNMPInterfaceInfo");
        Map<String, SNMPInterfaceInfo> result = new HashMap<>();
        if (ifDescrList == null) {
            return result;
        }
        for (SNMPItem item : ifDescrList) {
            OID oid = validateItemAndGetOID(item);
            if (oid != null) {
                int index = oid.get(oid.size() - 1);
                SNMPInterfaceInfo info = new SNMPInterfaceInfo();
                info.setDescr(item.getName());
                if (values != null) {
                    String typeValue = values.get(getInterfaceTypeOID() + "." + index);
                    if (typeValue != null) {
                        info.setType(NumberUtil.getIntValue(typeValue, -1));
                    }
                    String statusValue = values.get(getInterfaceOperStatusOID() + "." + index);
                    if (statusValue != null) {
                        info.setStatus(NumberUtil.getIntValue(statusValue, -1));
                    }
                    String aliasValue = values.get(getInterfaceAliasOID() + "." + index);
                    if (aliasValue != null) {
                        info.setAlias(aliasValue);
                    }
                }
                result.put(item.getOid(), info);
            }
        }
        return result;
    }

    private OID validateItemAndGetOID(SNMPItem item) {
        if (item == null) {
            Log.d(SNMPMapping.class.getName(), "item is null");
            return null;
        }
        if (!SNMPItemType.INTERFACEDESCR.equals(item.getSnmpItemType())) {
            Log.d(SNMPMapping.class.getName(), "item has unexpected type " + item.getSnmpItemType());
            return null;
        }
        String oidString = item.getOid();
        if (StringUtil.isEmpty(oidString)) {
            Log.e(SNMPMapping.class.getName(), "OID is empty");
            return null;
        }
        OID oid;
        try {
            oid = new OID(oidString);
        } catch (Exception exc) {
            Log.e(SNMPMapping.class.getName(), "OID not parseable: " + oidString, exc);
            return null;
        }
        return oid;
    }

    public List<SNMPItem> toSNMPInterfaceItems(Map<String, String> values, long networktaskId) {
        Log.d(SNMPMapping.class.getName(), "toSNMPInterfaceItems");
        List<SNMPItem> snmpList = new ArrayList<>();
        for (Map.Entry<String, String> entry : values.entrySet()) {
            String oid = entry.getKey();
            String value = entry.getValue();
            SNMPItemType itemType = getSNMPItemType(oid);
            if (itemType != null) {
                SNMPItem item = new SNMPItem();
                item.setNetworkTaskId(networktaskId);
                item.setSnmpItemType(itemType);
                item.setOid(oid);
                item.setName(value);
                item.setMonitored(false);
                snmpList.add(item);
            }
        }
        return snmpList;
    }

    private SNMPItemType getSNMPItemType(String oidString) {
        if (StringUtil.isEmpty(oidString)) {
            return null;
        }
        OID oid;
        try {
            oid = new OID(oidString);
        } catch (Exception exc) {
            Log.e(SNMPMapping.class.getName(), "OID not parseable", exc);
            return null;
        }
        if (oid.startsWith(new OID(getInterfaceDescrOID()))) {
            return SNMPItemType.INTERFACEDESCR;
        }
        if (oid.startsWith(new OID(getInterfaceTypeOID()))) {
            return SNMPItemType.INTERFACETYPE;
        }
        if (oid.startsWith(new OID(getInterfaceAliasOID()))) {
            return SNMPItemType.INTERFACEALIAS;
        }
        return null;
    }

    public String getValueForOID(String oid, Variable variable) {
        if (StringUtil.isEmpty(oid) || variable == null) {
            return null;
        }
        if (isSysUpTimeOID(oid)) {
            try {
                return String.valueOf(variable.toLong());
            } catch (Exception exc) {
                return variable.toString();
            }
        }
        return variable.toString();
    }

    public boolean isSysUpTimeOID(String oid) {
        if (StringUtil.isEmpty(oid)) {
            return false;
        }
        return getSysUpTimeOID().equals(oid);
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

    public boolean supportsSystemOID(String oid) {
        return getLabelForSystemOID(oid) != null;
    }

    public String getLabelForSystemOID(String oid) {
        if (oid == null) {
            return null;
        }
        return oidLabels.get(oid);
    }

    public String getLabelForInterfaceOperStatus(int status) {
        String label = ifStatusLabels.get(status);
        if (StringUtil.isEmpty(label)) {
            return getResources().getString(R.string.interface_operstatus_unknown_label);
        }
        return label;
    }

    private Context getContext() {
        return context;
    }

    private Resources getResources() {
        return getContext().getResources();
    }
}
