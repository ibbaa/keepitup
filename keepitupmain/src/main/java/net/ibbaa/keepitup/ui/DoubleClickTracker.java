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

package net.ibbaa.keepitup.ui;

import android.content.Context;

import net.ibbaa.keepitup.R;

public class DoubleClickTracker {

    private final int doubleClickInterval;
    private long lastClickTime;

    public DoubleClickTracker(Context context) {
        this.doubleClickInterval = context.getResources().getInteger(R.integer.doubleclick_interval);
        this.lastClickTime = 0;
    }

    public boolean isDoubleClick() {
        long currentTime = System.currentTimeMillis();
        boolean result = currentTime - lastClickTime < doubleClickInterval;
        lastClickTime = currentTime;
        return result;
    }
}
