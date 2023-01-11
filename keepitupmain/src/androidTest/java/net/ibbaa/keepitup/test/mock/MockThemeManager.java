/*
 * Copyright (c) 2023. Alwin Ibba
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

package net.ibbaa.keepitup.test.mock;

import androidx.appcompat.app.AppCompatDelegate;

import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.service.IThemeManager;

public class MockThemeManager implements IThemeManager {

    private int code;

    public MockThemeManager() {
        this.code = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
    }

    @Override
    public void setThemeByCode(int code) {
        Log.d(MockThemeManager.class.getName(), "setThemeByCode, code is " + code);
        this.code = code;
    }

    public void reset() {
        this.code = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String getThemeName(int code) {
        Log.d(MockThemeManager.class.getName(), "getThemeName, code is " + code);
        switch (code) {
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
