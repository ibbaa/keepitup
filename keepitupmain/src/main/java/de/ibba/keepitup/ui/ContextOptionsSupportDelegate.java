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

public class ContextOptionsSupportDelegate {

    private final FragmentManager fragmentManager;
    private final ContextOptionsSupport contextOptionsSupport;
    private final IClipboardManager clipboardManager;

    public ContextOptionsSupportDelegate(FragmentManager fragmentManager, ContextOptionsSupport contextOptionsSupport, IClipboardManager clipboardManager) {
        this.fragmentManager = fragmentManager;
        this.contextOptionsSupport = contextOptionsSupport;
        this.clipboardManager = clipboardManager;
    }

    public void showContextOptionsDialog(EditText editText) {
        Log.d(ContextOptionsSupportDelegate.class.getName(), "showContextOptionsDialog");
        Editable text = editText.getText();
        List<String> options = new ArrayList<>();
        if (!StringUtil.isEmpty(text)) {
            editText.selectAll();
            options.add(ContextOption.COPY.name());
        }
        if (doesClipboardContainSuitableData(editText)) {
            options.add(ContextOption.PASTE.name());
        }
        Log.d(ContextOptionsSupportDelegate.class.getName(), "options are " + options);
        if (options.isEmpty()) {
            Log.d(ContextOptionsSupportDelegate.class.getName(), "Not showing dialog because options are empty");
            return;
        }
        Log.d(ContextOptionsSupportDelegate.class.getName(), "Showing dialog...");
        ContextOptionsDialog contextOptionsDialog = new ContextOptionsDialog(contextOptionsSupport);
        Bundle bundle = BundleUtil.stringListToBundle(ContextOption.class.getSimpleName(), options);
        bundle.putInt(contextOptionsDialog.getSourceResourceIdKey(), editText.getId());
        contextOptionsDialog.setArguments(bundle);
        contextOptionsDialog.show(fragmentManager, ContextOptionsDialog.class.getName());
    }

    private boolean doesClipboardContainSuitableData(EditText editText) {
        Log.d(ContextOptionsSupportDelegate.class.getName(), "doesClipboardContainSuitableData");
        boolean isNumericField = UIUtil.isInpuTypeNumber(editText.getInputType());
        if (!isNumericField) {
            Log.d(ContextOptionsSupportDelegate.class.getName(), "Field is not numeric");
            return clipboardManager.hasData();
        }
        Log.d(ContextOptionsSupportDelegate.class.getName(), "Field is numeric");
        return clipboardManager.hasNumericIntegerData();
    }
}
