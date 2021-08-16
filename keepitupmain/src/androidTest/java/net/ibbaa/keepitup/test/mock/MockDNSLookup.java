package net.ibbaa.keepitup.test.mock;

import net.ibbaa.keepitup.service.network.DNSLookup;
import net.ibbaa.keepitup.service.network.DNSLookupResult;

public class MockDNSLookup extends DNSLookup {

    private final DNSLookupResult dnsLookupResult;

    public MockDNSLookup(String host, DNSLookupResult dnsLookupResult) {
        super(host);
        this.dnsLookupResult = dnsLookupResult;
    }

    @Override
    public DNSLookupResult call() {
        return dnsLookupResult;
    }
}
