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

package net.ibbaa.keepitup.ui;

import android.os.Bundle;
import android.text.Editable;
import android.widget.EditText;

import androidx.fragment.app.FragmentManager;

import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.ui.clipboard.IClipboardManager;
import net.ibbaa.keepitup.ui.dialog.ContextOption;
import net.ibbaa.keepitup.ui.dialog.ContextOptionsDialog;
import net.ibbaa.keepitup.util.BundleUtil;
import net.ibbaa.keepitup.util.StringUtil;
import net.ibbaa.keepitup.util.UIUtil;

import java.util.ArrayList;
import java.util.List;

public class ContextOptionsSupportManager {

    private final FragmentManager fragmentManager;
    private final IClipboardManager clipboardManager;

    public ContextOptionsSupportManager(FragmentManager fragmentManager, IClipboardManager clipboardManager) {
        this.fragmentManager = fragmentManager;
        this.clipboardManager = clipboardManager;
    }

    public void showContextOptionsDialog(EditText editText) {
        Log.d(ContextOptionsSupportManager.class.getName(), "showContextOptionsDialog");
        Editable text = editText.getText();
        List<String> options = new ArrayList<>();
        if (!StringUtil.isEmpty(text)) {
            editText.selectAll();
            options.add(ContextOption.COPY.name());
        }
        if (doesClipboardContainSuitableData(editText)) {
            options.add(ContextOption.PASTE.name());
        }
        Log.d(ContextOptionsSupportManager.class.getName(), "options are " + options);
        if (options.isEmpty()) {
            Log.d(ContextOptionsSupportManager.class.getName(), "Not showing dialog because options are empty");
            return;
        }
        Log.d(ContextOptionsSupportManager.class.getName(), "Showing dialog...");
        ContextOptionsDialog contextOptionsDialog = createContextOptionsDialog();
        Bundle bundle = BundleUtil.stringListToBundle(ContextOption.class.getSimpleName(), options);
        bundle.putInt(contextOptionsDialog.getSourceResourceIdKey(), editText.getId());
        contextOptionsDialog.setArguments(bundle);
        contextOptionsDialog.show(fragmentManager, ContextOptionsDialog.class.getName());
    }

    public void handleContextOption(EditText editText, ContextOption option) {
        Log.d(ContextOptionsSupportManager.class.getName(), "handleContextOption, option is " + option);
        if (ContextOption.COPY.equals(option)) {
            String text = StringUtil.notNull(editText.getText());
            Log.d(ContextOptionsSupportManager.class.getName(), "Text field content is " + text);
            int selectionStart = editText.getSelectionStart();
            int selectionEnd = editText.getSelectionEnd();
            Log.d(ContextOptionsSupportManager.class.getName(), "Selection start is " + selectionStart);
            Log.d(ContextOptionsSupportManager.class.getName(), "Selection end is " + selectionEnd);
            if (StringUtil.isTextSelected(text, selectionStart, selectionEnd)) {
                Log.d(ContextOptionsSupportManager.class.getName(), "Selection is valid");
                text = text.substring(selectionStart, selectionEnd);
                Log.d(ContextOptionsSupportManager.class.getName(), "Selected text is " + text);
            }
            Log.d(ContextOptionsSupportManager.class.getName(), "Copying to clipboard");
            clipboardManager.putData(StringUtil.notNull(text));
        } else if (ContextOption.PASTE.equals(option)) {
            if (doesClipboardContainSuitableData(editText)) {
                String text = StringUtil.notNull(clipboardManager.getData());
                Log.d(ContextOptionsSupportManager.class.getName(), "Clipboard content is " + text);
                String textFieldText = StringUtil.notNull(editText.getText());
                Log.d(ContextOptionsSupportManager.class.getName(), "Text field content is " + text);
                int selectionStart = editText.getSelectionStart();
                int selectionEnd = editText.getSelectionEnd();
                Log.d(ContextOptionsSupportManager.class.getName(), "Selection start is " + selectionStart);
                Log.d(ContextOptionsSupportManager.class.getName(), "Selection end is " + selectionEnd);
                String prefixString = "";
                String suffixString = "";
                if (StringUtil.isTextSelected(textFieldText, selectionStart, selectionEnd)) {
                    if (StringUtil.isTextSelected(textFieldText, 0, selectionStart)) {
                        prefixString = textFieldText.substring(0, selectionStart);
                    }
                    if (StringUtil.isTextSelected(textFieldText, selectionEnd, textFieldText.length())) {
                        suffixString = textFieldText.substring(selectionEnd);
                    }
                }
                String finalText = prefixString + text + suffixString;
                Log.d(ContextOptionsSupportManager.class.getName(), "Pasting to text field: " + finalText);
                editText.setText(finalText);
            } else {
                Log.d(ContextOptionsSupportManager.class.getName(), "Clipboard does not contain suitable data for paste");
            }
        } else {
            Log.d(ContextOptionsSupportManager.class.getName(), "Unknown option: " + option);
        }
    }

    private boolean doesClipboardContainSuitableData(EditText editText) {
        Log.d(ContextOptionsSupportManager.class.getName(), "doesClipboardContainSuitableData");
        boolean isNumericField = UIUtil.isInputTypeNumber(editText.getInputType());
        if (!isNumericField) {
            Log.d(ContextOptionsSupportManager.class.getName(), "Field is not numeric");
            return clipboardManager.hasData();
        }
        Log.d(ContextOptionsSupportManager.class.getName(), "Field is numeric");
        return clipboardManager.hasNumericIntegerData();
    }

    protected ContextOptionsDialog createContextOptionsDialog() {
        return new ContextOptionsDialog();
    }
}
