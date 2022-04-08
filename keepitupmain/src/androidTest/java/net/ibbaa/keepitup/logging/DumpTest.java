/*
 * Copyright (c) 2022. Alwin Ibba
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.ibbaa.keepitup.logging;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import net.ibbaa.keepitup.test.mock.MockDump;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

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
        assertNull(dumpCall.getBaseFileName());
        assertSame(dumpSource, dumpCall.getDumpSource());
        Dump.dump("tag", "message", dumpSource);
        assertEquals(2, mockDump.numberDumpCalls());
        dumpCall = mockDump.getDumpCall(1);
        assertEquals("tag", dumpCall.getTag());
        assertEquals("message", dumpCall.getMessage());
        assertNull(dumpCall.getBaseFileName());
        assertSame(dumpSource, dumpCall.getDumpSource());
        Dump.dump("tag", "message", "file", dumpSource);
        assertEquals(3, mockDump.numberDumpCalls());
        dumpCall = mockDump.getDumpCall(2);
        assertEquals("tag", dumpCall.getTag());
        assertEquals("message", dumpCall.getMessage());
        assertEquals("file", dumpCall.getBaseFileName());
        assertSame(dumpSource, dumpCall.getDumpSource());
    }
}
