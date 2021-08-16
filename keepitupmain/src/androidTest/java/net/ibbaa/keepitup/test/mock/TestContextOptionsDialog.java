package net.ibbaa.keepitup.test.mock;

import net.ibbaa.keepitup.ui.ContextOptionsSupport;
import net.ibbaa.keepitup.ui.dialog.ContextOptionsDialog;

public class TestContextOptionsDialog extends ContextOptionsDialog {

    private TestContextOptionsSupport contextOptionsSupport;

    @Override
    public ContextOptionsSupport getContextOptionsSupport() {
        if (contextOptionsSupport == null) {
            contextOptionsSupport = new TestContextOptionsSupport();
        }
        return contextOptionsSupport;
    }
}
