package de.ibba.keepitup.ui;

import android.os.Bundle;
import android.text.Editable;
import android.widget.EditText;

import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;
import java.util.List;

import de.ibba.keepitup.logging.Log;
import de.ibba.keepitup.ui.clipboard.IClipboardManager;
import de.ibba.keepitup.ui.dialog.ContextOption;
import de.ibba.keepitup.ui.dialog.ContextOptionsDialog;
import de.ibba.keepitup.util.BundleUtil;
import de.ibba.keepitup.util.StringUtil;
import de.ibba.keepitup.util.UIUtil;

public class ContextOptionsSupportManager {

    private final FragmentManager fragmentManager;
    private final ContextOptionsSupport contextOptionsSupport;
    private final IClipboardManager clipboardManager;

    public ContextOptionsSupportManager(FragmentManager fragmentManager, ContextOptionsSupport contextOptionsSupport, IClipboardManager clipboardManager) {
        this.fragmentManager = fragmentManager;
        this.contextOptionsSupport = contextOptionsSupport;
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
        ContextOptionsDialog contextOptionsDialog = new ContextOptionsDialog(contextOptionsSupport);
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
        boolean isNumericField = UIUtil.isInpuTypeNumber(editText.getInputType());
        if (!isNumericField) {
            Log.d(ContextOptionsSupportManager.class.getName(), "Field is not numeric");
            return clipboardManager.hasData();
        }
        Log.d(ContextOptionsSupportManager.class.getName(), "Field is numeric");
        return clipboardManager.hasNumericIntegerData();
    }
}
