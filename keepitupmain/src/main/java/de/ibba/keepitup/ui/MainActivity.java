package de.ibba.keepitup.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import de.ibba.keepitup.R;
import de.ibba.keepitup.service.NetworkKeepAliveServiceScheduler;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button startButton = findViewById(R.id.button_start);
        startButton.setOnClickListener(this::onStartClicked);
        Button stopButton = findViewById(R.id.button_stop);
        stopButton.setOnClickListener(this::onStopClicked);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_action_settings) {
            Log.d(MainActivity.class.getName(), "menu_action_settings triggered");
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivityForResult(intent, SettingsActivity.SETTING_ACTIVITY_CODE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(MainActivity.class.getName(), "onActivityResult");
        if (requestCode == SettingsActivity.SETTING_ACTIVITY_CODE) {
            Log.d(MainActivity.class.getName(), "onActivityResult returned from SettingsActivity");
            restartService();
        }
    }

    @SuppressWarnings("unused")
    private void onStartClicked(View view) {
        Log.d(MainActivity.class.getName(), "onStartClicked");
        NetworkKeepAliveServiceScheduler scheduler = new NetworkKeepAliveServiceScheduler(this);
        scheduler.start();
    }

    @SuppressWarnings("unused")
    private void onStopClicked(View view) {
        Log.d(MainActivity.class.getName(), "onStopClicked");
        NetworkKeepAliveServiceScheduler scheduler = new NetworkKeepAliveServiceScheduler(this);
        scheduler.stop();
    }

    private void restartService() {
        Log.d(MainActivity.class.getName(), "restartService");
        NetworkKeepAliveServiceScheduler scheduler = new NetworkKeepAliveServiceScheduler(this);
        if (scheduler.isRunning()) {
            Log.d(MainActivity.class.getName(), "Restarting service...");
            scheduler.stop();
            scheduler.start();
        } else {
            Log.d(MainActivity.class.getName(), "Service is not running. Restart skipped.");
        }
    }
}
