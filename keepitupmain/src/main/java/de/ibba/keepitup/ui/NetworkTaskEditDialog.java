package de.ibba.keepitup.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import de.ibba.keepitup.R;

public class NetworkTaskEditDialog extends DialogFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.DialogTheme);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_edit_network_task, container);
        ImageView okImage = view.findViewById(R.id.imageview_dialog_edit_network_task_ok);
        ImageView cancelImage = view.findViewById(R.id.imageview_dialog_edit_network_task_ok);
        return view;
    }

    private void onOkClicked(View view) {

    }

    private void onCancelClicked(View view) {

    }
}
