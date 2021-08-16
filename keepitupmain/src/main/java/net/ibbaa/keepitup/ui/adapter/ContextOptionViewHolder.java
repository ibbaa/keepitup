package net.ibbaa.keepitup.ui.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.ui.dialog.ContextOptionsDialog;

public class ContextOptionViewHolder extends RecyclerView.ViewHolder {

    private final ContextOptionsDialog contextOptionsDialog;
    private final CardView contextOptionCardView;
    private final TextView contextOptionText;

    public ContextOptionViewHolder(@NonNull View itemView, ContextOptionsDialog contextOptionsDialog) {
        super(itemView);
        this.contextOptionsDialog = contextOptionsDialog;
        contextOptionCardView = itemView.findViewById(R.id.cardview_list_item_context_option);
        contextOptionCardView.setOnClickListener(this::onContextOptionClicked);
        contextOptionText = itemView.findViewById(R.id.textview_list_item_context_option_name);
    }

    public void setContextOptionText(String text) {
        contextOptionText.setText(text);
    }

    public void setContextOptionEntrySelected() {
        contextOptionCardView.setCardElevation(getResources().getDimension(R.dimen.cardview_list_item_context_option_card_elevation_selected));
    }

    public void setContextOptionEntryUnselected() {
        contextOptionCardView.setCardElevation(getResources().getDimension(R.dimen.cardview_list_item_context_option_card_elevation));
    }

    private void onContextOptionClicked(View view) {
        contextOptionsDialog.onContextOptionClicked(view, getAdapterPosition());
    }

    private Context getContext() {
        return contextOptionsDialog.getActivity();
    }

    private Resources getResources() {
        return getContext().getResources();
    }
}
