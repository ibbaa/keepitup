/*
 * Copyright (c) 2026 Alwin Ibba
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

package net.ibbaa.keepitup.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import org.junit.Test;
import org.junit.runner.RunWith;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class SNMPInterfaceInfoTest {

    @Test
    public void testDefaultValues() {
        SNMPInterfaceInfo info = new SNMPInterfaceInfo();
        assertNull(info.getDescr());
        assertEquals(-1, info.getType());
        assertEquals(-1, info.getStatus());
        assertNull(info.getAlias());
        PersistableBundle persistableBundle = info.toPersistableBundle();
        assertNotNull(persistableBundle);
        Bundle bundle = info.toBundle();
        assertNotNull(bundle);
        info = new SNMPInterfaceInfo(bundle);
        assertNull(info.getDescr());
        assertEquals(-1, info.getType());
        assertEquals(-1, info.getStatus());
        assertNull(info.getAlias());
    }

    @Test
    public void testToBundleValues() {
        SNMPInterfaceInfo info = new SNMPInterfaceInfo();
        info.setDescr("eth0");
        info.setType(6);
        info.setStatus(1);
        info.setAlias("LAN");
        assertEquals("eth0", info.getDescr());
        assertEquals(6, info.getType());
        assertEquals(1, info.getStatus());
        assertEquals("LAN", info.getAlias());
        PersistableBundle persistableBundle = info.toPersistableBundle();
        assertNotNull(persistableBundle);
        Bundle bundle = info.toBundle();
        assertNotNull(bundle);
        info = new SNMPInterfaceInfo(bundle);
        assertEquals("eth0", info.getDescr());
        assertEquals(6, info.getType());
        assertEquals(1, info.getStatus());
        assertEquals("LAN", info.getAlias());
    }

    @Test
    public void testIsEqual() {
        SNMPInterfaceInfo info1 = new SNMPInterfaceInfo();
        SNMPInterfaceInfo info2 = new SNMPInterfaceInfo();
        assertTrue(info1.isEqual(info2));
        info1.setDescr("eth0");
        assertFalse(info1.isEqual(info2));
        info2.setDescr("eth0");
        assertTrue(info1.isEqual(info2));
        info1.setType(6);
        assertFalse(info1.isEqual(info2));
        info2.setType(6);
        assertTrue(info1.isEqual(info2));
        info1.setStatus(1);
        assertFalse(info1.isEqual(info2));
        info2.setStatus(1);
        assertTrue(info1.isEqual(info2));
        info1.setAlias("LAN");
        assertFalse(info1.isEqual(info2));
        info2.setAlias("LAN");
        assertTrue(info1.isEqual(info2));
        assertFalse(info1.isEqual(null));
    }
}
