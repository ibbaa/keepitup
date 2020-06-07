package de.ibba.keepitup.ui.dialog;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
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

import java.util.Collections;
import java.util.List;

import de.ibba.keepitup.R;
import de.ibba.keepitup.logging.Log;
import de.ibba.keepitup.model.FileEntry;
import de.ibba.keepitup.service.IFileManager;
import de.ibba.keepitup.service.SystemFileManager;
import de.ibba.keepitup.ui.ContextOptionsSupport;
import de.ibba.keepitup.ui.ContextOptionsSupportManager;
import de.ibba.keepitup.ui.FolderChooseSupport;
import de.ibba.keepitup.ui.GlobalSettingsActivity;
import de.ibba.keepitup.ui.adapter.FileEntryAdapter;
import de.ibba.keepitup.ui.clipboard.IClipboardManager;
import de.ibba.keepitup.ui.clipboard.SystemClipboardManager;
import de.ibba.keepitup.util.BundleUtil;
import de.ibba.keepitup.util.StringUtil;

public class FolderChooseDialog extends DialogFragment implements ContextOptionsSupport {

    private View dialogView;
    private TextView absoluteFolderText;
    private EditText folderEditText;
    private FolderChooseWatcher folderChooseTextWatcher;
    private CheckBox showFilesCheckBox;
    private RecyclerView fileEntriesRecyclerView;
    private String selectionFolder;

    private IClipboardManager clipboardManager;

    public void injectClipboardManager(IClipboardManager clipboardManager) {
        this.clipboardManager = clipboardManager;
    }

