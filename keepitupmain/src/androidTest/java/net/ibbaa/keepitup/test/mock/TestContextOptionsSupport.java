/*
 * Copyright (c) 2022. Alwin Ibba
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.ibbaa.keepitup.test.mock;

import net.ibbaa.keepitup.ui.ContextOptionsSupport;
import net.ibbaa.keepitup.ui.dialog.ContextOption;
import net.ibbaa.keepitup.ui.dialog.ContextOptionsDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
