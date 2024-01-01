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

public class ConnectCommandResult {

    private final boolean success;
    private final int attempts;
    private final int successfulAttempts;
    private final int timeoutAttempts;
    private final int errorAttempts;
    private final double averageTime;
    private final Throwable exception;

    public ConnectCommandResult(boolean success, int attempts, int successfulAttempts, int timeoutAttempts, int errorAttempts, double averageTime, Throwable exception) {
        this.success = success;
        this.attempts = attempts;
        this.successfulAttempts = successfulAttempts;
        this.timeoutAttempts = timeoutAttempts;
        this.errorAttempts = errorAttempts;
        this.averageTime = averageTime;
        this.exception = exception;
    }

    public boolean isSuccess() {
        return success;
    }

    public int getAttempts() {
        return attempts;
    }

    public int getSuccessfulAttempts() {
        return successfulAttempts;
    }

    public int getTimeoutAttempts() {
        return timeoutAttempts;
    }

    public int getErrorAttempts() {
        return errorAttempts;
    }

    public double getAverageTime() {
        return averageTime;
    }

    public Throwable getException() {
        return exception;
    }

    @NonNull
    @Override
    public String toString() {
        return "ConnectCommandResult{" +
                "success=" + success +
                ", attempts=" + attempts +
                ", successfulAttempts=" + successfulAttempts +
                ", timeoutAttempts=" + timeoutAttempts +
                ", errorAttempts=" + errorAttempts +
                ", averageTime=" + averageTime +
                ", exception=" + exception +
                '}';
    }
}
