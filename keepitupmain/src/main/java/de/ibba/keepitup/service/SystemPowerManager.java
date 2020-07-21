package de.ibba.keepitup.service;

import android.content.Context;
import android.os.Build;
import android.os.PowerManager;

import de.ibba.keepitup.logging.Log;

public class SystemPowerManager implements IPowerManager {

    private final String packageName;
    private final PowerManager powerManager;

    public SystemPowerManager(Context context) {
        this.packageName = context.getPackageName();
        this.powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
    }

    @Override
    public boolean supportsBatteryOptimization() {
        boolean supportsBatteryOptimization = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
        Log.d(SystemPowerManager.class.getName(), "supportsBatteryOptimization: " + supportsBatteryOptimization);
        return supportsBatteryOptimization;
    }

    @Override
    public boolean isBatteryOptimized() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean isBatteryOptimized = !powerManager.isIgnoringBatteryOptimizations(packageName);
            Log.d(SystemPowerManager.class.getName(), "isBatteryOptimized: " + isBatteryOptimized);
            return isBatteryOptimized;
        }
        Log.d(SystemPowerManager.class.getName(), "isBatteryOptimized: false");
        return false;
    }
}
