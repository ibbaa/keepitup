package net.ibbaa.keepitup.ui;

import net.ibbaa.keepitup.service.IFileManager;
import net.ibbaa.keepitup.ui.dialog.FileChooseDialog;

public interface FileChooseSupport {

    IFileManager getFileManager();

    void onFileChooseDialogOkClicked(FileChooseDialog chooseDialog, FileChooseDialog.Type type);

    void onFileChooseDialogCancelClicked(FileChooseDialog chooseDialog);
}
