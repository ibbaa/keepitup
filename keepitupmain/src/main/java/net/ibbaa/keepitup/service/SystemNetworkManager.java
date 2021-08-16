package net.ibbaa.keepitup.service;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import net.ibbaa.keepitup.logging.Log;

@SuppressWarnings({"deprecation", "RedundantSuppression"})
public class SystemNetworkManager implements INetworkManager {

    private final ConnectivityManager connectivityManager;

    public SystemNetworkManager(Context context) {
        this.connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    @Override
    public boolean isConnected() {
        Log.d(SystemNetworkManager.class.getName(), "isConnected");
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null) {
            return networkInfo.isConnectedOrConnecting();
        }
        return false;
    }

    @Override
    public boolean isConnectedWithWiFi() {
        Log.d(SystemNetworkManager.class.getName(), "isConnectedWithWiFi");
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null) {
            return networkInfo.isConnectedOrConnecting() && networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
        }
        return false;
    }
}
