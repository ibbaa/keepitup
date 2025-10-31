/*
 * Copyright (c) 2025 Alwin Ibba
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

package net.ibbaa.keepitup.ui.dialog;

import android.text.Editable;
import android.text.TextWatcher;

import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.service.SystemFileManager;

@SuppressWarnings("ClassCanBeRecord")
public class FileChooseWatcher implements TextWatcher {

    private final FileChooseDialog dialog;

    public FileChooseWatcher(FileChooseDialog dialog) {
        this.dialog = dialog;
    }

    @Override
    public void beforeTextChanged(CharSequence seq, int start, int count, int after) {
        Log.d(FileChooseWatcher.class.getName(), "beforeTextChanged");
    }

    @Override
    public void onTextChanged(CharSequence seq, int start, int before, int count) {
        Log.d(FileChooseWatcher.class.getName(), "onTextChanged");
    }

    @Override
    public void afterTextChanged(Editable seq) {
        Log.d(FileChooseWatcher.class.getName(), "afterTextChanged");
        if (seq != null) {
            String folder = getAbsolutePath(dialog.getRoot(), dialog.getFolder());
            if (folder != null) {
                if (dialog.isFileMode()) {
                    folder = getAbsolutePath(folder, seq);
                }
                if (folder != null) {
                    dialog.getAbsoluteFolderText().setText(folder);
                }
            }
        }
    }

    private String getAbsolutePath(String root, CharSequence path) {
        return new SystemFileManager(dialog.getActivity()).getAbsolutePath(root, String.valueOf(path));
    }
}
