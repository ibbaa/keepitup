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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.checkbox.MaterialCheckBox;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.model.SNMPInterfaceInfo;
import net.ibbaa.keepitup.model.SNMPItem;
import net.ibbaa.keepitup.model.SNMPItemMergeResult;
import net.ibbaa.keepitup.model.SNMPVersion;
import net.ibbaa.keepitup.service.network.SNMPMapping;
import net.ibbaa.keepitup.ui.adapter.SNMPInterfacesAdapter;
import net.ibbaa.keepitup.ui.support.SNMPInterfacesSupport;
import net.ibbaa.keepitup.ui.sync.SNMPScanResult;
import net.ibbaa.keepitup.ui.sync.SNMPScanTask;
import net.ibbaa.keepitup.ui.sync.SNMPScanViewModel;
import net.ibbaa.keepitup.ui.validation.ValidationResult;
import net.ibbaa.keepitup.util.BundleUtil;
import net.ibbaa.keepitup.util.ExceptionUtil;
import net.ibbaa.keepitup.util.ThreadUtil;
import net.ibbaa.keepitup.util.UIUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@SuppressWarnings({"unused"})
public class SNMPInterfacesDialog extends DialogFragmentBase {

    private View dialogView;
    private RecyclerView snmpInterfacesRecyclerView;
    private SNMPScanViewModel scanViewModel;
    private SNMPScanTask scanTask;
    private boolean scanned;
    private List<SNMPItem> initialSNMPItems;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(SNMPInterfacesDialog.class.getName(), "onCreate");
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.DialogTheme);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(SNMPInterfacesDialog.class.getName(), "onCreateView");
        dialogView = inflater.inflate(R.layout.dialog_snmp_interfaces, container);
        initEdgeToEdgeInsets(dialogView);
        boolean containsSavedState = containsSavedState(savedInstanceState);
        Log.d(SNMPInterfacesDialog.class.getName(), "containsSavedState is " + containsSavedState);
        Bundle adapterState = containsSavedState ? savedInstanceState.getBundle(getSNMPInterfacesAdapterKey()) : null;
        prepareScanned(savedInstanceState);
        prepareScanFields();
        prepareSNMPInterfacesRecyclerView(adapterState);
        prepareShowAll();
        prepareOkCancelImageButtons();
        return dialogView;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        Log.d(SNMPInterfacesDialog.class.getName(), "onViewCreated");
        super.onViewCreated(view, savedInstanceState);
        scanViewModel = new ViewModelProvider(this).get(SNMPScanViewModel.class);
        scanViewModel.getScanDispatcher().observe(getViewLifecycleOwner(), this::onScanDone);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Log.d(SNMPInterfacesDialog.class.getName(), "onSaveInstanceState");
        super.onSaveInstanceState(outState);
        Bundle adapterBundle = getAdapter().saveStateToBundle();
        outState.putBundle(getSNMPInterfacesAdapterKey(), adapterBundle);
        outState.putBoolean(getScannedKey(), scanned);
    }

    private boolean containsSavedState(Bundle savedInstanceState) {
        Log.d(SNMPInterfacesDialog.class.getName(), "containsSavedState");
        if (savedInstanceState == null) {
            Log.d(SNMPInterfacesDialog.class.getName(), "savedInstanceState bundle is null");
            return false;
        }
        return savedInstanceState.containsKey(getSNMPInterfacesAdapterKey());
    }

    private void prepareScanned(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.containsKey(getScannedKey())) {
            scanned = savedInstanceState.getBoolean(getScannedKey());
        } else {
            scanned = false;
        }
    }

    private void prepareScanFields() {
        Log.d(SNMPInterfacesDialog.class.getName(), "prepareScanFields");
        ImageView scanImageView = dialogView.findViewById(R.id.imageview_dialog_snmp_interfaces_scan);
        scanImageView.setOnClickListener(this::onScanClicked);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void prepareShowAll() {
        Log.d(SNMPInterfacesDialog.class.getName(), "prepareShowAll");
        MaterialCheckBox showAllCheckBox = dialogView.findViewById(R.id.checkbox_dialog_snmp_interfaces_show_all);
        showAllCheckBox.setChecked(getAdapter().isShowAll());
        showAllCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            getAdapter().setShowAll(isChecked);
            getAdapter().notifyDataSetChanged();
        });
    }

    private void prepareSNMPInterfacesRecyclerView(Bundle adapterState) {
        Log.d(SNMPInterfacesDialog.class.getName(), "prepareSNMPInterfacesRecyclerView");
        Bundle arguments = getArguments();
        initialSNMPItems = arguments != null ? BundleUtil.snmpItemListFromBundle(getInitialSNMPItemsKey(), arguments) : Collections.emptyList();
        snmpInterfacesRecyclerView = dialogView.findViewById(R.id.listview_dialog_snmp_interfaces_items);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        snmpInterfacesRecyclerView.setLayoutManager(layoutManager);
        snmpInterfacesRecyclerView.setItemAnimator(new DefaultItemAnimator());
        RecyclerView.Adapter<?> adapter = adapterState == null ? createAdapter() : restoreAdapter(adapterState);
        snmpInterfacesRecyclerView.setAdapter(adapter);
    }

    private void prepareOkCancelImageButtons() {
        Log.d(SNMPInterfacesDialog.class.getName(), "prepareOkCancelImageButtons");
        ImageView okImage = dialogView.findViewById(R.id.imageview_dialog_snmp_interfaces_ok);
        ImageView cancelImage = dialogView.findViewById(R.id.imageview_dialog_snmp_interfaces_cancel);
        okImage.setOnClickListener(this::onOkClicked);
        cancelImage.setOnClickListener(this::onCancelClicked);
    }

    public String getAddressKey() {
        return SNMPInterfacesDialog.class.getSimpleName() + ".Address";
    }

    public String getPortKey() {
        return SNMPInterfacesDialog.class.getSimpleName() + ".Port";
    }

    public String getSNMPVersionKey() {
        return SNMPInterfacesDialog.class.getSimpleName() + ".SNMPVersion";
    }

    public String getCommunityKey() {
        return SNMPInterfacesDialog.class.getSimpleName() + ".Community";
    }

    public String getInitialSNMPItemsKey() {
        return SNMPInterfacesDialog.class.getSimpleName() + ".InitialSNMPItems";
    }

    public String getNetworkTaskIdKey() {
        return SNMPInterfacesDialog.class.getSimpleName() + ".NetworkTaskId";
    }

    private String getSNMPInterfacesAdapterKey() {
        return SNMPInterfacesDialog.class.getSimpleName() + ".SNMPItemAdapter";
    }

    private String getScannedKey() {
        return SNMPInterfacesDialog.class.getSimpleName() + ".Scanned";
    }

    public List<SNMPItem> getInitialSNMPItems() {
        return initialSNMPItems != null ? Collections.unmodifiableList(initialSNMPItems) : Collections.emptyList();
    }

    public void onScanClicked(View view) {
        Log.d(SNMPInterfacesDialog.class.getName(), "onScanClicked");
        showProgressDialog();
        Future<SNMPScanResult> scanFuture = ThreadUtil.execute(getScanTask());
        boolean synchronousExecution = getResources().getBoolean(R.bool.uisync_synchronous_execution);
        if (synchronousExecution) {
            try {
                int timeout = getResources().getInteger(R.integer.snmp_scan_timeout) + getResources().getInteger(R.integer.dns_timeout);
                scanFuture.get(timeout, TimeUnit.MILLISECONDS);
            } catch (Exception exc) {
                Log.e(SNMPInterfacesDialog.class.getName(), "Error waiting for scan execution", exc);
                closeProgressDialog();
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void onScanDone(SNMPScanResult result) {
        Log.d(SNMPInterfacesDialog.class.getName(), "onScanDone, success is " + result.success());
        scanned = true;
        if (result.success()) {
            SNMPMapping snmpMapping = new SNMPMapping(requireContext());
            SNMPItemMergeResult mergeResult = snmpMapping.mergeDescrItems(getAdapter().getAllItems(), result.descrResult());
            List<SNMPItem> removedMonitoredItems = mergeResult.removedMonitoredItems();
            if (result.interfaceInfos().isEmpty()) {
                getAdapter().replaceItems(mergeResult.mergedItems());
            } else {
                Map<String, SNMPInterfaceInfo> mergedInfos = snmpMapping.mergeSNMPInterfaceInfos(getAdapter().getInterfaceInfos(), result.interfaceInfos());
                getAdapter().replaceItems(mergeResult.mergedItems(), mergedInfos);
            }
            if (!removedMonitoredItems.isEmpty()) {
                List<ValidationResult> removedMonitoredResult = UIUtil.snmpRemovedMonitoredSNMPItemsValidationResultList(requireContext(), removedMonitoredItems);
                if (!removedMonitoredResult.isEmpty()) {
                    showValidatorErrorDialog(removedMonitoredResult, requireContext().getResources().getString(R.string.text_dialog_validator_error_title_snmp_removed));
                }
            }
        } else {
            List<String> errors = result.errorMessages();
            if (result.exception() != null) {
                errors = new ArrayList<>(errors);
                errors.add(getMessageFromException(result.exception()));
            }
            List<ValidationResult> walkErrorResult = UIUtil.snmpWalkErrorsToValidationResultList(requireContext(), errors);
            if (!walkErrorResult.isEmpty()) {
                showValidatorErrorDialog(walkErrorResult, requireContext().getResources().getString(R.string.text_dialog_validator_error_title_snmp));
            }
        }
        getAdapter().notifyDataSetChanged();
        closeProgressDialog();
    }

    private void onOkClicked(View view) {
        Log.d(SNMPInterfacesDialog.class.getName(), "onOkClicked");
        SNMPInterfacesSupport snmpInterfacesSupport = getSNMPInterfacesSupport();
        if (snmpInterfacesSupport != null) {
            snmpInterfacesSupport.onSNMPInterfacesDialogOkClicked(this);
        } else {
            Log.e(SNMPInterfacesDialog.class.getName(), "snmpInterfacesSupport is null");
            dismiss();
        }
    }

    private void onCancelClicked(View view) {
        Log.d(SNMPInterfacesDialog.class.getName(), "onCancelClicked");
        SNMPInterfacesSupport snmpInterfacesSupport = getSNMPInterfacesSupport();
        if (snmpInterfacesSupport != null) {
            snmpInterfacesSupport.onSNMPInterfacesDialogCancelClicked(this);
        } else {
            Log.e(SNMPInterfacesDialog.class.getName(), "snmpInterfacesSupport is null");
            dismiss();
        }
    }

    public boolean isScanned() {
        return scanned;
    }

    public RecyclerView getSNMPInterfacesRecyclerView() {
        return snmpInterfacesRecyclerView;
    }

    public SNMPInterfacesAdapter getAdapter() {
        return (SNMPInterfacesAdapter) getSNMPInterfacesRecyclerView().getAdapter();
    }

    private RecyclerView.Adapter<?> restoreAdapter(Bundle adapterState) {
        Log.d(SNMPInterfacesDialog.class.getName(), "restoreAdapter");
        SNMPInterfacesAdapter adapter = new SNMPInterfacesAdapter(Collections.emptyList(), Collections.emptyMap(), this);
        adapter.restoreStateFromBundle(adapterState);
        return adapter;
    }

    private RecyclerView.Adapter<?> createAdapter() {
        Log.d(SNMPInterfacesDialog.class.getName(), "createAdapter");
        SNMPMapping snmpMapping = new SNMPMapping(requireContext());
        List<SNMPItem> descrItems = snmpMapping.filterDescrItems(initialSNMPItems);
        return new SNMPInterfacesAdapter(descrItems, snmpMapping.extractSNMPInterfaceInfos(initialSNMPItems), this);
    }

    private SNMPInterfacesSupport getSNMPInterfacesSupport() {
        Log.d(SNMPInterfacesDialog.class.getName(), "getSNMPInterfacesSupport");
        List<Fragment> fragments = getParentFragmentManager().getFragments();
        for (Fragment fragment : fragments) {
            if (fragment instanceof SNMPInterfacesSupport) {
                return (SNMPInterfacesSupport) fragment;
            }
        }
        Activity activity = getActivity();
        if (activity == null) {
            Log.e(SNMPInterfacesDialog.class.getName(), "getSNMPInterfacesSupport, activity is null");
            return null;
        }
        if (!(activity instanceof SNMPInterfacesSupport)) {
            Log.e(SNMPInterfacesDialog.class.getName(), "getSNMPInterfacesSupport, activity is not an instance of " + SNMPInterfacesSupport.class.getSimpleName());
            return null;
        }
        return (SNMPInterfacesSupport) activity;
    }

    public void injectScanTask(SNMPScanTask task) {
        this.scanTask = task;
    }

    protected SNMPScanTask getScanTask() {
        if (scanTask != null) {
            return scanTask;
        }
        String address = BundleUtil.stringFromBundle(getAddressKey(), requireArguments());
        int port = BundleUtil.integerFromBundle(getPortKey(), requireArguments());
        SNMPVersion snmpVersion = SNMPVersion.valueOf(BundleUtil.stringFromBundle(getSNMPVersionKey(), requireArguments()));
        String community = BundleUtil.stringFromBundle(getCommunityKey(), requireArguments());
        long networkTaskId = BundleUtil.longFromBundle(getNetworkTaskIdKey(), requireArguments());
        return new SNMPScanTask(scanViewModel.getScanDispatcher(), requireContext(), networkTaskId, address, port, snmpVersion, community);
    }

    protected void showProgressDialog() {
        Log.d(SNMPInterfacesDialog.class.getName(), "showProgressDialog");
        ProgressDialog progressDialog = new ProgressDialog();
        progressDialog.show(getParentFragmentManager(), ProgressDialog.class.getName());
    }

    private void showValidatorErrorDialog(List<ValidationResult> validationResult, String title) {
        Log.d(SNMPInterfacesDialog.class.getName(), "showValidatorErrorDialog");
        ValidatorErrorDialog errorDialog = new ValidatorErrorDialog();
        Bundle bundle = BundleUtil.validationResultListToBundle(errorDialog.getValidationResultBaseKey(), validationResult);
        bundle = BundleUtil.stringToBundle(errorDialog.getTitleKey(), title, bundle);
        bundle = BundleUtil.integerToBundle(errorDialog.getMessageWidthKey(), getResources().getDimensionPixelSize(R.dimen.textview_dialog_grid_based_message_width), bundle);
        errorDialog.setArguments(bundle);
        errorDialog.show(getParentFragmentManager(), ValidatorErrorDialog.class.getName());
    }

    private String getMessageFromException(Throwable exc) {
        return ExceptionUtil.getLogableMessage(ExceptionUtil.getRootCause(exc));
    }

    protected void closeProgressDialog() {
        Log.d(SNMPInterfacesDialog.class.getName(), "closeProgressDialog");
        List<Fragment> fragments = getParentFragmentManager().getFragments();
        for (Fragment fragment : fragments) {
            if (fragment instanceof ProgressDialog) {
                try {
                    ((ProgressDialog) fragment).dismiss();
                } catch (Exception exc) {
                    Log.d(SNMPInterfacesDialog.class.getName(), "Error closing ProgressDialog", exc);
                }
            }
        }
    }
}
