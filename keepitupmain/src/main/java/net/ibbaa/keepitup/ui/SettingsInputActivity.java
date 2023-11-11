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

package net.ibbaa.keepitup.ui;

import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.service.IFileManager;
import net.ibbaa.keepitup.service.IPowerManager;
import net.ibbaa.keepitup.service.SystemFileManager;
import net.ibbaa.keepitup.service.SystemPowerManager;
import net.ibbaa.keepitup.service.TimeBasedSuspensionScheduler;
import net.ibbaa.keepitup.ui.dialog.BatteryOptimizationDialog;
import net.ibbaa.keepitup.ui.dialog.ConfirmDialog;
import net.ibbaa.keepitup.ui.dialog.FileChooseDialog;
import net.ibbaa.keepitup.ui.dialog.GeneralErrorDialog;
import net.ibbaa.keepitup.ui.dialog.ProgressDialog;
import net.ibbaa.keepitup.ui.dialog.SettingsInput;
import net.ibbaa.keepitup.ui.dialog.SettingsInputDialog;
import net.ibbaa.keepitup.util.BundleUtil;

import java.util.List;

public abstract class SettingsInputActivity extends AppCompatActivity implements SettingsInputSupport, FileChooseSupport, BatteryOptimizationSupport, ConfirmSupport {

    private Resources resources;
    private IFileManager fileManager;
    private IPowerManager powerManager;
    private TimeBasedSuspensionScheduler timeBasedScheduler;

    public void injectResources(Resources resources) {
        this.resources = resources;
    }

    public void injectFileManager(IFileManager fileManager) {
        this.fileManager = fileManager;
    }

    public void injectPowerManager(IPowerManager powerManager) {
        this.powerManager = powerManager;
    }

    public void injectTimeBasedSuspensionScheduler(TimeBasedSuspensionScheduler timeBasedScheduler) {
        this.timeBasedScheduler = timeBasedScheduler;
    }

    @Override
    public Resources getResources() {
        if (resources != null) {
            return resources;
        }
        return super.getResources();
    }

    @Override
    public IFileManager getFileManager() {
        if (fileManager != null) {
            return fileManager;
        }
        return new SystemFileManager(this);
    }

    @Override
    public IPowerManager getPowerManager() {
        if (powerManager != null) {
            return powerManager;
        }
        return new SystemPowerManager(this);
    }

    public TimeBasedSuspensionScheduler getTimeBasedSuspensionScheduler() {
        if (timeBasedScheduler != null) {
            return timeBasedScheduler;
        }
        return new TimeBasedSuspensionScheduler(this);
    }

    @Override
    public void onInputDialogOkClicked(SettingsInputDialog inputDialog, SettingsInput.Type type) {
        Log.d(SettingsInputActivity.class.getName(), "onInputDialogOkClicked, type is " + type + ", value is " + inputDialog.getValue());
        inputDialog.dismiss();
    }

    @Override
    public void onInputDialogCancelClicked(SettingsInputDialog inputDialog) {
        Log.d(SettingsInputActivity.class.getName(), "onInputDialogCancelClicked");
        inputDialog.dismiss();
    }

    @Override
    public void onFileChooseDialogOkClicked(FileChooseDialog chooseDialog, FileChooseDialog.Type type) {
        Log.d(SettingsInputActivity.class.getName(), "onFileChooseDialogOkClicked");
        chooseDialog.dismiss();
    }

    @Override
    public void onFileChooseDialogCancelClicked(FileChooseDialog chooseDialog) {
        Log.d(SettingsInputActivity.class.getName(), "onFileChooseDialogCancelClicked");
        chooseDialog.dismiss();
    }

    @Override
    public void onBatteryOptimizationDialogOkClicked(BatteryOptimizationDialog batteryOptimizationDialog) {
        Log.d(SettingsInputActivity.class.getName(), "onBatteryOptimizationDialogOkClicked");
        batteryOptimizationDialog.dismiss();
    }

    @Override
    public void onConfirmDialogOkClicked(ConfirmDialog confirmDialog, ConfirmDialog.Type type) {
        Log.d(SettingsInputActivity.class.getName(), "onConfirmDialogOkClicked");
        confirmDialog.dismiss();
    }

    @Override
    public void onConfirmDialogCancelClicked(ConfirmDialog confirmDialog) {
        Log.d(SettingsInputActivity.class.getName(), "onConfirmDialogCancelClicked");
        confirmDialog.dismiss();
    }

    protected void recreateActivity() {
        Log.d(SettingsInputActivity.class.getName(), "recreateActivity");
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }

    protected void showErrorDialog(String errorMessage) {
        showErrorDialog(errorMessage, Typeface.BOLD);
    }

    protected void showErrorDialog(String errorMessage, int typeface) {
        Log.d(SettingsInputActivity.class.getName(), "showErrorDialog with message " + errorMessage);
        GeneralErrorDialog errorDialog = new GeneralErrorDialog();
        Bundle bundle = BundleUtil.stringToBundle(errorDialog.getMessageKey(), errorMessage);
        bundle.putInt(errorDialog.getTypefaceStyleKey(), typeface);
        errorDialog.setArguments(bundle);
        showDialog(errorDialog, GeneralErrorDialog.class.getName());
    }

    protected void showConfirmDialog(String confirmMessage, String description, ConfirmDialog.Type type) {
        Log.d(SettingsInputActivity.class.getName(), "showConfirmDialog with message " + confirmMessage + " an description " + description + " for type " + type);
        ConfirmDialog confirmDialog = new ConfirmDialog();
        Bundle bundle = BundleUtil.stringsToBundle(new String[]{confirmDialog.getMessageKey(), confirmDialog.getDescriptionKey(), confirmDialog.getTypeKey()}, new String[]{confirmMessage, description, type.name()});
        confirmDialog.setArguments(bundle);
        showDialog(confirmDialog, ConfirmDialog.class.getName());
    }

    protected void showConfirmDialog(String confirmMessage, String description, ConfirmDialog.Type type, Bundle extraData) {
        Log.d(SettingsInputActivity.class.getName(), "showConfirmDialog with message " + confirmMessage + " an description " + description + " for type " + type);
        ConfirmDialog confirmDialog = new ConfirmDialog();
        Bundle bundle = BundleUtil.stringsToBundle(new String[]{confirmDialog.getMessageKey(), confirmDialog.getDescriptionKey(), confirmDialog.getTypeKey()}, new String[]{confirmMessage, description, type.name()});
        BundleUtil.bundleToBundle(confirmDialog.getExtraDataKey(), extraData, bundle);
        confirmDialog.setArguments(bundle);
        showDialog(confirmDialog, ConfirmDialog.class.getName());
    }

    protected ProgressDialog showProgressDialog() {
        Log.d(SettingsInputActivity.class.getName(), "showProgressDialog");
        ProgressDialog progressDialog = new ProgressDialog();
        showDialog(progressDialog, ProgressDialog.class.getName());
        return progressDialog;
    }

    protected void closeProgressDialog() {
        Log.d(SettingsInputActivity.class.getName(), "closeProgressDialog");
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for (Fragment fragment : fragments) {
            if (fragment instanceof ProgressDialog) {
                try {
                    ((ProgressDialog) fragment).dismiss();
                } catch (Exception exc) {
                    Log.d(SettingsInputActivity.class.getName(), "Error closing ProgressDialog", exc);
                }
            }
        }
    }

    private void showDialog(DialogFragment dialog, String name) {
        try {
            dialog.show(getSupportFragmentManager(), name);
        } catch (Exception exc) {
            Log.d(SettingsInputActivity.class.getName(), "Error opening dialog", exc);
        }
    }
}
