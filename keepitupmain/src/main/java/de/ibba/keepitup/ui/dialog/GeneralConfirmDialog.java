package de.ibba.keepitup.ui.dialog;

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

public class GeneralConfirmDialog extends DialogFragment {

    private boolean wasConfirmed;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(GeneralConfirmDialog.class.getName(), "onCreate");
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.DialogTheme);
        wasConfirmed = false;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(GeneralConfirmDialog.class.getName(), "onCreateView");
        View view = inflater.inflate(R.layout.dialog_general_confirm, container);
        String message = BundleUtil.bundleToMessage(GeneralConfirmDialog.class.getSimpleName(), Objects.requireNonNull(getArguments()));
        prepareConfirmMessage(view, message);
        prepareOkCancelImageButtons(view);
        return view;
    }

    public boolean wasConfirmed() {
        return wasConfirmed;
    }

    private void prepareConfirmMessage(View view, String message) {
        Log.d(GeneralConfirmDialog.class.getName(), "prepareConfirmMessage");
        TextView messageText = view.findViewById(R.id.textview_dialog_general_confirm_message);
        messageText.setText(message);
    }

    private void prepareOkCancelImageButtons(View view) {
        Log.d(GeneralConfirmDialog.class.getName(), "prepareOkCancelImageButtons");
        ImageView okImage = view.findViewById(R.id.imageview_dialog_general_confirm_ok);
        ImageView cancelImage = view.findViewById(R.id.imageview_dialog_general_confirm_cancel);
        okImage.setOnClickListener(this::onOkClicked);
        cancelImage.setOnClickListener(this::onCancelClicked);
    }

    private void onOkClicked(@SuppressWarnings("unused") View view) {
        Log.d(GeneralConfirmDialog.class.getName(), "onOkClicked");
        wasConfirmed = true;
        dismiss();
    }

    private void onCancelClicked(@SuppressWarnings("unused") View view) {
        Log.d(GeneralConfirmDialog.class.getName(), "onCancelClicked");
        wasConfirmed = false;
        dismiss();
    }
}
