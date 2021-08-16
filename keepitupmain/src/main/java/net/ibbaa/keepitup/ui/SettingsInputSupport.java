package net.ibbaa.keepitup.ui;

import net.ibbaa.keepitup.ui.dialog.SettingsInput;
import net.ibbaa.keepitup.ui.dialog.SettingsInputDialog;

public interface SettingsInputSupport {

    void onInputDialogOkClicked(SettingsInputDialog inputDialog, SettingsInput.Type type);

    void onInputDialogCancelClicked(SettingsInputDialog inputDialog);
}
