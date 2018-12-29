package de.ibba.keepitup.ui;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.common.net.InetAddresses;
import com.google.common.net.InternetDomainName;

import de.ibba.keepitup.R;
import de.ibba.keepitup.util.NumberUtil;
import de.ibba.keepitup.util.StringUtil;

public class SettingsActivity extends AppCompatPreferenceActivity {

    public static final int SETTING_ACTIVITY_CODE = 1000;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_action_defaults) {
            Log.d(SettingsActivity.class.getName(), "menu_action_defaults triggered");
            SharedPreferences.Editor preferencesEditor = PreferenceManager.getDefaultSharedPreferences(this).edit();
            preferencesEditor.remove(getResources().getString(R.string.interval_setting_key));
            preferencesEditor.remove(getResources().getString(R.string.hostname_setting_key));
            preferencesEditor.apply();
            PreferenceManager.setDefaultValues(this, R.xml.settings, true);
            recreate();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);
            Preference interval = findPreference(getResources().getString(R.string.interval_setting_key));
            interval.setOnPreferenceChangeListener(this::onIntervalChanged);
            Preference hostname = findPreference(getResources().getString(R.string.hostname_setting_key));
            hostname.setOnPreferenceChangeListener(this::onHostnameChanged);
        }

        @SuppressWarnings("unused")
        boolean onIntervalChanged(Preference preference, Object newValue) {
            Log.d(SettingsActivity.class.getName(), "onIntervalChanged validating input " + newValue);
            boolean isValidValue = NumberUtil.isValidLongValue(newValue);
            long refreshInterval = NumberUtil.getLongValue(newValue, getResources().getInteger(R.integer.interval_setting_default));
            long refreshIntervalMinimum = getResources().getInteger(R.integer.interval_setting_minimum);
            if (!isValidValue || refreshInterval < refreshIntervalMinimum) {
                Log.d(SettingsActivity.class.getName(), "onIntervalChanged, input " + newValue + " is invalid");
                String failure = getResources().getString(R.string.interval_setting_label) + System.lineSeparator()
                        + getResources().getString(R.string.string_value) + ": " + newValue + System.lineSeparator()
                        + getResources().getString(R.string.string_minimum) + ": " + refreshIntervalMinimum;
                showErrorDialog(failure);
                return false;
            }
            Log.d(SettingsActivity.class.getName(), "onIntervalChanged, input " + newValue + " is valid");
            return true;
        }

        @SuppressWarnings("unused")
        boolean onHostnameChanged(Preference preference, Object newValue) {
            Log.d(SettingsActivity.class.getName(), "onHostnameChanged validating input " + newValue);
            String hostname = StringUtil.getStringValue(newValue, "");
            boolean isValidHostname = InternetDomainName.isValid(hostname);
            boolean isValidIPAddress = InetAddresses.isInetAddress(hostname);
            if (!isValidHostname && !isValidIPAddress) {
                Log.d(SettingsActivity.class.getName(), "onHostnameChanged, input " + newValue + " is invalid");
                @SuppressWarnings("ConstantConditions") String failure = getResources().getString(R.string.hostname_setting_label) + System.lineSeparator()
                        + getResources().getString(R.string.string_value) + ": " + newValue + System.lineSeparator()
                        + getResources().getString(R.string.string_hostname_valid) + ": " + isValidHostname + System.lineSeparator()
                        + getResources().getString(R.string.string_ip_valid) + ": " + isValidIPAddress;
                showErrorDialog(failure);
                return false;
            }
            Log.d(SettingsActivity.class.getName(), "onHostnameChanged, input " + newValue + " is valid");
            return true;
        }

        private void showErrorDialog(String failureText) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(getResources().getString(R.string.string_invalid_input));
            builder.setMessage(failureText);
            builder.setPositiveButton(android.R.string.ok, null);
            builder.show();
        }
    }
}
