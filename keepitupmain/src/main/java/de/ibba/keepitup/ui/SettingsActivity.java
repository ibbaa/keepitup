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
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import de.ibba.keepitup.R;
import de.ibba.keepitup.resources.NetworkTaskPreferenceManager;
import de.ibba.keepitup.ui.dialog.SettingsInput;
import de.ibba.keepitup.ui.dialog.SettingsInputDialog;
import de.ibba.keepitup.ui.validation.HostFieldValidator;
import de.ibba.keepitup.ui.validation.IntervalFieldValidator;
import de.ibba.keepitup.ui.validation.PortFieldValidator;
import de.ibba.keepitup.ui.validation.URLFieldValidator;
import de.ibba.keepitup.util.NumberUtil;
import de.ibba.keepitup.util.StringUtil;

public class SettingsActivity extends AppCompatActivity {

    private TextView addressText;
    private TextView portText;
    private TextView intervalText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_settings);
        prepareAddressField();
        preparePortField();
        prepareIntervalField();
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
            Log.d(SettingsActivity.class.getName(), "menu_action_reset triggered");
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
        setAddress(preferenceManager.getPreferenceAddress());
        CardView addressCardView = findViewById(R.id.cardview_settings_activity_address);
        addressCardView.setOnClickListener(this::showAddressInputDialog);
    }

    private void preparePortField() {
        NetworkTaskPreferenceManager preferenceManager = new NetworkTaskPreferenceManager(this);
        portText = findViewById(R.id.textview_settings_activity_port);
        setPort(String.valueOf(preferenceManager.getPreferencePort()));
        CardView portCardView = findViewById(R.id.cardview_settings_activity_port);
        portCardView.setOnClickListener(this::showPortInputDialog);
    }

    private void prepareIntervalField() {
        NetworkTaskPreferenceManager preferenceManager = new NetworkTaskPreferenceManager(this);
        intervalText = findViewById(R.id.textview_settings_activity_interval);
        setInterval(String.valueOf(preferenceManager.getPreferenceInterval()));
        CardView intervalCardView = findViewById(R.id.cardview_settings_activity_interval);
        intervalCardView.setOnClickListener(this::showIntervalInputDialog);
    }

    private String getAddress() {
        return StringUtil.notNull(addressText.getText());
    }

    private void setAddress(String address) {
        addressText.setText(StringUtil.notNull(address));
    }

    private String getPort() {
        return StringUtil.notNull(portText.getText());
    }

    private void setPort(String port) {
        portText.setText(StringUtil.notNull(port));
    }

    private String getInterval() {
        return StringUtil.notNull(intervalText.getText());
    }

    private void setInterval(String interval) {
        intervalText.setText(StringUtil.notNull(interval));
    }

    private void showAddressInputDialog(View view) {
        List<String> validators = Arrays.asList(HostFieldValidator.class.getName(), URLFieldValidator.class.getName());
        SettingsInput input = new SettingsInput(SettingsInput.Type.ADDRESS, getAddress(), getResources().getString(R.string.label_settings_activity_address), validators);
        showInputDialog(input.toBundle());
    }

    private void showPortInputDialog(View view) {
        List<String> validators = Collections.singletonList(PortFieldValidator.class.getName());
        SettingsInput input = new SettingsInput(SettingsInput.Type.PORT, getPort(), getResources().getString(R.string.label_settings_activity_port), validators);
        showInputDialog(input.toBundle());
    }

    private void showIntervalInputDialog(View view) {
        List<String> validators = Collections.singletonList(IntervalFieldValidator.class.getName());
        SettingsInput input = new SettingsInput(SettingsInput.Type.INTERVAL, getInterval(), getResources().getString(R.string.label_settings_activity_interval), validators);
        showInputDialog(input.toBundle());
    }

    private void showInputDialog(Bundle bundle) {
        Log.d(SettingsActivity.class.getName(), "showErrorDialog, opening SettingsInputDialog");
        SettingsInputDialog inputDialog = new SettingsInputDialog();
        inputDialog.setArguments(bundle);
        inputDialog.show(getSupportFragmentManager(), SettingsInputDialog.class.getName());
    }

    public void onInputDialogOkClicked(SettingsInputDialog inputDialog, SettingsInput.Type type) {
        Log.d(SettingsActivity.class.getName(), "onInputDialogOkClicked, type is " + type + ", value is " + inputDialog.getValue());
        NetworkTaskPreferenceManager preferenceManager = new NetworkTaskPreferenceManager(this);
        if (SettingsInput.Type.ADDRESS.equals(type)) {
            setAddress(inputDialog.getValue());
            preferenceManager.setPreferenceAddress(getAddress());
        } else if (SettingsInput.Type.PORT.equals(type)) {
            setPort(inputDialog.getValue());
            preferenceManager.setPreferencePort(NumberUtil.getIntValue(getPort(), getResources().getInteger(R.integer.task_port_default)));
        } else if (SettingsInput.Type.INTERVAL.equals(type)) {
            setInterval(inputDialog.getValue());
            preferenceManager.setPreferenceInterval(NumberUtil.getIntValue(getInterval(), getResources().getInteger(R.integer.task_interval_default)));
        } else {
            Log.e(SettingsActivity.class.getName(), "type " + type + " unknown");
        }
        inputDialog.dismiss();
    }

    public void onInputDialogCancelClicked(SettingsInputDialog inputDialog) {
        Log.d(SettingsActivity.class.getName(), "onInputDialogCancelClicked");
        inputDialog.dismiss();
    }
}
