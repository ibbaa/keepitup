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

import java.util.Map;

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
}
