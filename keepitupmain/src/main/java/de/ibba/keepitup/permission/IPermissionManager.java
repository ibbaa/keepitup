package de.ibba.keepitup.permission;

import androidx.annotation.NonNull;

public interface IPermissionManager {

    boolean shouldAskForRuntimePermission();

    boolean hasExternalStoragePermission();

    void requestExternalStoragePermission();

    void requestPermission(String[] permissions, int code);

    boolean hasPermission(String permission);

    void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);
}
