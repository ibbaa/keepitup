package de.ibba.keepitup.service;

import android.content.Context;

public interface IPowerManager {

    boolean supportsBatteryOptimization();

    boolean isBatteryOptimized(Context context);
}
