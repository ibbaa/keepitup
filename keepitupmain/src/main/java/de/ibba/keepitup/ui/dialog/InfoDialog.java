package de.ibba.keepitup.ui.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import de.ibba.keepitup.R;
import de.ibba.keepitup.logging.Log;

public class InfoDialog extends DialogFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(InfoDialog.class.getName(), "onCreate");
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.DialogTheme);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(InfoDialog.class.getName(), "onCreateView");
        View view = inflater.inflate(R.layout.dialog_info, container);
        prepareOkImageButton(view);
        return view;
    }

    private void prepareOkImageButton(View view) {
        Log.d(InfoDialog.class.getName(), "prepareOkImageButton");
        ImageView okImage = view.findViewById(R.id.imageview_dialog_info_ok);
        okImage.setOnClickListener(this::onOkClicked);
    }

    private void onOkClicked(@SuppressWarnings("unused") View view) {
        Log.d(InfoDialog.class.getName(), "onOkClicked");
        dismiss();
    }
}
