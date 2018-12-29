package de.ibba.keepitup.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import de.ibba.keepitup.R;
import de.ibba.keepitup.service.NetworkKeppAliveServiceScheduler;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onStartClicked(View view) {
        Log.d(MainActivity.class.getName(), "onStartClicked");
        NetworkKeppAliveServiceScheduler scheduler = new NetworkKeppAliveServiceScheduler(this);
        scheduler.start();
        ;
    }

    private void onStopClicked(View view) {
        Log.d(MainActivity.class.getName(), "onStopClicked");
        NetworkKeppAliveServiceScheduler scheduler = new NetworkKeppAliveServiceScheduler(this);
        scheduler.stop();
    }
}
