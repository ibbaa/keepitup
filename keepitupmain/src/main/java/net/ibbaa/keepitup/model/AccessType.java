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

package net.ibbaa.keepitup.model;

public enum AccessType {

    PING(1, false),
    CONNECT(2, true),
    DOWNLOAD(3, false);

    private final int code;
    private final boolean needsPort;

    AccessType(int code, boolean needsPort) {
        this.code = code;
        this.needsPort = needsPort;
    }

    public int getCode() {
        return code;
    }

    public boolean needsPort() {
        return needsPort;
    }

    public boolean isPing() {
        return PING.equals(this);
    }

    public boolean isConnect() {
        return CONNECT.equals(this);
    }

    public boolean isDownload() {
        return DOWNLOAD.equals(this);
    }

    public static AccessType forCode(int code) {
        AccessType[] values = AccessType.values();
        for (AccessType value : values) {
            if (code == value.getCode()) {
                return value;
            }
        }
        return null;
    }
}
