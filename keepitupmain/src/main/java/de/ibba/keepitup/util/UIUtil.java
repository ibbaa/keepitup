package de.ibba.keepitup.util;

import android.text.InputType;

public class UIUtil {

    public static boolean isInpuTypeNumber(int inputType) {
        return (inputType & InputType.TYPE_CLASS_NUMBER) == InputType.TYPE_CLASS_NUMBER;
    }
}
