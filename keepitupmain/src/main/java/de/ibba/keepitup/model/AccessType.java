package de.ibba.keepitup.model;

import android.content.Context;
import android.content.res.Resources;

public enum AccessType {

    PING(1);

    private final int code;

    AccessType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public String getTypeText(Context context) {
        Resources resources = context.getResources();
        return resources.getString(resources.getIdentifier(this.getClass().getSimpleName() + "_" + this.name(), "string", context.getPackageName()));
    }

    public String getAddressText(Context context) {
        Resources resources = context.getResources();
        return resources.getString(resources.getIdentifier(this.getClass().getSimpleName() + "_" + this.name() + "_address", "string", context.getPackageName()));
    }

    public static AccessType forCode(int code) {
        AccessType[] values = AccessType.values();
        for (AccessType value : values) {
            if (code == value.getCode()) {
                return value;
            }
        }
        return null;
    }
}
