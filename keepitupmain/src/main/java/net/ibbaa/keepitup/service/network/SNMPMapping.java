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
import net.ibbaa.keepitup.model.SNMPItemMergeResult;
import net.ibbaa.keepitup.model.SNMPItemType;
import net.ibbaa.keepitup.model.validation.SNMPInterfaceInfoValidator;
import net.ibbaa.keepitup.model.validation.SNMPItemValidator;
import net.ibbaa.keepitup.util.NumberUtil;
import net.ibbaa.keepitup.util.StringUtil;

import org.snmp4j.smi.OID;
import org.snmp4j.smi.Variable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
        SNMPInterfaceInfoValidator validator = new SNMPInterfaceInfoValidator(getContext());
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
                if (validator.validateDescr(info)) {
                    if (!validator.validateAlias(info)) {
                        info.setAlias(null);
                    }
                    if (!validator.validateType(info)) {
                        info.setType(-1);
                    }
                    if (!validator.validateStatus(info)) {
                        info.setStatus(-1);
                    }
                    result.put(item.getOid(), info);
                }
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

    public List<SNMPItem> toSNMPItems(Map<String, String> values, long networktaskId) {
        Log.d(SNMPMapping.class.getName(), "toSNMPItems");
        SNMPItemValidator validator = new SNMPItemValidator(getContext());
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
                if (validator.validate(item)) {
                    snmpList.add(item);
                }
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

    public List<SNMPItem> filterDescrItems(List<SNMPItem> allItems) {
        Log.d(SNMPMapping.class.getName(), "filterDescrItems");
        List<SNMPItem> result = new ArrayList<>();
        if (allItems == null) {
            return result;
        }
        for (SNMPItem item : allItems) {
            if (SNMPItemType.INTERFACEDESCR.equals(item.getSnmpItemType())) {
                result.add(item);
            }
        }
        return result;
    }

    public Map<String, SNMPInterfaceInfo> extractSNMPInterfaceInfos(List<SNMPItem> allItems) {
        Log.d(SNMPMapping.class.getName(), "extractSNMPInterfaceInfos");
        if (allItems == null) {
            return new HashMap<>();
        }
        List<SNMPItem> descrItems = filterDescrItems(allItems);
        Map<String, String> values = new HashMap<>();
        for (SNMPItem item : allItems) {
            if (item.getSnmpItemType() != null && !SNMPItemType.INTERFACEDESCR.equals(item.getSnmpItemType()) && !StringUtil.isEmpty(item.getOid()) && item.getName() != null) {
                values.put(item.getOid(), item.getName());
            }
        }
        return toSNMPInterfaceInfo(descrItems, values);
    }

    public SNMPItemMergeResult mergeDescrItems(List<SNMPItem> existing, List<SNMPItem> scanned) {
        Log.d(SNMPMapping.class.getName(), "mergeDescrItems");
        List<SNMPItem> mergedItems = new ArrayList<>();
        List<SNMPItem> removedMonitoredItems = new ArrayList<>();
        Map<String, SNMPItem> existingByName = new HashMap<>();
        Map<String, SNMPItem> scannedByName = new HashMap<>();
        for (SNMPItem item : existing) {
            existingByName.put(StringUtil.notNull(item.getName()), item);
        }
        for (SNMPItem item : scanned) {
            scannedByName.put(StringUtil.notNull(item.getName()), item);
        }
        for (SNMPItem existingItem : existing) {
            String name = StringUtil.notNull(existingItem.getName());
            SNMPItem scannedItem = scannedByName.get(name);
            if (scannedItem != null) {
                SNMPItem mergedItem = new SNMPItem();
                mergedItem.setId(existingItem.getId());
                mergedItem.setNetworkTaskId(existingItem.getNetworkTaskId());
                mergedItem.setSnmpItemType(existingItem.getSnmpItemType());
                mergedItem.setName(existingItem.getName());
                mergedItem.setOid(scannedItem.getOid());
                mergedItem.setMonitored(existingItem.isMonitored());
                mergedItems.add(mergedItem);
            } else {
                if (existingItem.isMonitored()) {
                    removedMonitoredItems.add(existingItem);
                }
            }
        }
        for (SNMPItem scannedItem : scanned) {
            if (!existingByName.containsKey(StringUtil.notNull(scannedItem.getName()))) {
                mergedItems.add(scannedItem);
            }
        }
        Collections.sort(mergedItems, new SNMPItemNameComparator());
        return new SNMPItemMergeResult(mergedItems, removedMonitoredItems);
    }

    public Map<String, SNMPInterfaceInfo> mergeSNMPInterfaceInfos(Map<String, SNMPInterfaceInfo> existing, Map<String, SNMPInterfaceInfo> scanned) {
        Log.d(SNMPMapping.class.getName(), "mergeSNMPInterfaceInfos");
        Map<String, SNMPInterfaceInfo> result = new HashMap<>();
        if (scanned == null || scanned.isEmpty()) {
            return result;
        }
        Map<String, SNMPInterfaceInfo> existingByName = new HashMap<>();
        if (existing != null) {
            for (SNMPInterfaceInfo info : existing.values()) {
                if (info.getDescr() != null) {
                    existingByName.put(info.getDescr(), info);
                }
            }
        }
        for (Map.Entry<String, SNMPInterfaceInfo> entry : scanned.entrySet()) {
            String oid = entry.getKey();
            SNMPInterfaceInfo scannedInfo = entry.getValue();
            SNMPInterfaceInfo existingInfo = scannedInfo.getDescr() != null ? existingByName.get(scannedInfo.getDescr()) : null;
            if (existingInfo == null) {
                result.put(oid, scannedInfo);
            } else {
                SNMPInterfaceInfo merged = new SNMPInterfaceInfo();
                merged.setDescr(scannedInfo.getDescr());
                merged.setStatus(scannedInfo.getStatus());
                merged.setType(scannedInfo.getType() >= 0 ? scannedInfo.getType() : existingInfo.getType());
                merged.setAlias(scannedInfo.getAlias() != null ? scannedInfo.getAlias() : existingInfo.getAlias());
                result.put(oid, merged);
            }
        }
        return result;
    }

    public List<SNMPItem> mergeAllSNMPItems(List<SNMPItem> originalAll, List<SNMPItem> editedDescrItems, Map<String, SNMPInterfaceInfo> newInfos, long networkTaskId) {
        Log.d(SNMPMapping.class.getName(), "mergeAllSNMPItems");
        Map<Integer, Long> oldIndexToDescrId = new HashMap<>();
        for (SNMPItem item : originalAll) {
            if (SNMPItemType.INTERFACEDESCR.equals(item.getSnmpItemType()) && item.getId() >= 0) {
                int index = getOidIndex(item.getOid());
                if (index >= 0) {
                    oldIndexToDescrId.put(index, item.getId());
                }
            }
        }
        Map<Long, SNMPItem> editedDescrById = new HashMap<>();
        List<SNMPItem> newDescrItems = new ArrayList<>();
        for (SNMPItem item : editedDescrItems) {
            if (item.getId() >= 0) {
                editedDescrById.put(item.getId(), item);
            } else {
                newDescrItems.add(item);
            }
        }
        List<SNMPItem> result = new ArrayList<>();
        Set<Integer> handledTypeIndices = new HashSet<>();
        Set<Integer> handledAliasIndices = new HashSet<>();
        for (SNMPItem originalItem : originalAll) {
            SNMPItemType itemType = originalItem.getSnmpItemType();
            if (SNMPItemType.INTERFACEDESCR.equals(itemType)) {
                SNMPItem edited = editedDescrById.get(originalItem.getId());
                if (edited != null) {
                    result.add(copyItem(originalItem, edited.getOid(), null, edited.isMonitored()));
                }
            } else if (SNMPItemType.INTERFACETYPE.equals(itemType) || SNMPItemType.INTERFACEALIAS.equals(itemType)) {
                int oldIndex = getOidIndex(originalItem.getOid());
                Long descrId = oldIndexToDescrId.get(oldIndex);
                if (descrId != null) {
                    SNMPItem editedDescr = editedDescrById.get(descrId);
                    if (editedDescr != null) {
                        int newIndex = getOidIndex(editedDescr.getOid());
                        SNMPInterfaceInfo info = newInfos != null ? newInfos.get(editedDescr.getOid()) : null;
                        if (SNMPItemType.INTERFACETYPE.equals(itemType)) {
                            if (info != null && info.getType() >= 0 && newIndex >= 0) {
                                result.add(copyItem(originalItem, getInterfaceTypeOID() + "." + newIndex, String.valueOf(info.getType()), false));
                                handledTypeIndices.add(newIndex);
                            }
                        } else {
                            if (info != null && info.getAlias() != null && newIndex >= 0) {
                                result.add(copyItem(originalItem, getInterfaceAliasOID() + "." + newIndex, info.getAlias(), false));
                                handledAliasIndices.add(newIndex);
                            }
                        }
                    }
                }
            }
        }
        for (SNMPItem editedDescr : editedDescrItems) {
            if (editedDescr.getId() >= 0) {
                int newIndex = getOidIndex(editedDescr.getOid());
                SNMPInterfaceInfo info = newIndex >= 0 && newInfos != null ? newInfos.get(editedDescr.getOid()) : null;
                if (info != null) {
                    if (info.getType() >= 0 && !handledTypeIndices.contains(newIndex)) {
                        result.add(createItem(networkTaskId, SNMPItemType.INTERFACETYPE, String.valueOf(info.getType()), getInterfaceTypeOID() + "." + newIndex));
                        handledTypeIndices.add(newIndex);
                    }
                    if (info.getAlias() != null && !handledAliasIndices.contains(newIndex)) {
                        result.add(createItem(networkTaskId, SNMPItemType.INTERFACEALIAS, info.getAlias(), getInterfaceAliasOID() + "." + newIndex));
                        handledAliasIndices.add(newIndex);
                    }
                }
            }
        }
        for (SNMPItem newDescr : newDescrItems) {
            result.add(newDescr);
            int newIndex = getOidIndex(newDescr.getOid());
            SNMPInterfaceInfo info = newInfos != null ? newInfos.get(newDescr.getOid()) : null;
            if (info != null && newIndex >= 0) {
                if (info.getType() >= 0) {
                    result.add(createItem(networkTaskId, SNMPItemType.INTERFACETYPE, String.valueOf(info.getType()), getInterfaceTypeOID() + "." + newIndex));
                }
                if (info.getAlias() != null) {
                    result.add(createItem(networkTaskId, SNMPItemType.INTERFACEALIAS, info.getAlias(), getInterfaceAliasOID() + "." + newIndex));
                }
            }
        }
        return result;
    }

    private SNMPItem copyItem(SNMPItem original, String newOid, String newName, boolean newMonitored) {
        SNMPItem copy = new SNMPItem();
        copy.setId(original.getId());
        copy.setNetworkTaskId(original.getNetworkTaskId());
        copy.setSnmpItemType(original.getSnmpItemType());
        copy.setName(newName != null ? newName : original.getName());
        copy.setOid(newOid);
        copy.setMonitored(newMonitored);
        return copy;
    }

    private SNMPItem createItem(long networkTaskId, SNMPItemType type, String name, String oid) {
        SNMPItem item = new SNMPItem();
        item.setNetworkTaskId(networkTaskId);
        item.setSnmpItemType(type);
        item.setName(name);
        item.setOid(oid);
        return item;
    }

    private int getOidIndex(String oidString) {
        if (StringUtil.isEmpty(oidString)) {
            return -1;
        }
        try {
            OID oid = new OID(oidString);
            if (oid.size() < 1) {
                return -1;
            }
            return oid.get(oid.size() - 1);
        } catch (Exception exc) {
            Log.e(SNMPMapping.class.getName(), "OID not parseable: " + oidString, exc);
            return -1;
        }
    }

    private Context getContext() {
        return context;
    }

    private Resources getResources() {
        return getContext().getResources();
    }

    public static class SNMPItemNameComparator implements Comparator<SNMPItem> {
        @Override
        public int compare(SNMPItem item1, SNMPItem item2) {
            String name1 = item1.getName() != null ? item1.getName() : "";
            String name2 = item2.getName() != null ? item2.getName() : "";
            return name1.compareTo(name2);
        }
    }
}
