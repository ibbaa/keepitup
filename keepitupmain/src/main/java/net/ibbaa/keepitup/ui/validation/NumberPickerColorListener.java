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

package net.ibbaa.keepitup.ui.validation;

import android.os.Build;
import android.widget.NumberPicker;

import net.ibbaa.keepitup.logging.Log;

import java.util.Collections;
import java.util.List;

public class NumberPickerColorListener implements NumberPicker.OnValueChangeListener {

    private final List<NumberPicker> numberPickers;
    private final ValidatorPredicate<NumberPicker> validator;
    private final int color;
    private final int errorColor;

    public NumberPickerColorListener(List<NumberPicker> numberPickers, ValidatorPredicate<NumberPicker> validator, int color, int errorColor) {
        this.numberPickers = Collections.unmodifiableList(numberPickers);
        this.validator = validator;
        this.color = color;
        this.errorColor = errorColor;
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldValue, int newValue) {
        Log.d(NumberPickerColorListener.class.getName(), "onValueChange, oldValue = " + oldValue + ", newValue = " + newValue);
        if (validator.validate(picker)) {
            setColor(color);
        } else {
            setColor(errorColor);
        }
    }

    private void setColor(int colorToSet) {
        Log.d(NumberPickerColorListener.class.getName(), "setColor, colorToSet = " + colorToSet);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            for (NumberPicker numberPicker : numberPickers) {
                numberPicker.setTextColor(colorToSet);
            }
        }
    }
}
