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
import de.ibba.keepitup.ui.dialog.FolderChooseEditDialog;

public class FileEntryAdapter extends RecyclerView.Adapter<FileEntryViewHolder> {

    private final List<FileEntry> fileEntries;
    private final FolderChooseEditDialog folderChooseEditDialog;
    private final RecyclerView fileEntriesRecyclerView;
    private int selected;

    public FileEntryAdapter(List<FileEntry> fileEntries, FolderChooseEditDialog folderChooseEditDialog) {
        this.fileEntries = new ArrayList<>();
        this.folderChooseEditDialog = folderChooseEditDialog;
        this.fileEntriesRecyclerView = folderChooseEditDialog.getFileEntriesRecyclerView();
        this.selected = -1;
        replaceItems(fileEntries);
    }

    @NonNull
    @Override
    public FileEntryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Log.d(FileEntryAdapter.class.getName(), "onCreateViewHolder");
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_file_entry, viewGroup, false);
        return new FileEntryViewHolder(itemView, folderChooseEditDialog);
    }

    @Override
    public void onBindViewHolder(@NonNull FileEntryViewHolder fileEntryViewHolder, int position) {
        Log.d(FileEntryAdapter.class.getName(), "onBindViewHolder");
        if (position < fileEntries.size()) {
            FileEntry fileEntry = fileEntries.get(position);
            bindFileName(fileEntryViewHolder, fileEntry);
        }
    }

    private void bindFileName(@NonNull FileEntryViewHolder fileEntryViewHolder, FileEntry fileEntry) {
        Log.d(FileEntryAdapter.class.getName(), "bindFileName fo file entry " + fileEntry);
        fileEntryViewHolder.setFileNameText(fileEntry.getName());
        if (fileEntry.isDirectory()) {
            fileEntryViewHolder.setFileNameTextBold();
        } else {
            fileEntryViewHolder.setFileNameTextNormal();
        }
    }

    public FileEntry getItem(int position) {
        Log.d(FileEntryAdapter.class.getName(), "getItem for position " + position);
        if (position < 0 || position >= getItemCount()) {
            Log.e(LogEntryAdapter.class.getName(), "position " + position + " is invalid");
            return null;
        }
        return fileEntries.get(position);
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
        if (selected > 0) {
            unselectItem(selected);
        }
        selected = position;
        FileEntryViewHolder selectedViewHolder = getViewHolder(position);
        if (selectedViewHolder != null) {
            Log.d(FileEntryAdapter.class.getName(), "select item");
            selectedViewHolder.setFileEntrySelected();
        } else {
            Log.d(LogEntryAdapter.class.getName(), "item is null");
        }
    }

    public void unselectItem(int position) {
        Log.d(FileEntryAdapter.class.getName(), "unselectItem for position " + position);
        if (position < 0 || position >= getItemCount()) {
            Log.e(LogEntryAdapter.class.getName(), "position " + position + " is invalid");
            return;
        }
        FileEntryViewHolder viewHolder = getViewHolder(position);
        if (viewHolder != null) {
            Log.d(FileEntryAdapter.class.getName(), "unselect item");
            viewHolder.setFileEntryUnselected();
        } else {
            Log.d(LogEntryAdapter.class.getName(), "item is null");
        }
    }

    @Override
    public int getItemCount() {
        return fileEntries.size();
    }

    public FileEntryViewHolder getViewHolder(int position) {
        Log.d(FileEntryAdapter.class.getName(), "getViewHolder for position " + position);
        return (FileEntryViewHolder) fileEntriesRecyclerView.findViewHolderForAdapterPosition(position);
    }

    public void replaceItems(List<FileEntry> fileEntries) {
        this.fileEntries.clear();
        this.fileEntries.addAll(fileEntries);
    }

    private Context getContext() {
        return folderChooseEditDialog.getActivity();
    }

    private Resources getResources() {
        return getContext().getResources();
    }
}
