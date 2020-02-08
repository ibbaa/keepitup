package de.ibba.keepitup.ui;

import de.ibba.keepitup.ui.dialog.ConfirmDialog;

public interface ConfirmSupport {

    void onConfirmDialogOkClicked(ConfirmDialog confirmDialog, ConfirmDialog.Type type);

    void onConfirmDialogCancelClicked(ConfirmDialog confirmDialog);
}
