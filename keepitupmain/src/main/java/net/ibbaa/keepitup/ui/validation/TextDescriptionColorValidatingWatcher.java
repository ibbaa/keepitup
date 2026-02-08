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

package net.ibbaa.keepitup.ui.validation;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.util.StringUtil;

@SuppressWarnings("ClassCanBeRecord")
public class TextDescriptionColorValidatingWatcher implements TextWatcher {

    private final EditText editText;
    private final TextView textView;
    private final ValidatorPredicate<EditText> validator;
    private final int okTextColor;
    private final int errorTextColor;

    public TextDescriptionColorValidatingWatcher(EditText editText, TextView textView, ValidatorPredicate<EditText> validator, int okTextColor, int errorTextColor) {
        this.editText = editText;
        this.textView = textView;
        this.validator = validator;
        this.okTextColor = okTextColor;
        this.errorTextColor = errorTextColor;
    }

    @Override
    public void beforeTextChanged(CharSequence seq, int start, int count, int after) {
        Log.d(TextDescriptionColorValidatingWatcher.class.getName(), "beforeTextChanged");
    }

    @Override
    public void onTextChanged(CharSequence seq, int start, int before, int count) {
        Log.d(TextDescriptionColorValidatingWatcher.class.getName(), "onTextChanged");
    }

    @Override
    public void afterTextChanged(Editable editable) {
        Log.d(TextDescriptionColorValidatingWatcher.class.getName(), "afterTextChanged");
        if (StringUtil.isEmpty(editText.getText())) {
            textView.setVisibility(View.GONE);
        } else if (validator.validate(editText)) {
            textView.setVisibility(View.VISIBLE);
            textView.setTextColor(okTextColor);
        } else {
            textView.setVisibility(View.VISIBLE);
            textView.setTextColor(errorTextColor);
        }
    }
}
