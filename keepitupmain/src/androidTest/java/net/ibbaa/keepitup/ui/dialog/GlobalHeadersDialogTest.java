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
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertEquals;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.db.DBSetup;
import net.ibbaa.keepitup.model.Header;
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

    @Test
    public void testDefaultHeader() {
        addDefaultHeader();
        resetGlobalHeaderHandler();
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_global_settings_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_global_settings_global_headers)).perform(click());
        onView(withId(R.id.listview_dialog_global_headers_headers)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_global_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_global_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_global_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_global_headers_headers), 0))).check(matches(withText("User-Agent")));
        onView(allOf(withId(R.id.textview_list_item_global_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_global_headers_headers), 0))).check(matches(withText("Mozilla/5.0")));
        assertEquals(1, getDialog().getAdapter().getAllItems().size());
        activityScenario.close();
    }

    @Test
    public void testDefaultHeaderScreenRotation() {
        addDefaultHeader();
        resetGlobalHeaderHandler();
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_global_settings_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_global_settings_global_headers)).perform(click());
        onView(withId(R.id.listview_dialog_global_headers_headers)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_global_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_global_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_global_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_global_headers_headers), 0))).check(matches(withText("User-Agent")));
        onView(allOf(withId(R.id.textview_list_item_global_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_global_headers_headers), 0))).check(matches(withText("Mozilla/5.0")));
        assertEquals(1, getDialog().getAdapter().getAllItems().size());
        rotateScreen(activityScenario);
        onView(withId(R.id.listview_dialog_global_headers_headers)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_global_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_global_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_global_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_global_headers_headers), 0))).check(matches(withText("User-Agent")));
        onView(allOf(withId(R.id.textview_list_item_global_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_global_headers_headers), 0))).check(matches(withText("Mozilla/5.0")));
        assertEquals(1, getDialog().getAdapter().getAllItems().size());
        rotateScreen(activityScenario);
        onView(withId(R.id.listview_dialog_global_headers_headers)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_global_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_global_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_global_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_global_headers_headers), 0))).check(matches(withText("User-Agent")));
        onView(allOf(withId(R.id.textview_list_item_global_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_global_headers_headers), 0))).check(matches(withText("Mozilla/5.0")));
        assertEquals(1, getDialog().getAdapter().getAllItems().size());
        activityScenario.close();
    }

    @Test
    public void testFiveHeaders() {
        getHeaderDAO().insertHeader(getHeader(1));
        getHeaderDAO().insertHeader(getHeader(2));
        getHeaderDAO().insertHeader(getHeader(3));
        getHeaderDAO().insertHeader(getHeader(4));
        getHeaderDAO().insertHeader(getHeader(5));
        resetGlobalHeaderHandler();
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_global_settings_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_global_settings_global_headers)).perform(click());
        onView(withId(R.id.listview_dialog_global_headers_headers)).check(matches(withListSize(5)));
        onView(allOf(withId(R.id.textview_list_item_global_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_global_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_global_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_global_headers_headers), 0))).check(matches(withText("Name1")));
        onView(allOf(withId(R.id.textview_list_item_global_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_global_headers_headers), 0))).check(matches(withText("Value1")));
        onView(allOf(withId(R.id.textview_list_item_global_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_global_headers_headers), 1))).check(matches(withText("Name2")));
        onView(allOf(withId(R.id.textview_list_item_global_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_global_headers_headers), 1))).check(matches(withText("Value2")));
        onView(allOf(withId(R.id.textview_list_item_global_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_global_headers_headers), 2))).check(matches(withText("Name3")));
        onView(allOf(withId(R.id.textview_list_item_global_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_global_headers_headers), 2))).check(matches(withText("Value3")));
        onView(allOf(withId(R.id.textview_list_item_global_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_global_headers_headers), 3))).check(matches(withText("Name4")));
        onView(allOf(withId(R.id.textview_list_item_global_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_global_headers_headers), 3))).check(matches(withText("Value4")));
        onView(allOf(withId(R.id.textview_list_item_global_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_global_headers_headers), 4))).check(matches(withText("Name5")));
        onView(allOf(withId(R.id.textview_list_item_global_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_global_headers_headers), 4))).check(matches(withText("Value5")));
        assertEquals(5, getDialog().getAdapter().getAllItems().size());
        activityScenario.close();
    }

    @Test
    public void testFiveHeadersScreenRotation() {
        getHeaderDAO().insertHeader(getHeader(1));
        getHeaderDAO().insertHeader(getHeader(2));
        getHeaderDAO().insertHeader(getHeader(3));
        getHeaderDAO().insertHeader(getHeader(4));
        getHeaderDAO().insertHeader(getHeader(5));
        resetGlobalHeaderHandler();
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_global_settings_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_global_settings_global_headers)).perform(click());
        onView(withId(R.id.listview_dialog_global_headers_headers)).check(matches(withListSize(5)));
        onView(allOf(withId(R.id.textview_list_item_global_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_global_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_global_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_global_headers_headers), 0))).check(matches(withText("Name1")));
        onView(allOf(withId(R.id.textview_list_item_global_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_global_headers_headers), 0))).check(matches(withText("Value1")));
        onView(allOf(withId(R.id.textview_list_item_global_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_global_headers_headers), 1))).check(matches(withText("Name2")));
        onView(allOf(withId(R.id.textview_list_item_global_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_global_headers_headers), 1))).check(matches(withText("Value2")));
        onView(allOf(withId(R.id.textview_list_item_global_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_global_headers_headers), 2))).check(matches(withText("Name3")));
        onView(allOf(withId(R.id.textview_list_item_global_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_global_headers_headers), 2))).check(matches(withText("Value3")));
        onView(allOf(withId(R.id.textview_list_item_global_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_global_headers_headers), 3))).check(matches(withText("Name4")));
        onView(allOf(withId(R.id.textview_list_item_global_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_global_headers_headers), 3))).check(matches(withText("Value4")));
        onView(allOf(withId(R.id.textview_list_item_global_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_global_headers_headers), 4))).check(matches(withText("Name5")));
        onView(allOf(withId(R.id.textview_list_item_global_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_global_headers_headers), 4))).check(matches(withText("Value5")));
        assertEquals(5, getDialog().getAdapter().getAllItems().size());
        rotateScreen(activityScenario);
        onView(withId(R.id.listview_dialog_global_headers_headers)).check(matches(withListSize(5)));
        onView(allOf(withId(R.id.textview_list_item_global_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_global_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_global_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_global_headers_headers), 0))).check(matches(withText("Name1")));
        onView(allOf(withId(R.id.textview_list_item_global_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_global_headers_headers), 0))).check(matches(withText("Value1")));
        onView(allOf(withId(R.id.textview_list_item_global_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_global_headers_headers), 1))).check(matches(withText("Name2")));
        onView(allOf(withId(R.id.textview_list_item_global_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_global_headers_headers), 1))).check(matches(withText("Value2")));
        onView(allOf(withId(R.id.textview_list_item_global_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_global_headers_headers), 2))).check(matches(withText("Name3")));
        onView(allOf(withId(R.id.textview_list_item_global_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_global_headers_headers), 2))).check(matches(withText("Value3")));
        onView(allOf(withId(R.id.textview_list_item_global_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_global_headers_headers), 3))).check(matches(withText("Name4")));
        onView(allOf(withId(R.id.textview_list_item_global_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_global_headers_headers), 3))).check(matches(withText("Value4")));
        onView(allOf(withId(R.id.textview_list_item_global_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_global_headers_headers), 4))).check(matches(withText("Name5")));
        onView(allOf(withId(R.id.textview_list_item_global_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_global_headers_headers), 4))).check(matches(withText("Value5")));
        assertEquals(5, getDialog().getAdapter().getAllItems().size());
        rotateScreen(activityScenario);
        onView(withId(R.id.listview_dialog_global_headers_headers)).check(matches(withListSize(5)));
        onView(allOf(withId(R.id.textview_list_item_global_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_global_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_global_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_global_headers_headers), 0))).check(matches(withText("Name1")));
        onView(allOf(withId(R.id.textview_list_item_global_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_global_headers_headers), 0))).check(matches(withText("Value1")));
        onView(allOf(withId(R.id.textview_list_item_global_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_global_headers_headers), 1))).check(matches(withText("Name2")));
        onView(allOf(withId(R.id.textview_list_item_global_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_global_headers_headers), 1))).check(matches(withText("Value2")));
        onView(allOf(withId(R.id.textview_list_item_global_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_global_headers_headers), 2))).check(matches(withText("Name3")));
        onView(allOf(withId(R.id.textview_list_item_global_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_global_headers_headers), 2))).check(matches(withText("Value3")));
        onView(allOf(withId(R.id.textview_list_item_global_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_global_headers_headers), 3))).check(matches(withText("Name4")));
        onView(allOf(withId(R.id.textview_list_item_global_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_global_headers_headers), 3))).check(matches(withText("Value4")));
        onView(allOf(withId(R.id.textview_list_item_global_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_global_headers_headers), 4))).check(matches(withText("Name5")));
        onView(allOf(withId(R.id.textview_list_item_global_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_global_headers_headers), 4))).check(matches(withText("Value5")));
        assertEquals(5, getDialog().getAdapter().getAllItems().size());
        activityScenario.close();
    }

    @Test
    public void testOneHeaderEllipsized() {
        Header header = getHeader(1);
        header.setValue("122345689012234568901223456890122345689012234568901223456890122345689012234568901223456890122345689012234568901223456890122345689012234568901223456890122345689012234568901223456890122345689012234568901223456890122345689012234568901223456890122345689012234568901223456890122345689012234568901223456890122345689012234568901223456890122345689012234568901223456890");
        getHeaderDAO().insertHeader(header);
        resetGlobalHeaderHandler();
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_global_settings_global_headers)).perform(scrollTo());
        onView(allOf(withGridLayoutRowColumnPosition(1, 0), isDescendantOfA(withId(R.id.gridlayout_activity_global_settings_global_headers_value)))).perform(click());
        onView(withId(R.id.listview_dialog_global_headers_headers)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_global_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_global_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_global_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_global_headers_headers), 0))).check(matches(withText("Name1")));
        onView(allOf(withId(R.id.textview_list_item_global_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_global_headers_headers), 0))).check(matches(isEllipsized()));
        activityScenario.close();
    }

    private GlobalHeadersDialog getDialog() {
        return (GlobalHeadersDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
    }

    private void resetGlobalHeaderHandler() {
        GlobalHeaderHandler handler = new GlobalHeaderHandler(TestRegistry.getContext());
        handler.reset();
    }

    private void addDefaultHeader() {
        DBSetup dbSetup = new DBSetup(TestRegistry.getContext());
        dbSetup.initializeHeaderTable();
    }

    private Header getHeader(int number) {
        Header header = new Header();
        header.setNetworkTaskId(-1);
        header.setName("Name" + number);
        header.setValue("Value" + number);
        return header;
    }
}
