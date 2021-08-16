package net.ibbaa.keepitup.test.mock;

import androidx.fragment.app.FragmentManager;

import net.ibbaa.keepitup.ui.ContextOptionsSupportManager;
import net.ibbaa.keepitup.ui.clipboard.IClipboardManager;
import net.ibbaa.keepitup.ui.dialog.ContextOptionsDialog;

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
