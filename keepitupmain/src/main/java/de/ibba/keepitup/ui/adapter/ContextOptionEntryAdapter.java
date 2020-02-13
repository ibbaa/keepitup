package de.ibba.keepitup.ui.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import de.ibba.keepitup.R;
import de.ibba.keepitup.logging.Log;
import de.ibba.keepitup.ui.dialog.ContextOption;
import de.ibba.keepitup.ui.dialog.ContextOptionsDialog;
import de.ibba.keepitup.ui.mapping.EnumMapping;

public class ContextOptionEntryAdapter extends RecyclerView.Adapter<ContextOptionEntryViewHolder> {

    private final List<ContextOption> contextOptionEntries;
    private final ContextOptionsDialog contextOptionsDialog;
    private final RecyclerView contextOptionEntriesRecyclerView;
    private int selected;

    public ContextOptionEntryAdapter(List<ContextOption> contextOptionEntries, ContextOptionsDialog contextOptionsDialog) {
        this.contextOptionEntries = new ArrayList<>();
        this.contextOptionsDialog = contextOptionsDialog;
        this.contextOptionEntriesRecyclerView = contextOptionsDialog.getContextOptionEntriesRecyclerView();
        this.selected = -1;
        replaceItems(contextOptionEntries);
    }

    @NonNull
    @Override
    public ContextOptionEntryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Log.d(ContextOptionEntryAdapter.class.getName(), "onCreateViewHolder");
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_context_option_entry, viewGroup, false);
        return new ContextOptionEntryViewHolder(itemView, contextOptionsDialog);
    }

    @Override
    public void onBindViewHolder(@NonNull ContextOptionEntryViewHolder contextOptionEntryViewHolder, int position) {
        Log.d(ContextOptionEntryAdapter.class.getName(), "onBindViewHolder");
        if (position < getContextOptionEntries().size()) {
            ContextOption contextOptionEntry = getContextOptionEntries().get(position);
            EnumMapping mapping = new EnumMapping(getContext());
            bindContextOptionName(contextOptionEntryViewHolder, mapping.getContextOptionName(contextOptionEntry), position);
        }
    }

    private void bindContextOptionName(@NonNull ContextOptionEntryViewHolder contextOptionEntryViewHolder, String name, int position) {
        Log.d(ContextOptionEntryAdapter.class.getName(), "bindContextOptionName for name " + name);
        contextOptionEntryViewHolder.setContextOptionText(name);
    }

    public ContextOption getItem(int position) {
        Log.d(ContextOptionEntryAdapter.class.getName(), "getItem for position " + position);
        if (position < 0 || position >= getItemCount()) {
            Log.e(ContextOptionEntryAdapter.class.getName(), "position " + position + " is invalid");
            return null;
        }
        return getContextOptionEntries().get(position);
    }

    public void selectItem(int position) {
        Log.d(ContextOptionEntryAdapter.class.getName(), "selectItem for position " + position);
        if (position < 0 || position >= getItemCount()) {
            Log.e(LogEntryAdapter.class.getName(), "position " + position + " is invalid");
            return;
        }
        unselectItem();
        selected = position;
        ContextOptionEntryViewHolder selectedViewHolder = getViewHolder(position);
        if (selectedViewHolder != null) {
            Log.d(ContextOptionEntryAdapter.class.getName(), "select item for position " + position);
            selectedViewHolder.setContextOptionEntrySelected();
        } else {
            Log.d(ContextOptionEntryAdapter.class.getName(), "item is null");
        }
    }

    public void unselectItem() {
        Log.d(ContextOptionEntryAdapter.class.getName(), "unselectItem");
        if (selected >= 0) {
            ContextOptionEntryViewHolder selectedViewHolder = getViewHolder(selected);
            if (selectedViewHolder != null) {
                Log.d(FileEntryAdapter.class.getName(), "unselect item for position " + selected);
                selectedViewHolder.setContextOptionEntryUnselected();
            } else {
                Log.d(LogEntryAdapter.class.getName(), "selected item view holder is null");
            }
            selected = -1;
        } else {
            Log.d(ContextOptionEntryAdapter.class.getName(), "No item selected. Nothing to unselect.");
        }
    }

    @Override
    public int getItemCount() {
        return getContextOptionEntries().size();
    }

    public void replaceItems(List<ContextOption> contextOptionEntries) {
        this.contextOptionEntries.clear();
        this.contextOptionEntries.addAll(contextOptionEntries);
    }

    public ContextOptionEntryViewHolder getViewHolder(int position) {
        Log.d(ContextOptionEntryAdapter.class.getName(), "getViewHolder for position " + position);
        return (ContextOptionEntryViewHolder) contextOptionEntriesRecyclerView.findViewHolderForAdapterPosition(position);
    }

    private List<ContextOption> getContextOptionEntries() {
        return contextOptionEntries;
    }

    private Context getContext() {
        return contextOptionsDialog.getActivity();
    }

    private Resources getResources() {
        return getContext().getResources();
    }
}
