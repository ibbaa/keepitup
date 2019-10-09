package de.ibba.keepitup.ui.dialog;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Objects;

import de.ibba.keepitup.R;
import de.ibba.keepitup.ui.GlobalSettingsActivity;
import de.ibba.keepitup.util.BundleUtil;
import de.ibba.keepitup.util.StringUtil;

public class DownloadFolderEditDialog extends DialogFragment {

    private View dialogView;
    private TextView rootfolderText;
    private EditText folderEditText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(DownloadFolderEditDialog.class.getName(), "onCreate");
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.DialogTheme);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(DownloadFolderEditDialog.class.getName(), "onCreateView");
        dialogView = inflater.inflate(R.layout.dialog_download_folder_edit, container);
        String root = BundleUtil.bundleToMessage(getDownloadFolderRootKey(), Objects.requireNonNull(getArguments()));
        String folder = BundleUtil.bundleToMessage(getDownloadFolderKey(), Objects.requireNonNull(getArguments()));
        prepareDownloadFolderRoot(root);
        prepareDownloadFolder(folder);
        prepareOkCancelImageButtons();
        return dialogView;
    }

    public String getDownloadFolderRootKey() {
        return DownloadFolderEditDialog.class.getSimpleName() + "Root";
    }

    public String getDownloadFolderKey() {
        return DownloadFolderEditDialog.class.getSimpleName() + "Folder";
    }

    public String getDownloadFolder() {
        return StringUtil.notNull(folderEditText.getText());
    }

    private void prepareDownloadFolderRoot(String root) {
        Log.d(DownloadFolderEditDialog.class.getName(), "prepareDownloadFolderRoot");
        rootfolderText = dialogView.findViewById(R.id.textview_dialog_download_folder_edit_root);
        rootfolderText.setText(root);
    }

    private void prepareDownloadFolder(String folder) {
        Log.d(DownloadFolderEditDialog.class.getName(), "prepareDownloadFolder");
        folderEditText = dialogView.findViewById(R.id.edittext_dialog_download_folder_edit_folder);
        folderEditText.setText(folder);
    }

    private void prepareOkCancelImageButtons() {
        Log.d(DownloadFolderEditDialog.class.getName(), "prepareOkCancelImageButtons");
        ImageView okImage = dialogView.findViewById(R.id.imageview_dialog_download_folder_edit_ok);
        ImageView cancelImage = dialogView.findViewById(R.id.imageview_dialog_download_folder_edit_cancel);
        okImage.setOnClickListener(this::onOkClicked);
        cancelImage.setOnClickListener(this::onCancelClicked);
    }

    private void onOkClicked(@SuppressWarnings("unused") View view) {
        Log.d(DownloadFolderEditDialog.class.getName(), "onOkClicked");
        GlobalSettingsActivity activity = (GlobalSettingsActivity) getActivity();
        Objects.requireNonNull(activity).onDownloadFolderEditDialogOkClicked(this);
    }

    private void onCancelClicked(@SuppressWarnings("unused") View view) {
        Log.d(DownloadFolderEditDialog.class.getName(), "onCancelClicked");
        GlobalSettingsActivity activity = (GlobalSettingsActivity) getActivity();
        Objects.requireNonNull(activity).onDownloadFolderEditDialogCancelClicked(this);
    }
}
