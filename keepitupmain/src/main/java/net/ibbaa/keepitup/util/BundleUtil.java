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

package net.ibbaa.keepitup.util;

import android.os.Bundle;

import net.ibbaa.keepitup.model.FileEntry;
import net.ibbaa.keepitup.model.Interval;
import net.ibbaa.keepitup.ui.dialog.ContextOption;
import net.ibbaa.keepitup.ui.validation.ValidationResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BundleUtil {

    public static Bundle stringToBundle(String key, String text) {
        return stringToBundle(key, text, new Bundle());
    }

    public static Bundle stringToBundle(String key, String text, Bundle bundle) {
        if (bundle == null) {
            bundle = new Bundle();
        }
        if (key == null || text == null) {
            return bundle;
        }
        bundle.putString(key, text);
        return bundle;
    }

    public static Bundle stringsToBundle(String[] keys, String[] texts) {
        Bundle bundle = new Bundle();
        if (keys == null || texts == null) {
            return bundle;
        }
        for (int ii = 0; ii < keys.length; ii++) {
            if (ii < texts.length) {
                bundle.putString(keys[ii], texts[ii]);
            }
        }
        return bundle;
    }

    public static String stringFromBundle(String key, Bundle bundle) {
        if (bundle == null || key == null) {
            return null;
        }
        return bundle.getString(key);
    }

    public static Bundle stringListToBundle(String baseKey, List<String> list) {
        Bundle bundle = new Bundle();
        if (baseKey == null || list == null) {
            return bundle;
        }
        for (int ii = 0; ii < list.size(); ii++) {
            bundle.putString(baseKey + ii, list.get(ii));
        }
        return bundle;
    }

    public static List<String> stringListFromBundle(String baseKey, Bundle bundle) {
        if (baseKey == null || bundle == null) {
            return Collections.emptyList();
        }
        List<String> resultList = new ArrayList<>();
        for (int ii = 0; ii < bundle.size(); ii++) {
            String currentString = bundle.getString(baseKey + ii);
            if (currentString != null) {
                resultList.add(currentString);
            }
        }
        return resultList;
    }

    public static Bundle booleanToBundle(String key, boolean value) {
        return booleanToBundle(key, value, new Bundle());
    }

    public static Bundle booleanToBundle(String key, boolean value, Bundle bundle) {
        if (bundle == null) {
            bundle = new Bundle();
        }
        if (key == null) {
            return bundle;
        }
        bundle.putBoolean(key, value);
        return bundle;
    }

    public static boolean booleanFromBundle(String key, Bundle bundle) {
        if (bundle == null || key == null) {
            return false;
        }
        return bundle.getBoolean(key);
    }

    public static Bundle integerToBundle(String key, int value) {
        return integerToBundle(key, value, new Bundle());
    }

    public static Bundle integerToBundle(String key, int value, Bundle bundle) {
        if (bundle == null) {
            bundle = new Bundle();
        }
        if (key == null) {
            return bundle;
        }
        bundle.putInt(key, value);
        return bundle;
    }

    public static int integerFromBundle(String key, Bundle bundle) {
        if (bundle == null || key == null || !bundle.containsKey(key)) {
            return -1;
        }
        return bundle.getInt(key);
    }

    public static Bundle bundleToBundle(String key, Bundle bundle) {
        return bundleToBundle(key, bundle, new Bundle());
    }

    public static Bundle bundleToBundle(String key, Bundle bundle, Bundle resultBundle) {
        if (key == null || bundle == null) {
            return resultBundle;
        }
        resultBundle.putBundle(key, bundle);
        return resultBundle;
    }

    public static Bundle bundleFromBundle(String key, Bundle bundle) {
        if (bundle == null || key == null) {
            return null;
        }
        return bundle.getBundle(key);
    }

    public static Bundle bundleListToBundle(String baseKey, List<Bundle> list) {
        Bundle bundle = new Bundle();
        if (baseKey == null || list == null) {
            return bundle;
        }
        for (int ii = 0; ii < list.size(); ii++) {
            bundle.putBundle(baseKey + ii, list.get(ii));
        }
        return bundle;
    }

    public static List<Bundle> bundleListFromBundle(String baseKey, Bundle bundle) {
        if (baseKey == null || bundle == null) {
            return Collections.emptyList();
        }
        List<Bundle> resultList = new ArrayList<>();
        for (int ii = 0; ii < bundle.size(); ii++) {
            Bundle currentBundle = bundle.getBundle(baseKey + ii);
            if (currentBundle != null) {
                resultList.add(currentBundle);
            }
        }
        return resultList;
    }

    public static Bundle validationResultListToBundle(String baseKey, List<ValidationResult> validationResultList) {
        if (baseKey == null || validationResultList == null) {
            return new Bundle();
        }
        List<Bundle> bundleList = new ArrayList<>();
        for (ValidationResult result : validationResultList) {
            bundleList.add(result.toBundle());
        }
        return bundleListToBundle(baseKey, bundleList);
    }

    public static List<ValidationResult> validationResultListFromBundle(String baseKey, Bundle bundle) {
        List<Bundle> bundleList = bundleListFromBundle(baseKey, bundle);
        List<ValidationResult> validationResultList = new ArrayList<>();
        for (Bundle currentBundle : bundleList) {
            if (currentBundle != null) {
                validationResultList.add(new ValidationResult(currentBundle));
            }
        }
        return validationResultList;
    }

    public static Bundle fileEntryListToBundle(String baseKey, List<FileEntry> fileEntryList) {
        if (baseKey == null || fileEntryList == null) {
            return new Bundle();
        }
        List<Bundle> bundleList = new ArrayList<>();
        for (FileEntry entry : fileEntryList) {
            bundleList.add(entry.toBundle());
        }
        return bundleListToBundle(baseKey, bundleList);
    }

    public static List<FileEntry> fileEntryListFromBundle(String baseKey, Bundle bundle) {
        List<Bundle> bundleList = bundleListFromBundle(baseKey, bundle);
        List<FileEntry> entryList = new ArrayList<>();
        for (Bundle currentBundle : bundleList) {
            if (currentBundle != null) {
                entryList.add(new FileEntry(currentBundle));
            }
        }
        return entryList;
    }

    public static Bundle contextOptionListToBundle(String baseKey, List<ContextOption> contextOptionList) {
        if (baseKey == null || contextOptionList == null) {
            return new Bundle();
        }
        List<Bundle> bundleList = new ArrayList<>();
        for (ContextOption contextOption : contextOptionList) {
            bundleList.add(contextOption.toBundle());
        }
        return bundleListToBundle(baseKey, bundleList);
    }

    public static List<ContextOption> contextOptionListFromBundle(String baseKey, Bundle bundle) {
        List<Bundle> bundleList = bundleListFromBundle(baseKey, bundle);
        List<ContextOption> contextOptionList = new ArrayList<>();
        for (Bundle currentBundle : bundleList) {
            if (currentBundle != null) {
                ContextOption contextOption = ContextOption.fromBundle(currentBundle);
                if (contextOption != null) {
                    contextOptionList.add(contextOption);
                }
            }
        }
        return contextOptionList;
    }

    public static Bundle suspensionIntervalListToBundle(String baseKey, List<Interval> intervalList) {
        if (baseKey == null || intervalList == null) {
            return new Bundle();
        }
        List<Bundle> bundleList = new ArrayList<>();
        for (Interval interval : intervalList) {
            bundleList.add(interval.toBundle());
        }
        return bundleListToBundle(baseKey, bundleList);
    }

    public static List<Interval> suspensionIntervalListFromBundle(String baseKey, Bundle bundle) {
        List<Bundle> bundleList = bundleListFromBundle(baseKey, bundle);
        List<Interval> intervalList = new ArrayList<>();
        for (Bundle currentBundle : bundleList) {
            if (currentBundle != null) {
                intervalList.add(new Interval(currentBundle));
            }
        }
        return intervalList;
    }
}
