/*
 * Copyright (c) 2024. Alwin Ibba
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

package net.ibbaa.keepitup.ui.mapping;

import android.content.Context;
import android.content.res.Resources;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.model.AccessType;
import net.ibbaa.keepitup.ui.dialog.ContextOption;
import net.ibbaa.keepitup.ui.validation.AccessTypeDataValidator;
import net.ibbaa.keepitup.ui.validation.NetworkTaskValidator;
import net.ibbaa.keepitup.ui.validation.NullAccessTypeDataValidator;
import net.ibbaa.keepitup.ui.validation.NullNetworkTaskValidator;

import java.lang.reflect.Constructor;

@SuppressWarnings("DiscouragedApi")
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

    public NetworkTaskValidator getNetworkTaskValidator(AccessType accessType) {
        Log.d(EnumMapping.class.getName(), "getNetworkTaskValidator for access type " + accessType);
        if (accessType == null) {
            Log.d(EnumMapping.class.getName(), "returning " + NullNetworkTaskValidator.class.getSimpleName());
            return new NullNetworkTaskValidator(getContext());
        }
        String validatorClassName = getResources().getString(getResources().getIdentifier(accessType.getClass().getSimpleName() + "_" + accessType.name() + "_task_validator", "string", context.getPackageName()));
        Log.d(EnumMapping.class.getName(), "specified validator class is " + validatorClassName);
        try {
            Class<?> validatorClass = getContext().getClassLoader().loadClass(validatorClassName);
            Constructor<?> validatorClassConstructor = validatorClass.getConstructor(Context.class);
            return (NetworkTaskValidator) validatorClassConstructor.newInstance(getContext());
        } catch (Throwable exc) {
            Log.e(EnumMapping.class.getName(), "Error instantiating validator class", exc);
        }
        Log.d(EnumMapping.class.getName(), "returning " + NullNetworkTaskValidator.class.getSimpleName());
        return new NullNetworkTaskValidator(getContext());
    }

    public AccessTypeDataValidator getAccessTypeDataValidator(AccessType accessType) {
        Log.d(EnumMapping.class.getName(), "getAccessTypeDataValidator for access type " + accessType);
        if (accessType == null) {
            Log.d(EnumMapping.class.getName(), "returning " + NullAccessTypeDataValidator.class.getSimpleName());
            return new NullAccessTypeDataValidator(getContext());
        }
        String validatorClassName = getResources().getString(getResources().getIdentifier(accessType.getClass().getSimpleName() + "_" + accessType.name() + "_data_validator", "string", context.getPackageName()));
        Log.d(EnumMapping.class.getName(), "specified validator class is " + validatorClassName);
        try {
            Class<?> validatorClass = getContext().getClassLoader().loadClass(validatorClassName);
            Constructor<?> validatorClassConstructor = validatorClass.getConstructor(Context.class);
            return (AccessTypeDataValidator) validatorClassConstructor.newInstance(getContext());
        } catch (Throwable exc) {
            Log.e(EnumMapping.class.getName(), "Error instantiating validator class", exc);
        }
        Log.d(EnumMapping.class.getName(), "returning " + NullAccessTypeDataValidator.class.getSimpleName());
        return new NullAccessTypeDataValidator(getContext());
    }

    public String getContextOptionName(ContextOption contextOption) {
        Log.d(EnumMapping.class.getName(), "getContextOptionName for context option " + contextOption);
        return getResources().getString(getResources().getIdentifier(contextOption.getClass().getSimpleName() + "_" + contextOption.name() + "_name", "string", context.getPackageName()));
    }

    private Context getContext() {
        return context;
    }

    private Resources getResources() {
        return getContext().getResources();
    }
}
