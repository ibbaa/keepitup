package de.ibba.keepitup.util;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.ibba.keepitup.ui.validation.ValidationResult;

public class BundleUtil {

    public static Bundle stringToBundle(String key, String text) {
        Bundle bundle = new Bundle();
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

    public static Bundle bundleToBundle(String key, Bundle bundle) {
        Bundle resultBundle = new Bundle();
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
}
