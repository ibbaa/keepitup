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

package net.ibbaa.keepitup.service;

import android.content.Context;
import android.os.Build;
import android.os.PowerManager;

import net.ibbaa.keepitup.logging.Log;

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
