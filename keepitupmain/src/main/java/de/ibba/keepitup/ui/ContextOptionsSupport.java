package de.ibba.keepitup.ui;

import de.ibba.keepitup.ui.dialog.ContextOption;
import de.ibba.keepitup.ui.dialog.ContextOptionsDialog;

public interface ContextOptionsSupport {

    void onContextOptionsDialogEntryClicked(ContextOptionsDialog contextOptionsDialog, int sourceResourceId, ContextOption option);
}
