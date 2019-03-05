package de.ibba.keepitup.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Objects;

import de.ibba.keepitup.R;
import de.ibba.keepitup.util.BundleUtil;

public class GeneralErrorDialog extends DialogFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.DialogTheme);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.general_error, container);
        String message = BundleUtil.bundleToMessage(GeneralErrorDialog.class.getSimpleName(), Objects.requireNonNull(getArguments()));
        prepareErrorMessage(view, message);
        prepareOkButton(view);
        return view;
    }

    private void prepareErrorMessage(View view, String message) {
        Log.d(GeneralErrorDialog.class.getName(), "prepareErrorMessage");
        TextView messageText = view.findViewById(R.id.textview_general_error_message);
        messageText.setText(message);
    }

    private void prepareOkButton(View view) {
        Log.d(GeneralErrorDialog.class.getName(), "prepareOkButton");
        ImageView okImage = view.findViewById(R.id.imageview_general_error_ok);
        okImage.setOnClickListener(this::onOkClicked);
    }

    private void onOkClicked(@SuppressWarnings("unused") View view) {
        Log.d(GeneralErrorDialog.class.getName(), "onOkClicked");
        dismiss();
    }
}
