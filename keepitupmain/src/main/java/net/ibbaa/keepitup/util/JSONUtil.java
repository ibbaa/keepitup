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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class JSONUtil {

    public static Map<String, ?> toMap(JSONObject jsonObject) {
        Map<String, Object> map = new HashMap<>();
        Iterator<String> keys = jsonObject.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            Object value;
            if (jsonObject.isNull(key)) {
                value = null;
            } else {
                value = jsonObject.opt(key);
                if (value instanceof JSONArray) {
                    value = toList((JSONArray) value);
                } else if (value instanceof JSONObject) {
                    value = toMap((JSONObject) value);
                }
            }
            map.put(key, value);
        }
        return map;
    }

    public static List<?> toList(JSONArray jsonArray) {
        List<Object> list = new ArrayList<>();
        for (int ii = 0; ii < jsonArray.length(); ii++) {
            Object value;
            if (jsonArray.isNull(ii)) {
                value = null;
            } else {
                value = jsonArray.opt(ii);
                if (value instanceof JSONArray) {
                    value = toList((JSONArray) value);
                } else if (value instanceof JSONObject) {
                    value = toMap((JSONObject) value);
                }
            }
            list.add(value);
        }
        return list;
    }

    public static String canonicalize(JSONObject jsonObject) {
        StringBuilder stringBuilder = new StringBuilder();
        writeObject(jsonObject, stringBuilder);
        return stringBuilder.toString();
    }

    private static void writeObject(JSONObject jsonObject, StringBuilder stringBuilder) {
        stringBuilder.append('{');
        List<String> keys = new ArrayList<>();
        Iterator<String> iterator = jsonObject.keys();
        while (iterator.hasNext()) {
            keys.add(iterator.next());
        }
        Collections.sort(keys);
        boolean first = true;
        for (String key : keys) {
            if (!first) {
                stringBuilder.append(',');
            }
            first = false;
            writeString(key, stringBuilder);
            stringBuilder.append(':');
            writeValue(safeGet(jsonObject, key), stringBuilder);
        }
        stringBuilder.append('}');
    }

    private static void writeArray(JSONArray jsonArray, StringBuilder stringBuilder) {
        stringBuilder.append('[');
        for (int ii = 0; ii < jsonArray.length(); ii++) {
            if (ii > 0) {
                stringBuilder.append(',');
            }
            writeValue(safeGet(jsonArray, ii), stringBuilder);
        }
        stringBuilder.append(']');
    }

    private static void writeValue(Object value, StringBuilder stringBuilder) {
        if (value == null || value == JSONObject.NULL) {
            stringBuilder.append("null");
        } else if (value instanceof JSONObject) {
            writeObject((JSONObject) value, stringBuilder);
        } else if (value instanceof JSONArray) {
            writeArray((JSONArray) value, stringBuilder);
        } else if (value instanceof Number) {
            writeNumber((Number) value, stringBuilder);
        } else if (value instanceof Boolean) {
            stringBuilder.append(value);
        } else {
            writeString(value.toString(), stringBuilder);
        }
    }

    private static void writeNumber(Number number, StringBuilder stringBuilder) {
        if (number instanceof Double || number instanceof Float) {
            if (number.doubleValue() == Math.rint(number.doubleValue())) {
                stringBuilder.append(number.longValue());
            } else {
                stringBuilder.append(number.doubleValue());
            }
        } else {
            stringBuilder.append(number.toString());
        }
    }

    private static void writeString(String string, StringBuilder stringBuilder) {
        stringBuilder.append('"');
        for (int ii = 0; ii < string.length(); ii++) {
            char cc = string.charAt(ii);
            switch (cc) {
                case '"':
                    stringBuilder.append("\\\"");
                    break;
                case '\\':
                    stringBuilder.append("\\\\");
                    break;
                case '\b':
                    stringBuilder.append("\\b");
                    break;
                case '\f':
                    stringBuilder.append("\\f");
                    break;
                case '\n':
                    stringBuilder.append("\\n");
                    break;
                case '\r':
                    stringBuilder.append("\\r");
                    break;
                case '\t':
                    stringBuilder.append("\\t");
                    break;
                case '/':
                    stringBuilder.append('/');
                    break;
                default:
                    if (cc < 0x20) {
                        stringBuilder.append(String.format("\\u%04x", (int) cc));
                    } else {
                        stringBuilder.append(cc);
                    }
            }
        }
        stringBuilder.append('"');
    }

    private static Object safeGet(JSONObject jsonObject, String key) {
        try {
            return jsonObject.get(key);
        } catch (JSONException jsonException) {
            throw new IllegalStateException("Canonical JSON failed at key: " + key, jsonException);
        }
    }

    private static Object safeGet(JSONArray jsonArray, int index) {
        try {
            return jsonArray.get(index);
        } catch (JSONException jsonException) {
            throw new IllegalStateException("Canonical JSON array access failed at index: " + index, jsonException);
        }
    }
}
