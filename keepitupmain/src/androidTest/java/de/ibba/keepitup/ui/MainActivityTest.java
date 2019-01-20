package de.ibba.keepitup.ui;

import android.support.test.annotation.UiThreadTest;
import android.support.test.filters.MediumTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.ibba.keepitup.R;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public final ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class);

    private RecyclerView recyclerView;

    @Before
    @UiThreadTest
    public void beforeEachTestMethod() {
        MainActivity activity = rule.getActivity();
        recyclerView = activity.findViewById(R.id.listview_main_activity_network_tasks);
    }

    @Test
    @UiThreadTest
    public void testBindText() {
        NetworkTaskViewHolder viewHolder1 = (NetworkTaskViewHolder) recyclerView.findViewHolderForAdapterPosition(0);
        Assert.assertNotNull(viewHolder1);
        TextView statusText1 = viewHolder1.itemView.findViewById(R.id.textview_list_item_network_task_status);
        Assert.assertTrue(statusText1.getText().toString().contains("Status"));
        Assert.assertTrue(statusText1.getText().toString().contains("Stopped"));
        TextView accessTypeText1 = viewHolder1.itemView.findViewById(R.id.textview_list_item_network_task_accesstype);
        Assert.assertTrue(accessTypeText1.getText().toString().contains("Ping"));
        TextView addressText1 = viewHolder1.itemView.findViewById(R.id.textview_list_item_network_task_address);
        Assert.assertTrue(addressText1.getText().toString().contains("Host: Address1"));
        Assert.assertTrue(addressText1.getText().toString().contains("Port: 21"));
        TextView intervalText1 = viewHolder1.itemView.findViewById(R.id.textview_list_item_network_task_interval);
        Assert.assertTrue(intervalText1.getText().toString().contains("Interval:"));
        Assert.assertTrue(intervalText1.getText().toString().contains("15 minutes"));
        TextView notificationText1 = viewHolder1.itemView.findViewById(R.id.textview_list_item_network_task_notification);
        Assert.assertTrue(notificationText1.getText().toString().contains("Notification"));
        Assert.assertTrue(notificationText1.getText().toString().contains("yes"));
        TextView lastExecTimestampText1 = viewHolder1.itemView.findViewById(R.id.textview_list_item_network_task_last_exec_timestamp);
        Assert.assertTrue(lastExecTimestampText1.getText().toString().contains("successful"));
        TextView lastExecMessageText1 = viewHolder1.itemView.findViewById(R.id.textview_list_item_network_task_last_exec_message);
        Assert.assertEquals(View.VISIBLE, lastExecMessageText1.getVisibility());
        Assert.assertTrue(lastExecMessageText1.getText().toString().contains("Successful execution"));
        NetworkTaskViewHolder viewHolder2 = (NetworkTaskViewHolder) recyclerView.findViewHolderForAdapterPosition(1);
        Assert.assertNotNull(viewHolder2);
        TextView statusText2 = viewHolder2.itemView.findViewById(R.id.textview_list_item_network_task_status);
        Assert.assertTrue(statusText2.getText().toString().contains("Status"));
        Assert.assertTrue(statusText2.getText().toString().contains("Stopped"));
        TextView accessTypeText2 = viewHolder2.itemView.findViewById(R.id.textview_list_item_network_task_accesstype);
        Assert.assertTrue(accessTypeText2.getText().toString().contains("No type"));
        TextView addressText2 = viewHolder2.itemView.findViewById(R.id.textview_list_item_network_task_address);
        Assert.assertTrue(addressText2.getText().toString().contains("Host: not applicable"));
        Assert.assertFalse(addressText2.getText().toString().contains("Port"));
        TextView intervalText2 = viewHolder2.itemView.findViewById(R.id.textview_list_item_network_task_interval);
        Assert.assertTrue(intervalText2.getText().toString().contains("Interval:"));
        Assert.assertTrue(intervalText2.getText().toString().contains("30 minutes"));
        TextView notificationText2 = viewHolder2.itemView.findViewById(R.id.textview_list_item_network_task_notification);
        Assert.assertTrue(notificationText2.getText().toString().contains("Notification"));
        Assert.assertTrue(notificationText2.getText().toString().contains("no"));
        TextView lastExecTimestampText2 = viewHolder2.itemView.findViewById(R.id.textview_list_item_network_task_last_exec_timestamp);
        Assert.assertTrue(lastExecTimestampText2.getText().toString().contains("not executed"));
        TextView lastExecMessageText2 = viewHolder2.itemView.findViewById(R.id.textview_list_item_network_task_last_exec_message);
        Assert.assertEquals(View.GONE, lastExecMessageText2.getVisibility());
        Assert.assertTrue(lastExecMessageText2.getText().toString().isEmpty());
    }
}
