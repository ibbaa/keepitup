package de.ibba.keepitup.ui.sync;

import android.support.test.filters.MediumTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.ibba.keepitup.test.mock.MockHandler;
import de.ibba.keepitup.ui.BaseUITest;
import de.ibba.keepitup.ui.NetworkTaskMainActivity;
import de.ibba.keepitup.ui.adapter.NetworkTaskAdapter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class UISyncControllerTest extends BaseUITest {

    @Rule
    public final ActivityTestRule<NetworkTaskMainActivity> rule = new ActivityTestRule<>(NetworkTaskMainActivity.class, false, false);

    @Test
    public void testStartStop() {
        launchRecyclerViewBaseActivity(rule);
        assertTrue(UISyncController.isRunning());
        MockHandler handler = (MockHandler) UISyncController.getHandler();
        assertNotNull(handler);
        assertTrue(handler.wasStartCalled());
        assertTrue(handler.wasStartDelayedCalled());
        assertFalse(handler.wasStopCalled());
        MockHandler.StartDelayedCall startDelayedCall = handler.getStartDelayedCalls().get(0);
        assertEquals(5000, startDelayedCall.getDelay());
        MockHandler.StartCall startCall = handler.getStartCalls().get(0);
        Runnable startedRunnable = startCall.getRunnable();
        UISyncController.stop();
        assertFalse(UISyncController.isRunning());
        assertTrue(handler.wasStopCalled());
        MockHandler.StopCall stopCall = handler.getStopCalls().get(0);
        Runnable stoppedRunnable = stopCall.getRunnable();
        assertSame(startedRunnable, stoppedRunnable);
    }

    private NetworkTaskAdapter getAdapter() {
        NetworkTaskMainActivity activity = rule.getActivity();
        return (NetworkTaskAdapter) activity.getAdapter();
    }
}
