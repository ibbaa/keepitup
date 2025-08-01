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

package net.ibbaa.keepitup.ui.dialog;

import android.os.Bundle;
import android.text.InputType;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class SettingsInput {

    public enum Type {
        ADDRESS(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_URI | InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS),
        PORT(InputType.TYPE_CLASS_NUMBER),
        INTERVAL(InputType.TYPE_CLASS_NUMBER),
        PINGCOUNT(InputType.TYPE_CLASS_NUMBER),
        PINGPACKAGESIZE(InputType.TYPE_CLASS_NUMBER),
        CONNECTCOUNT(InputType.TYPE_CLASS_NUMBER),
        NOTIFICATIONAFTER(InputType.TYPE_CLASS_NUMBER),
        TASKNAME(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS),
        USERAGENT(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

        private final int inputType;

        Type(int inputType) {
            this.inputType = inputType;
        }

        public int getInputType() {
            return inputType;
        }
    }

    private final Type type;
    private final String value;
    private final String title;
    private final String field;
    private final int position;
    private final List<String> validators;

    public SettingsInput(Type type, String value, String field, List<String> validators) {
        this(type, null, value, field, -1, validators);
    }

    public SettingsInput(Type type, String title, String value, String field, int position, List<String> validators) {
        this.type = type;
        this.value = value;
        this.title = title;
        this.field = field;
        this.position = position;
        this.validators = validators;
    }

    public SettingsInput(Bundle bundle) {
        if (bundle.containsKey("type")) {
            type = Type.valueOf(bundle.getString("type"));
        } else {
            type = null;
        }
        this.value = bundle.getString("value");
        this.title = bundle.getString("title");
        this.field = bundle.getString("field");
        this.position = bundle.getInt("position", -1);
        this.validators = bundle.getStringArrayList("validators");
    }

    public Type getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getValue() {
        return value;
    }

    public String getField() {
        return field;
    }

    public int getPosition() {
        return position;
    }

    public List<String> getValidators() {
        return validators;
    }

    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        if (type != null) {
            bundle.putString("type", type.name());
        }
        bundle.putString("value", value);
        bundle.putString("title", title);
        bundle.putString("field", field);
        bundle.putInt("position", position);
        bundle.putStringArrayList("validators", validators == null ? null : new ArrayList<>(validators));
        return bundle;
    }

    @NonNull
    @Override
    public String toString() {
        return "SettingsInput{" +
                "type=" + type +
                ", value='" + value + '\'' +
                ", title='" + title + '\'' +
                ", field='" + field + '\'' +
                ", position=" + position +
                ", validators=" + validators +
                '}';
    }
}
