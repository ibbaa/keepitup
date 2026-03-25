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

package net.ibbaa.keepitup.ui.validation;

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
public class CredentialInfoTest {

    @Test
    public void testToBundleNullValues() {
        CredentialInfo info = new CredentialInfo(null, null);
        assertNull(info.getName());
        assertNull(info.getMessage());
        PersistableBundle persistableBundle = info.toPersistableBundle();
        assertNotNull(persistableBundle);
        info = new CredentialInfo(persistableBundle);
        assertNull(info.getName());
        assertNull(info.getMessage());
        Bundle bundle = info.toBundle();
        assertNotNull(bundle);
        info = new CredentialInfo(bundle);
        assertNull(info.getName());
        assertNull(info.getMessage());
    }

    @Test
    public void testToBundle() {
        CredentialInfo info = new CredentialInfo("testtask", "testmessage");
        assertEquals("testtask", info.getName());
        assertEquals("testmessage", info.getMessage());
        PersistableBundle persistableBundle = info.toPersistableBundle();
        assertNotNull(persistableBundle);
        info = new CredentialInfo(persistableBundle);
        assertEquals("testtask", info.getName());
        assertEquals("testmessage", info.getMessage());
        Bundle bundle = info.toBundle();
        assertNotNull(bundle);
        info = new CredentialInfo(bundle);
        assertEquals("testtask", info.getName());
        assertEquals("testmessage", info.getMessage());
    }

    @Test
    public void testIsEqual() {
        CredentialInfo info = new CredentialInfo("testtask", "testmessage");
        assertTrue(info.isEqual(info));
        assertFalse(info.isEqual(null));
        CredentialInfo otherResult = new CredentialInfo("testtask", "testmessage") {
        };
        assertFalse(info.isEqual(otherResult));
        otherResult = new CredentialInfo("testtask", "testmessage");
        assertTrue(info.isEqual(otherResult));
        otherResult = new CredentialInfo("testtask2", "testmessage");
        assertFalse(info.isEqual(otherResult));
        otherResult = new CredentialInfo(null, "testmessage");
        assertFalse(info.isEqual(otherResult));
        otherResult = new CredentialInfo("1", "testmessage");
        assertFalse(info.isEqual(otherResult));
        otherResult = new CredentialInfo("1", "testmessage1");
        assertFalse(info.isEqual(otherResult));
        otherResult = new CredentialInfo("1", null);
        assertFalse(info.isEqual(otherResult));
        otherResult = new CredentialInfo("", "");
        assertFalse(info.isEqual(otherResult));
    }
}
