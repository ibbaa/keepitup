package de.ibba.keepitup.ui.dialog;

import android.os.Bundle;
import android.text.InputType;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class SettingsInput {

    public enum Type {
        ADDRESS(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_URI | InputType.TYPE_TEXT_FLAG_MULTI_LINE),
        PORT(InputType.TYPE_CLASS_NUMBER),
        INTERVAL(InputType.TYPE_CLASS_NUMBER),
        PINGCOUNT(InputType.TYPE_CLASS_NUMBER);

        private final int inputType;

        Type(int inputType) {
            this.inputType = inputType;
        }

        public int getInputType() {
            return inputType;
        }
    }

    private final Type type;
    private final String value;
    private final String field;
    private final List<String> validators;

    public SettingsInput(Type type, String value, String field, List<String> validators) {
        this.type = type;
        this.value = value;
        this.field = field;
        this.validators = validators;
    }

    public SettingsInput(Bundle bundle) {
        if (bundle.containsKey("type")) {
            type = Type.valueOf(bundle.getString("type"));
        } else {
            type = null;
        }
        this.value = bundle.getString("value");
        this.field = bundle.getString("field");
        this.validators = bundle.getStringArrayList("validators");
    }

    public Type getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public String getField() {
        return field;
    }

    public List<String> getValidators() {
        return validators;
    }

    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        if (type != null) {
            bundle.putString("type", type.name());
        }
        bundle.putString("value", value);
        bundle.putString("field", field);
        bundle.putStringArrayList("validators", validators == null ? null : new ArrayList<>(validators));
        return bundle;
    }

    @NonNull
    @Override
    public String toString() {
        return "SettingsInput{" +
                "type=" + type +
                ", value='" + value + '\'' +
                ", field='" + field + '\'' +
                ", validators=" + validators +
                '}';
    }
}
