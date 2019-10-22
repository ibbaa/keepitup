package de.ibba.keepitup.ui.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import de.ibba.keepitup.R;
import de.ibba.keepitup.model.FileEntry;
import de.ibba.keepitup.ui.dialog.FolderChooseDialog;

public class FileEntryAdapter extends RecyclerView.Adapter<FileEntryViewHolder> {

    private final List<FileEntry> fileEntries;
    private final List<FileEntry> fileEntriesFoldersOnly;
    private final FolderChooseDialog folderChooseDialog;
    private final RecyclerView fileEntriesRecyclerView;
    private int selected;

    public FileEntryAdapter(List<FileEntry> fileEntries, FolderChooseDialog folderChooseDialog) {
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
        if (position < getfileEntries().size()) {
            FileEntry fileEntry = getfileEntries().get(position);
            bindFileName(fileEntryViewHolder, fileEntry);
        }
    }

    private void bindFileName(@NonNull FileEntryViewHolder fileEntryViewHolder, FileEntry fileEntry) {
        Log.d(FileEntryAdapter.class.getName(), "bindFileName fo file entry " + fileEntry);
        fileEntryViewHolder.setFileNameText(fileEntry.getName());
        if (fileEntry.isDirectory()) {
            fileEntryViewHolder.setFileNameImage(fileEntry.getName(), R.drawable.icon_folder);
            fileEntryViewHolder.setFileNameTextBold();
        } else {
            fileEntryViewHolder.setFileNameImage(fileEntry.getName(), R.drawable.icon_file);
            fileEntryViewHolder.setFileNameTextNormal();
        }
    }

    public FileEntry getItem(int position) {
        Log.d(FileEntryAdapter.class.getName(), "getItem for position " + position);
        if (position < 0 || position >= getItemCount()) {
            Log.e(LogEntryAdapter.class.getName(), "position " + position + " is invalid");
            return null;
        }
        return getfileEntries().get(position);
    }

    public FileEntry getSelectedItem() {
        Log.d(FileEntryAdapter.class.getName(), "getSelectedItem");
        if (selected < 0) {
            Log.d(FileEntryAdapter.class.getName(), "no item is selected, returing null");
            return null;
        }
        return getItem(selected);
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
            Log.d(LogEntryAdapter.class.getName(), "item is null");
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
        return getfileEntries().size();
    }

    public FileEntryViewHolder getViewHolder(int position) {
        Log.d(FileEntryAdapter.class.getName(), "getViewHolder for position " + position);
        return (FileEntryViewHolder) fileEntriesRecyclerView.findViewHolderForAdapterPosition(position);
    }

    public void replaceItems(List<FileEntry> fileEntries) {
        this.fileEntries.clear();
        this.fileEntries.addAll(fileEntries);
        for (FileEntry currentEntry : fileEntries) {
            if (currentEntry.isDirectory()) {
                this.fileEntriesFoldersOnly.add(currentEntry);
            }
        }
    }

    private List<FileEntry> getfileEntries() {
        if (folderChooseDialog.isShowFiles()) {
            return fileEntries;
        }
        return fileEntriesFoldersOnly;
    }

    private Context getContext() {
        return folderChooseDialog.getActivity();
    }

    private Resources getResources() {
        return getContext().getResources();
    }
}
