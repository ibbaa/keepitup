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

public enum SNMPPrivAlgorithm {

    NONE(1),
    DES(2),
    AES128(3),
    AES192(4),
    AES256(5),
    AES256C(6);

    private final int code;

    SNMPPrivAlgorithm(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public boolean isNone() {
        return NONE.equals(this);
    }

    public boolean isDES() {
        return DES.equals(this);
    }

    public boolean isAES128() {
        return AES128.equals(this);
    }

    public boolean isAES192() {
        return AES192.equals(this);
    }

    public boolean isAES256() {
        return AES256.equals(this);
    }

    public boolean isAES256C() {
        return AES256C.equals(this);
    }

    public static SNMPPrivAlgorithm forCode(int code) {
        SNMPPrivAlgorithm[] values = SNMPPrivAlgorithm.values();
        for (SNMPPrivAlgorithm value : values) {
            if (code == value.getCode()) {
                return value;
            }
        }
        return null;
    }
}
