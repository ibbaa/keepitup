package de.ibba.keepitup.ui.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.text.DateFormat;
import java.util.Date;

import de.ibba.keepitup.BuildConfig;
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
        prepareContent(view);
        prepareOkImageButton(view);
        return view;
    }

    private void prepareContent(View view) {
        Log.d(InfoDialog.class.getName(), "prepareContent");
        TextView versionText = view.findViewById(R.id.textview_dialog_info_version);
        versionText.setText(BuildConfig.VERSION_NAME);
        TextView buildTypeText = view.findViewById(R.id.textview_dialog_info_build_type);
        buildTypeText.setText(BuildConfig.BUILD_TYPE.toUpperCase());
        TextView buildTimeText = view.findViewById(R.id.textview_dialog_info_build_timestamp_);
        String buildTime = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(new Date(BuildConfig.TIMESTAMP));
        buildTimeText.setText(buildTime);
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
