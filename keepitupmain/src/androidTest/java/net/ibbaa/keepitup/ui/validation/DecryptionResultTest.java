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
public class DecryptionResultTest {

    @Test
    public void testToBundleNullValues() {
        DecryptionResult result = new DecryptionResult(null, null);
        assertNull(result.getNetworkTask());
        assertNull(result.getMessage());
        PersistableBundle persistableBundle = result.toPersistableBundle();
        assertNotNull(persistableBundle);
        result = new DecryptionResult(persistableBundle);
        assertNull(result.getNetworkTask());
        assertNull(result.getMessage());
        Bundle bundle = result.toBundle();
        assertNotNull(bundle);
        result = new DecryptionResult(bundle);
        assertNull(result.getNetworkTask());
        assertNull(result.getMessage());
    }

    @Test
    public void testToBundle() {
        DecryptionResult result = new DecryptionResult("testtask", "testmessage");
        assertEquals("testtask", result.getNetworkTask());
        assertEquals("testmessage", result.getMessage());
        PersistableBundle persistableBundle = result.toPersistableBundle();
        assertNotNull(persistableBundle);
        result = new DecryptionResult(persistableBundle);
        assertEquals("testtask", result.getNetworkTask());
        assertEquals("testmessage", result.getMessage());
        Bundle bundle = result.toBundle();
        assertNotNull(bundle);
        result = new DecryptionResult(bundle);
        assertEquals("testtask", result.getNetworkTask());
        assertEquals("testmessage", result.getMessage());
    }

    @Test
    public void testIsEqual() {
        DecryptionResult result = new DecryptionResult("testtask", "testmessage");
        assertTrue(result.isEqual(result));
        assertFalse(result.isEqual(null));
        DecryptionResult otherResult = new DecryptionResult("testtask", "testmessage") {
        };
        assertFalse(result.isEqual(otherResult));
        otherResult = new DecryptionResult("testtask", "testmessage");
        assertTrue(result.isEqual(otherResult));
        otherResult = new DecryptionResult("testtask2", "testmessage");
        assertFalse(result.isEqual(otherResult));
        otherResult = new DecryptionResult(null, "testmessage");
        assertFalse(result.isEqual(otherResult));
        otherResult = new DecryptionResult("1", "testmessage");
        assertFalse(result.isEqual(otherResult));
        otherResult = new DecryptionResult("1", "testmessage1");
        assertFalse(result.isEqual(otherResult));
        otherResult = new DecryptionResult("1", null);
        assertFalse(result.isEqual(otherResult));
        otherResult = new DecryptionResult("", "");
        assertFalse(result.isEqual(otherResult));
    }
}
