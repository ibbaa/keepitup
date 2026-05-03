/*
 * Copyright (c) 2026 Alwin Ibba
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
import android.content.res.Resources;
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
import net.ibbaa.keepitup.model.Header;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.ui.validation.CredentialInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UIUtil {

    public static String getNetworkTaskTitleName(Context context, NetworkTask task, boolean lowerCase, boolean appendDefaultSuffix) {
        Resources resources = context.getResources();
        String defaultName = resources.getString(R.string.task_title_normal);
        String name = StringUtil.isEmpty(task.getName()) ? defaultName : task.getName();
        boolean isDefault = name.equals(defaultName);
        if (isDefault) {
            if (lowerCase) {
                name = name.toLowerCase();
            }
            if (task.getIndex() >= 0) {
                name += " " + (task.getIndex() + 1);
            }
        } else {
            if (appendDefaultSuffix && task.getIndex() >= 0) {
                return resources.getString(R.string.task_title_named, name, task.getIndex() + 1);
            }
        }
        return name;
    }

    public static List<CredentialInfo> snmpCommunitiesToCredentialInfoList(Context context, List<NetworkTask> tasks) {
        if (tasks == null) {
            return Collections.emptyList();
        }
        List<CredentialInfo> credentialInfoList = new ArrayList<>(tasks.size());
        String snmpCommunityText = context.getResources().getString(R.string.text_dialog_credential_info_snmp_community);
        for (NetworkTask task : tasks) {
            String name = getNetworkTaskTitleName(context, task, false, false);
            CredentialInfo credentialInfo = new CredentialInfo(name, snmpCommunityText);
            credentialInfoList.add(credentialInfo);
        }
        return credentialInfoList;
    }

    public static List<CredentialInfo> headersToCredentialInfoList(Context context, NetworkTask task, List<Header> headers) {
        if (headers == null) {
            return Collections.emptyList();
        }
        List<CredentialInfo> credentialInfoList = new ArrayList<>(headers.size());
        for (Header currentHeader : headers) {
            String headerText = context.getResources().getString(R.string.text_dialog_credential_info_header);
            String name;
            if (task == null) {
                name = context.getResources().getString(R.string.text_dialog_credential_info_default);
            } else {
                name = getNetworkTaskTitleName(context, task, false, false);
            }
            CredentialInfo credentialInfo = new CredentialInfo(name, currentHeader.getName() + " (" + headerText + ")");
            credentialInfoList.add(credentialInfo);
        }
        return credentialInfoList;
    }

    public static boolean isInputTypeNumber(int inputType) {
        return (inputType & InputType.TYPE_CLASS_NUMBER) == InputType.TYPE_CLASS_NUMBER;
    }

    public static String getEmptyIfNotSet(Context context, String value) {
        String notSetValue = context.getResources().getString(R.string.string_not_set);
        return notSetValue.equals(value) ? "" : value;
    }

    public static String getNotSetIfEmpty(Context context, String value) {
        String notSetValue = context.getResources().getString(R.string.string_not_set);
        return StringUtil.isEmpty(value) ? notSetValue : value;
    }

    public static int getNegativeIfNotSet(Context context, String value) {
        String notSetValue = context.getResources().getString(R.string.string_not_set);
        if (StringUtil.isEmpty(value) || notSetValue.equals(value)) {
            return -1;
        }
        return NumberUtil.getIntValue(value, -1);
    }

    public static String getNotSetIfNegative(Context context, int value) {
        String notSetValue = context.getResources().getString(R.string.string_not_set);
        return value < 0 ? notSetValue : String.valueOf(value);
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
