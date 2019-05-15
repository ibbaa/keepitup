package de.ibba.keepitup.ui.dialog;

import android.os.Bundle;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class SettingsInput {

    private final String value;
    private final String field;
    private final List<String> validators;

    public SettingsInput(String value, String field, List<String> validators) {
        this.value = value;
        this.field = field;
        this.validators = validators;
    }

    public SettingsInput(Bundle bundle) {
        this.value = bundle.getString("value");
        this.field = bundle.getString("field");
        this.validators = bundle.getStringArrayList("validators");
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
        bundle.putString("value", value);
        bundle.putString("field", field);
        bundle.putStringArrayList("validators", validators == null ? null : new ArrayList<>(validators));
        return bundle;
    }

    @NonNull
    @Override
    public String toString() {
        return "SettingsInput{" +
                "value='" + value + '\'' +
                ", field='" + field + '\'' +
                ", validators=" + validators +
                '}';
    }
}
