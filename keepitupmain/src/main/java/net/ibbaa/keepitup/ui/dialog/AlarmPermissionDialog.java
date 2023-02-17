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

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.ui.AlarmPermissionSupport;

public class AlarmPermissionDialog extends DialogFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(AlarmPermissionDialog.class.getName(), "onCreate");
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.DialogTheme);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(AlarmPermissionDialog.class.getName(), "onCreateView");
        View view = inflater.inflate(R.layout.dialog_alarm_permission, container);
        prepareOkImageButton(view);
        return view;
    }

    private void prepareOkImageButton(View view) {
        Log.d(AlarmPermissionDialog.class.getName(), "prepareOkImageButton");
        ImageView okImage = view.findViewById(R.id.imageview_dialog_alarm_permission_ok);
        okImage.setOnClickListener(this::onOkClicked);
    }

    private void onOkClicked(View view) {
        Log.d(AlarmPermissionDialog.class.getName(), "onOkClicked");
        AlarmPermissionSupport alarmPermissionSupport = getAlarmPermissionSupport();
        if (alarmPermissionSupport != null) {
            alarmPermissionSupport.onAlarmPermissionDialogOkClicked(this);
        } else {
            Log.e(SettingsInputDialog.class.getName(), "alarmPermissionSupport is null");
            dismiss();
        }
    }

    private AlarmPermissionSupport getAlarmPermissionSupport() {
        Log.d(AlarmPermissionDialog.class.getName(), "getAlarmPermissionSupport");
        Activity activity = getActivity();
        if (activity == null) {
            Log.e(AlarmPermissionDialog.class.getName(), "getAlarmPermissionSupport, activity is null");
            return null;
        }
        if (!(activity instanceof AlarmPermissionSupport)) {
            Log.e(AlarmPermissionDialog.class.getName(), "getAlarmPermissionSupport, activity is not an instance of " + AlarmPermissionSupport.class.getSimpleName());
            return null;
        }
        return (AlarmPermissionSupport) activity;
    }
}
