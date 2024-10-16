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
import android.content.Intent;
import android.content.UriPermission;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;

import androidx.fragment.app.FragmentActivity;

import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.util.StringUtil;

import java.util.List;
import java.util.Set;

public class StoragePermissionManager implements IStoragePermissionManager {

    private final String mimeType;

    public StoragePermissionManager() {
        this("application/json");
    }

    public StoragePermissionManager(String mimeType) {
        this.mimeType = mimeType;
    }

    public boolean hasPersistentPermission(Context context, String folder) {
        Log.d(StoragePermissionManager.class.getName(), "hasPermission for folder " + folder);
        if (StringUtil.isEmpty(folder)) {
            return false;
        }
        List<UriPermission> permissions = getPermissions(context);
        for (UriPermission permission : permissions) {
            if (folder.equals(permission.getUri().toString()) && permission.isReadPermission() && permission.isWritePermission()) {
                return true;
            }
        }
        return false;
    }

    public boolean hasAnyPersistentPermission(Context context) {
        Log.d(StoragePermissionManager.class.getName(), "hasAnyPermission");
        List<UriPermission> permissions = getPermissions(context);
        return !permissions.isEmpty();
    }

    public void requestPersistentFolderPermission(PermissionLauncher launcher, String folder) {
        Log.d(StoragePermissionManager.class.getName(), "requestPermission for folder " + folder);
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !StringUtil.isEmpty(folder)) {
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, folder);
        }
        launcher.launch(intent);
    }

    public void requestCreateFilePermission(PermissionLauncher launcher, String fileName) {
        requestFilePermission(launcher, Intent.ACTION_CREATE_DOCUMENT, fileName, null);
    }

    public void requestOpenFilePermission(PermissionLauncher launcher, String fullFilePath) {
        requestFilePermission(launcher, Intent.ACTION_OPEN_DOCUMENT, null, fullFilePath);
    }

    private void requestFilePermission(PermissionLauncher launcher, String action, String fileName, String fullFilePath) {
        Log.d(StoragePermissionManager.class.getName(), "requestFilePermission for fileName " + fileName + " and fullFilePath " + fullFilePath);
        Intent intent = new Intent(action);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType(mimeType);
        if (!StringUtil.isEmpty(fileName)) {
            intent.putExtra(Intent.EXTRA_TITLE, fileName);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !StringUtil.isEmpty(fullFilePath)) {
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, fullFilePath);
        }
        launcher.launch(intent);
    }

    public void revokePersistentPermission(FragmentActivity activity, String folder) {
        Log.d(StoragePermissionManager.class.getName(), "revokePermission for folder " + folder);
        if (StringUtil.isEmpty(folder)) {
            return;
        }
        if (!hasPersistentPermission(activity, folder)) {
            Log.d(StoragePermissionManager.class.getName(), "No permission for folder " + folder + ". Skipping revoke.");
            return;
        }
        try {
            Uri folderUri = Uri.parse(folder);
            activity.getContentResolver().releasePersistableUriPermission(folderUri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            activity.revokeUriPermission(folderUri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } catch (Exception exc) {
            Log.e(StoragePermissionManager.class.getName(), "Error parsing folder uri " + folder, exc);
        }
    }

    public void revokeAllPersistentPermissions(FragmentActivity activity) {
        Log.d(StoragePermissionManager.class.getName(), "revokeAllPermissions");
        List<UriPermission> permissions = getPermissions(activity);
        for (UriPermission permission : permissions) {
            String currentPermission = permission.getUri().toString();
            revokePersistentPermission(activity, currentPermission);
        }
    }

    public void revokeOrphanPersistentPermissions(FragmentActivity activity, Set<String> usedFolders) {
        Log.d(StoragePermissionManager.class.getName(), "revokeOrphanPermissions");
        List<UriPermission> permissions = getPermissions(activity);
        for (UriPermission permission : permissions) {
            String currentPermission = permission.getUri().toString();
            if (!usedFolders.contains(currentPermission)) {
                revokePersistentPermission(activity, currentPermission);
            }
        }
    }

    private List<UriPermission> getPermissions(Context context) {
        Log.d(StoragePermissionManager.class.getName(), "getPermissions");
        return context.getContentResolver().getPersistedUriPermissions();
    }
}
