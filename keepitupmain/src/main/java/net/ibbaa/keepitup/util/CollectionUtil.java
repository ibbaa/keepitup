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

import net.ibbaa.keepitup.model.Equality;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class CollectionUtil {

    public static <V> void copyMap(Map<String, V> source, Map<String, V> target, String prefix) {
        if (source == null || target == null) {
            return;
        }
        if (StringUtil.isEmpty(prefix)) {
            target.putAll(source);
            return;
        }
        for (Map.Entry<String, V> entry : source.entrySet()) {
            String newKey = prefix + entry.getKey();
            target.put(newKey, entry.getValue());
        }
    }

    public static <V> String mapToStableString(Map<String, V> map) {
        if (map == null) {
            return "[]";
        }
        TreeMap<String, V> sortedMap = new TreeMap<>(map);
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        Iterator<Map.Entry<String, V>> iterator = sortedMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, V> entry = iterator.next();
            builder.append(entry.getKey());
            builder.append("=");
            V value = entry.getValue();
            builder.append(value != null ? value.toString() : "null");
            if (iterator.hasNext()) {
                builder.append(":");
            }
        }
        builder.append("]");
        return builder.toString();
    }

    public static <T> boolean areListsEqual(List<T> list1, List<T> list2, Equality<T> equality) {
        if (list1 == list2) {return true;}
        if (list1 == null || list2 == null || list1.size() != list2.size()) {
            return false;
        }
        Iterator<T> iterator1 = list1.iterator();
        Iterator<T> iterator2 = list2.iterator();
        while (iterator1.hasNext() && iterator2.hasNext()) {
            T t1 = iterator1.next();
            T t2 = iterator2.next();
            if (t1 != t2) {
                if (t1 == null || t2 == null || !equality.areEqual(t1, t2)) {
                    return false;
                }
            }
        }
        return true;
    }
}
