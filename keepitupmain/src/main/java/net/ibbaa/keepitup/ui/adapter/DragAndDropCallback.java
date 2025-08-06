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
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.ui.NetworkTaskHandler;
import net.ibbaa.keepitup.ui.NetworkTaskMainActivity;

@SuppressWarnings("NotifyDataSetChanged")
public class DragAndDropCallback extends ItemTouchHelper.Callback {

    private final NetworkTaskMainActivity mainActivity;

    public DragAndDropCallback(NetworkTaskMainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        int fromPosition = viewHolder.getAbsoluteAdapterPosition();
        int toPosition = target.getAbsoluteAdapterPosition();
        Log.d(DragAndDropCallback.class.getName(), "moveItem, fromPosition is " + fromPosition + ", toPosition is " + toPosition);
        NetworkTaskHandler handler = new NetworkTaskHandler(mainActivity);
        return handler.moveNetworkTask(fromPosition, toPosition);
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        mainActivity.getAdapter().notifyDataSetChanged();
    }
}
