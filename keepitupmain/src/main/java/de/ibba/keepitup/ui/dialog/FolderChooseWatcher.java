package de.ibba.keepitup.ui.dialog;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

public class FolderChooseWatcher implements TextWatcher {

    private final FolderChooseDialog dialog;

    public FolderChooseWatcher(FolderChooseDialog dialog) {
        this.dialog = dialog;
    }

    @Override
    public void beforeTextChanged(CharSequence seq, int start, int count, int after) {
        Log.d(FolderChooseWatcher.class.getName(), "beforeTextChanged");
    }

    @Override
    public void onTextChanged(CharSequence seq, int start, int before, int count) {
        Log.d(FolderChooseWatcher.class.getName(), "onTextChanged");
    }

    @Override
    public void afterTextChanged(Editable seq) {
        Log.d(FolderChooseWatcher.class.getName(), "afterTextChanged");
        if (seq != null) {
            dialog.getAbsoluteFolderText().setText(getAbsoluteDownloadFolder(dialog.getRoot(), seq));
            dialog.getAdapter().unselectItem();
        }
    }

    private String getAbsoluteDownloadFolder(String root, CharSequence folder) {
        if (folder == null || folder.length() == 0) {
            return root;
        }
        return root + "/" + folder;
    }
}
