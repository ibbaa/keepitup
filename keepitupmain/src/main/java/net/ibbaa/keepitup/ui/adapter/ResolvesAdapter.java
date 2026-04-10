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
import net.ibbaa.keepitup.model.Resolve;
import net.ibbaa.keepitup.ui.dialog.ResolvesDialog;
import net.ibbaa.keepitup.util.BundleUtil;
import net.ibbaa.keepitup.util.StringUtil;
import net.ibbaa.keepitup.util.URLUtil;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ResolvesAdapter extends RecyclerView.Adapter<ResolveViewHolder> {

    private final List<Resolve> resolves;
    private final ResolvesDialog resolvesDialog;

    public ResolvesAdapter(List<Resolve> resolves, ResolvesDialog resolvesDialog) {
        this.resolves = new ArrayList<>();
        this.resolvesDialog = resolvesDialog;
        replaceItems(resolves);
    }

    @NonNull
    @Override
    public ResolveViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Log.d(ResolvesAdapter.class.getName(), "onCreateViewHolder");
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_resolve, viewGroup, false);
        return new ResolveViewHolder(itemView, resolvesDialog);
    }

    @Override
    public void onBindViewHolder(@NonNull ResolveViewHolder resolveViewHolder, int position) {
        Log.d(ResolvesAdapter.class.getName(), "onBindViewHolder");
        if (!resolves.isEmpty()) {
            if (position < resolves.size()) {
                Resolve resolve = resolves.get(position);
                bindResolveText(resolveViewHolder, resolve);
                resolveViewHolder.showResolvesCardView();
                resolveViewHolder.hideNoResolvesTextView();
            } else {
                resolveViewHolder.hideResolvesCardView();
                resolveViewHolder.hideNoResolvesTextView();
            }
        } else {
            resolveViewHolder.hideResolvesCardView();
            resolveViewHolder.showNoResolvesTextView();
        }
    }

    private void bindResolveText(@NonNull ResolveViewHolder resolveViewHolder, Resolve resolve) {
        Log.d(ResolvesAdapter.class.getName(), "bindResolveText");
        resolveViewHolder.setResolveMatchText(getMatchText(resolve));
        resolveViewHolder.setResolveConnectToText(getConnectToText(resolve));
    }

    private String getMatchText(Resolve resolve) {
        String match;
        if (isMatchEmpty(resolve)) {
            match =  getResources().getString(R.string.string_not_set);
        } else {
            match = getHostAndPort(resolve.getSourceAddress(), resolve.getSourcePort());
        }
        return getResources().getString(R.string.list_item_resolve_match, match);
    }

    private String getConnectToText(Resolve resolve) {
        String connectTo;
        if (isConnectToEmpty(resolve)) {
            connectTo = getResources().getString(R.string.string_not_set);
        } else {
            connectTo = getHostAndPort(resolve.getTargetAddress(), resolve.getTargetPort());
        }
        return getResources().getString(R.string.list_item_resolve_connect_to, connectTo);
    }

    private boolean isMatchEmpty(Resolve resolve) {
        return StringUtil.isEmpty(resolve.getSourceAddress()) && resolve.getSourcePort() < 0;
    }

    private boolean isConnectToEmpty(Resolve resolve) {
        return StringUtil.isEmpty(resolve.getTargetAddress()) && resolve.getTargetPort() < 0;
    }

    private String getHostAndPort(String address, int port) {
        URL url = URLUtil.getURL(resolvesDialog.getNetworkTaskURL());
        if (url == null) {
            return getResources().getString(R.string.string_not_set);
        }
        String resolvedAddress = StringUtil.isEmpty(address) ? url.getHost() : address;
        resolvedAddress = URLUtil.isValidIP6Address(resolvedAddress) ? "[" + resolvedAddress + "]" : resolvedAddress;
        int resolvedPort = port < 0 ? URLUtil.getPort(url) : port;
        return resolvedAddress + ":" + resolvedPort;
    }

    public Bundle saveStateToBundle() {
        Log.d(ResolvesAdapter.class.getName(), "saveStateToBundle");
        return BundleUtil.resolveListToBundle(getResolvesKey(), resolves);
    }

    public void restoreStateFromBundle(Bundle bundle) {
        Log.d(ResolvesAdapter.class.getName(), "restoreStateFromBundle");
        replaceItems(BundleUtil.resolveListFromBundle(getResolvesKey(), bundle));
    }

    private String getResolvesKey() {
        return ResolvesAdapter.class.getSimpleName() + ".Resolves";
    }

    public void addItem(Resolve resolve) {
        Log.d(ResolvesAdapter.class.getName(), "addItem " + resolve);
        resolve.setIndex(resolves.size());
        resolves.add(resolve);
    }

    public Resolve getItem(int index) {
        Log.d(ResolvesAdapter.class.getName(), "getItem for index " + index);
        if (index < 0 || index >= resolves.size()) {
            Log.e(ResolvesAdapter.class.getName(), "invalid index " + index);
            return null;
        }
        return resolves.get(index);
    }

    public void replaceItem(int index, Resolve resolve) {
        Log.d(ResolvesAdapter.class.getName(), "replaceItem for index " + index + " with " + resolve);
        if (index < 0 || index >= resolves.size()) {
            Log.e(ResolvesAdapter.class.getName(), "invalid index " + index);
            return;
        }
        resolve.setIndex(index);
        resolves.set(index, resolve);
    }

    public void removeItem(int index) {
        Log.d(ResolvesAdapter.class.getName(), "removeItem for index " + index);
        if (index < 0 || index >= resolves.size()) {
            Log.e(ResolvesAdapter.class.getName(), "invalid index " + index);
            return;
        }
        resolves.remove(index);
        updateIndex();
    }

    public void updateIndex() {
        Log.d(ResolvesAdapter.class.getName(), "updateIndex");
        for (int ii = 0; ii < resolves.size(); ii++) {
            resolves.get(ii).setIndex(ii);
        }
    }

    @SuppressWarnings("unused")
    public void removeItems() {
        resolves.clear();
    }

    public void replaceItems(List<Resolve> resolves) {
        this.resolves.clear();
        this.resolves.addAll(resolves);
    }

    @Override
    public int getItemCount() {
        return resolves.isEmpty() ? 1 : resolves.size();
    }

    public List<Resolve> getAllItems() {
        return Collections.unmodifiableList(resolves);
    }

    private Resources getResources() {
        return getContext().getResources();
    }

    public Context getContext() {
        return resolvesDialog.getContext();
    }
}
