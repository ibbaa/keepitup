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

package net.ibbaa.keepitup.ui.dialog;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.model.Header;
import net.ibbaa.keepitup.ui.adapter.DeleteSwipeCallback;
import net.ibbaa.keepitup.ui.adapter.HeadersAdapter;
import net.ibbaa.keepitup.ui.support.ConfirmSupport;
import net.ibbaa.keepitup.ui.support.HeaderEditSupport;
import net.ibbaa.keepitup.ui.support.HeadersSupport;
import net.ibbaa.keepitup.ui.support.SwipeDeleteSupport;
import net.ibbaa.keepitup.util.BundleUtil;
import net.ibbaa.keepitup.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@SuppressWarnings({"unused"})
public class HeadersDialog extends DialogFragmentBase implements HeaderEditSupport, ConfirmSupport, SwipeDeleteSupport {

    private View dialogView;
    private RecyclerView headersRecyclerView;
    private ItemTouchHelper itemTouchHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(HeadersDialog.class.getName(), "onCreate");
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.DialogTheme);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(HeadersDialog.class.getName(), "onCreateView");
        dialogView = inflater.inflate(R.layout.dialog_headers, container);
        initEdgeToEdgeInsets(dialogView);
        boolean containsSavedState = containsSavedState(savedInstanceState);
        Log.d(HeadersDialog.class.getName(), "containsSavedState is " + containsSavedState);
        Bundle adapterState = containsSavedState ? savedInstanceState.getBundle(getHeadersAdapterKey()) : null;
        prepareHeadersRecyclerView(adapterState);
        prepareAddImageButton();
        prepareOkCancelImageButtons();
        return dialogView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Log.d(HeadersDialog.class.getName(), "onSaveInstanceState");
        super.onSaveInstanceState(outState);
        Bundle adapterBundle = getAdapter().saveStateToBundle();
        outState.putBundle(getHeadersAdapterKey(), adapterBundle);
    }

    private boolean containsSavedState(Bundle savedInstanceState) {
        Log.d(HeadersDialog.class.getName(), "containsSavedState");
        if (savedInstanceState == null) {
            Log.d(HeadersDialog.class.getName(), "savedInstanceState bundle is null");
            return false;
        }
        return savedInstanceState.containsKey(getHeadersAdapterKey());
    }

    public String getInitialHeadersKey() {
        return HeadersDialog.class.getSimpleName() + "InitialHeaders";
    }

    public String getNetworkTaskIdKey() {
        return HeadersDialog.class.getSimpleName() + "NetworkTaskId";
    }

    private String getHeadersAdapterKey() {
        return HeadersDialog.class.getSimpleName() + "HeadersAdapter";
    }

    public long getNetworkTaskId() {
        return BundleUtil.longFromBundle(getNetworkTaskIdKey(), requireArguments());
    }

    private void prepareHeadersRecyclerView(Bundle adapterState) {
        Log.d(HeadersDialog.class.getName(), "prepareHeadersRecyclerView");
        headersRecyclerView = dialogView.findViewById(R.id.listview_dialog_headers_headers);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        headersRecyclerView.setLayoutManager(layoutManager);
        headersRecyclerView.setItemAnimator(new DefaultItemAnimator());
        RecyclerView.Adapter<?> adapter = adapterState == null ? createAdapter() : restoreAdapter(adapterState);
        headersRecyclerView.setAdapter(adapter);
        itemTouchHelper = new ItemTouchHelper(new DeleteSwipeCallback(this));
        itemTouchHelper.attachToRecyclerView(headersRecyclerView);
    }

    private boolean noHeadersDefined() {
        return ((HeadersAdapter) Objects.requireNonNull(headersRecyclerView.getAdapter())).getAllItems().isEmpty();
    }

    private void prepareAddImageButton() {
        Log.d(HeadersDialog.class.getName(), "prepareAddImageButton");
        ImageView addImage = dialogView.findViewById(R.id.imageview_dialog_headers_add);
        addImage.setOnClickListener(this::onHeaderAddClicked);
    }

    private void prepareOkCancelImageButtons() {
        Log.d(HeadersDialog.class.getName(), "prepareOkCancelImageButtons");
        ImageView okImage = dialogView.findViewById(R.id.imageview_dialog_headers_ok);
        ImageView cancelImage = dialogView.findViewById(R.id.imageview_dialog_headers_cancel);
        okImage.setOnClickListener(this::onOkClicked);
        cancelImage.setOnClickListener(this::onCancelClicked);
    }

    public void onHeaderOpenClicked(View view, int index) {
        Log.d(HeadersDialog.class.getName(), "onHeaderOpenClicked for index " + index);
        Header header = getAdapter().getItem(index);
        if (header == null) {
            Log.e(HeadersDialog.class.getName(), "onHeaderOpenClicked, header is null");
            return;
        }
        showGlobalHeaderEditDialog(header, index);
    }

    public void onHeaderDeleteClicked(View view, int index) {
        Log.d(HeadersDialog.class.getName(), "onHeaderDeleteClicked for index " + index);
        if (index < 0) {
            Log.e(HeadersDialog.class.getName(), "index " + index + " is invalid");
            return;
        }
        openConfirmDialog(index, ConfirmDialog.Type.DELETEHEADER);
    }

    public void onDeleteSwiped(int index) {
        Log.d(HeadersDialog.class.getName(), "onHeaderDeleteSwiped for index " + index);
        if (index < 0) {
            Log.e(HeadersDialog.class.getName(), "index " + index + " is invalid");
            return;
        }
        openConfirmDialog(index, ConfirmDialog.Type.DELETEHEADERSWIPE);
    }

    private void openConfirmDialog(int index, ConfirmDialog.Type type) {
        ConfirmDialog confirmDialog = new ConfirmDialog();
        String confirmMessage = getResources().getString(R.string.text_dialog_confirm_delete_header);
        Bundle bundle = BundleUtil.stringsToBundle(new String[]{confirmDialog.getMessageKey(), confirmDialog.getTypeKey()}, new String[]{confirmMessage, type.name()});
        bundle.putInt(confirmDialog.getPositionKey(), index);
        confirmDialog.setArguments(bundle);
        showDialog(confirmDialog, ConfirmDialog.class.getName());
    }

    @Override
    public void onConfirmDialogOkClicked(ConfirmDialog confirmDialog, ConfirmDialog.Type type) {
        Log.d(HeadersDialog.class.getName(), "onConfirmDialogOkClicked for type " + type);
        if (ConfirmDialog.Type.DELETEHEADER.equals(type) || ConfirmDialog.Type.DELETEHEADERSWIPE.equals(type)) {
            int deletePosition = confirmDialog.getPosition();
            if (deletePosition >= 0) {
                Log.d(HeadersDialog.class.getName(), "deleting header at deletePosition " + deletePosition);
                getAdapter().removeItem(deletePosition);
                getAdapter().notifyItemRemoved(deletePosition);
                if (ConfirmDialog.Type.DELETEHEADERSWIPE.equals(type)) {
                    reattachItemTouchHelper();
                }
            } else {
                Log.e(HeadersDialog.class.getName(), ConfirmDialog.class.getSimpleName() + " arguments do not contain deletePosition key " + confirmDialog.getPositionKey());
            }
        } else {
            Log.e(HeadersDialog.class.getName(), "Unknown type " + type);
        }
        confirmDialog.dismiss();
    }

    @Override
    public void onConfirmDialogCancelClicked(ConfirmDialog confirmDialog, ConfirmDialog.Type type) {
        Log.d(HeadersDialog.class.getName(), "onConfirmDialogCancelClicked");
        if (ConfirmDialog.Type.DELETEHEADERSWIPE.equals(type)) {
            int position = confirmDialog.getPosition();
            getAdapter().notifyItemChanged(position);
            reattachItemTouchHelper();
        }
        confirmDialog.dismiss();
    }

    private void reattachItemTouchHelper() {
        Log.d(HeadersDialog.class.getName(), "reattachItemTouchHelper");
        RecyclerView recyclerView = dialogView.findViewById(R.id.listview_dialog_headers_headers);
        if (itemTouchHelper != null) {
            itemTouchHelper.attachToRecyclerView(null);
        }
        recyclerView.post(() -> {
            itemTouchHelper = new ItemTouchHelper(new DeleteSwipeCallback(this));
            itemTouchHelper.attachToRecyclerView(recyclerView);
        });
    }

    @Override
    @SuppressWarnings("NotifyDataSetChanged")
    public void onHeaderEditDialogOkClicked(HeaderEditDialog headerEditDialog, int position) {
        Log.d(HeadersDialog.class.getName(), "onGlobalHeaderEditDialogOkClicked with position " + position);
        if (position >= 0) {
            getAdapter().removeItem(position);
        }
        Header header = headerEditDialog.getHeader();
        getAdapter().addItem(header);
        List<Header> headers = sortHeaderList(getAdapter().getAllItems());
        getAdapter().replaceItems(headers);
        getAdapter().notifyDataSetChanged();
        headerEditDialog.dismiss();
    }

    @Override
    public void onHeaderEditDialogCancelClicked(HeaderEditDialog headerEditDialog) {
        Log.d(HeadersDialog.class.getName(), "onGlobalHeaderEditDialogCancelClicked");
        headerEditDialog.dismiss();
    }

    @Override
    public List<String> getExistingHeaderNames() {
        Log.d(HeadersDialog.class.getName(), "getExistingHeaderNames");
        List<String> names = new ArrayList<>();
        List<Header> headers = getAdapter().getAllItems();
        for (Header header : headers) {
            names.add(header.getName());
        }
        return names;
    }

    private void onHeaderAddClicked(View view) {
        Log.d(HeadersDialog.class.getName(), "onHeaderAddClicked");
        Header header = new Header();
        header.setNetworkTaskId(getNetworkTaskId());
        showGlobalHeaderEditDialog(header, -1);
    }

    private void onOkClicked(View view) {
        Log.d(HeadersDialog.class.getName(), "onOkClicked");
        HeadersSupport headersSupport = getHeadersSupport();
        if (headersSupport != null) {
            headersSupport.onHeadersDialogOkClicked(this);
        } else {
            Log.e(HeadersDialog.class.getName(), "globalHeadersSupport is null");
            dismiss();
        }
    }

    private void onCancelClicked(View view) {
        Log.d(HeadersDialog.class.getName(), "onCancelClicked");
        HeadersSupport headersSupport = getHeadersSupport();
        if (headersSupport != null) {
            headersSupport.onHeadersDialogCancelClicked(this);
        } else {
            Log.e(HeadersDialog.class.getName(), "globalHeadersSupport is null");
            dismiss();
        }
    }

    public RecyclerView getHeadersRecyclerView() {
        return headersRecyclerView;
    }

    private RecyclerView.Adapter<?> restoreAdapter(Bundle adapterState) {
        Log.d(HeadersDialog.class.getName(), "restoreAdapter");
        HeadersAdapter adapter = new HeadersAdapter(Collections.emptyList(), this);
        adapter.restoreStateFromBundle(adapterState);
        return adapter;
    }

    private RecyclerView.Adapter<?> createAdapter() {
        Log.d(HeadersDialog.class.getName(), "createAdapter");
        List<Header> initialHeaders = Collections.emptyList();
        Bundle arguments = getArguments();
        if (arguments != null) {
            initialHeaders = BundleUtil.headerListFromBundle(getInitialHeadersKey(), arguments);
        }
        return new HeadersAdapter(initialHeaders, this);
    }

    public HeadersAdapter getAdapter() {
        return (HeadersAdapter) getHeadersRecyclerView().getAdapter();
    }

    private HeadersSupport getHeadersSupport() {
        Log.d(HeadersDialog.class.getName(), "getHeadersSupport");
        Activity activity = getActivity();
        if (activity == null) {
            Log.e(HeadersDialog.class.getName(), "getGlobalHeadersSupport, activity is null");
            return null;
        }
        if (!(activity instanceof HeadersSupport)) {
            Log.e(HeadersDialog.class.getName(), "getGlobalHeadersSupport, activity is not an instance of " + HeadersSupport.class.getSimpleName());
            return null;
        }
        return (HeadersSupport) activity;
    }

    private void showGlobalHeaderEditDialog(Header header, int position) {
        Log.d(HeadersDialog.class.getName(), "showGlobalHeaderEditDialog with header " + header + " and position " + position);
        HeaderEditDialog headerEditDialog = new HeaderEditDialog();
        Bundle bundle = BundleUtil.bundleToBundle(headerEditDialog.getHeaderKey(), header.toBundle());
        bundle = BundleUtil.integerToBundle(headerEditDialog.getPositionKey(), position, bundle);
        headerEditDialog.setArguments(bundle);
        showDialog(headerEditDialog, HeaderEditDialog.class.getName());
    }

    private void showDialog(DialogFragment dialog, String name) {
        try {
            dialog.show(getParentFragmentManager(), name);
        } catch (Exception exc) {
            Log.e(HeadersDialog.class.getName(), "Error opening dialog", exc);
        }
    }

    public static List<Header> sortHeaderList(List<Header> headerList) {
        List<Header> sortedList = new ArrayList<>(headerList);
        Collections.sort(sortedList, new HeaderComparator());
        return sortedList;
    }

    private static class HeaderComparator implements Comparator<Header> {
        @Override
        public int compare(Header header1, Header header2) {
            if (header1.isEqual(header2)) {
                return 0;
            }
            return StringUtil.notNull(header1.getName()).compareTo(StringUtil.notNull(header2.getName()));
        }
    }
}
