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

package net.ibbaa.keepitup.ui.dialog;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertEquals;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.test.mock.TestRegistry;
import net.ibbaa.keepitup.ui.BaseUITest;
import net.ibbaa.keepitup.ui.GlobalHeaderHandler;
import net.ibbaa.keepitup.ui.GlobalSettingsActivity;

import org.junit.Test;
import org.junit.runner.RunWith;

@MediumTest
@SuppressWarnings({"SequencedCollectionMethodCanBeUsed"})
@RunWith(AndroidJUnit4.class)
public class GlobalHeadersDialogTest extends BaseUITest {

    private ActivityScenario<?> activityScenario;

    @Test
    public void testOpenCloseDialog() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_global_settings_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_global_settings_global_headers)).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.imageview_dialog_global_headers_cancel)).perform(click());
        assertEquals(0, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.cardview_activity_global_settings_global_headers)).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.imageview_dialog_global_headers_ok)).perform(click());
        assertEquals(0, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        activityScenario.close();
    }

    @Test
    public void testNoHeaders() {
        resetGlobalHeaderHandler();
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_global_settings_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_global_settings_global_headers)).perform(click());
        onView(withId(R.id.listview_dialog_global_headers_headers)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_global_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_global_headers_headers), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_global_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_global_headers_headers), 0))).check(matches(withText("No headers defined")));
        assertEquals(0, getDialog().getAdapter().getAllItems().size());
        onView(withId(R.id.imageview_dialog_global_headers_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testNoHeadersScreenRotation() {
        resetGlobalHeaderHandler();
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_global_settings_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_global_settings_global_headers)).perform(click());
        onView(withId(R.id.listview_dialog_global_headers_headers)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_global_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_global_headers_headers), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_global_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_global_headers_headers), 0))).check(matches(withText("No headers defined")));
        assertEquals(0, getDialog().getAdapter().getAllItems().size());
        rotateScreen(activityScenario);
        onView(withId(R.id.listview_dialog_global_headers_headers)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_global_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_global_headers_headers), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_global_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_global_headers_headers), 0))).check(matches(withText("No headers defined")));
        assertEquals(0, getDialog().getAdapter().getAllItems().size());
        rotateScreen(activityScenario);
        onView(withId(R.id.listview_dialog_global_headers_headers)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_global_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_global_headers_headers), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_global_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_global_headers_headers), 0))).check(matches(withText("No headers defined")));
        assertEquals(0, getDialog().getAdapter().getAllItems().size());
        onView(withId(R.id.imageview_dialog_global_headers_cancel)).perform(click());
        activityScenario.close();
    }

    private GlobalHeadersDialog getDialog() {
        return (GlobalHeadersDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
    }

    private void resetGlobalHeaderHandler() {
        GlobalHeaderHandler handler = new GlobalHeaderHandler(TestRegistry.getContext());
        handler.reset();
    }
}
