/*
 * Copyright (c) 2024. Alwin Ibba
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

package net.ibbaa.keepitup.ui;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.endsWith;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.resources.PreferenceManager;
import net.ibbaa.keepitup.test.mock.MockFolderPermissionManager;
import net.ibbaa.keepitup.ui.permission.NullFolderPermissionLauncher;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@MediumTest
@SuppressWarnings({"SameParameterValue"})
@RunWith(AndroidJUnit4.class)
public class SAFGlobalSettingsActivityTest extends BaseUITest {

    private MockFolderPermissionManager folderPermissionManager;

    @Before
    public void beforeEachTestMethod() {
        super.beforeEachTestMethod();
        folderPermissionManager = getMockFolderPermissionManager();
    }

    @Test
    public void testEnableLogToFile() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(getTimeBasedSuspensionScheduler());
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectFolderPermissionManager(folderPermissionManager);
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectLogFolderLauncher(new NullFolderPermissionLauncher(((GlobalSettingsActivity) getActivity(activityScenario))::grantArbitraryLogFolderPermission));
        PreferenceManager preferenceManager = getPreferenceManager();
        preferenceManager.setPreferenceAllowArbitraryFileLocation(true);
        onView(withId(R.id.switch_activity_global_settings_log_file)).perform(scrollTo());
        onView(withId(R.id.switch_activity_global_settings_log_file)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_log_file_on_off)).check(matches(withText("yes")));
        assertTrue(folderPermissionManager.hasPermission(getActivity(activityScenario), "/Documents"));
        onView(withId(R.id.textview_activity_global_settings_log_folder)).check(matches(withText(endsWith("/Documents"))));
        activityScenario.close();
    }

    @Test
    public void testLogToFileChangeFolder() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(getTimeBasedSuspensionScheduler());
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectFolderPermissionManager(folderPermissionManager);
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectLogFolderLauncher(new NullFolderPermissionLauncher(((GlobalSettingsActivity) getActivity(activityScenario))::grantArbitraryLogFolderPermission));
        PreferenceManager preferenceManager = getPreferenceManager();
        preferenceManager.setPreferenceAllowArbitraryFileLocation(true);
        onView(withId(R.id.switch_activity_global_settings_log_file)).perform(scrollTo());
        onView(withId(R.id.switch_activity_global_settings_log_file)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_log_file_on_off)).check(matches(withText("yes")));
        preferenceManager.setPreferenceArbitraryLogFolder("/Pictures");
        onView(withId(R.id.cardview_activity_global_settings_log_folder)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_global_settings_log_folder)).perform(click());
        assertTrue(folderPermissionManager.hasPermission(getActivity(activityScenario), "/Documents"));
        onView(withId(R.id.textview_activity_global_settings_log_folder)).check(matches(withText(endsWith("/Documents"))));
        assertFalse(folderPermissionManager.hasPermission(getActivity(activityScenario), "/Pictures"));
    }

    private MockFolderPermissionManager getMockFolderPermissionManager() {
        return new MockFolderPermissionManager();
    }
}
