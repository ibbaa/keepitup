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
public class EncryptInfoTest {

    @Test
    public void testDefaultValues() {
        EncryptionInfo info = new EncryptionInfo();
        assertFalse(info.isEncrypt());
        assertNull(info.getPassword());
        PersistableBundle persistableBundle = info.toPersistableBundle();
        assertNotNull(persistableBundle);
        info = new EncryptionInfo(persistableBundle);
        assertFalse(info.isEncrypt());
        assertNull(info.getPassword());
        Bundle bundle = info.toBundle();
        assertNotNull(bundle);
        info = new EncryptionInfo(bundle);
        assertFalse(info.isEncrypt());
        assertNull(info.getPassword());
    }

    @Test
    public void testToBundleValues() {
        EncryptionInfo info = new EncryptionInfo();
        info.setEncrypt(true);
        info.setPassword("123");
        assertTrue(info.isEncrypt());
        assertEquals("123", info.getPassword());
        PersistableBundle persistableBundle = info.toPersistableBundle();
        assertNotNull(persistableBundle);
        info = new EncryptionInfo(persistableBundle);
        assertTrue(info.isEncrypt());
        assertEquals("123", info.getPassword());
        Bundle bundle = info.toBundle();
        assertNotNull(bundle);
        info = new EncryptionInfo(bundle);
        assertTrue(info.isEncrypt());
        assertEquals("123", info.getPassword());
    }

    @Test
    public void testIsEqual() {
        EncryptionInfo info1 = new EncryptionInfo();
        EncryptionInfo info2 = new EncryptionInfo();
        assertTrue(info1.isEqual(info2));
        info1.setEncrypt(true);
        assertFalse(info1.isEqual(info2));
        info2.setEncrypt(true);
        assertTrue(info1.isEqual(info2));
        info1.setPassword("123");
        assertFalse(info1.isEqual(info2));
        info2.setPassword("123");
        assertTrue(info1.isEqual(info2));
    }
}
