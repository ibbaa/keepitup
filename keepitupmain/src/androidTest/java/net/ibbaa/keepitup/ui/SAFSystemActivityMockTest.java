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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.test.mock.DelegatingTestFolderPermissionLauncher;
import net.ibbaa.keepitup.test.mock.MockFolderPermissionManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@MediumTest
@SuppressWarnings({"SameParameterValue"})
@RunWith(AndroidJUnit4.class)
public class SAFSystemActivityMockTest extends BaseUITest {

    private MockFolderPermissionManager folderPermissionManager;

    @Before
    public void beforeEachTestMethod() {
        super.beforeEachTestMethod();
        folderPermissionManager = getMockFolderPermissionManager();
    }

    @Test
    public void testAllowArbitraryFileLocation() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(SystemActivity.class, getBypassSystemSAFBundle());
        injectMocks(activityScenario);
        injectArbitraryFolderLauncher(activityScenario, "/Test");
        folderPermissionManager.setGrantedFolder("/Test");
        onView(withId(R.id.switch_activity_system_allow_arbitrary_file_location)).perform(scrollTo());
        onView(withId(R.id.switch_activity_system_allow_arbitrary_file_location)).perform(click());
        onView(withId(R.id.textview_activity_system_allow_arbitrary_file_location_on_off)).check(matches(withText("yes")));
        assertTrue(folderPermissionManager.hasPersistentPermission(getActivity(activityScenario), "/Test"));
        assertFalse(folderPermissionManager.hasPersistentPermission(getActivity(activityScenario), "/Documents"));
        onView(withId(R.id.switch_activity_system_allow_arbitrary_file_location)).perform(scrollTo());
        onView(withId(R.id.switch_activity_system_allow_arbitrary_file_location)).perform(click());
        onView(withId(R.id.textview_activity_system_allow_arbitrary_file_location_on_off)).check(matches(withText("no")));
        assertTrue(folderPermissionManager.hasPersistentPermission(getActivity(activityScenario), "/Test"));
        assertFalse(folderPermissionManager.hasPersistentPermission(getActivity(activityScenario), "/Documents"));
        onView(withId(R.id.switch_activity_system_allow_arbitrary_file_location)).perform(scrollTo());
        onView(withId(R.id.switch_activity_system_allow_arbitrary_file_location)).perform(click());
        onView(withId(R.id.textview_activity_system_allow_arbitrary_file_location_on_off)).check(matches(withText("yes")));
        assertTrue(folderPermissionManager.hasPersistentPermission(getActivity(activityScenario), "/Test"));
        assertFalse(folderPermissionManager.hasPersistentPermission(getActivity(activityScenario), "/Documents"));
        activityScenario.close();
    }

    @Test
    public void testAllowArbitraryFileLocationPermissionPresent() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(SystemActivity.class, getBypassSystemSAFBundle());
        injectMocks(activityScenario);
        folderPermissionManager.setGrantedFolder("/Movies");
        folderPermissionManager.requestPersistentFolderPermission(null, null, "/Movies");
        injectArbitraryFolderLauncher(activityScenario, "/Test");
        folderPermissionManager.setGrantedFolder("/Test");
        onView(withId(R.id.switch_activity_system_allow_arbitrary_file_location)).perform(scrollTo());
        onView(withId(R.id.switch_activity_system_allow_arbitrary_file_location)).perform(click());
        onView(withId(R.id.textview_activity_system_allow_arbitrary_file_location_on_off)).check(matches(withText("yes")));
        assertTrue(folderPermissionManager.hasPersistentPermission(getActivity(activityScenario), "/Movies"));
        assertFalse(folderPermissionManager.hasPersistentPermission(getActivity(activityScenario), "/Test"));
        assertFalse(folderPermissionManager.hasPersistentPermission(getActivity(activityScenario), "/Documents"));
        onView(withId(R.id.switch_activity_system_allow_arbitrary_file_location)).perform(scrollTo());
        onView(withId(R.id.switch_activity_system_allow_arbitrary_file_location)).perform(click());
        onView(withId(R.id.textview_activity_system_allow_arbitrary_file_location_on_off)).check(matches(withText("no")));
        assertTrue(folderPermissionManager.hasPersistentPermission(getActivity(activityScenario), "/Movies"));
        assertFalse(folderPermissionManager.hasPersistentPermission(getActivity(activityScenario), "/Test"));
        assertFalse(folderPermissionManager.hasPersistentPermission(getActivity(activityScenario), "/Documents"));
        onView(withId(R.id.switch_activity_system_allow_arbitrary_file_location)).perform(scrollTo());
        onView(withId(R.id.switch_activity_system_allow_arbitrary_file_location)).perform(click());
        onView(withId(R.id.textview_activity_system_allow_arbitrary_file_location_on_off)).check(matches(withText("yes")));
        assertTrue(folderPermissionManager.hasPersistentPermission(getActivity(activityScenario), "/Movies"));
        assertFalse(folderPermissionManager.hasPersistentPermission(getActivity(activityScenario), "/Test"));
        assertFalse(folderPermissionManager.hasPersistentPermission(getActivity(activityScenario), "/Documents"));
        activityScenario.close();
    }

    private void injectMocks(ActivityScenario<?> activityScenario) {
        ((SystemActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(getTimeBasedSuspensionScheduler());
        ((SystemActivity) getActivity(activityScenario)).injectFolderPermissionManager(folderPermissionManager);
        ((SystemActivity) getActivity(activityScenario)).injectArbitraryFolderLauncher(new DelegatingTestFolderPermissionLauncher(((SystemActivity) getActivity(activityScenario))::grantArbitraryFolderPermissions));
    }

    private void injectArbitraryFolderLauncher(ActivityScenario<?> activityScenario, String uri) {
        ((SystemActivity) getActivity(activityScenario)).injectArbitraryFolderLauncher(new DelegatingTestFolderPermissionLauncher(((SystemActivity) getActivity(activityScenario))::grantArbitraryFolderPermissions, uri));
    }

    private MockFolderPermissionManager getMockFolderPermissionManager() {
        return new MockFolderPermissionManager();
    }
}
