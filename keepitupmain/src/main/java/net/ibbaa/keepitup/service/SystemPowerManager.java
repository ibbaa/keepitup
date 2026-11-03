/*
 * Copyright (c) 2026 Alwin Ibba
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

package net.ibbaa.keepitup.service;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.PowerManager;

import net.ibbaa.keepitup.logging.Log;

public class SystemPowerManager implements IPowerManager {

    private final String packageName;
    private final PowerManager powerManager;
    private final ActivityManager activityManager;

    public SystemPowerManager(Context context) {
        this.packageName = context.getPackageName();
        this.powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        this.activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    }

    @Override
    public boolean isBatteryOptimized() {
        boolean isBatteryOptimized = !powerManager.isIgnoringBatteryOptimizations(packageName);
        Log.d(SystemPowerManager.class.getName(), "isBatteryOptimized: " + isBatteryOptimized);
        return isBatteryOptimized;
    }

    @Override
    public boolean isBackgroundRestricted() {
        boolean isBackgroundRestricted = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && activityManager.isBackgroundRestricted();
        Log.d(SystemPowerManager.class.getName(), "isBackgroundRestricted: " + isBackgroundRestricted);
        return isBackgroundRestricted;
    }

    @Override
    public boolean usesAppBatteryUsageSettings() {
        boolean usesAppBatteryUsageSettings = Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM;
        Log.d(SystemPowerManager.class.getName(), "usesAppBatteryUsageSettings: " + usesAppBatteryUsageSettings);
        return usesAppBatteryUsageSettings;
    }
}
