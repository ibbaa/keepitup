package de.ibba.keepitup.service.network;

import android.util.Log;

import java.net.InetAddress;
import java.util.concurrent.Callable;

public class DNSLookup implements Callable<DNSLookupResult> {

    private final String host;

    public DNSLookup(String host) {
        this.host = host;
    }

    @Override
    public DNSLookupResult call() throws Exception {
        Log.d(DNSLookup.class.getName(), "call");
        try {
            InetAddress address = InetAddress.getByName(host);
            return new DNSLookupResult(address, null);
        } catch (Exception exc) {
            Log.e(DNSLookup.class.getName(), "Error executing DNS lookup", exc);
            return new DNSLookupResult(null, exc);
        }
    }
}
