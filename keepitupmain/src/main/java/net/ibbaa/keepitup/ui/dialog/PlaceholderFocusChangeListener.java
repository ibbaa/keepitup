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

import android.view.View;
import android.widget.EditText;

import net.ibbaa.keepitup.util.StringUtil;

@SuppressWarnings("ClassCanBeRecord")
public class PlaceholderFocusChangeListener implements View.OnFocusChangeListener {

    private final String placeholder;
    private final EditText editText;

    public PlaceholderFocusChangeListener(EditText editText, String placeholder) {
        this.editText = editText;
        this.placeholder = placeholder;
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        if (hasFocus) {
            if (placeholder.equals(editText.getText().toString())) {
                editText.setText("");
            }
        } else {
            if (StringUtil.isEmpty(editText.getText())) {
                editText.setText(placeholder);
            }
        }
    }
}
