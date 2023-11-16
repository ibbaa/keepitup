/*
 * Copyright (c) 2023. Alwin Ibba
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
import net.ibbaa.keepitup.ui.dialog.SuspensionIntervalsDialog;

public class SuspensionIntervalViewHolder extends RecyclerView.ViewHolder {

    private final SuspensionIntervalsDialog intervalsDialog;
    private final TextView noIntervalsText;
    private final CardView cardView;
    private final TextView intervalText;
    private final ImageView intervalDeleteImage;

    public SuspensionIntervalViewHolder(@NonNull View itemView, SuspensionIntervalsDialog intervalsDialog) {
        super(itemView);
        this.intervalsDialog = intervalsDialog;
        noIntervalsText = itemView.findViewById(R.id.textview_list_item_suspension_interval_no_interval);
        cardView = itemView.findViewById(R.id.cardview_list_item_suspension_interval);
        intervalText = itemView.findViewById(R.id.textview_list_item_suspension_interval);
        intervalDeleteImage = itemView.findViewById(R.id.imageview_list_item_suspension_interval_delete);
        intervalDeleteImage.setOnClickListener(this::onIntervalDeleteClicked);
    }

    public void setIntervalText(String text) {
        intervalText.setText(text);
    }

    public void onIntervalDeleteClicked(View view) {
        intervalsDialog.onIntervalDeleteClicked(view, getAdapterPosition());
    }

    public void showIntervalsCardView() {
        cardView.setVisibility(View.VISIBLE);
    }

    public void hideIntervalsCardView() {
        cardView.setVisibility(View.GONE);
    }

    public void showNoIntervalsTextView() {
        noIntervalsText.setVisibility(View.VISIBLE);
    }

    public void hideNoIntervalsTextView() {
        noIntervalsText.setVisibility(View.GONE);
    }
}
