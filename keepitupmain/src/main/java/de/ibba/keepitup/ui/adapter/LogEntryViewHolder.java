package de.ibba.keepitup.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import de.ibba.keepitup.R;

public class LogEntryViewHolder extends RecyclerView.ViewHolder {

    private final TextView noLogText;
    private final CardView cardView;
    private final TextView titleText;
    private final TextView successText;
    private final TextView timestampText;
    private final TextView messageText;

    public LogEntryViewHolder(@NonNull View itemView) {
        super(itemView);
        noLogText = itemView.findViewById(R.id.textview_list_item_log_entry_no_log);
        cardView = itemView.findViewById(R.id.cardview_list_item_log_entry);
        titleText = itemView.findViewById(R.id.textview_list_item_log_entry_title);
        successText = itemView.findViewById(R.id.textview_list_item_log_entry_success);
        timestampText = itemView.findViewById(R.id.textview_list_item_log_entry_timestamp);
        messageText = itemView.findViewById(R.id.textview_list_item_log_entry_message);
    }

    public void setTitleText(String title) {
        titleText.setText(title);
    }

    public void setSuccessText(String success) {
        successText.setText(success);
    }

    public void setTimestampText(String timestamp) {
        timestampText.setText(timestamp);
    }

    public void setMessageText(String message) {
        messageText.setText(message);
    }

    public void setNoLogText(String nolog) {
        noLogText.setText(nolog);
    }

    public void showNoLogTextView() {
        noLogText.setVisibility(View.VISIBLE);
        cardView.setVisibility(View.GONE);
    }

    public void hideNoLogTextView() {
        noLogText.setVisibility(View.GONE);
        cardView.setVisibility(View.VISIBLE);
    }
}
