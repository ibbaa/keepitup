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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.resources.PreferenceManager;
import net.ibbaa.keepitup.test.mock.DelegatingTestPermissionLauncher;
import net.ibbaa.keepitup.test.mock.MockStoragePermissionManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@MediumTest
@SuppressWarnings({"SameParameterValue"})
@RunWith(AndroidJUnit4.class)
public class SAFGlobalSettingsActivityMockTest extends BaseUITest {

    private MockStoragePermissionManager storagePermissionManager;

    @Before
    public void beforeEachTestMethod() {
        super.beforeEachTestMethod();
        storagePermissionManager = getMockStoragePermissionManager();
    }

    @Test
    public void testEnableLogToFile() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        injectMocks(activityScenario);
        PreferenceManager preferenceManager = getPreferenceManager();
        preferenceManager.setPreferenceAllowArbitraryFileLocation(true);
        storagePermissionManager.setGrantedFolder("/Documents");
        onView(withId(R.id.switch_activity_global_settings_log_file)).perform(scrollTo());
        onView(withId(R.id.switch_activity_global_settings_log_file)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_log_file_on_off)).check(matches(withText("yes")));
        assertTrue(storagePermissionManager.hasPersistentPermission(getActivity(activityScenario), "/Documents"));
        onView(withId(R.id.textview_activity_global_settings_log_folder)).check(matches(withText(endsWith("/Documents"))));
        activityScenario.close();
    }

    @Test
    public void testEnableLogToFileChangeFolder() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        injectMocks(activityScenario);
        PreferenceManager preferenceManager = getPreferenceManager();
        preferenceManager.setPreferenceAllowArbitraryFileLocation(true);
        injectLogFolderLauncher(activityScenario, "/Pictures");
        storagePermissionManager.setGrantedFolder("/Pictures");
        onView(withId(R.id.switch_activity_global_settings_log_file)).perform(scrollTo());
        onView(withId(R.id.switch_activity_global_settings_log_file)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_log_file_on_off)).check(matches(withText("yes")));
        assertTrue(storagePermissionManager.hasPersistentPermission(getActivity(activityScenario), "/Pictures"));
        onView(withId(R.id.textview_activity_global_settings_log_folder)).check(matches(withText(endsWith("/Pictures"))));
        assertEquals("/Pictures", preferenceManager.getPreferenceArbitraryLogFolder());
        assertFalse(storagePermissionManager.hasPersistentPermission(getActivity(activityScenario), "/Documents"));
        activityScenario.close();
    }

    @Test
    public void testLogToFileChangeFolder() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        injectMocks(activityScenario);
        PreferenceManager preferenceManager = getPreferenceManager();
        preferenceManager.setPreferenceAllowArbitraryFileLocation(true);
        onView(withId(R.id.switch_activity_global_settings_log_file)).perform(scrollTo());
        onView(withId(R.id.switch_activity_global_settings_log_file)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_log_file_on_off)).check(matches(withText("yes")));
        injectLogFolderLauncher(activityScenario, "/Pictures");
        storagePermissionManager.setGrantedFolder("/Pictures");
        onView(withId(R.id.cardview_activity_global_settings_log_folder)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_global_settings_log_folder)).perform(click());
        assertTrue(storagePermissionManager.hasPersistentPermission(getActivity(activityScenario), "/Pictures"));
        onView(withId(R.id.textview_activity_global_settings_log_folder)).check(matches(withText(endsWith("/Pictures"))));
        assertEquals("/Pictures", preferenceManager.getPreferenceArbitraryLogFolder());
        assertTrue(storagePermissionManager.hasPersistentPermission(getActivity(activityScenario), "/Documents"));
    }

    @Test
    public void testEnableDownloadExternalStorage() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        injectMocks(activityScenario);
        PreferenceManager preferenceManager = getPreferenceManager();
        preferenceManager.setPreferenceAllowArbitraryFileLocation(true);
        storagePermissionManager.setGrantedFolder("/Documents");
        onView(withId(R.id.switch_activity_global_settings_download_external_storage)).perform(scrollTo());
        onView(withId(R.id.switch_activity_global_settings_download_external_storage)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_download_external_storage_on_off)).check(matches(withText("yes")));
        assertTrue(storagePermissionManager.hasPersistentPermission(getActivity(activityScenario), "/Documents"));
        onView(withId(R.id.textview_activity_global_settings_download_folder)).check(matches(withText(endsWith("/Documents"))));
        activityScenario.close();
    }

    @Test
    public void testEnableDownloadExternalStorageChangeFolder() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        injectMocks(activityScenario);
        PreferenceManager preferenceManager = getPreferenceManager();
        preferenceManager.setPreferenceAllowArbitraryFileLocation(true);
        injectDownloadFolderLauncher(activityScenario, "/Pictures");
        storagePermissionManager.setGrantedFolder("/Pictures");
        onView(withId(R.id.switch_activity_global_settings_download_external_storage)).perform(scrollTo());
        onView(withId(R.id.switch_activity_global_settings_download_external_storage)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_download_external_storage_on_off)).check(matches(withText("yes")));
        assertTrue(storagePermissionManager.hasPersistentPermission(getActivity(activityScenario), "/Pictures"));
        onView(withId(R.id.textview_activity_global_settings_download_folder)).check(matches(withText(endsWith("/Pictures"))));
        assertEquals("/Pictures", preferenceManager.getPreferenceArbitraryDownloadFolder());
        assertFalse(storagePermissionManager.hasPersistentPermission(getActivity(activityScenario), "/Documents"));
        activityScenario.close();
    }

    @Test
    public void testDownloadExternalStorageChangeFolder() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        injectMocks(activityScenario);
        PreferenceManager preferenceManager = getPreferenceManager();
        preferenceManager.setPreferenceAllowArbitraryFileLocation(true);
        onView(withId(R.id.switch_activity_global_settings_download_external_storage)).perform(scrollTo());
        onView(withId(R.id.switch_activity_global_settings_download_external_storage)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_download_external_storage_on_off)).check(matches(withText("yes")));
        injectDownloadFolderLauncher(activityScenario, "/Pictures");
        storagePermissionManager.setGrantedFolder("/Pictures");
        onView(withId(R.id.cardview_activity_global_settings_download_folder)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_global_settings_download_folder)).perform(click());
        assertTrue(storagePermissionManager.hasPersistentPermission(getActivity(activityScenario), "/Pictures"));
        onView(withId(R.id.textview_activity_global_settings_download_folder)).check(matches(withText(endsWith("/Pictures"))));
        assertEquals("/Pictures", preferenceManager.getPreferenceArbitraryDownloadFolder());
        assertTrue(storagePermissionManager.hasPersistentPermission(getActivity(activityScenario), "/Documents"));
    }

    @Test
    public void testRevokePermissions() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        injectMocks(activityScenario);
        PreferenceManager preferenceManager = getPreferenceManager();
        preferenceManager.setPreferenceAllowArbitraryFileLocation(true);
        preferenceManager.setPreferenceLastArbitraryExportFile("/Export");
        storagePermissionManager.setGrantedFolder("/Documents");
        onView(withId(R.id.switch_activity_global_settings_log_file)).perform(scrollTo());
        onView(withId(R.id.switch_activity_global_settings_log_file)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_log_file_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_activity_global_settings_download_external_storage)).perform(scrollTo());
        onView(withId(R.id.switch_activity_global_settings_download_external_storage)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_download_external_storage_on_off)).check(matches(withText("yes")));
        assertTrue(storagePermissionManager.hasPersistentPermission(getActivity(activityScenario), "/Documents"));
        onView(withId(R.id.textview_activity_global_settings_log_folder)).check(matches(withText(endsWith("/Documents"))));
        onView(withId(R.id.textview_activity_global_settings_download_folder)).check(matches(withText(endsWith("/Documents"))));
        injectLogFolderLauncher(activityScenario, "/Pictures");
        injectDownloadFolderLauncher(activityScenario, "/Pictures");
        storagePermissionManager.setGrantedFolder("/Pictures");
        onView(withId(R.id.cardview_activity_global_settings_log_folder)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_global_settings_log_folder)).perform(click());
        assertTrue(storagePermissionManager.hasPersistentPermission(getActivity(activityScenario), "/Pictures"));
        onView(withId(R.id.textview_activity_global_settings_log_folder)).check(matches(withText(endsWith("/Pictures"))));
        assertEquals("/Pictures", preferenceManager.getPreferenceArbitraryLogFolder());
        assertTrue(storagePermissionManager.hasPersistentPermission(getActivity(activityScenario), "/Documents"));
        onView(withId(R.id.cardview_activity_global_settings_download_folder)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_global_settings_download_folder)).perform(click());
        assertTrue(storagePermissionManager.hasPersistentPermission(getActivity(activityScenario), "/Pictures"));
        onView(withId(R.id.textview_activity_global_settings_download_folder)).check(matches(withText(endsWith("/Pictures"))));
        assertEquals("/Pictures", preferenceManager.getPreferenceArbitraryLogFolder());
        assertFalse(storagePermissionManager.hasPersistentPermission(getActivity(activityScenario), "/Documents"));
        activityScenario.close();
    }

    private void injectMocks(ActivityScenario<?> activityScenario) {
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(getTimeBasedSuspensionScheduler());
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectStoragePermissionManager(storagePermissionManager);
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectLogFolderLauncher(new DelegatingTestPermissionLauncher(((GlobalSettingsActivity) getActivity(activityScenario))::grantArbitraryLogFolderPermission));
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectDownloadFolderLauncher(new DelegatingTestPermissionLauncher(((GlobalSettingsActivity) getActivity(activityScenario))::grantArbitraryDownloadFolderPermission));
    }

    private void injectLogFolderLauncher(ActivityScenario<?> activityScenario, String uri) {
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectLogFolderLauncher(new DelegatingTestPermissionLauncher(((GlobalSettingsActivity) getActivity(activityScenario))::grantArbitraryLogFolderPermission, uri));
    }

    private void injectDownloadFolderLauncher(ActivityScenario<?> activityScenario, String uri) {
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectDownloadFolderLauncher(new DelegatingTestPermissionLauncher(((GlobalSettingsActivity) getActivity(activityScenario))::grantArbitraryDownloadFolderPermission, uri));
    }

    private MockStoragePermissionManager getMockStoragePermissionManager() {
        return new MockStoragePermissionManager();
    }
}
