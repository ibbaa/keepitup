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
import android.text.InputType;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.model.NetworkTask;

public class UIUtil {

    public static boolean isInputTypeNumber(int inputType) {
        return (inputType & InputType.TYPE_CLASS_NUMBER) == InputType.TYPE_CLASS_NUMBER;
    }

    public static String getTextForNamedTask(Context context, NetworkTask task) {
        String name = StringUtil.isEmpty(task.getName()) ? context.getResources().getString(R.string.task_name_default) : task.getName();
        String formattedTitleText;
        if (name.equals(context.getResources().getString(R.string.task_name_default))) {
            formattedTitleText = context.getResources().getString(R.string.task_title_normal, task.getIndex() + 1);
        } else {
            formattedTitleText = context.getResources().getString(R.string.task_title_named, name, task.getIndex() + 1);
        }
        return formattedTitleText;
    }
}
