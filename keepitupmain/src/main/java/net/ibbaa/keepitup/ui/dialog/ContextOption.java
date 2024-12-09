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
