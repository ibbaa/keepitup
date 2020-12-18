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
import android.widget.LinearLayout;
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
import de.ibba.keepitup.ui.FileChooseSupport;
import de.ibba.keepitup.ui.GlobalSettingsActivity;
import de.ibba.keepitup.ui.adapter.FileEntryAdapter;
import de.ibba.keepitup.ui.clipboard.IClipboardManager;
import de.ibba.keepitup.ui.clipboard.SystemClipboardManager;
import de.ibba.keepitup.util.BundleUtil;
import de.ibba.keepitup.util.StringUtil;

public class FileChooseDialog extends DialogFragment implements ContextOptionsSupport {

    public enum Type {
        DOWNLOADFOLDER,
    }

    public enum Mode {
        FILE,
        FOLDER
    }

    private View dialogView;
    private TextView absoluteFolderText;
    private EditText folderEditText;
    private EditText fileEditText;
    private FolderChooseWatcher folderChooseTextWatcher;
    private FileChooseWatcher fileChooseTextWatcher;
    private CheckBox showFilesCheckBox;
    private RecyclerView fileEntriesRecyclerView;
    private Mode mode;
    private Type type;
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
        Log.d(FileChooseDialog.class.getName(), "onCreate");
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.DialogTheme);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(FileChooseDialog.class.getName(), "onCreateView");
        dialogView = inflater.inflate(R.layout.dialog_file_choose, container);
        mode = getMode();
        type = getType();
        Log.d(FileChooseDialog.class.getName(), "mode is " + mode);
        Log.d(FileChooseDialog.class.getName(), "type is " + type);
        boolean containsSavedState = containsSavedState(savedInstanceState);
        Log.d(FileChooseDialog.class.getName(), "containsSavedState is " + containsSavedState);
        String folder = containsSavedState ? savedInstanceState.getString(getSelectionFolderKey()) : BundleUtil.stringFromBundle(getFolderKey(), requireArguments());
        String file = "";
        if (isFileMode()) {
            file = BundleUtil.stringFromBundle(getFileKey(), requireArguments());
        }
        Log.d(FileChooseDialog.class.getName(), "folder is " + folder);
        Log.d(FileChooseDialog.class.getName(), "file is " + file);
        Bundle adapterState = containsSavedState ? savedInstanceState.getBundle(getFileEntryAdapterKey()) : null;
        prepareFolderAbsolute(folder, file);
        prepareFolder(folder);
        prepareFile(file);
        prepareShowFilesCheckBox();
        prepareFolderRecyclerView(adapterState);
        prepareOkCancelImageButtons();
        return dialogView;
    }

    private Mode getMode() {
        Log.d(FileChooseDialog.class.getName(), "getMode");
        String modeString = BundleUtil.stringFromBundle(getFileModeKey(), requireArguments());
        if (StringUtil.isEmpty(modeString)) {
            Log.d(FileChooseDialog.class.getName(), "No mode specified. Using " + Mode.FOLDER);
            return Mode.FOLDER;
        }
        try {
            return Mode.valueOf(modeString);
        } catch (IllegalArgumentException exc) {
            Log.e(FileChooseDialog.class.getName(), Mode.class.getSimpleName() + "." + modeString + " does not exist", exc);
            Log.d(FileChooseDialog.class.getName(), "No mode specified. Using " + Mode.FOLDER);
            return Mode.FOLDER;
        }
    }

    private Type getType() {
        Log.d(FileChooseDialog.class.getName(), "getType");
        String typeString = BundleUtil.stringFromBundle(getTypeKey(), requireArguments());
        if (StringUtil.isEmpty(typeString)) {
            Log.d(FileChooseDialog.class.getName(), "No type specified.");
            return null;
        }
        try {
            return Type.valueOf(typeString);
        } catch (IllegalArgumentException exc) {
            Log.e(ConfirmDialog.class.getName(), Type.class.getSimpleName() + "." + typeString + " does not exist");
            Log.d(FileChooseDialog.class.getName(), "No mode specified.");
            return null;
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Log.d(FileChooseDialog.class.getName(), "onSaveInstanceState");
        super.onSaveInstanceState(outState);
        outState.putString(getSelectionFolderKey(), selectionFolder);
        Bundle adapterBundle = getAdapter().saveStateToBundle();
        outState.putBundle(getFileEntryAdapterKey(), adapterBundle);
    }

    private boolean containsSavedState(Bundle savedInstanceState) {
        Log.d(FileChooseDialog.class.getName(), "containsSavedState");
        if (savedInstanceState == null) {
            Log.d(FileChooseDialog.class.getName(), "savedInstanceState bundle is null");
            return false;
        }
        return savedInstanceState.containsKey(getSelectionFolderKey()) && savedInstanceState.containsKey(getFileEntryAdapterKey());
    }

    public String getFolderRootKey() {
        return FileChooseDialog.class.getSimpleName() + "Root";
    }

    public String getFolderKey() {
        return FileChooseDialog.class.getSimpleName() + "Folder";
    }

    public String getFileKey() {
        return FileChooseDialog.class.getSimpleName() + "File";
    }

    public String getFileModeKey() {
        return FileChooseDialog.Mode.class.getSimpleName();
    }

    public String getTypeKey() {
        return FileChooseDialog.Type.class.getSimpleName();
    }

    private String getSelectionFolderKey() {
        return FileChooseDialog.class.getSimpleName() + "SelectionFolder";
    }

    private String getFileEntryAdapterKey() {
        return FileChooseDialog.class.getSimpleName() + "FileEntryAdapter";
    }

    public boolean isFileMode() {
        return Mode.FILE.equals(mode);
    }

    public String getFolder() {
        return StringUtil.notNull(folderEditText.getText());
    }

    public String getFile() {
        return StringUtil.notNull(fileEditText.getText());
    }

    public boolean isShowFiles() {
        return showFilesCheckBox.isChecked() || isFileMode();
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

    private void prepareFolderAbsolute(String folder, String file) {
        Log.d(FileChooseDialog.class.getName(), "prepareFolderAbsolute");
        absoluteFolderText = dialogView.findViewById(R.id.textview_dialog_file_choose_absolute);
        String absoluteFolder = getAbsolutePath(getRoot(), folder);
        if (absoluteFolder != null) {
            if (isFileMode()) {
                absoluteFolder = getAbsolutePath(absoluteFolder, file);
            }
            if (absoluteFolder != null) {
                absoluteFolderText.setText(absoluteFolder);
            }
        }
    }

    private void prepareFolder(String folder) {
        Log.d(FileChooseDialog.class.getName(), "prepareFolder, folder is " + folder);
        folderEditText = dialogView.findViewById(R.id.edittext_dialog_file_choose_folder);
        folderEditText.setOnLongClickListener(this::onFolderEditTextLongClicked);
        folderEditText.setText(folder);
        selectionFolder = folder;
        prepareFolderChooseTextWatcher();
    }

    private void prepareFolderChooseTextWatcher() {
        Log.d(FileChooseDialog.class.getName(), "prepareFolderChooseTextWatcher");
        if (folderChooseTextWatcher != null) {
            folderEditText.removeTextChangedListener(folderChooseTextWatcher);
            folderChooseTextWatcher = null;
        }
        folderChooseTextWatcher = new FolderChooseWatcher(this);
        folderEditText.addTextChangedListener(folderChooseTextWatcher);
    }

    private void prepareFile(String file) {
        Log.d(FileChooseDialog.class.getName(), "prepareFile, file is " + file);
        fileEditText = dialogView.findViewById(R.id.edittext_dialog_file_choose_file);
        fileEditText.setText(file);
        LinearLayout fileLayout = dialogView.findViewById(R.id.linearlayout_dialog_file_choose_file);
        if (isFileMode()) {
            fileEditText.setOnLongClickListener(this::onFileEditTextLongClicked);
            fileLayout.setVisibility(View.VISIBLE);
        } else {
            fileEditText.setOnLongClickListener(null);
            fileLayout.setVisibility(View.GONE);
        }
        prepareFileChooseTextWatcher();
    }

    private void prepareFileChooseTextWatcher() {
        Log.d(FileChooseDialog.class.getName(), "prepareFileChooseTextWatcher");
        if (fileChooseTextWatcher != null) {
            fileEditText.removeTextChangedListener(fileChooseTextWatcher);
            fileChooseTextWatcher = null;
        }
        fileChooseTextWatcher = new FileChooseWatcher(this);
        fileEditText.addTextChangedListener(fileChooseTextWatcher);
    }

    private void prepareShowFilesCheckBox() {
        Log.d(FileChooseDialog.class.getName(), "prepareShowFilesCheckBox");
        showFilesCheckBox = dialogView.findViewById(R.id.checkbox_dialog_file_choose_show_files);
        LinearLayout showFilesLayout = dialogView.findViewById(R.id.linearlayout_dialog_file_choose_show_files);
        if (isFileMode()) {
            showFilesLayout.setVisibility(View.GONE);
            showFilesCheckBox.setOnCheckedChangeListener(null);
        } else {
            showFilesLayout.setVisibility(View.VISIBLE);
            showFilesCheckBox.setOnCheckedChangeListener(this::onShowFilesCheckedChanged);
        }
    }

    private void onShowFilesCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.d(GlobalSettingsActivity.class.getName(), "onShowFilesCheckedChanged, new value is " + isChecked);
        FileEntryAdapter adapter = getAdapter();
        adapter.unselectItem();
        adapter.notifyDataSetChanged();
        adapter.selectFolderByName(getSelectionFolder());
        if (!adapter.isItemSelected()) {
            selectionFolder = getFileManager().getRelativeParent(selectionFolder);
            selectionFolder = checkAndShowFatalErrorDialog(selectionFolder);
        }
    }

    private void prepareFolderRecyclerView(Bundle adapterState) {
        Log.d(FileChooseDialog.class.getName(), "prepareFolderRecyclerView");
        fileEntriesRecyclerView = dialogView.findViewById(R.id.listview_dialog_file_choose_file_entries);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        fileEntriesRecyclerView.setLayoutManager(layoutManager);
        fileEntriesRecyclerView.setItemAnimator(new DefaultItemAnimator());
        RecyclerView.Adapter<?> adapter = adapterState == null ? createAdapter() : restoreAdapter(adapterState);
        fileEntriesRecyclerView.setAdapter(adapter);
    }

    private void prepareOkCancelImageButtons() {
        Log.d(FileChooseDialog.class.getName(), "prepareOkCancelImageButtons");
        ImageView okImage = dialogView.findViewById(R.id.imageview_dialog_file_choose_ok);
        ImageView cancelImage = dialogView.findViewById(R.id.imageview_dialog_file_choose_cancel);
        okImage.setOnClickListener(this::onOkClicked);
        cancelImage.setOnClickListener(this::onCancelClicked);
    }

    private void onOkClicked(View view) {
        Log.d(FileChooseDialog.class.getName(), "onOkClicked");
        FileChooseSupport folderChooseSupport = getFolderChooseSupport();
        if (folderChooseSupport != null) {
            folderChooseSupport.onFileChooseDialogOkClicked(this, type);
        } else {
            Log.e(FileChooseDialog.class.getName(), "folderChooseSupport is null");
            dismiss();
        }
    }

    private void onCancelClicked(View view) {
        Log.d(FileChooseDialog.class.getName(), "onCancelClicked");
        FileChooseSupport folderChooseSupport = getFolderChooseSupport();
        if (folderChooseSupport != null) {
            folderChooseSupport.onFileChooseDialogCancelClicked(this);
        } else {
            Log.e(FileChooseDialog.class.getName(), "folderChooseSupport is null");
            dismiss();
        }
    }

    private String getAbsolutePath(String root, String path) {
        return getFileManager().getAbsolutePath(root, path);
    }

    public void onFileEntryClicked(View view, int position) {
        Log.d(FileChooseDialog.class.getName(), "onFileEntryClicked, position is " + position);
        selectEntry(position);
    }

    public void onFileOpenClicked(View view, int position) {
        Log.d(FileChooseDialog.class.getName(), "onFileOpenClicked, position is " + position);
        openEntry(position);
    }

    private boolean openEntry(int position) {
        Log.d(FileChooseDialog.class.getName(), "openEntry, position is " + position);
        FileEntry selectedEntry = getAdapter().getItem(position);
        if (selectedEntry == null) {
            Log.e(FileChooseDialog.class.getName(), "selected entry is null");
            return false;
        }
        if (!selectedEntry.isDirectory()) {
            Log.d(FileChooseDialog.class.getName(), "selected entry " + selectedEntry + " is a file. Select skipped.");
            return false;
        }
        List<FileEntry> entries;
        if (!selectFolderEntry(position, selectedEntry)) {
            Log.e(FileChooseDialog.class.getName(), "Error selecting entry for position " + position);
            return false;
        }
        if (selectedEntry.isParent()) {
            String folder = getAbsolutePath(getRoot(), getSelectionFolder());
            if (folder != null) {
                entries = readFiles(getFileManager().getAbsoluteParent(getRoot(), folder));
            } else {
                entries = Collections.emptyList();
                showErrorDialog(getResources().getString(R.string.text_dialog_general_error_file_access));
            }
        } else {
            entries = readFiles(getFileManager().getAbsolutePath(getRoot(), getSelectionFolder()));
        }
        FileEntryAdapter adapter = getAdapter();
        adapter.unselectItem();
        adapter.replaceItems(entries);
        if (isFileMode()) {
            adapter.selectFileByName(getFile());
        } else {
            if (selectedEntry.isParent()) {
                adapter.selectFolderByName(getSelectionFolder());
            }
        }
        adapter.notifyDataSetChanged();
        if (!adapter.isFolderItemSelected() && selectedEntry.isParent()) {
            setFolders(getFileManager().getRelativeParent(getSelectionFolder()));
        }
        return true;
    }

    private boolean selectEntry(int position) {
        Log.d(FileChooseDialog.class.getName(), "selectEntry, position is " + position);
        FileEntry selectedEntry = getAdapter().getItem(position);
        if (selectedEntry == null) {
            Log.e(FileChooseDialog.class.getName(), "selected entry is null");
            return false;
        }
        if (!isFileMode()) {
            if (!selectedEntry.isDirectory()) {
                Log.d(FileChooseDialog.class.getName(), "Folder mode but selected entry " + selectedEntry + " is a file. Select skipped.");
                return false;
            }
            return selectFolderEntry(position, selectedEntry);
        }
        if (selectedEntry.isDirectory()) {
            Log.d(FileChooseDialog.class.getName(), "File mode but selected entry " + selectedEntry + " is a folder. Opening folder.");
            return selectFolderEntry(position, selectedEntry);
        }
        return selectFileEntry(position, selectedEntry);
    }

    private boolean selectFileEntry(int position, FileEntry selectedEntry) {
        Log.d(FileChooseDialog.class.getName(), "selectFileEntry, position is " + position + ", selectedEntry is " + selectedEntry);
        IFileManager fileManager = getFileManager();
        boolean isFolderItemSelected = getAdapter().isFolderItemSelected();
        boolean isParentItemSelected = getAdapter().isParentItemSelected();
        String nestedFolder;
        if (isFolderItemSelected && !isParentItemSelected) {
            nestedFolder = fileManager.getRelativeParent(getSelectionFolder());
        } else {
            nestedFolder = getSelectionFolder();
        }
        if (nestedFolder == null) {
            Log.e(FileChooseDialog.class.getName(), "Error preparing selected folder");
            return false;
        }
        getAdapter().selectItem(position);
        setFolders(nestedFolder);
        setFile(selectedEntry.getName());
        return true;
    }

    private void setFile(String file) {
        if (file == null) {
            resetFiles();
            showErrorDialog(getResources().getString(R.string.text_dialog_general_error_file_access));
            return;
        }
        String absolutePath = getAbsolutePath(getRoot(), getFolder());
        if (absolutePath != null) {
            absolutePath = getAbsolutePath(absolutePath, file);
            if (absolutePath != null) {
                absoluteFolderText.setText(absolutePath);
            }
        }
        if (fileChooseTextWatcher != null) {
            fileEditText.removeTextChangedListener(fileChooseTextWatcher);
            fileChooseTextWatcher = null;
        }
        fileEditText.setText(file);
        fileChooseTextWatcher = new FileChooseWatcher(this);
        fileEditText.addTextChangedListener(fileChooseTextWatcher);
    }

    private boolean selectFolderEntry(int position, FileEntry selectedEntry) {
        Log.d(FileChooseDialog.class.getName(), "selectFolderEntry, position is " + position + ", selectedEntry is " + selectedEntry);
        String folderName = selectedEntry.getName();
        Log.d(FileChooseDialog.class.getName(), "Prepare selected folder name " + folderName);
        IFileManager fileManager = getFileManager();
        String nestedFolder;
        boolean isFolderItemSelected = getAdapter().isFolderItemSelected();
        boolean isParentItemSelected = getAdapter().isParentItemSelected();
        if (isFolderItemSelected && !isParentItemSelected) {
            Log.d(FileChooseDialog.class.getName(), "A non-parent item is currently selected.");
            if (selectedEntry.isParent()) {
                Log.d(FileChooseDialog.class.getName(), "New selected item is the parent item. Selecting parent folder.");
                nestedFolder = fileManager.getRelativeParent(getSelectionFolder());
            } else {
                Log.d(FileChooseDialog.class.getName(), "New selected item is not the parent item. Selecting sibling folder.");
                nestedFolder = fileManager.getRelativeSibling(getSelectionFolder(), folderName);
            }
        } else {
            if (isParentItemSelected) {
                Log.d(FileChooseDialog.class.getName(), "The parent item is currently selected.");
            } else {
                Log.d(FileChooseDialog.class.getName(), "No item is currently selected.");
            }
            if (selectedEntry.isParent()) {
                Log.d(FileChooseDialog.class.getName(), "New selected item is the parent item. Keeping selected folder.");
                nestedFolder = getSelectionFolder();
            } else {
                Log.d(FileChooseDialog.class.getName(), "New selected item is not the parent item. Nesting folder.");
                nestedFolder = fileManager.getNestedPath(getSelectionFolder(), folderName);
            }
        }
        if (nestedFolder == null) {
            Log.e(FileChooseDialog.class.getName(), "Error preparing selected folder");
            return false;
        }
        Log.d(FileChooseDialog.class.getName(), "Selected folder name is " + nestedFolder);
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
        String folder = getAbsolutePath(getRoot(), nestedFolder);
        if (folder != null) {
            if (isFileMode()) {
                folder = getAbsolutePath(folder, getFile());
            }
            if (folder != null) {
                absoluteFolderText.setText(folder);
            }
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
        Log.d(FileChooseDialog.class.getName(), "getFileManager");
        FileChooseSupport folderChooseSupport = getFolderChooseSupport();
        if (folderChooseSupport != null) {
            return folderChooseSupport.getFileManager();
        }
        Log.e(FileChooseDialog.class.getName(), "folderChooseSupport is null");
        return new SystemFileManager(getContext());
    }

    private RecyclerView.Adapter<?> restoreAdapter(Bundle adapterState) {
        Log.d(FileChooseDialog.class.getName(), "restoreAdapter");
        FileEntryAdapter adapter = new FileEntryAdapter(Collections.emptyList(), this);
        adapter.restoreStateFromBundle(adapterState);
        return adapter;
    }

    private RecyclerView.Adapter<?> createAdapter() {
        Log.d(FileChooseDialog.class.getName(), "createAdapter");
        String absoluteFolder = getAbsolutePath(getRoot(), getSelectionFolder());
        if (absoluteFolder == null) {
            Log.e(FileChooseDialog.class.getName(), "File manager returned null as parent");
            showErrorDialog(getResources().getString(R.string.text_dialog_general_error_list_folder_files));
            return new FileEntryAdapter(Collections.emptyList(), this);
        }
        IFileManager fileManager = getFileManager();
        FileEntryAdapter adapter;
        if (!isFileMode()) {
            String parent = fileManager.getAbsoluteParent(getRoot(), absoluteFolder);
            if (parent == null) {
                Log.e(FileChooseDialog.class.getName(), "File manager returned null as parent");
                showErrorDialog(getResources().getString(R.string.text_dialog_general_error_list_folder_files));
                return new FileEntryAdapter(Collections.emptyList(), this);
            }
            List<FileEntry> entries = readFiles(parent);
            adapter = new FileEntryAdapter(entries, this);
            adapter.selectFolderByName(getSelectionFolder());
        } else {
            List<FileEntry> entries = readFiles(absoluteFolder);
            adapter = new FileEntryAdapter(entries, this);
            adapter.selectFileByName(getFile());
        }
        if (!adapter.isItemSelected() && !isFileMode()) {
            selectionFolder = fileManager.getRelativeParent(selectionFolder);
            if (selectionFolder == null) {
                Log.e(FileChooseDialog.class.getName(), "selectionFolder is null");
                showErrorDialog(getResources().getString(R.string.text_dialog_general_error_file_access));
                adapter = new FileEntryAdapter(Collections.emptyList(), this);
            }
        }
        adapter.notifyDataSetChanged();
        return adapter;
    }

    private List<FileEntry> readFiles(String folder) {
        Log.d(FileChooseDialog.class.getName(), "readFiles, folder is " + folder);
        if (folder == null) {
            Log.e(FileChooseDialog.class.getName(), "File manager returned null as folder file list");
            showErrorDialog(getResources().getString(R.string.text_dialog_general_error_list_folder_files));
            return Collections.emptyList();
        }
        IFileManager fileManager = getFileManager();
        String root = getRoot();
        List<FileEntry> entries = fileManager.getFiles(root, folder);
        if (entries == null) {
            Log.e(FileChooseDialog.class.getName(), "File manager returned null as folder file list");
            showErrorDialog(getResources().getString(R.string.text_dialog_general_error_list_folder_files));
            return Collections.emptyList();
        }
        Log.d(FileChooseDialog.class.getName(), "File manager returned the following file entries: " + (entries.isEmpty() ? "no file entries" : ""));
        for (FileEntry entry : entries) {
            Log.d(FileChooseDialog.class.getName(), entry.toString());
        }
        return entries;
    }

    private String checkAndShowFatalErrorDialog(String folder) {
        Log.d(FileChooseDialog.class.getName(), "checkAndShowFatalErrorDialog for folder " + folder);
        if (folder != null) {
            return folder;
        }
        Log.e(FileChooseDialog.class.getName(), "Folder is null");
        resetFiles();
        showErrorDialog(getResources().getString(R.string.text_dialog_general_error_file_access));
        return "";
    }

    private void resetFiles() {
        Log.d(FileChooseDialog.class.getName(), "resetFiles");
        FileEntryAdapter adapter = getAdapter();
        adapter.unselectItem();
        adapter.replaceItems(Collections.emptyList());
        adapter.notifyDataSetChanged();
    }

    private boolean onFolderEditTextLongClicked(View view) {
        Log.d(FileChooseDialog.class.getName(), "onFolderEditTextLongClicked");
        showContextOptionsDialog((EditText) view);
        return true;
    }

    private boolean onFileEditTextLongClicked(View view) {
        Log.d(FileChooseDialog.class.getName(), "onFileEditTextLongClicked");
        showContextOptionsDialog((EditText) view);
        return true;
    }

    private void showContextOptionsDialog(EditText editText) {
        Log.d(FileChooseDialog.class.getName(), "showContextOptionsDialog");
        new ContextOptionsSupportManager(getParentFragmentManager(), getClipboardManager()).showContextOptionsDialog(editText);
    }

    @Override
    public void onContextOptionsDialogClicked(ContextOptionsDialog contextOptionsDialog, int sourceResourceId, ContextOption option) {
        Log.d(SettingsInputDialog.class.getName(), "onContextOptionsDialogEntryClicked, sourceResourceId is " + sourceResourceId + ", option is " + option);
        ContextOptionsSupportManager contextOptionsSupportManager = new ContextOptionsSupportManager(getParentFragmentManager(), getClipboardManager());
        if (folderEditText.getId() == sourceResourceId) {
            Log.d(SettingsInputDialog.class.getName(), "Source field is the folder input field.");
            contextOptionsSupportManager.handleContextOption(folderEditText, option);
            folderEditText.setSelection(folderEditText.getText().length());
        } else if (fileEditText.getId() == sourceResourceId) {
            Log.d(SettingsInputDialog.class.getName(), "Source field is the file input field.");
            contextOptionsSupportManager.handleContextOption(fileEditText, option);
            fileEditText.setSelection(fileEditText.getText().length());
        } else {
            Log.e(SettingsInputDialog.class.getName(), "Source field is undefined.");
        }
        contextOptionsDialog.dismiss();
    }

    private void showErrorDialog(String errorMessage) {
        showErrorDialog(errorMessage, Typeface.BOLD);
    }

    private void showErrorDialog(String errorMessage, int typeface) {
        Log.d(FileChooseDialog.class.getName(), "showErrorDialog with message " + errorMessage);
        GeneralErrorDialog errorDialog = new GeneralErrorDialog();
        Bundle bundle = BundleUtil.stringToBundle(errorDialog.getMessageKey(), errorMessage);
        bundle.putInt(errorDialog.getTypefaceStyleKey(), typeface);
        errorDialog.setArguments(bundle);
        errorDialog.show(getParentFragmentManager(), GeneralErrorDialog.class.getName());
    }

    private FileChooseSupport getFolderChooseSupport() {
        Log.d(FileChooseDialog.class.getName(), "getFolderChooseSupport");
        Activity activity = getActivity();
        if (activity == null) {
            Log.e(FileChooseDialog.class.getName(), "getFolderChooseSupport, activity is null");
            return null;
        }
        if (!(activity instanceof FileChooseSupport)) {
            Log.e(FileChooseDialog.class.getName(), "getFolderChooseSupport, activity is not an instance of " + FileChooseSupport.class.getSimpleName());
            return null;
        }
        return (FileChooseSupport) activity;
    }
}
