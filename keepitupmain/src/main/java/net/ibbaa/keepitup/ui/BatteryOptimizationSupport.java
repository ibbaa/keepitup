package net.ibbaa.keepitup.ui;

import net.ibbaa.keepitup.service.IPowerManager;
import net.ibbaa.keepitup.ui.dialog.BatteryOptimizationDialog;

public interface BatteryOptimizationSupport {

    IPowerManager getPowerManager();

    void onBatteryOptimizationDialogOkClicked(BatteryOptimizationDialog batteryOptimizationDialog);
}
