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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@SmallTest
@SuppressWarnings({"unchecked", "DataFlowIssue"})
@RunWith(AndroidJUnit4.class)
public class JSONUtilTest {

    @Test
    public void testToJSONObject() throws Exception {
        JSONObject jsonObj = new JSONObject(getTestMap());
        assertEquals("value", jsonObj.get("stringKey"));
        assertEquals(5, jsonObj.get("intKey"));
        assertEquals(8L, jsonObj.get("longKey"));
        assertEquals(10d, jsonObj.get("doubleKey"));
        assertTrue((boolean) jsonObj.get("booleanKey"));
        JSONObject nestedJsonObj = (JSONObject) jsonObj.get("objectKey");
        assertEquals("value", nestedJsonObj.get("stringKey"));
        assertEquals(5, nestedJsonObj.get("intKey"));
        assertEquals(8L, nestedJsonObj.get("longKey"));
        assertEquals(10d, nestedJsonObj.get("doubleKey"));
        assertTrue((boolean) nestedJsonObj.get("booleanKey"));
        JSONArray nestedList = (JSONArray) jsonObj.get("listKey");
        assertEquals("value", nestedList.get(0));
        assertEquals(5, nestedList.get(1));
        assertEquals(8L, nestedList.get(2));
        assertEquals(10d, nestedList.get(3));
        assertTrue((boolean) nestedList.get(4));
    }

    @Test
    public void testToJSONArray() throws Exception {
        JSONArray jsonArray = new JSONArray(getTestList());
        assertEquals("value", jsonArray.get(0));
        assertEquals(5, jsonArray.get(1));
        assertEquals(8L, jsonArray.get(2));
        assertEquals(10d, jsonArray.get(3));
        assertTrue((boolean) jsonArray.get(4));
    }

    @Test
    public void testToJSONArrayWithMaps() throws Exception {
        JSONArray jsonArray = new JSONArray(getTestListWithMaps());
        JSONObject jsonObj = (JSONObject) jsonArray.get(0);
        assertEquals("value", jsonObj.get("stringKey"));
        assertEquals(5, jsonObj.get("intKey"));
        assertEquals(8L, jsonObj.get("longKey"));
        assertEquals(10d, jsonObj.get("doubleKey"));
        assertTrue((boolean) jsonObj.get("booleanKey"));
        jsonObj = (JSONObject) jsonArray.get(1);
        assertEquals("value", jsonObj.get("stringKey"));
        assertEquals(5, jsonObj.get("intKey"));
        assertEquals(8L, jsonObj.get("longKey"));
        assertEquals(10d, jsonObj.get("doubleKey"));
        assertTrue((boolean) jsonObj.get("booleanKey"));
        jsonObj = (JSONObject) jsonArray.get(2);
        assertEquals("value", jsonObj.get("stringKey"));
        assertEquals(5, jsonObj.get("intKey"));
        assertEquals(8L, jsonObj.get("longKey"));
        assertEquals(10d, jsonObj.get("doubleKey"));
        assertTrue((boolean) jsonObj.get("booleanKey"));
    }

    @Test
    public void testToMap() {
        JSONObject jsonObj = new JSONObject(getTestMap());
        Map<String, ?> map = JSONUtil.toMap(jsonObj);
        assertEquals("value", map.get("stringKey"));
        assertEquals(5, map.get("intKey"));
        assertEquals(8L, map.get("longKey"));
        assertEquals(10d, map.get("doubleKey"));
        assertTrue((Boolean) map.get("booleanKey"));
        Map<String, ?> nestedMap = (Map<String, ?>) map.get("objectKey");
        assertEquals("value", Objects.requireNonNull(nestedMap).get("stringKey"));
        assertEquals(5, nestedMap.get("intKey"));
        assertEquals(8L, nestedMap.get("longKey"));
        assertEquals(10d, nestedMap.get("doubleKey"));
        assertTrue((Boolean) nestedMap.get("booleanKey"));
        List<?> nestedList = (List<?>) map.get("listKey");
        assertEquals("value", Objects.requireNonNull(nestedList).get(0));
        assertEquals(5, nestedList.get(1));
        assertEquals(8L, nestedList.get(2));
        assertEquals(10d, nestedList.get(3));
        assertTrue((Boolean) nestedList.get(4));
    }

    @Test
    public void testToList() {
        JSONArray jsonArray = new JSONArray(getTestList());
        List<?> list = JSONUtil.toList(jsonArray);
        assertEquals("value", list.get(0));
        assertEquals(5, list.get(1));
        assertEquals(8L, list.get(2));
        assertEquals(10d, list.get(3));
        assertTrue((Boolean) list.get(4));
    }

    @Test
    public void testToListWithMaps() {
        JSONArray jsonArray = new JSONArray(getTestListWithMaps());
        List<?> list = JSONUtil.toList(jsonArray);
        Map<String, ?> map = (Map<String, ?>) list.get(0);
        assertEquals("value", map.get("stringKey"));
        assertEquals(5, map.get("intKey"));
        assertEquals(8L, map.get("longKey"));
        assertEquals(10d, map.get("doubleKey"));
        assertTrue((Boolean) map.get("booleanKey"));
        map = (Map<String, ?>) list.get(1);
        assertEquals("value", map.get("stringKey"));
        assertEquals(5, map.get("intKey"));
        assertEquals(8L, map.get("longKey"));
        assertEquals(10d, map.get("doubleKey"));
        assertTrue((Boolean) map.get("booleanKey"));
        map = (Map<String, ?>) list.get(2);
        assertEquals("value", map.get("stringKey"));
        assertEquals(5, map.get("intKey"));
        assertEquals(8L, map.get("longKey"));
        assertEquals(10d, map.get("doubleKey"));
        assertTrue((Boolean) map.get("booleanKey"));
    }

    private Map<String, ?> getTestMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("stringKey", "value");
        map.put("intKey", 5);
        map.put("longKey", 8L);
        map.put("doubleKey", 10d);
        map.put("booleanKey", Boolean.TRUE);
        Map<String, Object> nestedMap = new HashMap<>();
        nestedMap.put("stringKey", "value");
        nestedMap.put("intKey", 5);
        nestedMap.put("longKey", 8L);
        nestedMap.put("doubleKey", 10d);
        nestedMap.put("booleanKey", Boolean.TRUE);
        map.put("objectKey", nestedMap);
        List<?> nestedList = getTestList();
        map.put("listKey", nestedList);
        return map;
    }

    private List<?> getTestList() {
        List<Object> list = new ArrayList<>();
        list.add("value");
        list.add(5);
        list.add(8L);
        list.add(10d);
        list.add(Boolean.TRUE);
        return list;
    }

    private List<?> getTestListWithMaps() {
        List<Object> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("stringKey", "value");
        map.put("intKey", 5);
        map.put("longKey", 8L);
        map.put("doubleKey", 10d);
        map.put("booleanKey", Boolean.TRUE);
        list.add(map);
        list.add(map);
        list.add(map);
        return list;
    }
}
