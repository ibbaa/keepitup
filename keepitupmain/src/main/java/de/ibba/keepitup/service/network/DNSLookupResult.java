package de.ibba.keepitup.service.network;

import androidx.annotation.NonNull;

import java.net.InetAddress;

public class DNSLookupResult {

    private final InetAddress address;
    private final Throwable exception;

    public DNSLookupResult(InetAddress address, Throwable exception) {
        this.address = address;
        this.exception = exception;
    }

    public InetAddress getAddress() {
        return address;
    }

    public Throwable getException() {
        return exception;
    }

    @NonNull
    @Override
    public String toString() {
        return "DNSLookupResult{" +
                "address=" + address +
                ", exception=" + exception +
                '}';
    }
}
