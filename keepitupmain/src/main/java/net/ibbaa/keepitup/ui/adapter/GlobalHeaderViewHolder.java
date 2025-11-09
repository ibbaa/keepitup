/*
 * Copyright (c) 2025 Alwin Ibba
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
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.ui.dialog.GlobalHeadersDialog;

@SuppressWarnings({"FieldCanBeLocal"})
public class GlobalHeaderViewHolder extends RecyclerView.ViewHolder {

    private final GlobalHeadersDialog headersDialog;
    private final TextView noHeadersText;
    private final CardView cardView;
    private final TextView headerName;
    private final TextView headerValue;
    private final ImageView headerDeleteImage;

    public GlobalHeaderViewHolder(@NonNull View itemView, GlobalHeadersDialog headersDialog) {
        super(itemView);
        this.headersDialog = headersDialog;
        noHeadersText = itemView.findViewById(R.id.textview_list_item_global_header_no_header);
        cardView = itemView.findViewById(R.id.cardview_list_item_global_header);
        cardView.setOnClickListener(this::onHeaderOpenClicked);
        headerName = itemView.findViewById(R.id.textview_list_item_global_header_name);
        headerValue = itemView.findViewById(R.id.textview_list_item_global_header_value);
        headerDeleteImage = itemView.findViewById(R.id.imageview_list_item_global_header_delete);
        headerDeleteImage.setOnClickListener(this::onHeaderDeleteClicked);
    }

    public void setHeaderNameText(String text) {
        headerName.setText(text);
    }

    public void setHeaderValueText(String text) {
        headerValue.setText(text);
    }

    public void onHeaderOpenClicked(View view) {
        headersDialog.onHeaderOpenClicked(view, getBindingAdapterPosition());
    }

    public void onHeaderDeleteClicked(View view) {
        headersDialog.onHeaderDeleteClicked(view, getBindingAdapterPosition());
    }

    public void showHeadersCardView() {
        cardView.setVisibility(View.VISIBLE);
    }

    public void hideHeadersCardView() {
        cardView.setVisibility(View.GONE);
    }

    public void showNoHeadersTextView() {
        noHeadersText.setVisibility(View.VISIBLE);
    }

    public void hideNoHeadersTextView() {
        noHeadersText.setVisibility(View.GONE);
    }
}
