/*
 * Copyright (c) 2023. Alwin Ibba
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.ibbaa.keepitup.ui.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.model.FileEntry;
import net.ibbaa.keepitup.ui.dialog.FileChooseDialog;
import net.ibbaa.keepitup.util.BundleUtil;

import java.util.ArrayList;
import java.util.List;

public class FileEntryAdapter extends RecyclerView.Adapter<FileEntryViewHolder> {

    private final List<FileEntry> fileEntries;
    private final List<FileEntry> fileEntriesFoldersOnly;
    private final FileChooseDialog folderChooseDialog;
    private final RecyclerView fileEntriesRecyclerView;
    private int selected;

    public FileEntryAdapter(List<FileEntry> fileEntries, FileChooseDialog folderChooseDialog) {
        this.fileEntries = new ArrayList<>();
        this.fileEntriesFoldersOnly = new ArrayList<>();
        this.folderChooseDialog = folderChooseDialog;
        this.fileEntriesRecyclerView = folderChooseDialog.getFileEntriesRecyclerView();
        this.selected = -1;
        replaceItems(fileEntries);
    }

    @NonNull
    @Override
    public FileEntryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Log.d(FileEntryAdapter.class.getName(), "onCreateViewHolder");
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_file_entry, viewGroup, false);
        return new FileEntryViewHolder(itemView, folderChooseDialog);
    }

    @Override
    public void onBindViewHolder(@NonNull FileEntryViewHolder fileEntryViewHolder, int position) {
        Log.d(FileEntryAdapter.class.getName(), "onBindViewHolder");
        if (position < getFileEntries().size()) {
            FileEntry fileEntry = getFileEntries().get(position);
            bindFileName(fileEntryViewHolder, fileEntry, position);
        }
    }

    private void bindFileName(@NonNull FileEntryViewHolder fileEntryViewHolder, FileEntry fileEntry, int position) {
        Log.d(FileEntryAdapter.class.getName(), "bindFileName for file entry " + fileEntry);
        fileEntryViewHolder.setFileNameText(fileEntry.getName());
        if (fileEntry.isDirectory()) {
            fileEntryViewHolder.setFileSymbolImage(fileEntry.getName(), R.drawable.icon_folder);
            fileEntryViewHolder.setFileNameTextBold();
            if (fileEntry.canVisit()) {
                fileEntryViewHolder.setFileOpenImageVisible();
            } else {
                fileEntryViewHolder.setFileOpenImageInvisible();
            }
        } else {
            fileEntryViewHolder.setFileSymbolImage(fileEntry.getName(), R.drawable.icon_file);
            fileEntryViewHolder.setFileNameTextNormal();
            fileEntryViewHolder.setFileOpenImageInvisible();
        }
        if (selected == position) {
            Log.d(FileEntryAdapter.class.getName(), "file entry is selected");
            fileEntryViewHolder.setFileEntrySelected();
        } else {
            Log.d(FileEntryAdapter.class.getName(), "file entry is not selected");
            fileEntryViewHolder.setFileEntryUnselected();
        }
    }

    public FileEntry getItem(int position) {
        Log.d(FileEntryAdapter.class.getName(), "getItem for position " + position);
        if (position < 0 || position >= getItemCount()) {
            Log.e(LogEntryAdapter.class.getName(), "position " + position + " is invalid");
            return null;
        }
        return getFileEntries().get(position);
    }

    public FileEntry getSelectedItem() {
        Log.d(FileEntryAdapter.class.getName(), "getSelectedItem");
        if (selected < 0) {
            Log.d(FileEntryAdapter.class.getName(), "no item is selected, returning null");
            return null;
        }
        return getItem(selected);
    }

    public boolean isItemSelected() {
        boolean isItemSelected = selected >= 0;
        Log.d(FileEntryAdapter.class.getName(), "isItemSelected, returning " + isItemSelected);
        return isItemSelected;
    }

    public boolean isFileItemSelected() {
        Log.d(FileEntryAdapter.class.getName(), "isFileItemSelected");
        FileEntry entry = getSelectedItem();
        if (entry == null) {
            Log.d(FileEntryAdapter.class.getName(), "no item selected, returning false");
            return false;
        }
        Log.d(FileEntryAdapter.class.getName(), "currently selected item is " + entry);
        return !entry.isDirectory();
    }

    public boolean isFolderItemSelected() {
        Log.d(FileEntryAdapter.class.getName(), "isFolderItemSelected");
        FileEntry entry = getSelectedItem();
        if (entry == null) {
            Log.d(FileEntryAdapter.class.getName(), "no item selected, returning false");
            return false;
        }
        Log.d(FileEntryAdapter.class.getName(), "currently selected item is " + entry);
        return entry.isDirectory();
    }

    public boolean isParentItemSelected() {
        Log.d(FileEntryAdapter.class.getName(), "isParentItemSelected");
        FileEntry entry = getSelectedItem();
        if (entry == null) {
            Log.d(FileEntryAdapter.class.getName(), "no item selected, returning false");
            return false;
        }
        Log.d(FileEntryAdapter.class.getName(), "currently selected item is " + entry);
        return entry.isParent();
    }

    public void selectFolderByName(String folder) {
        Log.d(FileEntryAdapter.class.getName(), "selectFolderByName for folder " + folder);
        selectItemByName(folder, true);
    }

    public void selectFileByName(String file) {
        Log.d(FileEntryAdapter.class.getName(), "selectFileByName for file " + file);
        selectItemByName(file, false);
    }

    private void selectItemByName(String name, boolean folder) {
        Log.d(FileEntryAdapter.class.getName(), "selectItemByName for name " + name + ",folder is " + folder);
        List<FileEntry> entries = getFileEntries();
        for (int ii = 0; ii < entries.size(); ii++) {
            FileEntry entry = entries.get(ii);
            if (folder == entry.isDirectory()) {
                if (name.equals(entry.getName()) || name.endsWith("/" + entry.getName())) {
                    Log.d(FileEntryAdapter.class.getName(), "Found item to select at position " + ii);
                    selectItem(ii);
                    return;
                }
            }
        }
        Log.d(FileEntryAdapter.class.getName(), "No item found matching the specified name.");
    }

    public void selectItem(int position) {
        Log.d(FileEntryAdapter.class.getName(), "selectItem for position " + position);
        if (position < 0 || position >= getItemCount()) {
            Log.e(LogEntryAdapter.class.getName(), "position " + position + " is invalid");
            return;
        }
        unselectItem();
        selected = position;
        FileEntryViewHolder selectedViewHolder = getViewHolder(position);
        if (selectedViewHolder != null) {
            Log.d(FileEntryAdapter.class.getName(), "select item for position " + position);
            selectedViewHolder.setFileEntrySelected();
        } else {
            Log.d(FileEntryAdapter.class.getName(), "item is null");
        }
    }

    public void unselectItem() {
        Log.d(FileEntryAdapter.class.getName(), "unselectItem");
        if (selected >= 0) {
            FileEntryViewHolder viewHolder = getViewHolder(selected);
            if (viewHolder != null) {
                Log.d(FileEntryAdapter.class.getName(), "unselect item for position " + selected);
                viewHolder.setFileEntryUnselected();
            } else {
                Log.d(LogEntryAdapter.class.getName(), "selected item view holder is null");
            }
            selected = -1;
        } else {
            Log.d(FileEntryAdapter.class.getName(), "No item selected. Nothing to unselect.");
        }
    }

    @Override
    public int getItemCount() {
        return getFileEntries().size();
    }

    public FileEntryViewHolder getViewHolder(int position) {
        Log.d(FileEntryAdapter.class.getName(), "getViewHolder for position " + position);
        return (FileEntryViewHolder) fileEntriesRecyclerView.findViewHolderForAdapterPosition(position);
    }

    public void replaceItems(List<FileEntry> fileEntries) {
        selected = -1;
        this.fileEntries.clear();
        this.fileEntries.addAll(fileEntries);
        fileEntriesFoldersOnly.clear();
        for (FileEntry currentEntry : fileEntries) {
            if (currentEntry.isDirectory()) {
                this.fileEntriesFoldersOnly.add(currentEntry);
            }
        }
    }

    private List<FileEntry> getFileEntries() {
        if (folderChooseDialog.isShowFiles()) {
            return fileEntries;
        }
        return fileEntriesFoldersOnly;
    }

    public Bundle saveStateToBundle() {
        Log.d(FileEntryAdapter.class.getName(), "saveStateToBundle");
        Bundle bundle = BundleUtil.fileEntryListToBundle(getFileEntryKey(), fileEntries);
        bundle.putInt(getSelectedKey(), selected);
        return bundle;
    }

    public void restoreStateFromBundle(Bundle bundle) {
        Log.d(FileEntryAdapter.class.getName(), "restoreStateFromBundle");
        replaceItems(BundleUtil.fileEntryListFromBundle(getFileEntryKey(), bundle));
        if (bundle.containsKey(getSelectedKey())) {
            selected = bundle.getInt(getSelectedKey());
        }
    }

    private String getFileEntryKey() {
        return FileEntryAdapter.class.getSimpleName() + "FileEntry";
    }

    private String getSelectedKey() {
        return FileEntryAdapter.class.getSimpleName() + "Selected";
    }

    private Context getContext() {
        return folderChooseDialog.getActivity();
    }

    private Resources getResources() {
        return getContext().getResources();
    }
}
