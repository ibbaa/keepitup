package de.ibba.keepitup.ui;

import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import de.ibba.keepitup.resources.FileManager;
import de.ibba.keepitup.resources.IFileManager;
import de.ibba.keepitup.ui.dialog.FolderChooseDialog;
import de.ibba.keepitup.ui.dialog.GeneralErrorDialog;
import de.ibba.keepitup.ui.dialog.SettingsInput;
import de.ibba.keepitup.ui.dialog.SettingsInputDialog;
import de.ibba.keepitup.util.BundleUtil;

public abstract class SettingsInputActivity extends AppCompatActivity {

    private Resources resources;
    private IFileManager fileManager;

    public void injectResources(Resources resources) {
        this.resources = resources;
    }

    public void injectFileManager(IFileManager fileManager) {
        this.fileManager = fileManager;
    }

    @Override
    public Resources getResources() {
        if (resources != null) {
            return resources;
        }
        return super.getResources();
    }

    public IFileManager getFileManager() {
        if (fileManager != null) {
            return fileManager;
        }
        return new FileManager(this);
    }

    public void onInputDialogOkClicked(SettingsInputDialog inputDialog, SettingsInput.Type type) {
        Log.d(SettingsInputActivity.class.getName(), "onInputDialogOkClicked, type is " + type + ", value is " + inputDialog.getValue());
        inputDialog.dismiss();
    }

    public void onInputDialogCancelClicked(SettingsInputDialog inputDialog) {
        Log.d(SettingsInputActivity.class.getName(), "onInputDialogCancelClicked");
        inputDialog.dismiss();
    }

    public void onFolderChooseEditDialogOkClicked(FolderChooseDialog editDialog) {
        Log.d(SettingsInputActivity.class.getName(), "onFolderChooseEditDialogOkClicked");
        editDialog.dismiss();
    }

    public void onFolderChooseEditDialogCancelClicked(FolderChooseDialog editDialog) {
        Log.d(SettingsInputActivity.class.getName(), "onFolderChooseEditDialogOkClicked");
        editDialog.dismiss();
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
        Bundle bundle = BundleUtil.messageToBundle(GeneralErrorDialog.class.getSimpleName(), errorMessage);
        bundle.putInt(errorDialog.getTypefaceStyleKey(), typeface);
        errorDialog.setArguments(bundle);
        errorDialog.show(getSupportFragmentManager(), GeneralErrorDialog.class.getName());
    }
}
