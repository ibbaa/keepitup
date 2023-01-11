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
import net.ibbaa.keepitup.ui.dialog.ContextOption;
import net.ibbaa.keepitup.ui.dialog.ContextOptionsDialog;
import net.ibbaa.keepitup.ui.mapping.EnumMapping;
import net.ibbaa.keepitup.util.BundleUtil;

import java.util.ArrayList;
import java.util.List;

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
