package de.ibba.keepitup.ui;

import de.ibba.keepitup.service.IFileManager;
import de.ibba.keepitup.ui.dialog.FolderChooseDialog;

public interface FolderChooseSupport {

    IFileManager getFileManager();

    void onFolderChooseDialogOkClicked(FolderChooseDialog chooseDialog);

    void onFolderChooseDialogCancelClicked(FolderChooseDialog chooseDialog);
}
