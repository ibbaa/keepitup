/*
 * Copyright (c) 2024. Alwin Ibba
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

package net.ibbaa.keepitup.ui.permission;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.ui.dialog.PermissionExplainDialog;
import net.ibbaa.keepitup.util.BundleUtil;

@SuppressWarnings({"unused"})
public class PermissionManager implements IPermissionManager {

    public boolean shouldAskForRuntimePermission() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public boolean hasPostNotificationsPermission(Context context) {
        Log.d(PermissionManager.class.getName(), "hasPostNotificationsPermission");
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return true;
        }
        String postNotificationsPermission = getPostNotificationsPermission(context);
        if (!hasPermission(context, postNotificationsPermission)) {
            Log.d(PermissionManager.class.getName(), "Permission " + postNotificationsPermission + " is not granted");
            return false;
        }
        Log.d(PermissionManager.class.getName(), "Permission " + postNotificationsPermission + " is granted");
        return true;
    }

    public void requestPostNotificationsPermission(FragmentActivity activity) {
        Log.d(PermissionManager.class.getName(), "requestPostNotificationsPermission");
        requestPermission(activity, getPostNotificationsPermission(activity), getPostNotificationsPermissionCode(activity));
    }

    public void requestPermission(FragmentActivity activity, String permission, int code) {
        Log.d(PermissionManager.class.getName(), "requestPermission " + permission + " with code " + code);
        ActivityCompat.requestPermissions(activity, new String[]{permission}, code);
    }

    public boolean hasPermission(Context context, String permission) {
        Log.d(PermissionManager.class.getName(), "hasPermission for permission " + permission);
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onRequestPermissionsResult(FragmentActivity activity, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(PermissionManager.class.getName(), "onRequestPermissionsResult for code " + requestCode);
        if (wasPermissionGranted(grantResults)) {
            Log.d(PermissionManager.class.getName(), "Permission for code " + requestCode + " was granted");
            return;
        }
        Log.d(PermissionManager.class.getName(), "Permission for code " + requestCode + " was not granted");
        if (requestCode == getPostNotificationsPermissionCode(activity)) {
            Log.d(PermissionManager.class.getName(), "Post notifications permission was requested");
            if (shouldShowPostNotificationsRationale(activity)) {
                Log.d(PermissionManager.class.getName(), "Showing permission explain dialog for post notifications");
                PermissionExplainDialog permissionExplainDialog = new PermissionExplainDialog();
                String message = activity.getResources().getString(R.string.text_dialog_permission_explain_post_notifications);
                PermissionExplainDialog.Permission permission = PermissionExplainDialog.Permission.POST_NOTIFICATIONS;
                Bundle bundle = BundleUtil.stringsToBundle(new String[]{permissionExplainDialog.getMessageKey(), PermissionExplainDialog.Permission.class.getSimpleName()}, new String[]{message, permission.name()});
                permissionExplainDialog.setArguments(bundle);
                permissionExplainDialog.show(activity.getSupportFragmentManager(), PermissionExplainDialog.class.getName());
            } else {
                Log.d(PermissionManager.class.getName(), "shouldShowPostNotificationsRationale returned false, not showing explain dialog again");
                Log.d(PermissionManager.class.getName(), "Post notifications permission was denied permanently");
            }
        }
    }

    public void onPermissionExplainDialogOkClicked(PermissionExplainDialog explainDialog, PermissionExplainDialog.Permission permission) {
        Log.d(PermissionManager.class.getName(), "onPermissionExplainDialogOkClicked for permission " + permission);
        if (PermissionExplainDialog.Permission.POST_NOTIFICATIONS.equals(permission)) {
            if (shouldShowPostNotificationsRationale(explainDialog.getActivity())) {
                Log.d(PermissionManager.class.getName(), "shouldShowPostNotificationsRationale returned true");
                Log.d(PermissionManager.class.getName(), "Requesting post notifications permission again");
                requestPostNotificationsPermission(explainDialog.getActivity());
            } else {
                Log.d(PermissionManager.class.getName(), "shouldShowExternalStorageRational returned false");
            }
        } else {
            Log.d(PermissionManager.class.getName(), "Unknown permission " + permission);
        }
        explainDialog.dismiss();
    }

    private boolean wasPermissionGranted(int[] grantResults) {
        Log.d(PermissionManager.class.getName(), "wasPermissionGranted");
        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private boolean shouldShowPostNotificationsRationale(FragmentActivity activity) {
        Log.d(PermissionManager.class.getName(), "shouldShowPostNotificationsRationale");
        String postNotificationsPermission = getPostNotificationsPermission(activity);
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, postNotificationsPermission)) {
            Log.d(PermissionManager.class.getName(), "shouldShowRequestPermissionRationale for " + postNotificationsPermission + " returned true");
            return true;
        }
        Log.d(PermissionManager.class.getName(), "shouldShowRequestPermissionRationale for " + postNotificationsPermission + " returned false");
        return false;
    }

    private String getPostNotificationsPermission(Context context) {
        return context.getResources().getString(R.string.permission_post_notifications);
    }

    private int getPostNotificationsPermissionCode(Context context) {
        return context.getResources().getInteger(R.integer.permission_post_notifications_code);
    }
}
