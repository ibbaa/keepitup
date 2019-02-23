package de.ibba.keepitup.ui.mapping;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import java.lang.reflect.Constructor;

import de.ibba.keepitup.R;
import de.ibba.keepitup.model.AccessType;
import de.ibba.keepitup.ui.validation.NullValidator;
import de.ibba.keepitup.ui.validation.Validator;

public class EnumMapping {

    private final Context context;

    public EnumMapping(Context context) {
        this.context = context;
    }

    public String getAccessTypeText(AccessType accessType) {
        Log.d(EnumMapping.class.getName(), "getAccessTypeText for access type " + accessType);
        if (accessType == null) {
            return getResources().getString(R.string.AccessType_NULL);
        }
        return getResources().getString(getResources().getIdentifier(accessType.getClass().getSimpleName() + "_" + accessType.name(), "string", context.getPackageName()));
    }

    public String getAccessTypeAddressText(AccessType accessType) {
        Log.d(EnumMapping.class.getName(), "getAccessTypeAddressText for access type " + accessType);
        if (accessType == null) {
            return getResources().getString(R.string.AccessType_NULL_address);
        }
        String address = getAccessTypeAddressLabel(accessType) + " %s";
        if (accessType.needsPort()) {
            address += " " + getAccessTypePortLabel(accessType) + " %d";
        }
        return address;
    }

    public String getAccessTypeAddressLabel(AccessType accessType) {
        Log.d(EnumMapping.class.getName(), "getAccessTypeAddressLabel for access type " + accessType);
        return getResources().getString(getResources().getIdentifier(accessType.getClass().getSimpleName() + "_" + accessType.name() + "_address", "string", context.getPackageName()));
    }

    public String getAccessTypePortLabel(AccessType accessType) {
        Log.d(EnumMapping.class.getName(), "getAccessTypePortLabel for access type " + accessType);
        return getResources().getString(getResources().getIdentifier(accessType.getClass().getSimpleName() + "_" + accessType.name() + "_port", "string", context.getPackageName()));
    }

    public Validator getValidator(AccessType accessType) {
        Log.d(EnumMapping.class.getName(), "getValidator for access type " + accessType);
        if (accessType == null) {
            Log.d(EnumMapping.class.getName(), "returning NullValidator");
            return new NullValidator(getContext());
        }
        String validatorClassName = getResources().getString(getResources().getIdentifier(accessType.getClass().getSimpleName() + "_" + accessType.name() + "_validator", "string", context.getPackageName()));
        Log.d(EnumMapping.class.getName(), "specified validator class is " + validatorClassName);
        try {
            Class<?> validatorClass = getContext().getClassLoader().loadClass(validatorClassName);
            Constructor<?> validatorClassConstructor = validatorClass.getConstructor(Context.class);
            return (Validator) validatorClassConstructor.newInstance(getContext());
        } catch (Throwable exc) {
            Log.e(EnumMapping.class.getName(), "Error instantiating validator class", exc);
        }
        Log.d(EnumMapping.class.getName(), "returning NullValidator");
        return new NullValidator(getContext());
    }

    private Context getContext() {
        return context;
    }

    private Resources getResources() {
        return getContext().getResources();
    }
}
