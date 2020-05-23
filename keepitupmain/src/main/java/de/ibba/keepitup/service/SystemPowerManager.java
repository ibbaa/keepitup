package de.ibba.keepitup.service;

import android.content.Context;
import android.os.Build;
import android.os.PowerManager;

public class SystemPowerManager implements IPowerManager {

    private String packageName;
    private PowerManager powerManager;

    public SystemPowerManager(Context context) {
        this.packageName = context.getPackageName();
        this.powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
    }

    @Override
    public boolean supportsBatteryOptimization() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    @Override
    public boolean isBatteryOptimized() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return !powerManager.isIgnoringBatteryOptimizations(packageName);
        }
        return false;
    }
}
