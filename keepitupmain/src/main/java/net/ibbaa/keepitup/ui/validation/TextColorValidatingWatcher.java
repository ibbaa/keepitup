/*
 * Copyright (c) 2022. Alwin Ibba
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
import android.widget.EditText;

import net.ibbaa.keepitup.logging.Log;

public class TextColorValidatingWatcher implements TextWatcher {

    private final EditText editText;
    private final ValidatorPredicate<EditText> validator;
    private final int textColor;
    private final int errorTextColor;

    public TextColorValidatingWatcher(EditText editText, ValidatorPredicate<EditText> validator, int textColor, int errorTextColor) {
        this.editText = editText;
        this.validator = validator;
        this.textColor = textColor;
        this.errorTextColor = errorTextColor;
    }

    @Override
    public void beforeTextChanged(CharSequence seq, int start, int count, int after) {
        Log.d(TextColorValidatingWatcher.class.getName(), "beforeTextChanged");
    }

    @Override
    public void onTextChanged(CharSequence seq, int start, int before, int count) {
        Log.d(TextColorValidatingWatcher.class.getName(), "onTextChanged");
    }

    @Override
    public void afterTextChanged(Editable editable) {
        Log.d(TextColorValidatingWatcher.class.getName(), "afterTextChanged");
        if (validator.validate(editText)) {
            editText.setTextColor(textColor);
        } else {
            editText.setTextColor(errorTextColor);
        }
    }
}
