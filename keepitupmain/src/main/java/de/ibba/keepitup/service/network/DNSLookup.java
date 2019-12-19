package de.ibba.keepitup.service.network;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.Callable;

import de.ibba.keepitup.logging.Log;

public class DNSLookup implements Callable<DNSLookupResult> {

    private final String host;

    public DNSLookup(String host) {
        this.host = host;
    }

    @Override
    public DNSLookupResult call() {
        Log.d(DNSLookup.class.getName(), "call");
        try {
            InetAddress[] addresses = InetAddress.getAllByName(host);
            return new DNSLookupResult(Arrays.asList(addresses), null);
        } catch (Exception exc) {
            Log.e(DNSLookup.class.getName(), "Error executing DNS lookup", exc);
            return new DNSLookupResult(Collections.emptyList(), exc);
        }
    }
}
