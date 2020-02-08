package de.ibba.keepitup.ui;

import de.ibba.keepitup.ui.dialog.SettingsInput;
import de.ibba.keepitup.ui.dialog.SettingsInputDialog;

public interface SettingsInputSupport {

    void onInputDialogOkClicked(SettingsInputDialog inputDialog, SettingsInput.Type type);

    void onInputDialogCancelClicked(SettingsInputDialog inputDialog);
}
