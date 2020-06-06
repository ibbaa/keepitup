package de.ibba.keepitup.test.mock;

import androidx.fragment.app.FragmentManager;

import de.ibba.keepitup.ui.ContextOptionsSupportManager;
import de.ibba.keepitup.ui.clipboard.IClipboardManager;
import de.ibba.keepitup.ui.dialog.ContextOptionsDialog;

public class TestContextOptionsSupportManager extends ContextOptionsSupportManager {

    private TestContextOptionsDialog contextOptionsDialog;

    public TestContextOptionsSupportManager(FragmentManager fragmentManager, IClipboardManager clipboardManager) {
        super(fragmentManager, clipboardManager);
    }

    @Override
    protected ContextOptionsDialog createContextOptionsDialog() {
        if (contextOptionsDialog == null) {
            contextOptionsDialog = new TestContextOptionsDialog();
        }
        return contextOptionsDialog;
    }

    public TestContextOptionsDialog getTestContextOptionsDialog() {
        return contextOptionsDialog;
    }
}
