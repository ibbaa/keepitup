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

package net.ibbaa.keepitup.test.mock;

import android.content.Intent;
import android.net.Uri;

import androidx.activity.ComponentActivity;
import androidx.fragment.app.FragmentActivity;

import net.ibbaa.keepitup.ui.permission.FolderPermissionLauncher;
import net.ibbaa.keepitup.ui.permission.IFolderPermissionManager;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings({"unused"})
public class MockFolderPermissionManager implements IFolderPermissionManager {

    private Set<String> permissions;
    private String grantedFolder;

    public MockFolderPermissionManager() {
        reset();
    }

    public MockFolderPermissionManager(String grantedFolder) {
        reset();
        this.grantedFolder = grantedFolder;
    }

    public void reset() {
        permissions = new HashSet<>();
        grantedFolder = null;
    }

    public void setGrantedFolder(String grantedFolder) {
        this.grantedFolder = grantedFolder;
    }

    @Override
    public boolean hasPermission(ComponentActivity activity, String folder) {
        return permissions.contains(folder);
    }

    @Override
    public boolean hasAnyPermission(ComponentActivity activity) {
        return !permissions.isEmpty();
    }

    @Override
    public void requestPermission(ComponentActivity activity, FolderPermissionLauncher launcher, String folder) {
        String actualGrantedFolder = grantedFolder != null ? grantedFolder : folder;
        permissions.add(actualGrantedFolder);
        Intent intent = new Intent();
        intent.setData(Uri.parse(actualGrantedFolder));
        if (launcher != null) {
            launcher.launch(intent);
        }
    }

    @Override
    public void revokePermission(FragmentActivity activity, String folder) {
        permissions.remove(folder);
    }

    @Override
    public void revokeAllPermissions(FragmentActivity activity) {
        permissions.clear();
    }

    @Override
    public void revokeOrphanPermissions(FragmentActivity activity, Set<String> usedFolders) {
        permissions.retainAll(usedFolders);
    }
}
