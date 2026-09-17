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
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RunWith(AndroidJUnit4.class)
public class TestSuiteCompletenessTest {

    private static final String TEST_SOURCE_ROOT = "androidTestSrc";

    private static final String[] SUITE_ASSET_PATHS = {
            "androidTestSrc/net/ibbaa/keepitup/OtherTestSuite.java",
            "androidTestSrc/net/ibbaa/keepitup/ActivityDialogTestSuite.java"
    };

    private static final Pattern CLASS_REFERENCE_PATTERN = Pattern.compile("\\b([A-Za-z0-9_$]+)\\.class\\b");

    private static final Pattern TEST_ANNOTATION_PATTERN = Pattern.compile("@Test\\b");

    @Test
    public void testAllTestClassesRegisteredInSuites() throws Exception {
        Context context = InstrumentationRegistry.getInstrumentation().getContext();
        Set<String> registeredClasses = new HashSet<>();
        for (String suiteAssetPath : SUITE_ASSET_PATHS) {
            registeredClasses.addAll(extractClassReferences(context, suiteAssetPath));
        }
        List<String> testSourceFiles = new ArrayList<>();
        collectJavaAssets(context, TEST_SOURCE_ROOT, testSourceFiles);
        List<String> violations = new ArrayList<>();
        for (String assetPath : testSourceFiles) {
            String filename = assetPath.substring(assetPath.lastIndexOf('/') + 1);
            if (!filename.endsWith("Test.java")) {
                continue;
            }
            String className = filename.substring(0, filename.length() - ".java".length());
            List<String> lines = readAllLines(context, assetPath);
            if (!containsTestAnnotation(lines)) {
                continue;
            }
            if (!registeredClasses.contains(className)) {
                violations.add(assetPath + " — class '" + className + "' is not registered in OtherTestSuite or ActivityDialogTestSuite");
            }
        }
        if (!violations.isEmpty()) {
            StringBuilder stringBuilder = new StringBuilder("Test suite completeness violations found:\n");
            for (String violation : violations) {
                stringBuilder.append(violation).append("\n");
            }
            fail(stringBuilder.toString());
        }
    }

    private Set<String> extractClassReferences(Context context, String assetPath) throws IOException {
        Set<String> result = new HashSet<>();
        for (String line : readAllLines(context, assetPath)) {
            Matcher matcher = CLASS_REFERENCE_PATTERN.matcher(line);
            while (matcher.find()) {
                result.add(matcher.group(1));
            }
        }
        return result;
    }

    private boolean containsTestAnnotation(List<String> lines) {
        for (String line : lines) {
            if (TEST_ANNOTATION_PATTERN.matcher(line).find()) {
                return true;
            }
        }
        return false;
    }

    private void collectJavaAssets(Context context, String path, List<String> out) throws IOException {
        String[] list = context.getAssets().list(path);
        if (list == null) {
            return;
        }
        for (String entry : list) {
            String full = path + "/" + entry;
            if (Objects.requireNonNull(context.getAssets().list(full)).length > 0) {
                collectJavaAssets(context, full, out);
            }
            if (full.endsWith(".java")) {
                out.add(full);
            }
        }
    }

    private List<String> readAllLines(Context context, String assetPath) throws IOException {
        List<String> result = new ArrayList<>();
        try (InputStream is = context.getAssets().open(assetPath);
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = br.readLine()) != null) {
                result.add(line);
            }
        }
        return result;
    }
}
