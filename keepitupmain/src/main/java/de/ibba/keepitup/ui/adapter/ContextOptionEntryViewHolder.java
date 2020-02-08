package de.ibba.keepitup.ui.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import de.ibba.keepitup.R;
import de.ibba.keepitup.ui.dialog.ContextOptionsDialog;

public class ContextOptionEntryViewHolder extends RecyclerView.ViewHolder {

    private final ContextOptionsDialog contextOptionsDialog;
    private final CardView contextOptionEntryCardView;
    private final TextView contextOptionText;

    public ContextOptionEntryViewHolder(@NonNull View itemView, ContextOptionsDialog contextOptionsDialog) {
        super(itemView);
        this.contextOptionsDialog = contextOptionsDialog;
        contextOptionEntryCardView = itemView.findViewById(R.id.cardview_list_item_context_option_entry);
        contextOptionEntryCardView.setOnClickListener(this::onContextOptionEntryClicked);
        contextOptionText = itemView.findViewById(R.id.textview_list_item_context_option_entry_name);
    }

    public void setContextOptionText(String text) {
        contextOptionText.setText(text);
    }

    public void setContextOptionEntrySelected() {
        contextOptionEntryCardView.setCardElevation(getResources().getDimension(R.dimen.cardview_list_item_context_option_entry_card_elevation_selected));
    }

    public void setContextOptionEntryUnselected() {
        contextOptionEntryCardView.setCardElevation(getResources().getDimension(R.dimen.cardview_list_item_context_option_entry_card_elevation));
    }

    private void onContextOptionEntryClicked(View view) {
        contextOptionsDialog.onContextOptionEntryClicked(view, getAdapterPosition());
    }

    private Context getContext() {
        return contextOptionsDialog.getActivity();
    }

    private Resources getResources() {
        return getContext().getResources();
    }
}
