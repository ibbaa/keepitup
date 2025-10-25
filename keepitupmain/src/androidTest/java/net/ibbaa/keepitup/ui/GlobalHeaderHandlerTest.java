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

package net.ibbaa.keepitup.ui;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import net.ibbaa.keepitup.model.Header;
import net.ibbaa.keepitup.ui.dialog.GlobalHeadersDialog;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@MediumTest
@SuppressWarnings({"SequencedCollectionMethodCanBeUsed"})
@RunWith(AndroidJUnit4.class)
public class GlobalHeaderHandlerTest extends BaseUITest {

    private ActivityScenario<?> activityScenario;

    @Before
    public void beforeEachTestMethod() {
        super.beforeEachTestMethod();
        getHeaderDAO().deleteAllHeaders();
    }

    @After
    public void afterEachTestMethod() {
        super.afterEachTestMethod();
        getHeaderDAO().deleteAllHeaders();
    }

    @Test
    public void testGetGlobalHeaders() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        GlobalHeadersDialog headersDialog = openGlobalHeadersDialog();
        onView(isRoot()).perform(waitFor(500));
        GlobalHeaderHandler handler = new GlobalHeaderHandler(getGlobalSettingsActivity(), headersDialog);
        handler.reset();
        assertEquals(0, handler.getGlobalHeaders().size());
        getHeaderDAO().insertHeader(getHeader1(-1));
        getHeaderDAO().insertHeader(getHeader1(1));
        assertEquals(0, handler.getGlobalHeaders().size());
        handler.reset();
        assertEquals(1, handler.getGlobalHeaders().size());
        getHeaderDAO().insertHeader(getHeader2(-1));
        handler.reset();
        assertEquals(2, handler.getGlobalHeaders().size());
        assertTrue(getHeader1(-1).isTechnicallyEqual(handler.getGlobalHeaders().get(0)));
        assertTrue(getHeader2(-1).isTechnicallyEqual(handler.getGlobalHeaders().get(1)));
        handler.reset();
        activityScenario.close();
    }

    @Test
    public void testSynchronizeHeaderEmpty() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        GlobalHeadersDialog headersDialog = openGlobalHeadersDialog();
        onView(isRoot()).perform(waitFor(500));
        GlobalHeaderHandler handler = new GlobalHeaderHandler(getGlobalSettingsActivity(), headersDialog);
        handler.reset();
        assertFalse(handler.synchronizeHeaders());
        assertTrue(getHeaderDAO().readAllHeaders().isEmpty());
        assertTrue(headersDialog.getAdapter().getAllItems().isEmpty());
        activityScenario.close();
    }

    @Test
    public void testSynchronizeHeadersDelete() {
        getHeaderDAO().insertHeader(getHeader1(-1));
        getHeaderDAO().insertHeader(getHeader2(-1));
        getHeaderDAO().insertHeader(getHeader2(1));
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        GlobalHeadersDialog headersDialog = openGlobalHeadersDialog();
        onView(isRoot()).perform(waitFor(500));
        GlobalHeaderHandler handler = new GlobalHeaderHandler(getGlobalSettingsActivity(), headersDialog);
        handler.reset();
        headersDialog.getAdapter().removeItems();
        assertTrue(handler.synchronizeHeaders());
        assertTrue(getHeaderDAO().readGlobalHeaders().isEmpty());
        assertEquals(1, getHeaderDAO().readAllHeaders().size());
        assertTrue(getHeader2(1).isTechnicallyEqual(getHeaderDAO().readAllHeaders().get(0)));
        activityScenario.close();
    }

    @Test
    public void testSynchronizeIntervalsAdd() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        GlobalHeadersDialog headersDialog = openGlobalHeadersDialog();
        onView(isRoot()).perform(waitFor(500));
        GlobalHeaderHandler handler = new GlobalHeaderHandler(getGlobalSettingsActivity(), headersDialog);
        handler.reset();
        headersDialog.getAdapter().addItem(getHeader1(-1));
        headersDialog.getAdapter().addItem(getHeader2(-1));
        headersDialog.getAdapter().addItem(getHeader2(1));
        assertTrue(handler.synchronizeHeaders());
        assertEquals(2, getHeaderDAO().readAllHeaders().size());
        assertTrue(getHeader1(-1).isTechnicallyEqual(getHeaderDAO().readAllHeaders().get(0)));
        assertTrue(getHeader2(-1).isTechnicallyEqual(getHeaderDAO().readAllHeaders().get(1)));
        activityScenario.close();
    }

    @Test
    public void testSynchronizeHeadersNotUpdated() {
        Header header1 = getHeaderDAO().insertHeader(getHeader1(-1));
        Header header2 = getHeaderDAO().insertHeader(getHeader2(-1));
        getHeaderDAO().insertHeader(getHeader2(1));
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        GlobalHeadersDialog headersDialog = openGlobalHeadersDialog();
        onView(isRoot()).perform(waitFor(500));
        GlobalHeaderHandler handler = new GlobalHeaderHandler(getGlobalSettingsActivity(), headersDialog);
        handler.reset();
        headersDialog.getAdapter().removeItems();
        headersDialog.getAdapter().addItem(header1);
        headersDialog.getAdapter().addItem(header2);
        assertFalse(handler.synchronizeHeaders());
        List<Header> headers = getHeaderDAO().readAllHeaders();
        assertEquals(3, headers.size());
        activityScenario.close();
    }

    @Test
    public void testSynchronizeHeadersUpdated() {
        Header header1 = getHeaderDAO().insertHeader(getHeader1(-1));
        Header header2 = getHeaderDAO().insertHeader(getHeader2(-1));
        getHeaderDAO().insertHeader(getHeader2(1));
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        GlobalHeadersDialog headersDialog = openGlobalHeadersDialog();
        onView(isRoot()).perform(waitFor(500));
        GlobalHeaderHandler handler = new GlobalHeaderHandler(getGlobalSettingsActivity(), headersDialog);
        handler.reset();
        headersDialog.getAdapter().removeItems();
        header2.setName("name3");
        header2.setValue("value3");
        headersDialog.getAdapter().addItem(header1);
        headersDialog.getAdapter().addItem(header2);
        assertTrue(handler.synchronizeHeaders());
        assertEquals(3, getHeaderDAO().readAllHeaders().size());
        List<Header> headers = getHeaderDAO().readGlobalHeaders();
        assertTrue(headers.get(0).isTechnicallyEqual(getHeader1(-1)));
        assertTrue(headers.get(0).isTechnicallyEqual(header1));
        assertFalse(headers.get(1).isTechnicallyEqual(getHeader2(-1)));
        assertTrue(headers.get(1).isTechnicallyEqual(header2));
        activityScenario.close();
    }

    @Test
    public void testSynchronizeHeadersAddedUpdatedDeleted() {
        getHeaderDAO().insertHeader(getHeader2(-1));
        Header header3 = getHeaderDAO().insertHeader(getHeader3(-1));
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        GlobalHeadersDialog headersDialog = openGlobalHeadersDialog();
        onView(isRoot()).perform(waitFor(500));
        GlobalHeaderHandler handler = new GlobalHeaderHandler(getGlobalSettingsActivity(), headersDialog);
        handler.reset();
        headersDialog.getAdapter().removeItems();
        header3.setName("anotherName");
        headersDialog.getAdapter().addItem(getHeader1(-1));
        headersDialog.getAdapter().addItem(header3);
        assertTrue(handler.synchronizeHeaders());
        List<Header> headers = getHeaderDAO().readAllHeaders();
        assertEquals(2, headers.size());
        assertTrue(headers.get(0).isTechnicallyEqual(header3));
        assertTrue(headers.get(1).isTechnicallyEqual(getHeader1(-1)));
        activityScenario.close();
    }

    private GlobalHeadersDialog openGlobalHeadersDialog() {
        GlobalHeadersDialog headersDialog = new GlobalHeadersDialog();
        headersDialog.show(getActivity(activityScenario).getSupportFragmentManager(), GlobalHeadersDialog.class.getName());
        return headersDialog;
    }

    private GlobalSettingsActivity getGlobalSettingsActivity() {
        return (GlobalSettingsActivity) getActivity(activityScenario);
    }

    private Header getHeader1(long networktaskid) {
        Header resolve = new Header();
        resolve.setId(-1);
        resolve.setNetworkTaskId(networktaskid);
        resolve.setName("name1");
        resolve.setValue("value1");
        return resolve;
    }

    private Header getHeader2(long networktaskid) {
        Header resolve = new Header();
        resolve.setId(-1);
        resolve.setNetworkTaskId(networktaskid);
        resolve.setName("name2");
        resolve.setValue("value2");
        return resolve;
    }

    private Header getHeader3(long networktaskid) {
        Header resolve = new Header();
        resolve.setId(-1);
        resolve.setNetworkTaskId(networktaskid);
        resolve.setName("name3");
        resolve.setValue("value3");
        return resolve;
    }
}
