package de.ibba.keepitup.ui;

import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import de.ibba.keepitup.ui.dialog.SettingsInput;
import de.ibba.keepitup.ui.dialog.SettingsInputDialog;

public abstract class SettingsInputActivity extends AppCompatActivity {

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
