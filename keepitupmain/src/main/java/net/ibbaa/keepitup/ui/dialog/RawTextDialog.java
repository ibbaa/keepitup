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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.google.common.base.Charsets;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.util.StreamUtil;

import java.io.IOException;
import java.io.InputStream;

public class RawTextDialog extends DialogFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(RawTextDialog.class.getName(), "onCreate");
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.DialogTheme);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(RawTextDialog.class.getName(), "onCreateView");
        View view = inflater.inflate(R.layout.dialog_raw_text, container);
        prepareContent(view);
        prepareOkImageButton(view);
        return view;
    }

    public String getResourceIdKey() {
        return RawTextDialog.class.getSimpleName() + "ResourceId";
    }

    private void prepareContent(View view) {
        Log.d(RawTextDialog.class.getName(), "prepareContent");
        TextView messageText = view.findViewById(R.id.textview_dialog_raw_text_content);
        Bundle arguments = requireArguments();
        int textResourceId = arguments.getInt(getResourceIdKey());
        InputStream inputStream = null;
        try {
            Log.d(RawTextDialog.class.getName(), "Reading text");
            inputStream = getResources().openRawResource(textResourceId);
            String text = StreamUtil.inputStreamToString(inputStream, Charsets.UTF_8);
            Log.d(RawTextDialog.class.getName(), "Raw text is " + text);
            text = doReplacements(arguments, text);
            Log.d(RawTextDialog.class.getName(), "Text with applied replacements is " + text);
            messageText.setText(text);
        } catch (Exception exc) {
            Log.e(RawTextDialog.class.getName(), "Error while reading text from resource.", exc);
            messageText.setText(getResources().getString(R.string.text_dialog_raw_text_fatal_error));
        } finally {
            if (inputStream != null) {
                Log.d(RawTextDialog.class.getName(), "Closing text stream.");
                try {
                    inputStream.close();
                } catch (IOException exc) {
                    Log.e(RawTextDialog.class.getName(), "Error while closing text stream.", exc);
                }
            }
        }
    }

    private String doReplacements(Bundle arguments, String text) {
        for (String key : arguments.keySet()) {
            String replacement = "@" + key + "@";
            Object value = arguments.get(key);
            if (value instanceof String) {
                if (text.contains(replacement)) {
                    text = text.replaceAll(replacement, value.toString());
                }
            }
        }
        return text;
    }

    private void prepareOkImageButton(View view) {
        Log.d(RawTextDialog.class.getName(), "prepareOkImageButton");
        ImageView okImage = view.findViewById(R.id.imageview_dialog_raw_text_ok);
        okImage.setOnClickListener(this::onOkClicked);
    }

    private void onOkClicked(View view) {
        Log.d(RawTextDialog.class.getName(), "onOkClicked");
        dismiss();
    }
}
