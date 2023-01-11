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
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.ui.BaseUITest;
import net.ibbaa.keepitup.ui.NetworkTaskMainActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class ProgressDialogTest extends BaseUITest {

    private ActivityScenario<?> activityScenario;

    @Before
    public void beforeEachTestMethod() {
        super.beforeEachTestMethod();
        activityScenario = ActivityScenario.launch(NetworkTaskMainActivity.class);
    }

    @After
    public void afterEachTestMethod() {
        super.afterEachTestMethod();
        activityScenario.close();
    }

    @Test
    public void testProgressBarDisplayed() {
        openProgressDialog();
        onView(withId(R.id.progressbar_dialog_progress)).check(matches(isDisplayed()));
    }

    @Test
    public void testProgressBarScreenRotation() {
        openProgressDialog();
        onView(withId(R.id.progressbar_dialog_progress)).check(matches(isDisplayed()));
        rotateScreen(activityScenario);
        onView(withId(R.id.progressbar_dialog_progress)).check(matches(isDisplayed()));
        rotateScreen(activityScenario);
        onView(withId(R.id.progressbar_dialog_progress)).check(matches(isDisplayed()));
    }

    private ProgressDialog openProgressDialog() {
        ProgressDialog progressDialog = new ProgressDialog();
        progressDialog.show(getActivity(activityScenario).getSupportFragmentManager(), ConfirmDialog.class.getName());
        onView(isRoot()).perform(waitFor(500));
        return progressDialog;
    }
}
