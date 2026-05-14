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

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.model.SNMPInterfaceInfo;
import net.ibbaa.keepitup.model.SNMPItem;
import net.ibbaa.keepitup.ui.dialog.SNMPItemDialog;
import net.ibbaa.keepitup.util.BundleUtil;
import net.ibbaa.keepitup.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SNMPItemAdapter extends RecyclerView.Adapter<SNMPItemViewHolder> {

    private final List<SNMPItem> snmpItems;
    private final Map<String, SNMPInterfaceInfo> snmpInterfaceInfo;
    private final SNMPItemDialog snmpItemDialog;

    public SNMPItemAdapter(List<SNMPItem> snmpItems, Map<String, SNMPInterfaceInfo> snmpInterfaceInfo, SNMPItemDialog snmpItemDialog) {
        this.snmpItems = new ArrayList<>();
        this.snmpInterfaceInfo = new HashMap<>();
        this.snmpItemDialog = snmpItemDialog;
        replaceItems(snmpItems, snmpInterfaceInfo);
    }

    @NonNull
    @Override
    public SNMPItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Log.d(SNMPItemAdapter.class.getName(), "onCreateViewHolder");
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_snmp_item, viewGroup, false);
        return new SNMPItemViewHolder(itemView, snmpItemDialog);
    }

    @Override
    public void onBindViewHolder(@NonNull SNMPItemViewHolder snmpItemViewHolder, int position) {
        Log.d(SNMPItemAdapter.class.getName(), "onBindViewHolder");
        if (!snmpItems.isEmpty()) {
            if (position < snmpItems.size()) {
                SNMPItem item = snmpItems.get(position);
                snmpItemViewHolder.setNameText(StringUtil.notNull(item.getName()));
                snmpItemViewHolder.setOnMonitoredChangedListener(null);
                snmpItemViewHolder.setChecked(item.isMonitored());
                snmpItemViewHolder.setOnMonitoredChangedListener((buttonView, isChecked) -> item.setMonitored(isChecked));
                snmpItemViewHolder.showItemCardView();
                snmpItemViewHolder.hideNoItemsTextView();
            } else {
                snmpItemViewHolder.hideItemCardView();
                snmpItemViewHolder.hideNoItemsTextView();
            }
        } else {
            String noItemsText = snmpItemDialog.isScanned() ? getContext().getString(R.string.text_dialog_snmp_item_no_items_after_scan) : getContext().getString(R.string.text_dialog_snmp_item_no_items);
            snmpItemViewHolder.setNoItemsText(noItemsText);
            snmpItemViewHolder.hideItemCardView();
            snmpItemViewHolder.showNoItemsTextView();
        }
    }

    public Bundle saveStateToBundle() {
        Log.d(SNMPItemAdapter.class.getName(), "saveStateToBundle");
        Bundle bundle = BundleUtil.snmpItemListToBundle(getSNMPItemsKey(), snmpItems);
        return BundleUtil.snmpInterfaceInfoMapToBundle(getSNMPInterfaceInfoKey(), snmpInterfaceInfo, bundle);
    }

    public void restoreStateFromBundle(Bundle bundle) {
        Log.d(SNMPItemAdapter.class.getName(), "restoreStateFromBundle");
        replaceItems(BundleUtil.snmpItemListFromBundle(getSNMPItemsKey(), bundle), BundleUtil.snmpInterfaceInfoMapFromBundle(getSNMPInterfaceInfoKey(), bundle));
    }

    private String getSNMPItemsKey() {
        return SNMPItemAdapter.class.getSimpleName() + ".SNMPItems";
    }

    private String getSNMPInterfaceInfoKey() {
        return SNMPItemAdapter.class.getSimpleName() + ".SNMPInterfaceInfo";
    }

    public SNMPItem getItem(int index) {
        Log.d(SNMPItemAdapter.class.getName(), "getItem for index " + index);
        if (index < 0 || index >= snmpItems.size()) {
            Log.e(SNMPItemAdapter.class.getName(), "invalid index " + index);
            return null;
        }
        return snmpItems.get(index);
    }

    public void replaceItems(List<SNMPItem> items) {
        this.snmpItems.clear();
        this.snmpItems.addAll(items);
    }

    public void replaceItems(List<SNMPItem> items, Map<String, SNMPInterfaceInfo> snmpInterfaceInfo) {
        this.snmpItems.clear();
        this.snmpItems.addAll(items);
        this.snmpInterfaceInfo.clear();
        this.snmpInterfaceInfo.putAll(snmpInterfaceInfo);
    }

    @Override
    public int getItemCount() {
        return snmpItems.isEmpty() ? 1 : snmpItems.size();
    }

    public List<SNMPItem> getAllItems() {
        return Collections.unmodifiableList(snmpItems);
    }

    public Map<String, SNMPInterfaceInfo> getInterfaceInfos() {
        return Collections.unmodifiableMap(snmpInterfaceInfo);
    }

    public Context getContext() {
        return snmpItemDialog.getContext();
    }
}
