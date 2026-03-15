/*
 * Copyright (c) 2026 Alwin Ibba
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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.model.Header;
import net.ibbaa.keepitup.ui.dialog.HeadersDialog;
import net.ibbaa.keepitup.util.BundleUtil;
import net.ibbaa.keepitup.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HeadersAdapter extends RecyclerView.Adapter<HeaderViewHolder> {

    private final List<Header> headers;
    private final HeadersDialog headersDialog;

    public HeadersAdapter(List<Header> headers, HeadersDialog headersDialog) {
        this.headers = new ArrayList<>();
        this.headersDialog = headersDialog;
        replaceItems(headers);
    }

    @NonNull
    @Override
    public HeaderViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Log.d(HeadersAdapter.class.getName(), "onCreateViewHolder");
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_header, viewGroup, false);
        return new HeaderViewHolder(itemView, headersDialog);
    }

    @Override
    public void onBindViewHolder(@NonNull HeaderViewHolder headerViewHolder, int position) {
        Log.d(HeadersAdapter.class.getName(), "onBindViewHolder");
        if (!headers.isEmpty()) {
            if (position < headers.size()) {
                Header header = headers.get(position);
                bindHeaderText(headerViewHolder, header);
                headerViewHolder.showHeadersCardView();
                headerViewHolder.hideNoHeadersTextView();
            } else {
                headerViewHolder.hideHeadersCardView();
                headerViewHolder.hideNoHeadersTextView();
            }
            headerViewHolder.hideNoHeadersTextView();
        } else {
            headerViewHolder.hideHeadersCardView();
            headerViewHolder.showNoHeadersTextView();
        }
    }

    private void bindHeaderText(@NonNull HeaderViewHolder headerViewHolder, Header header) {
        Log.d(HeadersAdapter.class.getName(), "bindHeaderText");
        headerViewHolder.setHeaderNameText(header.getName());
        headerViewHolder.setHeaderValueText(StringUtil.maskSecret(header.getValue(), header.isValueSecret()));
    }

    public Bundle saveStateToBundle() {
        Log.d(HeadersAdapter.class.getName(), "saveStateToBundle");
        return BundleUtil.headerListToBundle(getHeadersKey(), headers);
    }

    public void restoreStateFromBundle(Bundle bundle) {
        Log.d(HeadersAdapter.class.getName(), "restoreStateFromBundle");
        replaceItems(BundleUtil.headerListFromBundle(getHeadersKey(), bundle));
    }

    private String getHeadersKey() {
        return HeadersAdapter.class.getSimpleName() + "Headers";
    }

    public void addItem(Header header) {
        Log.d(HeadersAdapter.class.getName(), "addItem " + header);
        headers.add(header);
    }

    public Header getItem(int index) {
        Log.d(HeadersAdapter.class.getName(), "getItem for index " + index);
        if (index < 0 || index >= headers.size()) {
            Log.e(HeadersAdapter.class.getName(), "invalid index " + index);
            return null;
        }
        return headers.get(index);
    }

    public void removeItem(int index) {
        Log.d(HeadersAdapter.class.getName(), "removeItem for index " + index);
        if (index < 0 || index >= headers.size()) {
            Log.e(HeadersAdapter.class.getName(), "invalid index " + index);
            return;
        }
        headers.remove(index);
    }

    @SuppressWarnings("unused")
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
}
