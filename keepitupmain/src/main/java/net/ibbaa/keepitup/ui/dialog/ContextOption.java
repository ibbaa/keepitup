package net.ibbaa.keepitup.ui.dialog;

import android.os.Bundle;

import net.ibbaa.keepitup.logging.Log;

public enum ContextOption {
    COPY,
    PASTE;

    public static ContextOption fromBundle(Bundle bundle) {
        if (bundle != null && bundle.containsKey("name")) {
            String name = bundle.getString("name");
            try {
                return ContextOption.valueOf(name);
            } catch (IllegalArgumentException exc) {
                Log.e(ContextOption.class.getName(), "Unknown context option " + name, exc);
            }
        }
        return null;
    }

    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putString("name", name());
        return bundle;
    }
}
