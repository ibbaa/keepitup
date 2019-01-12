package de.ibba.keepitup.ui.mapping;

import android.content.Context;
import android.content.res.Resources;

import de.ibba.keepitup.R;
import de.ibba.keepitup.model.AccessType;

public class EnumMapping {

    private final Context context;

    public EnumMapping(Context context) {
        this.context = context;
    }

    public String getAccessTypeText(AccessType accessType) {
        if (accessType == null) {
            return getResources().getString(R.string.AccessType_NULL);
        }
        return getResources().getString(getResources().getIdentifier(accessType.getClass().getSimpleName() + "_" + accessType.name(), "string", context.getPackageName()));
    }

    public String getAccessTypeAddressText(AccessType accessType) {
        if (accessType == null) {
            return getResources().getString(R.string.AccessType_NULL_address);
        }
        return getResources().getString(getResources().getIdentifier(accessType.getClass().getSimpleName() + "_" + accessType.name() + "_address", "string", context.getPackageName()));
    }

    private Context getContext() {
        return context;
    }

    private Resources getResources() {
        return getContext().getResources();
    }
}