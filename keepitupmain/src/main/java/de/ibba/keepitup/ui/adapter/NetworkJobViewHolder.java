package de.ibba.keepitup.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import de.ibba.keepitup.R;

public class NetworkJobViewHolder extends RecyclerView.ViewHolder {

    private TextView addressText;

    public NetworkJobViewHolder(@NonNull View itemView) {
        super(itemView);
        addressText = (TextView) itemView.findViewById(R.id.list_item_network_job_address);
    }

    public void setAddress(String address) {
        addressText.setText(address);
    }
}