    public IClipboardManager getClipboardManager() {
        if (clipboardManager != null) {
            return clipboardManager;
        }
        return new SystemClipboardManager(requireContext());
    }

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
        boolean containsSavedState = containsSavedState(savedInstanceState);
        Log.d(FolderChooseDialog.class.getName(), "containsSavedState is " + containsSavedState);
        String folder = containsSavedState ? savedInstanceState.getString(getSelectionFolderKey()) : BundleUtil.stringFromBundle(getFolderKey(), requireArguments());
        Bundle adapterState = containsSavedState ? savedInstanceState.getBundle(getFileEntryAdapterKey()) : null;
        prepareFolderAbsolute(folder);
        prepareFolder(folder);
        prepareShowFilesCheckBox();
        prepareFolderRecyclerView(adapterState);
        prepareOkCancelImageButtons();
        return dialogView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Log.d(FolderChooseDialog.class.getName(), "onSaveInstanceState");
        super.onSaveInstanceState(outState);
        outState.putString(getSelectionFolderKey(), selectionFolder);
        Bundle adapterBundle = getAdapter().saveStateToBundle();
        outState.putBundle(getFileEntryAdapterKey(), adapterBundle);
    }

    private boolean containsSavedState(Bundle savedInstanceState) {
        Log.d(FolderChooseDialog.class.getName(), "containsSavedState");
        if (savedInstanceState == null) {
            Log.d(FolderChooseDialog.class.getName(), "savedInstanceState bundle is null");
            return false;
        }
        return savedInstanceState.containsKey(getSelectionFolderKey()) && savedInstanceState.containsKey(getFileEntryAdapterKey());
    }


    public String getFolderRootKey() {
        return FolderChooseDialog.class.getSimpleName() + "Root";
    }

    public String getFolderKey() {
        return FolderChooseDialog.class.getSimpleName() + "Folder";
    }

    private String getSelectionFolderKey() {
        return FolderChooseDialog.class.getSimpleName() + "SelectionFolder";
    }

    private String getFileEntryAdapterKey() {
        return FolderChooseDialog.class.getSimpleName() + "FileEntryAdapter";
    }

    public String getFolder() {
        return StringUtil.notNull(folderEditText.getText());
    }

    public boolean isShowFiles() {
        return showFilesCheckBox.isChecked();
    }

    public String getRoot() {
        return BundleUtil.stringFromBundle(getFolderRootKey(), requireArguments());
    }

    public TextView getAbsoluteFolderText() {
        return absoluteFolderText;
    }

    private String getSelectionFolder() {
        return selectionFolder;
    }

    private void prepareFolderAbsolute(String folder) {
        Log.d(FolderChooseDialog.class.getName(), "prepareFolderAbsolute");
        absoluteFolderText = dialogView.findViewById(R.id.textview_dialog_folder_choose_absolute);
        String absoluteFolder = getAbsoluteFolder(getRoot(), folder);
        if (absoluteFolder != null) {
            absoluteFolderText.setText(absoluteFolder);
        }
    }

    private void prepareFolder(String folder) {
        Log.d(FolderChooseDialog.class.getName(), "prepareFolder");
        folderEditText = dialogView.findViewById(R.id.edittext_dialog_folder_choose_folder);
        folderEditText.setOnLongClickListener(this::onFolderEditTextLongClicked);
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
        FileEntryAdapter adapter = getAdapter();
        adapter.unselectItem();
        adapter.notifyDataSetChanged();
        adapter.selectItemByName(getSelectionFolder());
        if (!adapter.isItemSelected()) {
            selectionFolder = getFileManager().getRelativeParent(selectionFolder);
            selectionFolder = checkAndShowFatalErrorDialog(selectionFolder);
        }
    }

    private void prepareFolderRecyclerView(Bundle adapterState) {
        Log.d(FolderChooseDialog.class.getName(), "prepareFolderRecyclerView");
        fileEntriesRecyclerView = dialogView.findViewById(R.id.listview_dialog_folder_choose_file_entries);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        fileEntriesRecyclerView.setLayoutManager(layoutManager);
        fileEntriesRecyclerView.setItemAnimator(new DefaultItemAnimator());
        RecyclerView.Adapter<?> adapter = adapterState == null ? createAdapter() : restoreAdapter(adapterState);
        fileEntriesRecyclerView.setAdapter(adapter);
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
        FolderChooseSupport folderChooseSupport = getFolderChooseSupport();
        if (folderChooseSupport != null) {
            folderChooseSupport.onFolderChooseDialogOkClicked(this);
        } else {
            Log.e(FolderChooseDialog.class.getName(), "folderChooseSupport is null");
            dismiss();
        }
    }

    private void onCancelClicked(@SuppressWarnings("unused") View view) {
        Log.d(FolderChooseDialog.class.getName(), "onCancelClicked");
        FolderChooseSupport folderChooseSupport = getFolderChooseSupport();
        if (folderChooseSupport != null) {
            folderChooseSupport.onFolderChooseDialogCancelClicked(this);
        } else {
            Log.e(FolderChooseDialog.class.getName(), "folderChooseSupport is null");
            dismiss();
        }
    }

    private String getAbsoluteFolder(String root, String folder) {
        return getFileManager().getAbsoluteFolder(root, folder);
    }

    public void onFileEntryClicked(View view, int position) {
        Log.d(FolderChooseDialog.class.getName(), "onFileEntryClicked, position is " + position);
        selectEntry(position);
    }

    public void onFileOpenClicked(View view, int position) {
        Log.d(FolderChooseDialog.class.getName(), "onFileOpenClicked, position is " + position);
        FileEntry selectedEntry = getAdapter().getItem(position);
        if (selectedEntry == null) {
            Log.e(FolderChooseDialog.class.getName(), "selected entry is null");
            return;
        }
        if (!selectedEntry.isDirectory()) {
            Log.d(FolderChooseDialog.class.getName(), "selected entry " + selectedEntry + " is a file. Select skipped.");
            return;
        }
        List<FileEntry> entries;
        if (!selectEntry(position)) {
            Log.e(FolderChooseDialog.class.getName(), "Error selecting entry for position " + position);
            return;
        }
        if (selectedEntry.isParent()) {
            String folder = getAbsoluteFolder(getRoot(), getSelectionFolder());
            if (folder != null) {
                entries = readFiles(getFileManager().getAbsoluteParent(getRoot(), folder));
            } else {
                entries = Collections.emptyList();
                showErrorDialog(getResources().getString(R.string.text_dialog_general_error_file_access));
            }
        } else {
            entries = readFiles(getFileManager().getAbsoluteFolder(getRoot(), getSelectionFolder()));
        }
        FileEntryAdapter adapter = getAdapter();
        adapter.unselectItem();
        adapter.replaceItems(entries);
        if (selectedEntry.isParent()) {
            adapter.selectItemByName(getSelectionFolder());
        }
        adapter.notifyDataSetChanged();
        if (!adapter.isItemSelected() && selectedEntry.isParent()) {
            setFolders(getFileManager().getRelativeParent(getSelectionFolder()));
        }
    }

    private boolean selectEntry(int position) {
        Log.d(FolderChooseDialog.class.getName(), "selectEntry, position is " + position);
        FileEntry selectedEntry = getAdapter().getItem(position);
        if (selectedEntry == null) {
            Log.e(FolderChooseDialog.class.getName(), "selected entry is null");
            return false;
        }
        if (!selectedEntry.isDirectory()) {
            Log.d(FolderChooseDialog.class.getName(), "selected entry " + selectedEntry + " is a file. Select skipped.");
            return false;
        }
        String folderName = selectedEntry.getName();
        Log.d(FolderChooseDialog.class.getName(), "Prepare selected folder name " + folderName);
        IFileManager fileManager = getFileManager();
        String nestedFolder;
        boolean isItemSelected = getAdapter().isItemSelected();
        boolean isParentItemSelected = getAdapter().isParentItemSelected();
        if (isItemSelected && !isParentItemSelected) {
            Log.d(FolderChooseDialog.class.getName(), "A non-parent item is currently selected.");
            if (selectedEntry.isParent()) {
                Log.d(FolderChooseDialog.class.getName(), "New selected item is the parent item. Selecting parent folder.");
                nestedFolder = fileManager.getRelativeParent(getSelectionFolder());
            } else {
                Log.d(FolderChooseDialog.class.getName(), "New selected item is not the parent item. Selecting sibling folder.");
                nestedFolder = fileManager.getRelativeSibling(getSelectionFolder(), folderName);
            }
        } else {
            if (isParentItemSelected) {
                Log.d(FolderChooseDialog.class.getName(), "The parent item is currenty selected.");
            } else {
                Log.d(FolderChooseDialog.class.getName(), "No item is currenty selected.");
            }
            if (selectedEntry.isParent()) {
                Log.d(FolderChooseDialog.class.getName(), "New selected item is the parent item. Keeping selected folder.");
                nestedFolder = getSelectionFolder();
            } else {
                Log.d(FolderChooseDialog.class.getName(), "New selected item is not the parent item. Nesting folder.");
                nestedFolder = fileManager.getNestedFolder(getSelectionFolder(), folderName);
            }
        }
        if (nestedFolder == null) {
            Log.e(FolderChooseDialog.class.getName(), "Error preparing selected folder");
            return false;
        }
        Log.d(FolderChooseDialog.class.getName(), "Selected folder name is " + nestedFolder);
        getAdapter().selectItem(position);
        setFolders(nestedFolder);
        return true;
    }

    private void setFolders(String nestedFolder) {
        if (nestedFolder == null) {
            resetFiles();
            showErrorDialog(getResources().getString(R.string.text_dialog_general_error_file_access));
            return;
        }
        if (folderChooseTextWatcher != null) {
            folderEditText.removeTextChangedListener(folderChooseTextWatcher);
            folderChooseTextWatcher = null;
        }
        folderEditText.setText(nestedFolder);
        String folder = getAbsoluteFolder(getRoot(), nestedFolder);
        if (folder != null) {
            absoluteFolderText.setText(folder);
        }
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
        FolderChooseSupport folderChooseSupport = getFolderChooseSupport();
        if (folderChooseSupport != null) {
            return folderChooseSupport.getFileManager();
        }
        Log.e(FolderChooseDialog.class.getName(), "folderChooseSupport is null");
        return new SystemFileManager(getContext());
    }

    private RecyclerView.Adapter<?> restoreAdapter(Bundle adapterState) {
        Log.d(FolderChooseDialog.class.getName(), "restoreAdapter");
        FileEntryAdapter adapter = new FileEntryAdapter(Collections.emptyList(), this);
        adapter.restoreStateFromBundle(adapterState);
        return adapter;
    }

    private RecyclerView.Adapter<?> createAdapter() {
        Log.d(FolderChooseDialog.class.getName(), "createAdapter");
        String absoluteFolder = getAbsoluteFolder(getRoot(), getSelectionFolder());
        if (absoluteFolder == null) {
            Log.e(FolderChooseDialog.class.getName(), "File manager returned null as parent");
            showErrorDialog(getResources().getString(R.string.text_dialog_general_error_list_folder_files));
            return new FileEntryAdapter(Collections.emptyList(), this);
        }
        IFileManager fileManager = getFileManager();
        String parent = fileManager.getAbsoluteParent(getRoot(), absoluteFolder);
        if (parent == null) {
            Log.e(FolderChooseDialog.class.getName(), "File manager returned null as parent");
            showErrorDialog(getResources().getString(R.string.text_dialog_general_error_list_folder_files));
            return new FileEntryAdapter(Collections.emptyList(), this);
        }
        List<FileEntry> entries = readFiles(parent);
        FileEntryAdapter adapter = new FileEntryAdapter(entries, this);
        adapter.selectItemByName(getSelectionFolder());
        if (!adapter.isItemSelected()) {
            selectionFolder = fileManager.getRelativeParent(selectionFolder);
            if (selectionFolder == null) {
                Log.e(FolderChooseDialog.class.getName(), "selectionFolder is null");
                showErrorDialog(getResources().getString(R.string.text_dialog_general_error_file_access));
                adapter = new FileEntryAdapter(Collections.emptyList(), this);
            }
        }
        adapter.notifyDataSetChanged();
        return adapter;
    }

    private List<FileEntry> readFiles(String folder) {
        Log.d(FolderChooseDialog.class.getName(), "readFiles, folder is " + folder);
        if (folder == null) {
            Log.e(FolderChooseDialog.class.getName(), "File manager returned null as folder file list");
            showErrorDialog(getResources().getString(R.string.text_dialog_general_error_list_folder_files));
            return Collections.emptyList();
        }
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

    private String checkAndShowFatalErrorDialog(String folder) {
        Log.d(FolderChooseDialog.class.getName(), "checkAndShowFatalErrorDialog for folder " + folder);
        if (folder != null) {
            return folder;
        }
        Log.e(FolderChooseDialog.class.getName(), "Folder is null");
        resetFiles();
        showErrorDialog(getResources().getString(R.string.text_dialog_general_error_file_access));
        return "";
    }

    private void resetFiles() {
        Log.d(FolderChooseDialog.class.getName(), "resetFiles");
        FileEntryAdapter adapter = getAdapter();
        adapter.unselectItem();
        adapter.replaceItems(Collections.emptyList());
        adapter.notifyDataSetChanged();
    }

    private boolean onFolderEditTextLongClicked(View view) {
        Log.d(FolderChooseDialog.class.getName(), "onFolderEditTextLongClicked");
        showContextOptionsDialog((EditText) view);
        return true;
    }

    private void showContextOptionsDialog(EditText editText) {
        Log.d(FolderChooseDialog.class.getName(), "showContextOptionsDialog");
        new ContextOptionsSupportManager(getParentFragmentManager(), getClipboardManager()).showContextOptionsDialog(editText);
    }

    @Override
    public void onContextOptionsDialogClicked(ContextOptionsDialog contextOptionsDialog, int sourceResourceId, ContextOption option) {
        Log.d(SettingsInputDialog.class.getName(), "onContextOptionsDialogEntryClicked, sourceResourceId is " + sourceResourceId + ", option is " + option);
        ContextOptionsSupportManager contextOptionsSupportManager = new ContextOptionsSupportManager(getParentFragmentManager(), getClipboardManager());
        if (folderEditText.getId() == sourceResourceId) {
            Log.e(SettingsInputDialog.class.getName(), "Source field is the correct folder input field.");
            contextOptionsSupportManager.handleContextOption(folderEditText, option);
            folderEditText.setSelection(folderEditText.getText().length());
        } else {
            Log.e(SettingsInputDialog.class.getName(), "Source field is undefined.");
        }
        contextOptionsDialog.dismiss();
    }

    private void showErrorDialog(String errorMessage) {
        showErrorDialog(errorMessage, Typeface.BOLD);
    }

    private void showErrorDialog(String errorMessage, int typeface) {
        Log.d(FolderChooseDialog.class.getName(), "showErrorDialog with message " + errorMessage);
        GeneralErrorDialog errorDialog = new GeneralErrorDialog();
        Bundle bundle = BundleUtil.stringToBundle(GeneralErrorDialog.class.getSimpleName(), errorMessage);
        bundle.putInt(errorDialog.getTypefaceStyleKey(), typeface);
        errorDialog.setArguments(bundle);
        errorDialog.show(getParentFragmentManager(), GeneralErrorDialog.class.getName());
    }

    private FolderChooseSupport getFolderChooseSupport() {
        Log.d(FolderChooseDialog.class.getName(), "getFolderChooseSupport");
        Activity activity = getActivity();
        if (activity == null) {
            Log.e(FolderChooseDialog.class.getName(), "getFolderChooseSupport, activity is null");
            return null;
        }
        if (!(activity instanceof FolderChooseSupport)) {
            Log.e(FolderChooseDialog.class.getName(), "getFolderChooseSupport, activity is not an instance of " + FolderChooseSupport.class.getSimpleName());
            return null;
        }
        return (FolderChooseSupport) activity;
    }
}
