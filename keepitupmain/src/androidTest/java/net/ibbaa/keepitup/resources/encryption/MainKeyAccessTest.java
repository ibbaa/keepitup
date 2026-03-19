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

package net.ibbaa.keepitup.resources.encryption;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.test.mock.TestRegistry;
import net.ibbaa.keepitup.util.StringUtil;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class MainKeyAccessTest {

    @Before
    public void beforeEachTestMethod() {
        MainKeyAccess mainKeyAccess = new MainKeyAccess(TestRegistry.getContext());
        mainKeyAccess.reset();
    }

    @Test
    public void testGetMainKey() {
        MainKeyAccess mainKeyAccess = new MainKeyAccess(TestRegistry.getContext());
        MainKeyAccess.MainKey mainKey1 = mainKeyAccess.getMainKey();
        byte[] mainKey1Key = mainKey1.key();
        assertTrue(mainKey1.keyValid());
        assertTrue(mainKey1.keyNew());
        assertEquals("New key valid", mainKey1.message());
        MainKeyAccess.MainKey mainKey2 = mainKeyAccess.getMainKey();
        byte[] mainKey2Key = mainKey2.key();
        assertTrue(mainKey2.keyValid());
        assertFalse(mainKey2.keyNew());
        assertEquals("Existing key valid", mainKey2.message());
        assertArrayEquals(mainKey1Key, mainKey2Key);
    }

    @Test
    public void testGetMainKeyFailureCreateNew() {
        MainKeyAccess mainKeyAccess = new MainKeyAccess(TestRegistry.getContext());
        MainKeyAccess.MainKey mainKey1 = mainKeyAccess.getMainKey();
        byte[] mainKey1Key = mainKey1.key();
        assertTrue(mainKey1.keyValid());
        assertTrue(mainKey1.keyNew());
        assertEquals("New key valid", mainKey1.message());
        corruptKey();
        MainKeyAccess.MainKey mainKey2 = mainKeyAccess.getMainKey();
        byte[] mainKey2Key = mainKey2.key();
        assertTrue(mainKey2.keyValid());
        assertTrue(mainKey2.keyNew());
        assertEquals("New key valid", mainKey2.message());
        assertFalse(Arrays.equals(mainKey1Key, mainKey2Key));
        MainKeyAccess.MainKey mainKey3 = mainKeyAccess.getMainKey();
        byte[] mainKey3Key = mainKey3.key();
        assertTrue(mainKey3.keyValid());
        assertFalse(mainKey3.keyNew());
        assertEquals("Existing key valid", mainKey3.message());
        assertArrayEquals(mainKey2Key, mainKey3Key);
    }

    private void corruptKey() {
        String main_key_prefs_file = TestRegistry.getContext().getResources().getString(R.string.main_key_prefs_file);
        String mainKeyPrefsKey = TestRegistry.getContext().getResources().getString(R.string.main_key_prefs_key);
        SharedPreferences.Editor mainKeyPreferences = TestRegistry.getContext().getSharedPreferences(main_key_prefs_file, Context.MODE_PRIVATE).edit();
        mainKeyPreferences.putString(mainKeyPrefsKey, StringUtil.byteArrayToBase64(new byte[32]));
        mainKeyPreferences.commit();
    }
}
