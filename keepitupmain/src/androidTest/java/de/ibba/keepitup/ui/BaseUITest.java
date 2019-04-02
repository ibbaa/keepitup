package de.ibba.keepitup.ui;

import android.support.test.InstrumentationRegistry;
import android.view.View;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;

import java.util.Locale;

import de.ibba.keepitup.db.LogDAO;
import de.ibba.keepitup.db.NetworkTaskDAO;
import de.ibba.keepitup.service.NetworkTaskServiceScheduler;
import de.ibba.keepitup.test.matcher.ChildDescendantAtPositionMatcher;
import de.ibba.keepitup.test.matcher.DrawableMatcher;
import de.ibba.keepitup.test.matcher.ListSizeMatcher;
import de.ibba.keepitup.test.matcher.TextColorMatcher;

public abstract class BaseUITest {

    private NetworkTaskDAO networkTaskDAO;
    private LogDAO logDAO;
    private NetworkTaskServiceScheduler scheduler;

    @Before
    public void beforeEachTestMethod() {
        scheduler = new NetworkTaskServiceScheduler(InstrumentationRegistry.getTargetContext());
        scheduler.cancelAll();
        logDAO = new LogDAO(InstrumentationRegistry.getTargetContext());
        logDAO.deleteAllLogs();
        networkTaskDAO = new NetworkTaskDAO(InstrumentationRegistry.getTargetContext());
        networkTaskDAO.deleteAllNetworkTasks();
        setLocale(Locale.US);
    }

    @After
    public void afterEachTestMethod() {
        scheduler.cancelAll();
        logDAO.deleteAllLogs();
        networkTaskDAO.deleteAllNetworkTasks();
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

    public void setLocale(Locale locale) {
        InstrumentationRegistry.getTargetContext().getResources().getConfiguration().setLocale(locale);
    }

    public static Matcher<View> withListSize(final int size) {
        return new ListSizeMatcher(size);
    }

    public static Matcher<View> withChildDescendantAtPosition(final Matcher<View> parentMatcher, final int childPosition) {
        return new ChildDescendantAtPositionMatcher(parentMatcher, childPosition);
    }

    public static Matcher<View> withDrawable(final int resourceId) {
        return new DrawableMatcher(resourceId);
    }

    public static Matcher<View> withTextColor(final int expectedId) {
        return new TextColorMatcher(expectedId);
    }
}
