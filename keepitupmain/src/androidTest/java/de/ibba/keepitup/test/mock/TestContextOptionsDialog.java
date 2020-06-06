package de.ibba.keepitup.test.mock;

import de.ibba.keepitup.ui.ContextOptionsSupport;
import de.ibba.keepitup.ui.dialog.ContextOptionsDialog;

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
