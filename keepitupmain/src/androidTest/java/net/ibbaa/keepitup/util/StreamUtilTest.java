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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import com.google.common.base.Charsets;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;

@SmallTest
@SuppressWarnings({"CharsetObjectCanBeUsed", "StringOperationCanBeSimplified"})
@RunWith(AndroidJUnit4.class)
public class StreamUtilTest {

    @Test
    public void testInputStreamToString() throws Exception {
        ByteArrayInputStream stream = new ByteArrayInputStream("Test".getBytes(Charsets.US_ASCII));
        assertEquals("Test", StreamUtil.inputStreamToString(stream, Charsets.US_ASCII));
    }

    @Test
    public void testStringToOutputStream() throws Exception {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        StreamUtil.stringToOutputStream("Test", stream, Charsets.US_ASCII);
        assertEquals("Test", stream.toString(Charsets.US_ASCII.name()));
    }

    @Test
    public void testInputStreamToOutputStream() throws Exception {
        ByteArrayInputStream inputStream = new ByteArrayInputStream("Test".getBytes(Charsets.US_ASCII));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        StreamUtil.inputStreamToOutputStream(inputStream, outputStream, null);
        assertEquals("Test", new String(outputStream.toByteArray(), Charsets.US_ASCII));
        inputStream = new ByteArrayInputStream("Test".getBytes(Charsets.US_ASCII));
        outputStream = new ByteArrayOutputStream();
        StreamUtil.inputStreamToOutputStream(inputStream, outputStream, () -> true);
        assertEquals("Test", new String(outputStream.toByteArray(), Charsets.US_ASCII));
        inputStream = new ByteArrayInputStream("Test".getBytes(Charsets.US_ASCII));
        outputStream = new ByteArrayOutputStream();
        StreamUtil.inputStreamToOutputStream(inputStream, outputStream, () -> false);
        assertEquals(0, outputStream.toByteArray().length);
        inputStream = new ByteArrayInputStream(getTestByteArray());
        outputStream = new ByteArrayOutputStream();
        StreamUtil.inputStreamToOutputStream(inputStream, outputStream, () -> true);
        assertArrayEquals(getTestByteArray(), outputStream.toByteArray());
        inputStream = new ByteArrayInputStream(getTestByteArray());
        outputStream = new ByteArrayOutputStream();
        TestInterrupt testInterrupt = new TestInterrupt(1);
        StreamUtil.inputStreamToOutputStream(inputStream, outputStream, testInterrupt);
        assertArrayEquals(getTestByteArrayOfSize(4096), outputStream.toByteArray());
        inputStream = new ByteArrayInputStream(getTestByteArray());
        outputStream = new ByteArrayOutputStream();
        testInterrupt = new TestInterrupt(2);
        StreamUtil.inputStreamToOutputStream(inputStream, outputStream, testInterrupt);
        assertArrayEquals(getTestByteArrayOfSize(8192), outputStream.toByteArray());
    }

    private byte[] getTestByteArray() {
        byte[] bytes = new byte[10000];
        Arrays.fill(bytes, (byte) 5);
        return bytes;
    }

    private byte[] getTestByteArrayOfSize(int size) {
        byte[] bytes = new byte[size];
        Arrays.fill(bytes, (byte) 5);
        return bytes;
    }

    private static class TestInterrupt implements StreamUtil.Interrupt {

        private int countdown;

        public TestInterrupt(int countdown) {
            this.countdown = countdown;
        }

        @Override
        public boolean shouldContinue() {
            if (countdown > 0) {
                countdown--;
                return true;
            }
            return false;
        }
    }
}
