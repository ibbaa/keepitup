package de.ibba.keepitup.test.mock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.ibba.keepitup.ui.ContextOptionsSupport;
import de.ibba.keepitup.ui.dialog.ContextOption;
import de.ibba.keepitup.ui.dialog.ContextOptionsDialog;

public class TestContextOptionsSupport implements ContextOptionsSupport {

    private final List<OnContextOptionsDialogEntryClickedCall> onContextOptionsDialogEntryClickedCalls;

    public TestContextOptionsSupport() {
        this.onContextOptionsDialogEntryClickedCalls = new ArrayList<>();
    }

    public void reset() {
        onContextOptionsDialogEntryClickedCalls.clear();
    }

    public List<OnContextOptionsDialogEntryClickedCall> getOnContextOptionsDialogEntryClickedCalls() {
        return Collections.unmodifiableList(onContextOptionsDialogEntryClickedCalls);
    }

    public boolean wasOnContextOptionsDialogEntryClickedCalled() {
        return !onContextOptionsDialogEntryClickedCalls.isEmpty();
    }

    @Override
    public void onContextOptionsDialogClicked(ContextOptionsDialog contextOptionsDialog, int sourceResourceId, ContextOption option) {
        onContextOptionsDialogEntryClickedCalls.add(new OnContextOptionsDialogEntryClickedCall(contextOptionsDialog, sourceResourceId, option));
    }

    public static class OnContextOptionsDialogEntryClickedCall {

        private final ContextOptionsDialog contextOptionsDialog;
        private final int sourceResourceId;
        private final ContextOption option;

        public OnContextOptionsDialogEntryClickedCall(ContextOptionsDialog contextOptionsDialog, int sourceResourceId, ContextOption option) {
            this.contextOptionsDialog = contextOptionsDialog;
            this.sourceResourceId = sourceResourceId;
            this.option = option;
        }

        public ContextOptionsDialog getContextOptionsDialog() {
            return contextOptionsDialog;
        }

        public int getSourceResourceId() {
            return sourceResourceId;
        }

        public ContextOption getOption() {
            return option;
        }
    }
}
