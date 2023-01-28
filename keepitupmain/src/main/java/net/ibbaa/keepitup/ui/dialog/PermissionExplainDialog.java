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

package net.ibbaa.keepitup.ui.dialog;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.ui.permission.IPermissionManager;
import net.ibbaa.keepitup.ui.permission.PermissionManager;
import net.ibbaa.keepitup.util.BundleUtil;
import net.ibbaa.keepitup.util.StringUtil;

public class PermissionExplainDialog extends DialogFragment {

    public enum Permission {
        POST_NOTIFICATIONS
    }

    private IPermissionManager permissionManager;

    public void injectPermissionManager(IPermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }

    public IPermissionManager getPermissionManager() {
        if (permissionManager != null) {
            return permissionManager;
        }
        return new PermissionManager(requireActivity());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(PermissionExplainDialog.class.getName(), "onCreate");
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.DialogTheme);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(PermissionExplainDialog.class.getName(), "onCreateView");
        View view = inflater.inflate(R.layout.dialog_permission_explain, container);
        String message = BundleUtil.stringFromBundle(getMessageKey(), requireArguments());
        prepareExplainMessage(view, message);
        prepareOkImageButton(view);
        return view;
    }

    public String getMessageKey() {
        return PermissionExplainDialog.class.getSimpleName() + "Message";
    }

    private void prepareExplainMessage(View view, String message) {
        Log.d(PermissionExplainDialog.class.getName(), "prepareExplainMessage");
        TextView messageText = view.findViewById(R.id.textview_dialog_general_permission_explain_message);
        messageText.setText(message);
    }

    private void prepareOkImageButton(View view) {
        Log.d(PermissionExplainDialog.class.getName(), "prepareOkImageButton");
        ImageView okImage = view.findViewById(R.id.imageview_dialog_permission_explain_ok);
        okImage.setOnClickListener(this::onOkClicked);
    }

    private void onOkClicked(@SuppressWarnings("unused") View view) {
        Log.d(PermissionExplainDialog.class.getName(), "onOkClicked");
        String permissionString = BundleUtil.stringFromBundle(PermissionExplainDialog.Permission.class.getSimpleName(), requireArguments());
        if (StringUtil.isEmpty(permissionString)) {
            Log.e(PermissionExplainDialog.class.getName(), PermissionExplainDialog.Permission.class.getSimpleName() + " not specified.");
            getPermissionManager().onPermissionExplainDialogOkClicked(this, null);
            return;
        }
        Permission permission = null;
        try {
            permission = Permission.valueOf(permissionString);
        } catch (IllegalArgumentException exc) {
            Log.e(PermissionExplainDialog.class.getName(), PermissionExplainDialog.Permission.class.getSimpleName() + "." + permissionString + " does not exist");
        }
        getPermissionManager().onPermissionExplainDialogOkClicked(this, permission);
    }
}
