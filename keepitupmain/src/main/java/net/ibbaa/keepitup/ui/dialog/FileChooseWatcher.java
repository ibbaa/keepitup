package net.ibbaa.keepitup.ui.dialog;

import android.text.Editable;
import android.text.TextWatcher;

import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.service.SystemFileManager;

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
