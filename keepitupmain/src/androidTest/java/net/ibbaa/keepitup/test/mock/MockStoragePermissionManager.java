/*
 * Copyright (c) 2025 Alwin Ibba
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
import android.content.Intent;
import android.net.Uri;

import androidx.fragment.app.FragmentActivity;

import net.ibbaa.keepitup.ui.permission.IStoragePermissionManager;
import net.ibbaa.keepitup.ui.permission.PermissionLauncher;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings({"unused"})
public class MockStoragePermissionManager implements IStoragePermissionManager {

    private Set<String> folderPermissions;
    private Set<String> createFilePermissions;
    private Set<String> openFilePermissions;
    private String grantedFolder;
    private String grantedCreateFile;
    private String grantedOpenFile;

    public MockStoragePermissionManager() {
        this(null);
    }

    public MockStoragePermissionManager(String grantedFolder) {
        this(grantedFolder, null, null);
    }

    public MockStoragePermissionManager(String grantedFolder, String grantedCreateFile, String grantedOpenFile) {
        reset();
        this.grantedFolder = grantedFolder;
        this.grantedCreateFile = grantedCreateFile;
        this.grantedOpenFile = grantedOpenFile;
    }

    public void reset() {
        folderPermissions = new HashSet<>();
        createFilePermissions = new HashSet<>();
        openFilePermissions = new HashSet<>();
        grantedFolder = null;
        grantedCreateFile = null;
        grantedOpenFile = null;
    }

    public void setGrantedFolder(String grantedFolder) {
        this.grantedFolder = grantedFolder;
    }

    public void setGrantedCreateFile(String grantedCreateFile) {
        this.grantedCreateFile = grantedCreateFile;
    }

    public void setGrantedOpenFile(String grantedOpenFile) {
        this.grantedOpenFile = grantedOpenFile;
    }

    @Override
    public boolean hasPersistentPermission(Context context, String folder) {
        return folderPermissions.contains(folder);
    }

    @Override
    public boolean hasAnyPersistentPermission(Context context) {
        return !folderPermissions.isEmpty();
    }

    @Override
    public void requestPersistentFolderPermission(PermissionLauncher launcher, String folder) {
        String actualGrantedFolder = grantedFolder != null ? grantedFolder : folder;
        folderPermissions.add(actualGrantedFolder);
        Intent intent = new Intent();
        intent.setData(Uri.parse(actualGrantedFolder));
        if (launcher != null) {
            launcher.launch(intent);
        }
    }

    @Override
    public void requestCreateFilePermission(PermissionLauncher launcher, String fileName) {
        String actualGrantedCreateFile = grantedCreateFile != null ? grantedCreateFile : fileName;
        createFilePermissions.add(actualGrantedCreateFile);
        Intent intent = new Intent();
        intent.setData(Uri.parse(actualGrantedCreateFile));
        if (launcher != null) {
            launcher.launch(intent);
        }
    }

    @Override
    public void requestOpenFilePermission(PermissionLauncher launcher, String fullFilePath) {
        String actualGrantedOpenFile = grantedOpenFile != null ? grantedOpenFile : fullFilePath;
        openFilePermissions.add(actualGrantedOpenFile);
        Intent intent = new Intent();
        intent.setData(Uri.parse(actualGrantedOpenFile));
        if (launcher != null) {
            launcher.launch(intent);
        }
    }

    @Override
    public void revokePersistentPermission(FragmentActivity activity, String folder) {
        folderPermissions.remove(folder);
    }

    @Override
    public void revokeAllPersistentPermissions(FragmentActivity activity) {
        folderPermissions.clear();
    }

    @Override
    public void revokeOrphanPersistentPermissions(FragmentActivity activity, Set<String> usedFolders) {
        folderPermissions.retainAll(usedFolders);
    }

    public Set<String> getCreateFilePermissions() {
        return createFilePermissions;
    }

    public Set<String> getOpenFilePermissions() {
        return openFilePermissions;
    }

    public Set<String> getFolderPermissions() {
        return folderPermissions;
    }
}
