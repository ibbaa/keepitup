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

package net.ibbaa.keepitup.ui;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.endsWith;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.platform.app.InstrumentationRegistry;

import net.ibbaa.keepitup.db.LogDAO;
import net.ibbaa.keepitup.db.NetworkTaskDAO;
import net.ibbaa.keepitup.db.SchedulerIdHistoryDAO;
import net.ibbaa.keepitup.logging.Dump;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.resources.PreferenceManager;
import net.ibbaa.keepitup.service.IFileManager;
import net.ibbaa.keepitup.service.NetworkTaskProcessServiceScheduler;
import net.ibbaa.keepitup.service.SystemFileManager;
import net.ibbaa.keepitup.service.SystemThemeManager;
import net.ibbaa.keepitup.test.matcher.ChildDescendantAtPositionMatcher;
import net.ibbaa.keepitup.test.matcher.DrawableMatcher;
import net.ibbaa.keepitup.test.matcher.GridLayoutPositionMatcher;
import net.ibbaa.keepitup.test.matcher.ListSizeMatcher;
import net.ibbaa.keepitup.test.matcher.TextColorMatcher;
import net.ibbaa.keepitup.test.mock.TestRegistry;
import net.ibbaa.keepitup.test.viewaction.WaitForViewAction;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

public abstract class BaseUITest {

    private NetworkTaskDAO networkTaskDAO;
    private LogDAO logDAO;
    private SchedulerIdHistoryDAO schedulerIdHistoryDAO;
    private NetworkTaskProcessServiceScheduler scheduler;
    private PreferenceManager preferenceManager;
    private IFileManager fileManager;

    @Before
    public void beforeEachTestMethod() {
        Log.initialize(null);
        Dump.initialize(null);
        scheduler = new NetworkTaskProcessServiceScheduler(TestRegistry.getContext());
        scheduler.cancelAll();
        NetworkTaskProcessServiceScheduler.getNetworkTaskProcessPool().reset();
        logDAO = new LogDAO(TestRegistry.getContext());
        logDAO.deleteAllLogs();
        networkTaskDAO = new NetworkTaskDAO(TestRegistry.getContext());
        networkTaskDAO.deleteAllNetworkTasks();
        schedulerIdHistoryDAO = new SchedulerIdHistoryDAO(TestRegistry.getContext());
        schedulerIdHistoryDAO.deleteAllSchedulerIds();
        setLocale(Locale.US);
        preferenceManager = new PreferenceManager(TestRegistry.getContext());
        preferenceManager.removeAllPreferences();
        fileManager = new SystemFileManager(TestRegistry.getContext());
        fileManager.delete(fileManager.getInternalDownloadDirectory());
        fileManager.delete(fileManager.getExternalRootDirectory(0));
        fileManager.delete(fileManager.getExternalRootDirectory(1));
        SystemThemeManager themeManager = new SystemThemeManager();
        themeManager.setThemeByCode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    }

    @After
    public void afterEachTestMethod() {
        Log.initialize(null);
        Dump.initialize(null);
        scheduler.cancelAll();
        NetworkTaskProcessServiceScheduler.getNetworkTaskProcessPool().reset();
        logDAO.deleteAllLogs();
        networkTaskDAO.deleteAllNetworkTasks();
        schedulerIdHistoryDAO.deleteAllSchedulerIds();
        preferenceManager.removeAllPreferences();
        fileManager = new SystemFileManager(TestRegistry.getContext());
        fileManager.delete(fileManager.getInternalDownloadDirectory());
        fileManager.delete(fileManager.getExternalRootDirectory(0));
        fileManager.delete(fileManager.getExternalRootDirectory(1));
    }

    public IFileManager getFileManager() {
        return fileManager;
    }

    public FragmentActivity getActivity(ActivityScenario<?> scenario) {
        final AtomicReference<FragmentActivity> reference = new AtomicReference<>();
        scenario.onActivity(activity -> reference.set((FragmentActivity) activity));
        return reference.get();
    }

    public ActivityScenario<? extends SettingsInputActivity> launchSettingsInputActivity(Class<? extends SettingsInputActivity> clazz) {
        Intent intent = new Intent(TestRegistry.getContext(), clazz);
        return launchSettingsInputActivity(intent);
    }

