package de.ibba.keepitup.ui.dialog;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import de.ibba.keepitup.service.FileManager;

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
            String folder = getAbsoluteFolder(dialog.getRoot(), seq);
            if (folder != null) {
                dialog.getAbsoluteFolderText().setText(getAbsoluteFolder(dialog.getRoot(), seq));
            }
        }
    }

    private String getAbsoluteFolder(String root, CharSequence folder) {
        return new FileManager(dialog.getActivity()).getAbsoluteFolder(root, String.valueOf(folder));
    }
}
