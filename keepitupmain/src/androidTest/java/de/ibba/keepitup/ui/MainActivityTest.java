package de.ibba.keepitup.ui;

import android.support.test.annotation.UiThreadTest;
import android.support.test.filters.MediumTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.ibba.keepitup.R;
import de.ibba.keepitup.ui.mapping.NetworkTaskViewHolder;

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
        recyclerView = activity.findViewById(R.id.listview_network_tasks);
    }

    @Test
    @UiThreadTest
    public void testBindText() {
        NetworkTaskViewHolder viewHolder1 = (NetworkTaskViewHolder) recyclerView.findViewHolderForAdapterPosition(0);
        Assert.assertNotNull(viewHolder1);
        TextView statusText1 = viewHolder1.itemView.findViewById(R.id.textview_list_item_network_task_status);
        Assert.assertTrue(statusText1.getText().toString().contains("Status"));
        Assert.assertTrue(statusText1.getText().toString().contains("Stopped"));
        TextView accessTypeText1 = viewHolder1.itemView.findViewById(R.id.textview_list_item_network_task_access_type);
        Assert.assertTrue(accessTypeText1.getText().toString().contains("Ping"));
        TextView addressText1 = viewHolder1.itemView.findViewById(R.id.textview_list_item_network_task_address);
        Assert.assertTrue(addressText1.getText().toString().contains("Host: Address1"));
        Assert.assertTrue(addressText1.getText().toString().contains("Port: 21"));
        NetworkTaskViewHolder viewHolder2 = (NetworkTaskViewHolder) recyclerView.findViewHolderForAdapterPosition(1);
        Assert.assertNotNull(viewHolder2);
        TextView statusText2 = viewHolder2.itemView.findViewById(R.id.textview_list_item_network_task_status);
        Assert.assertTrue(statusText2.getText().toString().contains("Status"));
        Assert.assertTrue(statusText2.getText().toString().contains("Stopped"));
        TextView accessTypeText2 = viewHolder2.itemView.findViewById(R.id.textview_list_item_network_task_access_type);
        Assert.assertTrue(accessTypeText2.getText().toString().contains("No type"));
        TextView addressText2 = viewHolder2.itemView.findViewById(R.id.textview_list_item_network_task_address);
        Assert.assertTrue(addressText2.getText().toString().contains("Host: not applicable"));
        Assert.assertFalse(addressText2.getText().toString().contains("Port"));
    }
}