    public ActivityScenario<? extends SettingsInputActivity> launchSettingsInputActivity(Intent intent) {
        ActivityScenario<? extends SettingsInputActivity> activityScenario = ActivityScenario.launch(intent);
        activityScenario.onActivity(activity -> activity.injectResources(TestRegistry.getContext().getResources()));
        activityScenario.onActivity(activity -> activity.setRequestedOrientation(Configuration.ORIENTATION_PORTRAIT));
        return activityScenario;
    }

    public ActivityScenario<? extends RecyclerViewBaseActivity> launchRecyclerViewBaseActivity(Class<? extends RecyclerViewBaseActivity> clazz) {
        Intent intent = new Intent(TestRegistry.getContext(), clazz);
        return launchRecyclerViewBaseActivity(intent);
    }

    public ActivityScenario<? extends RecyclerViewBaseActivity> launchRecyclerViewBaseActivity(Intent intent) {
        ActivityScenario<? extends RecyclerViewBaseActivity> activityScenario = ActivityScenario.launch(intent);
        activityScenario.onActivity(activity -> activity.injectResources(TestRegistry.getContext().getResources()));
        activityScenario.onActivity(activity -> activity.setRequestedOrientation(Configuration.ORIENTATION_PORTRAIT));
        return activityScenario;
    }

    public void rotateScreen(ActivityScenario<?> activityScenario) {
        int orientation = TestRegistry.getContext().getResources().getConfiguration().orientation;
        activityScenario.onActivity(activity -> activity.setRequestedOrientation((orientation == Configuration.ORIENTATION_PORTRAIT) ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT));
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        onView(isRoot()).perform(waitFor(1000));
    }

    public DialogFragment getDialog(ActivityScenario<?> scenario, Class<? extends DialogFragment> clazz) {
        List<Fragment> fragments = getActivity(scenario).getSupportFragmentManager().getFragments();
        int size = fragments.size();
        for (int ii = size - 1; ii >= 0; ii--) {
            if (clazz.isInstance(fragments.get(ii))) {
                return (DialogFragment) fragments.get(ii);
            }
        }
        return null;
    }

    public NetworkTaskDAO getNetworkTaskDAO() {
        return networkTaskDAO;
    }

    public LogDAO getLogDAO() {
        return logDAO;
    }

    public SchedulerIdHistoryDAO getSchedulerIdHistoryDAO() {
        return schedulerIdHistoryDAO;
    }

    public NetworkTaskProcessServiceScheduler getScheduler() {
        return scheduler;
    }

    public PreferenceManager getPreferenceManager() {
        return preferenceManager;
    }

    public void setLocale(Locale locale) {
        InstrumentationRegistry.getInstrumentation().getTargetContext().getResources().getConfiguration().setLocale(locale);
    }

    public static Matcher<View> withListSize(int size) {
        return new ListSizeMatcher(size);
    }

    public static Matcher<View> withChildDescendantAtPosition(Matcher<View> parentMatcher, int childPosition) {
        return new ChildDescendantAtPositionMatcher(parentMatcher, childPosition);
    }

    public static Matcher<View> withGridLayoutPosition(int row, int column) {
        return new GridLayoutPositionMatcher(row, 1, GridLayout.LEFT, column, 1, GridLayout.LEFT);
    }

    public static Matcher<View> withGridLayoutPositionAndSpan(int row, int rowSpan, GridLayout.Alignment rowAlignment, int column, int columnSpan, GridLayout.Alignment columnAlignment) {
        return new GridLayoutPositionMatcher(row, rowSpan, rowAlignment, column, columnSpan, columnAlignment);
    }

    public static Matcher<View> withDrawable(int resourceId) {
        return new DrawableMatcher(resourceId);
    }

    public static Matcher<View> withTextColor(int expectedId) {
        return new TextColorMatcher(expectedId);
    }

    public static Matcher<View> withOverflowButton() {
        return anyOf(allOf(isDisplayed(), withContentDescription("More options")), allOf(isDisplayed(), withClassName(endsWith("OverflowMenuButton"))));
    }

    public static ViewAction waitFor(long time) {
        return new WaitForViewAction(time);
    }

    public static String getText(final Matcher<View> matcher) {
        final String[] stringHolder = {null};
        onView(matcher).perform(new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(TextView.class);
            }

            @Override
            public String getDescription() {
                return "getting text from a TextView";
            }

            @Override
            public void perform(UiController uiController, View view) {
                TextView textView = (TextView) view;
                stringHolder[0] = textView.getText().toString();
            }
        });
        return stringHolder[0];
    }
}
