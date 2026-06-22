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

package net.ibbaa.keepitup.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.model.SNMPInterfaceInfo;
import net.ibbaa.keepitup.model.SNMPItem;
import net.ibbaa.keepitup.service.network.SNMPMapping;
import net.ibbaa.keepitup.ui.dialog.SNMPInterfacesDialog;
import net.ibbaa.keepitup.util.BundleUtil;
import net.ibbaa.keepitup.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SNMPInterfacesAdapter extends RecyclerView.Adapter<SNMPInterfacesViewHolder> {

    private final List<SNMPItem> snmpItems;
    private final List<SNMPItem> filteredSnmpItems;
    private final Map<String, SNMPInterfaceInfo> snmpInterfaceInfo;
    private final SNMPInterfacesDialog snmpInterfacesDialog;
    private boolean showAll;

    public SNMPInterfacesAdapter(List<SNMPItem> snmpItems, Map<String, SNMPInterfaceInfo> snmpInterfaceInfo, SNMPInterfacesDialog snmpInterfacesDialog) {
        this.snmpItems = new ArrayList<>();
        this.filteredSnmpItems = new ArrayList<>();
        this.snmpInterfaceInfo = new HashMap<>();
        this.snmpInterfacesDialog = snmpInterfacesDialog;
        this.showAll = false;
        replaceItems(snmpItems, snmpInterfaceInfo);
    }

    @NonNull
    @Override
    public SNMPInterfacesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Log.d(SNMPInterfacesAdapter.class.getName(), "onCreateViewHolder");
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_snmp_interface, viewGroup, false);
        return new SNMPInterfacesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SNMPInterfacesViewHolder snmpInterfacesViewHolder, int position) {
        Log.d(SNMPInterfacesAdapter.class.getName(), "onBindViewHolder");
        if (!getItems().isEmpty()) {
            if (position < getItems().size()) {
                SNMPItem item = getItems().get(position);
                bindInterfaceItem(snmpInterfacesViewHolder, item);
                snmpInterfacesViewHolder.showItemCardView();
                snmpInterfacesViewHolder.hideNoItemsTextView();
            } else {
                snmpInterfacesViewHolder.hideItemCardView();
                snmpInterfacesViewHolder.hideNoItemsTextView();
            }
        } else {
            String noItemsText;
            if (snmpInterfacesDialog.isScanned() && !snmpItems.isEmpty()) {
                noItemsText = getContext().getString(R.string.text_dialog_snmp_interfaces_no_items_all_filtered);
            } else if (snmpInterfacesDialog.isScanned()) {
                noItemsText = getContext().getString(R.string.text_dialog_snmp_interfaces_no_items_after_scan);
            } else {
                noItemsText = getContext().getString(R.string.text_dialog_snmp_interfaces_no_items);
            }
            snmpInterfacesViewHolder.setNoItemsText(noItemsText);
            snmpInterfacesViewHolder.hideItemCardView();
            snmpInterfacesViewHolder.showNoItemsTextView();
        }
    }

    private void bindInterfaceItem(@NonNull SNMPInterfacesViewHolder snmpInterfacesViewHolder, SNMPItem item) {
        Log.d(SNMPInterfacesAdapter.class.getName(), "bindInterfaceItem, item is " + item);
        snmpInterfacesViewHolder.setNameText(StringUtil.notNull(item.getName()));
        snmpInterfacesViewHolder.setOnMonitoredChangedListener(null);
        snmpInterfacesViewHolder.setChecked(item.isMonitored());
        snmpInterfacesViewHolder.setOnMonitoredChangedListener((buttonView, isChecked) -> onMonitorChanged(item, isChecked));
        SNMPInterfaceInfo info = snmpInterfaceInfo.get(item.getOid());
        bindAliasText(snmpInterfacesViewHolder, info);
        bindStatusText(snmpInterfacesViewHolder, info);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void onMonitorChanged(SNMPItem item, boolean isChecked) {
        Log.d(SNMPInterfacesAdapter.class.getName(), "onMonitorChanged, isChecked is " + isChecked);
        item.setMonitored(isChecked);
        prepareFilteredList();
        notifyDataSetChanged();
    }

    private void bindAliasText(@NonNull SNMPInterfacesViewHolder snmpInterfacesViewHolder, SNMPInterfaceInfo info) {
        Log.d(SNMPInterfacesAdapter.class.getName(), "bindAliasText, info is " + info);
        if (info != null && !StringUtil.isEmpty(info.getAlias())) {
            snmpInterfacesViewHolder.setAliasText(info.getAlias());
            snmpInterfacesViewHolder.showAliasText();
        } else {
            snmpInterfacesViewHolder.hideAliasText();
        }
    }

    private void bindStatusText(@NonNull SNMPInterfacesViewHolder snmpInterfacesViewHolder, SNMPInterfaceInfo info) {
        Log.d(SNMPInterfacesAdapter.class.getName(), "bindStatusText, info is " + info);
        if (info == null || info.getStatus() < 0) {
            snmpInterfacesViewHolder.setStatusText("");
            return;
        }
        SNMPMapping snmpMapping = new SNMPMapping(getContext());
        snmpInterfacesViewHolder.setStatusText(snmpMapping.getLabelForInterfaceOperStatus(info.getStatus()));
        int upStatus = getContext().getResources().getInteger(R.integer.interface_operstatus_up);
        int color = (info.getStatus() == upStatus) ? ContextCompat.getColor(getContext(), R.color.textOkColor) : ContextCompat.getColor(getContext(), R.color.textErrorColor);
        snmpInterfacesViewHolder.setStatusTextColor(color);
    }

    public Bundle saveStateToBundle() {
        Log.d(SNMPInterfacesAdapter.class.getName(), "saveStateToBundle");
        Bundle bundle = BundleUtil.snmpItemListToBundle(getSNMPItemsKey(), snmpItems);
        bundle = BundleUtil.snmpInterfaceInfoMapToBundle(getSNMPInterfaceInfoKey(), snmpInterfaceInfo, bundle);
        return BundleUtil.booleanToBundle(getShowAllKey(), showAll, bundle);
    }

    public void restoreStateFromBundle(Bundle bundle) {
        Log.d(SNMPInterfacesAdapter.class.getName(), "restoreStateFromBundle");
        showAll = bundle.getBoolean(getShowAllKey());
        replaceItems(BundleUtil.snmpItemListFromBundle(getSNMPItemsKey(), bundle), BundleUtil.snmpInterfaceInfoMapFromBundle(getSNMPInterfaceInfoKey(), bundle));
    }

    private String getSNMPItemsKey() {
        return SNMPInterfacesAdapter.class.getSimpleName() + ".SNMPItems";
    }

    private String getSNMPInterfaceInfoKey() {
        return SNMPInterfacesAdapter.class.getSimpleName() + ".SNMPInterfaceInfo";
    }

    private String getShowAllKey() {
        return SNMPInterfacesAdapter.class.getSimpleName() + ".ShowAll";
    }

    @SuppressWarnings("unused")
    public SNMPItem getItem(int index) {
        Log.d(SNMPInterfacesAdapter.class.getName(), "getItem for index " + index);
        if (index < 0 || index >= getItems().size()) {
            Log.e(SNMPInterfacesAdapter.class.getName(), "invalid index " + index);
            return null;
        }
        return getItems().get(index);
    }

    public void replaceItems(List<SNMPItem> items) {
        this.snmpItems.clear();
        this.snmpItems.addAll(items);
        prepareFilteredList();
    }

    public void replaceItems(List<SNMPItem> items, Map<String, SNMPInterfaceInfo> snmpInterfaceInfo) {
        this.snmpItems.clear();
        this.snmpItems.addAll(items);
        this.snmpInterfaceInfo.clear();
        this.snmpInterfaceInfo.putAll(snmpInterfaceInfo);
        prepareFilteredList();
    }

    private void prepareFilteredList() {
        filteredSnmpItems.clear();
        SNMPMapping snmpMapping = new SNMPMapping(getContext());
        for (SNMPItem item : snmpItems) {
            SNMPInterfaceInfo info = snmpInterfaceInfo.get(item.getOid());
            if (!snmpMapping.isFilteredInterface(info) || item.isMonitored()) {
                filteredSnmpItems.add(item);
            }
        }
    }

    private List<SNMPItem> getItems() {
        if (showAll) {
            return snmpItems;
        }
        return filteredSnmpItems;
    }

    @Override
    public int getItemCount() {
        return getItems().isEmpty() ? 1 : getItems().size();
    }

    public boolean hasFilterEffect() {
        return snmpItems.size() != filteredSnmpItems.size();
    }

    public boolean isShowAll() {
        return showAll;
    }

    public void setShowAll(boolean showAll) {
        this.showAll = showAll;
    }

    public List<SNMPItem> getAllItems() {
        return Collections.unmodifiableList(snmpItems);
    }

    public Map<String, SNMPInterfaceInfo> getInterfaceInfos() {
        return Collections.unmodifiableMap(snmpInterfaceInfo);
    }

    public Context getContext() {
        return snmpInterfacesDialog.getContext();
    }
}
