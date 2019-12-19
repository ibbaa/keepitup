package de.ibba.keepitup.logging;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.ibba.keepitup.test.mock.MockDump;

import static org.junit.Assert.assertEquals;

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
        Dump.dump(null);
        assertEquals(1, mockDump.numberDumpCalls());
        Dump.dump(null);
        assertEquals(2, mockDump.numberDumpCalls());
    }
}
