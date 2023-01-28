/*
 * Copyright (c) 2023. Alwin Ibba
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

package net.ibbaa.keepitup.ui.dialog;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import android.os.Bundle;

import androidx.test.core.app.ActivityScenario;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.test.mock.MockPermissionManager;
import net.ibbaa.keepitup.ui.BaseUITest;
import net.ibbaa.keepitup.ui.NetworkTaskMainActivity;
import net.ibbaa.keepitup.util.BundleUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PermissionExplainDialogTest extends BaseUITest {

    private ActivityScenario<?> activityScenario;
    private MockPermissionManager permissionManager;

    @Before
    public void beforeEachTestMethod() {
        super.beforeEachTestMethod();
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class);
        permissionManager = new MockPermissionManager();
    }

    @After
    public void afterEachTestMethod() {
        super.afterEachTestMethod();
        activityScenario.close();
    }

    @Test
    public void testExplainMessageWithValidPermission() {
        openPermissionExplainDialog(PermissionExplainDialog.Permission.POST_NOTIFICATIONS);
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.textview_dialog_general_permission_explain_message)).check(matches(withText("Message")));
        onView(withId(R.id.imageview_dialog_permission_explain_ok)).perform(click());
        assertEquals(PermissionExplainDialog.Permission.POST_NOTIFICATIONS, permissionManager.getLastPermission());
    }

    @Test
    public void testExplainMessageWithoutValidPermission() {
        openPermissionExplainDialog(null);
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.textview_dialog_general_permission_explain_message)).check(matches(withText("Message")));
        onView(withId(R.id.imageview_dialog_permission_explain_ok)).perform(click());
        assertNull(permissionManager.getLastPermission());
    }

    @Test
    public void testScreenRotation() {
        PermissionExplainDialog permissionExplainDialog = openPermissionExplainDialog(PermissionExplainDialog.Permission.POST_NOTIFICATIONS);
        onView(withId(R.id.textview_dialog_general_permission_explain_message)).check(matches(withText("Message")));
        rotateScreen(activityScenario);
        permissionExplainDialog.injectPermissionManager(permissionManager);
        onView(withId(R.id.textview_dialog_general_permission_explain_message)).check(matches(withText("Message")));
        rotateScreen(activityScenario);
        permissionExplainDialog.injectPermissionManager(permissionManager);
        onView(withId(R.id.textview_dialog_general_permission_explain_message)).check(matches(withText("Message")));
        onView(withId(R.id.imageview_dialog_permission_explain_ok)).perform(click());
    }

    private PermissionExplainDialog openPermissionExplainDialog(PermissionExplainDialog.Permission permission) {
        PermissionExplainDialog permissionExplainDialog = new PermissionExplainDialog();
        permissionExplainDialog.injectPermissionManager(permissionManager);
        Bundle bundle = BundleUtil.stringsToBundle(new String[]{permissionExplainDialog.getMessageKey(), PermissionExplainDialog.Permission.class.getSimpleName()}, new String[]{"Message", permission == null ? null : permission.name()});
        permissionExplainDialog.setArguments(bundle);
        permissionExplainDialog.show(getActivity(activityScenario).getSupportFragmentManager(), PermissionExplainDialog.class.getName());
        onView(isRoot()).perform(waitFor(500));
        return permissionExplainDialog;
    }
}
