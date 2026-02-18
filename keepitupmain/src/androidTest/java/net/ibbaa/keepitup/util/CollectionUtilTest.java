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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class CollectionUtilTest {

    @Test
    public void testCopyMap() {
        Map<String, String> source = new HashMap<>();
        source.put("key1", "value1");
        source.put("key2", "value2");
        Map<String, String> target1 = new HashMap<>();
        CollectionUtil.copyMap(source, target1, "pre_");
        assertEquals(2, target1.size());
        assertEquals("value1", target1.get("pre_key1"));
        assertEquals("value2", target1.get("pre_key2"));
        assertNull(target1.get("key1"));
        Map<String, String> target2 = new HashMap<>();
        CollectionUtil.copyMap(source, target2, "");
        assertEquals(2, target2.size());
        assertEquals("value1", target2.get("key1"));
        assertEquals("value2", target2.get("key2"));
        Map<String, String> target3 = new HashMap<>();
        CollectionUtil.copyMap(source, target3, null);
        assertEquals(2, target3.size());
        assertEquals("value1", target3.get("key1"));
        assertEquals("value2", target3.get("key2"));
        Map<String, String> target4 = new HashMap<>();
        target4.put("pre_key1", "alterWert");
        CollectionUtil.copyMap(source, target4, "pre_");
        assertEquals(2, target4.size());
        assertEquals("value1", target4.get("pre_key1"));
        Map<String, String> target5 = new HashMap<>();
        CollectionUtil.copyMap(null, target5, "pre_");
        assertTrue(target5.isEmpty());
        CollectionUtil.copyMap(source, null, null);
        CollectionUtil.copyMap(null, null, null);
    }
}
