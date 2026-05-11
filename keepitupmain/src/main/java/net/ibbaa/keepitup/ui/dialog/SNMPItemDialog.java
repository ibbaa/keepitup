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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.model.SNMPItem;
import net.ibbaa.keepitup.ui.adapter.SNMPItemAdapter;
import net.ibbaa.keepitup.ui.support.SNMPItemSupport;
import net.ibbaa.keepitup.util.BundleUtil;

import java.util.Collections;
import java.util.List;

@SuppressWarnings({"unused"})
public class SNMPItemDialog extends DialogFragmentBase {

    private View dialogView;
    private RecyclerView snmpItemRecyclerView;
    private boolean scanned;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(SNMPItemDialog.class.getName(), "onCreate");
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.DialogTheme);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(SNMPItemDialog.class.getName(), "onCreateView");
        dialogView = inflater.inflate(R.layout.dialog_snmp_item, container);
        initEdgeToEdgeInsets(dialogView);
        boolean containsSavedState = containsSavedState(savedInstanceState);
        Log.d(SNMPItemDialog.class.getName(), "containsSavedState is " + containsSavedState);
        Bundle adapterState = containsSavedState ? savedInstanceState.getBundle(getSNMPItemAdapterKey()) : null;
        prepareScanned(savedInstanceState);
        prepareScanFields();
        prepareSNMPItemRecyclerView(adapterState);
        prepareOkCancelImageButtons();
        return dialogView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Log.d(SNMPItemDialog.class.getName(), "onSaveInstanceState");
        super.onSaveInstanceState(outState);
        Bundle adapterBundle = getAdapter().saveStateToBundle();
        outState.putBundle(getSNMPItemAdapterKey(), adapterBundle);
        outState.putBoolean(getScannedKey(), scanned);
    }

    private boolean containsSavedState(Bundle savedInstanceState) {
        Log.d(SNMPItemDialog.class.getName(), "containsSavedState");
        if (savedInstanceState == null) {
            Log.d(SNMPItemDialog.class.getName(), "savedInstanceState bundle is null");
            return false;
        }
        return savedInstanceState.containsKey(getSNMPItemAdapterKey());
    }

    private void prepareScanned(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.containsKey(getScannedKey())) {
            scanned = savedInstanceState.getBoolean(getScannedKey());
        } else {
            scanned = false;
        }
    }

    private void prepareScanFields() {
        Log.d(SNMPItemDialog.class.getName(), "prepareScanFields");
        ImageView scanImageView = dialogView.findViewById(R.id.imageview_dialog_snmp_item_scan);
        scanImageView.setOnClickListener(this::onScanClicked);
    }

    private void prepareSNMPItemRecyclerView(Bundle adapterState) {
        Log.d(SNMPItemDialog.class.getName(), "prepareSNMPItemRecyclerView");
        snmpItemRecyclerView = dialogView.findViewById(R.id.listview_dialog_snmp_item_items);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        snmpItemRecyclerView.setLayoutManager(layoutManager);
        snmpItemRecyclerView.setItemAnimator(new DefaultItemAnimator());
        RecyclerView.Adapter<?> adapter = adapterState == null ? createAdapter() : restoreAdapter(adapterState);
        snmpItemRecyclerView.setAdapter(adapter);
    }

    private void prepareOkCancelImageButtons() {
        Log.d(SNMPItemDialog.class.getName(), "prepareOkCancelImageButtons");
        ImageView okImage = dialogView.findViewById(R.id.imageview_dialog_snmp_item_ok);
        ImageView cancelImage = dialogView.findViewById(R.id.imageview_dialog_snmp_item_cancel);
        okImage.setOnClickListener(this::onOkClicked);
        cancelImage.setOnClickListener(this::onCancelClicked);
    }

    public void onScanClicked(View view) {
        Log.d(SNMPItemDialog.class.getName(), "onScanClicked");
        scanned = true;
    }

    private void onOkClicked(View view) {
        Log.d(SNMPItemDialog.class.getName(), "onOkClicked");
        SNMPItemSupport snmpItemSupport = getSNMPItemSupport();
        if (snmpItemSupport != null) {
            snmpItemSupport.onSNMPItemDialogOkClicked(this);
        } else {
            Log.e(SNMPItemDialog.class.getName(), "snmpItemSupport is null");
            dismiss();
        }
    }

    private void onCancelClicked(View view) {
        Log.d(SNMPItemDialog.class.getName(), "onCancelClicked");
        SNMPItemSupport snmpItemSupport = getSNMPItemSupport();
        if (snmpItemSupport != null) {
            snmpItemSupport.onSNMPItemDialogCancelClicked(this);
        } else {
            Log.e(SNMPItemDialog.class.getName(), "snmpItemSupport is null");
            dismiss();
        }
    }

    public boolean isScanned() {
        return scanned;
    }

    public RecyclerView getSNMPItemRecyclerView() {
        return snmpItemRecyclerView;
    }

    public SNMPItemAdapter getAdapter() {
        return (SNMPItemAdapter) getSNMPItemRecyclerView().getAdapter();
    }

    public String getInitialSNMPItemsKey() {
        return SNMPItemDialog.class.getSimpleName() + ".InitialSNMPItems";
    }

    private String getSNMPItemAdapterKey() {
        return SNMPItemDialog.class.getSimpleName() + ".SNMPItemAdapter";
    }

    private String getScannedKey() {
        return SNMPItemDialog.class.getSimpleName() + ".Scanned";
    }

    private RecyclerView.Adapter<?> restoreAdapter(Bundle adapterState) {
        Log.d(SNMPItemDialog.class.getName(), "restoreAdapter");
        SNMPItemAdapter adapter = new SNMPItemAdapter(Collections.emptyList(), this);
        adapter.restoreStateFromBundle(adapterState);
        return adapter;
    }

    private RecyclerView.Adapter<?> createAdapter() {
        Log.d(SNMPItemDialog.class.getName(), "createAdapter");
        List<SNMPItem> initialItems = Collections.emptyList();
        Bundle arguments = getArguments();
        if (arguments != null) {
            initialItems = BundleUtil.snmpItemListFromBundle(getInitialSNMPItemsKey(), arguments);
        }
        return new SNMPItemAdapter(initialItems, this);
    }

    private SNMPItemSupport getSNMPItemSupport() {
        Log.d(SNMPItemDialog.class.getName(), "getSNMPItemSupport");
        List<Fragment> fragments = getParentFragmentManager().getFragments();
        for (Fragment fragment : fragments) {
            if (fragment instanceof SNMPItemSupport) {
                return (SNMPItemSupport) fragment;
            }
        }
        Activity activity = getActivity();
        if (activity == null) {
            Log.e(SNMPItemDialog.class.getName(), "getSNMPItemSupport, activity is null");
            return null;
        }
        if (!(activity instanceof SNMPItemSupport)) {
            Log.e(SNMPItemDialog.class.getName(), "getSNMPItemSupport, activity is not an instance of " + SNMPItemSupport.class.getSimpleName());
            return null;
        }
        return (SNMPItemSupport) activity;
    }
}
