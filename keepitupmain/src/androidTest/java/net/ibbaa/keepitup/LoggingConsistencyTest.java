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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RunWith(AndroidJUnit4.class)
public class LoggingConsistencyTest {

    private static final Pattern LOG_PATTERN = Pattern.compile("Log\\.([diwe])\\s*\\(\\s*([A-Za-z0-9_$.]+)\\s*\\.\\s*class\\s*\\.\\s*getName\\s*\\(\\s*\\)\\s*,\\s*\"([^\"]*)\"");

    private static final Pattern METHOD_PATTERN = Pattern.compile("^\\s*(?:(?:public|private|protected|static|final|synchronized|abstract|native|strictfp)\\s+)*(?:[A-Za-z0-9_$<>\\[\\],?\\s]+?)\\s+([A-Za-z0-9_$]+)\\s*\\(");

    private static final Set<String> CONTROL_FLOW = new HashSet<>(Arrays.asList("if", "else", "for", "while", "do", "switch", "try", "catch", "finally", "synchronized"));

    private static final Set<String> KEYWORDS = new HashSet<>(Arrays.asList("if", "for", "while", "switch", "catch", "else", "return", "new", "throw", "class", "interface", "enum", "void", "int", "long", "boolean", "double", "float", "byte", "char", "short", "static", "final"));

    @Test
    public void testLoggingConsistency() throws Exception {
        Context context = InstrumentationRegistry.getInstrumentation().getContext();
        List<String> javaFiles = new ArrayList<>();
        collectJavaAssets(context, "", javaFiles);
        List<String> violations = new ArrayList<>();
        for (String assetPath : javaFiles) {
            checkFile(context, assetPath, violations);
        }
        if (!violations.isEmpty()) {
            StringBuilder stringBuilder = new StringBuilder("Logging violations found:\n");
            for (String v : violations) {
                stringBuilder.append(v).append("\n");
            }
            fail(stringBuilder.toString());
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
        String expectedClass = filename.substring(0, filename.length() - 5);
        List<String> lines = readAllLines(context, assetPath);
        for (int ii = 0; ii < lines.size(); ii++) {
            String line = lines.get(ii);
            Matcher logMatcher = LOG_PATTERN.matcher(line);
            if (!logMatcher.find()) {
                continue;
            }
            String usedClass = logMatcher.group(2);
            String logMessage = logMatcher.group(3);
            if (usedClass != null) {
                String simple = extractSimpleClassName(usedClass);
                if (!simple.equals(expectedClass)) {
                    violations.add(assetPath + ":" + (ii + 1) + " — class mismatch: uses '" + usedClass + "' instead of '" + expectedClass + "'");
                }
            }
            if (logMessage != null && isDirectlyFirstInMethod(lines, ii)) {
                String messageStart = logMessage.split("[\\s,]")[0];
                String owningMethod = findOwningMethodName(lines, ii);
                if (owningMethod != null && !messageStart.equals(owningMethod)) {
                    violations.add(assetPath + ":" + (ii + 1) + " — method name mismatch: log message starts with '" + messageStart + "' but method is '" + owningMethod + "'");
                }
            }
        }
    }

    private boolean isDirectlyFirstInMethod(List<String> lines, int logLineIndex) {
        int braceDepth = 0;
        for (int ii = logLineIndex - 1; ii >= 0; ii--) {
            String lineContent = lines.get(ii);
            for (int cc = lineContent.length() - 1; cc >= 0; cc--) {
                char character = lineContent.charAt(cc);
                if (character == '}') {
                    braceDepth++;
                } else if (character == '{') {
                    if (braceDepth > 0) {
                        braceDepth--;
                    } else {
                        if (isControlFlowBrace(lines, ii)) {
                            return false;
                        }
                        return !hasStatementAtMethodLevel(lines, ii, logLineIndex);
                    }
                }
            }
        }
        return false;
    }

    private boolean isControlFlowBrace(List<String> lines, int braceLineIndex) {
        StringBuilder sb = new StringBuilder();
        for (int i = Math.max(0, braceLineIndex - 2); i <= braceLineIndex; i++) {
            sb.append(lines.get(i)).append(" ");
        }
        String ctx = sb.toString();
        if (ctx.matches("(?s).*\\belse\\s*\\{.*")) {
            return true;
        }
        Matcher matcher = Pattern.compile("\\b([A-Za-z0-9_$]+)\\s*\\(").matcher(ctx);
        while (matcher.find()) {
            if (CONTROL_FLOW.contains(matcher.group(1))) {
                return true;
            }
        }
        return false;
    }

    private boolean hasStatementAtMethodLevel(List<String> lines, int fromLine, int toLine) {
        int depth = 0;
        for (int ii = fromLine; ii < toLine; ii++) {
            String lineContent = lines.get(ii);
            String trimmed = lineContent.trim();
            if (ii > fromLine && depth == 1) {
                if (!trimmed.isEmpty() && !trimmed.startsWith("//") && !trimmed.startsWith("/*") && !trimmed.startsWith("*") && !trimmed.equals("{") && !trimmed.equals("}")) {
                    return true;
                }
            }

            for (int cc = 0; cc < lineContent.length(); cc++) {
                char charAt = lineContent.charAt(cc);
                if (charAt == '{') depth++;
                else if (charAt == '}') depth--;
            }
        }
        return false;
    }

    private String findOwningMethodName(List<String> lines, int logLineIndex) {
        int braceDepth = 0;
        for (int ii = logLineIndex - 1; ii >= 0; ii--) {
            String lineContent = lines.get(ii);
            for (int c = lineContent.length() - 1; c >= 0; c--) {
                char ch = lineContent.charAt(c);
                if (ch == '}') {
                    braceDepth++;
                } else if (ch == '{') {
                    if (braceDepth > 0) {
                        braceDepth--;
                    } else {
                        for (int di = ii; di >= Math.max(0, ii - 2); di--) {
                            Matcher matcher = METHOD_PATTERN.matcher(lines.get(di));
                            if (matcher.find()) {
                                String candidate = matcher.group(1);
                                if (!KEYWORDS.contains(candidate) && !CONTROL_FLOW.contains(candidate)) {
                                    return candidate;
                                }
                            }
                        }
                        return null;
                    }
                }
            }
        }
        return null;
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
