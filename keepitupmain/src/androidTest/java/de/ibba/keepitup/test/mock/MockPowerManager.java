package de.ibba.keepitup.test.mock;

import android.content.Context;

import de.ibba.keepitup.service.IPowerManager;

public class MockPowerManager implements IPowerManager {

    private boolean supportsBatteryOptimization;
    private boolean batteryOptimized;

    public MockPowerManager() {
        this.supportsBatteryOptimization = true;
        this.batteryOptimized = true;
    }

    public void reset() {
        this.supportsBatteryOptimization = true;
        this.batteryOptimized = true;
    }

    public void setSupportsBatteryOptimization(boolean supportsBatteryOptimization) {
        this.supportsBatteryOptimization = supportsBatteryOptimization;
    }

    public void setBatteryOptimized(boolean batteryOptimized) {
        this.batteryOptimized = batteryOptimized;
    }

    @Override
    public boolean supportsBatteryOptimization() {
        return supportsBatteryOptimization;
    }

    @Override
    public boolean isBatteryOptimized(Context context) {
        return batteryOptimized;
    }
}
