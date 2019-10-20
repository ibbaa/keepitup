package de.ibba.keepitup.ui.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import de.ibba.keepitup.R;
import de.ibba.keepitup.ui.dialog.FolderChooseEditDialog;

public class FileEntryViewHolder extends RecyclerView.ViewHolder {

    private final FolderChooseEditDialog folderChooseEditDialog;
    private final CardView fileEntryCardView;
    private final TextView fileNameText;

    public FileEntryViewHolder(@NonNull View itemView, FolderChooseEditDialog folderChooseEditDialog) {
        super(itemView);
        this.folderChooseEditDialog = folderChooseEditDialog;
        fileEntryCardView = itemView.findViewById(R.id.cardview_list_item_file_entry);
        fileEntryCardView.setOnClickListener(this::onFileEntryClicked);
        fileNameText = itemView.findViewById(R.id.textview_list_item_file_entry_name);
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

    private void onFileEntryClicked(View view) {
        folderChooseEditDialog.onFileEntryClicked(view, getAdapterPosition());
    }

    private Context getContext() {
        return folderChooseEditDialog.getActivity();
    }

    private Resources getResources() {
        return getContext().getResources();
    }
}
