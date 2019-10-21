package de.ibba.keepitup.ui.dialog;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import de.ibba.keepitup.ui.SettingsInputActivity;
import de.ibba.keepitup.ui.adapter.FileEntryAdapter;
import de.ibba.keepitup.util.BundleUtil;
import de.ibba.keepitup.util.StringUtil;

public class FolderChooseDialog extends DialogFragment {

    private View dialogView;
    private TextView absoluteFolderText;
    private EditText folderEditText;
    private FolderChooseWatcher folderChooseTextWatcher;
    private CheckBox showFilesCheckBox;
    private RecyclerView fileEntriesRecyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(FolderChooseDialog.class.getName(), "onCreate");
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.DialogTheme);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(FolderChooseDialog.class.getName(), "onCreateView");
        dialogView = inflater.inflate(R.layout.dialog_folder_choose, container);
        String root = BundleUtil.bundleToMessage(getFolderRootKey(), Objects.requireNonNull(getArguments()));
        String folder = BundleUtil.bundleToMessage(getFolderKey(), Objects.requireNonNull(getArguments()));
        prepareFolderAbsolute(root, folder);
        prepareFolder(root, folder);
        prepareShowFilesCheckBox();
        prepareFolderRecyclerView();
        prepareOkCancelImageButtons();
        return dialogView;
    }

    public String getFolderRootKey() {
        return FolderChooseDialog.class.getSimpleName() + "Root";
    }

    public String getFolderKey() {
        return FolderChooseDialog.class.getSimpleName() + "Folder";
    }

    public String getFolder() {
        return StringUtil.notNull(folderEditText.getText());
    }

    public boolean isShowFiles() {
        return showFilesCheckBox.isChecked();
    }

    private void prepareFolderAbsolute(String root, String folder) {
        Log.d(FolderChooseDialog.class.getName(), "prepareFolderAbsolute");
        absoluteFolderText = dialogView.findViewById(R.id.textview_dialog_folder_choose_root);
        absoluteFolderText.setText(getAbsoluteFolder(root, folder));
    }

    private void prepareFolder(String root, String folder) {
        Log.d(FolderChooseDialog.class.getName(), "prepareFolder");
        folderEditText = dialogView.findViewById(R.id.edittext_dialog_folder_choose_folder);
        folderEditText.setText(folder);
        prepareFolderChooseTextWatcher(root);
    }

    private void prepareFolderChooseTextWatcher(String root) {
        Log.d(FolderChooseDialog.class.getName(), "prepareFolderChooseTextWatcher");
        if (folderChooseTextWatcher != null) {
            folderEditText.removeTextChangedListener(folderChooseTextWatcher);
            folderChooseTextWatcher = null;
        }
        folderChooseTextWatcher = new FolderChooseWatcher(root, absoluteFolderText);
        folderEditText.addTextChangedListener(folderChooseTextWatcher);
    }

    private void prepareShowFilesCheckBox() {
        Log.d(FolderChooseDialog.class.getName(), "prepareShowFilesCheckBox");
        showFilesCheckBox = dialogView.findViewById(R.id.checkbox_dialog_folder_choose_show_files);
        showFilesCheckBox.setOnCheckedChangeListener(this::onShowFilesCheckedChanged);
    }

    private void onShowFilesCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.d(GlobalSettingsActivity.class.getName(), "onShowFilesCheckedChanged, new value is " + isChecked);
        getAdapter().notifyDataSetChanged();
    }

    private void prepareFolderRecyclerView() {
        Log.d(FolderChooseDialog.class.getName(), "prepareFolderRecyclerView");
        fileEntriesRecyclerView = dialogView.findViewById(R.id.listview_dialog_folder_choose_file_entries);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        fileEntriesRecyclerView.setLayoutManager(layoutManager);
        fileEntriesRecyclerView.setItemAnimator(new DefaultItemAnimator());
        fileEntriesRecyclerView.setAdapter(createAdapter());
    }

    private void prepareOkCancelImageButtons() {
        Log.d(FolderChooseDialog.class.getName(), "prepareOkCancelImageButtons");
        ImageView okImage = dialogView.findViewById(R.id.imageview_dialog_folder_choose_ok);
        ImageView cancelImage = dialogView.findViewById(R.id.imageview_dialog_folder_choose_cancel);
        okImage.setOnClickListener(this::onOkClicked);
        cancelImage.setOnClickListener(this::onCancelClicked);
    }

    private void onOkClicked(@SuppressWarnings("unused") View view) {
        Log.d(FolderChooseDialog.class.getName(), "onOkClicked");
        SettingsInputActivity activity = (SettingsInputActivity) getActivity();
        Objects.requireNonNull(activity).onFolderChooseEditDialogOkClicked(this);
    }

    private void onCancelClicked(@SuppressWarnings("unused") View view) {
        Log.d(FolderChooseDialog.class.getName(), "onCancelClicked");
        SettingsInputActivity activity = (SettingsInputActivity) getActivity();
        Objects.requireNonNull(activity).onFolderChooseEditDialogCancelClicked(this);
    }

    private String getAbsoluteFolder(String root, String folder) {
        if (StringUtil.isEmpty(folder)) {
            return root;
        }
        return root + "/" + folder;
    }

    public void onFileEntryClicked(View view, int position) {
        Log.d(FolderChooseDialog.class.getName(), "onFileEntryClicked, position is " + position);
        getAdapter().selectItem(position);
    }

    public RecyclerView getFileEntriesRecyclerView() {
        return fileEntriesRecyclerView;
    }

    public FileEntryAdapter getAdapter() {
        return (FileEntryAdapter) getFileEntriesRecyclerView().getAdapter();
    }

    private RecyclerView.Adapter createAdapter() {
        List<FileEntry> entries = new ArrayList<>();
        FileEntry entry0 = new FileEntry();
        entry0.setName("Download");
        entry0.setDirectory(true);
        FileEntry entry1 = new FileEntry();
        entry1.setName("Download");
        entry1.setDirectory(true);
        FileEntry entry2 = new FileEntry();
        entry2.setName("Test");
        entry2.setDirectory(false);
        FileEntry entry3 = new FileEntry();
        entry3.setName("xyz");
        entry3.setDirectory(true);
        FileEntry entry4 = new FileEntry();
        entry4.setName("Download");
        entry4.setDirectory(true);
        FileEntry entry5 = new FileEntry();
        entry5.setName("Test");
        entry5.setDirectory(false);
        FileEntry entry6 = new FileEntry();
        entry6.setName("xyz");
        entry6.setDirectory(true);
        FileEntry entry7 = new FileEntry();
        entry7.setName("Download1");
        entry7.setDirectory(true);
        FileEntry entry8 = new FileEntry();
        entry8.setName("Test");
        entry8.setDirectory(false);
        FileEntry entry9 = new FileEntry();
        entry9.setName("xyz");
        entry9.setDirectory(true);
        FileEntry entry10 = new FileEntry();
        entry10.setName("xyz");
        entry10.setDirectory(true);
        entries.add(entry0);
        entries.add(entry1);
        entries.add(entry2);
        /*entries.add(entry3);
        entries.add(entry4);
        entries.add(entry5);
        entries.add(entry6);
        entries.add(entry7);
        entries.add(entry8);
        entries.add(entry9);
        entries.add(entry10);*/
        return new FileEntryAdapter(entries, this);
    }
}
