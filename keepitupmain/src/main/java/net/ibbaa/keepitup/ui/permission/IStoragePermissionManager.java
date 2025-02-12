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

package net.ibbaa.keepitup.ui.permission;

import android.content.Context;

import androidx.fragment.app.FragmentActivity;

import java.util.Set;

@SuppressWarnings({"unused"})
public interface IStoragePermissionManager {

    boolean hasPersistentPermission(Context context, String folder);

    boolean hasAnyPersistentPermission(Context context);

    void requestPersistentFolderPermission(PermissionLauncher launcher, String folder);

    void requestCreateFilePermission(PermissionLauncher launcher, String fileName);

    void requestOpenFilePermission(PermissionLauncher launcher, String fullFilePath);

    void revokePersistentPermission(FragmentActivity activity, String folder);

    void revokeAllPersistentPermissions(FragmentActivity activity);

    void revokeOrphanPersistentPermissions(FragmentActivity activity, Set<String> usedFolders);
}
