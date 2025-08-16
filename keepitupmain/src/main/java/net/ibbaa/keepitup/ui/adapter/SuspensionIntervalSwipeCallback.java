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
import net.ibbaa.keepitup.ui.dialog.SuspensionIntervalsDialog;

@SuppressWarnings("NotifyDataSetChanged")
public class SuspensionIntervalSwipeCallback extends ItemTouchHelper.Callback {

    private final SuspensionIntervalsDialog dialog;

    public SuspensionIntervalSwipeCallback(SuspensionIntervalsDialog dialog) {
        this.dialog = dialog;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(0, ItemTouchHelper.RIGHT);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        if (direction == ItemTouchHelper.RIGHT) {
            int position = viewHolder.getAbsoluteAdapterPosition();
            Log.d(SuspensionIntervalSwipeCallback.class.getName(), "onSwiped, position is " + position);
            dialog.onIntervalDeleteSwiped(position);
        }
    }
}
