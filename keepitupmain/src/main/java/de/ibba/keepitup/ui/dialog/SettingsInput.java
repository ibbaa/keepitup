package de.ibba.keepitup.ui.dialog;

import android.os.Bundle;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class SettingsInput {

    private final String value;
    private final List<String> validators;

    public SettingsInput(String value, List<String> validators) {
        this.value = value;
        this.validators = validators;
    }

    public SettingsInput(Bundle bundle) {
        this.value = bundle.getString("value");
        this.validators = bundle.getStringArrayList("validators");
    }

    public String getValue() {
        return value;
    }

    public List<String> getValidators() {
        return validators;
    }

    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putString("value", value);
        bundle.putStringArrayList("validators", validators == null ? null : new ArrayList<>(validators));
        return bundle;
    }

    @NonNull
    @Override
    public String toString() {
        return "SettingsInput{" +
                "value='" + value + '\'' +
                ", validators=" + validators +
                '}';
    }
}
