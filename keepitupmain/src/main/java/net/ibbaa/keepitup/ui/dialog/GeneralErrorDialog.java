/*
 * Copyright (c) 2021. Alwin Ibba
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

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.util.BundleUtil;

public class GeneralErrorDialog extends DialogFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(GeneralErrorDialog.class.getName(), "onCreate");
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.DialogTheme);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(GeneralErrorDialog.class.getName(), "onCreateView");
        View view = inflater.inflate(R.layout.dialog_general_error, container);
        String message = BundleUtil.stringFromBundle(getMessageKey(), requireArguments());
        prepareErrorMessage(view, message);
        prepareOkImageButton(view);
        return view;
    }

    public String getMessageKey() {
        return GeneralErrorDialog.class.getSimpleName() + "Message";
    }

    public String getTypefaceStyleKey() {
        return GeneralErrorDialog.class.getSimpleName() + "TypefaceStyle";
    }

    private void prepareErrorMessage(View view, String message) {
        Log.d(GeneralErrorDialog.class.getName(), "prepareErrorMessage");
        TextView messageText = view.findViewById(R.id.textview_dialog_general_error_message);
        int style = requireArguments().getInt(getTypefaceStyleKey(), Typeface.BOLD);
        messageText.setTypeface(null, style);
        messageText.setText(message);
    }

    private void prepareOkImageButton(View view) {
        Log.d(GeneralErrorDialog.class.getName(), "prepareOkImageButton");
        ImageView okImage = view.findViewById(R.id.imageview_dialog_general_error_ok);
        okImage.setOnClickListener(this::onOkClicked);
    }

    private void onOkClicked(View view) {
        Log.d(GeneralErrorDialog.class.getName(), "onOkClicked");
        dismiss();
    }
}
