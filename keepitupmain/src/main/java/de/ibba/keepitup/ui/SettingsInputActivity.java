package de.ibba.keepitup.ui;

import android.content.res.Resources;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import de.ibba.keepitup.ui.dialog.SettingsInput;
import de.ibba.keepitup.ui.dialog.SettingsInputDialog;

public abstract class SettingsInputActivity extends AppCompatActivity {

    private Resources resources;

    public void injectResources(Resources resources) {
        this.resources = resources;
    }

    @Override
    public Resources getResources() {
        if (resources != null) {
            return resources;
        }
        return super.getResources();
    }

    public abstract void onInputDialogOkClicked(SettingsInputDialog inputDialog, SettingsInput.Type type);

    public abstract void onInputDialogCancelClicked(SettingsInputDialog inputDialog);

    protected void recreateActivity() {
        Log.d(SettingsInputActivity.class.getName(), "recreateActivity");
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }
}
