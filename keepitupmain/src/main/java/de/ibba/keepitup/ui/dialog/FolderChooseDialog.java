package de.ibba.keepitup.ui.dialog;

import android.app.Activity;
import android.graphics.Typeface;
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

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import de.ibba.keepitup.R;
import de.ibba.keepitup.model.FileEntry;
import de.ibba.keepitup.resources.FileManager;
import de.ibba.keepitup.resources.IFileManager;
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
    private String selectionFolder;

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
        String folder = BundleUtil.bundleToMessage(getFolderKey(), Objects.requireNonNull(getArguments()));
        prepareFolderAbsolute(folder);
        prepareFolder(folder);
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

    public String getRoot() {
        return BundleUtil.bundleToMessage(getFolderRootKey(), Objects.requireNonNull(getArguments()));
    }

    public TextView getAbsoluteFolderText() {
        return absoluteFolderText;
    }

    private String getSelectionFolder() {
        return selectionFolder;
    }

    private void prepareFolderAbsolute(String folder) {
        Log.d(FolderChooseDialog.class.getName(), "prepareFolderAbsolute");
        absoluteFolderText = dialogView.findViewById(R.id.textview_dialog_folder_choose_root);
        absoluteFolderText.setText(getAbsoluteFolder(getRoot(), folder));
    }

    private void prepareFolder(String folder) {
        Log.d(FolderChooseDialog.class.getName(), "prepareFolder");
        folderEditText = dialogView.findViewById(R.id.edittext_dialog_folder_choose_folder);
        folderEditText.setText(folder);
        selectionFolder = folder;
        prepareFolderChooseTextWatcher();
    }

    private void prepareFolderChooseTextWatcher() {
        Log.d(FolderChooseDialog.class.getName(), "prepareFolderChooseTextWatcher");
        if (folderChooseTextWatcher != null) {
            folderEditText.removeTextChangedListener(folderChooseTextWatcher);
            folderChooseTextWatcher = null;
        }
        folderChooseTextWatcher = new FolderChooseWatcher(this);
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
        return getFileManager().getAbsoluteFolder(root, folder);
    }

    public void onFileEntryClicked(View view, int position) {
        Log.d(FolderChooseDialog.class.getName(), "onFileEntryClicked, position is " + position);
        selectEntry(position);
    }

    public boolean onFileEntryLongClicked(View view, int position) {
        Log.d(FolderChooseDialog.class.getName(), "onFileEntryLongClicked, position is " + position);
        selectEntry(position);
        return true;
    }

    private void selectEntry(int position) {
        Log.d(FolderChooseDialog.class.getName(), "selectEntry, position is " + position);
        FileEntry selectedEntry = getAdapter().getItem(position);
        if (selectedEntry == null) {
            Log.e(FolderChooseDialog.class.getName(), "selected entry is null");
            return;
        }
        if (!selectedEntry.isDirectory()) {
            Log.d(FolderChooseDialog.class.getName(), "selected entry " + selectedEntry + " is a file. Select skipped.");
            return;
        }
        String folderName = selectedEntry.getName();
        Log.d(FolderChooseDialog.class.getName(), "Prepare selected folder name " + folderName);
        IFileManager fileManager = getFileManager();
        String nestedFolder;
        if (selectedEntry.isParent()) {
            nestedFolder = fileManager.getRelativeParent(selectionFolder);
        } else if (getAdapter().isItemSelected()) {
            nestedFolder = fileManager.getRelativeSibling(selectionFolder, folderName);
        } else {
            nestedFolder = fileManager.getNestedFolder(selectionFolder, folderName);
        }
        if (nestedFolder == null) {
            Log.e(FolderChooseDialog.class.getName(), "Error preparing selected folder");
            return;
        }
        Log.d(FolderChooseDialog.class.getName(), "Selected folder name is " + nestedFolder);
        getAdapter().selectItem(position);
        if (folderChooseTextWatcher != null) {
            folderEditText.removeTextChangedListener(folderChooseTextWatcher);
            folderChooseTextWatcher = null;
        }
        folderEditText.setText(nestedFolder);
        absoluteFolderText.setText(getAbsoluteFolder(getRoot(), nestedFolder));
        selectionFolder = nestedFolder;
        folderChooseTextWatcher = new FolderChooseWatcher(this);
        folderEditText.addTextChangedListener(folderChooseTextWatcher);
    }

    public RecyclerView getFileEntriesRecyclerView() {
        return fileEntriesRecyclerView;
    }

    public FileEntryAdapter getAdapter() {
        return (FileEntryAdapter) getFileEntriesRecyclerView().getAdapter();
    }

    private IFileManager getFileManager() {
        Log.d(FolderChooseDialog.class.getName(), "getFileManager");
        Activity activity = getActivity();
        if (activity instanceof SettingsInputActivity) {
            Log.d(FolderChooseDialog.class.getName(), "Returning file manager from activity.");
            return ((SettingsInputActivity) activity).getFileManager();
        }
        Log.d(FolderChooseDialog.class.getName(), "Returning new file manager.");
        return new FileManager(activity);
    }

    private RecyclerView.Adapter createAdapter() {
        Log.d(FolderChooseDialog.class.getName(), "createAdapter");
        String aboluteFolder = getAbsoluteFolder(getRoot(), getSelectionFolder());
        IFileManager fileManager = getFileManager();
        String parent = fileManager.getAbsoluteParent(getRoot(), aboluteFolder);
        if (parent == null) {
            Log.e(FolderChooseDialog.class.getName(), "File manager returned null as parent");
            showErrorDialog(getResources().getString(R.string.text_dialog_general_error_list_folder_files));
            return new FileEntryAdapter(Collections.emptyList(), this);
        }
        try {
            File file1 = new File(parent, "test1");
            File file2 = new File(parent, "test2");
            File file3 = new File(parent, "test3");
            file1.mkdir();
            file2.mkdir();
            file3.createNewFile();
        } catch (IOException exc) {
            exc.printStackTrace();
        }
        try {
            File file1 = new File(parent + "/test1", "testNested11");
            File file2 = new File(parent + "/test1", "testNested12");
            File file3 = new File(parent + "/test1", "testNested13");
            file1.mkdir();
            file2.mkdir();
            file3.createNewFile();
        } catch (IOException exc) {
            exc.printStackTrace();
        }
        try {
            File file1 = new File(parent + "/test2", "testNested21");
            File file2 = new File(parent + "/test2", "testNested22");
            File file3 = new File(parent + "/test2", "testNested23");
            file1.mkdir();
            file2.mkdir();
            file3.createNewFile();
        } catch (IOException exc) {
            exc.printStackTrace();
        }
        List<FileEntry> entries = readFiles(parent);
        FileEntryAdapter adapter = new FileEntryAdapter(entries, this);
        adapter.selectItemByName(getSelectionFolder());
        adapter.notifyDataSetChanged();
        return adapter;
    }

    private List<FileEntry> readFiles(String folder) {
        Log.d(FolderChooseDialog.class.getName(), "readFiles, folder is " + folder);
        IFileManager fileManager = getFileManager();
        String root = getRoot();
        List<FileEntry> entries = fileManager.getFiles(root, folder);
        if (entries == null) {
            Log.e(FolderChooseDialog.class.getName(), "File manager returned null as folder file list");
            showErrorDialog(getResources().getString(R.string.text_dialog_general_error_list_folder_files));
            return Collections.emptyList();
        }
        Log.d(FolderChooseDialog.class.getName(), "File manager returned the following file entries: " + (entries.isEmpty() ? "no file entries" : ""));
        for (FileEntry entry : entries) {
            Log.d(FolderChooseDialog.class.getName(), entry.toString());
        }
        return entries;
    }

    private void showErrorDialog(String errorMessage) {
        showErrorDialog(errorMessage, Typeface.BOLD);
    }

    private void showErrorDialog(String errorMessage, int typeface) {
        Log.d(FolderChooseDialog.class.getName(), "showErrorDialog with message " + errorMessage);
        GeneralErrorDialog errorDialog = new GeneralErrorDialog();
        Bundle bundle = BundleUtil.messageToBundle(GeneralErrorDialog.class.getSimpleName(), errorMessage);
        bundle.putInt(errorDialog.getTypefaceStyleKey(), typeface);
        errorDialog.setArguments(bundle);
        errorDialog.show(Objects.requireNonNull(getFragmentManager()), GeneralErrorDialog.class.getName());
    }
}
