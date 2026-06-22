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
    private NoBackupPreferenceManager noBackupPreferenceManager;
    private JSONSystemMigrate migrate;
    private HeaderDAO headerDAO;

    @Before
    public void beforeEachTestMethod() {
        Dump.initialize(null);
        preferenceManager = new PreferenceManager(TestRegistry.getContext());
        preferenceManager.removeAllPreferences();
        noBackupPreferenceManager = new NoBackupPreferenceManager(TestRegistry.getContext());
        noBackupPreferenceManager.removeAllPreferences();
        migrate = new JSONSystemMigrate(TestRegistry.getContext());
        headerDAO = new HeaderDAO(TestRegistry.getContext());
        headerDAO.deleteAllHeaders();
    }

    @After
    public void afterEachTestMethod() {
        preferenceManager.removeAllPreferences();
        noBackupPreferenceManager.removeAllPreferences();
        headerDAO.deleteAllHeaders();
    }

    @Test
    public void testDbVersionAdaptAfter0to3() throws Exception {
        assertEquals(3, preferenceManager.getPreferencePingCount());
        assertEquals(1, preferenceManager.getPreferenceConnectCount());
        JSONObject root = createRoot(5, 5, "");
        migrate.adaptAfter(root, 0, 3);
        assertEquals(5, preferenceManager.getPreferencePingCount());
        assertEquals(5, preferenceManager.getPreferenceConnectCount());
    }

    @Test
    public void testDbVersionAdaptAfter2to3() throws Exception {
        assertEquals(3, preferenceManager.getPreferencePingCount());
        assertEquals(1, preferenceManager.getPreferenceConnectCount());
        JSONObject root = createRoot(5, 5, "");
        migrate.adaptAfter(root, 2, 3);
        assertEquals(5, preferenceManager.getPreferencePingCount());
        assertEquals(5, preferenceManager.getPreferenceConnectCount());
    }

    @Test
    public void testDbVersionAdaptAfter3to3() throws Exception {
        assertEquals(3, preferenceManager.getPreferencePingCount());
        assertEquals(1, preferenceManager.getPreferenceConnectCount());
        JSONObject root = createRoot(5, 5, "");
        migrate.adaptAfter(root, 3, 3);
        assertEquals(3, preferenceManager.getPreferencePingCount());
        assertEquals(1, preferenceManager.getPreferenceConnectCount());
    }

    @Test
    public void testDbVersionAdaptAfter0to3ValuesInvalid() throws Exception {
        assertEquals(3, preferenceManager.getPreferencePingCount());
        assertEquals(1, preferenceManager.getPreferenceConnectCount());
        preferenceManager.setPreferencePingCount(5);
        preferenceManager.setPreferenceConnectCount(5);
        JSONObject root = createRoot(11, 11, "");
        migrate.adaptAfter(root, 0, 3);
        assertEquals(3, preferenceManager.getPreferencePingCount());
        assertEquals(1, preferenceManager.getPreferenceConnectCount());
    }

    @Test
    public void testDbVersionAdaptAfter0to3JSONInvalid() throws Exception {
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
    public void testDbVersionAdaptAfter3to6() throws Exception {
        JSONObject root = createRoot(1, 1, "MyHeader");
        migrate.adaptAfter(root, 3, 6);
        assertEquals(1, headerDAO.readGlobalHeaders().size());
        assertEquals(1, headerDAO.readAllHeaders().size());
        Header header = headerDAO.readGlobalHeaders().get(0);
        assertEquals("User-Agent", header.getName());
        assertEquals("MyHeader", header.getValue());
    }

    @Test
    public void testDbVersionAdaptAfter3to6JSONInvalid() throws Exception {
        JSONObject root = new JSONObject();
        JSONObject settings = new JSONObject();
        root.put("preferences", settings);
        migrate.adaptAfter(root, 3, 6);
        assertEquals(1, headerDAO.readGlobalHeaders().size());
        assertEquals(1, headerDAO.readAllHeaders().size());
        Header header = headerDAO.readGlobalHeaders().get(0);
        assertEquals("User-Agent", header.getName());
        assertEquals("Mozilla/5.0 (Linux; Android) KeepItUp/-", header.getValue());
    }

    @Test
    public void testDbVersionAdaptAfter0to6() throws Exception {
        assertEquals(3, preferenceManager.getPreferencePingCount());
        assertEquals(1, preferenceManager.getPreferenceConnectCount());
        JSONObject root = createRoot(5, 5, "MyHeader");
        migrate.adaptAfter(root, 0, 6);
        assertEquals(5, preferenceManager.getPreferencePingCount());
        assertEquals(5, preferenceManager.getPreferenceConnectCount());
        assertEquals(1, headerDAO.readGlobalHeaders().size());
        assertEquals(1, headerDAO.readAllHeaders().size());
        Header header = headerDAO.readGlobalHeaders().get(0);
        assertEquals("User-Agent", header.getName());
        assertEquals("MyHeader", header.getValue());
    }

    private JSONObject createRoot(int pingCount, int connectCount, String header) throws JSONException {
        JSONObject root = new JSONObject();
        JSONObject settings = new JSONObject();
        JSONObject globalSettings = new JSONObject();
        globalSettings.put("preferencePingCount", pingCount);
        globalSettings.put("preferenceConnectCount", connectCount);
        globalSettings.put("preferenceHTTPUserAgent", header);
        settings.put("global", globalSettings);
        root.put("preferences", settings);
        return root;
    }
}
