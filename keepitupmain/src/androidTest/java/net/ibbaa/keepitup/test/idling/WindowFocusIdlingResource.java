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

package net.ibbaa.keepitup.test.idling;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inspector.WindowInspector;

import androidx.test.espresso.IdlingResource;

import net.ibbaa.keepitup.logging.Log;

import java.lang.ref.WeakReference;
import java.util.List;

public class WindowFocusIdlingResource implements IdlingResource {

    private static final long FOCUS_TIMEOUT = 2000;
    private static final long POLL_INTERVAL = 25;

    private final Handler handler;
    private volatile ResourceCallback callback;
    private WeakReference<View> busyWindow;
    private long busySince;
    private boolean timedOut;
    private boolean pollScheduled;

    public WindowFocusIdlingResource() {
        this.handler = new Handler(Looper.getMainLooper());
        this.busyWindow = null;
        this.busySince = 0;
        this.timedOut = false;
        this.pollScheduled = false;
    }

    @Override
    public String getName() {
        return WindowFocusIdlingResource.class.getName();
    }

    @Override
    public boolean isIdleNow() {
        boolean idle = checkIdle();
        if (!idle) {
            schedulePoll();
        }
        return idle;
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        this.callback = callback;
    }

    public boolean checkIdle() {
        View topWindow;
        try {
            topWindow = getUnfocusedTopWindow();
        } catch (Exception exc) {
            topWindow = null;
        }
        if (topWindow == null) {
            busyWindow = null;
            busySince = 0;
            timedOut = false;
            return true;
        }
        long now = SystemClock.uptimeMillis();
        if (busyWindow == null || busyWindow.get() != topWindow) {
            busyWindow = new WeakReference<>(topWindow);
            busySince = now;
            timedOut = false;
            return false;
        }
        if (timedOut) {
            return true;
        }
        if (now - busySince >= FOCUS_TIMEOUT) {
            Log.d(WindowFocusIdlingResource.class.getName(), "Top window did not get focus within " + FOCUS_TIMEOUT + " ms");
            timedOut = true;
            return true;
        }
        return false;
    }

    private void schedulePoll() {
        if (pollScheduled) {
            return;
        }
        pollScheduled = true;
        handler.postDelayed(this::poll, POLL_INTERVAL);
    }

    private void poll() {
        pollScheduled = false;
        if (checkIdle()) {
            ResourceCallback currentCallback = callback;
            if (currentCallback != null) {
                currentCallback.onTransitionToIdle();
            }
        } else {
            schedulePoll();
        }
    }

    public static boolean isTopWindowFocused() {
        return getUnfocusedTopWindow() == null;
    }

    private static View getUnfocusedTopWindow() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return null;
        }
        List<View> windowViews = WindowInspector.getGlobalWindowViews();
        for (int ii = windowViews.size() - 1; ii >= 0; ii--) {
            View windowView = windowViews.get(ii);
            if (windowView.getVisibility() != View.VISIBLE || isNotFocusable(windowView)) {
                continue;
            }
            if (windowView.isAttachedToWindow() && windowView.hasWindowFocus()) {
                return null;
            }
            return windowView;
        }
        return null;
    }

    private static boolean isNotFocusable(View windowView) {
        ViewGroup.LayoutParams params = windowView.getLayoutParams();
        if (params instanceof WindowManager.LayoutParams windowParams) {
            return (windowParams.flags & WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE) != 0;
        }
        return false;
    }
}
