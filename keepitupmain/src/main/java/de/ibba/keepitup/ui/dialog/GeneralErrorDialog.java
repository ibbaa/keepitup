package de.ibba.keepitup.ui.dialog;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Objects;

import de.ibba.keepitup.R;
import de.ibba.keepitup.logging.Log;
import de.ibba.keepitup.util.BundleUtil;

public class GeneralErrorDialog extends DialogFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(GeneralErrorDialog.class.getName(), "onCreate");
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.DialogTheme);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(GeneralErrorDialog.class.getName(), "onCreateView");
        View view = inflater.inflate(R.layout.dialog_general_error, container);
        String message = BundleUtil.stringFromBundle(GeneralErrorDialog.class.getSimpleName(), Objects.requireNonNull(getArguments()));
        prepareErrorMessage(view, message);
        prepareOkImageButton(view);
        return view;
    }

    public String getTypefaceStyleKey() {
        return GeneralErrorDialog.class.getSimpleName() + "TypefaceStyle";
    }

    private void prepareErrorMessage(View view, String message) {
        Log.d(GeneralErrorDialog.class.getName(), "prepareErrorMessage");
        TextView messageText = view.findViewById(R.id.textview_dialog_general_error_message);
        int style = Objects.requireNonNull(getArguments()).getInt(getTypefaceStyleKey(), Typeface.BOLD);
        messageText.setTypeface(null, style);
        messageText.setText(message);
    }

    private void prepareOkImageButton(View view) {
        Log.d(GeneralErrorDialog.class.getName(), "prepareOkImageButton");
        ImageView okImage = view.findViewById(R.id.imageview_dialog_general_error_ok);
        okImage.setOnClickListener(this::onOkClicked);
    }

    private void onOkClicked(@SuppressWarnings("unused") View view) {
        Log.d(GeneralErrorDialog.class.getName(), "onOkClicked");
        dismiss();
    }
}
