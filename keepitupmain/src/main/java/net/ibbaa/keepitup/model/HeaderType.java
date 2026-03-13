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

package net.ibbaa.keepitup.model;

public enum HeaderType {

    GENERIC(1, false, false),
    BASICAUTH(2, true, true);

    private final int code;
    private final boolean fixed;
    private final boolean secret;

    HeaderType(int code, boolean fixed, boolean secret) {
        this.code = code;
        this.fixed = fixed;
        this.secret = secret;
    }

    public int getCode() {
        return code;
    }

    public boolean isFixed() {
        return fixed;
    }

    public boolean isSecret() {
        return secret;
    }

    public boolean isGeneric() {
        return GENERIC.equals(this);
    }

    public boolean isBasicAuth() {
        return BASICAUTH.equals(this);
    }

    public static HeaderType forCode(int code) {
        HeaderType[] values = HeaderType.values();
        for (HeaderType value : values) {
            if (code == value.getCode()) {
                return value;
            }
        }
        return null;
    }
}
