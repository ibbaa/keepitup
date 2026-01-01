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

package net.ibbaa.keepitup;

import static org.junit.Assert.fail;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RunWith(AndroidJUnit4.class)
public class LoggingConsistencyTest {

    private static final Pattern LOG_PATTERN = Pattern.compile("Log\\.([diwe])\\s*\\(\\s*([A-Za-z0-9_$.]+)\\s*\\.\\s*class\\s*\\.\\s*getName\\s*\\(\\s*\\)");

    @Test
    public void testLoggingClassNamesMatchFileNames() throws Exception {
        Context context = InstrumentationRegistry.getInstrumentation().getContext();
        List<String> javaFiles = new ArrayList<>();
        collectJavaAssets(context, "", javaFiles);
        List<String> violations = new ArrayList<>();
        for (String assetPath : javaFiles) {
            checkFile(context, assetPath, violations);
        }
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder("Log classname mismatches found:\n");
            for (String violation : violations) {
                sb.append(violation).append("\n");
            }
            fail(sb.toString());
        }
    }

    private void collectJavaAssets(Context context, String path, List<String> out) throws IOException {
        String[] list = context.getAssets().list(path);
        if (list == null) {
            return;
        }
        for (String entry : list) {
            String full = path.isEmpty() ? entry : path + "/" + entry;
            if (Objects.requireNonNull(context.getAssets().list(full)).length > 0) {
                collectJavaAssets(context, full, out);
            }
            if (full.endsWith(".java")) {
                out.add(full);
            }
        }
    }

    private void checkFile(Context context, String assetPath, List<String> violations) throws IOException {
        String filename = assetPath.substring(assetPath.lastIndexOf('/') + 1);
        String expected = filename.substring(0, filename.length() - 5);
        try (InputStream is = context.getAssets().open(assetPath); BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String line;
            int lineNumber = 1;
            while ((line = br.readLine()) != null) {
                Matcher m = LOG_PATTERN.matcher(line);
                if (m.find()) {
                    String used = m.group(2);
                    if (used != null) {
                        String simple = extractSimpleClassName(used);
                        if (!simple.equals(expected)) {
                            violations.add(assetPath + ":" + lineNumber + " uses " + used + " instead " + expected);
                        }
                    }
                }
                lineNumber++;
            }
        }
    }

    private String extractSimpleClassName(String used) {
        if (used.contains(".")) {
            used = used.substring(used.lastIndexOf('.') + 1);
        }
        if (used.contains("$")) {
            used = used.substring(0, used.indexOf('$'));
        }
        return used;
    }
}
