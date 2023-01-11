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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import net.ibbaa.keepitup.R;

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

    public void showLogEntryCardView() {
        cardView.setVisibility(View.VISIBLE);
    }

    public void hideLogEntryCardView() {
        cardView.setVisibility(View.GONE);
    }

    public void showNoLogTextView() {
        noLogText.setVisibility(View.VISIBLE);
    }

    public void hideNoLogTextView() {
        noLogText.setVisibility(View.GONE);
    }
}
