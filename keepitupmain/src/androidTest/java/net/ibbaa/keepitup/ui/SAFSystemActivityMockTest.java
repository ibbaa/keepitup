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
import net.ibbaa.keepitup.resources.SystemSetupResult;
import net.ibbaa.keepitup.test.mock.DelegatingTestPermissionLauncher;
import net.ibbaa.keepitup.test.mock.MockExportTask;
import net.ibbaa.keepitup.test.mock.MockImportTask;
import net.ibbaa.keepitup.test.mock.MockStoragePermissionManager;
import net.ibbaa.keepitup.test.mock.TestExportTask;
import net.ibbaa.keepitup.test.mock.TestImportTask;
import net.ibbaa.keepitup.ui.sync.ExportTask;
import net.ibbaa.keepitup.ui.sync.ImportTask;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@MediumTest
@SuppressWarnings({"SameParameterValue"})
@RunWith(AndroidJUnit4.class)
public class SAFSystemActivityMockTest extends BaseUITest {

    private MockStoragePermissionManager storagePermissionManager;

    @Before
    public void beforeEachTestMethod() {
        super.beforeEachTestMethod();
        storagePermissionManager = getMockStoragePermissionManager();
    }

    @Test
    public void testAllowArbitraryFileLocation() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(SystemActivity.class, getBypassSystemSAFBundle());
        injectMocks(activityScenario);
        injectArbitraryFolderLauncher(activityScenario, "/Test");
        storagePermissionManager.setGrantedFolder("/Test");
        onView(withId(R.id.switch_activity_system_allow_arbitrary_file_location)).perform(scrollTo());
        onView(withId(R.id.switch_activity_system_allow_arbitrary_file_location)).perform(click());
        onView(withId(R.id.textview_activity_system_allow_arbitrary_file_location_on_off)).check(matches(withText("yes")));
        assertTrue(getPreferenceManager().getPreferenceAllowArbitraryFileLocation());
        assertTrue(storagePermissionManager.hasPersistentPermission(getActivity(activityScenario), "/Test"));
        assertFalse(storagePermissionManager.hasPersistentPermission(getActivity(activityScenario), "/Documents"));
        onView(withId(R.id.switch_activity_system_allow_arbitrary_file_location)).perform(scrollTo());
        onView(withId(R.id.switch_activity_system_allow_arbitrary_file_location)).perform(click());
        onView(withId(R.id.textview_activity_system_allow_arbitrary_file_location_on_off)).check(matches(withText("no")));
        assertFalse(getPreferenceManager().getPreferenceAllowArbitraryFileLocation());
        assertTrue(storagePermissionManager.hasPersistentPermission(getActivity(activityScenario), "/Test"));
        assertFalse(storagePermissionManager.hasPersistentPermission(getActivity(activityScenario), "/Documents"));
        onView(withId(R.id.switch_activity_system_allow_arbitrary_file_location)).perform(scrollTo());
        onView(withId(R.id.switch_activity_system_allow_arbitrary_file_location)).perform(click());
        onView(withId(R.id.textview_activity_system_allow_arbitrary_file_location_on_off)).check(matches(withText("yes")));
        assertTrue(storagePermissionManager.hasPersistentPermission(getActivity(activityScenario), "/Test"));
        assertTrue(storagePermissionManager.hasPersistentPermission(getActivity(activityScenario), "/Test"));
        assertFalse(storagePermissionManager.hasPersistentPermission(getActivity(activityScenario), "/Documents"));
        activityScenario.close();
    }

    @Test
    public void testAllowArbitraryFileLocationPermissionPresent() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(SystemActivity.class, getBypassSystemSAFBundle());
        injectMocks(activityScenario);
        storagePermissionManager.setGrantedFolder("/Movies");
        storagePermissionManager.requestPersistentFolderPermission(null, "/Movies");
        injectArbitraryFolderLauncher(activityScenario, "/Test");
        storagePermissionManager.setGrantedFolder("/Test");
        onView(withId(R.id.switch_activity_system_allow_arbitrary_file_location)).perform(scrollTo());
        onView(withId(R.id.switch_activity_system_allow_arbitrary_file_location)).perform(click());
        onView(withId(R.id.textview_activity_system_allow_arbitrary_file_location_on_off)).check(matches(withText("yes")));
        assertTrue(getPreferenceManager().getPreferenceAllowArbitraryFileLocation());
        assertTrue(storagePermissionManager.hasPersistentPermission(getActivity(activityScenario), "/Movies"));
        assertFalse(storagePermissionManager.hasPersistentPermission(getActivity(activityScenario), "/Test"));
        assertFalse(storagePermissionManager.hasPersistentPermission(getActivity(activityScenario), "/Documents"));
        onView(withId(R.id.switch_activity_system_allow_arbitrary_file_location)).perform(scrollTo());
        onView(withId(R.id.switch_activity_system_allow_arbitrary_file_location)).perform(click());
        onView(withId(R.id.textview_activity_system_allow_arbitrary_file_location_on_off)).check(matches(withText("no")));
        assertFalse(getPreferenceManager().getPreferenceAllowArbitraryFileLocation());
        assertTrue(storagePermissionManager.hasPersistentPermission(getActivity(activityScenario), "/Movies"));
        assertFalse(storagePermissionManager.hasPersistentPermission(getActivity(activityScenario), "/Test"));
        assertFalse(storagePermissionManager.hasPersistentPermission(getActivity(activityScenario), "/Documents"));
        onView(withId(R.id.switch_activity_system_allow_arbitrary_file_location)).perform(scrollTo());
        onView(withId(R.id.switch_activity_system_allow_arbitrary_file_location)).perform(click());
        onView(withId(R.id.textview_activity_system_allow_arbitrary_file_location_on_off)).check(matches(withText("yes")));
        assertTrue(getPreferenceManager().getPreferenceAllowArbitraryFileLocation());
        assertTrue(storagePermissionManager.hasPersistentPermission(getActivity(activityScenario), "/Movies"));
        assertFalse(storagePermissionManager.hasPersistentPermission(getActivity(activityScenario), "/Test"));
        assertFalse(storagePermissionManager.hasPersistentPermission(getActivity(activityScenario), "/Documents"));
        activityScenario.close();
    }

    @Test
    public void testAllowArbitraryFileLocationImportExportFolderText() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(SystemActivity.class, getBypassSystemSAFBundle());
        injectMocks(activityScenario);
        injectArbitraryFolderLauncher(activityScenario, "/Test");
        storagePermissionManager.setGrantedFolder("/Test");
        onView(withId(R.id.textview_activity_system_config_import_folder)).check(matches(withText(endsWith("config"))));
        onView(withId(R.id.textview_activity_system_config_export_folder)).check(matches(withText(endsWith("config"))));
        onView(withId(R.id.switch_activity_system_allow_arbitrary_file_location)).perform(scrollTo());
        onView(withId(R.id.switch_activity_system_allow_arbitrary_file_location)).perform(click());
        onView(withId(R.id.textview_activity_system_allow_arbitrary_file_location_on_off)).check(matches(withText("yes")));
        assertTrue(getPreferenceManager().getPreferenceAllowArbitraryFileLocation());
        onView(withId(R.id.textview_activity_system_config_import_folder)).check(matches(withText("Choose file")));
        onView(withId(R.id.textview_activity_system_config_export_folder)).check(matches(withText("Choose file")));
        onView(withId(R.id.switch_activity_system_allow_arbitrary_file_location)).perform(scrollTo());
        onView(withId(R.id.switch_activity_system_allow_arbitrary_file_location)).perform(click());
        onView(withId(R.id.textview_activity_system_allow_arbitrary_file_location_on_off)).check(matches(withText("no")));
        assertFalse(getPreferenceManager().getPreferenceAllowArbitraryFileLocation());
        onView(withId(R.id.textview_activity_system_config_import_folder)).check(matches(withText(endsWith("config"))));
        onView(withId(R.id.textview_activity_system_config_export_folder)).check(matches(withText(endsWith("config"))));
    }

    private void injectMocks(ActivityScenario<?> activityScenario) {
        ((SystemActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(getTimeBasedSuspensionScheduler());
        ((SystemActivity) getActivity(activityScenario)).injectStoragePermissionManager(storagePermissionManager);
        ((SystemActivity) getActivity(activityScenario)).injectArbitraryFolderLauncher(new DelegatingTestPermissionLauncher(((SystemActivity) getActivity(activityScenario))::grantArbitraryFolderPermissions));
        ((SystemActivity) getActivity(activityScenario)).injectExportFileLauncher(new DelegatingTestPermissionLauncher(((SystemActivity) getActivity(activityScenario))::grantConfigurationExportFilePermission));
        ((SystemActivity) getActivity(activityScenario)).injectImportFileLauncher(new DelegatingTestPermissionLauncher(((SystemActivity) getActivity(activityScenario))::grantConfigurationImportFilePermission));
    }

    private void injectArbitraryFolderLauncher(ActivityScenario<?> activityScenario, String uri) {
        ((SystemActivity) getActivity(activityScenario)).injectArbitraryFolderLauncher(new DelegatingTestPermissionLauncher(((SystemActivity) getActivity(activityScenario))::grantArbitraryFolderPermissions, uri));
    }

    private void injectExportFileLauncher(ActivityScenario<?> activityScenario, String uri) {
        ((SystemActivity) getActivity(activityScenario)).injectExportFileLauncher(new DelegatingTestPermissionLauncher(((SystemActivity) getActivity(activityScenario))::grantConfigurationExportFilePermission, uri));
    }

    private void injectImportFileLauncher(ActivityScenario<?> activityScenario, String uri) {
        ((SystemActivity) getActivity(activityScenario)).injectImportFileLauncher(new DelegatingTestPermissionLauncher(((SystemActivity) getActivity(activityScenario))::grantConfigurationImportFilePermission, uri));
    }

    private MockStoragePermissionManager getMockStoragePermissionManager() {
        return new MockStoragePermissionManager();
    }

    private MockExportTask getMockExportTask(ActivityScenario<?> activityScenario, boolean success) {
        return new MockExportTask(getActivity(activityScenario), success);
    }

    private MockImportTask getMockImportTask(ActivityScenario<?> activityScenario, boolean success) {
        return getMockImportTask(activityScenario, success, false);
    }

    private MockImportTask getMockImportTask(ActivityScenario<?> activityScenario, boolean success, boolean mismatch) {
        return new MockImportTask(getActivity(activityScenario), new SystemSetupResult(success, mismatch, "", ""));
    }

    private TestExportTask getTestExportTask(ActivityScenario<?> activityScenario, String file) {
        return new TestExportTask(getActivity(activityScenario), null, file, true);
    }

    private TestImportTask getTesImportTask(ActivityScenario<?> activityScenario, String file) {
        return new TestImportTask(getActivity(activityScenario), null, file, true);
    }

    private void injectImportTask(ActivityScenario<?> activityScenario, ImportTask importTask) {
        SystemActivity activity = (SystemActivity) getActivity(activityScenario);
        activity.injectImportTask(importTask);
    }

    private void injectExportTask(ActivityScenario<?> activityScenario, ExportTask exportTask) {
        SystemActivity activity = (SystemActivity) getActivity(activityScenario);
        activity.injectExportTask(exportTask);
    }
}
