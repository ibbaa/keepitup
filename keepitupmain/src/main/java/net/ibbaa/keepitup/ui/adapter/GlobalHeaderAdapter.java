/*
 * Copyright (c) 2025 Alwin Ibba
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
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.model.Header;
import net.ibbaa.keepitup.ui.dialog.GlobalHeadersDialog;
import net.ibbaa.keepitup.util.BundleUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GlobalHeaderAdapter extends RecyclerView.Adapter<GlobalHeaderViewHolder> {

    private final List<Header> headers;
    private final GlobalHeadersDialog headersDialog;

    public GlobalHeaderAdapter(List<Header> headers, GlobalHeadersDialog headersDialog) {
        this.headers = new ArrayList<>();
        this.headersDialog = headersDialog;
        replaceItems(headers);
    }

    @NonNull
    @Override
    public GlobalHeaderViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Log.d(GlobalHeaderAdapter.class.getName(), "onCreateViewHolder");
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_global_header, viewGroup, false);
        return new GlobalHeaderViewHolder(itemView, headersDialog);
    }

    @Override
    public void onBindViewHolder(@NonNull GlobalHeaderViewHolder globalHeaderViewHolder, int position) {
        Log.d(GlobalHeaderAdapter.class.getName(), "onBindViewHolder");
        if (!headers.isEmpty()) {
            if (position < headers.size()) {
                Header header = headers.get(position);
                bindHeaderText(globalHeaderViewHolder, header);
                globalHeaderViewHolder.showHeadersCardView();
                globalHeaderViewHolder.hideNoHeadersTextView();
            } else {
                globalHeaderViewHolder.hideHeadersCardView();
                globalHeaderViewHolder.hideNoHeadersTextView();
            }
            globalHeaderViewHolder.hideNoHeadersTextView();
        } else {
            globalHeaderViewHolder.hideHeadersCardView();
            globalHeaderViewHolder.showNoHeadersTextView();
        }
    }

    private void bindHeaderText(@NonNull GlobalHeaderViewHolder globalHeaderViewHolder, Header header) {
        Log.d(LogEntryAdapter.class.getName(), "bindHeaderText");
        globalHeaderViewHolder.setHeaderText("");
    }

    public Bundle saveStateToBundle() {
        Log.d(GlobalHeaderAdapter.class.getName(), "saveStateToBundle");
        return BundleUtil.headerListToBundle(getGlobalHeadersKey(), headers);
    }

    public void restoreStateFromBundle(Bundle bundle) {
        Log.d(GlobalHeaderAdapter.class.getName(), "restoreStateFromBundle");
        replaceItems(BundleUtil.headerListFromBundle(getGlobalHeadersKey(), bundle));
    }

    private String getGlobalHeadersKey() {
        return GlobalHeaderAdapter.class.getSimpleName() + "getGlobalHeaders";
    }

    public void addItem(Header header) {
        Log.d(GlobalHeaderAdapter.class.getName(), "addItem " + header);
        headers.add(header);
    }

    public Header getItem(int index) {
        Log.d(GlobalHeaderAdapter.class.getName(), "getItem for index " + index);
        if (index < 0 || index >= headers.size()) {
            Log.e(GlobalHeaderAdapter.class.getName(), "invalid index " + index);
            return null;
        }
        return headers.get(index);
    }

    public void removeItem(int index) {
        Log.d(GlobalHeaderAdapter.class.getName(), "removeItem for index " + index);
        if (index < 0 || index >= headers.size()) {
            Log.e(GlobalHeaderAdapter.class.getName(), "invalid index " + index);
            return;
        }
        headers.remove(index);
    }

    public void removeItems() {
        this.headers.clear();
    }

    public void replaceItems(List<Header> headers) {
        this.headers.clear();
        this.headers.addAll(headers);
    }

    @Override
    public int getItemCount() {
        return headers.isEmpty() ? 1 : headers.size();
    }

    public List<Header> getAllItems() {
        return Collections.unmodifiableList(headers);
    }

    private Context getContext() {
        return headersDialog.getActivity();
    }
}
