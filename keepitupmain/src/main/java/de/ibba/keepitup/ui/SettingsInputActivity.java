package de.ibba.keepitup.ui;

import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import java.util.List;

import de.ibba.keepitup.logging.Log;
import de.ibba.keepitup.service.IFileManager;
import de.ibba.keepitup.service.IPowerManager;
import de.ibba.keepitup.service.SystemFileManager;
import de.ibba.keepitup.service.SystemPowerManager;
import de.ibba.keepitup.ui.dialog.BatteryOptimizationDialog;
import de.ibba.keepitup.ui.dialog.ConfirmDialog;
import de.ibba.keepitup.ui.dialog.FileChooseDialog;
import de.ibba.keepitup.ui.dialog.GeneralErrorDialog;
import de.ibba.keepitup.ui.dialog.ProgressDialog;
import de.ibba.keepitup.ui.dialog.SettingsInput;
import de.ibba.keepitup.ui.dialog.SettingsInputDialog;
import de.ibba.keepitup.util.BundleUtil;

public abstract class SettingsInputActivity extends AppCompatActivity implements SettingsInputSupport, FileChooseSupport, BatteryOptimizationSupport, ConfirmSupport {

    private Resources resources;
    private IFileManager fileManager;
    private IPowerManager powerManager;

    public void injectResources(Resources resources) {
        this.resources = resources;
    }

    public void injectFileManager(IFileManager fileManager) {
        this.fileManager = fileManager;
    }

    public void injectPowerManager(IPowerManager powerManager) {
        this.powerManager = powerManager;
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
        Log.d(SettingsInputActivity.class.getName(), "onFolderChooseDialogOkClicked");
        chooseDialog.dismiss();
    }

    @Override
    public void onFileChooseDialogCancelClicked(FileChooseDialog chooseDialog) {
        Log.d(SettingsInputActivity.class.getName(), "onFolderChooseDialogOkClicked");
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
