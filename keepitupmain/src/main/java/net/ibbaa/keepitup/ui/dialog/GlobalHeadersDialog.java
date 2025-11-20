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
import net.ibbaa.keepitup.ui.GlobalHeaderHandler;
import net.ibbaa.keepitup.ui.GlobalSettingsActivity;
import net.ibbaa.keepitup.ui.NetworkTaskMainActivity;
import net.ibbaa.keepitup.ui.adapter.DeleteSwipeCallback;
import net.ibbaa.keepitup.ui.adapter.GlobalHeaderAdapter;
import net.ibbaa.keepitup.ui.support.ConfirmSupport;
import net.ibbaa.keepitup.ui.support.GlobalHeaderEditSupport;
import net.ibbaa.keepitup.ui.support.GlobalHeadersSupport;
import net.ibbaa.keepitup.ui.support.SwipeDeleteSupport;
import net.ibbaa.keepitup.util.BundleUtil;

import java.util.Collections;
import java.util.Objects;

@SuppressWarnings({"unused"})
public class GlobalHeadersDialog extends DialogFragmentBase implements GlobalHeaderEditSupport, ConfirmSupport, SwipeDeleteSupport {

    private View dialogView;
    private RecyclerView globalHeadersRecyclerView;
    private ItemTouchHelper itemTouchHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(GlobalHeadersDialog.class.getName(), "onCreate");
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.DialogTheme);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(GlobalHeadersDialog.class.getName(), "onCreateView");
        dialogView = inflater.inflate(R.layout.dialog_global_headers, container);
        initEdgeToEdgeInsets(dialogView);
        boolean containsSavedState = containsSavedState(savedInstanceState);
        Log.d(GlobalHeadersDialog.class.getName(), "containsSavedState is " + containsSavedState);
        Bundle adapterState = containsSavedState ? savedInstanceState.getBundle(getGlobalHeadersAdapterKey()) : null;
        prepareGlobalHeadersRecyclerView(adapterState);
        prepareAddImageButton();
        prepareOkCancelImageButtons();
        return dialogView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Log.d(GlobalHeadersDialog.class.getName(), "onSaveInstanceState");
        super.onSaveInstanceState(outState);
        Bundle adapterBundle = getAdapter().saveStateToBundle();
        outState.putBundle(getGlobalHeadersAdapterKey(), adapterBundle);
    }

    private boolean containsSavedState(Bundle savedInstanceState) {
        Log.d(GlobalHeadersDialog.class.getName(), "containsSavedState");
        if (savedInstanceState == null) {
            Log.d(GlobalHeadersDialog.class.getName(), "savedInstanceState bundle is null");
            return false;
        }
        return savedInstanceState.containsKey(getGlobalHeadersAdapterKey());
    }

    private String getGlobalHeadersAdapterKey() {
        return GlobalHeadersDialog.class.getSimpleName() + "GlobalHeadersAdapter";
    }

    private void prepareGlobalHeadersRecyclerView(Bundle adapterState) {
        Log.d(GlobalHeadersDialog.class.getName(), "prepareGlobalHeadersRecyclerView");
        globalHeadersRecyclerView = dialogView.findViewById(R.id.listview_dialog_global_headers_headers);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        globalHeadersRecyclerView.setLayoutManager(layoutManager);
        globalHeadersRecyclerView.setItemAnimator(new DefaultItemAnimator());
        RecyclerView.Adapter<?> adapter = adapterState == null ? createAdapter() : restoreAdapter(adapterState);
        globalHeadersRecyclerView.setAdapter(adapter);
        itemTouchHelper = new ItemTouchHelper(new DeleteSwipeCallback(this));
        itemTouchHelper.attachToRecyclerView(globalHeadersRecyclerView);
    }

    private boolean noHeadersDefined() {
        return ((GlobalHeaderAdapter) Objects.requireNonNull(globalHeadersRecyclerView.getAdapter())).getAllItems().isEmpty();
    }

    private void prepareAddImageButton() {
        Log.d(GlobalHeadersDialog.class.getName(), "prepareAddImageButton");
        ImageView addImage = dialogView.findViewById(R.id.imageview_dialog_global_headers_add);
        addImage.setOnClickListener(this::onHeaderAddClicked);
    }

    private void prepareOkCancelImageButtons() {
        Log.d(GlobalHeadersDialog.class.getName(), "prepareOkCancelImageButtons");
        ImageView okImage = dialogView.findViewById(R.id.imageview_dialog_global_headers_ok);
        ImageView cancelImage = dialogView.findViewById(R.id.imageview_dialog_global_headers_cancel);
        okImage.setOnClickListener(this::onOkClicked);
        cancelImage.setOnClickListener(this::onCancelClicked);
    }

    public void onHeaderOpenClicked(View view, int index) {
        Log.d(GlobalHeadersDialog.class.getName(), "onHeaderOpenClicked for index " + index);
        Header header = getAdapter().getItem(index);
        if (header == null) {
            Log.e(GlobalHeadersDialog.class.getName(), "onHeaderOpenClicked, header is null");
            return;
        }
        //open dialog
    }

    public void onHeaderDeleteClicked(View view, int index) {
        Log.d(GlobalHeadersDialog.class.getName(), "onHeaderDeleteClicked for index " + index);
        if (index < 0) {
            Log.e(GlobalHeadersDialog.class.getName(), "index " + index + " is invalid");
            return;
        }
        openConfirmDialog(index, ConfirmDialog.Type.DELETEHEADER);
    }

    public void onDeleteSwiped(int index) {
        Log.d(GlobalHeadersDialog.class.getName(), "onHeaderDeleteSwiped for index " + index);
        if (index < 0) {
            Log.e(GlobalHeadersDialog.class.getName(), "index " + index + " is invalid");
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
        Log.d(GlobalHeadersDialog.class.getName(), "onConfirmDialogOkClicked for type " + type);
        if (ConfirmDialog.Type.DELETEHEADER.equals(type) || ConfirmDialog.Type.DELETEHEADERSWIPE.equals(type)) {
            int deletePosition = confirmDialog.getPosition();
            if (deletePosition >= 0) {
                Log.d(GlobalHeadersDialog.class.getName(), "deleting header at deletePosition " + deletePosition);
                getAdapter().removeItem(deletePosition);
                getAdapter().notifyItemRemoved(deletePosition);
                if (ConfirmDialog.Type.DELETEHEADERSWIPE.equals(type)) {
                    reattachItemTouchHelper();
                }
            } else {
                Log.e(GlobalHeadersDialog.class.getName(), ConfirmDialog.class.getSimpleName() + " arguments do not contain deletePosition key " + confirmDialog.getPositionKey());
            }
        } else {
            Log.e(GlobalHeadersDialog.class.getName(), "Unknown type " + type);
        }
        confirmDialog.dismiss();
    }

    @Override
    public void onConfirmDialogCancelClicked(ConfirmDialog confirmDialog, ConfirmDialog.Type type) {
        Log.d(GlobalHeadersDialog.class.getName(), "onConfirmDialogCancelClicked");
        if (ConfirmDialog.Type.DELETEHEADERSWIPE.equals(type)) {
            int position = confirmDialog.getPosition();
            getAdapter().notifyItemChanged(position);
            reattachItemTouchHelper();
        }
        confirmDialog.dismiss();
    }

    private void reattachItemTouchHelper() {
        Log.d(NetworkTaskMainActivity.class.getName(), "reattachItemTouchHelper");
        RecyclerView recyclerView = dialogView.findViewById(R.id.listview_dialog_global_headers_headers);
        if (itemTouchHelper != null) {
            itemTouchHelper.attachToRecyclerView(null);
        }
        recyclerView.post(() -> {
            itemTouchHelper = new ItemTouchHelper(new DeleteSwipeCallback(this));
            itemTouchHelper.attachToRecyclerView(recyclerView);
        });
    }

    @Override
    public void onGlobalHeaderEditDialogOkClicked(GlobalHeaderEditDialog globalHeaderEditDialog, int position) {
        Log.d(GlobalHeadersDialog.class.getName(), "onGlobalHeaderEditDialogOkClicked with position " + position);
        globalHeaderEditDialog.dismiss();
    }

    @Override
    public void onGlobalHeaderEditDialogCancelClicked(GlobalHeaderEditDialog globalHeaderEditDialog) {
        Log.d(GlobalHeadersDialog.class.getName(), "onGlobalHeaderEditDialogCancelClicked");
        globalHeaderEditDialog.dismiss();
    }

    /*@Override
    @SuppressWarnings("NotifyDataSetChanged")
    public void onGlobalHeaderSelectDialogOkClicked(SuspensionIntervalSelectDialog intervalSelectDialog, SuspensionIntervalSelectDialog.Mode mode) {
        Log.d(GlobalHeadersDialog.class.getName(), "onSuspensionIntervalSelectDialogOkClicked with mode ");
        if (SuspensionIntervalSelectDialog.Mode.START.equals(mode)) {
            prepareCurrentInterval(intervalSelectDialog);
            intervalSelectDialog.dismiss();
            showSuspensionIntervalSelectDialog(SuspensionIntervalSelectDialog.Mode.END, currentInterval.getEnd(), currentInterval.getStart());
        } else {
            currentInterval.setEnd(intervalSelectDialog.getSelectedTime());
            if (position >= 0) {
                getAdapter().removeItem(position);
                position = -1;
            }
            getAdapter().addItem(currentInterval);
            List<Interval> intervals = TimeUtil.sortIntervalList(getAdapter().getAllItems());
            getAdapter().replaceItems(intervals);
            getAdapter().notifyDataSetChanged();
            initializeCurrentInterval();
            intervalSelectDialog.dismiss();
        }
    }

    @Override
    public void onSuspensionIntervalSelectDialogCancelClicked(SuspensionIntervalSelectDialog intervalSelectDialog, SuspensionIntervalSelectDialog.Mode mode) {
        Log.d(GlobalHeadersDialog.class.getName(), "onSuspensionIntervalSelectDialogCancelClicked with mode " + mode);
        if (position >= 0) {
            position = -1;
            initializeCurrentInterval();
        } else {
            if (SuspensionIntervalSelectDialog.Mode.START.equals(mode)) {
                prepareCurrentInterval(intervalSelectDialog);
            } else {
                currentInterval.setEnd(intervalSelectDialog.getSelectedTime());
                keepEnd = true;
            }
        }
        intervalSelectDialog.dismiss();
    }

    private void prepareCurrentInterval(SuspensionIntervalSelectDialog intervalSelectDialog) {
        Log.d(GlobalHeadersDialog.class.getName(), "prepareCurrentInterval");
        Log.d(GlobalHeadersDialog.class.getName(), "position is " + position);
        Time start = intervalSelectDialog.getSelectedTime();
        Time end;
        if (position >= 0) {
            Interval interval = getAdapter().getItem(position);
            if (interval == null) {
                Log.e(GlobalHeadersDialog.class.getName(), "prepareCurrentInterval, interval at position " + position + " is null");
                end = getEnd(start);
            } else {
                end = isIntervalEndStillValid(interval, start) ? interval.getEnd() : getEnd(start);
            }
        } else {
            end = keepEnd && isIntervalEndStillValid(currentInterval, start) ? currentInterval.getEnd() : getEnd(start);
            getEnd(start);
        }
        Log.d(GlobalHeadersDialog.class.getName(), "start is " + start);
        Log.d(GlobalHeadersDialog.class.getName(), "end is " + end);
        currentInterval.setStart(start);
        currentInterval.setEnd(end);
    }*/

    private void onHeaderAddClicked(View view) {
        Log.d(GlobalHeadersDialog.class.getName(), "onHeaderAddClicked");
        showGlobalHeaderEditDialog(new Header(), -1);
    }

    private void onOkClicked(View view) {
        Log.d(GlobalHeadersDialog.class.getName(), "onOkClicked");
        GlobalHeadersSupport globalHeadersSupport = getGlobalHeadersSupport();
        if (globalHeadersSupport != null) {
            globalHeadersSupport.onGlobalHeadersDialogOkClicked(this);
        } else {
            Log.e(GlobalHeadersDialog.class.getName(), "globalHeadersSupport is null");
            dismiss();
        }
    }

    private void onCancelClicked(View view) {
        Log.d(GlobalHeadersDialog.class.getName(), "onCancelClicked");
        GlobalHeadersSupport globalHeadersSupport = getGlobalHeadersSupport();
        if (globalHeadersSupport != null) {
            globalHeadersSupport.onGlobalHeadersDialogCancelClicked(this);
        } else {
            Log.e(GlobalHeadersDialog.class.getName(), "globalHeadersSupport is null");
            dismiss();
        }
    }

    public RecyclerView getGlobalHeadersRecyclerView() {
        return globalHeadersRecyclerView;
    }

    private RecyclerView.Adapter<?> restoreAdapter(Bundle adapterState) {
        Log.d(GlobalHeadersDialog.class.getName(), "restoreAdapter");
        GlobalHeaderAdapter adapter = new GlobalHeaderAdapter(Collections.emptyList(), this);
        adapter.restoreStateFromBundle(adapterState);
        return adapter;
    }

    private RecyclerView.Adapter<?> createAdapter() {
        Log.d(GlobalHeadersDialog.class.getName(), "createAdapter");
        return new GlobalHeaderAdapter(getGlobalHeaderHandler().getGlobalHeaders(), this);
    }

    public GlobalHeaderAdapter getAdapter() {
        return (GlobalHeaderAdapter) getGlobalHeadersRecyclerView().getAdapter();
    }

    public GlobalHeaderHandler getGlobalHeaderHandler() {
        Log.d(GlobalHeadersDialog.class.getName(), "getGlobalHeaderHandler");
        Activity activity = getActivity();
        if (!(activity instanceof GlobalSettingsActivity)) {
            Log.e(SuspensionIntervalsDialog.class.getName(), "getGlobalHeaderHandler, activity is invalid");
            return null;
        }
        return new GlobalHeaderHandler((GlobalSettingsActivity) activity, this);
    }

    private GlobalHeadersSupport getGlobalHeadersSupport() {
        Log.d(GlobalHeadersDialog.class.getName(), "getGlobalHeadersSupport");
        Activity activity = getActivity();
        if (activity == null) {
            Log.e(GlobalHeadersDialog.class.getName(), "getGlobalHeadersSupport, activity is null");
            return null;
        }
        if (!(activity instanceof GlobalHeadersSupport)) {
            Log.e(GlobalHeadersDialog.class.getName(), "getGlobalHeadersSupport, activity is not an instance of " + GlobalHeadersSupport.class.getSimpleName());
            return null;
        }
        return (GlobalHeadersSupport) activity;
    }

    private void showGlobalHeaderEditDialog(Header header, int position) {
        Log.d(GlobalHeadersDialog.class.getName(), "showGlobalHeaderEditDialog with header " + header + " and position " + position);
        GlobalHeaderEditDialog headerEditDialog = new GlobalHeaderEditDialog();
        Bundle bundle = BundleUtil.bundleToBundle(headerEditDialog.getHeaderKey(), header.toBundle());
        bundle = BundleUtil.integerToBundle(headerEditDialog.getPositionKey(), position, bundle);
        headerEditDialog.setArguments(bundle);
        showDialog(headerEditDialog, GlobalHeaderEditDialog.class.getName());
    }

    private void showDialog(DialogFragment dialog, String name) {
        try {
            dialog.show(getParentFragmentManager(), name);
        } catch (Exception exc) {
            Log.e(GlobalHeadersDialog.class.getName(), "Error opening dialog", exc);
        }
    }
}
