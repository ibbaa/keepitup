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
public class SNMPAuthInfoTest {

    @Test
    public void testDefaultValues() {
        SNMPAuthInfo info = new SNMPAuthInfo();
        assertNull(info.getCommunity());
        assertNull(info.getAuthAlgorithm());
        assertNull(info.getUserName());
        assertNull(info.getAuthPassphrase());
        assertNull(info.getPrivAlgorithm());
        assertNull(info.getPrivPassphrase());
        PersistableBundle persistableBundle = info.toPersistableBundle();
        assertNotNull(persistableBundle);
        info = new SNMPAuthInfo(persistableBundle);
        assertNull(info.getCommunity());
        assertNull(info.getAuthAlgorithm());
        assertNull(info.getUserName());
        assertNull(info.getAuthPassphrase());
        assertNull(info.getPrivAlgorithm());
        assertNull(info.getPrivPassphrase());
        Bundle bundle = info.toBundle();
        assertNotNull(bundle);
        info = new SNMPAuthInfo(bundle);
        assertNull(info.getCommunity());
        assertNull(info.getAuthAlgorithm());
        assertNull(info.getUserName());
        assertNull(info.getAuthPassphrase());
        assertNull(info.getPrivAlgorithm());
        assertNull(info.getPrivPassphrase());
    }

    @Test
    public void testToBundleValues() {
        SNMPAuthInfo info = new SNMPAuthInfo();
        info.setCommunity("public");
        info.setAuthAlgorithm(SNMPAuthAlgorithm.SHA256);
        info.setUserName("user");
        info.setAuthPassphrase("authpass");
        info.setPrivAlgorithm(SNMPPrivAlgorithm.AES256);
        info.setPrivPassphrase("privpass");
        assertEquals("public", info.getCommunity());
        assertEquals(SNMPAuthAlgorithm.SHA256, info.getAuthAlgorithm());
        assertEquals("user", info.getUserName());
        assertEquals("authpass", info.getAuthPassphrase());
        assertEquals(SNMPPrivAlgorithm.AES256, info.getPrivAlgorithm());
        assertEquals("privpass", info.getPrivPassphrase());
        PersistableBundle persistableBundle = info.toPersistableBundle();
        assertNotNull(persistableBundle);
        info = new SNMPAuthInfo(persistableBundle);
        assertEquals("public", info.getCommunity());
        assertEquals(SNMPAuthAlgorithm.SHA256, info.getAuthAlgorithm());
        assertEquals("user", info.getUserName());
        assertEquals("authpass", info.getAuthPassphrase());
        assertEquals(SNMPPrivAlgorithm.AES256, info.getPrivAlgorithm());
        assertEquals("privpass", info.getPrivPassphrase());
        Bundle bundle = info.toBundle();
        assertNotNull(bundle);
        info = new SNMPAuthInfo(bundle);
        assertEquals("public", info.getCommunity());
        assertEquals(SNMPAuthAlgorithm.SHA256, info.getAuthAlgorithm());
        assertEquals("user", info.getUserName());
        assertEquals("authpass", info.getAuthPassphrase());
        assertEquals(SNMPPrivAlgorithm.AES256, info.getPrivAlgorithm());
        assertEquals("privpass", info.getPrivPassphrase());
    }

    @Test
    public void testIsEqual() {
        SNMPAuthInfo info1 = new SNMPAuthInfo();
        SNMPAuthInfo info2 = new SNMPAuthInfo();
        assertTrue(info1.isEqual(info2));
        info1.setCommunity("public");
        assertFalse(info1.isEqual(info2));
        info2.setCommunity("public");
        assertTrue(info1.isEqual(info2));
        info1.setAuthAlgorithm(SNMPAuthAlgorithm.MD5);
        assertFalse(info1.isEqual(info2));
        info2.setAuthAlgorithm(SNMPAuthAlgorithm.MD5);
        assertTrue(info1.isEqual(info2));
        info1.setUserName("user");
        assertFalse(info1.isEqual(info2));
        info2.setUserName("user");
        assertTrue(info1.isEqual(info2));
        info1.setAuthPassphrase("authpass");
        assertFalse(info1.isEqual(info2));
        info2.setAuthPassphrase("authpass");
        assertTrue(info1.isEqual(info2));
        info1.setPrivAlgorithm(SNMPPrivAlgorithm.DES);
        assertFalse(info1.isEqual(info2));
        info2.setPrivAlgorithm(SNMPPrivAlgorithm.DES);
        assertTrue(info1.isEqual(info2));
        info1.setPrivPassphrase("privpass");
        assertFalse(info1.isEqual(info2));
        info2.setPrivPassphrase("privpass");
        assertTrue(info1.isEqual(info2));
    }
}
