package de.ibba.keepitup.service.network;

import androidx.annotation.NonNull;

import java.net.InetAddress;
import java.util.Collections;
import java.util.List;

public class DNSLookupResult {

    private final List<InetAddress> addresses;
    private final Throwable exception;

    public DNSLookupResult(InetAddress address, Throwable exception) {
        this.addresses = Collections.singletonList(address);
        this.exception = exception;
    }

    public DNSLookupResult(List<InetAddress> addresses, Throwable exception) {
        this.addresses = addresses;
        this.exception = exception;
    }

    public List<InetAddress> getAddresses() {
        return Collections.unmodifiableList(addresses);
    }

    public Throwable getException() {
        return exception;
    }

    @NonNull
    @Override
    public String toString() {
        return "DNSLookupResult{" +
                "addresses=" + addresses +
                ", exception=" + exception +
                '}';
    }
}
