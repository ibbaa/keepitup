package de.ibba.keepitup.ui;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import de.ibba.keepitup.R;

public class NetworkTaskEditErrorDialog extends DialogFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.DialogTheme);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_edit_network_task_error, container);
        prepareErrorMessages(view);
        prepareOkButton(view);
        return view;
    }

    private void prepareErrorMessages(View view) {
        Log.d(NetworkTaskEditErrorDialog.class.getName(), "prepareErrorMessages");
        GridLayout gridLayout = view.findViewById(R.id.gridlayout_dialog_edit_network_task_error);
        TextView labelText = new TextView(getContext());
        labelText.setId(View.generateViewId());
        labelText.setText("Field");
        labelText.setTypeface(null, Typeface.BOLD);
        GridLayout.LayoutParams labelTextParams = new GridLayout.LayoutParams();
        labelTextParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
        labelTextParams.width = GridLayout.LayoutParams.WRAP_CONTENT;
        labelTextParams.setGravity(Gravity.CENTER);
        labelTextParams.rightMargin = 10;
        labelTextParams.topMargin = 5;
        labelTextParams.columnSpec = GridLayout.spec(0);
        labelTextParams.rowSpec = GridLayout.spec(2);
        gridLayout.addView(labelText, labelTextParams);
        TextView messageText = new TextView(getContext());
        labelText.setId(View.generateViewId());
        messageText.setText("Message");
        messageText.setTypeface(null, Typeface.NORMAL);
        GridLayout.LayoutParams messageTextParams = new GridLayout.LayoutParams();
        messageTextParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
        messageTextParams.width = GridLayout.LayoutParams.WRAP_CONTENT;
        messageTextParams.setGravity(Gravity.CENTER);
        messageTextParams.leftMargin = 10;
        messageTextParams.topMargin = 5;
        messageTextParams.columnSpec = GridLayout.spec(1);
        messageTextParams.rowSpec = GridLayout.spec(2);
        gridLayout.addView(messageText, messageTextParams);
    }

    private void prepareOkButton(View view) {
        Log.d(NetworkTaskEditErrorDialog.class.getName(), "prepareOkButton");
        ImageView okImage = view.findViewById(R.id.imageview_dialog_edit_network_task_error_ok);
        GridLayout.LayoutParams okImageParams = new GridLayout.LayoutParams();
        okImageParams.columnSpec = GridLayout.spec(0, 2);
        okImageParams.rowSpec = GridLayout.spec(3);
        okImageParams.setGravity(Gravity.CENTER);
        okImage.setLayoutParams(okImageParams);
        okImage.setOnClickListener(this::onOkClicked);
    }

    private void onOkClicked(@SuppressWarnings("unused") View view) {
        Log.d(NetworkTaskEditErrorDialog.class.getName(), "onOkClicked");
        dismiss();
    }
}
