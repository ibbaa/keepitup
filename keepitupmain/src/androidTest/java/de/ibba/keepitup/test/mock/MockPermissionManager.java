package de.ibba.keepitup.test.mock;

import androidx.annotation.NonNull;

import de.ibba.keepitup.permission.IPermissionManager;

public class MockPermissionManager implements IPermissionManager {

    private boolean shouldAskForRuntimePermission;
    private boolean hasExternalStoragePermission;
    private boolean hasPermission;
    private int requestExternalStoragePermissionCalls;
    private int requestPermissionCalls;

    public MockPermissionManager() {
        reset();
    }

    public void reset() {
        this.shouldAskForRuntimePermission = false;
        this.hasExternalStoragePermission = true;
        this.hasPermission = true;
        this.requestExternalStoragePermissionCalls = 0;
        this.requestPermissionCalls = 0;
    }

    public void setShouldAskForRuntimePermission(boolean shouldAskForRuntimePermission) {
        this.shouldAskForRuntimePermission = shouldAskForRuntimePermission;
    }

    public void setHasExternalStoragePermission(boolean hasExternalStoragePermission) {
        this.hasExternalStoragePermission = hasExternalStoragePermission;
    }

    public void setHasPermission(boolean hasPermission) {
        this.hasPermission = hasPermission;
    }

    public int getRequestExternalStoragePermissionCalls() {
        return requestExternalStoragePermissionCalls;
    }

    public int getRequestPermissionCalls() {
        return requestPermissionCalls;
    }

    @Override
    public boolean shouldAskForRuntimePermission() {
        return shouldAskForRuntimePermission;
    }

    @Override
    public boolean hasExternalStoragePermission() {
        return hasExternalStoragePermission;
    }

    @Override
    public void requestExternalStoragePermission() {
        requestExternalStoragePermissionCalls++;
    }

    @Override
    public void requestPermission(String[] permissions, int code) {
        requestPermissionCalls++;
    }

    @Override
    public boolean hasPermission(String permission) {
        return hasPermission;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

    }
}
