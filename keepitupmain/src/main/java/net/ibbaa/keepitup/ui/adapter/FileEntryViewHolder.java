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

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.ui.dialog.FileChooseDialog;

public class FileEntryViewHolder extends RecyclerView.ViewHolder {

    private final FileChooseDialog folderChooseDialog;
    private final CardView fileEntryCardView;
    private final ImageView fileSymbolImage;
    private final TextView fileNameText;
    private final ImageView fileOpenImage;

    public FileEntryViewHolder(@NonNull View itemView, FileChooseDialog folderChooseDialog) {
        super(itemView);
        this.folderChooseDialog = folderChooseDialog;
        fileEntryCardView = itemView.findViewById(R.id.cardview_list_item_file_entry);
        fileEntryCardView.setOnClickListener(this::onFileEntryClicked);
        fileSymbolImage = itemView.findViewById(R.id.imageview_list_item_file_entry_symbol);
        fileNameText = itemView.findViewById(R.id.textview_list_item_file_entry_name);
        fileOpenImage = itemView.findViewById(R.id.imageview_list_item_file_entry_open);
        fileOpenImage.setOnClickListener(this::onFileOpenClicked);
    }

    public void setFileEntrySelected() {
        fileEntryCardView.setCardElevation(getResources().getDimension(R.dimen.cardview_list_item_file_entry_card_elevation_selected));
    }

    public void setFileEntryUnselected() {
        fileEntryCardView.setCardElevation(getResources().getDimension(R.dimen.cardview_list_item_file_entry_card_elevation));
    }

    public void setFileNameText(String file) {
        fileNameText.setText(file);
    }

    public void setFileNameTextNormal() {
        fileNameText.setTypeface(null, Typeface.NORMAL);
    }

    public void setFileNameTextBold() {
        fileNameText.setTypeface(null, Typeface.BOLD);
    }

    public void setFileOpenImageVisible() {
        fileOpenImage.setVisibility(View.VISIBLE);
    }

    public void setFileOpenImageInvisible() {
        fileOpenImage.setVisibility(View.INVISIBLE);
    }

    public void setFileSymbolImage(String description, int image) {
        fileSymbolImage.setImageResource(image);
        fileSymbolImage.setContentDescription(description);
    }

    private void onFileEntryClicked(View view) {
        folderChooseDialog.onFileEntryClicked(view, getAdapterPosition());
    }

    private void onFileOpenClicked(View view) {
        folderChooseDialog.onFileOpenClicked(view, getAdapterPosition());
    }

    private Context getContext() {
        return folderChooseDialog.getActivity();
    }

    private Resources getResources() {
        return getContext().getResources();
    }
}
