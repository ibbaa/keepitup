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

package net.ibbaa.keepitup.util;

import android.content.Context;

import net.ibbaa.keepitup.R;

import java.text.NumberFormat;

public class StringUtil {

    public static String getStringValue(Object value, String defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        return value.toString();
    }

    public static String trim(String value) {
        if (value == null) {
            return null;
        }
        return value.trim();
    }

    public static boolean isEmpty(CharSequence value) {
        return value == null || value.length() <= 0;
    }

    public static boolean isEmpty(String value) {
        return isEmpty((CharSequence) value);
    }

    public static String notNull(String value) {
        return notNull((CharSequence) value);
    }

    public static String notNull(CharSequence value) {
        return value == null ? "" : value.toString();
    }

    public static String formatTimeRange(double timeRange, Context context) {
        String unit = context.getResources().getString(R.string.string_msec);
        if (timeRange >= 1000) {
            timeRange /= 1000;
            unit = context.getResources().getString(R.string.string_sec);
        }
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMaximumFractionDigits(2);
        return numberFormat.format(timeRange) + " " + unit;
    }

    public static boolean isTextSelected(String text, int selectionStart, int selectionEnd) {
        if (selectionStart < 0 || selectionEnd < 0 || selectionStart > selectionEnd) {
            return false;
        }
        return selectionStart <= text.length() && selectionEnd <= text.length() && selectionStart != selectionEnd;
    }
}
