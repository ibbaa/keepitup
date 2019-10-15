package de.ibba.keepitup.ui.dialog;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.TextView;

public class DownloadFolderEditWatcher implements TextWatcher {

    private final String root;
    private final TextView absoluteFolderText;

    public DownloadFolderEditWatcher(String root, TextView absoluteFolderText) {
        this.root = root;
        this.absoluteFolderText = absoluteFolderText;
    }

    @Override
    public void beforeTextChanged(CharSequence seq, int start, int count, int after) {
        Log.d(DownloadFolderEditWatcher.class.getName(), "beforeTextChanged");
    }

    @Override
    public void onTextChanged(CharSequence seq, int start, int before, int count) {
        Log.d(DownloadFolderEditWatcher.class.getName(), "onTextChanged");
    }

    @Override
    public void afterTextChanged(Editable seq) {
        Log.d(DownloadFolderEditWatcher.class.getName(), "afterTextChanged");
        if (seq != null) {
            absoluteFolderText.setText(getAbsoluteDownloadFolder(root, seq));
        }
    }

    private String getAbsoluteDownloadFolder(String root, CharSequence folder) {
        if (folder == null || folder.length() == 0) {
            return root;
        }
        return root + "/" + folder;
    }
}
