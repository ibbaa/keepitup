/*
 * Copyright (c) 2021. Alwin Ibba
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

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

import net.ibbaa.keepitup.logging.Log;

public class NumberUtil {

    public static boolean isValidLongValue(Object value) {
        if (value == null) {
            return false;
        }
        try {
            Long.valueOf(value.toString());
            return true;
        } catch (Exception exc) {
            Log.d(NumberUtil.class.getName(), "Parsing error, value == " + value, exc);
        }
        return false;
    }

    public static long getLongValue(Object value, long defaultValue) {
        if (isValidLongValue(value)) {
            return Long.parseLong(value.toString());
        }
        return defaultValue;
    }

    public static boolean isValidIntValue(Object value) {
        if (value == null) {
            return false;
        }
        try {
            Integer.valueOf(value.toString());
            return true;
        } catch (Exception exc) {
            Log.d(NumberUtil.class.getName(), "Parsing error, value == " + value, exc);
        }
        return false;
    }

    public static int getIntValue(Object value, int defaultValue) {
        if (isValidIntValue(value)) {
            return Integer.parseInt(value.toString());
        }
        return defaultValue;
    }

    public static boolean isValidDoubleValue(Object value) {
        if (value == null) {
            return false;
        }
        try {
            NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
            numberFormat.parse(value.toString());
            return true;
        } catch (Exception exc) {
            Log.d(NumberUtil.class.getName(), "Parsing error, value == " + value, exc);
        }
        return false;
    }

    public static double getDoubleValue(Object value, double defaultValue) {
        if (isValidDoubleValue(value)) {
            NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
            try {
                return Objects.requireNonNull(numberFormat.parse(value.toString())).doubleValue();
            } catch (Exception exc) {
                Log.d(NumberUtil.class.getName(), "Parsing error, value == " + value, exc);
            }
        }
        return defaultValue;
    }

    public static long ensurePositive(long value) {
        if (value < 0) {
            return 0;
        }
        return value;
    }
}
