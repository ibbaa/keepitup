package de.ibba.keepitup.ui.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
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
import de.ibba.keepitup.util.BundleUtil;

public class ContextOptionAdapter extends RecyclerView.Adapter<ContextOptionViewHolder> {

    private final List<ContextOption> contextOptions;
    private final ContextOptionsDialog contextOptionsDialog;
    private final RecyclerView contextOptionRecyclerView;
    private int selected;

    public ContextOptionAdapter(List<ContextOption> contextOptions, ContextOptionsDialog contextOptionsDialog) {
        this.contextOptions = new ArrayList<>();
        this.contextOptionsDialog = contextOptionsDialog;
        this.contextOptionRecyclerView = contextOptionsDialog.getContextOptionRecyclerView();
        this.selected = -1;
        replaceItems(contextOptions);
    }

    @NonNull
    @Override
    public ContextOptionViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Log.d(ContextOptionAdapter.class.getName(), "onCreateViewHolder");
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_context_option_entry, viewGroup, false);
        return new ContextOptionViewHolder(itemView, contextOptionsDialog);
    }

    @Override
    public void onBindViewHolder(@NonNull ContextOptionViewHolder contextOptionEntryViewHolder, int position) {
        Log.d(ContextOptionAdapter.class.getName(), "onBindViewHolder");
        if (position < getContextOptions().size()) {
            ContextOption contextOptionEntry = getContextOptions().get(position);
            EnumMapping mapping = new EnumMapping(getContext());
            bindContextOptionName(contextOptionEntryViewHolder, mapping.getContextOptionName(contextOptionEntry), position);
        }
    }

    private void bindContextOptionName(@NonNull ContextOptionViewHolder contextOptionEntryViewHolder, String name, int position) {
        Log.d(ContextOptionAdapter.class.getName(), "bindContextOptionName for name " + name);
        contextOptionEntryViewHolder.setContextOptionText(name);
    }

    public ContextOption getItem(int position) {
        Log.d(ContextOptionAdapter.class.getName(), "getItem for position " + position);
        if (position < 0 || position >= getItemCount()) {
            Log.e(ContextOptionAdapter.class.getName(), "position " + position + " is invalid");
            return null;
        }
        return getContextOptions().get(position);
    }

    public void selectItem(int position) {
        Log.d(ContextOptionAdapter.class.getName(), "selectItem for position " + position);
        if (position < 0 || position >= getItemCount()) {
            Log.e(LogEntryAdapter.class.getName(), "position " + position + " is invalid");
            return;
        }
        unselectItem();
        selected = position;
        ContextOptionViewHolder selectedViewHolder = getViewHolder(position);
        if (selectedViewHolder != null) {
            Log.d(ContextOptionAdapter.class.getName(), "select item for position " + position);
            selectedViewHolder.setContextOptionEntrySelected();
        } else {
            Log.d(ContextOptionAdapter.class.getName(), "item is null");
        }
    }

    public void unselectItem() {
        Log.d(ContextOptionAdapter.class.getName(), "unselectItem");
        if (selected >= 0) {
            ContextOptionViewHolder selectedViewHolder = getViewHolder(selected);
            if (selectedViewHolder != null) {
                Log.d(FileEntryAdapter.class.getName(), "unselect item for position " + selected);
                selectedViewHolder.setContextOptionEntryUnselected();
            } else {
                Log.d(LogEntryAdapter.class.getName(), "selected item view holder is null");
            }
            selected = -1;
        } else {
            Log.d(ContextOptionAdapter.class.getName(), "No item selected. Nothing to unselect.");
        }
    }

    @Override
    public int getItemCount() {
        return getContextOptions().size();
    }

    public void replaceItems(List<ContextOption> contextOptionEntries) {
        this.selected = -1;
        this.contextOptions.clear();
        this.contextOptions.addAll(contextOptionEntries);
    }

    public ContextOptionViewHolder getViewHolder(int position) {
        Log.d(ContextOptionAdapter.class.getName(), "getViewHolder for position " + position);
        return (ContextOptionViewHolder) contextOptionRecyclerView.findViewHolderForAdapterPosition(position);
    }

    private List<ContextOption> getContextOptions() {
        return contextOptions;
    }

    public Bundle saveStateToBundle() {
        Log.d(ContextOptionAdapter.class.getName(), "saveStateToBundle");
        Bundle bundle = BundleUtil.contextOptionListToBundle(getContextOptionKey(), contextOptions);
        bundle.putInt(getSelectedKey(), selected);
        return bundle;
    }

    public void restoreStateFromBundle(Bundle bundle) {
        Log.d(ContextOptionAdapter.class.getName(), "restoreStateFromBundle");
        replaceItems(BundleUtil.contextOptionListFromBundle(getContextOptionKey(), bundle));
        if (bundle.containsKey(getSelectedKey())) {
            selected = bundle.getInt(getSelectedKey());
        }
    }

    private String getContextOptionKey() {
        return ContextOptionAdapter.class.getSimpleName() + "ContextOption";
    }

    private String getSelectedKey() {
        return ContextOptionAdapter.class.getSimpleName() + "Selected";
    }

    private Context getContext() {
        return contextOptionsDialog.getActivity();
    }

    private Resources getResources() {
        return getContext().getResources();
    }
}
