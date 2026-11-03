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

package net.ibbaa.keepitup.test.mock;

import net.ibbaa.keepitup.service.IPowerManager;

@SuppressWarnings({"unused"})
public class MockPowerManager implements IPowerManager {

    private boolean batteryOptimized;
    private boolean backgroundRestricted;
    private boolean usesAppBatteryUsageSettings;

    public MockPowerManager() {
        reset();
    }

    public void reset() {
        this.batteryOptimized = true;
        this.backgroundRestricted = false;
        this.usesAppBatteryUsageSettings = false;
    }

    public void setBatteryOptimized(boolean batteryOptimized) {
        this.batteryOptimized = batteryOptimized;
    }

    public void setBackgroundRestricted(boolean backgroundRestricted) {
        this.backgroundRestricted = backgroundRestricted;
    }

    public void setUsesAppBatteryUsageSettings(boolean usesAppBatteryUsageSettings) {
        this.usesAppBatteryUsageSettings = usesAppBatteryUsageSettings;
    }

    @Override
    public boolean isBatteryOptimized() {
        return batteryOptimized;
    }

    @Override
    public boolean isBackgroundRestricted() {
        return backgroundRestricted;
    }

    @Override
    public boolean usesAppBatteryUsageSettings() {
        return usesAppBatteryUsageSettings;
    }
}
