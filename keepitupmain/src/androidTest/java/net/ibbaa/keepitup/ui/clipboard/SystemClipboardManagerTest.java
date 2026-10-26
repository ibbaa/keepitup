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

package net.ibbaa.keepitup.ui.clipboard;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import net.ibbaa.keepitup.test.mock.TestRegistry;
import net.ibbaa.keepitup.ui.BaseUITest;
import net.ibbaa.keepitup.ui.NetworkTaskMainActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class SystemClipboardManagerTest extends BaseUITest {

    private ActivityScenario<?> activityScenario;
    private IClipboardManager clipboardManager;

    @Before
    public void beforeEachTestMethod() {
        super.beforeEachTestMethod();
        getNoBackupPreferenceManager().setPreferenceAskedNotificationPermission(true);
        getNoBackupPreferenceManager().setPreferenceAskedLocalNetworkPermission(true);
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class);
        waitUntilWindowFocused(activityScenario);
        clipboardManager = new SystemClipboardManager(TestRegistry.getContext());
    }

    @After
    public void afterEachTestMethod() {
        super.afterEachTestMethod();
        activityScenario.close();
    }

    @Test
    public void testHasNumericIntegerData() {
        putDataAndWait("Test");
        assertFalse(clipboardManager.hasNumericIntegerData());
        putDataAndWait("11");
        assertTrue(clipboardManager.hasNumericIntegerData());
    }

    @Test
    public void testPutAndGetData() {
        putDataAndWait("Test");
        assertTrue(clipboardManager.hasData());
        assertEquals("Test", clipboardManager.getData());
        putDataAndWait("123");
        assertTrue(clipboardManager.hasData());
        assertEquals("123", clipboardManager.getData());
    }

    private void putDataAndWait(String data) {
        int waits = 10;
        while (waits > 0) {
            clipboardManager.putData(data);
            onView(isRoot()).perform(waitFor(500));
            if (data.equals(clipboardManager.getData())) {
                return;
            }
            waits--;
        }
        fail("Clipboard data " + data + " was overwritten");
    }
}
