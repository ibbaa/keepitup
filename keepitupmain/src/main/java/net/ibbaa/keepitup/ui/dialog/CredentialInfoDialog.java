/*
 * Copyright (c) 2026 Alwin Ibba
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
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.ui.support.CredentialInfoSupport;
import net.ibbaa.keepitup.ui.validation.CredentialInfo;
import net.ibbaa.keepitup.util.BundleUtil;

import java.util.ArrayList;
import java.util.List;

public class CredentialInfoDialog extends GridBasedMessageDialogBase {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(CredentialInfoDialog.class.getName(), "onCreate");
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.DialogTheme);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(CredentialInfoDialog.class.getName(), "onCreateView");
        View view = inflater.inflate(R.layout.dialog_credential_info, container);
        initEdgeToEdgeInsets(view);
        List<CredentialInfo> resultList = BundleUtil.credentialInfoListFromBundle(getCredentialInfoBaseKey(), requireArguments());
        String title = BundleUtil.stringFromBundle(getTitleKey(), requireArguments());
        String message = BundleUtil.stringFromBundle(getMessageKey(), requireArguments());
        prepareErrorMessages(view, toGridMessageList(resultList));
        prepareTitle(view, title);
        prepareMessage(view, message);
        prepareOkButton(view, resultList.size() + 1);
        return view;
    }

    private void prepareTitle(View view, String title) {
        Log.d(CredentialInfoDialog.class.getName(), "prepareTitle");
        TextView titleView = view.findViewById(R.id.textview_dialog_credential_info_title);
        if (title == null) {
            title = getResources().getString(R.string.text_dialog_credential_info_title);
        }
        titleView.setText(title);
    }

    private void prepareMessage(View view, String message) {
        Log.d(CredentialInfoDialog.class.getName(), "prepareMessage");
        TextView messageView = view.findViewById(R.id.textview_dialog_credential_info_message);
        if (message == null) {
            message = getResources().getString(R.string.text_dialog_credential_info_message);
        }
        messageView.setText(message);
    }

    public String getCredentialInfoBaseKey() {
        return CredentialInfoDialog.class.getSimpleName() + ".CredentialInfo";
    }

    public String getTitleKey() {
        return CredentialInfoDialog.class.getSimpleName() + ".Title";
    }

    public String getMessageKey() {
        return CredentialInfoDialog.class.getSimpleName() + ".Message";
    }

    private List<GridMessage> toGridMessageList(List<CredentialInfo> resultList) {
        List<GridMessage> messages = new ArrayList<>(resultList.size());
        for (CredentialInfo currentResult : resultList) {
            messages.add(new GridMessage(currentResult.getName(), currentResult.getMessage()));
        }
        return messages;
    }

    @Override
    protected void onOkClicked(View view) {
        CredentialInfoSupport credentialInfoSupport = getCredentialInfoSupport();
        if (credentialInfoSupport == null) {
            dismiss();
        } else {
            credentialInfoSupport.onCredentialInfoDialogOkClicked(this);
        }
    }

    protected GridLayout getGridLayout(View view) {
        return view.findViewById(R.id.gridlayout_dialog_credential_info);
    }

    protected ImageView getOkImageView(View view) {
        return view.findViewById(R.id.imageview_dialog_credential_info_ok);
    }

    private CredentialInfoSupport getCredentialInfoSupport() {
        Log.d(CredentialInfoDialog.class.getName(), "getCredentialInfoSupport");
        List<Fragment> fragments = getParentFragmentManager().getFragments();
        for (Fragment fragment : fragments) {
            if (fragment instanceof CredentialInfoSupport) {
                return (CredentialInfoSupport) fragment;
            }
        }
        Activity activity = getActivity();
        if (activity == null) {
            Log.d(CredentialInfoDialog.class.getName(), "getCredentialInfoSupport, activity is null");
            return null;
        }
        if (!(activity instanceof CredentialInfoSupport)) {
            Log.d(CredentialInfoDialog.class.getName(), "getCredentialInfoSupport, activity is not an instance of " + CredentialInfoSupport.class.getSimpleName());
            return null;
        }
        return (CredentialInfoSupport) activity;
    }
}
