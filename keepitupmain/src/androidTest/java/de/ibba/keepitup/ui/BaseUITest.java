package de.ibba.keepitup.ui;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.view.View;
import android.widget.GridLayout;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;

import java.util.Locale;

import de.ibba.keepitup.db.LogDAO;
import de.ibba.keepitup.db.NetworkTaskDAO;
import de.ibba.keepitup.resources.NetworkTaskPreferenceManager;
import de.ibba.keepitup.service.NetworkTaskServiceScheduler;
import de.ibba.keepitup.test.matcher.ChildDescendantAtPositionMatcher;
import de.ibba.keepitup.test.matcher.DrawableMatcher;
import de.ibba.keepitup.test.matcher.GridLayoutPositionMatcher;
import de.ibba.keepitup.test.matcher.ListSizeMatcher;
import de.ibba.keepitup.test.matcher.TextColorMatcher;
import de.ibba.keepitup.test.mock.MockHandler;
import de.ibba.keepitup.test.mock.TestRegistry;
import de.ibba.keepitup.ui.adapter.NetworkTaskAdapter;
import de.ibba.keepitup.ui.sync.UISyncController;

public abstract class BaseUITest {

    private NetworkTaskDAO networkTaskDAO;
    private LogDAO logDAO;
    private NetworkTaskServiceScheduler scheduler;
    private NetworkTaskPreferenceManager preferenceManager;

    @Before
    public void beforeEachTestMethod() {
        scheduler = new NetworkTaskServiceScheduler(TestRegistry.getContext());
        scheduler.cancelAll();
        logDAO = new LogDAO(TestRegistry.getContext());
        logDAO.deleteAllLogs();
        networkTaskDAO = new NetworkTaskDAO(TestRegistry.getContext());
        networkTaskDAO.deleteAllNetworkTasks();
        setLocale(Locale.US);
        UISyncController.injectContext(TestRegistry.getContext());
        MockHandler handler = (MockHandler) UISyncController.getHandler();
        if (handler != null) {
            handler.reset();
        }
        preferenceManager = new NetworkTaskPreferenceManager(TestRegistry.getContext());
        preferenceManager.removeAllPreferences();
    }

    @After
    public void afterEachTestMethod() {
        scheduler.cancelAll();
        logDAO.deleteAllLogs();
        networkTaskDAO.deleteAllNetworkTasks();
        MockHandler handler = (MockHandler) UISyncController.getHandler();
        if (handler != null) {
            handler.reset();
        }
        preferenceManager.removeAllPreferences();
    }

    public RecyclerViewBaseActivity launchRecyclerViewBaseActivity(ActivityTestRule<?> rule) {
        return launchRecyclerViewBaseActivity(rule, null);
    }

    public RecyclerViewBaseActivity launchRecyclerViewBaseActivity(ActivityTestRule<?> rule, Intent intent) {
        RecyclerViewBaseActivity activity = (RecyclerViewBaseActivity) rule.launchActivity(intent);
        activity.injectResources(TestRegistry.getContext().getResources());
        activity.setRequestedOrientation(Configuration.ORIENTATION_PORTRAIT);
        return activity;
    }

    public void startUISyncController(NetworkTaskMainActivity activity, NetworkTaskAdapter adapter) {
        activity.runOnUiThread(() -> UISyncController.start(adapter));
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
    }

    public void stopUISyncController(NetworkTaskMainActivity activity) {
        activity.runOnUiThread(() -> UISyncController.stop());
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
    }

    public void rotateScreen(NetworkTaskMainActivity activity) {
        int orientation = TestRegistry.getContext().getResources().getConfiguration().orientation;
        activity.setRequestedOrientation((orientation == Configuration.ORIENTATION_PORTRAIT) ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
    }

    public NetworkTaskDAO getNetworkTaskDAO() {
        return networkTaskDAO;
    }

    public LogDAO getLogDAO() {
        return logDAO;
    }

    public NetworkTaskServiceScheduler getScheduler() {
        return scheduler;
    }

    public NetworkTaskPreferenceManager getPreferenceManager() {
        return preferenceManager;
    }

    public void setLocale(Locale locale) {
        InstrumentationRegistry.getTargetContext().getResources().getConfiguration().setLocale(locale);
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
}
