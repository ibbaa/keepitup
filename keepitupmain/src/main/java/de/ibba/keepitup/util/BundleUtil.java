package de.ibba.keepitup.util;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.ibba.keepitup.ui.validation.ValidationResult;

public class BundleUtil {

    public static Bundle messageToBundle(String key, String message) {
        Bundle bundle = new Bundle();
        if (key == null || message == null) {
            return bundle;
        }
        bundle.putString(key, message);
        return bundle;
    }

    public static String bundleToMessage(String key, Bundle bundle) {
        if (bundle == null || key == null) {
            return "";
        }
        return StringUtil.notNull(bundle.getString(key));
    }

    public static List<ValidationResult> indexedBundleToValidationResultList(Bundle bundle) {
        if (bundle == null) {
            return Collections.emptyList();
        }
        List<ValidationResult> resultList = new ArrayList<>();
        int index = 1;
        Bundle nestedBundle;
        while ((nestedBundle = (Bundle) bundle.get(String.valueOf(index))) != null) {
            resultList.add(new ValidationResult(nestedBundle));
            index++;
        }
        return resultList;
    }

    public static void addValidationResultToIndexedBundle(Bundle bundle, ValidationResult validationResult) {
        if (bundle != null && validationResult != null) {
            Bundle nestedBundle = validationResult.toBundle();
            List<ValidationResult> bundleList = indexedBundleToValidationResultList(bundle);
            bundle.putBundle(String.valueOf(bundleList.size() + 1), nestedBundle);
        }
    }
}
