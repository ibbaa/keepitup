package de.ibba.keepitup.ui;

import android.support.test.InstrumentationRegistry;
import android.view.View;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;

import java.util.Locale;

import de.ibba.keepitup.db.LogDAO;
import de.ibba.keepitup.db.NetworkTaskDAO;
import de.ibba.keepitup.service.NetworkKeepAliveServiceScheduler;
import de.ibba.keepitup.test.matcher.ChildDescendantAtPositionMatcher;
import de.ibba.keepitup.test.matcher.DrawableMatcher;
import de.ibba.keepitup.test.matcher.ListSizeMatcher;
import de.ibba.keepitup.test.matcher.TextColorMatcher;

public abstract class BaseUITest {

    private NetworkTaskDAO networkTaskDAO;
    private LogDAO logDAO;
    private NetworkKeepAliveServiceScheduler scheduler;

    @Before
    public void beforeEachTestMethod() {
        logDAO = new LogDAO(InstrumentationRegistry.getTargetContext());
        logDAO.deleteAllLogs();
        networkTaskDAO = new NetworkTaskDAO(InstrumentationRegistry.getTargetContext());
        networkTaskDAO.deleteAllNetworkTasks();
        scheduler = new NetworkKeepAliveServiceScheduler(InstrumentationRegistry.getTargetContext());
        scheduler.stopAll();
        setLocale(Locale.US);
    }

    @After
    public void afterEachTestMethod() {
        logDAO.deleteAllLogs();
        networkTaskDAO.deleteAllNetworkTasks();
        scheduler.stopAll();
    }

    public NetworkTaskDAO getNetworkTaskDAO() {
        return networkTaskDAO;
    }

    public LogDAO getLogDAO() {
        return logDAO;
    }

    public NetworkKeepAliveServiceScheduler getScheduler() {
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
