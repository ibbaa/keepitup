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

import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import net.ibbaa.keepitup.R;

public class PasswordToggleTouchListener implements View.OnTouchListener {

    private final EditText editText;

    private boolean visible;

    public PasswordToggleTouchListener(EditText editText) {
        this.editText = editText;
        this.visible = false;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
        applyVisibility();
    }

    private void applyVisibility() {
        if (visible) {
            editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility, 0);
        } else {
            editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility_off, 0);
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            final int drawableEnd = 2;
            if (editText.getCompoundDrawables()[drawableEnd] == null) {
                return false;
            }
            int iconStart = editText.getRight() - editText.getCompoundDrawables()[drawableEnd].getBounds().width() - editText.getPaddingEnd();
            if (event.getX() >= iconStart) {
                toggle();
                editText.performClick();
                return true;
            }
        }
        return false;
    }

    private void toggle() {
        visible = !visible;
        if (visible) {
            editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility, 0);
        } else {
            editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility_off, 0);
        }
        editText.setSelection(editText.getText().length());
    }
}