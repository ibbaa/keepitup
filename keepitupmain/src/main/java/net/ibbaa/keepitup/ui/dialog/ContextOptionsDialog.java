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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.ui.ContextOptionsSupport;
import net.ibbaa.keepitup.ui.adapter.ContextOptionAdapter;
import net.ibbaa.keepitup.util.BundleUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ContextOptionsDialog extends DialogFragment {

    private View dialogView;
    private RecyclerView contextOptionRecyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(ContextOptionsDialog.class.getName(), "onCreate");
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.DialogTheme);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(ContextOptionsDialog.class.getName(), "onCreateView");
        dialogView = inflater.inflate(R.layout.dialog_context_options, container);
        boolean containsSavedState = containsSavedState(savedInstanceState);
        Log.d(ContextOptionsDialog.class.getName(), "containsSavedState is " + containsSavedState);
        Bundle adapterState = containsSavedState ? savedInstanceState.getBundle(getContextOptionAdapterKey()) : null;
        prepareFolderRecyclerView(adapterState);
        prepareCancelImageButton(dialogView);
        return dialogView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Log.d(ContextOptionsDialog.class.getName(), "onSaveInstanceState");
        super.onSaveInstanceState(outState);
        Bundle adapterBundle = getAdapter().saveStateToBundle();
        outState.putBundle(getContextOptionAdapterKey(), adapterBundle);
    }

    private boolean containsSavedState(Bundle savedInstanceState) {
        Log.d(ContextOptionsDialog.class.getName(), "containsSavedState");
        if (savedInstanceState == null) {
            Log.d(ContextOptionsDialog.class.getName(), "savedInstanceState bundle is null");
            return false;
        }
        return savedInstanceState.containsKey(getContextOptionAdapterKey());
    }

    public String getSourceResourceIdKey() {
        return ContextOptionsDialog.class.getSimpleName() + "SourceResourceId";
    }

    private String getContextOptionAdapterKey() {
        return ContextOptionsDialog.class.getSimpleName() + "ContextOptionAdapter";
    }

    public RecyclerView getContextOptionRecyclerView() {
        return contextOptionRecyclerView;
    }

    private void prepareFolderRecyclerView(Bundle adapterState) {
        Log.d(ContextOptionsDialog.class.getName(), "prepareFolderRecyclerView");
        contextOptionRecyclerView = dialogView.findViewById(R.id.listview_dialog_context_options);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        contextOptionRecyclerView.setLayoutManager(layoutManager);
        contextOptionRecyclerView.setItemAnimator(new DefaultItemAnimator());
        RecyclerView.Adapter<?> adapter = adapterState == null ? createAdapter() : restoreAdapter(adapterState);
        contextOptionRecyclerView.setAdapter(adapter);
    }

    private void prepareCancelImageButton(View view) {
        Log.d(ContextOptionsDialog.class.getName(), "prepareCancelImageButton");
        ImageView cancelImage = view.findViewById(R.id.imageview_dialog_context_options_cancel);
        cancelImage.setOnClickListener(this::onCancelClicked);
    }

    public ContextOptionAdapter getAdapter() {
        return (ContextOptionAdapter) getContextOptionRecyclerView().getAdapter();
    }

    private RecyclerView.Adapter<?> restoreAdapter(Bundle adapterState) {
        Log.d(ContextOptionsDialog.class.getName(), "restoreAdapter");
        ContextOptionAdapter adapter = new ContextOptionAdapter(Collections.emptyList(), this);
        adapter.restoreStateFromBundle(adapterState);
        return adapter;
    }

    private RecyclerView.Adapter<?> createAdapter() {
        Log.d(ContextOptionsDialog.class.getName(), "createAdapter");
        List<String> contextOptionStrings = BundleUtil.stringListFromBundle(ContextOption.class.getSimpleName(), requireArguments());
        List<ContextOption> contextOptionList = new ArrayList<>();
        for (String contextOptionString : contextOptionStrings) {
            try {
                contextOptionList.add(ContextOption.valueOf(contextOptionString));
            } catch (IllegalArgumentException exc) {
                Log.e(ContextOptionsDialog.class.getName(), ContextOption.class.getSimpleName() + "." + contextOptionString + " does not exist");
            }
        }
        return new ContextOptionAdapter(contextOptionList, this);
    }

    private void onCancelClicked(View view) {
        Log.d(ContextOptionsDialog.class.getName(), "onCancelClicked");
        dismiss();
    }

    public void onContextOptionClicked(View view, int position) {
        Log.d(ContextOptionsDialog.class.getName(), "onContextOptionClicked, position is " + position);
        getAdapter().selectItem(position);
        int sourceResourceId = requireArguments().getInt(getSourceResourceIdKey());
        ContextOption contextOption = getAdapter().getItem(position);
        Log.d(ContextOptionsDialog.class.getName(), "sourceResourceId is " + sourceResourceId);
        Log.d(ContextOptionsDialog.class.getName(), "contextOption is " + contextOption);
        ContextOptionsSupport contextOptionsSupport = getContextOptionsSupport();
        if (contextOptionsSupport != null) {
            contextOptionsSupport.onContextOptionsDialogClicked(this, sourceResourceId, contextOption);
        } else {
            Log.e(ContextOptionsDialog.class.getName(), "contextOptionsSupport is null");
            dismiss();
        }
    }

    protected ContextOptionsSupport getContextOptionsSupport() {
        Log.d(ContextOptionsDialog.class.getName(), "getContextOptionsSupport");
        List<Fragment> fragments = getParentFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment instanceof ContextOptionsSupport) {
                    return (ContextOptionsSupport) fragment;
                }
            }
        }
        Log.e(ContextOptionsDialog.class.getName(), "getContextOptionsSupport, no parent fragment implementing " + ContextOptionsSupport.class.getSimpleName());
        Activity activity = getActivity();
        if (activity == null) {
            Log.e(ContextOptionsDialog.class.getName(), "getContextOptionsSupport, activity is null");
            return null;
        }
        if (!(activity instanceof ContextOptionsSupport)) {
            Log.e(ContextOptionsDialog.class.getName(), "getContextOptionsSupport, activity is not an instance of " + ContextOptionsSupport.class.getSimpleName());
            return null;
        }
        return (ContextOptionsSupport) activity;
    }
}
