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

package net.ibbaa.keepitup.resources;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Log;

@SuppressWarnings("ClassCanBeRecord")
public class ConstantPreferenceManager {

    private final Context context;

    public ConstantPreferenceManager(Context context) {
        this.context = context;
    }

    public void removeAllPreferences() {
        Log.d(ConstantPreferenceManager.class.getName(), "removeAllPreferences");
        SharedPreferences.Editor editor = getDefaultSharedPreferencesEditor();
        editor.clear();
        editor.commit();
    }

    public void removePreferenceValue(String key) {
        Log.d(ConstantPreferenceManager.class.getName(), "removePreferenceValue, key is " + key);
        SharedPreferences.Editor editor = getDefaultSharedPreferencesEditor();
        editor.remove(key);
        editor.commit();
    }

    public String getPreferenceString(String key, String defaultValue) {
        Log.d(ConstantPreferenceManager.class.getName(), "getPreferenceString, key is " + key + ", default is " + defaultValue);
        String value = getDefaultSharedPreferences().getString(key, defaultValue);
        Log.d(ConstantPreferenceManager.class.getName(), "resolved value is " + value);
        return value;
    }

    public String getPreferenceHTTPUserAgent() {
        Log.d(ConstantPreferenceManager.class.getName(), "getPreferenceHTTPUserAgent");
        return getPreferenceString(getResources().getString(R.string.http_user_agent_key), getResources().getString(R.string.http_user_agent_default));
    }

    public void removePreferenceHTTPUserAgent() {
        Log.d(ConstantPreferenceManager.class.getName(), "removePreferenceHTTPUserAgent");
        removePreferenceValue(getResources().getString(R.string.http_user_agent_key));
    }

    private SharedPreferences getDefaultSharedPreferences() {
        return androidx.preference.PreferenceManager.getDefaultSharedPreferences(getContext());
    }

    private SharedPreferences.Editor getDefaultSharedPreferencesEditor() {
        return getDefaultSharedPreferences().edit();
    }

    private Context getContext() {
        return context;
    }

    private Resources getResources() {
        return getContext().getResources();
    }
}
