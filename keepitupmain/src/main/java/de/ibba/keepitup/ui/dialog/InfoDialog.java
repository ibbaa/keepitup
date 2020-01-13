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
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Objects;

import de.ibba.keepitup.BuildConfig;
import de.ibba.keepitup.R;
import de.ibba.keepitup.logging.Log;
import de.ibba.keepitup.util.BundleUtil;

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
        prepareBuildInfo(view);
        prepareCopyright(view);
        prepareLicense(view);
        prepareOkImageButton(view);
        return view;
    }

    private void prepareBuildInfo(View view) {
        Log.d(InfoDialog.class.getName(), "prepareBuildInfo");
        TextView versionText = view.findViewById(R.id.textview_dialog_info_version);
        versionText.setText(BuildConfig.VERSION_NAME);
        TextView buildTypeText = view.findViewById(R.id.textview_dialog_info_build_type);
        buildTypeText.setText(BuildConfig.BUILD_TYPE.toUpperCase());
        TextView buildTimeText = view.findViewById(R.id.textview_dialog_info_build_timestamp_);
        String buildTime = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(new Date(BuildConfig.TIMESTAMP));
        buildTimeText.setText(buildTime);
    }

    private void prepareCopyright(View view) {
        Log.d(InfoDialog.class.getName(), "prepareCopyright");
        TextView copyrightText = view.findViewById(R.id.textview_dialog_info_copyright);
        copyrightText.setText(getCopyrightText());
    }

    private void prepareLicense(View view) {
        Log.d(InfoDialog.class.getName(), "prepareLicense");
        TextView licenseText = view.findViewById(R.id.textview_dialog_info_license);
        licenseText.setOnClickListener(this::onLicenseClicked);
    }

    private void prepareOkImageButton(View view) {
        Log.d(InfoDialog.class.getName(), "prepareOkImageButton");
        ImageView okImage = view.findViewById(R.id.imageview_dialog_info_ok);
        okImage.setOnClickListener(this::onOkClicked);
    }

    private void onLicenseClicked(@SuppressWarnings("unused") View view) {
        Log.d(InfoDialog.class.getName(), "onLicenseClicked");
        RawTextDialog licenseDialog = new RawTextDialog();
        Bundle bundle = BundleUtil.messageToBundle(getResources().getString(R.string.dialog_info_copyright_key), getCopyrightText());
        bundle.putInt(licenseDialog.getResourceIdKey(), R.raw.license);
        licenseDialog.setArguments(bundle);
        Log.d(InfoDialog.class.getName(), "Opening license dialog.");
        licenseDialog.show(Objects.requireNonNull(getFragmentManager()), RawTextDialog.class.getName());
    }

    private void onOkClicked(@SuppressWarnings("unused") View view) {
        Log.d(InfoDialog.class.getName(), "onOkClicked");
        dismiss();
    }

    private String getCopyrightText() {
        Calendar buildDate = new GregorianCalendar();
        buildDate.setTime(new Date(BuildConfig.TIMESTAMP));
        int buildYear = buildDate.get(GregorianCalendar.YEAR);
        int releaseYear = BuildConfig.RELEASE_YEAR;
        String copyrightYear = String.valueOf(releaseYear);
        if (buildYear > releaseYear) {
            copyrightYear += " - " + buildYear;
        }
        return String.format(getResources().getString(R.string.text_dialog_info_copyright), copyrightYear);
    }
}
