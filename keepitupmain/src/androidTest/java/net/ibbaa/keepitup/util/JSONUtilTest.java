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

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SmallTest
@SuppressWarnings({"unchecked", "DataFlowIssue", "SequencedCollectionMethodCanBeUsed"})
@RunWith(AndroidJUnit4.class)
public class JSONUtilTest {

    @Test
    public void testToJSONObject() throws Exception {
        JSONObject jsonObj = new JSONObject(getTestMap());
        assertEquals(JSONObject.NULL, jsonObj.get("nullKey"));
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
        assertEquals(JSONObject.NULL, nestedList.get(0));
        assertEquals("value", nestedList.get(1));
        assertEquals(5, nestedList.get(2));
        assertEquals(8L, nestedList.get(3));
        assertEquals(10d, nestedList.get(4));
        assertTrue((boolean) nestedList.get(5));
    }

    @Test
    public void testToJSONArray() throws Exception {
        JSONArray jsonArray = new JSONArray(getTestList());
        assertEquals(JSONObject.NULL, jsonArray.get(0));
        assertEquals("value", jsonArray.get(1));
        assertEquals(5, jsonArray.get(2));
        assertEquals(8L, jsonArray.get(3));
        assertEquals(10d, jsonArray.get(4));
        assertTrue((boolean) jsonArray.get(5));
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
        assertNull(null, map.get("nullKey"));
        assertEquals("value", map.get("stringKey"));
        assertEquals(5, map.get("intKey"));
        assertEquals(8L, map.get("longKey"));
        assertEquals(10d, map.get("doubleKey"));
        assertTrue((Boolean) map.get("booleanKey"));
        Map<String, ?> nestedMap = (Map<String, ?>) map.get("objectKey");
        assertEquals("value", nestedMap.get("stringKey"));
        assertEquals(5, nestedMap.get("intKey"));
        assertEquals(8L, nestedMap.get("longKey"));
        assertEquals(10d, nestedMap.get("doubleKey"));
        assertTrue((Boolean) nestedMap.get("booleanKey"));
        List<?> nestedList = (List<?>) map.get("listKey");
        assertNull(nestedList.get(0));
        assertEquals("value", nestedList.get(1));
        assertEquals(5, nestedList.get(2));
        assertEquals(8L, nestedList.get(3));
        assertEquals(10d, nestedList.get(4));
        assertTrue((Boolean) nestedList.get(5));
    }

    @Test
    public void testToList() {
        JSONArray jsonArray = new JSONArray(getTestList());
        List<?> list = JSONUtil.toList(jsonArray);
        assertNull(list.get(0));
        assertEquals("value", list.get(1));
        assertEquals(5, list.get(2));
        assertEquals(8L, list.get(3));
        assertEquals(10d, list.get(4));
        assertTrue((Boolean) list.get(5));
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

    @Test
    public void testCanonicalize() throws Exception {
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("b", 2);
        jsonObj.put("a", 1);
        jsonObj.put("c", 3);
        assertEquals("{\"a\":1,\"b\":2,\"c\":3}", JSONUtil.canonicalize(jsonObj));
        JSONObject inner = new JSONObject();
        inner.put("z", 1);
        inner.put("a", 2);
        JSONObject root = new JSONObject();
        root.put("inner", inner);
        assertEquals("{\"inner\":{\"a\":2,\"z\":1}}", JSONUtil.canonicalize(root));
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(3);
        jsonArray.put(1);
        jsonArray.put(2);
        root = new JSONObject();
        root.put("arr", jsonArray);
        assertEquals("{\"arr\":[3,1,2]}", JSONUtil.canonicalize(root));
        jsonObj = new JSONObject();
        jsonObj.put("quote", "\"");
        jsonObj.put("backslash", "\\");
        jsonObj.put("newline", "\n");
        assertEquals("{\"backslash\":\"\\\\\",\"newline\":\"\\n\",\"quote\":\"\\\"\"}", JSONUtil.canonicalize(jsonObj));
        jsonObj = new JSONObject();
        jsonObj.put("url", "https://www.test.org/");
        assertEquals("{\"url\":\"https://www.test.org/\"}", JSONUtil.canonicalize(jsonObj));
        jsonObj = new JSONObject();
        jsonObj.put("int", 1);
        jsonObj.put("floatInt", 1.0);
        jsonObj.put("float", 1.5);
        assertEquals("{\"float\":1.5,\"floatInt\":1,\"int\":1}", JSONUtil.canonicalize(jsonObj));
        jsonObj = new JSONObject();
        jsonObj.put("t", true);
        jsonObj.put("f", false);
        jsonObj.put("n", JSONObject.NULL);
        assertEquals( "{\"f\":false,\"n\":null,\"t\":true}", JSONUtil.canonicalize(jsonObj));
        JSONObject level3 = new JSONObject();
        level3.put("b", 2);
        level3.put("a", 1);
        jsonArray = new JSONArray();
        jsonArray.put(level3);
        JSONObject level1 = new JSONObject();
        level1.put("arr", jsonArray);
        assertEquals("{\"arr\":[{\"a\":1,\"b\":2}]}", JSONUtil.canonicalize(level1));
    }

    @Test
    public void testCanonicalizeFullHeader() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("version", 1);
        jsonObject.put("format", "encrypted-json");
        JSONObject cipher = new JSONObject();
        cipher.put("tagLength", 128);
        cipher.put("algorithm", "AES-256-GCM");
        cipher.put("iv", "abc");
        JSONObject kdf = new JSONObject();
        kdf.put("iterations", 3);
        kdf.put("memoryCost", 65536);
        kdf.put("algorithm", "Argon2id");
        jsonObject.put("cipher", cipher);
        jsonObject.put("kdf", kdf);
        assertEquals("{\"cipher\":{\"algorithm\":\"AES-256-GCM\",\"iv\":\"abc\",\"tagLength\":128},\"format\":\"encrypted-json\",\"kdf\":{\"algorithm\":\"Argon2id\",\"iterations\":3,\"memoryCost\":65536},\"version\":1}", JSONUtil.canonicalize(jsonObject));
    }

    private Map<String, ?> getTestMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("nullKey", null);
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
        list.add(null);
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
