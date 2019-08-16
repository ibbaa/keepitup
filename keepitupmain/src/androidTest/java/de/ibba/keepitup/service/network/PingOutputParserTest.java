package de.ibba.keepitup.service.network;

import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertFalse;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class PingOutputParserTest {

    private PingOutputParser parser;

    @Before
    public void beforeEachTestMethod() {
        parser = new PingOutputParser();
        parser.reset();
    }

    @Test
    public void testEmptyInput() {
        parser.parse(null);
        assertFalse(parser.isValidInput());
        parser.parse("");
        assertFalse(parser.isValidInput());
    }

    @Test
    public void testValidInput() {
        parser.parse(getTestPing1());
    }

    private String getTestPing1() {
        return "PING 127.0.0.1 (127.0.0.1) 56(84) bytes of data.\n" +
                "64 bytes from 127.0.0.1: icmp_seq=1 ttl=64 time=0.084 ms\n" +
                "64 bytes from 127.0.0.1: icmp_seq=2 ttl=64 time=0.083 ms\n" +
                "64 bytes from 127.0.0.1: icmp_seq=3 ttl=64 time=0.083 ms\n\n" +
                "--- 127.0.0.1 ping statistics ---\n" +
                "3 packets transmitted, 3 received, 0% packet loss, time 1998ms\n" +
                "rtt min/avg/max/mdev = 0.083/0.083/0.084/0.007 ms";
    }
}
