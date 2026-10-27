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

package net.ibbaa.keepitup.test.mock;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import net.ibbaa.keepitup.ui.dialog.PermissionExplainDialog;
import net.ibbaa.keepitup.ui.permission.IPermissionManager;

@SuppressWarnings({"unused"})
public class MockPermissionManager implements IPermissionManager {

    private boolean shouldAskForRuntimePermission;
    private boolean hasPostNotificationsPermission;
    private boolean hasLocalNetworkPermission;
    private boolean hasPermission;
    private int requestPostNotificationsPermissionCalls;
    private int requestLocalNetworkPermissionCalls;
    private int requestPermissionCalls;
    private PermissionExplainDialog.Permission lastPermission;
    private PermissionExplainDialog.Permission lastCancelledPermission;

    public MockPermissionManager() {
        reset();
    }

    public void reset() {
        this.shouldAskForRuntimePermission = false;
        this.hasPostNotificationsPermission = true;
        this.hasLocalNetworkPermission = true;
        this.hasPermission = true;
        this.requestPostNotificationsPermissionCalls = 0;
        this.requestLocalNetworkPermissionCalls = 0;
        this.requestPermissionCalls = 0;
        lastPermission = null;
        lastCancelledPermission = null;
    }

    public void setShouldAskForRuntimePermission(boolean shouldAskForRuntimePermission) {
        this.shouldAskForRuntimePermission = shouldAskForRuntimePermission;
    }

    public void setHasPostNotificationsPermission(boolean hasPostNotificationsPermission) {
        this.hasPostNotificationsPermission = hasPostNotificationsPermission;
    }

    public void setHasLocalNetworkPermission(boolean hasLocalNetworkPermission) {
        this.hasLocalNetworkPermission = hasLocalNetworkPermission;
    }

    public void setHasPermission(boolean hasPermission) {
        this.hasPermission = hasPermission;
    }

    public int getRequestPostNotificationsPermissionCalls() {
        return requestPostNotificationsPermissionCalls;
    }

    public int getRequestLocalNetworkPermissionCalls() {
        return requestLocalNetworkPermissionCalls;
    }

    public int getRequestPermissionCalls() {
        return requestPermissionCalls;
    }

    public PermissionExplainDialog.Permission getLastPermission() {
        return lastPermission;
    }

    public PermissionExplainDialog.Permission getLastCancelledPermission() {
        return lastCancelledPermission;
    }

    @Override
    public boolean hasPostNotificationsPermission(Context context) {
        return hasPostNotificationsPermission;
    }

    @Override
    public void requestPostNotificationsPermission(FragmentActivity activity) {
        requestPostNotificationsPermissionCalls++;
    }

    @Override
    public boolean hasLocalNetworkPermission(Context context) {
        return hasLocalNetworkPermission;
    }

    @Override
    public void requestLocalNetworkPermission(FragmentActivity activity) {
        requestLocalNetworkPermissionCalls++;
    }

    @Override
    public void requestPermission(FragmentActivity activity, String permission, int code) {
        requestPermissionCalls++;
    }

    @Override
    public boolean hasPermission(Context context, String permission) {
        return hasPermission;
    }

    @Override
    public void onRequestPermissionsResult(FragmentActivity activity, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

    }

    @Override
    public void onPermissionExplainDialogOkClicked(PermissionExplainDialog explainDialog, PermissionExplainDialog.Permission permission) {
        lastPermission = permission;
    }

    @Override
    public void onPermissionExplainDialogCancelClicked(PermissionExplainDialog explainDialog, PermissionExplainDialog.Permission permission) {
        lastCancelledPermission = permission;
    }
}
