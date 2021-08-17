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

package net.ibbaa.keepitup.ui.clipboard;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;

import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.util.NumberUtil;

public class SystemClipboardManager implements IClipboardManager {

    private final Context context;
    private final android.content.ClipboardManager clipboardManager;

    public SystemClipboardManager(Context context) {
        this.context = context;
        this.clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
    }

    @Override
    public boolean hasData() {
        Log.d(SystemClipboardManager.class.getName(), "hasData");
        if (!clipboardManager.hasPrimaryClip()) {
            Log.d(SystemClipboardManager.class.getName(), "Clipboard does not have primary clip.");
            return false;
        }
        ClipDescription description = clipboardManager.getPrimaryClipDescription();
        Log.d(SystemClipboardManager.class.getName(), "Clipboard description is " + description);
        if (description != null && hasCompatibleMimeType(description)) {
            Log.d(SystemClipboardManager.class.getName(), "Clipboard has suitable data");
            return true;
        }
        Log.d(SystemClipboardManager.class.getName(), "Clipboard does not have suitable data");
        return false;
    }

    private boolean hasCompatibleMimeType(ClipDescription description) {
        return description.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN) || description.hasMimeType(ClipDescription.MIMETYPE_TEXT_HTML) || description.hasMimeType(ClipDescription.MIMETYPE_TEXT_URILIST);
    }

    @Override
    public boolean hasNumericIntegerData() {
        Log.d(SystemClipboardManager.class.getName(), "hasNumericIntegerData");
        if (!hasData()) {
            return false;
        }
        String data = getData();
        Log.d(SystemClipboardManager.class.getName(), "Clipboard data is " + data);
        return NumberUtil.isValidLongValue(data);
    }

    @Override
    public String getData() {
        Log.d(SystemClipboardManager.class.getName(), "getData");
        if (!hasData()) {
            return null;
        }
        ClipData data = clipboardManager.getPrimaryClip();
        if (data == null) {
            Log.d(SystemClipboardManager.class.getName(), "ClipData is null");
            return null;
        }
        if (data.getItemCount() <= 0) {
            Log.d(SystemClipboardManager.class.getName(), "ClipData does not contain items");
            return null;
        }
        ClipData.Item dataItem = data.getItemAt(0);
        if (dataItem == null) {
            Log.d(SystemClipboardManager.class.getName(), "ClipData item is null");
            return null;
        }
        CharSequence text = dataItem.coerceToText(context);
        Log.d(SystemClipboardManager.class.getName(), "Clipboard text is " + text);
        if (text == null) {
            Log.d(SystemClipboardManager.class.getName(), "ClipData text is null");
            return null;
        }
        return text.toString();
    }

    @Override
    public void putData(String data) {
        Log.d(SystemClipboardManager.class.getName(), "putData, data is " + data);
        if (data == null) {
            Log.e(SystemClipboardManager.class.getName(), "data is null");
            return;
        }
        ClipData clipData = ClipData.newPlainText(data, data);
        clipboardManager.setPrimaryClip(clipData);
    }
}
