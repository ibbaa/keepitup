/*
 * Copyright (c) 2024. Alwin Ibba
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

import net.ibbaa.keepitup.logging.Log;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.nio.charset.Charset;

public class StreamUtil {

    private static final int BUFFER_SIZE_1024 = 1024;
    private static final int BUFFER_SIZE_4096 = 4096;
    private static final int PART_SIZE = 1024 * 1024 * 10;

    public static String inputStreamToString(InputStream stream, Charset charset) throws Exception {
        Log.d(StreamUtil.class.getName(), "inputStreamToString");
        byte[] buffer = new byte[BUFFER_SIZE_1024];
        StringBuilder stringBuilder = new StringBuilder();
        int read;
        while ((read = stream.read(buffer, 0, BUFFER_SIZE_1024)) >= 0) {
            String data = new String(buffer, 0, read, charset);
            stringBuilder.append(data);
        }
        return stringBuilder.toString();
    }

    public static void stringToOutputStream(String data, OutputStream stream, Charset charset) throws Exception {
        Log.d(StreamUtil.class.getName(), "stringToFile, data is " + data);
        char[] buffer = new char[BUFFER_SIZE_1024];
        StringReader reader = new StringReader(data);
        OutputStreamWriter writer = new OutputStreamWriter(stream, charset);
        int read;
        while ((read = reader.read(buffer, 0, BUFFER_SIZE_1024)) >= 0) {
            writer.write(buffer, 0, read);
        }
        writer.flush();
    }

    public static boolean inputStreamToOutputStream(InputStream inputStream, OutputStream outputStream, Interrupt interrupt) throws Exception {
        Log.d(StreamUtil.class.getName(), "inputStreamToOutputStream");
        int read = 0;
        if (interrupt == null) {
            interrupt = () -> true;
        }
        byte[] buffer = new byte[BUFFER_SIZE_4096];
        int partsProgress = 0;
        int bytesDownloaded = 0;
        Log.d(StreamUtil.class.getName(), "Starting...");
        while (interrupt.shouldContinue() && (read = inputStream.read(buffer, 0, BUFFER_SIZE_4096)) >= 0) {
            outputStream.write(buffer, 0, read);
            bytesDownloaded += read;
            if (bytesDownloaded / PART_SIZE > partsProgress) {
                partsProgress++;
                Log.d(StreamUtil.class.getName(), "Progress: " + bytesDownloaded + " bytes.");
            }
        }
        Log.d(StreamUtil.class.getName(), "Bytes transmitted: " + bytesDownloaded);
        Log.d(StreamUtil.class.getName(), "Finished.");
        boolean success = read < 0;
        if (success) {
            Log.d(StreamUtil.class.getName(), "Transmission was successful.");
        } else if (!interrupt.shouldContinue()) {
            Log.d(StreamUtil.class.getName(), "Transmission was interrupted.");
        } else {
            Log.d(StreamUtil.class.getName(), "Transmission was not successful for an unknown reason.");
        }
        return success;
    }

    @FunctionalInterface
    public interface Interrupt {
        boolean shouldContinue();
    }
}
