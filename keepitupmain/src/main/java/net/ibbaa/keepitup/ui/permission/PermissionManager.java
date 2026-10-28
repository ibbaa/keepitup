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

package net.ibbaa.keepitup.ui.permission;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.ui.dialog.PermissionExplainDialog;
import net.ibbaa.keepitup.ui.support.PermissionRequestSupport;
import net.ibbaa.keepitup.util.BundleUtil;

public class PermissionManager implements IPermissionManager {

    public boolean hasPostNotificationsPermission(Context context) {
        Log.d(PermissionManager.class.getName(), "hasPostNotificationsPermission");
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
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return;
        }
        requestPermission(activity, getPostNotificationsPermission(activity), getPostNotificationsPermissionCode(activity));
    }

    public boolean hasLocalNetworkPermission(Context context) {
        Log.d(PermissionManager.class.getName(), "hasLocalNetworkPermission");
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.CINNAMON_BUN) {
            Log.d(PermissionManager.class.getName(), "Local network permission is not required for API level " + Build.VERSION.SDK_INT);
            return true;
        }
        String localNetworkPermission = getLocalNetworkPermission(context);
        if (!hasPermission(context, localNetworkPermission)) {
            Log.d(PermissionManager.class.getName(), "Permission " + localNetworkPermission + " is not granted");
            return false;
        }
        Log.d(PermissionManager.class.getName(), "Permission " + localNetworkPermission + " is granted");
        return true;
    }

    public void requestLocalNetworkPermission(FragmentActivity activity) {
        Log.d(PermissionManager.class.getName(), "requestLocalNetworkPermission");
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.CINNAMON_BUN) {
            return;
        }
        requestPermission(activity, getLocalNetworkPermission(activity), getLocalNetworkPermissionCode(activity));
    }

    public void requestPermission(FragmentActivity activity, String permission, int code) {
        Log.d(PermissionManager.class.getName(), "requestPermission " + permission + " with code " + code);
        ActivityCompat.requestPermissions(activity, new String[]{permission}, code);
    }

    public boolean hasPermission(Context context, String permission) {
        Log.d(PermissionManager.class.getName(), "hasPermission for permission " + permission);
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public void onRequestPermissionsResult(FragmentActivity activity, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(PermissionManager.class.getName(), "onRequestPermissionsResult for code " + requestCode);
        PermissionExplainDialog.Permission permission = getPermissionForCode(activity, requestCode);
        if (permission == null) {
            Log.d(PermissionManager.class.getName(), "Unknown permission code " + requestCode);
            return;
        }
        if (wasPermissionGranted(grantResults)) {
            Log.d(PermissionManager.class.getName(), "Permission " + permission + " was granted");
            notifyPermissionRequestFinished(activity, permission);
            return;
        }
        Log.d(PermissionManager.class.getName(), "Permission " + permission + " was not granted");
        if (shouldShowRationale(activity, permission)) {
            Log.d(PermissionManager.class.getName(), "Showing permission explain dialog for " + permission);
            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            String tag = PermissionExplainDialog.class.getName();
            if (fragmentManager.findFragmentByTag(tag) == null) {
                PermissionExplainDialog permissionExplainDialog = new PermissionExplainDialog();
                String message = getExplainMessage(activity, permission);
                Bundle bundle = BundleUtil.stringsToBundle(new String[]{permissionExplainDialog.getMessageKey(), PermissionExplainDialog.Permission.class.getSimpleName()}, new String[]{message, permission.name()});
                permissionExplainDialog.setArguments(bundle);
                permissionExplainDialog.show(fragmentManager, tag);
            }
        } else {
            Log.d(PermissionManager.class.getName(), "shouldShowRationale returned false, not showing explain dialog again");
            Log.d(PermissionManager.class.getName(), "Permission " + permission + " was denied permanently");
            notifyPermissionRequestFinished(activity, permission);
        }
    }

    public void onPermissionExplainDialogOkClicked(PermissionExplainDialog explainDialog, PermissionExplainDialog.Permission permission) {
        Log.d(PermissionManager.class.getName(), "onPermissionExplainDialogOkClicked for permission " + permission);
        FragmentActivity activity = explainDialog.getActivity();
        if (permission == null) {
            Log.d(PermissionManager.class.getName(), "Permission is null");
        } else if (shouldShowRationale(activity, permission)) {
            Log.d(PermissionManager.class.getName(), "shouldShowRationale returned true");
            Log.d(PermissionManager.class.getName(), "Requesting permission " + permission + " again");
            requestPermission(activity, permission);
        } else {
            Log.d(PermissionManager.class.getName(), "shouldShowRationale returned false");
            notifyPermissionRequestFinished(activity, permission);
        }
        explainDialog.dismiss();
    }

    @Override
    public void onPermissionExplainDialogCancelClicked(PermissionExplainDialog explainDialog, PermissionExplainDialog.Permission permission) {
        Log.d(PermissionManager.class.getName(), "onPermissionExplainDialogCancelClicked for permission " + permission);
        if (permission != null) {
            notifyPermissionRequestFinished(explainDialog.getActivity(), permission);
        }
    }

    private void requestPermission(FragmentActivity activity, PermissionExplainDialog.Permission permission) {
        if (PermissionExplainDialog.Permission.POST_NOTIFICATIONS.equals(permission)) {
            requestPostNotificationsPermission(activity);
        } else if (PermissionExplainDialog.Permission.LOCAL_NETWORK.equals(permission)) {
            requestLocalNetworkPermission(activity);
        }
    }

    private void notifyPermissionRequestFinished(FragmentActivity activity, PermissionExplainDialog.Permission permission) {
        Log.d(PermissionManager.class.getName(), "notifyPermissionRequestFinished for permission " + permission);
        if (activity instanceof PermissionRequestSupport) {
            ((PermissionRequestSupport) activity).onPermissionRequestFinished(permission);
        } else {
            Log.d(PermissionManager.class.getName(), "Activity does not implement " + PermissionRequestSupport.class.getSimpleName());
        }
    }

    private boolean wasPermissionGranted(int[] grantResults) {
        Log.d(PermissionManager.class.getName(), "wasPermissionGranted");
        if (grantResults.length == 0) {
            Log.d(PermissionManager.class.getName(), "grantResults is empty");
            return false;
        }
        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private boolean shouldShowRationale(FragmentActivity activity, PermissionExplainDialog.Permission permission) {
        Log.d(PermissionManager.class.getName(), "shouldShowRationale for permission " + permission);
        if (activity == null) {
            Log.d(PermissionManager.class.getName(), "activity is null");
            return false;
        }
        String permissionString = getPermissionString(activity, permission);
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permissionString)) {
            Log.d(PermissionManager.class.getName(), "shouldShowRequestPermissionRationale for " + permissionString + " returned true");
            return true;
        }
        Log.d(PermissionManager.class.getName(), "shouldShowRequestPermissionRationale for " + permissionString + " returned false");
        return false;
    }

    private PermissionExplainDialog.Permission getPermissionForCode(Context context, int requestCode) {
        if (requestCode == getPostNotificationsPermissionCode(context)) {
            return PermissionExplainDialog.Permission.POST_NOTIFICATIONS;
        }
        if (requestCode == getLocalNetworkPermissionCode(context)) {
            return PermissionExplainDialog.Permission.LOCAL_NETWORK;
        }
        return null;
    }

    private String getPermissionString(Context context, PermissionExplainDialog.Permission permission) {
        if (PermissionExplainDialog.Permission.LOCAL_NETWORK.equals(permission)) {
            return getLocalNetworkPermission(context);
        }
        return getPostNotificationsPermission(context);
    }

    private String getExplainMessage(Context context, PermissionExplainDialog.Permission permission) {
        if (PermissionExplainDialog.Permission.LOCAL_NETWORK.equals(permission)) {
            return context.getResources().getString(R.string.text_dialog_permission_explain_local_network);
        }
        return context.getResources().getString(R.string.text_dialog_permission_explain_post_notifications);
    }

    private String getPostNotificationsPermission(Context context) {
        return context.getResources().getString(R.string.permission_post_notifications);
    }

    private int getPostNotificationsPermissionCode(Context context) {
        return context.getResources().getInteger(R.integer.permission_post_notifications_code);
    }

    private String getLocalNetworkPermission(Context context) {
        return context.getResources().getString(R.string.permission_local_network);
    }

    private int getLocalNetworkPermissionCode(Context context) {
        return context.getResources().getInteger(R.integer.permission_local_network_code);
    }
}
