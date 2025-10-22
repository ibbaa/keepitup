/*
 * Copyright (c) 2025 Alwin Ibba
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

package net.ibbaa.keepitup.resources;

import static org.junit.Assert.assertEquals;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import net.ibbaa.keepitup.db.HeaderDAO;
import net.ibbaa.keepitup.logging.Dump;
import net.ibbaa.keepitup.model.Header;
import net.ibbaa.keepitup.test.mock.TestRegistry;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@MediumTest
@SuppressWarnings({"SequencedCollectionMethodCanBeUsed"})
@RunWith(AndroidJUnit4.class)
public class JSONSystemMigrateTest {

    private PreferenceManager preferenceManager;
    private JSONSystemMigrate migrate;
    private HeaderDAO headerDAO;

    @Before
    public void beforeEachTestMethod() {
        Dump.initialize(null);
        preferenceManager = new PreferenceManager(TestRegistry.getContext());
        preferenceManager.removeAllPreferences();
        migrate = new JSONSystemMigrate(TestRegistry.getContext());
        headerDAO = new HeaderDAO(TestRegistry.getContext());
        headerDAO.deleteAllHeaders();
    }

    @After
    public void afterEachTestMethod() {
        preferenceManager.removeAllPreferences();
        headerDAO.deleteAllHeaders();
    }

    @Test
    public void testVersionAdaptAfter0to3() throws Exception {
        assertEquals(3, preferenceManager.getPreferencePingCount());
        assertEquals(1, preferenceManager.getPreferenceConnectCount());
        JSONObject root = createRoot(5, 5);
        migrate.adaptAfter(root, 0, 3);
        assertEquals(5, preferenceManager.getPreferencePingCount());
        assertEquals(5, preferenceManager.getPreferenceConnectCount());
    }

    @Test
    public void testVersionAdaptAfter2to3() throws Exception {
        assertEquals(3, preferenceManager.getPreferencePingCount());
        assertEquals(1, preferenceManager.getPreferenceConnectCount());
        JSONObject root = createRoot(5, 5);
        migrate.adaptAfter(root, 2, 3);
        assertEquals(5, preferenceManager.getPreferencePingCount());
        assertEquals(5, preferenceManager.getPreferenceConnectCount());
    }

    @Test
    public void testVersionAdaptAfter3to3() throws Exception {
        assertEquals(3, preferenceManager.getPreferencePingCount());
        assertEquals(1, preferenceManager.getPreferenceConnectCount());
        JSONObject root = createRoot(5, 5);
        migrate.adaptAfter(root, 3, 3);
        assertEquals(3, preferenceManager.getPreferencePingCount());
        assertEquals(1, preferenceManager.getPreferenceConnectCount());
    }

    @Test
    public void testVersionAdaptAfter0to3ValuesInvalid() throws Exception {
        assertEquals(3, preferenceManager.getPreferencePingCount());
        assertEquals(1, preferenceManager.getPreferenceConnectCount());
        preferenceManager.setPreferencePingCount(5);
        preferenceManager.setPreferenceConnectCount(5);
        JSONObject root = createRoot(11, 11);
        migrate.adaptAfter(root, 0, 3);
        assertEquals(3, preferenceManager.getPreferencePingCount());
        assertEquals(1, preferenceManager.getPreferenceConnectCount());
    }

    @Test
    public void testVersionAdaptAfter0to3JSONInvalid() throws Exception {
        JSONObject root = new JSONObject();
        JSONObject settings = new JSONObject();
        root.put("preferences", settings);
        assertEquals(3, preferenceManager.getPreferencePingCount());
        assertEquals(1, preferenceManager.getPreferenceConnectCount());
        preferenceManager.setPreferencePingCount(5);
        preferenceManager.setPreferenceConnectCount(5);
        migrate.adaptAfter(root, 0, 3);
        assertEquals(5, preferenceManager.getPreferencePingCount());
        assertEquals(5, preferenceManager.getPreferenceConnectCount());
    }

    @Test
    public void testVersionAdaptAfter3to6() {
        preferenceManager.setPreferenceHTTPUserAgent("test");
        migrate.adaptAfter(new JSONObject(), 3, 6);
        assertEquals(1, headerDAO.readGlobalHeaders().size());
        assertEquals(1, headerDAO.readAllHeaders().size());
        Header header = headerDAO.readGlobalHeaders().get(0);
        assertEquals("User-Agent", header.getName());
        assertEquals("test", header.getValue());
    }

    @Test
    public void testVersionAdaptAfter0to6() throws Exception {
        assertEquals(3, preferenceManager.getPreferencePingCount());
        assertEquals(1, preferenceManager.getPreferenceConnectCount());
        JSONObject root = createRoot(5, 5);
        migrate.adaptAfter(root, 0, 6);
        assertEquals(5, preferenceManager.getPreferencePingCount());
        assertEquals(5, preferenceManager.getPreferenceConnectCount());
        assertEquals(1, headerDAO.readGlobalHeaders().size());
        assertEquals(1, headerDAO.readAllHeaders().size());
        Header header = headerDAO.readGlobalHeaders().get(0);
        assertEquals("User-Agent", header.getName());
        assertEquals("Mozilla/5.0", header.getValue());
    }

    private JSONObject createRoot(int pingCount, int connectCount) throws JSONException {
        JSONObject root = new JSONObject();
        JSONObject settings = new JSONObject();
        JSONObject globalSettings = new JSONObject();
        globalSettings.put("preferencePingCount", pingCount);
        globalSettings.put("preferenceConnectCount", connectCount);
        settings.put("global", globalSettings);
        root.put("preferences", settings);
        return root;
    }
}
