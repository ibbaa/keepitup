/*
 * Copyright (c) 2025 Alwin Ibba
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

package net.ibbaa.keepitup.model.validation;

import android.content.Context;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.model.Resolve;
import net.ibbaa.keepitup.util.StringUtil;
import net.ibbaa.keepitup.util.URLUtil;

public class ResolveValidator {

    private final Context context;

    public ResolveValidator(Context context) {
        this.context = context;
    }

    public boolean validate(Resolve resolve) {
        Log.d(ResolveValidator.class.getName(), "validate resolve object " + resolve);
        return validateTargetAddress(resolve) && validateTargetPort(resolve) && validateSourceAddress(resolve) && validateSourcePort(resolve);
    }

    public boolean validateSourceAddress(Resolve resolve) {
        Log.d(ResolveValidator.class.getName(), "validateSourceAddress of resolve object " + resolve);
        return validateAddress(resolve.getSourceAddress());
    }

    public boolean validateSourcePort(Resolve resolve) {
        Log.d(ResolveValidator.class.getName(), "validateSourcePort of resolve object " + resolve);
        return validatePort(resolve.getSourcePort());
    }

    public boolean validateTargetAddress(Resolve resolve) {
        Log.d(ResolveValidator.class.getName(), "validateTargetAddress of resolve object " + resolve);
        return validateAddress(resolve.getTargetAddress());
    }

    public boolean validateTargetPort(Resolve resolve) {
        Log.d(ResolveValidator.class.getName(), "validateTargetPort of resolve object " + resolve);
        return validatePort(resolve.getTargetPort());
    }

    public boolean validateAddress(String address) {
        Log.d(ResolveValidator.class.getName(), "validateAddress, address is " + address);
        if (StringUtil.isEmpty(address)) {
            Log.d(ResolveValidator.class.getName(), "address is null. Returning true.");
            return true;
        }
        boolean isValidAddress = URLUtil.isValidIPAddress(address) || URLUtil.isValidHostName(address);
        Log.d(ResolveValidator.class.getName(), "isValidAddress is " + isValidAddress);
        return isValidAddress;
    }

    private boolean validatePort(int port) {
        Log.d(ResolveValidator.class.getName(), "validatePort, port is " + port);
        if (port == -1) {
            Log.d(ResolveValidator.class.getName(), "port is -1. Returning true.");
            return true;
        }
        int portMin = context.getResources().getInteger(R.integer.resolve_port_minimum);
        int portMax = context.getResources().getInteger(R.integer.resolve_port_maximum);
        if (port < portMin || port > portMax) {
            Log.d(ResolveValidator.class.getName(), "port is invalid. Returning false.");
            return false;
        }
        Log.d(ResolveValidator.class.getName(), "port is valid. Returning true.");
        return true;
    }
}
