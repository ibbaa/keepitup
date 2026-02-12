/*
 * Copyright (c) 2026 Alwin Ibba
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

package net.ibbaa.keepitup.test.viewaction;

import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;

import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;

import org.hamcrest.Matcher;

public class TogglePasswordViewAction implements ViewAction {
    @Override
    public Matcher<View> getConstraints() {
        return isAssignableFrom(EditText.class);
    }

    @Override
    public String getDescription() {
        return "Click drawable end";
    }

    @Override
    public void perform(UiController uiController, View view) {
        int[] screenPos = new int[2];
        view.getLocationOnScreen(screenPos);
        float x = view.getWidth() - view.getPaddingEnd() - 10;
        float y = view.getHeight() / 2f;
        MotionEvent down = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, x, y, 0);
        MotionEvent up = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, x, y, 0);
        view.dispatchTouchEvent(down);
        view.dispatchTouchEvent(up);
        down.recycle();
        up.recycle();
    }
}
