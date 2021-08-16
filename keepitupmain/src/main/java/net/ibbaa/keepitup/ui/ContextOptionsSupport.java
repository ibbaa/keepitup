package net.ibbaa.keepitup.ui;

import net.ibbaa.keepitup.ui.dialog.ContextOption;
import net.ibbaa.keepitup.ui.dialog.ContextOptionsDialog;

public interface ContextOptionsSupport {

    void onContextOptionsDialogClicked(ContextOptionsDialog contextOptionsDialog, int sourceResourceId, ContextOption option);
}
