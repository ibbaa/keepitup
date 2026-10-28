/*
 * Copyright (c) 2026 Alwin Ibba
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.ibbaa.keepitup.service;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.RouteInfo;
import android.os.Build;

import net.ibbaa.keepitup.logging.Log;

import java.net.InetAddress;

@SuppressWarnings({"deprecation", "RedundantSuppression", "ClassCanBeRecord"})
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

    @Override
    public boolean isLocalAddress(InetAddress address) {
        Log.d(SystemNetworkManager.class.getName(), "isLocalAddress for address " + address);
        if (address == null) {
            return false;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.CINNAMON_BUN) {
            Log.d(SystemNetworkManager.class.getName(), "Local network detection is not required for API level " + Build.VERSION.SDK_INT);
            return false;
        }
        if (address.isLoopbackAddress()) {
            Log.d(SystemNetworkManager.class.getName(), address + " is a loopback address");
            return false;
        }
        if (address.isLinkLocalAddress() || address.isMulticastAddress() || isBroadcastAddress(address)) {
            Log.d(SystemNetworkManager.class.getName(), address + " is a link local, multicast or broadcast address");
            return true;
        }
        try {
            Network network = connectivityManager.getActiveNetwork();
            if (network == null) {
                Log.d(SystemNetworkManager.class.getName(), "No active network");
                return false;
            }
            LinkProperties linkProperties = connectivityManager.getLinkProperties(network);
            if (linkProperties == null) {
                Log.d(SystemNetworkManager.class.getName(), "No link properties for active network");
                return false;
            }
            for (RouteInfo route : linkProperties.getRoutes()) {
                if (!route.isDefaultRoute() && !route.hasGateway() && route.matches(address)) {
                    Log.d(SystemNetworkManager.class.getName(), address + " matches directly connected route " + route);
                    return true;
                }
            }
        } catch (Exception exc) {
            Log.e(SystemNetworkManager.class.getName(), "Error determining whether " + address + " is a local address", exc);
            return false;
        }
        Log.d(SystemNetworkManager.class.getName(), address + " is not a local address");
        return false;
    }

    private boolean isBroadcastAddress(InetAddress address) {
        byte[] bytes = address.getAddress();
        if (bytes.length != 4) {
            return false;
        }
        for (byte currentByte : bytes) {
            if (currentByte != (byte) 0xff) {
                return false;
            }
        }
        return true;
    }
}
