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

package net.ibbaa.keepitup.ui.dialog;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.ui.ConfirmSupport;
import net.ibbaa.keepitup.util.BundleUtil;
import net.ibbaa.keepitup.util.StringUtil;

import java.util.List;

@SuppressWarnings({"unused"})
public class ConfirmDialog extends DialogFragmentBase {

    public enum Type {
        DELETETASK,
        DELETETASKSWIPE,
        DELETELOGS,
        DELETEINTERVAL,
        RESETCONFIG,
        IMPORTCONFIG,
        EXPORTCONFIGEXISTINGFILE,
        DISMISSALARM
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(ConfirmDialog.class.getName(), "onCreate");
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.DialogTheme);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(ConfirmDialog.class.getName(), "onCreateView");
        View view = inflater.inflate(R.layout.dialog_confirm, container);
        initEdgeToEdgeInsets(view);
        String message = BundleUtil.stringFromBundle(getMessageKey(), requireArguments());
        String description = BundleUtil.stringFromBundle(getDescriptionKey(), requireArguments());
        prepareConfirmMessage(view, message);
        prepareConfirmDescription(view, description);
        prepareOkCancelImageButtons(view);
        return view;
    }

    public String getPositionKey() {
        return ConfirmDialog.class.getSimpleName() + "Position";
    }

    public String getExtraDataKey() {
        return ConfirmDialog.class.getSimpleName() + "ExtraData";
    }

    public String getMessageKey() {
        return ConfirmDialog.class.getSimpleName() + "Message";
    }

    public String getDescriptionKey() {
        return ConfirmDialog.class.getSimpleName() + "Description";
    }

    public String getTypeKey() {
        return ConfirmDialog.Type.class.getSimpleName();
    }

    public int getPosition() {
        return BundleUtil.integerFromBundle(getPositionKey(), requireArguments());
    }

    public Bundle getExtraData() {
        return BundleUtil.bundleFromBundle(getExtraDataKey(), requireArguments());
    }

    private void prepareConfirmMessage(View view, String message) {
        Log.d(ConfirmDialog.class.getName(), "prepareConfirmMessage, message is " + message);
        TextView messageText = view.findViewById(R.id.textview_dialog_confirm_message);
        messageText.setText(message);
    }

    private void prepareConfirmDescription(View view, String description) {
        Log.d(ConfirmDialog.class.getName(), "prepareConfirmDescription, description is " + description);
        TextView descriptionText = view.findViewById(R.id.textview_dialog_confirm_description);
        if (StringUtil.isEmpty(description)) {
            descriptionText.setText("");
            descriptionText.setVisibility(View.GONE);
        } else {
            descriptionText.setText(description);
            descriptionText.setVisibility(View.VISIBLE);
        }
    }

    private void prepareOkCancelImageButtons(View view) {
        Log.d(ConfirmDialog.class.getName(), "prepareOkCancelImageButtons");
        ImageView okImage = view.findViewById(R.id.imageview_dialog_confirm_ok);
        ImageView cancelImage = view.findViewById(R.id.imageview_dialog_confirm_cancel);
        okImage.setOnClickListener(this::onOkClicked);
        cancelImage.setOnClickListener(this::onCancelClicked);
    }

    private void onOkClicked(View view) {
        Log.d(ConfirmDialog.class.getName(), "onOkClicked");
        ConfirmSupport confirmSupport = getConfirmSupport();
        if (confirmSupport == null) {
            Log.e(ConfirmDialog.class.getName(), "confirmSupport is null");
            dismiss();
            return;
        }
        confirmSupport.onConfirmDialogOkClicked(this, getType());
    }

    private void onCancelClicked(View view) {
        Log.d(ConfirmDialog.class.getName(), "onCancelClicked");
        ConfirmSupport confirmSupport = getConfirmSupport();
        if (confirmSupport == null) {
            Log.e(ConfirmDialog.class.getName(), "confirmSupport is null");
            dismiss();
            return;
        }
        confirmSupport.onConfirmDialogCancelClicked(this, getType());
    }

    private Type getType() {
        Log.d(ConfirmDialog.class.getName(), "getType");
        String typeString = BundleUtil.stringFromBundle(getTypeKey(), requireArguments());
        Type type = null;
        if (!StringUtil.isEmpty(typeString)) {
            try {
                type = Type.valueOf(typeString);
            } catch (IllegalArgumentException exc) {
                Log.e(ConfirmDialog.class.getName(), Type.class.getSimpleName() + "." + typeString + " does not exist");
            }
        }
        Log.d(ConfirmDialog.class.getName(), "type is " + type);
        return type;
    }

    private ConfirmSupport getConfirmSupport() {
        Log.d(ConfirmDialog.class.getName(), "getConfirmSupport");
        List<Fragment> fragments = getParentFragmentManager().getFragments();
        for (Fragment fragment : fragments) {
            if (fragment instanceof ConfirmSupport) {
                return (ConfirmSupport) fragment;
            }
        }
        Log.d(ContextOptionsDialog.class.getName(), "getConfirmSupport, no parent fragment implementing " + ConfirmSupport.class.getSimpleName());
        Activity activity = getActivity();
        if (activity == null) {
            Log.e(ConfirmDialog.class.getName(), "getConfirmSupport, activity is null");
            return null;
        }
        if (!(activity instanceof ConfirmSupport)) {
            Log.e(ConfirmDialog.class.getName(), "getConfirmSupport, activity is not an instance of " + ConfirmSupport.class.getSimpleName());
            return null;
        }
        return (ConfirmSupport) activity;
    }
}
