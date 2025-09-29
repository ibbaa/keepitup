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
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.util.StringUtil;
import net.ibbaa.keepitup.util.URLUtil;

public class NetworkTaskValidator {

    private final Context context;

    public NetworkTaskValidator(Context context) {
        this.context = context;
    }

    public boolean validate(NetworkTask task) {
        Log.d(NetworkTaskValidator.class.getName(), "validate task " + task);
        return validateName(task) && validateAccessType(task) && validateAddress(task) && validatePort(task) && validateInterval(task);
    }

    public boolean validateName(NetworkTask task) {
        Log.d(NetworkTaskValidator.class.getName(), "validateName of task " + task);
        String name = task.getName();
        if (!StringUtil.isEmpty(name)) {
            int nameMaxLength = context.getResources().getInteger(R.integer.task_name_max_length);
            if (name.length() > nameMaxLength) {
                Log.d(NetworkTaskValidator.class.getName(), "name is too long. Returning false.");
                return false;
            }
        }
        return true;
    }

    public boolean validateAccessType(NetworkTask task) {
        Log.d(NetworkTaskValidator.class.getName(), "validateAccessType of task " + task);
        if (task.getAccessType() == null) {
            Log.d(NetworkTaskValidator.class.getName(), "AccessType is null. Returning false.");
            return false;
        }
        Log.d(NetworkTaskValidator.class.getName(), "AccessType is valid. Returning true.");
        return true;
    }

    public boolean validateAddress(NetworkTask task) {
        Log.d(NetworkTaskValidator.class.getName(), "validateAddress of task " + task);
        String address = task.getAddress();
        if (address == null) {
            Log.d(NetworkTaskValidator.class.getName(), "address is null. Returning false.");
            return false;
        }
        boolean isValidAddress;
        if (task.getAccessType() == null) {
            isValidAddress = URLUtil.isValidIPAddress(address) || URLUtil.isValidHostName(address) || URLUtil.isValidURL(address);
        } else if (task.getAccessType().isDownload()) {
            isValidAddress = URLUtil.isValidURL(address);
        } else {
            isValidAddress = URLUtil.isValidIPAddress(address) || URLUtil.isValidHostName(address);
        }
        Log.d(NetworkTaskValidator.class.getName(), "isValidAddress is " + isValidAddress);
        return isValidAddress;
    }

    public boolean validatePort(NetworkTask task) {
        Log.d(NetworkTaskValidator.class.getName(), "validatePort of task " + task);
        int portMin = context.getResources().getInteger(R.integer.task_port_minimum);
        int portMax = context.getResources().getInteger(R.integer.task_port_maximum);
        if (task.getPort() < portMin || task.getPort() > portMax) {
            Log.d(NetworkTaskValidator.class.getName(), "port is invalid. Returning false.");
            return false;
        }
        Log.d(NetworkTaskValidator.class.getName(), "port is valid. Returning true.");
        return true;
    }

    public boolean validateInterval(NetworkTask task) {
        Log.d(NetworkTaskValidator.class.getName(), "validateInterval of task " + task);
        int intervalMin = context.getResources().getInteger(R.integer.task_interval_minimum);
        int intervalMax = context.getResources().getInteger(R.integer.task_interval_maximum);
        if (task.getInterval() < intervalMin || task.getInterval() > intervalMax) {
            Log.d(NetworkTaskValidator.class.getName(), "interval is invalid. Returning false.");
            return false;
        }
        Log.d(NetworkTaskValidator.class.getName(), "interval is valid. Returning true.");
        return true;
    }
}
