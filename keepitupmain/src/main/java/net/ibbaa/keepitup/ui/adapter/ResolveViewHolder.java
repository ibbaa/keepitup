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
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.ui.dialog.ResolvesDialog;

@SuppressWarnings({"FieldCanBeLocal"})
public class ResolveViewHolder extends RecyclerView.ViewHolder {

    private final ResolvesDialog resolvesDialog;
    private final TextView noResolvesText;
    private final CardView cardView;
    private final TextView resolveMatch;
    private final TextView resolveConnectTo;
    private final ImageView resolveDeleteImage;

    public ResolveViewHolder(@NonNull View itemView, ResolvesDialog resolvesDialog) {
        super(itemView);
        this.resolvesDialog = resolvesDialog;
        noResolvesText = itemView.findViewById(R.id.textview_list_item_resolve_no_resolve);
        cardView = itemView.findViewById(R.id.cardview_list_item_resolve);
        cardView.setOnClickListener(this::onResolveOpenClicked);
        resolveMatch = itemView.findViewById(R.id.textview_list_item_resolve_match);
        resolveConnectTo = itemView.findViewById(R.id.textview_list_item_resolve_connect_to);
        resolveDeleteImage = itemView.findViewById(R.id.imageview_list_item_resolve_delete);
        resolveDeleteImage.setOnClickListener(this::onResolveDeleteClicked);
    }

    public void setResolveMatchText(String text) {
        resolveMatch.setText(text);
    }

    public void setResolveConnectToText(String text) {
        resolveConnectTo.setText(text);
    }

    public void onResolveOpenClicked(View view) {
        resolvesDialog.onResolveOpenClicked(view, getBindingAdapterPosition());
    }

    public void onResolveDeleteClicked(View view) {
        resolvesDialog.onResolveDeleteClicked(view, getBindingAdapterPosition());
    }

    public void showResolvesCardView() {
        cardView.setVisibility(View.VISIBLE);
    }

    public void hideResolvesCardView() {
        cardView.setVisibility(View.GONE);
    }

    public void showNoResolvesTextView() {
        noResolvesText.setVisibility(View.VISIBLE);
    }

    public void hideNoResolvesTextView() {
        noResolvesText.setVisibility(View.GONE);
    }
}
