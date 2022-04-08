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

package net.ibbaa.keepitup.ui.clipboard;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class);
        clipboardManager = new SystemClipboardManager(TestRegistry.getContext());
    }

    @After
    public void afterEachTestMethod() {
        super.afterEachTestMethod();
        activityScenario.close();
    }

    @Test
    public void testHasNumericIntegerData() {
        clipboardManager.putData("Test");
        assertFalse(clipboardManager.hasNumericIntegerData());
        clipboardManager.putData("11");
        assertTrue(clipboardManager.hasNumericIntegerData());
    }

    @Test
    public void testPutAndGetData() {
        clipboardManager.putData("Test");
        assertTrue(clipboardManager.hasData());
        assertEquals("Test", clipboardManager.getData());
        clipboardManager.putData("123");
        assertTrue(clipboardManager.hasData());
        assertEquals("123", clipboardManager.getData());
    }
}
