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
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.InputType;
import android.view.View;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

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

    public static void drawSwipeTrashcan(@NonNull Canvas canvas, @NonNull RecyclerView recyclerView, @NonNull View itemView, float dX, @DrawableRes int iconRes, float marginDp, float startSizeDp, float endSizeDp) {
        if (dX <= 0f) {
            return;
        }
        Drawable icon = AppCompatResources.getDrawable(recyclerView.getContext(), iconRes);
        if (icon == null) {
            return;
        }
        float density = recyclerView.getResources().getDisplayMetrics().density;
        int marginPx = Math.round(marginDp * density);
        int startSizePx = Math.round(startSizeDp * density);
        int endSizePx = Math.round(endSizeDp * density);
        float progress = Math.min(1f, dX / itemView.getWidth());
        int iconSize = (int) (startSizePx + (endSizePx - startSizePx) * progress);
        int iconTop = itemView.getTop() + (itemView.getHeight() - iconSize) / 2;
        int iconLeft = itemView.getLeft() + marginPx;
        icon.setBounds(iconLeft, iconTop, iconLeft + iconSize, iconTop + iconSize);
        icon.setAlpha((int) (255 * progress));
        int nightModeFlags = recyclerView.getContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            icon.setTint(Color.WHITE);
        } else {
            icon.setTint(Color.BLACK);
        }
        icon.draw(canvas);
    }
}
