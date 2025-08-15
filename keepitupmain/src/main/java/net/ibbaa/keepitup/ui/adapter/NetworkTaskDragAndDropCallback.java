/*
 * Copyright (c) 2025 Alwin Ibba
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

package net.ibbaa.keepitup.ui.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.ui.NetworkTaskHandler;
import net.ibbaa.keepitup.ui.NetworkTaskMainActivity;

@SuppressWarnings("NotifyDataSetChanged")
public class NetworkTaskDragAndDropCallback extends ItemTouchHelper.Callback {

    private final NetworkTaskMainActivity mainActivity;
    private final NestedScrollRunnable scrollRunnable;

    public NetworkTaskDragAndDropCallback(NetworkTaskMainActivity mainActivity) {
        this.mainActivity = mainActivity;
        NestedScrollView scrollView = mainActivity.findViewById(R.id.scrollview_main_content);
        int scrollThreshold = mainActivity.getResources().getInteger(R.integer.ui_scroll_threshold);
        int scrollAmount = mainActivity.getResources().getInteger(R.integer.ui_scroll_amount);
        this.scrollRunnable = new NestedScrollRunnable(scrollView, scrollThreshold, scrollAmount);
    }

    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
        if (!mainActivity.getResources().getBoolean(R.bool.ui_use_scrolling)) {
            Log.d(NetworkTaskDragAndDropCallback.class.getName(), "scrolling is disabled");
            return;
        }
        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG && viewHolder != null) {
            scrollRunnable.start(viewHolder);
        } else if (actionState == ItemTouchHelper.ACTION_STATE_IDLE) {
            scrollRunnable.stop();
        }
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.RIGHT);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        int fromPosition = viewHolder.getAbsoluteAdapterPosition();
        int toPosition = target.getAbsoluteAdapterPosition();
        Log.d(NetworkTaskDragAndDropCallback.class.getName(), "moveItem, fromPosition is " + fromPosition + ", toPosition is " + toPosition);
        NetworkTaskHandler handler = new NetworkTaskHandler(mainActivity);
        return handler.moveNetworkTask(fromPosition, toPosition);
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        if (direction == ItemTouchHelper.RIGHT) {
            int position = viewHolder.getAbsoluteAdapterPosition();
            Log.d(NetworkTaskDragAndDropCallback.class.getName(), "onSwiped, position is " + position);
            mainActivity.onMainDeleteSwiped(position);
        }
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        mainActivity.getAdapter().notifyDataSetChanged();
    }
}
