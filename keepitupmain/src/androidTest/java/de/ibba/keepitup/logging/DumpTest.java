package de.ibba.keepitup.logging;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import de.ibba.keepitup.test.mock.MockDump;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class DumpTest {

    private MockDump mockDump;

    @Before
    public void beforeEachTestMethod() {
        mockDump = new MockDump();
        Dump.initialize(mockDump);
    }

    @After
    public void afterEachTestMethod() {
        Dump.initialize(null);
    }

    @Test
    public void testDump() {
        IDumpSource dumpSource = ArrayList::new;
        Dump.dump(dumpSource);
        assertEquals(1, mockDump.numberDumpCalls());
        MockDump.DumpCall dumpCall = mockDump.getDumpCall(0);
        assertNull(dumpCall.getTag());
        assertNull(dumpCall.getMessage());
        assertSame(dumpSource, dumpCall.getDumpSource());
        Dump.dump("tag", "message", dumpSource);
        assertEquals(2, mockDump.numberDumpCalls());
        dumpCall = mockDump.getDumpCall(1);
        assertEquals("tag", dumpCall.getTag());
        assertEquals("message", dumpCall.getMessage());
        assertSame(dumpSource, dumpCall.getDumpSource());
    }
}
