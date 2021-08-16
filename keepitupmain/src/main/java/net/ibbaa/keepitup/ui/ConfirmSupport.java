package net.ibbaa.keepitup.ui;

import net.ibbaa.keepitup.ui.dialog.ConfirmDialog;

public interface ConfirmSupport {

    void onConfirmDialogOkClicked(ConfirmDialog confirmDialog, ConfirmDialog.Type type);

    void onConfirmDialogCancelClicked(ConfirmDialog confirmDialog);
}
