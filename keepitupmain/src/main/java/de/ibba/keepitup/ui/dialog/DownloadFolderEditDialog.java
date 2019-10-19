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
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.ibba.keepitup.R;
import de.ibba.keepitup.model.FileEntry;
import de.ibba.keepitup.ui.GlobalSettingsActivity;
import de.ibba.keepitup.ui.adapter.FileEntryAdapter;
import de.ibba.keepitup.util.BundleUtil;
import de.ibba.keepitup.util.StringUtil;

public class DownloadFolderEditDialog extends DialogFragment {

    private View dialogView;
    private TextView absoluteFolderText;
    private EditText folderEditText;
    private DownloadFolderEditWatcher folderEditTextWatcher;
    private RecyclerView folderRecyclerView;

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
        prepareDownloadFolderAbsolute(root, folder);
        prepareDownloadFolder(root, folder);
        prepareDownloadFolderRecyclerView();
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

    private void prepareDownloadFolderAbsolute(String root, String folder) {
        Log.d(DownloadFolderEditDialog.class.getName(), "prepareDownloadFolderAbsolute");
        absoluteFolderText = dialogView.findViewById(R.id.textview_dialog_download_folder_edit_root);
        absoluteFolderText.setText(getAbsoluteDownloadFolder(root, folder));
    }

    private void prepareDownloadFolder(String root, String folder) {
        Log.d(DownloadFolderEditDialog.class.getName(), "prepareDownloadFolder");
        folderEditText = dialogView.findViewById(R.id.edittext_dialog_download_folder_edit_folder);
        folderEditText.setText(folder);
        prepareFolderEditTextWatcher(root);
    }

    private void prepareFolderEditTextWatcher(String root) {
        Log.d(DownloadFolderEditDialog.class.getName(), "prepareFolderEditTextWatcher");
        if (folderEditTextWatcher != null) {
            folderEditText.removeTextChangedListener(folderEditTextWatcher);
            folderEditTextWatcher = null;
        }
        folderEditTextWatcher = new DownloadFolderEditWatcher(root, absoluteFolderText);
        folderEditText.addTextChangedListener(folderEditTextWatcher);
    }

    private void prepareDownloadFolderRecyclerView() {
        folderRecyclerView = dialogView.findViewById(R.id.listview_dialog_download_folder_edit_files);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        folderRecyclerView.setLayoutManager(layoutManager);
        folderRecyclerView.setItemAnimator(new DefaultItemAnimator());
        folderRecyclerView.setAdapter(createAdapter());
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

    private String getAbsoluteDownloadFolder(String root, String folder) {
        if (StringUtil.isEmpty(folder)) {
            return root;
        }
        return root + "/" + folder;
    }

    private RecyclerView.Adapter createAdapter() {
        List<FileEntry> entries = new ArrayList<>();
        FileEntry entry1 = new FileEntry();
        entry1.setName("Download");
        entry1.setDirectory(true);
        FileEntry entry2 = new FileEntry();
        entry2.setName("Test");
        entry2.setDirectory(false);
        FileEntry entry3 = new FileEntry();
        entry3.setName("xyz");
        entry3.setDirectory(true);
        entries.add(entry1);
        entries.add(entry2);
        entries.add(entry3);
        return new FileEntryAdapter(entries, getActivity());
    }
}
