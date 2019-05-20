package de.ibba.keepitup.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import de.ibba.keepitup.R;
import de.ibba.keepitup.resources.NetworkTaskPreferenceManager;
import de.ibba.keepitup.ui.dialog.SettingsInput;
import de.ibba.keepitup.ui.dialog.SettingsInputDialog;
import de.ibba.keepitup.ui.validation.HostFieldValidator;
import de.ibba.keepitup.ui.validation.URLFieldValidator;
import de.ibba.keepitup.util.StringUtil;

public class SettingsActivity extends AppCompatActivity {

    private TextView addressText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_settings);
        prepareAddressField();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_action_reset) {
            Log.d(SettingsActivity.class.getName(), "menu_action_defaults triggered");
            /*SharedPreferences.Editor preferencesEditor = PreferenceManager.getDefaultSharedPreferences(this).edit();
            preferencesEditor.remove(getResources().getString(R.string.interval_setting_key));
            preferencesEditor.remove(getResources().getString(R.string.key_settings_defaults_address));
            preferencesEditor.apply();
            PreferenceManager.setDefaultValues(this, R.xml.settings, true);
            recreate();*/
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void prepareAddressField() {
        NetworkTaskPreferenceManager preferenceManager = new NetworkTaskPreferenceManager(this);
        addressText = findViewById(R.id.textview_settings_activity_address);
        addressText.setText(preferenceManager.getPreferenceAddress());
        CardView addressCardView = findViewById(R.id.cardview_settings_activity_address);
        addressCardView.setOnClickListener(this::showAddressInputDialog);
    }

    private String getAddress() {
        return StringUtil.notNull(addressText.getText());
    }

    private void showAddressInputDialog(View view) {
        List<String> validators = Arrays.asList(HostFieldValidator.class.getName(), URLFieldValidator.class.getName());
        SettingsInput input = new SettingsInput(getAddress(), getResources().getString(R.string.label_settings_activity_address), validators);
        showInputDialog(input.toBundle());
    }

    private void showInputDialog(Bundle bundle) {
        Log.d(SettingsActivity.class.getName(), "showErrorDialog, opening SettingsInputDialog");
        SettingsInputDialog inputDialog = new SettingsInputDialog();
        inputDialog.setArguments(bundle);
        inputDialog.show(getSupportFragmentManager(), SettingsInputDialog.class.getName());
    }

    public void onInputDialogOkClicked(SettingsInputDialog inputDialog) {
        Log.d(SettingsActivity.class.getName(), "onInputDialogOkClicked");
        inputDialog.dismiss();
    }

    public void onInputDialogCancelClicked(SettingsInputDialog inputDialog) {
        Log.d(SettingsActivity.class.getName(), "onInputDialogCancelClicked");
        inputDialog.dismiss();
    }
}
