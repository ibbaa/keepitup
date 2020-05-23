package de.ibba.keepitup.ui;

import de.ibba.keepitup.service.IPowerManager;
import de.ibba.keepitup.ui.dialog.BatteryOptimizationDialog;

public interface BatteryOptimizationSupport {

    IPowerManager getPowerManager();

    void onBatteryOptimizationDialogOkClicked(BatteryOptimizationDialog batteryOptimizationDialog);
}
