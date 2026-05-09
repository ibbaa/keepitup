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

import net.ibbaa.keepitup.model.NetworkTask;

import java.util.regex.Pattern;

@SuppressWarnings("BooleanMethodIsAlwaysInverted")
public class SNMPUtil {

    private static final Pattern COMMUNITY_PATTERN = Pattern.compile("^[\\x21-\\x7E]*$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[^\\x00-\\x1F\\x7F]*$");
    private static final Pattern OID_PATTERN = Pattern.compile("^\\d+(\\.\\d+)+$");

    public static boolean validateCommunity(String community) {
        return COMMUNITY_PATTERN.matcher(community).matches();
    }

    public static boolean validateName(String name) {
        return NAME_PATTERN.matcher(name).matches();
    }

    public static boolean validateOID(String oid) {
        return OID_PATTERN.matcher(oid).matches();
    }

    public static boolean isSNMPTask(NetworkTask task) {
        return task != null && task.getAccessType() != null && task.getAccessType().isSNMP();
    }
}
