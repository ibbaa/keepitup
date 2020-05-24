package de.ibba.keepitup.ui;

import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.util.List;

import de.ibba.keepitup.logging.Log;
import de.ibba.keepitup.service.IFileManager;
import de.ibba.keepitup.service.IPowerManager;
import de.ibba.keepitup.service.SystemFileManager;
import de.ibba.keepitup.service.SystemPowerManager;
import de.ibba.keepitup.ui.dialog.BatteryOptimizationDialog;
import de.ibba.keepitup.ui.dialog.FolderChooseDialog;
import de.ibba.keepitup.ui.dialog.GeneralErrorDialog;
import de.ibba.keepitup.ui.dialog.SettingsInput;
import de.ibba.keepitup.ui.dialog.SettingsInputDialog;
import de.ibba.keepitup.util.BundleUtil;

public abstract class SettingsInputActivity extends AppCompatActivity implements SettingsInputSupport, FolderChooseSupport, BatteryOptimizationSupport {

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null) {
            restoreFragments(fragments);
        }
    }

    private void restoreFragments(List<Fragment> fragments) {
        for (Fragment currentFragment : fragments) {
            if (currentFragment instanceof SettingsInputAware) {
                ((SettingsInputAware) currentFragment).setSettingsInputSupport(this);
            }
            if (currentFragment instanceof FolderChooseAware) {
                ((FolderChooseAware) currentFragment).setFolderChooseSupport(this);
            }
            if (currentFragment instanceof BatteryOptimizationAware) {
                ((BatteryOptimizationAware) currentFragment).setBatteryOptimizationSupport(this);
            }
        }
    }

    public void onInputDialogOkClicked(SettingsInputDialog inputDialog, SettingsInput.Type type) {
        Log.d(SettingsInputActivity.class.getName(), "onInputDialogOkClicked, type is " + type + ", value is " + inputDialog.getValue());
        inputDialog.dismiss();
    }

    public void onInputDialogCancelClicked(SettingsInputDialog inputDialog) {
        Log.d(SettingsInputActivity.class.getName(), "onInputDialogCancelClicked");
        inputDialog.dismiss();
    }

    public void onFolderChooseDialogOkClicked(FolderChooseDialog chooseDialog) {
        Log.d(SettingsInputActivity.class.getName(), "onFolderChooseDialogOkClicked");
        chooseDialog.dismiss();
    }

    public void onFolderChooseDialogCancelClicked(FolderChooseDialog chooseDialog) {
        Log.d(SettingsInputActivity.class.getName(), "onFolderChooseDialogOkClicked");
        chooseDialog.dismiss();
    }

    @Override
    public void onBatteryOptimizationDialogOkClicked(BatteryOptimizationDialog batteryOptimizationDialog) {
        Log.d(SettingsInputActivity.class.getName(), "onFolderChooseDialogOkClicked");
        batteryOptimizationDialog.dismiss();
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
        Bundle bundle = BundleUtil.stringToBundle(GeneralErrorDialog.class.getSimpleName(), errorMessage);
        bundle.putInt(errorDialog.getTypefaceStyleKey(), typeface);
        errorDialog.setArguments(bundle);
        errorDialog.show(getSupportFragmentManager(), GeneralErrorDialog.class.getName());
    }
}
