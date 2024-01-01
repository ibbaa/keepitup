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

package net.ibbaa.keepitup.service.network;

import androidx.annotation.NonNull;

public class PingCommandResult {

    private final int processReturnCode;
    private final String output;
    private final Throwable exception;

    public PingCommandResult(int processReturnCode, String output, Throwable exception) {
        this.processReturnCode = processReturnCode;
        this.output = output;
        this.exception = exception;
    }

    public int getProcessReturnCode() {
        return processReturnCode;
    }

    public String getOutput() {
        return output;
    }

    public Throwable getException() {
        return exception;
    }

    @NonNull
    @Override
    public String toString() {
        return "PingCommandResult{" +
                "processReturnCode=" + processReturnCode +
                ", output='" + output + '\'' +
                ", exception=" + exception +
                '}';
    }
}
