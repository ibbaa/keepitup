package de.ibba.keepitup.ui;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.platform.app.InstrumentationRegistry;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

import de.ibba.keepitup.db.LogDAO;
import de.ibba.keepitup.db.NetworkTaskDAO;
import de.ibba.keepitup.db.SchedulerIdHistoryDAO;
import de.ibba.keepitup.logging.Dump;
import de.ibba.keepitup.logging.Log;
import de.ibba.keepitup.resources.PreferenceManager;
import de.ibba.keepitup.service.IFileManager;
import de.ibba.keepitup.service.NetworkTaskProcessServiceScheduler;
import de.ibba.keepitup.service.SystemFileManager;
import de.ibba.keepitup.test.matcher.ChildDescendantAtPositionMatcher;
import de.ibba.keepitup.test.matcher.DrawableMatcher;
import de.ibba.keepitup.test.matcher.GridLayoutPositionMatcher;
import de.ibba.keepitup.test.matcher.ListSizeMatcher;
import de.ibba.keepitup.test.matcher.TextColorMatcher;
import de.ibba.keepitup.test.mock.TestRegistry;
import de.ibba.keepitup.test.viewaction.WaitForViewAction;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.endsWith;

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
