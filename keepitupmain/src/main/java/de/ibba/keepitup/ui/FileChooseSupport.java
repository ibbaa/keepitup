package de.ibba.keepitup.ui;

import de.ibba.keepitup.service.IFileManager;
import de.ibba.keepitup.ui.dialog.FileChooseDialog;

public interface FileChooseSupport {

    IFileManager getFileManager();

    void onFileChooseDialogOkClicked(FileChooseDialog chooseDialog, FileChooseDialog.Type type);

    void onFileChooseDialogCancelClicked(FileChooseDialog chooseDialog);
}
