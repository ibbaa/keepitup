package de.ibba.keepitup.ui;

import android.support.test.annotation.UiThreadTest;
import android.support.test.filters.MediumTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.ibba.keepitup.R;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class NetworkTaskMainActivityTest {

    @Rule
    public final ActivityTestRule<NetworkTaskMainActivity> rule = new ActivityTestRule<>(NetworkTaskMainActivity.class);

    private NetworkTaskMainActivity activity;
    private RecyclerView recyclerView;

    @Before
    @UiThreadTest
    public void beforeEachTestMethod() {
        activity = rule.getActivity();
        recyclerView = activity.findViewById(R.id.listview_main_activity_network_tasks);
    }

    @Test
    @UiThreadTest
    public void testBindText() {

    }
}
