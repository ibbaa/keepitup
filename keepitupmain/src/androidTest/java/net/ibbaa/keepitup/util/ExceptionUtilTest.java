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

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import org.junit.Test;
import org.junit.runner.RunWith;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class ExceptionUtilTest {

    @Test
    public void testGetRootCause() {
        Exception testException = null;
        try {
            testException = new Exception("Test");
            throw testException;
        } catch (Exception exc) {
            assertEquals(testException, ExceptionUtil.getRootCause(exc));
        }
        try {
            testException = new Exception("Test");
            throw new RuntimeException(testException);
        } catch (Exception exc) {
            assertEquals(testException, ExceptionUtil.getRootCause(exc));
        }
        try {
            testException = new Exception("Test");
            throw new Exception(new RuntimeException(testException));
        } catch (Exception exc) {
            assertEquals(testException, ExceptionUtil.getRootCause(exc));
        }
    }

    @Test
    public void testGetLogableMessage() {
        try {
            throw new NullPointerException("Null");
        } catch (Exception exc) {
            assertEquals("NullPointerException: Null", ExceptionUtil.getLogableMessage(exc));
        }
        try {
            throw new IllegalArgumentException("Test");
        } catch (Exception exc) {
            assertEquals("IllegalArgumentException: Test", ExceptionUtil.getLogableMessage(exc));
        }
        try {
            throw new Exception("");
        } catch (Exception exc) {
            assertEquals("java.lang.Exception", ExceptionUtil.getLogableMessage(exc));
        }
    }

}
