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

import android.os.Handler;
import android.os.Looper;

import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import net.ibbaa.keepitup.logging.Log;

public class NestedScrollRunnable implements Runnable {

    private final NestedScrollView scrollView;
    private final int scrollThreshold;
    private final int scrollAmount;
    private final Handler handler = new Handler(Looper.getMainLooper());

    private RecyclerView.ViewHolder draggingViewHolder;
    private boolean isRunning;

    public NestedScrollRunnable(NestedScrollView scrollView, int scrollThreshold, int scrollAmount) {
        this.scrollView = scrollView;
        this.scrollThreshold = scrollThreshold;
        this.scrollAmount = scrollAmount;
        this.isRunning = false;
    }

    public void start(RecyclerView.ViewHolder viewHolder) {
        Log.d(NestedScrollRunnable.class.getName(), "run, isRunning is " + isRunning);
        this.draggingViewHolder = viewHolder;
        if (!isRunning) {
            isRunning = true;
            handler.post(this);
        }
    }

    public void stop() {
        Log.d(NestedScrollRunnable.class.getName(), "run, isRunning is " + isRunning);
        isRunning = false;
        draggingViewHolder = null;
        handler.removeCallbacks(this);
    }

    @Override
    public void run() {
        Log.d(NestedScrollRunnable.class.getName(), "run, isRunning is " + isRunning);
        if (!isRunning || draggingViewHolder == null) {
            return;
        }
        int[] itemLocation = new int[2];
        draggingViewHolder.itemView.getLocationOnScreen(itemLocation);
        int yCoord = itemLocation[1];
        int[] scrollViewLocation = new int[2];
        scrollView.getLocationOnScreen(scrollViewLocation);
        int top = scrollViewLocation[1];
        int bottom = top + scrollView.getHeight();
        if (yCoord < top + scrollThreshold) {
            scrollView.smoothScrollBy(0, -scrollAmount);
        } else if (yCoord + draggingViewHolder.itemView.getHeight() > bottom - scrollThreshold) {
            scrollView.smoothScrollBy(0, scrollAmount);
        }
        handler.postDelayed(this, 16);
    }
}