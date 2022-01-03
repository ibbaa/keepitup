/*
 * Copyright (c) 2022. Alwin Ibba
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

package net.ibbaa.keepitup.ui.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import net.ibbaa.keepitup.BuildConfig;
import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.util.BundleUtil;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

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

    private void onLicenseClicked(View view) {
        Log.d(InfoDialog.class.getName(), "onLicenseClicked");
        RawTextDialog licenseDialog = new RawTextDialog();
        Bundle bundle = BundleUtil.stringToBundle(getResources().getString(R.string.dialog_info_copyright_key), getCopyrightText());
        bundle.putInt(licenseDialog.getResourceIdKey(), R.raw.license);
        licenseDialog.setArguments(bundle);
        Log.d(InfoDialog.class.getName(), "Opening license dialog.");
        licenseDialog.show(getParentFragmentManager(), RawTextDialog.class.getName());
    }

    private void onOkClicked(View view) {
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
