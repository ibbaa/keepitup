/*
 * Copyright (c) 2024. Alwin Ibba
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
