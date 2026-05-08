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

public enum SNMPItemType {

    INTERFACE(1),
    NUMERIC(2);

    private final int code;

    SNMPItemType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public boolean isInterface() {
        return INTERFACE.equals(this);
    }

    public boolean isNumeric() {
        return NUMERIC.equals(this);
    }

    public static SNMPItemType forCode(int code) {
        SNMPItemType[] values = SNMPItemType.values();
        for (SNMPItemType value : values) {
            if (code == value.getCode()) {
                return value;
            }
        }
        return null;
    }
}
