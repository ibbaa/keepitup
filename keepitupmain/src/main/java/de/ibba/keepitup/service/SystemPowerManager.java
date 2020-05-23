package de.ibba.keepitup.service;

import android.content.Context;
import android.os.Build;
import android.os.PowerManager;

public class SystemPowerManager implements IPowerManager {

    @Override
    public boolean supportsBatteryOptimization() {
        return Build.VERSION.SDK_INT >= 23;
    }

    @Override
    public boolean isBatteryOptimized(Context context) {
        if (Build.VERSION.SDK_INT >= 23) {
            PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            return !powerManager.isIgnoringBatteryOptimizations(context.getPackageName());
        }
        /*Intent myIntent = new Intent();
        myIntent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
        startActivity(myIntent);*/
        return false;
    }
}
