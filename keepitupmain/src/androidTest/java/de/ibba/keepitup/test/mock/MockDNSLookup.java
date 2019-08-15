package de.ibba.keepitup.test.mock;

import de.ibba.keepitup.service.network.DNSLookup;
import de.ibba.keepitup.service.network.DNSLookupResult;

public class MockDNSLookup extends DNSLookup {

    private final DNSLookupResult dnsLookupResult;

    public MockDNSLookup(String host, DNSLookupResult dnsLookupResult) {
        super(host);
        this.dnsLookupResult = dnsLookupResult;
    }

    @Override
    public DNSLookupResult call() throws Exception {
        return dnsLookupResult;
    }
}
