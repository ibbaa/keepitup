/*
 * Copyright (c) 2022. Alwin Ibba
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

package net.ibbaa.keepitup.service;

import androidx.appcompat.app.AppCompatDelegate;

import net.ibbaa.keepitup.logging.Log;

public class SystemThemeManager implements IThemeManager {

    @Override
    public void setThemeByCode(int code) {
        Log.d(SystemThemeManager.class.getName(), "setThemeByCode, code is " + code);
        AppCompatDelegate.setDefaultNightMode(code);
    }

    @Override
    public String getThemeName(int code) {
        Log.d(SystemThemeManager.class.getName(), "getThemeName, code is " + code);
        switch(code) {
            case AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM:
                return "SYSTEM";
            case AppCompatDelegate.MODE_NIGHT_NO:
                return "LIGHT";
            case AppCompatDelegate.MODE_NIGHT_YES:
                return "DARK";
        }
        return "UNDEFINED";
    }
}
