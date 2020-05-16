package de.ibba.keepitup.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.view.View;
import android.widget.GridLayout;

import androidx.test.espresso.ViewAction;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;

import java.util.Locale;

import de.ibba.keepitup.db.LogDAO;
import de.ibba.keepitup.db.NetworkTaskDAO;
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

public abstract class BaseUITest {

    private NetworkTaskDAO networkTaskDAO;
    private LogDAO logDAO;
    private NetworkTaskProcessServiceScheduler scheduler;
    private PreferenceManager preferenceManager;
    private IFileManager fileManager;

    @Before
    public void beforeEachTestMethod() {
        Log.initialize(null);
        Dump.initialize(null);
        scheduler = new NetworkTaskProcessServiceScheduler(TestRegistry.getContext());
        scheduler.cancelAll();
        logDAO = new LogDAO(TestRegistry.getContext());
        logDAO.deleteAllLogs();
        networkTaskDAO = new NetworkTaskDAO(TestRegistry.getContext());
        networkTaskDAO.deleteAllNetworkTasks();
        setLocale(Locale.US);
        preferenceManager = new PreferenceManager(TestRegistry.getContext());
        preferenceManager.removeAllPreferences();
        fileManager = new SystemFileManager(TestRegistry.getContext());
        fileManager.delete(fileManager.getInternalDownloadDirectory());
        fileManager.delete(fileManager.getExternalRootDirectory(0));
    }

    @After
    public void afterEachTestMethod() {
        Log.initialize(null);
        Dump.initialize(null);
        scheduler.cancelAll();
        logDAO.deleteAllLogs();
        networkTaskDAO.deleteAllNetworkTasks();
        preferenceManager.removeAllPreferences();
        fileManager = new SystemFileManager(TestRegistry.getContext());
        fileManager.delete(fileManager.getInternalDownloadDirectory());
        fileManager.delete(fileManager.getExternalRootDirectory(0));
    }

    public IFileManager getFileManager() {
        return fileManager;
    }

    public SettingsInputActivity launchSettingsInputActivity(ActivityTestRule<?> rule) {
        return launchSettingsInputActivity(rule, null);
    }

    public SettingsInputActivity launchSettingsInputActivity(ActivityTestRule<?> rule, Intent intent) {
        SettingsInputActivity activity = (SettingsInputActivity) rule.launchActivity(intent);
        activity.injectResources(TestRegistry.getContext().getResources());
        activity.setRequestedOrientation(Configuration.ORIENTATION_PORTRAIT);
        return activity;
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

    public void rotateScreen(Activity activity) {
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

    public static ViewAction waitFor(long time) {
        return new WaitForViewAction(time);
    }
}
