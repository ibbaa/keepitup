/*
 * Copyright (c) 2025 Alwin Ibba
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

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.URLSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.service.IPowerManager;
import net.ibbaa.keepitup.service.SystemPowerManager;
import net.ibbaa.keepitup.ui.BatteryOptimizationSupport;

@SuppressWarnings({"unused"})
public class BatteryOptimizationDialog extends DialogFragmentBase {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(BatteryOptimizationDialog.class.getName(), "onCreate");
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.DialogTheme);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(BatteryOptimizationDialog.class.getName(), "onCreateView");
        View view = inflater.inflate(R.layout.dialog_battery_optimization, container);
        initEdgeToEdgeInsets(view);
        prepareBatteryOptimizationInfo(view);
        prepareBatteryOptimizationLink(view);
        prepareOkImageButton(view);
        return view;
    }

    private void prepareBatteryOptimizationInfo(View view) {
        Log.d(BatteryOptimizationDialog.class.getName(), "prepareBatteryOptimizationInfo");
        String active = getPowerManager().isBatteryOptimized() ? getResources().getString(R.string.text_dialog_battery_optimization_info_active) : getResources().getString(R.string.text_dialog_battery_optimization_info_not_active);
        String formattedInfo = getResources().getString(R.string.text_dialog_battery_optimization_info, active);
        TextView infoText = view.findViewById(R.id.textview_dialog_battery_optimization_info);
        infoText.setText(formattedInfo);
    }

    private void prepareBatteryOptimizationLink(View view) {
        Log.d(BatteryOptimizationDialog.class.getName(), "prepareBatteryOptimizationLink");
        String link = getResources().getString(R.string.text_dialog_battery_optimization_link);
        TextView linkText = view.findViewById(R.id.textview_dialog_battery_optimization_link);
        SpannableString spannableLink = new SpannableString(link);
        spannableLink.setSpan(new URLSpan(""), 0, spannableLink.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        linkText.setText(spannableLink, TextView.BufferType.SPANNABLE);
        linkText.setOnClickListener(this::onBatteryOptimizationLinkClicked);
    }

    private void prepareOkImageButton(View view) {
        Log.d(BatteryOptimizationDialog.class.getName(), "prepareOkImageButton");
        ImageView okImage = view.findViewById(R.id.imageview_dialog_battery_optimization_ok);
        okImage.setOnClickListener(this::onOkClicked);
    }

    private void onBatteryOptimizationLinkClicked(View view) {
        Log.d(BatteryOptimizationDialog.class.getName(), "onBatteryOptimizationLinkClicked");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
            startActivity(intent);
        }
    }

    private void onOkClicked(View view) {
        Log.d(BatteryOptimizationDialog.class.getName(), "onOkClicked");
        BatteryOptimizationSupport batteryOptimizationSupport = getBatteryOptimizationSupport();
        if (batteryOptimizationSupport != null) {
            batteryOptimizationSupport.onBatteryOptimizationDialogOkClicked(this);
        } else {
            Log.e(SettingsInputDialog.class.getName(), "batteryOptimizationSupport is null");
            dismiss();
        }
    }

    private IPowerManager getPowerManager() {
        Log.d(BatteryOptimizationDialog.class.getName(), "getPowerManager");
        BatteryOptimizationSupport batteryOptimizationSupport = getBatteryOptimizationSupport();
        if (batteryOptimizationSupport != null) {
            return batteryOptimizationSupport.getPowerManager();
        }
        Log.e(BatteryOptimizationDialog.class.getName(), "batteryOptimizationSupport is null");
        return new SystemPowerManager(requireContext());
    }

    private BatteryOptimizationSupport getBatteryOptimizationSupport() {
        Log.d(BatteryOptimizationDialog.class.getName(), "getBatteryOptimizationSupport");
        Activity activity = getActivity();
        if (activity == null) {
            Log.e(BatteryOptimizationDialog.class.getName(), "getBatteryOptimizationSupport, activity is null");
            return null;
        }
        if (!(activity instanceof BatteryOptimizationSupport)) {
            Log.e(BatteryOptimizationDialog.class.getName(), "getBatteryOptimizationSupport, activity is not an instance of " + BatteryOptimizationSupport.class.getSimpleName());
            return null;
        }
        return (BatteryOptimizationSupport) activity;
    }
}
