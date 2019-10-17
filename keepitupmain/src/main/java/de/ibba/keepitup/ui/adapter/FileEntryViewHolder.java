package de.ibba.keepitup.ui.adapter;

import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.ibba.keepitup.R;

public class FileEntryViewHolder extends RecyclerView.ViewHolder {

    private final TextView fileNameText;

    public FileEntryViewHolder(@NonNull View itemView) {
        super(itemView);
        fileNameText = itemView.findViewById(R.id.textview_list_item_file_entry_name);
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
}
