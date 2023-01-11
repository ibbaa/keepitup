/*
 * Copyright (c) 2023. Alwin Ibba
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import org.junit.Test;
import org.junit.runner.RunWith;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class NumberUtilTest {

    @Test
    public void testLongValue() {
        assertTrue(NumberUtil.isValidLongValue("1"));
        assertTrue(NumberUtil.isValidLongValue(2));
        assertTrue(NumberUtil.isValidLongValue(Long.MAX_VALUE));
        assertFalse(NumberUtil.isValidLongValue("xyz"));
        assertFalse(NumberUtil.isValidLongValue(null));
        assertEquals(1, NumberUtil.getLongValue("1", 2));
        assertEquals(2, NumberUtil.getLongValue(2, 3));
        assertEquals(Long.MAX_VALUE, NumberUtil.getLongValue(Long.MAX_VALUE, 3));
        assertEquals(4, NumberUtil.getLongValue("xyz", 4));
        assertEquals(4, NumberUtil.getLongValue(null, 4));
    }

    @Test
    public void testIntValue() {
        assertTrue(NumberUtil.isValidIntValue("1"));
        assertTrue(NumberUtil.isValidIntValue(2));
        assertTrue(NumberUtil.isValidIntValue(Integer.MAX_VALUE));
        assertFalse(NumberUtil.isValidIntValue(Long.MAX_VALUE));
        assertFalse(NumberUtil.isValidIntValue("xyz"));
        assertFalse(NumberUtil.isValidIntValue(null));
        assertEquals(1, NumberUtil.getIntValue("1", 2));
        assertEquals(2, NumberUtil.getIntValue(2, 3));
        assertEquals(Integer.MAX_VALUE, NumberUtil.getIntValue(Integer.MAX_VALUE, 3));
        assertEquals(4, NumberUtil.getIntValue("xyz", 4));
        assertEquals(4, NumberUtil.getIntValue(null, 4));
    }

    @Test
    public void testDoubleValue() {
        assertTrue(NumberUtil.isValidDoubleValue("1"));
        assertTrue(NumberUtil.isValidDoubleValue("2.3"));
        assertTrue(NumberUtil.isValidDoubleValue("23,000"));
        assertFalse(NumberUtil.isValidDoubleValue("xyz"));
        assertFalse(NumberUtil.isValidDoubleValue(null));
        assertEquals(1, NumberUtil.getDoubleValue("1", 2), 0.01);
        assertEquals(2.3, NumberUtil.getDoubleValue("2.3", 2), 0.01);
        assertEquals(23000, NumberUtil.getDoubleValue("23,000", 2), 0.01);
        assertEquals(2, NumberUtil.getDoubleValue("xyz", 2), 0.01);
        assertEquals(2, NumberUtil.getDoubleValue(null, 2), 0.01);
    }

    @Test
    public void testEnsurePositive() {
        assertEquals(0, NumberUtil.ensurePositive(0));
        assertEquals(0, NumberUtil.ensurePositive(-1));
        assertEquals(0, NumberUtil.ensurePositive(Long.MIN_VALUE));
        assertEquals(1, NumberUtil.ensurePositive(1));
        assertEquals(Long.MAX_VALUE, NumberUtil.ensurePositive(Long.MAX_VALUE));
    }
}
