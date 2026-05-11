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

import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.checkbox.MaterialCheckBox;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.ui.dialog.SNMPItemDialog;

@SuppressWarnings({"FieldCanBeLocal"})
public class SNMPItemViewHolder extends RecyclerView.ViewHolder {

    private final SNMPItemDialog snmpItemDialog;
    private final TextView noItemsText;
    private final CardView cardView;
    private final MaterialCheckBox monitoredCheckbox;
    private final TextView nameText;
    private final TextView secondText;

    public SNMPItemViewHolder(@NonNull View itemView, SNMPItemDialog snmpItemDialog) {
        super(itemView);
        this.snmpItemDialog = snmpItemDialog;
        noItemsText = itemView.findViewById(R.id.textview_list_item_snmp_item_no_item);
        cardView = itemView.findViewById(R.id.cardview_list_item_snmp_item);
        monitoredCheckbox = itemView.findViewById(R.id.checkbox_list_item_snmp_item_monitored);
        nameText = itemView.findViewById(R.id.textview_list_item_snmp_item_name);
        secondText = itemView.findViewById(R.id.textview_list_item_snmp_item_second);
    }

    public void setNameText(String text) {
        nameText.setText(text);
    }

    public void setSecondText(String text) {
        secondText.setText(text);
    }

    public void setChecked(boolean checked) {
        monitoredCheckbox.setChecked(checked);
    }

    public void setOnMonitoredChangedListener(CompoundButton.OnCheckedChangeListener listener) {
        monitoredCheckbox.setOnCheckedChangeListener(listener);
    }

    public void setNoItemsText(String text) {
        noItemsText.setText(text);
    }

    public void showNoItemsTextView() {
        noItemsText.setVisibility(View.VISIBLE);
    }

    public void hideNoItemsTextView() {
        noItemsText.setVisibility(View.GONE);
    }

    public void showItemCardView() {
        cardView.setVisibility(View.VISIBLE);
    }

    public void hideItemCardView() {
        cardView.setVisibility(View.GONE);
    }
}
