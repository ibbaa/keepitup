package de.ibba.keepitup.ui.validation;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

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
