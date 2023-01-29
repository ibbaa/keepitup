/*
 * Copyright (c) 2023. Alwin Ibba
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

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import net.ibbaa.keepitup.ui.dialog.PermissionExplainDialog;

public interface IPermissionManager {

    boolean shouldAskForRuntimePermission();

    boolean hasPostNotificationsPermission(Context context);

    void requestPostNotificationsPermission(FragmentActivity activity);

    void requestPermission(FragmentActivity activity, String permissions, int code);

    boolean hasPermission(Context context, String permission);

    void onRequestPermissionsResult(FragmentActivity activity, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);

    void onPermissionExplainDialogOkClicked(PermissionExplainDialog explainDialog, PermissionExplainDialog.Permission permission);
}
