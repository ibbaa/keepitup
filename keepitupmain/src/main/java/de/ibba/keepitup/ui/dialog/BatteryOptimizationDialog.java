package de.ibba.keepitup.ui.dialog;

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

import de.ibba.keepitup.R;
import de.ibba.keepitup.logging.Log;
import de.ibba.keepitup.service.IPowerManager;
import de.ibba.keepitup.ui.BatteryOptimizationAware;
import de.ibba.keepitup.ui.BatteryOptimizationSupport;

public class BatteryOptimizationDialog extends DialogFragment implements BatteryOptimizationAware {

    private BatteryOptimizationSupport batteryOptimizationSupport;

    public BatteryOptimizationDialog() {

    }

    public BatteryOptimizationDialog(BatteryOptimizationSupport batteryOptimizationSupport) {
        this.batteryOptimizationSupport = batteryOptimizationSupport;
    }

    @Override
    public void setBatteryOptimizationSupport(BatteryOptimizationSupport batteryOptimizationSupport) {
        this.batteryOptimizationSupport = batteryOptimizationSupport;
    }

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

    private void onBatteryOptimizationLinkClicked(@SuppressWarnings("unused") View view) {
        Log.d(BatteryOptimizationDialog.class.getName(), "onBatteryOptimizationLinkClicked");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
            startActivity(intent);
        }
    }

    private void onOkClicked(@SuppressWarnings("unused") View view) {
        Log.d(BatteryOptimizationDialog.class.getName(), "onOkClicked");
        batteryOptimizationSupport.onBatteryOptimizationDialogOkClicked(this);
    }

    private IPowerManager getPowerManager() {
        Log.d(BatteryOptimizationDialog.class.getName(), "getPowerManager");
        return batteryOptimizationSupport.getPowerManager();
    }
}
