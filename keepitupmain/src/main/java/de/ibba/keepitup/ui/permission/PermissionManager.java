package de.ibba.keepitup.ui.permission;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import de.ibba.keepitup.R;
import de.ibba.keepitup.ui.dialog.PermissionExplainDialog;

public class PermissionManager {

    private final Activity activity;

    public PermissionManager(Activity activity) {
        this.activity = activity;
    }

    public boolean hasExternalStoragePermission() {
        Log.d(PermissionManager.class.getName(), "hasExternalStoragePermission");
        String[] externalStoragePermissions = getExternalStoragePermission();
        for (String permission : externalStoragePermissions) {
            if (!hasPermission(permission)) {
                Log.d(PermissionManager.class.getName(), "Permission " + permission + " is not granted");
                return false;
            }
            Log.d(PermissionManager.class.getName(), "Permission " + permission + " is granted");
        }
        Log.d(PermissionManager.class.getName(), "All external storage permissions granted");
        return true;
    }

    public void requestExternalStoragePermission() {
        Log.d(PermissionManager.class.getName(), "requestExternalStoragePermission");
        if (shouldShowExternalStorageRationale()) {
            Log.d(PermissionManager.class.getName(), "shouldShowExternalStorageRational returned true");
            requestPermission(getExternalStoragePermission(), getExternalStoragePermissionCode());
        } else {
            Log.d(PermissionManager.class.getName(), "shouldShowExternalStorageRational returned false");
        }
    }

    public void requestPermission(String[] permissions, int code) {
        Log.d(PermissionManager.class.getName(), "requestPermission for code " + code);
        ActivityCompat.requestPermissions(activity, permissions, code);
    }

    public boolean hasPermission(String permission) {
        Log.d(PermissionManager.class.getName(), "hasPermission for permission " + permission);
        return ContextCompat.checkSelfPermission(getContext(), permission) == PackageManager.PERMISSION_GRANTED;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(PermissionManager.class.getName(), "onRequestPermissionsResult for code " + requestCode);
        if (wasPermissionGranted(grantResults)) {
            Log.d(PermissionManager.class.getName(), "Permission for code " + requestCode + " was granted");
            return;
        }
        Log.d(PermissionManager.class.getName(), "Permission for code " + requestCode + " was not granted");
        if (requestCode == getExternalStoragePermissionCode()) {
            //Show external permission explain dialog
        }
    }

    public void onPermissionExplainDialogOkClicked(PermissionExplainDialog explainDialog, PermissionExplainDialog.Permission permission) {
        Log.d(PermissionManager.class.getName(), "onExternalPermissionExplainDialogOkClicked for permission " + permission);
        if (PermissionExplainDialog.Permission.EXTERNAL_STORAGE.equals(permission)) {
            if (shouldShowExternalStorageRationale()) {
                Log.d(PermissionManager.class.getName(), "shouldShowExternalStorageRationale returned true");
                Log.d(PermissionManager.class.getName(), "Requesting external storage permission again");
                requestExternalStoragePermission();
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

    private boolean shouldShowExternalStorageRationale() {
        Log.d(PermissionManager.class.getName(), "shouldShowExternalStorageRationale");
        String[] externalStoragePermissions = getExternalStoragePermission();
        for (String permission : externalStoragePermissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                Log.d(PermissionManager.class.getName(), "shouldShowRequestPermissionRationale for " + permission + " returned true");
                return true;
            }
            Log.d(PermissionManager.class.getName(), "shouldShowRequestPermissionRationale for " + permission + " returned false");
        }
        return false;
    }

    private String[] getExternalStoragePermission() {
        return new String[]{getResources().getString(R.string.permission_external_read), getResources().getString(R.string.permission_external_write)};
    }

    private int getExternalStoragePermissionCode() {
        return getResources().getInteger(R.integer.permission_external_code);
    }

    private Context getContext() {
        return activity;
    }

    private Resources getResources() {
        return getContext().getResources();
    }
}
