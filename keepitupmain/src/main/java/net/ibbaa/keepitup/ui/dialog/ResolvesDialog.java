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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.model.Resolve;
import net.ibbaa.keepitup.ui.adapter.DeleteSwipeCallback;
import net.ibbaa.keepitup.ui.adapter.ResolvesAdapter;
import net.ibbaa.keepitup.ui.support.ConfirmSupport;
import net.ibbaa.keepitup.ui.support.ResolveEditSupport;
import net.ibbaa.keepitup.ui.support.ResolvesSupport;
import net.ibbaa.keepitup.ui.support.SwipeDeleteSupport;
import net.ibbaa.keepitup.util.BundleUtil;

import java.util.Collections;
import java.util.List;

@SuppressWarnings({"unused"})
public class ResolvesDialog extends DialogFragmentBase implements ResolveEditSupport, ConfirmSupport, SwipeDeleteSupport {

    private View dialogView;
    private RecyclerView resolvesRecyclerView;
    private ItemTouchHelper itemTouchHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(ResolvesDialog.class.getName(), "onCreate");
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.DialogTheme);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(ResolvesDialog.class.getName(), "onCreateView");
        dialogView = inflater.inflate(R.layout.dialog_resolves, container);
        initEdgeToEdgeInsets(dialogView);
        boolean containsSavedState = containsSavedState(savedInstanceState);
        Log.d(ResolvesDialog.class.getName(), "containsSavedState is " + containsSavedState);
        Bundle adapterState = containsSavedState ? savedInstanceState.getBundle(getResolvesAdapterKey()) : null;
        prepareResolvesRecyclerView(adapterState);
        prepareAddImageButton();
        prepareOkCancelImageButtons();
        return dialogView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Log.d(ResolvesDialog.class.getName(), "onSaveInstanceState");
        super.onSaveInstanceState(outState);
        Bundle adapterBundle = getAdapter().saveStateToBundle();
        outState.putBundle(getResolvesAdapterKey(), adapterBundle);
    }

    private boolean containsSavedState(Bundle savedInstanceState) {
        Log.d(ResolvesDialog.class.getName(), "containsSavedState");
        if (savedInstanceState == null) {
            Log.d(ResolvesDialog.class.getName(), "savedInstanceState bundle is null");
            return false;
        }
        return savedInstanceState.containsKey(getResolvesAdapterKey());
    }

    public String getNetworkTaskIdKey() {
        return ResolvesDialog.class.getSimpleName() + ".NetworkTaskId";
    }

    public String getNetworkTaskURLKey() {
        return ResolvesDialog.class.getSimpleName() + ".NetworkTaskURL";
    }

    public String getInitialResolvesKey() {
        return ResolvesDialog.class.getSimpleName() + ".InitialResolves";
    }

    private String getResolvesAdapterKey() {
        return ResolvesDialog.class.getSimpleName() + ".ResolvesAdapter";
    }

    public long getNetworkTaskId() {
        return BundleUtil.longFromBundle(getNetworkTaskIdKey(), requireArguments());
    }

    public String getNetworkTaskURL() {
        return BundleUtil.stringFromBundle(getNetworkTaskURLKey(), requireArguments());
    }

    private void prepareResolvesRecyclerView(Bundle adapterState) {
        Log.d(ResolvesDialog.class.getName(), "prepareResolvesRecyclerView");
        resolvesRecyclerView = dialogView.findViewById(R.id.listview_dialog_resolves_resolves);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        resolvesRecyclerView.setLayoutManager(layoutManager);
        resolvesRecyclerView.setItemAnimator(new DefaultItemAnimator());
        RecyclerView.Adapter<?> adapter = adapterState == null ? createAdapter() : restoreAdapter(adapterState);
        resolvesRecyclerView.setAdapter(adapter);
        itemTouchHelper = new ItemTouchHelper(new DeleteSwipeCallback(this));
        itemTouchHelper.attachToRecyclerView(resolvesRecyclerView);
    }

    private void prepareAddImageButton() {
        Log.d(ResolvesDialog.class.getName(), "prepareAddImageButton");
        ImageView addImage = dialogView.findViewById(R.id.imageview_dialog_resolves_add);
        addImage.setOnClickListener(this::onResolveAddClicked);
    }

    private void prepareOkCancelImageButtons() {
        Log.d(ResolvesDialog.class.getName(), "prepareOkCancelImageButtons");
        ImageView okImage = dialogView.findViewById(R.id.imageview_dialog_resolves_ok);
        ImageView cancelImage = dialogView.findViewById(R.id.imageview_dialog_resolves_cancel);
        okImage.setOnClickListener(this::onOkClicked);
        cancelImage.setOnClickListener(this::onCancelClicked);
    }

    public void onResolveOpenClicked(View view, int index) {
        Log.d(ResolvesDialog.class.getName(), "onResolveOpenClicked for index " + index);
        Resolve resolve = getAdapter().getItem(index);
        if (resolve == null) {
            Log.e(ResolvesDialog.class.getName(), "onResolveOpenClicked, resolve is null");
            return;
        }
        showResolveEditDialog(resolve, index);
    }

    public void onResolveDeleteClicked(View view, int index) {
        Log.d(ResolvesDialog.class.getName(), "onResolveDeleteClicked for index " + index);
        if (index < 0) {
            Log.e(ResolvesDialog.class.getName(), "index " + index + " is invalid");
            return;
        }
        openConfirmDialogDelete(index, ConfirmDialog.Type.DELETERESOLVE);
    }

    @Override
    public void onDeleteSwiped(int index) {
        Log.d(ResolvesDialog.class.getName(), "onDeleteSwiped for index " + index);
        if (index < 0) {
            Log.e(ResolvesDialog.class.getName(), "index " + index + " is invalid");
            return;
        }
        openConfirmDialogDelete(index, ConfirmDialog.Type.DELETERESOLVESWIPE);
    }

    private void openConfirmDialogDelete(int index, ConfirmDialog.Type type) {
        ConfirmDialog confirmDialog = new ConfirmDialog();
        String confirmMessage = getResources().getString(R.string.text_dialog_confirm_delete_resolve);
        Bundle bundle = BundleUtil.stringsToBundle(new String[]{confirmDialog.getMessageKey(), confirmDialog.getTypeKey()}, new String[]{confirmMessage, type.name()});
        bundle.putInt(confirmDialog.getPositionKey(), index);
        confirmDialog.setArguments(bundle);
        showDialog(confirmDialog, ConfirmDialog.class.getName());
    }

    @Override
    @SuppressWarnings("NotifyDataSetChanged")
    public void onConfirmDialogOkClicked(ConfirmDialog confirmDialog, ConfirmDialog.Type type) {
        Log.d(ResolvesDialog.class.getName(), "onConfirmDialogOkClicked for type " + type);
        if (ConfirmDialog.Type.DELETERESOLVE.equals(type) || ConfirmDialog.Type.DELETERESOLVESWIPE.equals(type)) {
            int deletePosition = confirmDialog.getPosition();
            if (deletePosition >= 0) {
                Log.d(ResolvesDialog.class.getName(), "deleting resolve at position " + deletePosition);
                getAdapter().removeItem(deletePosition);
                getAdapter().notifyItemRemoved(deletePosition);
                getAdapter().notifyItemRangeChanged(deletePosition, getAdapter().getItemCount());
                if (ConfirmDialog.Type.DELETERESOLVESWIPE.equals(type)) {
                    reattachItemTouchHelper();
                }
            } else {
                Log.e(ResolvesDialog.class.getName(), ConfirmDialog.class.getSimpleName() + " arguments do not contain deletePosition key " + confirmDialog.getPositionKey());
            }
        } else {
            Log.e(ResolvesDialog.class.getName(), "Unknown type " + type);
        }
        confirmDialog.dismiss();
    }

    @Override
    public void onConfirmDialogCancelClicked(ConfirmDialog confirmDialog, ConfirmDialog.Type type) {
        Log.d(ResolvesDialog.class.getName(), "onConfirmDialogCancelClicked");
        if (ConfirmDialog.Type.DELETERESOLVESWIPE.equals(type)) {
            int position = confirmDialog.getPosition();
            getAdapter().notifyItemChanged(position);
            reattachItemTouchHelper();
        }
        confirmDialog.dismiss();
    }

    private void reattachItemTouchHelper() {
        Log.d(ResolvesDialog.class.getName(), "reattachItemTouchHelper");
        RecyclerView recyclerView = dialogView.findViewById(R.id.listview_dialog_resolves_resolves);
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
    public void onResolveEditDialogOkClicked(ResolveEditDialog resolveEditDialog, int position) {
        Log.d(ResolvesDialog.class.getName(), "onResolveEditDialogOkClicked with position " + position);
        if (position >= 0) {
            getAdapter().removeItem(position);
        }
        Resolve resolve = resolveEditDialog.getResolve();
        getAdapter().addItem(resolve);
        getAdapter().notifyDataSetChanged();
        resolveEditDialog.dismiss();
    }

    @Override
    public void onResolveEditDialogCancelClicked(ResolveEditDialog resolveEditDialog) {
        Log.d(ResolvesDialog.class.getName(), "onResolveEditDialogCancelClicked");
        resolveEditDialog.dismiss();
    }

    private void onResolveAddClicked(View view) {
        Log.d(ResolvesDialog.class.getName(), "onResolveAddClicked");
        Resolve resolve = new Resolve(getNetworkTaskId());
        showResolveEditDialog(resolve, -1);
    }

    private void onOkClicked(View view) {
        Log.d(ResolvesDialog.class.getName(), "onOkClicked");
        ResolvesSupport resolvesSupport = getResolvesSupport();
        if (resolvesSupport != null) {
            resolvesSupport.onResolvesDialogOkClicked(this);
        } else {
            Log.e(ResolvesDialog.class.getName(), "resolvesSupport is null");
            dismiss();
        }
    }

    private void onCancelClicked(View view) {
        Log.d(ResolvesDialog.class.getName(), "onCancelClicked");
        ResolvesSupport resolvesSupport = getResolvesSupport();
        if (resolvesSupport != null) {
            resolvesSupport.onResolvesDialogCancelClicked(this);
        } else {
            Log.e(ResolvesDialog.class.getName(), "resolvesSupport is null");
            dismiss();
        }
    }

    public RecyclerView getResolvesRecyclerView() {
        return resolvesRecyclerView;
    }

    private RecyclerView.Adapter<?> restoreAdapter(Bundle adapterState) {
        Log.d(ResolvesDialog.class.getName(), "restoreAdapter");
        ResolvesAdapter adapter = new ResolvesAdapter(Collections.emptyList(), this);
        adapter.restoreStateFromBundle(adapterState);
        return adapter;
    }

    private RecyclerView.Adapter<?> createAdapter() {
        Log.d(ResolvesDialog.class.getName(), "createAdapter");
        List<Resolve> initialResolves = Collections.emptyList();
        Bundle arguments = getArguments();
        if (arguments != null) {
            initialResolves = BundleUtil.resolveListFromBundle(getInitialResolvesKey(), arguments);
        }
        return new ResolvesAdapter(initialResolves, this);
    }

    public ResolvesAdapter getAdapter() {
        return (ResolvesAdapter) getResolvesRecyclerView().getAdapter();
    }

    private ResolvesSupport getResolvesSupport() {
        Log.d(ResolvesDialog.class.getName(), "getResolvesSupport");
        List<Fragment> fragments = getParentFragmentManager().getFragments();
        for (Fragment fragment : fragments) {
            if (fragment instanceof ResolvesSupport) {
                return (ResolvesSupport) fragment;
            }
        }
        Activity activity = getActivity();
        if (activity == null) {
            Log.e(ResolvesDialog.class.getName(), "getResolvesSupport, activity is null");
            return null;
        }
        if (!(activity instanceof ResolvesSupport)) {
            Log.e(ResolvesDialog.class.getName(), "getResolvesSupport, activity is not an instance of " + ResolvesSupport.class.getSimpleName());
            return null;
        }
        return (ResolvesSupport) activity;
    }

    private void showResolveEditDialog(Resolve resolve, int position) {
        Log.d(ResolvesDialog.class.getName(), "showResolveEditDialog with resolve " + resolve + " and position " + position);
        ResolveEditDialog resolveEditDialog = new ResolveEditDialog();
        Bundle bundle = BundleUtil.bundleToBundle(resolveEditDialog.getResolveKey(), resolve.toBundle());
        bundle = BundleUtil.integerToBundle(resolveEditDialog.getPositionKey(), position, bundle);
        resolveEditDialog.setArguments(bundle);
        showDialog(resolveEditDialog, ResolveEditDialog.class.getName());
    }

    private void showDialog(DialogFragment dialog, String name) {
        try {
            dialog.show(getParentFragmentManager(), name);
        } catch (Exception exc) {
            Log.e(ResolvesDialog.class.getName(), "Error opening dialog", exc);
        }
    }
}
