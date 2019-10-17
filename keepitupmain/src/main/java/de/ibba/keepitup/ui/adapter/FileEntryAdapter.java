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

public class FileEntryAdapter extends RecyclerView.Adapter<FileEntryViewHolder> {

    private final List<FileEntry> fileEntries;
    private final Context context;

    public FileEntryAdapter(List<FileEntry> fileEntries, Context context) {
        this.fileEntries = new ArrayList<>();
        this.context = context;
        replaceItems(fileEntries);
    }

    @NonNull
    @Override
    public FileEntryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Log.d(FileEntryAdapter.class.getName(), "onCreateViewHolder");
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_file_entry, viewGroup, false);
        return new FileEntryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FileEntryViewHolder fileEntryViewHolder, int position) {
        Log.d(FileEntryAdapter.class.getName(), "onBindViewHolder");
        if (position < fileEntries.size()) {
            FileEntry fileEntry = fileEntries.get(position);

        }
    }

    private void bindFileName(@NonNull FileEntryViewHolder fileEntryViewHolder, FileEntry fileEntry) {
        fileEntryViewHolder.setFileNameText(fileEntry.getName());
        if (fileEntry.isDirectory()) {
            fileEntryViewHolder.setFileNameTextBold();
        } else {
            fileEntryViewHolder.setFileNameTextNormal();
        }
    }

    @Override
    public int getItemCount() {
        return fileEntries.size();
    }

    public void replaceItems(List<FileEntry> fileEntries) {
        this.fileEntries.clear();
        this.fileEntries.addAll(fileEntries);
    }

    private Context getContext() {
        return context;
    }

    private Resources getResources() {
        return getContext().getResources();
    }
}
