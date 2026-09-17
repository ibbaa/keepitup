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

public enum SNMPAuthAlgorithm {

    NONE(1),
    MD5(2),
    SHA1(3),
    SHA224(4),
    SHA256(5),
    SHA384(6),
    SHA512(7);

    private final int code;

    SNMPAuthAlgorithm(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public boolean isNone() {
        return NONE.equals(this);
    }

    public boolean isMD5() {
        return MD5.equals(this);
    }

    public boolean isSHA1() {
        return SHA1.equals(this);
    }

    public boolean isSHA224() {
        return SHA224.equals(this);
    }

    public boolean isSHA256() {
        return SHA256.equals(this);
    }

    public boolean isSHA384() {
        return SHA384.equals(this);
    }

    public boolean isSHA512() {
        return SHA512.equals(this);
    }

    public static SNMPAuthAlgorithm forCode(int code) {
        SNMPAuthAlgorithm[] values = SNMPAuthAlgorithm.values();
        for (SNMPAuthAlgorithm value : values) {
            if (code == value.getCode()) {
                return value;
            }
        }
        return null;
    }
}
