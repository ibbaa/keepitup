package de.ibba.keepitup.service.network;

import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
    public void testInvalidInput() {
        parser.parse("xyz");
        assertFalse(parser.isValidInput());
        parser.parse(getInvalidTestIP4Ping1());
        assertFalse(parser.isValidInput());
    }

    @Test
    public void testValidSuccessfulIP4Input1() {
        parser.parse(getTestIP4Ping1());
        assertTrue(parser.isValidInput());
        assertEquals(3, parser.getPacketsTransmitted());
        assertEquals(3, parser.getPacketsReceived());
        assertEquals(0, parser.getPacketLoss(), 0.01);
        assertEquals(3, parser.getValidTimes());
        assertEquals(0.0833, parser.getAverageTime(), 0.01);
    }

    @Test
    public void testValidSuccessfulIP4Input2() {
        parser.parse(getTestIP4Ping2());
        assertTrue(parser.isValidInput());
        assertEquals(7, parser.getPacketsTransmitted());
        assertEquals(5, parser.getPacketsReceived());
        assertEquals(3.1, parser.getPacketLoss(), 0.01);
        assertEquals(5, parser.getValidTimes());
        assertEquals(0.42, parser.getAverageTime(), 0.01);
    }

    @Test
    public void testValidSuccessfulIP4Input3() {
        parser.parse(getTestIP4Ping3());
        assertTrue(parser.isValidInput());
        assertEquals(3, parser.getPacketsTransmitted());
        assertEquals(0, parser.getPacketsReceived());
        assertEquals(100, parser.getPacketLoss(), 0.01);
        assertEquals(0, parser.getValidTimes());
        assertEquals(0, parser.getAverageTime(), 0.01);
    }

    @Test
    public void testValidSuccessfulIP4Input4() {
        parser.parse(getTestIP4Ping4());
        assertTrue(parser.isValidInput());
        assertEquals(3, parser.getPacketsTransmitted());
        assertEquals(5, parser.getPacketsReceived());
        assertEquals(10, parser.getPacketLoss(), 0.01);
        assertEquals(2, parser.getValidTimes());
        assertEquals(4, parser.getAverageTime(), 0.01);
    }

    @Test
    public void testValidSuccessfulIP6Input1() {
        parser.parse(getTestIP6Ping1());
        assertTrue(parser.isValidInput());
        assertEquals(3, parser.getPacketsTransmitted());
        assertEquals(3, parser.getPacketsReceived());
        assertEquals(0, parser.getPacketLoss(), 0.01);
        assertEquals(3, parser.getValidTimes());
        assertEquals(20.1, parser.getAverageTime(), 0.01);
    }

    private String getTestIP4Ping1() {
        return "PING 127.0.0.1 (127.0.0.1) 56(84) bytes of data.\n" +
                "64 bytes from 127.0.0.1: icmp_seq=1 ttl=64 time=0.084 ms\n" +
                " 64 bytes from 127.0.0.1: icmp_seq=2 ttl=64 time=0.083 ms\n" +
                "64 bytes from 127.0.0.1: icmp_seq=3 ttl=64 time=0.083 ms\n\n" +
                "--- 127.0.0.1 ping statistics ---\n" +
                "3 packets transmitted, 3 received, 0% packet loss, time 1998ms\n" +
                "rtt min/avg/max/mdev = 0.083/0.083/0.084/0.007 ms";
    }

    private String getTestIP4Ping2() {
        return "PING 127.0.0.1 (127.0.0.1) 56(84) bytes of data.\n" +
                "64 bytes from 127.0.0.1: icmp_seq=1 ttl=64 time<1 ms\n" +
                " 64 bytes from 127.0.0.1: icmp_seq=2 ttl=64 time<1 ms\n" +
                " 64 bytes from 127.0.0.1: icmp_seq=2 ttl=64 time<1 ms\n" +
                " 64 bytes from 127.0.0.1: icmp_seq=2 ttl=64 time<1 ms\n" +
                "64 bytes from 127.0.0.1: icmp_seq=3 ttl=64 time=2.1 ms\n\n" +
                "--- 127.0.0.1 ping statistics ---\n" +
                "7 packets transmitted, 5 received, 3.1% packet loss, time 1998ms\n" +
                "rtt min/avg/max/mdev = 0.083/0.083/0.084/0.007 ms";
    }

    private String getTestIP4Ping3() {
        return "PING 192.168.178.1 (192.168.178.1) 56(84) bytes of data.\n\n" +
                "--- 192.168.178.1 ping statistics ---\n" +
                "3 packets transmitted, 0 received, 100% packet loss, time 1998ms";
    }

    private String getTestIP4Ping4() {
        return "PING 127.0.0.1 (127.0.0.1) 56(84) bytes of data.\n" +
                "64 bytes from 127.0.0.1: icmp_seq=1 ttl=64 time=x ms\n" +
                " 64 bytes from 127.0.0.1: icmp_seq=2 ttl=64 time=2 ms\n" +
                "64 bytes from 127.0.0.1: icmp_seq=3 ttl=64 time=6 ms\n\n" +
                "--- 127.0.0.1 ping statistics ---\n" +
                "3 packets transmitted, 5 received, 10% packet loss, time 1998ms\n" +
                "rtt min/avg/max/mdev = 0.083/0.083/0.084/0.007 ms";
    }

    private String getTestIP6Ping1() {
        return "PING 2a00:1450:4016:801::200e(2a00:1450:4016:801::200e) 56 data bytes\n" +
                "64 bytes from 2a00:1450:4016:801::200e: icmp_seq=1 ttl=57 time=10.5 ms\n" +
                "64 bytes from 2a00:1450:4016:801::200e: icmp_seq=2 ttl=57 time=23.7 ms\n" +
                "64 bytes from 2a00:1450:4016:801::200e: icmp_seq=3 ttl=57 time=26.1 ms\n\n" +
                "--- 2a00:1450:4016:801::200e ping statistics ---\n" +
                " 3 packets transmitted, 3 received, 0% packet loss, time 2003ms\n" +
                "rtt min/avg/max/mdev = 10.511/20.134/26.160/6.877 ms";
    }

    private String getInvalidTestIP4Ping1() {
        return "PING 127.0.0.1 (127.0.0.1) 56(84) bytes of data.\n" +
                "64 bytes from 127.0.0.1: icmp_seq=1 ttl=64 time=0.083 ms\n" +
                " 64 bytes from 127.0.0.1: icmp_seq=2 ttl=64 time=0.083 ms\n" +
                "64 bytes from 127.0.0.1: icmp_seq=3 ttl=64 time=0.083 ms\n\n" +
                "--- 127.0.0.1 ping statistics ---\n" +
                "3 packets transmitted, x received, 0% packet loss, time 1998ms\n" +
                "rtt min/avg/max/mdev = 0.083/0.083/0.084/0.007 ms";
    }
}
